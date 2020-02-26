package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
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
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSchematicsSchematicIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
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
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	//	private static final List<Long> id4Names = new ArrayList<>();
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
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
		}
		return returnAssetList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final Credential credential ) {
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
		}
		return returnBlueprintList;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings( final Credential credential ) {
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
		return null;
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
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
	public List<GetCorporationsCorporationIdAssets200Ok> getCorporationsCorporationIdAssets( final Credential credential
			, final Integer corporationId ) {
//		NeoComLogger.enter( "Credential:", NeoComLogger.toJSON( credential ) );
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
		}
		return returnAssetList;
	}

	// - P L A N E T A R Y   I N T E R A C T I O N   P U B L I C   I N F O R M A T I O N
	@TimeElapsed
	public GetUniverseSchematicsSchematicIdOk getUniversePlanetarySchematicsById( final int schematicId ) {
		logger.info( ">> [ESIDataProvider.getUniversePlanetarySchematicsById]" );
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSchematicsSchematicIdOk> schematicistResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( PlanetaryInteractionApi.class )
					.getUniverseSchematicsSchematicId(
							schematicId
							, DEFAULT_ESI_SERVER
							, null )
					.execute();
			if (!schematicistResponse.isSuccessful()) {
				return null;
			} else return schematicistResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info( "<< [ESIDataProvider.getUniverseMarketsPrices]> [TIMING] Full elapsed: {}"
					, new Duration( startTimePoint, DateTime.now() ).getMillis() + "ms" );
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

	// - U N I V E R S E
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
		logger.info( ">> [ESINetworkManager.postCharactersCharacterIdAssetsNames]" );
		//		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
//			String datasource = DEFAULT_ESI_SERVER;
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( AssetsApi.class )
					.postCharactersCharacterIdAssetsNames(
							credential.getAccountId(),
							listItemIds,
							credential.getDataSource().toLowerCase(),
							null ).execute();
			if (!assetsApiResponse.isSuccessful()) {
				return null;
			} else return assetsApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
			//		} finally {
			//			logger.info("<< [ESINetworkManager.postCharactersCharacterIdAssetsNames]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessItem( itemId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		logger.info( "-- [ESIDataProvider.searchItemCategory4Id]> categoryId: {}", categoryId );
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		logger.info( "-- [ESIDataProvider.searchItemGroup4Id]> targetGroupId: {}", groupId );
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}

	//	public Future<MarketDataSet> searchMarketData( final int itemId, final EMarketSide side ) {
//		return Futures.immediateFuture( new MarketDataSet( itemId, side ) );
//	}
	@Deprecated
	public EsiLocation searchLocation4Id( final Long locationId ) {
		return null;
	}

	@Deprecated
	public EsiLocation searchLocation4Id( final Integer locationId ) {
		return null;
	}

	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
		return this.ancestriesCache.get( identifier );
	}

	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
		return this.bloodLinesCache.get( identifier );
	}

	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
		return racesCache.get( identifier );
	}

	// - M I N I N G

	// - S D E   D A T A
	public GetUniverseStructuresStructureIdOk searchStructureById( final Long structureId, final Credential credential ) {
		final String refreshToken = credential.getRefreshToken();
		final int identifier = credential.getAccountId();
		try {
//			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			final Response<GetUniverseStructuresStructureIdOk> universeResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( UniverseApi.class )
					.getUniverseStructuresStructureId( structureId, datasource, null,
							credential.getAccessToken() )
					.execute();
			if (universeResponse.isSuccessful()) {
				return universeResponse.body();
			} else return null;
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
			return null;
		} catch (RuntimeException rte) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", rte.getMessage() );
			rte.printStackTrace();
			return null;
		}
//		return null;
	}

	private synchronized void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getUniverseRaces( DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download race: {} items", racesList.size() );
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put( race.getRaceId(), race );
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getUniverseAncestries( DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download ancestries: {} items", ancestriesList.size() );
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put( ancestry.getId(), ancestry );
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getUniverseBloodlines( DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download blood lines: {} items", bloodLineList.size() );
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put( bloodLine.getBloodlineId(), bloodLine );
		}
	}

	private List<GetUniverseAncestries200Ok> getUniverseAncestries( final String datasource ) {
		logger.info( ">> [ESIDataProvider.getUniverseAncestries]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseAncestries(
							DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, "en-us" )
					.execute();
			if (ancestriesList.isSuccessful()) return ancestriesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			logger.info("<< [ESIDataProvider.getUniverseAncestries]> [TIMING] Full elapsed: {}",
//			            accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private List<GetUniverseBloodlines200Ok> getUniverseBloodlines( final String datasource ) {
		logger.info( ">> [ESIDataProvider.getUniverseBloodlines]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = this.retrofitFactory
					.accessUniverseConnector()
					.create(
							UniverseApi.class )
					.getUniverseBloodlines(
							DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, "en-us" )
					.execute();
			if (bloodLinesList.isSuccessful()) return bloodLinesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			logger.info("<< [ESIDataProvider.getUniverseBloodlines]> [TIMING] Full elapsed: {}",
//			            accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

//	/**
//	 * Search for the item on the current downloaded items cache. If not found then go for it to the network.
//	 */
//	public GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId ) {
//		final GetUniverseTypesTypeIdOk item = this.getUniverseTypeById( typeId, "tranquility" );
//		//		return getUniverseTypeById("tranquility", typeId);
//		return item;
//	}
//
//	@Deprecated
//	private GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId, final String server ) {
//		//		logger.info(">> [ESINetworkManagerMock.getUniverseTypeById]");
//		final DateTime startTimePoint = DateTime.now();
//		try {
//			// Create the request to be returned so it can be called.
//			final Response<GetUniverseTypesTypeIdOk> itemListResponse = retrofitFactory.accessNoAuthRetrofit()
//					.create( UniverseApi.class )
//					.getUniverseTypesTypeId( typeId
//							, "en-us"
//							, server
//							, null
//							, null )
//					.execute();
//			if (!itemListResponse.isSuccessful()) {
//				return null;
//			} else {
//				logger.info( "-- [ESIDataProvider.getUniverseTypeById]> Downloading: {}-{}"
//						, itemListResponse.body().getTypeId()
//						, itemListResponse.body().getName() );
//				return itemListResponse.body();
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		} catch (RuntimeException runtime) {
//			runtime.printStackTrace();
//		} finally {
//			//			logger.info("<< [ESINetworkManager.getUniverseTypeById]> [TIMING] Full elapsed: {}"
//			//					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
//		}
//		return null;
//	}

	private List<GetUniverseRaces200Ok> getUniverseRaces( final String datasource ) {
		NeoComLogger.enter();
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseRaces( DEFAULT_ACCEPT_LANGUAGE, datasource, null, "en-us" )
					.execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			NeoComLogger.exit();
		}
		return new ArrayList<>();
	}

//	/**
//	 * Aggregates ids for some of the assets until it reached 10 and then posts and update for the whole batch.
//	 */
//	private void downloadAssetEveName( final long assetId, final Credential credential ) {
//		this.id4Names.add( assetId );
//		if (this.id4Names.size() > 9) {
//			postUserLabelNameDownload( credential );
//			this.id4Names.clear();
//		}
//	}
//
//	private void postUserLabelNameDownload( final Credential credential ) {
//		// Launch the download of the names block.
//		final List<Long> idList = new ArrayList<>();
//		idList.addAll( id4Names );
//
//		// TODO - Use a local executor for this tasks or even better remove executors at all.
//		private static final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
//		backgroundExecutor.submit(task);
//
//		GlobalDataManager.getSingleton().submitJob( () -> {
//			// Copy yhe list of assets to local to allow parallel use.
//			final List<Long> localIdList = new ArrayList<>();
//			localIdList.addAll( idList );
//			try {
//				final List<PostCharactersCharacterIdAssetsNames200Ok> itemNames = this.postCharactersCharacterIdAssetsNames(
//						credential.getAccountId(), localIdList, credential.getRefreshToken(), null );
//				for (final PostCharactersCharacterIdAssetsNames200Ok name : itemNames) {
//					final List<NeoComAsset> assetsMatch = GlobalDataManager.getSingleton().getNeocomDBHelper().getAssetDao()
//							.queryForEq( "assetId",
//									name.getItemId() );
//					for (NeoComAsset asset : assetsMatch) {
//						logger.info( "-- [DownloadManager.downloadAssetEveName]> Setting UserLabel name {} for asset {}.", name
//										.getName(),
//								name.getItemId() );
//						asset.setUserLabel( name.getName() )
//								.store();
//					}
//				}
//			} catch (SQLException sqle) {
//				sqle.printStackTrace();
//			}
//		} );
//	}

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
			// Preload the esi caches with SDE data. Do it on background to avoid problems with Android main UI thread
			this.onConstruction.downloadPilotFamilyData();
			return this.onConstruction;
		}

		public ESIDataProvider.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
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
