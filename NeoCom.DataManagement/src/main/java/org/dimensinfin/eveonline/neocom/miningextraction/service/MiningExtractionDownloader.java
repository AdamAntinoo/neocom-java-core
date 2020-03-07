package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.GetCharactersCharacterIdMiningToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class MiningExtractionDownloader /*extends Job*/ {
	private Credential credential;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	private MiningRepository miningRepository;

	private MiningExtractionDownloader() {}

	//	// - J O B
//	@Override
//	public int getUniqueIdentifier() {
//		return 0;
//	}
//
//	@Override
//	public String getName() {
//		return this.getClass().getSimpleName();
//	}
//
//	@Override
//	public Boolean call() throws Exception {
//		return null;
//	}
	@TimeElapsed
	@LogEnterExit
	public List<MiningExtraction> downloadMiningExtractions() {
		// Get to the Network and download the data from the ESI api.
		NeoComLogger.info( "Starting download of credential {} mining extractions...", this.credential.getAccountId() + "" );
		final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = Objects.requireNonNull(
				this.esiDataProvider.getCharactersCharacterIdMining( credential ) );
		for (GetCharactersCharacterIdMining200Ok extractionOk : miningActionsOk) {
			final MiningExtraction extraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
					this.locationCatalogService,
					this.credential.getAccountId(),
					LocalDate.now() )
					.convert( extractionOk );
			// Set the missing owner that is something not available at the esi record.
//			extraction.setOwnerId( this.credential.getAccountId() );
			// Before doing any store of the data, see if this is a delta. Search for an already existing record.
			final String recordId = extraction.getId();
			NeoComLogger.info( "Generating record identifier: {}.", recordId );
			final MiningExtractionEntity targetRecord = this.miningRepository.accessMiningExtractionFindById( recordId );
			NeoComLogger.info( "Searching for record on database: {}.", targetRecord.getId() );


		}

//		List<MiningExtraction> oreExtractions = new ArrayList<>();
		return null;
	}

	/**
	 * Mining actions are small records that register the ore collected by a Pilot or the Moon mining done by a Corporation. The time
	 * base is 10 minutes and I suppose that those records are aggregated during a day. The data is a list of entries, each one
	 * declaring the quantity of one ore mined on a date and related to a single star system.
	 *
	 * The processing algorithm should add the capturing hour of day to the search index and update with the current value or create a
	 * new record when the index matches. The <b>id</b> of the record is changed to an string having the next fields:
	 * YEAR/MONTH/DAY:HOUR-TYPEID-SYSTEMID-OWNERID.
	 * Once one record is found instead storing on the database a single record per day/item/system/owner we store this same data for
	 * each hour until the date changes and then we stop processing entries until we have new current mining extractions.
	 */
	public List<MiningExtraction> persistMiningActionsESI() throws SQLException {
//		DownloadManager.logger.info(">> [DownloadManager.persistMiningActionsESI]> Credential: []", credential.getAccountName());
//		List<MiningExtraction> oreExtractions = new ArrayList<>();
//		final Dao<MiningExtraction, String> miningDao = GlobalDataManager.getSingleton().getNeocomDBHelper().getMiningExtractionDao();
//		// Get to the Network and download the data from the ESI api.
//		logger.info("-- [MARKETORDERS]> Starting download of data");
//		final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = this.esiAdapter.getCharactersCharacterIdMining(credential.getAccountId()
//				, credential.getRefreshToken()
//				, credential.getDataSource());
//		if (null != miningActionsOk) {
//			logger.info("-- [MARKETORDERS]> Downloaded {} extractions.", miningActionsOk.size());
//			// Process the data and convert it to structures compatible with MVC.
//			for (GetCharactersCharacterIdMining200Ok extractionOk : miningActionsOk) {
//				// Before doing any store of the data, see if this is a delta. Search for an already existing record.
//				final String recordId = MiningExtraction.generateRecordId(extractionOk.getDate(), extractionOk.getTypeId()
//						, extractionOk.getSolarSystemId(), credential.getAccountId());
//				logger.info("-- [MARKETORDERS]> Generating record identifier: {}.", recordId);
//				MiningExtraction recordFound = null;
//				try {
//					recordFound = GlobalDataManager.getSingleton().getNeocomDBHelper().getMiningExtractionDao().queryForId(recordId);
//					logger.info("-- [MARKETORDERS]> Searching for record on database: {}.", recordFound);
//				} catch (NeoComRuntimeException nrex) {
//					logger.info("EX [DownloadManager.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
//							, nrex.getMessage());
//				}
//				// If we found and exact record then we can update the value that can have changed or not.
//				if (null != recordFound) {
//					final long currentQty = recordFound.getQuantity();
//					recordFound.setQuantity(extractionOk.getQuantity())
//							.store();
//					logger.info("-- [MARKETORDERS]> Updating record on database: {} > Quantity: {}/{}", recordId, extractionOk.getQuantity(), currentQty);
//					logger.info("-- [DownloadManager.persistMiningActionsESI]> Updating mining extraction: {} > Quantity: {}/{}"
//							, recordId, extractionOk.getQuantity(), currentQty);
//				} else {
//					final MiningExtraction newExtraction = new MiningExtraction()
//							                                       .setTypeId(extractionOk.getTypeId())
//							                                       .setSolarSystemId(extractionOk.getSolarSystemId())
//							                                       .setExtractionDate(extractionOk.getDate())
//							                                       .setQuantity(extractionOk.getQuantity())
//							                                       .setOwnerId(credential.getAccountId())
//							                                       .create(recordId);
//					logger.info("-- [MARKETORDERS]> Creating new record on database: {} > Quantity: {}", recordId, extractionOk.getQuantity());
//					logger.info("-- [DownloadManager.persistMiningActionsESI]> Creating new mining extraction: {} > Quantity: {}"
//							, recordId, extractionOk.getQuantity());
//				}
//			}
//		}
//		DownloadManager.logger.info("<< [DownloadManager.persistMiningActionsESI]");
//		return oreExtractions;
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionDownloader onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionDownloader();
		}

		public MiningExtractionDownloader build() {
			return this.onConstruction;
		}

		public MiningExtractionDownloader.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public MiningExtractionDownloader.Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public MiningExtractionDownloader.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public MiningExtractionDownloader.Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull( miningRepository );
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}
	}
}