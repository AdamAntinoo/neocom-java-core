package org.dimensinfin.eveonline.neocom.asset.processor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetDownloadProcessor extends Job {
	// -  C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private GetCharactersCharacterIdAsset2NeoAssetConverter neoAssetConverter;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	// - I N T E R N A L   W O R K   F I E L D S
	private final Map<Long, GetCharactersCharacterIdAssets200Ok> assetMap = new HashMap<>();
	private final List<Long> id4Names = new ArrayList<>();

	private AssetDownloadProcessor() {super();}

	/**
	 * Download the list of assets that belong to a character or corporation and process their location references while
	 * converting to the application data version.
	 *
	 * @return true if the process completes successfully.
	 */
//	@Override
	public Boolean call() throws Exception {
		return this.processCharacterAssets();
	}

	private Boolean processCharacterAssets() throws SQLException {
		this.downloadPilotAssetsESI();
		return true;
	}

	/**
	 * This downloader will use the new ESI api to get access to the full list of assets for this character.
	 * Once the list is processed we should create an instance as close as possible to the older XML instances generated by the
	 * XML
	 * processing.
	 * That instance will then get stored at the database and then we should make the trick of asset replacing.
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the final list on the
	 * database
	 * will have the correct parentship hierarchy set up.
	 *
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
	 */
	public boolean downloadPilotAssetsESI() throws SQLException {
		NeoComLogger.enter( ">> [AssetsManager.downloadPilotAssetsESI]" );
		final List<GetCharactersCharacterIdAssets200Ok> assetOkList = this.esiDataProvider.getCharactersCharacterIdAssets(
				credential);
		if ((null == assetOkList) || (assetOkList.size() < 1)) return false;
		this.createAssetMap( assetOkList ); // Map of asset for easy lookup.
		this.assetRepository.clearInvalidRecords( this.credential.getAccountId() );
		for (final GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
			// - A S S E T   P R O C E S S I N G
			try {
				// Convert the asset from the OK format to a MVC compatible structure.
				final NeoAsset targetAsset = this.neoAssetConverter.convert( assetOk );
				// TODO - Complete the code to read the assets userLabel after all assets are processed and persisted.
//				if (targetAsset.isShip()) downloadAssetEveName( targetAsset.getAssetId() );
//				if (targetAsset.isContainer()) downloadAssetEveName( targetAsset.getAssetId() );
				// Mark the asset owner to the work in progress value.
				targetAsset.setOwnerId( this.credential.getAccountId() * -1 );

				// - L O C A T I O N   P R O C E S S I N G
				this.locationProcessing( targetAsset );
				// With assets separate the update from the creation because they use a generated unique key.
				this.assetRepository.persist( targetAsset );
			} catch (final SQLException sqle) {
				NeoComLogger.info( "RTEX ´[AssetsManager.downloadPilotAssetsESI]> Processing asset: {} - {}"
						, assetOk.getItemId().toString(), sqle.getMessage() );
				sqle.printStackTrace();
			} catch (final IOException ioe) {
				NeoComLogger.info( "RTEX ´[AssetsManager.downloadPilotAssetsESI]> Processing asset: {} - {}"
						, assetOk.getItemId().toString(), ioe.getMessage() );
				ioe.printStackTrace();
			} catch (final RuntimeException rtex) {
				NeoComLogger.info( "RTEX ´[AssetsManager.downloadPilotAssetsESI]> Processing asset: {} - {}"
						, assetOk.getItemId().toString(), rtex.getMessage() );
				rtex.printStackTrace();
			}
		}
		//--- O R P H A N   L O C A T I O N   A S S E T S
		// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
//		for (NeoComAsset asset : this.unlocatedAssets) {
//			this.validateLocation( asset );
//		}
		// Assign the assets to the pilot.
		this.assetRepository.replaceAssets( this.credential.getAccountId() );
		// Remove from memory the managers that contain now stale data.
		//TODO Removed until this is checked if required.
		//			GlobalDataManager.dropAssetsManager(credential.getAccountId());
		//		} catch (final Exception ex) {
		//			ex.printStackTrace();
		//			return false;
		//		}
		NeoComLogger.info( "<< [AssetsManager.downloadPilotAssetsESI]" );
		return true;
	}

	private void createAssetMap( final List<GetCharactersCharacterIdAssets200Ok> assetList ) {
		for (final GetCharactersCharacterIdAssets200Ok assetOk : assetList)
			this.assetMap.put( assetOk.getItemId(), assetOk );
	}

	private void locationProcessing( final NeoAsset targetAsset ) {
		// If the preliminary calculation returns UNKNOWN then search if the location is reachable.
		if (targetAsset.getLocationId().getType() == LocationIdentifierType.UNKNOWN) {
			final LocationIdentifier workLocationId = targetAsset.getLocationId();
			// Check if location is a user asset.
			if (this.assetMap.containsKey( workLocationId.getSpaceIdentifier() ))
				workLocationId.setType( LocationIdentifierType.CONTAINER );
			else {
				// Check if the location is a public reachable structure.
				final Optional<SpaceLocation> structure = this.locationCatalogService
						.searchStructure4Id( targetAsset.getLocationId().getSpaceIdentifier(),
								this.credential );
				if (structure.isPresent()) {
					workLocationId.setType( LocationIdentifierType.STRUCTURE );
					workLocationId.setStructureIdentifier( workLocationId.getSpaceIdentifier() );
				}
			}
		}
	}

	// - B U I L D E R
	public static class Builder extends Job.Builder<AssetDownloadProcessor, AssetDownloadProcessor.Builder>{
		private AssetDownloadProcessor onConstruction;

		@Override
		protected AssetDownloadProcessor getActual() {
			if (null == this.onConstruction) this.onConstruction = new AssetDownloadProcessor();
			return this.onConstruction;
		}

		@Override
		protected AssetDownloadProcessor.Builder getActualBuilder() {
			return this;
		}

		public AssetDownloadProcessor.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.getActual().credential = credential;
			return this;
		}
		public AssetDownloadProcessor.Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}
		public AssetDownloadProcessor.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public AssetDownloadProcessor.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.getActual().assetRepository = assetRepository;
			return this;
		}

		public AssetDownloadProcessor.Builder withNeoAssetConverter( final GetCharactersCharacterIdAsset2NeoAssetConverter neoAssetConverter ) {
			Objects.requireNonNull( neoAssetConverter );
			this.getActual().neoAssetConverter = neoAssetConverter;
			return this;
		}

		public AssetDownloadProcessor build() {
			final AssetDownloadProcessor instance = super.build();
			Objects.requireNonNull( instance.credential );
			Objects.requireNonNull( instance.esiDataProvider );
			Objects.requireNonNull( instance.locationCatalogService );
			Objects.requireNonNull( instance.assetRepository );
			Objects.requireNonNull( instance.neoAssetConverter );
			return instance;
		}
	}
}