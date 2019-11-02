package org.dimensinfin.eveonline.neocom.adapter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.core.domain.Units;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

import retrofit2.Retrofit;

public class RetrofitUniverseConnector {
	private static final int CACHE_SIZE = 4 * 1024 * 1024; // 4G of storage space for the ESI universe downloaded data.
	// -  C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;

	private Retrofit noAuthRetrofitConnector; // HTTP client to be used on not authenticated endpoints.

	private RetrofitUniverseConnector() {}

	public void clear() {
		this.noAuthRetrofitConnector = null;
	}

	public Retrofit getRetrofit() {
		if (null == this.noAuthRetrofitConnector)
			this.noAuthRetrofitConnector = this.generateNoAuthRetrofit();
		return this.noAuthRetrofitConnector;
	}

	private Retrofit generateNoAuthRetrofit() {
		try {
			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
					+ this.configurationProvider.getResourceString( "P.cache.retrofit.universe.network" );
			final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
			final String agent = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
			final long timeout = TimeUnit.SECONDS
					.toMillis( this.configurationProvider.getResourceInteger( "P.cache.retrofit.universe.timeout" ) );
			final long cacheSize = this.configurationProvider.getResourceInteger(
					"P.cache.retrofit.universe.size.gb", CACHE_SIZE
			) * Units.GIGABYTES;
			return new NeoComRetrofitNoOAuthHTTP.Builder()
					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
							, "https://esi.evetech.net/latest/" ) )
					.withAgent( agent )
					.withCacheDataFile( cacheDataFile )
					.withCacheSize( cacheSize )
					.withTimeout( timeout )
					.build();
		} catch (final IOException ioe) { // If there is an exception with the cache create the retrofit not cached.
			final String agent = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
			final long timeout = TimeUnit.SECONDS
					.toMillis( this.configurationProvider.getResourceInteger( "P.cache.esiitem.timeout" ) );
			return new NeoComRetrofitNoOAuthHTTP.Builder()
					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
							, "https://esi.evetech.net/latest/" ) )
					.withAgent( agent )
					.withTimeout( timeout )
					.build();
		}
	}

	// - B U I L D E R
	public static class Builder {
		private RetrofitUniverseConnector onConstruction;

		public Builder() {
			this.onConstruction = new RetrofitUniverseConnector();
		}

		public RetrofitUniverseConnector.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public RetrofitUniverseConnector.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public RetrofitUniverseConnector build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			return this.onConstruction;
		}
	}
}