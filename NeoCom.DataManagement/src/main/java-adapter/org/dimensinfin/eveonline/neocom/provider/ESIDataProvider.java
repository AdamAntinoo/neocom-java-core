package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.FittingsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.StatusApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.WalletApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdDivisionsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSchematicsSchematicIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCorporationsCorporationIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.updater.NeoComUpdater;

import retrofit2.Response;

/**
 * This class will be the base to access most of the non authenticated SDE data available though the ESI data service.
 *
 * The new data service allows to access many endpoints with data that do not require pilot authentication. With this endpoints I
 * will try to remove
 * the required SDE database and remove the need to add that heavy resource to the application download when implemented in
 * Android.
 *
 * This class will also use other components to be able to store downloaded SDE data into caches, be them temporal in memory or
 * persisted on disk.
 */
@NeoComAdapter
public class ESIDataProvider extends ESIUniverseDataProvider {
	public static final String DEFAULT_ESI_SERVER = "Tranquility".toLowerCase();
	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";
	private static final String CREDENTIAL_LOG_LITERAL="Credential: {}";
	// - C A C H E S
	private static final Map<Integer, GetUniverseRaces200Ok> racesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseAncestries200Ok> ancestriesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new HashMap<>();
	// - C O M P O N E N T S
	protected LocationCatalogService locationCatalogService;

	// - C O N S T R U C T O R S
	protected ESIDataProvider() {}

	@TimeElapsed
	public GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier ) {
		NeoComLogger.enter( "Pilot Identifier:", Integer.toString( identifier ) );
		try {
			final Response<GetCharactersCharacterIdOk> characterResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( CharacterApi.class )
					.getCharactersCharacterId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (characterResponse.isSuccessful()) return characterResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdAssets200Ok>> assetsApiResponse = this.retrofitFactory
						.accessAuthenticatedConnector( credential )
						.create( AssetsApi.class )
						.getCharactersCharacterIdAssets( credential.getAccountId(),
								credential.getDataSource().toLowerCase(),
								null, pageCounter, null )
						.execute();
				if (assetsApiResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnAssetList.addAll( Objects.requireNonNull( assetsApiResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( assetsApiResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return returnAssetList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		List<GetCharactersCharacterIdBlueprints200Ok> returnBlueprintList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdBlueprints200Ok>> blueprintResponse = this.retrofitFactory
						.accessAuthenticatedConnector( credential )
						.create( CharacterApi.class )
						.getCharactersCharacterIdBlueprints(
								credential.getAccountId(),
								credential.getDataSource().toLowerCase(),
								null,
								pageCounter,
								null )
						.execute();
				if (blueprintResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnBlueprintList.addAll( Objects.requireNonNull( blueprintResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( blueprintResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return returnBlueprintList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		try {
			final Response<List<GetCharactersCharacterIdFittings200Ok>> fittingsResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( FittingsApi.class )
					.getCharactersCharacterIdFittings( credential.getAccountId(),
							credential.getDataSource(), null, null )
					.execute();
			if (fittingsResponse.isSuccessful()) return fittingsResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return new ArrayList<>(  );
	}

	/**
	 * This method encapsulates the call to the esi server to retrieve the current list of mining operations. This listing will
	 * contain the operations
	 * for the last 30 days. It will be internally cached during 1800 seconds so we have to check the hour change less
	 * frequently.
	 *
	 * @param credential the credential to be used when composing the ESI call.
	 * @return the list of mining actions performed during the last 30 days.
	 */
	@TimeElapsed
	public List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		List<GetCharactersCharacterIdMining200Ok> returnMiningList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdMining200Ok>> industryApiResponse = this.retrofitFactory
						.accessAuthenticatedConnector( credential )
						.create( IndustryApi.class )
						.getCharactersCharacterIdMining(
								credential.getAccountId(),
								credential.getDataSource().toLowerCase(), null,
								pageCounter, null )
						.execute();
				if (industryApiResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnMiningList.addAll( Objects.requireNonNull( industryApiResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( industryApiResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return returnMiningList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdPlanets200Ok>> planetaryApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanets(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(), null, null )
					.execute();
			if (planetaryApiResponse.isSuccessful()) return planetaryApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	public GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId( final Integer planetId,
	                                                                                          final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		try {
			final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> planetaryApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanetsPlanetId(
							credential.getAccountId(),
							planetId,
							credential.getDataSource().toLowerCase(), null,
							null ).execute();
			if (planetaryApiResponse.isSuccessful()) return planetaryApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return null;
	}

	@TimeElapsed
	public Double getCharactersCharacterIdWallet( final Credential credential ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		try {
			final Response<Double> walletApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( WalletApi.class )
					.getCharactersCharacterIdWallet( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (walletApiResponse.isSuccessful()) return walletApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return -1.0;
	}

	@TimeElapsed
	public List<GetCorporationsCorporationIdAssets200Ok> getCorporationsCorporationIdAssets( final Credential credential,
	                                                                                         final Integer corporationId ) {
		NeoComLogger.enter( CREDENTIAL_LOG_LITERAL, credential.toString() );
		List<GetCorporationsCorporationIdAssets200Ok> returnAssetList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCorporationsCorporationIdAssets200Ok>> assetsApiResponse = this.retrofitFactory
						.accessAuthenticatedConnector( credential )
						.create( AssetsApi.class )
						.getCorporationsCorporationIdAssets( corporationId,
								credential.getDataSource().toLowerCase(),
								null, pageCounter, null )
						.execute();
				if (assetsApiResponse.isSuccessful()) {
					// Copy the assets to the result list.
					returnAssetList.addAll( Objects.requireNonNull( assetsApiResponse.body() ) );
					pageCounter++;
					// Check for out of page running.
					if (Objects.requireNonNull( assetsApiResponse.body() ).isEmpty()) morePages = false;
				}
			}
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return returnAssetList;
	}

	@TimeElapsed
	@LogEnterExit
	public GetCorporationsCorporationIdDivisionsOk getCorporationsCorporationIdDivisions( final Integer corporationId,
	                                                                                      final Credential credential ) {
		NeoComLogger.enter();
		try {
			final Response<GetCorporationsCorporationIdDivisionsOk> divisionsResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( CorporationApi.class )
					.getCorporationsCorporationIdDivisions(
							corporationId,
							credential.getDataSource().toLowerCase(),
							null, null )
					.execute();
			if (divisionsResponse.isSuccessful()) return divisionsResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetUniverseSchematicsSchematicIdOk getUniversePlanetarySchematicsById( final int schematicId ) {
		NeoComLogger.enter( "Schematic id: {}", Integer.toString( schematicId ) );
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSchematicsSchematicIdOk> schematicistResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( PlanetaryInteractionApi.class )
					.getUniverseSchematicsSchematicId(
							schematicId, DEFAULT_ESI_SERVER, null )
					.execute();
			if (schematicistResponse.isSuccessful()) return schematicistResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier ) {
		NeoComLogger.enter( "Planet identifier:", Integer.toString( identifier ) );
		try {
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniversePlanetsPlanetId(
							identifier,
							DEFAULT_ESI_SERVER
									.toLowerCase(),
							null ).execute();
			if (universeApiResponse.isSuccessful()) return universeApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return null;
	}

	@TimeElapsed
	public GetStatusOk getUniverseStatus( final String server ) {
		NeoComLogger.enter( "Server:", server );
		try {
			String datasource = DEFAULT_ESI_SERVER; // Set the server to the default or to the selected server.
			if (null != server) datasource = server;
			final Response<GetStatusOk> statusApiResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( StatusApi.class )
					.getStatus( datasource.toLowerCase(), null ).execute();
			if (statusApiResponse.isSuccessful()) return statusApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return null;
	}

	public List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames( final List<Long> listItemIds,
	                                                                                             final Credential credential ) {
		NeoComLogger.enter();
		try {
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( AssetsApi.class )
					.postCharactersCharacterIdAssetsNames(
							credential.getAccountId(),
							listItemIds,
							credential.getDataSource().toLowerCase(),
							null )
					.execute();
			if (assetsApiResponse.isSuccessful()) return assetsApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return new ArrayList<>();
	}

	public List<PostCorporationsCorporationIdAssetsNames200Ok> postCorporationsCorporationIdAssetsNames( final List<Long> listItemIds,
	                                                                                                     final Credential credential ) {
		NeoComLogger.enter();
		try {
			final Response<List<PostCorporationsCorporationIdAssetsNames200Ok>> assetsApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( AssetsApi.class )
					.postCorporationsCorporationIdAssetsNames(
							credential.getAccountId(),
							listItemIds,
							credential.getDataSource().toLowerCase(),
							null )
					.execute();
			if (assetsApiResponse.isSuccessful()) return assetsApiResponse.body();
		} catch (IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		} finally {
			NeoComLogger.exit();
		}
		return new ArrayList<>();
	}

	@Override
	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessItem( itemId ).blockingGet();
	}

	@Override
	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	@Override
	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}

	@RequiresNetwork
	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
		if (ancestriesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return ancestriesCache.get( identifier );
	}

	@RequiresNetwork
	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
		if (bloodLinesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return bloodLinesCache.get( identifier );
	}

	@RequiresNetwork
	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
		if (bloodLinesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return racesCache.get( identifier );
	}

	private synchronized void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getUniverseRaces( DEFAULT_ESI_SERVER );
		NeoComLogger.info( "Download race: {} items", racesList.size() + "" );
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put( race.getRaceId(), race );
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getUniverseAncestries( DEFAULT_ESI_SERVER );
		NeoComLogger.info( "Download ancestries: {} items", ancestriesList.size() + "" );
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put( ancestry.getId(), ancestry );
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getUniverseBloodlines( DEFAULT_ESI_SERVER );
		NeoComLogger.info( "-Download blood lines: {} items", bloodLineList.size() + "" );
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put( bloodLine.getBloodlineId(), bloodLine );
		}
	}

	@TimeElapsed
	private List<GetUniverseAncestries200Ok> getUniverseAncestries( final String datasource ) {
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseAncestries(
							DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, DEFAULT_ACCEPT_LANGUAGE )
					.execute();
			if (ancestriesList.isSuccessful()) return ancestriesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	private List<GetUniverseBloodlines200Ok> getUniverseBloodlines( final String datasource ) {
		try {
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = this.retrofitFactory
					.accessUniverseConnector()
					.create(
							UniverseApi.class )
					.getUniverseBloodlines( DEFAULT_ACCEPT_LANGUAGE, datasource,
							null, DEFAULT_ACCEPT_LANGUAGE )
					.execute();
			if (bloodLinesList.isSuccessful()) return bloodLinesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	private List<GetUniverseRaces200Ok> getUniverseRaces( final String datasource ) {
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseRaces( DEFAULT_ACCEPT_LANGUAGE, datasource,
							null, DEFAULT_ACCEPT_LANGUAGE )
					.execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return new ArrayList<>();
	}

	// - B U I L D E R
	public static class Builder {
		private ESIDataProvider onConstruction;

		public Builder() {
			this.onConstruction = new ESIDataProvider();
		}

		public ESIDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			// Inject the new adapter to the classes that depend on it.
			NeoComUpdater.injectsEsiDataAdapter( this.onConstruction );
			return this.onConstruction;
		}

		public ESIDataProvider.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public ESIDataProvider.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public ESIDataProvider.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public ESIDataProvider.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}

		public ESIDataProvider.Builder withStoreCacheManager( final StoreCacheManager storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}
	}
}
