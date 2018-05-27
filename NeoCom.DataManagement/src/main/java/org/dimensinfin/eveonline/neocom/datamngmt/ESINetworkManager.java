//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt;

import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AllianceApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.ClonesApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.FittingsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.WalletApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrdersHistory200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;

/**
 * This class download the OK data classes from the ESI api using the ESI authorization. It will then simply return the results
 * to the caller to be converted or to be used.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("ESINetworkManager");

//	private static final Hashtable<ECacheTimes, Long> ESICacheTimes = new Hashtable();
//	private static final long DEFAULT_CACHE_TIME = 600 * 1000;
//
//	public enum ECacheTimes {
//		CHARACTER_PUBLIC, CHARACTER_CLONES, PLANETARY_INTERACTION_PLANETS, PLANETARY_INTERACTION_STRUCTURES, ASSETS_ASSETS, CORPORATION_CUSTOM_OFFICES, UNIVERSE_SCHEMATICS, MARKET_PRICES, INDUSTRY_JOBS
//	}
//
//	static {
//		ESICacheTimes.put(ECacheTimes.CHARACTER_PUBLIC, TimeUnit.SECONDS.toMillis(3600));
//		ESICacheTimes.put(ECacheTimes.CHARACTER_CLONES, TimeUnit.SECONDS.toMillis(200));
//		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_PLANETS, TimeUnit.SECONDS.toMillis(600));
//		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, TimeUnit.SECONDS.toMillis(600));
//		ESICacheTimes.put(ECacheTimes.ASSETS_ASSETS, TimeUnit.SECONDS.toMillis(3600));
//		ESICacheTimes.put(ECacheTimes.MARKET_PRICES, TimeUnit.SECONDS.toMillis(3600));
//		ESICacheTimes.put(ECacheTimes.INDUSTRY_JOBS, TimeUnit.SECONDS.toMillis(300));
//	}

	private static final String CLIENT_ID = GlobalDataManager.getResourceString("R.esi.authorization.clientid");
	private static final String SECRET_KEY = GlobalDataManager.getResourceString("R.esi.authorization.secretkey");
	private static final String CALLBACK = GlobalDataManager.getResourceString("R.esi.authorization.callback");
	private static final String AGENT = GlobalDataManager.getResourceString("R.esi.authorization.agent");
	private static final ESIStore STORE = ESIStore.DEFAULT;
	private static final List<String> SCOPES = new ArrayList<>(2);
	private static String SCOPESTRING = "publicData";

	public static void initialize() throws IOException {
		logger.info(">> [ESINetworkManager.initialize]");
		// Read the configuration and open the ESI requests cache.
		cacheDataFile = new File(GlobalDataManager.accessResource4Path(cacheFilePath));
		neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
		// Read the scoped from a resource file
		constructScopes();
		logger.info("<< [ESINetworkManager.initialize]");
	}

	/**
	 * This is the location where to STORE the downloaded data from network cache.
	 */
	private static final String cacheFilePath = GlobalDataManager.getResourceString("R.cache.directorypath")
			+ GlobalDataManager.getResourceString("R.cache.esinetwork.filename");
	private static File cacheDataFile = new File(cacheFilePath);
	private static final long cacheSize = 100 * 1024 * 1024;
	private static final long timeout = TimeUnit.SECONDS.toMillis(60);

	private static final NeoComOAuth20 neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
	// TODO The refresh can be striped from the creation because it is only used at runtime when executing the callbacks.
	private static Retrofit neocomRetrofit = null;

	/**
	 * Response cache using the ESI api cache times to speed up all possible repetitive access. Setting caches at the
	 * lowest level but that may be changed to the Global configuration.
	 */
	private static final Hashtable<String, Response<?>> okResponseCache = new Hashtable();

	// - S T A T I C   U T I L I T Y   M E T H O D S
	public static String constructCachePointerReference( final GlobalDataManagerCache.ECacheTimes cachecode, final int identifier ) {
		return new StringBuffer("CC:")
				.append(cachecode.name())
				.append(":")
				.append(Integer.valueOf(identifier).toString())
				.toString();
	}

	public static String constructCachePointerReference( final GlobalDataManagerCache.ECacheTimes cachecode, final int
			identifier1, final int identifier2 ) {
		return new StringBuffer("CC:")
				.append(cachecode.name())
				.append(":")
				.append(Integer.valueOf(identifier1).toString())
				.append(":")
				.append(Integer.valueOf(identifier2).toString())
				.toString();
	}

	public static String getStringScopes() {
		return SCOPESTRING;
	}

	private static List<String> constructScopes() {
		try {
			final String propertyFileName = GlobalDataManager.getResourceString("R.esi.authorization.scopes.filename");
//			final ClassLoader classLoader = ESINetworkManager.class.getClassLoader();
//			final URI propertyURI = new URI(classLoader.getResource(propertyFileName).toString());
//			final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(propertyURI.getPath())));
			final InputStream istream = GlobalDataManager.openAsset4Input(propertyFileName);
			final BufferedReader input = new BufferedReader(new InputStreamReader(istream));
			String line = input.readLine();
			while (StringUtils.isNotEmpty(line)) {
				SCOPES.add(line);
				line = input.readLine();
			}

			// Convert the scopes to a single string.
			SCOPESTRING = transformScopes(SCOPES);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SCOPES;
	}

	private static String transformScopes( final List<String> scopeList ) {
		StringBuilder scope = new StringBuilder();
		for (String s : scopeList) {
			scope.append(s);
			scope.append(" ");
		}
		return StringUtils.removeEnd(scope.toString(), " ");
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E
	// - C H A R A C T E R
	//--- CHARACTER PUBLIC INFORMATION
	public static GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdClones]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCharactersCharacterIdOk> characterResponse = neocomRetrofit
					.create(CharacterApi.class)
					.getCharactersCharacterId(identifier, datasource, null, null).execute();
			if (characterResponse.isSuccessful())
				return characterResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdClones]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} catch (RuntimeException rte) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdClones]> [EXCEPTION]: {}", rte.getMessage());
			rte.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdClones]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}


	// - C L O N E S
	//--- CLONES
	public static GetCharactersCharacterIdClonesOk getCharactersCharacterIdClones( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdClones]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCharactersCharacterIdClonesOk> cloneApiResponse = neocomRetrofit
					.create(ClonesApi.class)
					.getCharactersCharacterIdClones(identifier, datasource, null, null, null).execute();
			if (!cloneApiResponse.isSuccessful()) {
				return null;
			} else return cloneApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdClones]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}
	//--- IMPLANTS

	// - C O R P O R A T I O N S
	//--- CORPORATION INFORMATION
	public static GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCorporationsCorporationIdOk> corporationResponse = neocomRetrofit
					.create(CorporationApi.class)
					.getCorporationsCorporationId(identifier, datasource, null, null).execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - A L L I A N C E S
	//--- ALLIANCE INFORMATION
	public static GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetAlliancesAllianceIdOk> allianceResponse = neocomRetrofit
					.create(AllianceApi.class)
					.getAlliancesAllianceId(identifier, datasource, null, null).execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}


	// - P L A N E T A R Y   I N T E R A C T I O N
	public static List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanets]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.PLANETARY_INTERACTION_PLANETS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		if (null == hit) {
			final Chrono accessFullTime = new Chrono();
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.SERVER_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final PlanetaryInteractionApi colonyApiRetrofit = neocomRetrofit.create(PlanetaryInteractionApi.class);
				final Response<List<GetCharactersCharacterIdPlanets200Ok>> colonyApiResponse = colonyApiRetrofit.getCharactersCharacterIdPlanets(identifier, datasource, null, null, null).execute();
				if (!colonyApiResponse.isSuccessful()) {
					return new ArrayList<>();
				} else {
					// Store results on the cache.
					okResponseCache.put(reference, colonyApiResponse);
					return colonyApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
		}
		return (List<GetCharactersCharacterIdPlanets200Ok>) hit.body();
	}

	public static GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getUniversePlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			//			final UniverseApi universeApiRetrofit = neocomRetrofit.create(UniverseApi.class);
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = neocomRetrofit
					.create(UniverseApi.class)
					.getUniversePlanetsPlanetId(identifier, datasource, null, null).execute();
			if (!universeApiResponse.isSuccessful()) {
				return null;
			} else return universeApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniversePlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public static GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId( final int identifier, final int planetid, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, identifier, planetid);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.SERVER_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> colonyApiResponse = neocomRetrofit
						.create(PlanetaryInteractionApi.class)
						.getCharactersCharacterIdPlanetsPlanetId(identifier, planetid, datasource, null, null, null).execute();
				if (colonyApiResponse.isSuccessful()) {
					// Store results on the cache.
//					okResponseCache.put(reference, colonyApiResponse);
					return colonyApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
			return null;
		} else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (GetCharactersCharacterIdPlanetsPlanetIdOk) hit.body();
		}
	}

	// - I N D U S T R Y
	public static List<GetCharactersCharacterIdIndustryJobs200Ok> getCharactersCharacterIdIndustryJobs( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdIndustryJobs]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.INDUSTRY_JOBS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.SERVER_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<List<GetCharactersCharacterIdIndustryJobs200Ok>> industryApiResponse = neocomRetrofit
						.create(IndustryApi.class)
						.getCharactersCharacterIdIndustryJobs(identifier, datasource, true
								, null, null, null).execute();
				if (industryApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, industryApiResponse);
					return industryApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdIndustryJobs]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
			return null;
		} else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (List<GetCharactersCharacterIdIndustryJobs200Ok>) hit.body();
		}
	}

	public static List<GetCharactersCharacterIdOrders200Ok> getCharactersCharacterIdOrders( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdOrders]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.MARKET_ORDERS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.SERVER_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<List<GetCharactersCharacterIdOrders200Ok>> marketApiResponse = neocomRetrofit
						.create(MarketApi.class)
						.getCharactersCharacterIdOrders(identifier, datasource, null, null, null).execute();
				if (marketApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, marketApiResponse);
					return marketApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdOrders]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
			return null;
		} else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (List<GetCharactersCharacterIdOrders200Ok>) hit.body();
		}
	}

	public static List<GetCharactersCharacterIdOrdersHistory200Ok> getCharactersCharacterIdOrdersHistory( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdOrdersHistory]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdOrdersHistory200Ok> returnOrderList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdOrdersHistory200Ok>> marketApiResponse = neocomRetrofit
						.create(MarketApi.class)
						.getCharactersCharacterIdOrdersHistory(identifier, datasource, pageCounter, null, null, null).execute();
				if (!marketApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnOrderList;
				} else {
					// Copy the assets to the result list.
					returnOrderList.addAll(marketApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if (marketApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdOrdersHistory]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnOrderList;
	}

	public static List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdAssets]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdAssets200Ok>> assetsApiResponse = neocomRetrofit
						.create(AssetsApi.class)
						.getCharactersCharacterIdAssets(identifier, datasource, pageCounter, null, null, null).execute();
				if (!assetsApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnAssetList;
				} else {
					// Copy the assets to the result list.
					returnAssetList.addAll(assetsApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if (assetsApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains("connection reset")) {
				// Recreate the retrofit.
				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdAssets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnAssetList;
	}

	public static List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames( final int identifier, final List<Long> listItemIds, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.postCharactersCharacterIdAssetsNames]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = neocomRetrofit
					.create(AssetsApi.class)
					.postCharactersCharacterIdAssetsNames(identifier, listItemIds, datasource, null, null, null).execute();
			if (!assetsApiResponse.isSuccessful()) {
				return null;
			} else return assetsApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.postCharactersCharacterIdAssetsNames]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public static List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdFittings]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdFittings200Ok>> fittingApiResponse = neocomRetrofit
					.create(FittingsApi.class)
					.getCharactersCharacterIdFittings(identifier, datasource, null, null, null).execute();
			if (!fittingApiResponse.isSuccessful()) {
				return null;
			} else return fittingApiResponse.body();
		} catch (RuntimeException rte) {
			rte.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdFittings]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	//--- W A L L E T   S E C T I O N
	public static Double getCharactersCharacterIdWallet( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdWallet]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.MARKET_ORDERS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.SERVER_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<Double> walletApiResponse = neocomRetrofit
						.create(WalletApi.class)
						.getCharactersCharacterIdWallet(identifier, datasource, null, null, null).execute();
				if (walletApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, walletApiResponse);
					return walletApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdWallet]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
			return 0.0;
		} else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (Double) hit.body();
		}
	}

	/**
	 * Go to the ESI api to ge the list of market prices. This method does not use other server than the Tranquility because
	 * probably there is not valid market price information at other servers.
	 * @param server
	 * @return
	 */
	public static List<GetMarketsPrices200Ok> getMarketsPrices( final String server ) {
		logger.info(">> [ESINetworkManager.getMarketsPrices]");
		final Chrono accessFullTime = new Chrono();
		try {
//			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
//			String datasource = GlobalDataManager.SERVER_DATASOURCE;
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = neocomRetrofit.create(MarketApi.class)
					.getMarketsPrices("tranquility", null, null)
					.execute();
			if (!marketApiResponse.isSuccessful()) {
				return new ArrayList<>();
			} else return marketApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getMarketsPrices]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public ESINetworkManager() {
//		super();
//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ESINetworkManager [");
		//		buffer.append("Status: ").append(0);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
