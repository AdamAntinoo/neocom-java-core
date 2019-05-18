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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class downloads the OK data classes from the ESI api using the ESI authorization OAuth2 protocol. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exception interception so the caller can perform the correct
 * interpretation in case of an unexpected situation.
 *
 * Eve Online has two main data servers. Tranquility for the production game and Singularity for testing and external development. The
 * gamer can choose any of this two servers so the ESI configuration provides access for both. Because this selection depends mostly
 * on the Credential in use, for all the backend access the isolation is ready and the endpoint configures the credential as an input
 * field. But for all other OAuth access (during the login process, by example) there is no server selection. For such cases, instead
 * adding the server parameter the solution is to use the last connected server and provide a function to connect the production
 * or the development server.
 *
 * @author Adam Antinoo
 */
public class ESINetworkManagerZBase {
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	protected static final long CACHE_SIZE = 10 * 1024 * 1024;
	protected static final long TIMEOUT = TimeUnit.SECONDS.toMillis(60);
	protected static final ESIStore STORE = ESIStore.DEFAULT;

	protected static ESINetworkManagerZBase singleton;

	//	protected static final List<String> SCOPES = new ArrayList<>(2);
	protected static String SCOPESTRING = "publicData";
	//
	//	protected static String CLIENT_ID;
	//	protected static String SECRET_KEY;
	//	protected static String CALLBACK;
	protected static String AGENT;

	/** This is the location where to STORE the downloaded data from network cache. */
	//	protected static String cacheFilePath;
	protected static File cacheDataFile;

	/** Define the fields for the OAuth connections. In ise always the Auth20 but can point to Tranquility (default) or Singularity. */
	//	protected static NeoComOAuth20 neocomAuth20Tranquility;
	//	protected static NeoComOAuth20 neocomAuth20Singularity;
	//	protected static NeoComOAuth20 neocomAuth20;
	// TODO The refresh can be striped from the creation because it is only used at runtime when executing the callbacks.
	protected static Retrofit neocomRetrofitTranquility;
	protected static Retrofit neocomRetrofitSingularity;
	protected static Retrofit neocomRetrofit;
	protected static Retrofit neocomRetrofitNoAuth;
	private static String authorizationURL;

	/**
	 * Response cache using the ESI api cache times to speed up all possible repetitive access. Setting caches at the
	 * lowest level but that may be changed to the Global configuration.
	 */
	protected static final Hashtable<String, Response<?>> okResponseCache = new Hashtable();

	// - S T A T I C   U T I L I T Y   M E T H O D S
	public static String constructCachePointerReference( final GlobalDataManagerCache.ECacheTimes cacheCode
			, final int identifier ) {
		return new StringBuffer("CC:")
				       .append(cacheCode.name())
				       .append(":")
				       .append(Integer.valueOf(identifier).toString())
				       .toString();
	}

	public static String constructCachePointerReference( final GlobalDataManagerCache.ECacheTimes cacheCode
			, final int identifier1
			, final int identifier2 ) {
		return new StringBuffer("CC:")
				       .append(cacheCode.name())
				       .append(":")
				       .append(Integer.valueOf(identifier1).toString())
				       .append(":")
				       .append(Integer.valueOf(identifier2).toString())
				       .toString();
	}

	public static String getAuthorizationUrl4Server( final String server ) {
		if (null == authorizationURL)
			authorizationURL = singleton.getConfiguredOAuth(server).getAuthorizationUrl();
		return authorizationURL;
	}

	public static String getStringScopes() {
		return SCOPESTRING;
	}

	/**
	 * Go to the ESI api to ge the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 */
	public static List<GetMarketsPrices200Ok> getMarketsPrices( final String server ) {
		logger.info(">> [ESINetworkManager.getMarketsPrices]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = neocomRetrofit.create(MarketApi.class)
					                                                                .getMarketsPrices("tranquility", null)
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

	//	private static Retrofit createNeoComRetrofit( final IConfigurationProvider configurationProvider ) {
	//		neocomRetrofit = new NeoComRetrofitHTTP.Builder()
	//				                 .withEsiServerLocation(configurationProvider.getResourceString("P.esi.data.server.location"
	//						                 , "https://esi.evetech.net/latest/"))
	//				                 .withNeoComOAuth20(neocomAuth20)
	//				                 .withAgent(AGENT)
	//				                 .withCacheDataFile(cacheDataFile)
	//				                 .withCacheSize(cacheSize)
	//				                 .withTimeout(timeout)
	//				                 .build();
	//		return neocomRetrofit;
	//	}

	// - F I E L D S
	public IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;

	// - C O N S T R U C T O R S
	public ESINetworkManagerZBase() {}

	protected ESINetworkManagerZBase( final IConfigurationProvider configurationProvider, final IFileSystem fileSystemAdapter ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
	}

	//	public IConfigurationProvider getConfigurationProvider() {
	//		return configurationProvider;
	//	}

	//	public IFileSystem getFileSystemAdapter() {
	//		return fileSystemAdapter;
	//	}

	public void initialise() {
		logger.info(">> [ESIGlobalAdapter.initialize]");
		// Read the configuration and open the ESI requests cache.
		final String cacheFilePath = this.configurationProvider.getResourceString("P.cache.directory.path")
				                             + this.configurationProvider.getResourceString("P.cache.esinetwork.filename");
		cacheDataFile = new File(this.fileSystemAdapter.accessResource4Path(cacheFilePath));
		AGENT = this.configurationProvider.getResourceString("P.esi.authorization.agent", "Default agent");
		// Authenticated retrofits.
		neocomRetrofitTranquility = new NeoComRetrofitHTTP.Builder()
				                            .withNeoComOAuth20(this.getConfiguredOAuth("Tranquility"))
				                            .withEsiServerLocation(this.configurationProvider.getResourceString("P.esi.data.server.location"
						                            , "https://esi.evetech.net/latest/"))
				                            .withAgent(AGENT)
				                            .withCacheDataFile(cacheDataFile)
				                            .withCacheSize(CACHE_SIZE)
				                            .withTimeout(TIMEOUT)
				                            .build();
		neocomRetrofitSingularity = new NeoComRetrofitHTTP.Builder()
				                            .withNeoComOAuth20(this.getConfiguredOAuth("Singularity"))
				                            .withEsiServerLocation(this.configurationProvider.getResourceString("P.esi.data.server.location"
						                            , "https://esi.evetech.net/latest/"))
				                            .withAgent(AGENT)
				                            .withCacheDataFile(cacheDataFile)
				                            .withCacheSize(CACHE_SIZE)
				                            .withTimeout(TIMEOUT)
				                            .build();
		neocomRetrofit = neocomRetrofitTranquility;

		// Other retrofits.
		neocomRetrofitNoAuth = new NeoComRetrofitNoOAuthHTTP.Builder()
				                       .withNeoComOAuth20(this.getConfiguredOAuth("Tranquility"))
				                       .withEsiServerLocation(this.configurationProvider.getResourceString("P.esi.data.server.location"
						                       , "https://esi.evetech.net/latest/"))
				                       .withAgent(AGENT)
				                       .withCacheDataFile(cacheDataFile)
				                       .withCacheSize(CACHE_SIZE)
				                       .withTimeout(TIMEOUT)
				                       .build();
		logger.info("<< [ESIGlobalAdapter.initialize]");
	}

	public NeoComOAuth20 getConfiguredOAuth( final String selector ) {
		Objects.requireNonNull(selector);
		final List<String> scopes = this.constructScopes(this.configurationProvider.getResourceString("P.esi."
				                                                                                              + selector.toLowerCase() + ".authorization.scopes.filename"));
		NeoComOAuth20 auth = null;
		if ("TRANQUILITY".equalsIgnoreCase(selector)) {
			final String CLIENT_ID = this.configurationProvider.getResourceString("P.esi.tranquility.authorization.clientid");
			final String SECRET_KEY = this.configurationProvider.getResourceString("P.esi.tranquility.authorization.secretkey");
			final String CALLBACK = this.configurationProvider.getResourceString("P.esi.tranquility.authorization.callback");
			final String AGENT = this.configurationProvider.getResourceString("P.esi.authorization.agent", "Default agent");
			// Verify that the constants have values. Otherwise launch exception.
			if (CLIENT_ID.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			if (SECRET_KEY.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			if (CALLBACK.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			auth = new NeoComOAuth20.Builder()
					       .withClientId(CLIENT_ID)
					       .withClientKey(SECRET_KEY)
					       .withCallback(CALLBACK)
					       .withAgent(AGENT)
					       .withStore(STORE)
					       .withScopes(scopes)
					       .withState("NEOCOM-VERIFICATION-STATE")
					       .withBaseUrl(this.configurationProvider.getResourceString("P.esi.tranquility.authorization.server"
							       , "https://login.eveonline.com/"))
					       .withAccessTokenEndpoint(this.configurationProvider.getResourceString("P.esi.authorization.accesstoken.url"
							       , "oauth/token"))
					       .withAuthorizationBaseUrl(this.configurationProvider.getResourceString("P.esi.authorization.authorize.url"
							       , "oauth/authorize"))
					       .build();
			// TODO - When new refactoring isolates scopes remove this.
			SCOPESTRING = this.transformScopes(scopes);
		}
		if ("SINGULARITY".equalsIgnoreCase(selector)) {
			final String CLIENT_ID = this.configurationProvider.getResourceString("P.esi.singularity.authorization.clientid");
			final String SECRET_KEY = this.configurationProvider.getResourceString("P.esi.singularity.authorization.secretkey");
			final String CALLBACK = this.configurationProvider.getResourceString("P.esi.singularity.authorization.callback");
			final String AGENT = this.configurationProvider.getResourceString("P.esi.authorization.agent", "Default agent");
			// Verify that the constants have values. Otherwise launch exception.
			if (CLIENT_ID.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			if (SECRET_KEY.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			if (CALLBACK.isEmpty())
				throw new NeoComRuntimeException("RT [ESIGlobalAdapter.initialize]> ESI configuration property is empty.");
			auth = new NeoComOAuth20.Builder()
					       .withClientId(CLIENT_ID)
					       .withClientKey(SECRET_KEY)
					       .withCallback(CALLBACK)
					       .withAgent(AGENT)
					       .withStore(STORE)
					       .withScopes(scopes)
					       .withState(this.configurationProvider.getResourceString("P.esi.authorization.state"
							       , "NEOCOM-VERIFICATION-STATE"))
					       .withBaseUrl(this.configurationProvider.getResourceString("P.esi.singularity.authorization.server"
							       , "https://sisilogin.testeveonline.com/"))
					       .withAccessTokenEndpoint(this.configurationProvider.getResourceString("P.esi.authorization.accesstoken.url"
							       , "oauth/token"))
					       .withAuthorizationBaseUrl(this.configurationProvider.getResourceString("P.esi.authorization.authorize.url"
							       , "oauth/authorize"))
					       .build();
		}
		Objects.requireNonNull(auth);
		return auth;
	}

	private List<String> constructScopes( final String propertyFileName ) {
		final List<String> SCOPES = new ArrayList<>();
		SCOPES.add("publicData");
		try {
			final InputStream istream = this.fileSystemAdapter.openAsset4Input(propertyFileName);
			final BufferedReader input = new BufferedReader(new InputStreamReader(istream));
			String line = input.readLine();
			while (StringUtils.isNotEmpty(line)) {
				SCOPES.add(line);
				line = input.readLine();
			}
		} catch (FileNotFoundException fnfe) {
			logger.info("EX [ESIGlobalAdapter.constructScopes]> FileNotFoundException: {}", fnfe.getMessage());
			return SCOPES;
		} catch (IOException ioe) {
			logger.info("EX [ESIGlobalAdapter.constructScopes]> FileNotFoundException: {}", ioe.getMessage());
			return SCOPES;
		}
		return SCOPES;
	}

	public void activateEsiServer( final String esiServer ) {
		authorizationURL = null;
		if ("TRANQUILITY".equalsIgnoreCase(esiServer)) neocomRetrofit = neocomRetrofitTranquility;
		if ("SINGULARITY".equalsIgnoreCase(esiServer)) neocomRetrofit = neocomRetrofitSingularity;
	}

	private String transformScopes( final List<String> scopeList ) {
		StringBuilder scope = new StringBuilder();
		for (String s : scopeList) {
			scope.append(s);
			scope.append(" ");
		}
		return StringUtils.removeEnd(scope.toString(), " ");
	}
}
