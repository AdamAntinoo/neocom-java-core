package org.dimensinfin.eveonline.neocom.provider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.domain.Units;
import org.dimensinfin.eveonline.neocom.adapters.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapters.IFileSystem;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

import retrofit2.Response;
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter.DEFAULT_ESI_SERVER;

public class ESIUniverseDataProvider {
	private static final Logger logger = LoggerFactory.getLogger( ESIUniverseDataProvider.class );
	private static final long CACHE_SIZE = 2 * Units.GIGABYTES;

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
//	protected LocationCatalogService locationCatalogService;
////	protected NeoComRetrofitFactory retrofitFactory;
//	protected StoreCacheManager cacheManager;

	protected Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.

	private ESIUniverseDataProvider() {}

	private Retrofit generateNoAuthRetrofit() {
		try {
			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
					+ this.configurationProvider.getResourceString( "P.cache.esinetwork.filename" );
			final File cacheDataFile = new File( fileSystemAdapter.accessResource4Path( cacheFilePath ) );
			final String agent = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
			final long timeout = TimeUnit.SECONDS
					.toMillis( this.configurationProvider.getResourceInteger( "P.cache.esinetwork.timeout" ) );
			return new NeoComRetrofitNoOAuthHTTP.Builder()
					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
							, "https://esi.evetech.net/latest/" ) )
					.withAgent( agent )
					.withCacheDataFile( cacheDataFile )
					.withCacheSize( CACHE_SIZE )
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

	// - P R O V I D E R   A P I
	public GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSystemsSystemIdOk> systemResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseSystemsSystemId( systemId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			logger.info( "EX [ESIUniverseDataProvider.getUniverseSystemById]> IOException during ESI data access: {}",
					ioe.getMessage() );
		}
		return null;
	}

	public GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseConstellationsConstellationIdOk> systemResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseConstellationsConstellationId( constellationId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			logger.info( "EX [ESIUniverseDataProvider.getUniverseConstellationById]> IOException during ESI data access: {}",
					ioe.getMessage() );
		}
		return null;
	}

	public GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseRegionsRegionIdOk> systemResponse = this.neocomRetrofitNoAuth
					.create( UniverseApi.class )
					.getUniverseRegionsRegionId( regionId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			logger.info( "EX [ESIUniverseDataProvider.getUniverseRegionById]> IOException during ESI data access: {}",
					ioe.getMessage() );
		}
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private ESIUniverseDataProvider onConstruction;

		public Builder() {
			this.onConstruction = new ESIUniverseDataProvider();
		}

		public ESIUniverseDataProvider.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public ESIUniverseDataProvider.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public ESIUniverseDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
//			Objects.requireNonNull( this.onConstruction.locationCatalogService );
//			this.onConstruction.cacheManager = new StoreCacheManager.Builder()
//					                                   .withEsiDataAdapter( this.onConstruction )
//					                                   .withConfigurationProvider( this.onConstruction.configurationProvider )
//					                                   .withFileSystem( this.onConstruction.fileSystemAdapter )
//					                                   .build();
//			Objects.requireNonNull( this.onConstruction.cacheManager );
			return this.onConstruction;
		}
	}
}