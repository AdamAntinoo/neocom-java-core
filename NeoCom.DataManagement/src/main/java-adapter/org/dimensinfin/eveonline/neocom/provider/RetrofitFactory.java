package org.dimensinfin.eveonline.neocom.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.HttpAuthenticatedClientFactory;
import org.dimensinfin.eveonline.neocom.auth.HttpBackendClientFactory;
import org.dimensinfin.eveonline.neocom.auth.HttpUniverseClientFactory;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
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
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.AUTHENTICATED_RETROFIT_SERVER_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.AUTHENTICATED_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.BACKEND_RETROFIT_CACHE_FILE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.CACHE_DIRECTORY_PATH;
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.NEOCOM_BACKEND_SERVER_BASE_URL;
import static org.dimensinfin.eveonline.neocom.provider.AConfigurationProvider.UNIVERSE_RETROFIT_SERVER_AGENT;

public class RetrofitFactory {
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final String UNIVERSE_CONNECTOR_IDENTIFIER = "-UNIVERSE-CONNECTOR-";
	private static final String BACKEND_CONNECTOR_IDENTIFIER = "-BACKEND-CONNECTOR-";
	private static final String DEFAULT_RETROFIT_AGENT = "Default agent";

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	private Map<String, Retrofit> connectors = new HashMap<>();

	// - C O N S T R U C T O R S
	protected RetrofitFactory() {}

	@LogEnterExit
	public Retrofit accessAuthenticatedConnector( final Credential credential ) throws IOException {
		Retrofit hitConnector = this.connectors.get( credential.getUniqueId() );
		if (null == hitConnector) { // Create a new connector for this credential.
			final String esiDataServerLocation = this.configurationProvider.getResourceString(
					AUTHENTICATED_RETROFIT_SERVER_LOCATION,
					"https://esi.evetech.net/latest/" );
			final String agent = this.configurationProvider
					.getResourceString( AUTHENTICATED_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
			final int timeout = (int) TimeUnit.SECONDS
					.toSeconds( this.configurationProvider.getResourceInteger( "P.authenticated.retrofit.server.timeout" ) );
			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
					+ this.configurationProvider.getResourceString( "P.authenticated.retrofit.cache.directory.name" );
			final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
			final Integer cacheSize = this.configurationProvider.getResourceInteger(
					"P.authenticated.retrofit.cache.size.gb" );
			hitConnector = new Retrofit.Builder()
					.baseUrl( esiDataServerLocation )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( new HttpAuthenticatedClientFactory.Builder()
							.withNeoComOAuth20( this.getConfiguredOAuth( credential.getDataSource().toLowerCase() ) )
							.withConfigurationProvider( this.configurationProvider )
							.withCredential( credential )
							.withAgent( agent )
							.withTimeout( timeout )
							.withCacheFile( cacheDataFile )
							.withCacheSize( cacheSize, StorageUnits.GIGABYTES )
							.generate() )
					.build();
			this.connectors.put( credential.getUniqueId(), hitConnector );
		}
		return hitConnector;
	}

	@LogEnterExit
	public Retrofit accessBackendConnector() {
		Retrofit hitConnector = this.connectors.get( BACKEND_CONNECTOR_IDENTIFIER );
		try {
			if (null == hitConnector) { // Create a new connector for the backend and cache it.
				final String serverBaseUrl = this.configurationProvider.getResourceString( NEOCOM_BACKEND_SERVER_BASE_URL,
						HttpBackendClientFactory.DEFAULT_NEOCOM_BACKEND_HOST );
				final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH )
						+ this.configurationProvider.getResourceString( BACKEND_RETROFIT_CACHE_FILE_NAME );
				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
				hitConnector = new Retrofit.Builder()
						.baseUrl( serverBaseUrl )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpBackendClientFactory.Builder()
								.optionalCacheFile( cacheDataFile )
								.generate() )
						.build();
			}
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
		}
		return hitConnector;
	}

	@LogEnterExit
	public Retrofit accessUniverseConnector() {
		Retrofit hitConnector = this.connectors.get( UNIVERSE_CONNECTOR_IDENTIFIER );
		try {
			if (null == hitConnector) { // Create a new connector for this credential.
				final String esiDataServerLocation = this.configurationProvider.getResourceString(
						"P.universe.retrofit.server.location",
						"https://esi.evetech.net/latest/" );
				final String agent = this.configurationProvider.getResourceString( UNIVERSE_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
				final int timeout = (int) TimeUnit.SECONDS
						.toSeconds( this.configurationProvider.getResourceInteger( "P.universe.retrofit.server.timeout" ) );
				final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
						+ this.configurationProvider.getResourceString( "P.universe.retrofit.cache.directory.name" );
				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
				final Integer cacheSize = this.configurationProvider.getResourceInteger(
						"P.universe.retrofit.cache.size.gb", 1 );
				hitConnector = new Retrofit.Builder()
						.baseUrl( esiDataServerLocation )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpUniverseClientFactory.Builder()
								.optionalAgent( agent )
								.optionalTimeout( timeout )
								.optionalCacheFile( cacheDataFile )
								.optionalCacheSize( cacheSize, StorageUnits.GIGABYTES )
								.generate() )
						.build();
				this.connectors.put( UNIVERSE_CONNECTOR_IDENTIFIER, hitConnector );
			}
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
		}
		return hitConnector;
	}

	protected NeoComOAuth20 getConfiguredOAuth( final String selector ) {
		Objects.requireNonNull( selector );
		final List<String> scopes = this.constructScopes( this.configurationProvider.getResourceString( "P.esi."
				+ selector.toLowerCase() + ".authorization.scopes.filename" ) );
		NeoComOAuth20 auth;
		final String SERVER_LOGIN_BASE = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.server.login.base",
				"https://login.eveonline.com/" );
		final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.clientid" );
		final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.secretkey" );
		final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.callback" );
		final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
				DEFAULT_RETROFIT_AGENT );
		final String STATE = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.state" );
		// Verify that the constants have values. Otherwise launch exception.
		if (CLIENT_ID.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if (SECRET_KEY.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if (CALLBACK.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		auth = new NeoComOAuth20.Builder()
				.withClientId( CLIENT_ID )
				.withClientKey( SECRET_KEY )
				.withCallback( CALLBACK )
				.withAgent( AGENT )
				.withStore( ESIStore.DEFAULT )
				.withScopes( scopes )
				.withState( STATE )
				.withBaseUrl( SERVER_LOGIN_BASE )
				.withAccessTokenEndpoint( this.configurationProvider.getResourceString(
						"P.esi.tranquility.authorization.accesstoken.url",
						"oauth/token" ) )
				.withAuthorizationBaseUrl( this.configurationProvider.getResourceString(
						"P.esi.tranquility.authorization.authorize.url",
						"oauth/authorize" ) )
				.build();
		Objects.requireNonNull( auth );
		return auth;
	}

	private List<String> constructScopes( final String propertyFileName ) {
		final List<String> scopes = new ArrayList<>();
		scopes.add( "publicData" );
		try (final InputStream istream = this.fileSystemAdapter.openAsset4Input( propertyFileName );
		     final BufferedReader input = new BufferedReader( new InputStreamReader( istream ) )) {
			String line = input.readLine();
			while (StringUtils.isNotEmpty( line )) {
				scopes.add( line );
				line = input.readLine();
			}
		} catch (IOException ioe) {
			NeoComLogger.error( ioe );
			return scopes;
		}
		return scopes;
	}

	// - B U I L D E R
	public static class Builder {
		private RetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new RetrofitFactory();
		}

		public RetrofitFactory build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			return this.onConstruction;
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
	}
}
