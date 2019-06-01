package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.*;
import org.dimensinfin.eveonline.neocom.esiswagger.model.*;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import retrofit2.Response;

/**
 * This class download the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 *
 * @author Adam Antinoo
 */
public class ESINetworkManagerCharacter extends ESINetworkManagerCorporation {
	// - S T A T I C - S E C T I O N

	protected static boolean allowDownloadPass() {
		return true;
		//		if (GlobalDataManager.getDefaultSharedPreferences().getBooleanPreference(PreferenceKeys.prefkey_BlockDownloads.name(), true))
		//			return false;
		//		else
		//			return GlobalDataManager.getNetworkStatus();
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E - C H A R A C T E R   A P I
	// - C H A R A C T E R   P U B L I C   I N F O R M A T I O N
	public GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterId]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_PUBLIC, identifier);
		// Check if network is available and we have configured allowed access to download data.
		if (allowDownloadPass()) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				// Use server parameter to override configuration server to use.
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				//				final NeoComOAuth20 auth = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
				//				final Retrofit retro = NeoComRetrofitHTTP.build(auth, AGENT, cacheDataFile, cacheSize, timeout);
				final Response<GetCharactersCharacterIdOk> characterResponse = neocomRetrofit
						                                                               .create(CharacterApi.class)
						                                                               .getCharactersCharacterId(identifier
								                                                               , datasource, null).execute();
				// TODO - Replace by a new request not authenticated and direct.
				//				final Response<GetCharactersCharacterIdOk> characterResponse = processCharacterNotAuthenticated(datasource, identifier);
				if (characterResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, characterResponse);
					return characterResponse.body();
				} else return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
			} catch (IOException ioe) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", ioe.getMessage());
				ioe.printStackTrace();
				// Return cached response if available
				if (null != okResponseCache.get(reference))
					return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
				else return null;
			} catch (RuntimeException rte) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", rte.getMessage());
				rte.printStackTrace();
				// Return cached response if available
				if (null != okResponseCache.get(reference))
					return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
				else return null;
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterId]> [TIMING] Full elapsed: {}"
						, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
		} else if (null != okResponseCache.get(reference))
			return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
		else return null;
	}

	//	private static Response<GetCharactersCharacterIdOk> processCharacterNotAuthenticated( final String datasource, final int identifier
	//	) {
	//		OkHttpClient.Builder verifyClient =
	//				new OkHttpClient.Builder()
	//						.certificatePinner(
	//								new CertificatePinner.Builder()
	//										.add("login.eveonline.com", "sha256/5UeWOuDyX7IUmcKnsVdx+vLMkxEGAtzfaOUQT/caUBE=")
	//										.add("login.eveonline.com", "sha256/980Ionqp3wkYtN9SZVgMzuWQzJta1nfxNPwTem1X0uc=")
	//										.add("login.eveonline.com", "sha256/du6FkDdMcVQ3u8prumAo6t3i3G27uMP2EOhR8R0at/U=")
	//										.build())
	//						.addInterceptor(chain -> chain.proceed(
	//								chain.request()
	//										.newBuilder()
	//										.addHeader("User-Agent", "org.dimensinfin")
	//										.build()));
	//		// Verify the character authenticated and create the Credential.
	//		logger.info("-- [AuthorizationFlowActivity.registerInBackground]> Creating character verification.");
	//		final Converter.Factory GSON_CONVERTER_FACTORY =
	//				GsonConverterFactory.create(
	//						new GsonBuilder()
	//								.registerTypeAdapter(DateTime.class, new NeoComRetrofitHTTP.GSONDateTimeDeserializer())
	//								.registerTypeAdapter(LocalDate.class, new NeoComRetrofitHTTP.GSONLocalDateDeserializer())
	//								.create());
	//		try {
	//			final Response<GetCharactersCharacterIdOk> characterResponse = new Retrofit.Builder()
	//					                                                               .baseUrl("https://esi.tech.ccp.is/latest/")
	//					                                                               .addConverterFactory(GSON_CONVERTER_FACTORY)
	//					                                                               .client(verifyClient.build())
	//					                                                               .build()
	//					                                                               .create(CharacterApi.class).getCharactersCharacterId(identifier
	//							, datasource, null).execute();
	//			return characterResponse;
	//		} catch (IOException ioe) {
	//			logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", ioe.getMessage());
	//			ioe.printStackTrace();
	//			// Return cached response if available
	//			//			if ( null != okResponseCache.get(reference) ) return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
	//			//			else
	//			return null;
	//		} catch (RuntimeException rte) {
	//			logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", rte.getMessage());
	//			rte.printStackTrace();
	//			// Return cached response if available
	//			//			if ( null != okResponseCache.get(reference) ) return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
	//			//			else
	//			return null;
	//		}
	//	}

	// --- C L O N E S
	public GetCharactersCharacterIdClonesOk getCharactersCharacterIdClones( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdClones]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_CLONES, identifier);
		// Check if network is available and we have configured allowed access to download data.
		if (allowDownloadPass()) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<GetCharactersCharacterIdClonesOk> cloneApiResponse = neocomRetrofit
						                                                                    .create(ClonesApi.class)
						                                                                    .getCharactersCharacterIdClones(identifier, datasource, null, null).execute();
				if (cloneApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, cloneApiResponse);
					return cloneApiResponse.body();
				} else return (GetCharactersCharacterIdClonesOk) okResponseCache.get(reference).body();
			} catch (IOException ioe) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", ioe.getMessage());
				ioe.printStackTrace();
				// Return cached response if available
				return (GetCharactersCharacterIdClonesOk) okResponseCache.get(reference).body();
			} catch (RuntimeException rte) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", rte.getMessage());
				rte.printStackTrace();
				// Return cached response if available
				return (GetCharactersCharacterIdClonesOk) okResponseCache.get(reference).body();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdClones]> [TIMING] Full elapsed: {}"
						, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
		} else return (GetCharactersCharacterIdClonesOk) okResponseCache.get(reference).body();
	}

	// - P L A N E T A R Y   I N T E R A C T I O N
	public GetUniverseSchematicsSchematicIdOk getUniversePlanetarySchematicsById( final int schematicId ) {
		logger.info(">> [ESINetworkManagerMock.getUniversePlanetarySchematicsById]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSchematicsSchematicIdOk> schematicistResponse = neocomRetrofitNoAuth
					                                                                          .create(PlanetaryInteractionApi.class)
					                                                                          .getUniverseSchematicsSchematicId(schematicId
							                                                                          , "en-us"
							                                                                          , null)
					                                                                          .execute();
			if (!schematicistResponse.isSuccessful()) {
				return null;
			} else return schematicistResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getMarketsPrices]> [TIMING] Full elapsed: {}"
					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}

	public List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanets]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_COLONIES, identifier);
		// Check if network is available and we have configured allowed access to download data.
		//		if ( allowDownloadPass() ) {
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdPlanets200Ok>> planetaryApiResponse = this.getESIRetrofit()
					                                                                                  .create(PlanetaryInteractionApi.class)
					                                                                                  .getCharactersCharacterIdPlanets(identifier, datasource, null, null).execute();
			if (planetaryApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, planetaryApiResponse);
				return planetaryApiResponse.body();
			} else return (List<GetCharactersCharacterIdPlanets200Ok>) okResponseCache.get(reference).body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
			// Return cached response if available
			return (List<GetCharactersCharacterIdPlanets200Ok>) okResponseCache.get(reference).body();
		} catch (RuntimeException rte) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", rte.getMessage());
			rte.printStackTrace();
			// Return cached response if available
			return (List<GetCharactersCharacterIdPlanets200Ok>) okResponseCache.get(reference).body();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanets]> [TIMING] Full elapsed: {}"
					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		//		} else return (List<GetCharactersCharacterIdPlanets200Ok>) okResponseCache.get(reference).body();
	}

	public GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId( final int identifier
			, final int planetid, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, identifier);
		// Check if network is available and we have configured allowed access to download data.
		//		if (allowDownloadPass()) {
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> planetaryApiResponse = neocomRetrofit
					                                                                                 .create(PlanetaryInteractionApi.class)
					                                                                                 .getCharactersCharacterIdPlanetsPlanetId(identifier, planetid, datasource, null, null).execute();
			if (planetaryApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, planetaryApiResponse);
				return planetaryApiResponse.body();
			} else return (GetCharactersCharacterIdPlanetsPlanetIdOk) okResponseCache.get(reference).body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
			// Return cached response if available
			return (GetCharactersCharacterIdPlanetsPlanetIdOk) okResponseCache.get(reference).body();
		} catch (RuntimeException rte) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterIdPlanets]> [EXCEPTION]: {}", rte.getMessage());
			rte.printStackTrace();
			// Return cached response if available
			return (GetCharactersCharacterIdPlanetsPlanetIdOk) okResponseCache.get(reference).body();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]> [TIMING] Full elapsed: {}"
					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		//		} else return (GetCharactersCharacterIdPlanetsPlanetIdOk) okResponseCache.get(reference).body();
	}

	// - I N D U S T R Y
	public List<GetCharactersCharacterIdIndustryJobs200Ok> getCharactersCharacterIdIndustryJobs( final int identifier
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
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<List<GetCharactersCharacterIdIndustryJobs200Ok>> industryApiResponse = neocomRetrofit
						                                                                                      .create(IndustryApi.class)
						                                                                                      .getCharactersCharacterIdIndustryJobs(identifier, datasource, null, true
								                                                                                      , null).execute();
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
			return new ArrayList<>();
		} else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (List<GetCharactersCharacterIdIndustryJobs200Ok>) hit.body();
		}
	}

	/**
	 * This method encapsulates the call to the esi server to retrieve the current list of mining operations. This listing will contain the operations
	 * for the last 30 days. It will be internally cached during 1800 seconds so we have to check the hour change less frequently.
	 *
	 * @param identifier   the character unique identifier.
	 * @param refreshToken the authorization refresh token to be used on this call if the current toked is expired.
	 * @param server       the esi data server to use, tranquility or singularity.
	 * @return the list of mining actions performed during the last 30 days.
	 */
	public List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdMining]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdMining200Ok> returnMiningList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdMining200Ok>> industryApiResponse = neocomRetrofitMountebank
						                                                                                .create(IndustryApi.class)
						                                                                                .getCharactersCharacterIdMining(identifier, datasource, null, pageCounter, null).execute();
				if (!industryApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnMiningList;
				} else {
					// Copy the assets to the result list.
					returnMiningList.addAll(industryApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if (industryApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains("connection reset")) {
				// Recreate the retrofit.
				logger.info("EX [ESINetworkManager.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage());
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdMining]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnMiningList;
	}

	// --- M A R K E T   O R D E R S
	public static List<GetCharactersCharacterIdOrders200Ok> getCharactersCharacterIdOrders( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdOrders]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.INDUSTRY_MARKET_ORDERS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<List<GetCharactersCharacterIdOrders200Ok>> marketApiResponse = neocomRetrofit
						                                                                              .create(MarketApi.class)
						                                                                              .getCharactersCharacterIdOrders(identifier, datasource, null, null).execute();
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
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdOrdersHistory200Ok>> marketApiResponse = neocomRetrofit
						                                                                                     .create(MarketApi.class)
						                                                                                     .getCharactersCharacterIdOrdersHistory(identifier, datasource, null, pageCounter, null).execute();
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

	// --- A S S E T S
	public static List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdAssets]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdAssets200Ok>> assetsApiResponse = neocomRetrofit
						                                                                              .create(AssetsApi.class)
						                                                                              .getCharactersCharacterIdAssets(identifier, datasource, null, pageCounter, null).execute();
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
				logger.info("EX [ESINetworkManager.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage());
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdAssets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnAssetList;
	}

	public static List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdBlueprints]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdBlueprints200Ok> returnBlueprintList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdBlueprints200Ok>> characterApiResponse = neocomRetrofit
						                                                                                     .create(CharacterApi.class)
						                                                                                     .getCharactersCharacterIdBlueprints(identifier, datasource, null, pageCounter, null).execute();
				if (!characterApiResponse.isSuccessful()) {
					// Or error or we have reached the end of the list.
					return returnBlueprintList;
				} else {
					// Copy the assets to the result list.
					returnBlueprintList.addAll(characterApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if (characterApiResponse.body().size() < 1) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException rtex) {
			// Check if the problem is a connection reset.
			if (rtex.getMessage().toLowerCase().contains("connection reset")) {
				// Recreate the retrofit.
				logger.info("EX [ESINetworkManager.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage());
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdBlueprints]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return returnBlueprintList;
	}

	public static List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames( final int identifier, final List<Long> listItemIds, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.postCharactersCharacterIdAssetsNames]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = neocomRetrofit
					                                                                                    .create(AssetsApi.class)
					                                                                                    .postCharactersCharacterIdAssetsNames(identifier, listItemIds, datasource, null).execute();
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

	// --- F I T T I N G   S E C T I O N
	public static List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdFittings]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdFittings200Ok>> fittingApiResponse = neocomRetrofit
					                                                                                 .create(FittingsApi.class)
					                                                                                 .getCharactersCharacterIdFittings(identifier, datasource, null, null).execute();
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

	// --- W A L L E T   S E C T I O N
	public static Double getCharactersCharacterIdWallet( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdWallet]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.WALLET, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if (null == hit) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<Double> walletApiResponse = neocomRetrofit
						                                           .create(WalletApi.class)
						                                           .getCharactersCharacterIdWallet(identifier, datasource, null, null).execute();
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

	// --- S K I L L S   S E C T I O N
	public static List<GetCharactersCharacterIdSkillqueue200Ok> getCharactersCharacterIdSkillqueue( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdSkillqueue]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_SKILLQUEUE, identifier);
		// Check if network is available and we have configured allowed access to download data.
		if (allowDownloadPass()) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				Response<List<GetCharactersCharacterIdSkillqueue200Ok>> skillsApiResponse = neocomRetrofit
						                                                                            .create(SkillsApi.class)
						                                                                            .getCharactersCharacterIdSkillqueue(identifier, datasource, null, null).execute();
				if (skillsApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, skillsApiResponse);
					return skillsApiResponse.body();
				} else return (List<GetCharactersCharacterIdSkillqueue200Ok>) okResponseCache.get(reference).body();
			} catch (IOException ioe) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterIdSkillqueue]> [EXCEPTION]: {}", ioe.getMessage());
				ioe.printStackTrace();
				// Return cached response if available
				return (List<GetCharactersCharacterIdSkillqueue200Ok>) okResponseCache.get(reference).body();
			} catch (RuntimeException rte) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterIdSkillqueue]> [EXCEPTION]: {}", rte.getMessage());
				rte.printStackTrace();
				// Return cached response if available
				return (List<GetCharactersCharacterIdSkillqueue200Ok>) okResponseCache.get(reference).body();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdSkillqueue]> [TIMING] Full elapsed: {}"
						, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
		} else return (List<GetCharactersCharacterIdSkillqueue200Ok>) okResponseCache.get(reference).body();
	}

	public static GetCharactersCharacterIdSkillsOk getCharactersCharacterIdSkills( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdSkills]");
		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_SKILLQUEUE, identifier);
		// Check if network is available and we have configured allowed access to download data.
		if (allowDownloadPass()) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
				if (null != server) datasource = server;
				// Create the request to be returned so it can be called.
				Response<GetCharactersCharacterIdSkillsOk> skillsApiResponse = neocomRetrofit
						                                                               .create(SkillsApi.class)
						                                                               .getCharactersCharacterIdSkills(identifier, datasource, null, null).execute();
				if (skillsApiResponse.isSuccessful()) {
					// Store results on the cache.
					okResponseCache.put(reference, skillsApiResponse);
					return skillsApiResponse.body();
				} else return (GetCharactersCharacterIdSkillsOk) okResponseCache.get(reference).body();
			} catch (IOException ioe) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterIdSkills]> [EXCEPTION]: {}", ioe.getMessage());
				ioe.printStackTrace();
				// Return cached response if available
				return (GetCharactersCharacterIdSkillsOk) okResponseCache.get(reference).body();
			} catch (RuntimeException rte) {
				logger.error("EX [ESINetworkManager.getCharactersCharacterIdSkills]> [EXCEPTION]: {}", rte.getMessage());
				rte.printStackTrace();
				// Return cached response if available
				return (GetCharactersCharacterIdSkillsOk) okResponseCache.get(reference).body();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdSkills]> [TIMING] Full elapsed: {}"
						, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
			}
		} else return (GetCharactersCharacterIdSkillsOk) okResponseCache.get(reference).body();
	}
}
