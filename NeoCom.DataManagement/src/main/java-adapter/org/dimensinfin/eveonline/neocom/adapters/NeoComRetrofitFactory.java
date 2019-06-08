package org.dimensinfin.eveonline.neocom.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Retrofit;

public class NeoComRetrofitFactory {
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	public static final long CACHE_SIZE = 10 * 1024 * 1024; // 10G of storage space for the ESI downloaded data.

	// - C O M P O N E N T S
	private static IConfigurationProvider configurationProvider;
	private static IFileSystem fileSystemAdapter;

	private Retrofit neocomRetrofitNoAuth;
	private Retrofit neocomRetrofitESIAuthorization;

	private NeoComRetrofitFactory() { }

	public Retrofit accessNoAuthRetrofit() {
		if (null == this.neocomRetrofitNoAuth) this.neocomRetrofitNoAuth = this.generateNoAuthRetrofit();
		return this.neocomRetrofitNoAuth;
	}

	public Retrofit accessESIAuthRetrofit() {
		if (null == this.neocomRetrofitESIAuthorization)
			this.neocomRetrofitESIAuthorization = this.generateESIAuthRetrofit();
		return this.neocomRetrofitESIAuthorization;
	}

	private Retrofit generateNoAuthRetrofit() {
		final String cacheFilePath = configurationProvider.getResourceString("P.cache.directory.path")
				                             + configurationProvider.getResourceString("P.cache.esinetwork.filename");
		final File cacheDataFile = new File(fileSystemAdapter.accessResource4Path(cacheFilePath));
		final String agent = configurationProvider.getResourceString("P.esi.authorization.agent", "Default agent");
		final long timeout = TimeUnit.SECONDS.toMillis(configurationProvider.getResourceInteger("P.cache.esiitem.time"));
		return new NeoComRetrofitNoOAuthHTTP.Builder()
				       //				                                      .withNeoComOAuth20(this.getConfiguredOAuth("Tranquility"))
				       .withEsiServerLocation(configurationProvider.getResourceString("P.esi.data.server.location"
						       , "https://esi.evetech.net/latest/"))
				       .withAgent(agent)
				       .withCacheDataFile(cacheDataFile)
				       .withCacheSize(CACHE_SIZE)
				       .withTimeout(timeout)
				       .build();
	}

	private Retrofit generateESIAuthRetrofit() {
		// TODO - This should be replaced by the code to generate a new authorized retrofit.
		return ESIGlobalAdapter.neocomRetrofitTranquility;
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
