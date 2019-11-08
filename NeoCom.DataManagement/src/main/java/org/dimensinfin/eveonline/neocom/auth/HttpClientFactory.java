package org.dimensinfin.eveonline.neocom.auth;

import java.util.Base64;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.auth.RetrofitFactory.GSON_CONVERTER_FACTORY;

public class HttpClientFactory {
	private static final String ESI_HOST = "login.eveonline.com";
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private Credential credential;
	private PostRefreshAccessToken refreshService;
	private OkHttpClient httpClient;

	private HttpClientFactory() {}

	private OkHttpClient clientBuilder() {
		final String esiDataServerLocation = this.configurationProvider.getResourceString(
				"P.esi.api.data.server.location",
				"https://esi.evetech.net/latest/" );
		final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
				"Dimensinfin Industries 2019 : NeoCom : Spring Boot 2.x backend client : Production" );
		final String authorizationClientid = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.clientid" );
		final String authorizationSecretKey = this.configurationProvider.getResourceString(
				"P.esi.tranquility.authorization.secretkey" );
		this.refreshService = new Retrofit.Builder()
				.baseUrl( esiDataServerLocation )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.build()
				.create( PostRefreshAccessToken.class );
		this.httpClient = new OkHttpClient.Builder()
				.addInterceptor( chain -> { // Add the headers
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
					if (response.body().string().contains( "expired" )) { // Current token expired. Get a fresh one.
						final String peckString = authorizationClientid + ":" + authorizationSecretKey;
						String peck = Base64.getEncoder().encodeToString( peckString.getBytes() )
								.replaceAll( "\n", "" );
						final retrofit2.Response<TokenTranslationResponse> refreshResponse = this.refreshService
								.getNewAccessToken( ESIDataProvider.DEFAULT_CONTENT_TYPE,
										ESI_HOST, "Basic " + peck, credential.getRefreshToken() )
								.execute();
						if (refreshResponse.isSuccessful()) { // Retry the connection with the new token
							this.credential.setAccessToken( refreshResponse.body().getAccessToken() );
							Request.Builder requestBuilder = chain.request().newBuilder()
									.removeHeader( "Authorization" )
//									.addHeader( "accept", "application/json" )
//									.addHeader( "User-Agent", AGENT )
									.addHeader( "Authorization", "Bearer " + this.credential.getAccessToken() );
							return chain.proceed( requestBuilder.build() );
						}
					}
					return response;
				} )
				.build();
		return this.httpClient;
	}

	// - B U I L D E R
	public static class Builder {
		private HttpClientFactory onConstruction;

		public Builder() {
			this.onConstruction = new HttpClientFactory();
		}

		public HttpClientFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public HttpClientFactory.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public OkHttpClient generate() {
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction.clientBuilder();
		}
	}
}