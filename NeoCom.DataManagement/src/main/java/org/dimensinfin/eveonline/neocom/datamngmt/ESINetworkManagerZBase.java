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

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
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
 * This class downloads the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 *
 * @author Adam Antinoo
 */
public class ESINetworkManagerZBase {
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	protected static final ESIStore STORE = ESIStore.DEFAULT;
	protected static final List<String> SCOPES = new ArrayList<>(2);
	protected static String SCOPESTRING = "publicData";

	//	public static void initialize() {
	//		logger.info(">> [ESINetworkManager.initialize]");
	//		// Read the configuration and open the ESI requests cache.
	//		cacheDataFile = new File(GlobalDataManager.accessResource4Path(cacheFilePath));
	//		// Read the scoped from a resource file
	//		constructScopes();
	//
	//		// Initialize global constants from configuration files.
	//		CLIENT_ID = GlobalDataManager.getResourceString("R.esi.authorization.clientid");
	//		SECRET_KEY = GlobalDataManager.getResourceString("R.esi.authorization.secretkey");
	//		CALLBACK = GlobalDataManager.getResourceString("R.esi.authorization.callback");
	//		AGENT = GlobalDataManager.getResourceString("R.esi.authorization.agent");
	//		// Verify that the constants have values. Otherwise launch exception.
	//		if (CLIENT_ID.isEmpty())
	//			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
	//		if (SECRET_KEY.isEmpty())
	//			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
	//		if (CALLBACK.isEmpty())
	//			throw new NeoComRuntimeException("RT [ESINetworkManager.initialize]> ESI configuration property is empty.");
	//		neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
	//		neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
	//		logger.info("<< [ESINetworkManager.initialize]");
	//	}

	protected static String CLIENT_ID;
	protected static String SECRET_KEY;
	protected static String CALLBACK;
	protected static String AGENT;

	/** This is the location where to STORE the downloaded data from network cache. */
	protected static String cacheFilePath;
	protected static File cacheDataFile;
	protected static final long cacheSize = 10 * 1024 * 1024;
	protected static final long timeout = TimeUnit.SECONDS.toMillis(60);

	protected static NeoComOAuth20 neocomAuth20;
	// TODO The refresh can be striped from the creation because it is only used at runtime when executing the callbacks.
	protected static Retrofit neocomRetrofit;

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

	public static String getAuthorizationUrl() {
		return neocomAuth20.getAuthorizationUrl();
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

	private static Retrofit createNeoComRetrofit( final IConfigurationProvider configurationProvider ) {
		neocomRetrofit = new NeoComRetrofitHTTP.Builder(configurationProvider)
				                 .withNeoComOAuth20(neocomAuth20)
				                 .withAgent(AGENT)
				                 .withCacheDataFile(cacheDataFile)
				                 .withCacheSize(cacheSize)
				                 .withTimeout(timeout)
				                 .build();
		return neocomRetrofit;
	}

	// - F I E L D S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;

	// - C O N S T R U C T O R S
	public ESINetworkManagerZBase() {}

	protected ESINetworkManagerZBase( final IConfigurationProvider configurationProvider, final IFileSystem fileSystemAdapter ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
	}

	public IConfigurationProvider getConfigurationProvider() {
		return configurationProvider;
	}

	public IFileSystem getFileSystemAdapter() {
		return fileSystemAdapter;
	}

	private void initialise() {
		logger.info(">> [ESIAdapter.initialize]");
		// Read the configuration and open the ESI requests cache.
		cacheFilePath = this.configurationProvider.getResourceString("R.cache.directorypath")
				                + this.configurationProvider.getResourceString("R.cache.esinetwork.filename");
		cacheDataFile = new File(this.fileSystemAdapter.accessResource4Path(cacheFilePath));
		// Read the scoped from a resource file
		this.constructScopes();

		// Initialize global constants from configuration files.
		CLIENT_ID = this.configurationProvider.getResourceString("R.esi.authorization.clientid");
		SECRET_KEY = this.configurationProvider.getResourceString("R.esi.authorization.secretkey");
		CALLBACK = this.configurationProvider.getResourceString("R.esi.authorization.callback");
		AGENT = this.configurationProvider.getResourceString("R.esi.authorization.agent");
		// Verify that the constants have values. Otherwise launch exception.
		if (CLIENT_ID.isEmpty())
			throw new NeoComRuntimeException("RT [ESIAdapter.initialize]> ESI configuration property is empty.");
		if (SECRET_KEY.isEmpty())
			throw new NeoComRuntimeException("RT [ESIAdapter.initialize]> ESI configuration property is empty.");
		if (CALLBACK.isEmpty())
			throw new NeoComRuntimeException("RT [ESIAdapter.initialize]> ESI configuration property is empty.");
		neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
		neocomRetrofit = new NeoComRetrofitHTTP.Builder(this.configurationProvider)
				                 .withNeoComOAuth20(neocomAuth20)
				                 .withAgent(AGENT)
				                 .withCacheDataFile(cacheDataFile)
				                 .withCacheSize(cacheSize)
				                 .withTimeout(timeout)
				                 .build();
		logger.info("<< [ESIAdapter.initialize]");
	}

	private List<String> constructScopes() {
		try {
			final String propertyFileName = this.configurationProvider.getResourceString("R.esi.authorization.scopes.filename");
			//			final ClassLoader classLoader = ESINetworkManager.class.getClassLoader();
			//			final URI propertyURI = new URI(classLoader.getResource(propertyFileName).toString());
			//			final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(propertyURI.getPath())));
			final InputStream istream = this.fileSystemAdapter.openAsset4Input(propertyFileName);
			final BufferedReader input = new BufferedReader(new InputStreamReader(istream));
			String line = input.readLine();
			while (StringUtils.isNotEmpty(line)) {
				SCOPES.add(line);
				line = input.readLine();
			}

			// Convert the scopes to a single string.
			SCOPESTRING = this.transformScopes(SCOPES);
			//		} catch (URISyntaxException e) {
			//			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SCOPES;
	}

	private String transformScopes( final List<String> scopeList ) {
		StringBuilder scope = new StringBuilder();
		for (String s : scopeList) {
			scope.append(s);
			scope.append(" ");
		}
		return StringUtils.removeEnd(scope.toString(), " ");
	}

	// - B U I L D E R
	public static class Builder {
		protected ESINetworkManagerZBase onConstruction;

		public Builder( final IConfigurationProvider configurationProvider, final IFileSystem fileSystemAdapter ) {
			this.onConstruction = new ESINetworkManagerZBase(configurationProvider, fileSystemAdapter);
		}

		public ESINetworkManagerZBase build() {
			// Run the initialisation code.
			this.onConstruction.initialise();
			return this.onConstruction;
		}
	}
}
