//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.datamngmt.manager;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChonoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.ClonesApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.FittingsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.PlanetaryInteractionApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class download the OK data classes from the ESI api using the ESI authorization. It will then simply return the results
 * to the caller to be converted or to be used.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("ESINetworkManager");
	//	private static final ESINetworkManager singleton = new ESINetworkManager();

	private static String datasource = "tranquility";
	private static final String CLIENT_ID = "396e0b6cbed2488284ed4ae426133c90";
	private static final String SECRET_KEY = "gf8X16xbdaO6soJWCdYHPFfftczZyvfo63z6WUjO";
	private static final String CALLBACK = "eveauth-neotest://authentication";
	private static final String agent = "org.dimensinfin.eveonline.neocom; Dimensinfin Industries";
	private static final ESIStore store = ESIStore.DEFAULT;
	private static final List<String> scopes = new ArrayList<>(2);

	static {
		scopes.add("publicData");
		scopes.add("esi-planets.manage_planets.v1");
	}

	// TODO This is platforma dependant and should be changed.
	public static File retrofitApplicationParameter = new File("./ESINetworkManager.cache.store");

	// TODO Cache is a feature that changes from Android to SB.
	// We are going to implement the Android feaure to wrote to the NeoCom directory.
	private static final String filePath = "neocomcache" + "/" + "ESINetworkManager.cache.store";
	private static final File cacheDataFile = new File(retrofitApplicationParameter, filePath);
	private static final long cacheSize = 1000000;
	private static final long timeout = 10000;

	private static final NeoComOAuth20 neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, agent, store, scopes);
	// TODO The refresh can be striped from the creation because it is only used at runtime when executing the callbacks.
	private static final Retrofit neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, agent, cacheDataFile, cacheSize, timeout);

	/**
	 * Response cache using the ESI api cache times to speed up all possible repetitive access. Setting caches at the
	 * lowest level but that may be cahnged to the Global configuration.
	 */
	private static final Hashtable<String, Response<?>> okResponseCache = new Hashtable();

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
	public static String constructCachePointerReference (final GlobalDataManager.ECacheTimes cachecode, final int identifier) {
		return new StringBuffer("CC:")
				.append(cachecode.name())
				.append(":")
				.append(Integer.valueOf(identifier).toString())
				.toString();
	}
	public static String constructCachePointerReference (final GlobalDataManager.ECacheTimes cachecode, final int
			identifier1,final int identifier2) {
		return new StringBuffer("CC:")
				.append(cachecode.name())
				.append(":")
				.append(Integer.valueOf(identifier1).toString())
				.append(":")
				.append(Integer.valueOf(identifier2).toString())
				.toString();
	}

	public static List<GetCharactersCharacterIdPlanets200Ok> getCharactersCharacterIdPlanets (final int identifier, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanets]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManager.ECacheTimes.PLANETARY_INTERACTION_PLANETS, identifier);
		final Response<?> hit = okResponseCache.get(reference);
		if ( null == hit ) {
			final Chrono accessFullTime = new Chrono();
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				if ( null != server ) datasource = server;
				// Create the request to be returned so it can be called.
				final PlanetaryInteractionApi colonyApiRetrofit = neocomRetrofit.create(PlanetaryInteractionApi.class);
				final Response<List<GetCharactersCharacterIdPlanets200Ok>> colonyApiResponse = colonyApiRetrofit.getCharactersCharacterIdPlanets(identifier, datasource, null, null, null).execute();
				if ( !colonyApiResponse.isSuccessful() ) {
					return new ArrayList<>();
				} else {
					// Store results on the cache.
					okResponseCache.put(reference, colonyApiResponse);
					return colonyApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
			}
		}
		return (List<GetCharactersCharacterIdPlanets200Ok>) hit.body();
	}

	public static GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId (final int identifier, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getUniversePlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			if ( null != server ) datasource = server;
			// Create the request to be returned so it can be called.
			//			final UniverseApi universeApiRetrofit = neocomRetrofit.create(UniverseApi.class);
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = neocomRetrofit
					.create(UniverseApi.class)
					.getUniversePlanetsPlanetId(identifier, datasource, null, null).execute();
			if ( !universeApiResponse.isSuccessful() ) {
				return null;
			} else return universeApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniversePlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
		}
		return null;
	}

	public static GetCharactersCharacterIdPlanetsPlanetIdOk getCharactersCharacterIdPlanetsPlanetId (final int identifier, final int planetid, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]");
		// Check if this response already available at cache.
		final String reference = constructCachePointerReference(GlobalDataManager.ECacheTimes
				.PLANETARY_INTERACTION_STRUCTURES, identifier,planetid);
		final Response<?> hit = okResponseCache.get(reference);
		final Chrono accessFullTime = new Chrono();
		if ( null == hit ) {
			try {
				// Set the refresh to be used during the request.
				NeoComRetrofitHTTP.setRefeshToken(refreshToken);
				if ( null != server ) datasource = server;
				// Create the request to be returned so it can be called.
				final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> colonyApiResponse = neocomRetrofit
						.create(PlanetaryInteractionApi.class)
						.getCharactersCharacterIdPlanetsPlanetId(identifier, planetid, datasource, null, null, null).execute();
				if ( colonyApiResponse.isSuccessful() ) {
					// Store results on the cache.
//					okResponseCache.put(reference, colonyApiResponse);
					return colonyApiResponse.body();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				logger.info("<< [ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
			}
			return null;
		}
		else {
			// TODO Needs checking and verification. Also the code need to check for expirations. And be moved to the Global.
			return (GetCharactersCharacterIdPlanetsPlanetIdOk) hit.body();
		}
	}

	public static GetCharactersCharacterIdClonesOk getCharactersCharacterIdClones (final int identifier, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdClones]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			if ( null != server ) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCharactersCharacterIdClonesOk> cloneApiResponse = neocomRetrofit
					.create(ClonesApi.class)
					.getCharactersCharacterIdClones(identifier, datasource, null, null, null).execute();
			if ( !cloneApiResponse.isSuccessful() ) {
				return null;
			} else return cloneApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdClones]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
		}
		return null;
	}

	public static List<GetCharactersCharacterIdAssets200Ok> getCharactersCharacterIdAssets (final int identifier, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdAssets]");
		final Chrono accessFullTime = new Chrono();
		List<GetCharactersCharacterIdAssets200Ok> returnAssetList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			if ( null != server ) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetCharactersCharacterIdAssets200Ok>> assetsApiResponse = neocomRetrofit
						.create(AssetsApi.class)
						.getCharactersCharacterIdAssets(identifier, datasource, pageCounter, null, null, null).execute();
				if ( !assetsApiResponse.isSuccessful() ) {
					// Or error or we have reached the end of the list.
					return returnAssetList;
				} else {
					// Copy the assets to the result list.
					returnAssetList.addAll(assetsApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if ( assetsApiResponse.body().size() < 1 ) morePages = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdAssets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
		}
		return returnAssetList;
	}

	public static List<PostCharactersCharacterIdAssetsNames200Ok> postCharactersCharacterIdAssetsNames (final int identifier, final List<Long> listItemIds, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.postCharactersCharacterIdAssetsNames]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			if ( null != server ) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<PostCharactersCharacterIdAssetsNames200Ok>> assetsApiResponse = neocomRetrofit
					.create(AssetsApi.class)
					.postCharactersCharacterIdAssetsNames(identifier, listItemIds, datasource, null, null, null).execute();
			if ( !assetsApiResponse.isSuccessful() ) {
				return null;
			} else return assetsApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.postCharactersCharacterIdAssetsNames]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
		}
		return null;
	}
	public static List<GetCharactersCharacterIdFittings200Ok> getCharactersCharacterIdFittings (final int identifier, final String refreshToken, final String server) {
		logger.info(">> [ESINetworkManager.getCharactersCharacterIdFittings]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			if ( null != server ) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<List<GetCharactersCharacterIdFittings200Ok>> fittingApiResponse = neocomRetrofit
					.create(FittingsApi.class)
					.getCharactersCharacterIdFittings(identifier, datasource, null, null, null).execute();
			if ( !fittingApiResponse.isSuccessful() ) {
				return null;
			} else return fittingApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCharactersCharacterIdFittings]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChonoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ESINetworkManager () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ESINetworkManager [");
		//		buffer.append("Status: ").append(0);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
