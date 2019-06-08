package org.dimensinfin.eveonline.neocom.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Retrofit;

public class NeoComRetrofitFactory {
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	private static IConfigurationProvider configurationProvider;
	private static IFileSystem fileSystemAdapter;
	public static final long CACHE_SIZE = 10 * 1024 * 1024; // 10G of storage space for the ESI downloaded data.
	public static long TIMEOUT;
	protected static File cacheDataFile;
	protected static String AGENT;

	//	private NeoComRetrofitFactory( final IConfigurationProvider configurationProvider, final IFileSystem fileSystemAdapter ) {
	//		configurationProvider = configurationProvider;
	//		fileSystemAdapter = fileSystemAdapter;
	//	}

	private NeoComRetrofitFactory() {
	}

	public static Retrofit generateNoAuthRetrofit() {
		// Read the configuration and open the ESI requests cache.
		final String cacheFilePath = configurationProvider.getResourceString("P.cache.directory.path")
				                             + configurationProvider.getResourceString("P.cache.esinetwork.filename");
		cacheDataFile = new File(fileSystemAdapter.accessResource4Path(cacheFilePath));
		AGENT = configurationProvider.getResourceString("P.esi.authorization.agent", "Default agent");
		TIMEOUT = TimeUnit.SECONDS.toMillis(configurationProvider.getResourceInteger("P.cache.esiitem.time"));
		final Retrofit neocomRetrofitNoAuth = new NeoComRetrofitNoOAuthHTTP.Builder()
				                                      //				                                      .withNeoComOAuth20(this.getConfiguredOAuth("Tranquility"))
				                                      .withEsiServerLocation(configurationProvider.getResourceString("P.esi.data.server.location"
						                                      , "https://esi.evetech.net/latest/"))
				                                      .withAgent(AGENT)
				                                      .withCacheDataFile(cacheDataFile)
				                                      .withCacheSize(CACHE_SIZE)
				                                      .withTimeout(TIMEOUT)
				                                      .build();
		return neocomRetrofitNoAuth;
	}

	// - B U I L D E R
	public static class Builder {
		protected NeoComRetrofitFactory onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as possible.
		 */
		public Builder( final IConfigurationProvider newConfigurationProvider, final IFileSystem newFileSystemAdapter ) {
			Objects.requireNonNull(newConfigurationProvider);
			Objects.requireNonNull(newFileSystemAdapter);
			configurationProvider = newConfigurationProvider;
			fileSystemAdapter = newFileSystemAdapter;
			this.onConstruction = new NeoComRetrofitFactory(/*configurationProvider, fileSystemAdapter*/);
		}

		public NeoComRetrofitFactory build() throws IOException {
			//			this.onConstruction.createStore(); // Run the initialisation code.
			//			singleton = this.onConstruction;
			return this.onConstruction;
		}
	}
}
