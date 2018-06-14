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

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.ClonesApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.FittingsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.IndustryApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.SkillsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.WalletApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrdersHistory200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillqueue200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * This class download the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManagerCharacter extends ESINetworkManagerBase {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - S T A T I C   S W A G G E R   I N T E R F A C E - C H A R A C T E R   A P I
	// --- C H A R A C T E R   P U B L I C   I N F O R M A T I O N
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


	// --- C L O N E S
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

	// --- P L A N E T A R Y   I N T E R A C T I O N
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

	// --- I N D U S T R Y
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

	public static List<GetCharactersCharacterIdMining200Ok> getCharactersCharacterIdMining( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdMining]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdMining200Ok> returnMiningList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdMining200Ok>> industryApiResponse = neocomRetrofit
						.create(IndustryApi.class)
						.getCharactersCharacterIdMining(identifier, datasource, pageCounter, null, null, null).execute();
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
				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
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

	public static List<GetCharactersCharacterIdBlueprints200Ok> getCharactersCharacterIdBlueprints( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdBlueprints]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdBlueprints200Ok> returnBlueprintList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdBlueprints200Ok>> characterApiResponse = neocomRetrofit
						.create(CharacterApi.class)
						.getCharactersCharacterIdBlueprints(identifier, datasource, pageCounter, null, null, null).execute();
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
				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
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

	// --- F I T T I N G   S E C T I O N
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

	// --- S K I L L S   S E C T I O N
	public static List<GetCharactersCharacterIdSkillqueue200Ok> getCharactersCharacterIdSkillqueue( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdSkillqueue]");
		// Store the response at the cache or if there is a network failure return the last access if available
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_SKILLQUEUE, identifier);
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			Response<List<GetCharactersCharacterIdSkillqueue200Ok>> skillsApiResponse = neocomRetrofit
					.create(SkillsApi.class)
					.getCharactersCharacterIdSkillqueue(identifier, datasource, null, null, null).execute();
			if (skillsApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, skillsApiResponse);
				return skillsApiResponse.body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// Check if this response already available at cache.
			return (List<GetCharactersCharacterIdSkillqueue200Ok>) okResponseCache.get(reference).body();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdSkillqueue]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public static GetCharactersCharacterIdSkillsOk getCharactersCharacterIdSkills( final int identifier
			, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdSkills]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_SKILLS, identifier);
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			Response<GetCharactersCharacterIdSkillsOk> skillsApiResponse = neocomRetrofit
					.create(SkillsApi.class)
					.getCharactersCharacterIdSkills(identifier, datasource, null, null, null).execute();
			if (skillsApiResponse.isSuccessful()) {
				// Store results on the cache.
				okResponseCache.put(reference, skillsApiResponse);
				return skillsApiResponse.body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// Check if this response already available at cache.
			return (GetCharactersCharacterIdSkillsOk) okResponseCache.get(reference).body();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdSkills]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
