package org.dimensinfin.eveonline.neocom.provider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.auth.HttpAuthenticatedClientFactory;
import org.dimensinfin.eveonline.neocom.auth.HttpUniverseClientFactory;
import org.dimensinfin.eveonline.neocom.core.StorageUnits;
import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final String UNIVERSE_CONNECTOR_IDENTIFIER = "-UNIVERSE-CONNECTOR-";

	private Map<String, Retrofit> connectors = new HashMap<>();
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;

	private RetrofitFactory() {}

	public Retrofit accessAuthenticatedConnector( final Credential credential ) throws IOException {
		Retrofit hitConnector = this.connectors.get( credential.getUniqueId() );
		if (null == hitConnector) { // Create a new connector for this credential.
			final String esiDataServerLocation = this.configurationProvider.getResourceString(
					"P.authenticated.retrofit.server.location",
					"https://esi.evetech.net/latest/" );
			final String agent = this.configurationProvider
					.getResourceString( "P.authenticated.retrofit.server.agent", "Default agent" );
			final Long timeout = TimeUnit.SECONDS
					.toSeconds( this.configurationProvider.getResourceInteger( "P.authenticated.retrofit.server.timeout" ) );
			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
					+ this.configurationProvider.getResourceString( "P.authenticated.retrofit.cache.directory.name" );
			final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
			final long cacheSize = this.configurationProvider.getResourceInteger(
					"P.authenticated.retrofit.cache.size.gb" );
			hitConnector = new Retrofit.Builder()
					.baseUrl( esiDataServerLocation )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( new HttpAuthenticatedClientFactory.Builder()
							.withConfigurationProvider( this.configurationProvider )
							.withCredential( credential )
							.withAgent( agent )
							.withTimeout( timeout.intValue() )
							.withCacheFile( cacheDataFile )
							.withCacheSize( 2, StorageUnits.GIGABYTES )
							.generate() )
					.build();
			this.connectors.put( credential.getUniqueId(), hitConnector );
		}
		return hitConnector;
	}

	public Retrofit accessUniverseConnector() {
		NeoComLogger.enter();
		Retrofit hitConnector = this.connectors.get( UNIVERSE_CONNECTOR_IDENTIFIER );
		try {
			if (null == hitConnector) { // Create a new connector for this credential.
				final String esiDataServerLocation = this.configurationProvider.getResourceString(
						"P.universe.retrofit.server.location",
						"https://esi.evetech.net/latest/" );
				final String agent = this.configurationProvider
						.getResourceString( "P.universe.retrofit.server.agent", "Default agent" );
				final Long timeout = TimeUnit.SECONDS
						.toSeconds( this.configurationProvider.getResourceInteger( "P.universe.retrofit.server.timeout" ) );
				final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
						+ this.configurationProvider.getResourceString( "P.universe.retrofit.cache.directory.name" );
				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
				final Integer cacheSize = this.configurationProvider.getResourceInteger(
						"P.universe.retrofit.cache.size.gb" );
				hitConnector = new Retrofit.Builder()
						.baseUrl( esiDataServerLocation )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpUniverseClientFactory.Builder()
								.withConfigurationProvider( this.configurationProvider )
								.withAgent( agent )
								.withTimeout( timeout.intValue() )
								.withCacheFile( cacheDataFile )
								.withCacheSize( cacheSize, StorageUnits.GIGABYTES )
								.generate() )
						.build();
				this.connectors.put( UNIVERSE_CONNECTOR_IDENTIFIER, hitConnector );
			}
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
		}
		NeoComLogger.exit();
		return hitConnector;
	}

	// - B U I L D E R
	public static class Builder {
		private RetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new RetrofitFactory();
		}

		public RetrofitFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public RetrofitFactory.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public RetrofitFactory build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			return this.onConstruction;
		}
	}
}
