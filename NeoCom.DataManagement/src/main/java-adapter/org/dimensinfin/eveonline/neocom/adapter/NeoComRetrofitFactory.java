package org.dimensinfin.eveonline.neocom.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;
import org.dimensinfin.eveonline.neocom.auth.mock.NeoComRetrofitMock;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

import retrofit2.Retrofit;

public class NeoComRetrofitFactory {
	public static Logger logger = LoggerFactory.getLogger( NeoComRetrofitFactory.class );
	private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10G of storage space for the ESI downloaded data.
	private static final List<String> mockList = new ArrayList<>(); // List of ESI methods mocked dynamically.
	private static String activatedServer;
	private static String authorizationURL;
	private static String SCOPESTRING = "publicData";

	// - M O C K   L I S T
	public static void add2MockList( final String methodName ) {
		mockList.add( methodName );
	}

	public static void remove4MockList( final String methodName ) {
		mockList.remove( methodName );
	}

	public static void clearMockList() {
		mockList.clear();
	}

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;

	protected Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.
	private Retrofit neocomRetrofitESIAuthorization; // HTTP client to be used to access ESI authenticated endpoints.
	private Retrofit neocomRetrofitMountebank; // HTTP client for mocked endpoints.

	// - C O N S T R U C T O R S
	protected NeoComRetrofitFactory() { }

	public void activateEsiServer( final String esiServer ) {
		authorizationURL = null;
		activatedServer = esiServer;
		neocomRetrofitESIAuthorization = this.generateESIAuthRetrofit( esiServer );
	}

	public String getAuthorizationUrl4Server( final String server ) {
		if (null == authorizationURL)
			authorizationURL = this.getConfiguredOAuth( server ).getAuthorizationUrl();
		return authorizationURL;
	}

	public Retrofit accessNoAuthRetrofit() {
		if (null == this.neocomRetrofitNoAuth) this.neocomRetrofitNoAuth = this.generateNoAuthRetrofit();
		return this.neocomRetrofitNoAuth;
	}

	public String getScopes() {
		return SCOPESTRING;
	}

	/**
	 * This is the point where I should check the table of requests to be mocked up. Get the caller method name from the
	 * stack trace and search for it on the mock table. If found then use the mock retrofit instead of the authenticated retrofit.
	 *
	 * @return the selected retrofit depending on the mock status.
	 */
	public Retrofit accessESIAuthRetrofit() {
		final String methodName = this.getCallerMethodName();
		if (mockList.contains( methodName )) {
			if (null == this.neocomRetrofitMountebank)
				this.neocomRetrofitMountebank = this.generateMountebankRetrofit();
			return this.neocomRetrofitMountebank;
		} else {
			if (null == this.neocomRetrofitESIAuthorization)
				this.neocomRetrofitESIAuthorization = this.generateESIAuthRetrofit( activatedServer );
			return this.neocomRetrofitESIAuthorization;
		}
	}

	/**
	 * This method should check if running on a device because the name index for the real call changes between Dalvik and
	 * java runs.
	 */
	private String getCallerMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement stackElement = stacktrace[0]; // This is to check if we are using Dalvik
		String methodName = stackElement.getMethodName();
		if (methodName.equalsIgnoreCase( "getThreadStackTrace" )) {
			stackElement = stacktrace[4]; // Using Dalvik. Get a trace one level deeper
			return stackElement.getMethodName();
		} else {
			stackElement = stacktrace[3]; // Using Java or testing. Get a trace at right level
			return stackElement.getMethodName();
		}
	}

	private Retrofit generateMountebankRetrofit() {
		final String agent = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
		final String esiMockServer = this.configurationProvider.getResourceString( "P.esi.authorization.mock.server" );
		return new NeoComRetrofitMock.Builder()
				.withEsiServerLocation( esiMockServer )
				.withAgent( agent )
				.build();
	}

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

	private Retrofit generateESIAuthRetrofit( final String esiServer ) {
		final String agent = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
		final long timeout = TimeUnit.SECONDS
				.toMillis( this.configurationProvider.getResourceInteger( "P.cache.esinetwork.timeout" ) );
		try {
			final String cacheFilePath = this.configurationProvider.getResourceString( "P.cache.directory.path" )
					+ this.configurationProvider.getResourceString( "P.cache.esinetwork.filename" );
			final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
			return new NeoComRetrofitHTTP.Builder()
					.withNeoComOAuth20( this.getConfiguredOAuth( esiServer.toLowerCase() ) )
					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
							, "https://esi.evetech.net/latest/" ) )
					.withAgent( agent )
					.withCacheDataFile( cacheDataFile )
					.withCacheSize( CACHE_SIZE )
					.withTimeout( timeout )
					.build();
		} catch (final IOException ioe) { // If there is an exception with the cache create the retrofit not cached.
			return new NeoComRetrofitHTTP.Builder()
					.withNeoComOAuth20( this.getConfiguredOAuth( esiServer.toLowerCase() ) )
					.withEsiServerLocation( this.configurationProvider.getResourceString( "P.esi.data.server.location"
							, "https://esi.evetech.net/latest/" ) )
					.withAgent( agent )
					.withTimeout( timeout )
					.build();
		}
	}

	protected NeoComOAuth20 getConfiguredOAuth( final String selector ) {
		Objects.requireNonNull( selector );
		final List<String> scopes = this.constructScopes( this.configurationProvider.getResourceString( "P.esi."
				+ selector.toLowerCase() + ".authorization.scopes.filename" ) );
		NeoComOAuth20 auth = null;
		if ("TRANQUILITY".equalsIgnoreCase( selector )) {
			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.clientid" );
			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.secretkey" );
			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.callback" );
			final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
					"Default agent" );
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
					.withState( "NEOCOM-VERIFICATION-STATE" )
					.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.server"
							, "https://login.eveonline.com/" ) )
					.withAccessTokenEndpoint( this.configurationProvider.getResourceString( "P.esi.authorization.accesstoken.url"
							, "oauth/token" ) )
					.withAuthorizationBaseUrl( this.configurationProvider.getResourceString( "P.esi.authorization.authorize.url"
							, "oauth/authorize" ) )
					.build();
			// TODO - When new refactoring isolates scopes remove this.
			SCOPESTRING = this.transformScopes( scopes );
		}
		if ("SINGULARITY".equalsIgnoreCase( selector )) {
			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.clientid" );
			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.secretkey" );
			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.callback" );
			final String AGENT = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
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
					.withState( this.configurationProvider.getResourceString( "P.esi.authorization.state"
							, "NEOCOM-VERIFICATION-STATE" ) )
					.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.singularity.authorization.server"
							, "https://sisilogin.testeveonline.com/" ) )
					.withAccessTokenEndpoint( this.configurationProvider.getResourceString( "P.esi.authorization.accesstoken.url"
							, "oauth/token" ) )
					.withAuthorizationBaseUrl( this.configurationProvider.getResourceString( "P.esi.authorization.authorize.url"
							, "oauth/authorize" ) )
					.build();
		}
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
			logger.info( "EX [NeoComRetrofitFactory.constructScopes]> FileNotFoundException: {}", fnfe.getMessage() );
			return SCOPES;
		} catch (IOException ioe) {
			logger.info( "EX [NeoComRetrofitFactory.constructScopes]> FileNotFoundException: {}", ioe.getMessage() );
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

	/**
	 * There is some problem on the connection. Reset the retrofits to be created again on next use.
	 */
	public void reset() {
		this.neocomRetrofitNoAuth = null;
		this.neocomRetrofitESIAuthorization = null;
		this.neocomRetrofitMountebank = null;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComRetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new NeoComRetrofitFactory();
		}

		public Builder( final NeoComRetrofitFactory preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new NeoComRetrofitFactory();
		}

		public NeoComRetrofitFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public NeoComRetrofitFactory.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public NeoComRetrofitFactory build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			this.onConstruction.activateEsiServer( ESIDataProvider.DEFAULT_ESI_SERVER );
			return this.onConstruction;
		}
	}
}
