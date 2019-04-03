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
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class downloads the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManagerZBase  {
	protected static Logger logger = LoggerFactory.getLogger("ESINetworkManager");
	protected static final ESIStore STORE = ESIStore.DEFAULT;
	protected static final List<String> SCOPES = new ArrayList<>(2);
	protected static String SCOPESTRING = "publicData";

	public static void initialize() {
		logger.info(">> [ESINetworkManager.initialize]");
		// Read the configuration and open the ESI requests cache.
		cacheDataFile = new File(GlobalDataManager.accessResource4Path(cacheFilePath));
		// Read the scoped from a resource file
		constructScopes();

		// Initialize global constants from configuration files.
		CLIENT_ID = GlobalDataManager.getResourceString("R.esi.authorization.clientid");
		SECRET_KEY = GlobalDataManager.getResourceString("R.esi.authorization.secretkey");
		CALLBACK = GlobalDataManager.getResourceString("R.esi.authorization.callback");
		AGENT = GlobalDataManager.getResourceString("R.esi.authorization.agent");
		// Verify that the constants have values. Otherwise launch exception.
		if ( CLIENT_ID.isEmpty() )
			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
		if ( SECRET_KEY.isEmpty() )
			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
		if ( CALLBACK.isEmpty() )
			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
		neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
		neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
		logger.info("<< [ESINetworkManager.initialize]");
	}

	protected static String CLIENT_ID = GlobalDataManager.getResourceString("R.esi.authorization.clientid");
	protected static String SECRET_KEY = GlobalDataManager.getResourceString("R.esi.authorization.secretkey");
	protected static String CALLBACK = GlobalDataManager.getResourceString("R.esi.authorization.callback");
	protected static String AGENT = GlobalDataManager.getResourceString("R.esi.authorization.agent");

	/** This is the location where to STORE the downloaded data from network cache. */
	protected static final String cacheFilePath = GlobalDataManager.getResourceString("R.cache.directorypath")
			+ GlobalDataManager.getResourceString("R.cache.esinetwork.filename");
	protected static File cacheDataFile = new File(cacheFilePath);
	protected static final long cacheSize = 10 * 1024 * 1024;
	protected static final long timeout = TimeUnit.SECONDS.toMillis(60);

	protected static NeoComOAuth20 neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
	// TODO The refresh can be striped from the creation because it is only used at runtime when executing the callbacks.
	protected static Retrofit neocomRetrofit = null;

	/**
	 * Response cache using the ESI api cache times to speed up all possible repetitive access. Setting caches at the
	 * lowest level but that may be changed to the Global configuration.
	 */
	protected static final Hashtable<String, Response<?>> okResponseCache = new Hashtable();

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

	public static String getAuthorizationUrl() {
		return neocomAuth20.getAuthorizationUrl();
	}

	public static String getStringScopes() {
		return SCOPESTRING;
	}

	protected static List<String> constructScopes() {
		try {
			final String propertyFileName = GlobalDataManager.getResourceString("R.esi.authorization.scopes.filename");
			//			final ClassLoader classLoader = ESINetworkManager.class.getClassLoader();
			//			final URI propertyURI = new URI(classLoader.getResource(propertyFileName).toString());
			//			final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(propertyURI.getPath())));
			final InputStream istream = GlobalDataManager.openAsset4Input(propertyFileName);
			final BufferedReader input = new BufferedReader(new InputStreamReader(istream));
			String line = input.readLine();
			while ( StringUtils.isNotEmpty(line) ) {
				SCOPES.add(line);
				line = input.readLine();
			}

			// Convert the scopes to a single string.
			SCOPESTRING = transformScopes(SCOPES);
			//		} catch (URISyntaxException e) {
			//			e.printStackTrace();
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return SCOPES;
	}

	protected static String transformScopes( final List<String> scopeList ) {
		StringBuilder scope = new StringBuilder();
		for ( String s : scopeList ) {
			scope.append(s);
			scope.append(" ");
		}
		return StringUtils.removeEnd(scope.toString(), " ");
	}

	/**
	 * Go to the ESI api to ge the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * @param server
	 * @return
	 */
	public static List<GetMarketsPrices200Ok> getMarketsPrices( final String server ) {
		logger.info(">> [ESINetworkManager.getMarketsPrices]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = neocomRetrofit.create(MarketApi.class)
					.getMarketsPrices("tranquility", null)
					.execute();
			if ( !marketApiResponse.isSuccessful() ) {
				return new ArrayList<>();
			} else return marketApiResponse.body();
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getMarketsPrices]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
