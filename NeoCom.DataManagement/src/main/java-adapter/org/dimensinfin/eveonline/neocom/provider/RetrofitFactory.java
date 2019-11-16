package org.dimensinfin.eveonline.neocom.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.HttpAuthenticatedClientFactory;
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

public class RetrofitFactory {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final String UNIVERSE_CONNECTOR_IDENTIFIER = "-UNIVERSE-CONNECTOR-";

	private Map<String, Retrofit> connectors = new HashMap<>();
	private String SCOPESTRING;
	private Long CACHE_SIZE=StorageUnits.GIGABYTES.toBytes( 2 );
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;

	private RetrofitFactory() {}

	@Deprecated
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
							.withTimeout( timeout.intValue() )
							.withCacheFile( cacheDataFile )
							.withCacheSize( cacheSize, StorageUnits.GIGABYTES )
							.generate() )
					.build();
			this.connectors.put( credential.getUniqueId(), hitConnector );
		}
		return hitConnector;
	}

//	private Retrofit generateESIAuthRetrofit( final String esiServer ) {
//		try {
//			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
//					+ this.configurationProvider.getResourceString( "P.cache.esinetwork.filename" );
//			final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
//			return new NeoComRetrofitHTTP.Builder()
//					.withNeoComOAuth20( this.getConfiguredOAuth( esiServer.toLowerCase() ) )
//					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
//							, "https://esi.evetech.net/latest/" ) )
//					.withAgent( agent )
//					.withCacheDataFile( cacheDataFile )
//					.withCacheSize( CACHE_SIZE )
//					.withTimeout( timeout )
//					.build();
//		} catch (final IOException ioe) { // If there is an exception with the cache create the retrofit not cached.
//			return new NeoComRetrofitHTTP.Builder()
//					.withNeoComOAuth20( this.getConfiguredOAuth( esiServer.toLowerCase() ) )
//					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
//							, "https://esi.evetech.net/latest/" ) )
//					.withAgent( agent )
//					.withTimeout( timeout )
//					.build();
//		}
//	}

	protected NeoComOAuth20 getConfiguredOAuth( final String selector ) {
		Objects.requireNonNull( selector );
		final List<String> scopes = this.constructScopes( this.configurationProvider.getResourceString( "P.esi."
				+ selector.toLowerCase() + ".authorization.scopes.filename" ) );
		NeoComOAuth20 auth = null;
//		if ("TRANQUILITY".equalsIgnoreCase( selector )) {
			final String SERVER_LOGIN_BASE = this.configurationProvider.getResourceString(
					"P.esi.tranquility.authorization.server.login.base",
					"https://login.eveonline.com/");
			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.clientid" );
			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.secretkey" );
			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.callback" );
			final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
					"Default agent" );
			final String STATE=this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.state" );
			// Verify that the constants have values. Otherwise launch exception.
			if (CLIENT_ID.isEmpty())
				throw new NeoComRuntimeException(
						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
			if (SECRET_KEY.isEmpty())
				throw new NeoComRuntimeException(
						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
			if (CALLBACK.isEmpty())
				throw new NeoComRuntimeException(
						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
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
			// TODO - When new refactoring isolates scopes remove this.
//			SCOPESTRING = this.transformScopes( scopes );
//		}
//		if ("SINGULARITY".equalsIgnoreCase( selector )) {
//			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.clientid" );
//			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.secretkey" );
//			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.callback" );
//			final String AGENT = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
//			// Verify that the constants have values. Otherwise launch exception.
//			if (CLIENT_ID.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (SECRET_KEY.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (CALLBACK.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			auth = new NeoComOAuth20.Builder()
//					.withClientId( CLIENT_ID )
//					.withClientKey( SECRET_KEY )
//					.withCallback( CALLBACK )
//					.withAgent( AGENT )
//					.withStore( ESIStore.DEFAULT )
//					.withScopes( scopes )
//					.withState( this.configurationProvider.getResourceString( "P.esi.authorization.state"
//							, "NEOCOM-VERIFICATION-STATE" ) )
//					.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.singularity.authorization.server"
//							, "https://sisilogin.testeveonline.com/" ) )
//					.withAccessTokenEndpoint( this.configurationProvider.getResourceString( "P.esi.authorization.accesstoken.url"
//							, "oauth/token" ) )
//					.withAuthorizationBaseUrl( this.configurationProvider.getResourceString( "P.esi.authorization.authorize.url"
//							, "oauth/authorize" ) )
//					.build();
//		}
		Objects.requireNonNull( auth );
		return auth;
	}

	private List<String> constructScopes( final String propertyFileName ) {
		final List<String> SCOPES = new ArrayList<>();
		SCOPES.add( "publicData" );
		try {
			final InputStream istream = this.fileSystemAdapter.openAsset4Input( propertyFileName );
			final BufferedReader input = new BufferedReader( new InputStreamReader( istream ) );
			String line = input.readLine();
			while (StringUtils.isNotEmpty( line )) {
				SCOPES.add( line );
				line = input.readLine();
			}
		} catch (FileNotFoundException fnfe) {
			NeoComLogger.info( "EX [NeoComRetrofitFactory.constructScopes]> FileNotFoundException: {}", fnfe.getMessage() );
			return SCOPES;
		} catch (IOException ioe) {
			NeoComLogger.info( "EX [NeoComRetrofitFactory.constructScopes]> FileNotFoundException: {}", ioe.getMessage() );
			return SCOPES;
		}
		return SCOPES;
	}

	private String transformScopes( final List<String> scopeList ) {
		StringBuilder scope = new StringBuilder();
		for (String s : scopeList) {
			scope.append( s );
			scope.append( " " );
		}
		return StringUtils.removeEnd( scope.toString(), " " );
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
