package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.StatusApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.WalletApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
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
public class ESIDataProvider {
	public static final String DEFAULT_ESI_SERVER = "Tranquility".toLowerCase();
	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	protected static final Logger logger = LoggerFactory.getLogger( ESIDataProvider.class );
	private static final List<Long> id4Names = new ArrayList<>();
	// - C A C H E S
	private static final Map<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap<>( 100 );
	private static final Map<Integer, GetUniverseRaces200Ok> racesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseAncestries200Ok> ancestriesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new HashMap<>();
	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected LocationCatalogService locationCatalogService;
	protected NeoComRetrofitFactory neocomRetrofitFactory;
	protected StoreCacheManager storeCacheManager;
	protected RetrofitFactory retrofitFactory;

	// - C O N S T R U C T O R S
	protected ESIDataProvider() {}

	/**
	 * Allows the selection of the ESI server. Changes the retrofit factory to discard current retrofit instances and create
	 * new ones but related to the new server configuration.
	 *
	 * @param esiServer the esi server name, be it the production (Tranquility) or development (Singularity).
	 */
	@Deprecated
	public void activateEsiServer( final String esiServer ) {
		this.neocomRetrofitFactory.activateEsiServer( esiServer );
	}

	/**
	 * Method to export the current retrofit factory authorization URL being in use by the current server configuration.
	 *
	 * @param esiServer the esi server name, be it the production (Tranquility) or development (Singularity).
	 * @return the current authrorization url.
	 */
	public String getAuthorizationUrl4Server( final String esiServer ) {
		return this.neocomRetrofitFactory.getAuthorizationUrl4Server( esiServer );
	}

	// - D O W N L O A D   S T A R T E R S
//	public void downloadItemPrices() {
//		// Initialize and process the list of market process form the ESI full market data.
//		final List<GetMarketsPrices200Ok> marketPrices = this.getUniverseMarketsPrices();
//		logger.info( ">> [ESIDataProvider.downloadItemPrices]> Download market prices: {} items", marketPrices.size() );
//		for (GetMarketsPrices200Ok price : marketPrices) {
//			marketDefaultPrices.put( price.getTypeId(), price );
//		}
//	}

	private void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getUniverseRaces( DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download race: {} items", racesList.size() );
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put( race.getRaceId(), race );
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getUniverseAncestries(
				DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download ancestries: {} items", ancestriesList.size() );
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put( ancestry.getId(), ancestry );
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getUniverseBloodlines(
				DEFAULT_ESI_SERVER );
		logger.info( "-- [ESIDataProvider.downloadPilotFamilyData]> Download blood lines: {} items", bloodLineList.size() );
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put( bloodLine.getBloodlineId(), bloodLine );
		}
	}

	// - S D E   D A T A
	public GetUniverseStructuresStructureIdOk searchStructureById( final Long structureId, final Credential credential ) {
		final String refreshToken = credential.getRefreshToken();
		final int identifier = credential.getAccountId();
		try {
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			final Response<GetUniverseStructuresStructureIdOk> universeResponse = this.neocomRetrofitFactory
					.accessESIAuthRetrofit()
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
//	public double searchSDEMarketPrice( final int typeId ) {
//		logger.info( "-- [ESIDataProvider.searchSDEMarketPrice]> price for: {}", typeId );
////		if (0 == marketDefaultPrices.size()) this.downloadItemPrices();
//		if (marketDefaultPrices.containsKey( typeId )) return marketDefaultPrices.get( typeId ).getAdjustedPrice();
//		else return -1.0;
//	}

	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessItem( itemId ).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		logger.info( "-- [ESIDataProvider.searchItemGroup4Id]> targetGroupId: {}", groupId );
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		logger.info( "-- [ESIDataProvider.searchItemCategory4Id]> categoryId: {}", categoryId );
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
		return racesCache.get( identifier );
	}

	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
		return this.ancestriesCache.get( identifier );
	}

	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
		return this.bloodLinesCache.get( identifier );
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

//	public LocationCatalogService.LocationCacheAccessType lastSearchLocationAccessType() {
//		return this.locationCatalogService.lastSearchLocationAccessType();
//	}


//	@Deprecated
//	protected void prepareRaces() {
//		if (racesCache.size() < 1) {
//			// Download race, bloodline and other pilot data.
//			final List<GetUniverseRaces200Ok> racesList = this.getUniverseRaces( GlobalDataManager.TRANQUILITY_DATASOURCE );
//			logger.info( ">> [ESIDataProvider.downloadPilotFamilyData]> Download race: {} items", racesList.size() );
//			for (GetUniverseRaces200Ok race : racesList) {
//				racesCache.put( race.getRaceId(), race );
//			}
//		}
//	}

	// - U N I V E R S E
//	@TimeElapsed
	public GetStatusOk getUniverseStatus( final String server ) {
		//		logger.info(">> [ESIDataProvider.getUniverseStatus]");
		// Store the response at the cache or if there is a network failure return the last access if available
		//		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.SERVERSTATUS, 0);
		//		final Chrono accessFullTime = new Chrono();
		try {
			String datasource = DEFAULT_ESI_SERVER; // Set the server to the default or to the selected server.
			if (null != server) datasource = server;
			final Response<GetStatusOk> statusApiResponse = this.neocomRetrofitFactory.accessNoAuthRetrofit()
					.create( StatusApi.class )
					.getStatus( datasource.toLowerCase(), null ).execute();
			if (statusApiResponse.isSuccessful())
				return statusApiResponse.body();
//			} else {
//				// Use the cached data is available.
//				return null;
//			}
		} catch (IOException ioe) {
//			ioe.printStackTrace();
			//		} finally {
			//			logger.info("<< [ESIDataProvider.getUniverseStatus]> [TIMING] Full elapsed: {}"
			//					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

//	/**
//	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
//	 * because probably there is not valid market price information at other servers.
//	 * To access the public data it will use the current unauthorized retrofit connection.
//	 */
////	@TimeElapsed
//	private List<GetMarketsPrices200Ok> getUniverseMarketsPrices() {
//		try {
//			// Create the request to be returned so it can be called.
//			final Response<List<GetMarketsPrices200Ok>> marketApiResponse =
//					this.retrofitFactory.accessNoAuthRetrofit()
//							.create( MarketApi.class )
//							.getMarketsPrices( DEFAULT_ESI_SERVER.toLowerCase(), null )
//							.execute();
//			if (!marketApiResponse.isSuccessful()) {
//				return new ArrayList<>();
//			} else return marketApiResponse.body();
//		} catch (IOException ioe) {
//			return new ArrayList<>();
//		} catch (RuntimeException rte) {
//			rte.printStackTrace();
//			return new ArrayList<>();
//		}
//	}

//	protected GetUniverseGroupsGroupIdOk getUniverseGroupById( final Integer groupId ) {
//		try {
//			// Create the request to be returned so it can be called.
//			final Response<GetUniverseGroupsGroupIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(
//					UniverseApi.class )
//					.getUniverseGroupsGroupId( groupId
//							, DEFAULT_ACCEPT_LANGUAGE
//							, DEFAULT_ESI_SERVER.toLowerCase(),
//							null, null )
//					.execute();
//			if (!groupResponse.isSuccessful()) {
//				return null;
//			} else return groupResponse.body();
//		} catch (IOException ioe) {
//			return null;
//		}
//	}

//	protected GetUniverseCategoriesCategoryIdOk getUniverseCategoryById( final Integer categoryId ) {
//		try {
//			// Create the request to be returned so it can be called.
//			final Response<GetUniverseCategoriesCategoryIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(
//					UniverseApi.class )
//					.getUniverseCategoriesCategoryId(
//							categoryId
//							, DEFAULT_ACCEPT_LANGUAGE
//							, DEFAULT_ESI_SERVER
//									.toLowerCase(),
//							null, null )
//					.execute();
//			if (!groupResponse.isSuccessful()) {
//				return null;
//			} else return groupResponse.body();
//		} catch (IOException ioe) {
//			return null;
//		}
//	}

	private List<GetUniverseRaces200Ok> getUniverseRaces( final String datasource ) {
		logger.info( ">> [ESIDataProvider.getUniverseRaces]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = neocomRetrofitFactory.accessNoAuthRetrofit()
					.create( UniverseApi.class )
					.getUniverseRaces( DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, "en-us" )
					.execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			logger.info("<< [ESIDataProvider.getUniverseRaces]> [TIMING] Full elapsed: {}",
//			            accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private List<GetUniverseAncestries200Ok> getUniverseAncestries( final String datasource ) {
		logger.info( ">> [ESIDataProvider.getUniverseAncestries]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = neocomRetrofitFactory.accessNoAuthRetrofit()
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
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = neocomRetrofitFactory.accessNoAuthRetrofit().create(
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

	//	@TimeElapsed
	public GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier ) {
		try {
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = this.neocomRetrofitFactory.accessNoAuthRetrofit()
					.create( UniverseApi.class )
					.getUniversePlanetsPlanetId(
							identifier,
							DEFAULT_ESI_SERVER
									.toLowerCase(),
							null ).execute();
			if (!universeApiResponse.isSuccessful()) {
				return null;
			} else return universeApiResponse.body();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

//	// - A L L I A N C E   P U B L I C   I N F O R M A T I O N
//	public GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier ) {
//		logger.info( ">> [ESIDataProvider.getCorporationsCorporationId]" );
////		final Chrono accessFullTime = new Chrono();
//		try {
//			// Set the refresh to be used during the request.
////			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
//			String datasource = DEFAULT_ESI_SERVER;
//			// Use server parameter to override configuration server to use.
////			if (null != server) datasource = server;
//			// Create the request to be returned so it can be called.
//			final Response<GetAlliancesAllianceIdOk> allianceResponse = this.retrofitFactory.accessNoAuthRetrofit()
//					.create( AllianceApi.class )
//					.getAlliancesAllianceId( identifier,
//							datasource,
//							null )
//					.execute();
//			if (allianceResponse.isSuccessful())
//				return allianceResponse.body();
//		} catch (IOException ioe) {
//			logger.error( "EX [ESIDataProvider.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage() );
//			ioe.printStackTrace();
////		} finally {
////			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
//		}
//		return null;
//	}
//
//	public GetAlliancesAllianceIdIconsOk getAlliancesAllianceIdIcons( final int identifier ) {
//		logger.info( ">> [ESIDataProvider.getAlliancesAllianceIdIcons]" );
//		try {
//			final Response<GetAlliancesAllianceIdIconsOk> allianceResponse = this.retrofitFactory.accessNoAuthRetrofit()
//					.create( AllianceApi.class )
//					.getAlliancesAllianceIdIcons(
//							identifier,
//							DEFAULT_ESI_SERVER, null )
//					.execute();
//			if (allianceResponse.isSuccessful())
//				return allianceResponse.body();
//		} catch (IOException ioe) {
//			logger.error( "EX [ESIDataProvider.getAlliancesAllianceIdIcons]> [EXCEPTION]: {}", ioe.getMessage() );
//			ioe.printStackTrace();
//		}
//		return null;
//	}

	// - C H A R A C T E R   P U B L I C   I N F O R M A T I O N
	@TimeElapsed
	public GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier ) {
		logger.info( "-- [ESIDataProvider.getCharactersCharacterId]> Character identifier: {}", identifier );
		try {
			final Response<GetCharactersCharacterIdOk> characterResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( CharacterApi.class )
					.getCharactersCharacterId(
							identifier,
							DEFAULT_ESI_SERVER, null )
					.execute();
			if (characterResponse.isSuccessful()) return characterResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterId]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
		} catch (RuntimeException rte) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterId]> [EXCEPTION]: {}", rte.getMessage() );
			rte.printStackTrace();
		}
		return null;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets( final Credential credential ) {
		//		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanets]");
		//		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		//		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_COLONIES, identifier);
		// Check if network is available and we have configured allowed access to download data.
		//		if ( allowDownloadPass() ) {
		try {
			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
//			String datasource = DEFAULT_ESI_SERVER;
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdPlanets200Ok>> planetaryApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanets(
							credential.getAccountId(),
							credential.getDataSource().toLowerCase(), null, null )
					.execute();
			if (planetaryApiResponse.isSuccessful())
				return planetaryApiResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
			// Return cached response if available
//			return new ArrayList<>();
		} catch (RuntimeException rte) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", rte.getMessage() );
			rte.printStackTrace();
			// Return cached response if available
//			return new ArrayList<>();
			//		} finally {
			//			logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanets]> [TIMING] Full elapsed: {}"
			//					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
		//		} else return (List<GetCharactersCharacterIdPlanets200Ok>) okResponseCache.get(reference).body();
	}

	@TimeElapsed
	public GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId( final int identifier
			, final int planetid, final String refreshToken, final String server ) {
		//		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]");
		//		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		//		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, identifier);
		// Check if network is available and we have configured allowed access to download data.
		//		if (allowDownloadPass()) {
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> planetaryApiResponse = this.neocomRetrofitFactory
					.accessESIAuthRetrofit()
					.create( PlanetaryInteractionApi.class )
					.getCharactersCharacterIdPlanetsPlanetId(
							identifier, planetid,
							datasource, null,
							null ).execute();
			if (planetaryApiResponse.isSuccessful()) {
				// Store results on the cache.
				//				okResponseCache.put(reference, planetaryApiResponse);
				return planetaryApiResponse.body();
			} else return null;
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
			// Return cached response if available
			return null;
		} catch (RuntimeException rte) {
			logger.error( "EX [ESIDataProvider.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", rte.getMessage() );
			rte.printStackTrace();
			// Return cached response if available
			return null;
			//		} finally {
			//			logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]> [TIMING] Full elapsed: {}"
			//					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		//		} else return (GetCharactersCharacterIdPlanetsPlanetIdOk) okResponseCache.get(reference).body();
	}

	// - P L A N E T A R Y   I N T E R A C T I O N   P U B L I C   I N F O R M A T I O N
	@TimeElapsed
	public GetUniverseSchematicsSchematicIdOk getUniversePlanetarySchematicsById( final int schematicId ) {
		logger.info( ">> [ESIDataProvider.getUniversePlanetarySchematicsById]" );
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSchematicsSchematicIdOk> schematicistResponse = this.neocomRetrofitFactory
					.accessNoAuthRetrofit()
					.create( PlanetaryInteractionApi.class )
					.getUniverseSchematicsSchematicId(
							schematicId
							, "en-us"
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

	// - M I N I N G

	/**
	 * This method encapsulates the call to the esi server to retrieve the current list of mining operations. This listing will
	 * contain the operations
	 * for the last 30 days. It will be internally cached during 1800 seconds so we have to check the hour change less
	 * frequently.
	 *
	 * @param credential the credential to be used when composing the ESI call.
	 * @return the list of mining actions performed during the last 30 days.
	 */
	public List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final Credential credential ) {
		return this.getCharactersCharacterIdMining( credential.getAccountId(),
				credential.getRefreshToken(),
				credential.getDataSource() );
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final int identifier
			, final String refreshToken, final String server ) {
		logger.info( ">> [ESIDataProvider.getCharactersCharacterIdMining]" );
		//		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdMining200Ok> returnMiningList = new ArrayList<>( 1000 );
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdMining200Ok>> industryApiResponse = this.neocomRetrofitFactory
						.accessESIAuthRetrofit()
						.create( IndustryApi.class )
						.getCharactersCharacterIdMining(
								identifier,
								datasource, null,
								pageCounter, null )
						.execute();
				if (!industryApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnMiningList;
				} else {
					// Copy the assets to the result list.
					returnMiningList.addAll( industryApiResponse.body() );
					pageCounter++;
					// Check for out of page running.
					if (industryApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains( "connection reset" )) {
				// Recreate the retrofit.
				logger.info( "EX [ESIDataProvider.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage() );
				this.neocomRetrofitFactory.reset();
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
			//		} finally {
			//			logger.info("<< [ESINetworkManager.getCharactersCharacterIdMining]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnMiningList;
	}

	// - C H A R A C T E R
	public Double getCharactersCharacterIdWallet( final Credential credential ) {
		logger.info( ">> [ESIDataProvider.getCharactersCharacterIdWallet]" );
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( credential.getRefreshToken() );
//			String datasource = DEFAULT_ESI_SERVER;
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<Double> walletApiResponse = this.retrofitFactory
					.accessAuthenticatedConnector( credential )
					.create( WalletApi.class )
					.getCharactersCharacterIdWallet( credential.getAccountId()
							, credential.getDataSource(), null, null )
					.execute();
			if (walletApiResponse.isSuccessful()) return walletApiResponse.body();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
			if (rtex.getMessage().toLowerCase().contains( "connection reset" )) {
				logger.info( "EX [ESIDataProvider.getCharactersCharacterIdWallet]> Exception: {}", rtex.getMessage() );
				this.neocomRetrofitFactory.reset();
			}
			if (rtex.getMessage().toLowerCase().contains( "response body is incorrect" )) {
				logger.info( "EX [ESIDataProvider.getCharactersCharacterIdWallet]> Exception: {}", rtex.getMessage() );
				this.neocomRetrofitFactory.reset();
			}
		}
		return -1.0;
	}

	@TimeElapsed
	public List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets( final Credential credential
			/*final int identifier
			, final String refreshToken
			, final String server */ ) {
		logger.info( ">> [ESIDataProvider.getCharactersCharacterIdAssets]" );
		//		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>( 1000 );
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( credential.getRefreshToken() );
//			String datasource = DEFAULT_ESI_SERVER;
//			if (null != server) datasource = server;
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
					returnAssetList.addAll( assetsApiResponse.body() );
					pageCounter++;
					// Check for out of page running.
					if (assetsApiResponse.body().size() < 1) morePages = false;
				} else
					return returnAssetList; // Or error or we have reached the end of the list.
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains( "connection reset" )) {
				logger.info( "EX [ESIDataProvider.getCharactersCharacterIdAssets]> Exception: {}", rtex.getMessage() );
				this.neocomRetrofitFactory.reset();
			}
			if (rtex.getMessage().toLowerCase().contains( "response body is incorrect" )) {
				logger.info( "EX [ESIDataProvider.getCharactersCharacterIdAssets]> Exception: {}", rtex.getMessage() );
				this.neocomRetrofitFactory.reset();
			}
		}
		return returnAssetList;
	}

	public List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info( ">> [ESIDataProvider.getCharactersCharacterIdBlueprints]" );
		//		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdBlueprints200Ok> returnBlueprintList = new ArrayList<>( 1000 );
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdBlueprints200Ok>> characterApiResponse = this.neocomRetrofitFactory
						.accessESIAuthRetrofit()
						.create( CharacterApi.class )
						.getCharactersCharacterIdBlueprints(
								identifier,
								datasource,
								null,
								pageCounter,
								null )
						.execute();
				if (!characterApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnBlueprintList;
				} else {
					// Copy the assets to the result list.
					returnBlueprintList.addAll( characterApiResponse.body() );
					pageCounter++;
					// Check for out of page running.
					if (characterApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains( "connection reset" )) {
				// Recreate the retrofit.
				logger.info( "EX [ESINetworkManager.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage() );
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
			//		} finally {
			//			logger.info("<< [ESINetworkManager.getCharactersCharacterIdBlueprints]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnBlueprintList;
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

	public List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames( final int identifier, final List<Long> listItemIds, final String refreshToken, final String server ) {
		logger.info( ">> [ESINetworkManager.postCharactersCharacterIdAssetsNames]" );
		//		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = DEFAULT_ESI_SERVER;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = this.neocomRetrofitFactory
					.accessESIAuthRetrofit()
					.create( AssetsApi.class )
					.postCharactersCharacterIdAssetsNames(
							identifier,
							listItemIds,
							datasource
									.toLowerCase(),
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
		private NeoComRetrofitFactory.Builder retrofitFactoryBuilder = new NeoComRetrofitFactory.Builder();

		public Builder() {
			this.onConstruction = new ESIDataProvider();
		}

		public Builder( final ESIDataProvider preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new ESIDataProvider();
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

		public ESIDataProvider.Builder withStoreCacheManager( final StoreCacheManager storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}

		public ESIDataProvider.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}

		public ESIDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			this.onConstruction.neocomRetrofitFactory = this.retrofitFactoryBuilder // Allow mocking for the retrofit factory.
					.withConfigurationProvider( this.onConstruction.configurationProvider )
					.withFileSystemAdapter( this.onConstruction.fileSystemAdapter )
					.build();
			Objects.requireNonNull( this.onConstruction.neocomRetrofitFactory );

			// Inject the new adapter to the classes that depend on it.
			NeoComUpdater.injectsEsiDataAdapter( this.onConstruction );

			// Preload the esi caches with SDE data.
//			this.onConstruction.downloadItemPrices();
			this.onConstruction.downloadPilotFamilyData();
			return this.onConstruction;
		}
	}
}
