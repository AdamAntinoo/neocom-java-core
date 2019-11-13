package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.core.StorageUnits;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.utility.Base64;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.RetrofitFactory.GSON_CONVERTER_FACTORY;

public class HttpAuthenticatedClientFactory {
	private static final String ESI_HOST = "login.eveonline.com";

	private String agent = "NeoCom Data Management Library Agent.";
	private Integer timeoutSeconds = 60;
	private File cacheStoreFile;
	private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes( 2 );
	private Retrofit refreshRetrofit;
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private Credential credential;

	private HttpAuthenticatedClientFactory() {}

	private OkHttpClient clientBuilder() {
		final String esiDataServerLocation = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.server",
				"https://login.eveonline.com/" );
		final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
				"Dimensinfin Industries 2019 : NeoCom : Spring Boot 2.x backend client : Production" );
		final String authorizationClientid = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.clientid" );
		final String authorizationSecretKey = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.secretkey" );
		final String peckString = authorizationClientid + ":" + authorizationSecretKey;
		final String peck = Base64.encodeBytes( peckString.getBytes() )
				.replaceAll( "\n", "" );
		this.refreshRetrofit = new Retrofit.Builder()
				.baseUrl( esiDataServerLocation )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.build();
		final OkHttpClient.Builder authenticatedClientBuilder = new OkHttpClient.Builder()
				.addInterceptor( chain -> { // Add the headers
					NeoComLogger.info( "Retrofit processing request. {}", chain.request().toString() );
					Request.Builder requestBuilder = chain.request().newBuilder()
							.addHeader( "accept", "application/json" )
							.addHeader( "User-Agent", AGENT )
							.addHeader( "Authorization", "Bearer " + this.credential.getAccessToken() );
					return chain.proceed( requestBuilder.build() );
				} )
				.addInterceptor( chain -> { // Check for the response. If token expired replace it
					Response response = chain.proceed( chain.request() );
					if (response.isSuccessful()) {
						return response;
					}
					if ((response.code() == 403) ||
							(response.body().string().contains( "expired" ))) { // Current token expired. Get a fresh one.
						NeoComLogger.info( "Authenticated request failed. {}", response.body().string() );
						NeoComLogger.info( "Response code: {} ", response.code() + "" );
						NeoComLogger.info( "Refresh token: {} ", credential.getRefreshToken() );
						final TokenRefreshBody tokenRefreshBody = new TokenRefreshBody()
								.setRefreshToken( this.credential.getAccessToken() );
						final retrofit2.Response<TokenTranslationResponse> refreshResponse = this.refreshRetrofit
								.create( PostRefreshAccessToken.class )
								.getNewAccessToken( ESIDataProvider.DEFAULT_CONTENT_TYPE,
										ESI_HOST,
										"Basic " + peck,
										authorizationClientid,
										credential.getRefreshToken(),
										tokenRefreshBody )
								.execute();
						if (refreshResponse.isSuccessful()) { // Retry the connection with the new token
							final TokenTranslationResponse refreshData = refreshResponse.body();
							NeoComLogger.info( "Refresh data. {}", refreshData.toString() );
							this.credential.setAccessToken( refreshData.getAccessToken() );
							Request.Builder requestBuilder = chain.request().newBuilder()
									.removeHeader( "Authorization" )
									.addHeader( "Authorization", "Bearer " + this.credential.getAccessToken() );
							return chain.proceed( requestBuilder.build() );
						} else {
							if (null != refreshResponse.errorBody())
								NeoComLogger.info( "Refresh operation failure. {}", refreshResponse.errorBody().string() );
						}
					}
					return response;
				} )
				.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS )
				.certificatePinner(
						new CertificatePinner.Builder()
								.add( "login.eveonline.com", "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
								.add( "login.eveonline.com", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
								.add( "login.eveonline.com", "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
								.build() );
		// Additional characteristics
		if (null != this.cacheStoreFile)
			authenticatedClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return authenticatedClientBuilder.build();
	}

	// - B U I L D E R
	public static class Builder {
		private HttpAuthenticatedClientFactory onConstruction;

		public Builder() {
			this.onConstruction = new HttpAuthenticatedClientFactory();
		}

		public HttpAuthenticatedClientFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withAgent( final String agent ) {
			Objects.requireNonNull( agent );
			this.onConstruction.agent = agent;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCacheFile( final File cacheLocation ) {
			Objects.requireNonNull( cacheLocation );
			this.onConstruction.cacheStoreFile = cacheLocation;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCacheSize( final Integer size, final StorageUnits unit ) {
			Objects.requireNonNull( size );
			this.onConstruction.cacheSizeBytes = unit.toBytes( size );
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withTimeout( final Integer seconds ) {
			if (null != seconds)
				this.onConstruction.timeoutSeconds = seconds;
			return this;
		}

		public OkHttpClient generate() {
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction.clientBuilder();
		}
	}
}
