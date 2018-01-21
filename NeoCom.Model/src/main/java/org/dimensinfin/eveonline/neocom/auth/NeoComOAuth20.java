//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Adam on 15/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComOAuth20 {
	public static class NeoComAuthApi20 extends DefaultApi20 {

		private static final String AUTHORIZE_URL = "https://login.eveonline.com/oauth/authorize";
		private static final String ACCESS_TOKEN_RESOURCE = "https://login.eveonline.com/oauth/token";

		protected NeoComAuthApi20 () {
		}

		private static class InstanceHolder {
			private static final NeoComAuthApi20 INSTANCE = new NeoComAuthApi20();
		}

		public static NeoComAuthApi20 instance () {
			return NeoComAuthApi20.InstanceHolder.INSTANCE;
		}

		@Override
		public String getAccessTokenEndpoint () {
			return ACCESS_TOKEN_RESOURCE;
		}

		@Override
		protected String getAuthorizationBaseUrl () {
			return AUTHORIZE_URL;
		}
	}

	public interface VerifyCharacter {
		@GET("/oauth/verify")
		Call<VerifyCharacterResponse> getVerification (@Header("Authorization") String token);
	}

	public static class VerifyCharacterResponse {
		@JsonProperty("CharacterID")
		private long characterID;
		@JsonProperty("CharacterName")
		private String characterName;
		@JsonProperty("ExpiresOn")
		private String expiresOn;
		private long expiresMillis;
		@JsonProperty("Scopes")
		private String scopes;
		@JsonProperty("TokenType")
		private String tokenType;
		@JsonProperty("CharacterOwnerHash")
		private String characterOwnerHash;
		@JsonProperty("IntellectualProperty")
		private String intellectualProperty;

		public long getCharacterID () {
			return characterID;
		}

		public void setCharacterID (final long characterID) {
			this.characterID = characterID;
		}

		public String getCharacterName () {
			return characterName;
		}

		public void setCharacterName (final String characterName) {
			this.characterName = characterName;
		}

		public String getExpiresOn () {
			return expiresOn;
		}

		public void setExpiresOn (final String expiresOn) {
			this.expiresOn = expiresOn;
			// Convert the string to a data and then to the date milliseconds.
			//			final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss");
			//			expiresMillis = fmt.parseMillis(expiresOn);
		}

		public long getExpiresMillis () {
			return expiresMillis;
		}

		public void setExpiresMillis (final long expiresInstant) {
			this.expiresMillis = expiresInstant;
		}

		public String getScopes () {
			return scopes;
		}

		public void setScopes (final String scopes) {
			this.scopes = scopes;
		}

		public String getTokenType () {
			return tokenType;
		}

		public void setTokenType (final String tokenType) {
			this.tokenType = tokenType;
		}

		public String getCharacterOwnerHash () {
			return characterOwnerHash;
		}

		public void setCharacterOwnerHash (final String characterOwnerHash) {
			this.characterOwnerHash = characterOwnerHash;
		}

		public String getIntellectualProperty () {
			return intellectualProperty;
		}

		public void setIntellectualProperty (final String intellectualProperty) {
			this.intellectualProperty = intellectualProperty;
		}
	}

	public interface ESIStore {

		ESIStore DEFAULT = new ESIStore() {
			private Map<String, TokenTranslationResponse> map = new HashMap<>();

			@Override
			public void save (TokenTranslationResponse token) {
				this.map.put(token.getRefreshToken(), token);
			}

			@Override
			public void delete (String refresh) {
				this.map.remove(refresh);
			}

			@Override
			public TokenTranslationResponse get (String refresh) {
				return this.map.get(refresh);
			}
		};


		void save (final TokenTranslationResponse token);

		void delete (final String refresh);

		TokenTranslationResponse get (final String refresh);

	}

	public static class TokenTranslationResponse {
		@JsonProperty("access_token")
		public String accessToken;
		@JsonProperty("token_type")
		public String tokenType;
		@JsonProperty("expires_in")
		public long expires;
		@JsonProperty("refresh_token")
		public String refreshToken;
		private final long created = System.currentTimeMillis();
		private String scope;

		public String getAccessToken () {
			return accessToken;
		}

		public TokenTranslationResponse setAccessToken (final String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public String getTokenType () {
			return tokenType;
		}

		public TokenTranslationResponse setTokenType (final String tokenType) {
			this.tokenType = tokenType;
			return this;
		}

		public long getExpires () {
			return expires;
		}

		public long getExpiresOn () {
			return created + (expires * 1000);
		}

		public TokenTranslationResponse setExpires (final long expires) {
			this.expires = expires;
			return this;
		}

		public String getRefreshToken () {
			return refreshToken;
		}

		public TokenTranslationResponse setRefreshToken (final String refreshToken) {
			this.refreshToken = refreshToken;
			return this;
		}

		public String getScope () {
			return scope;
		}

		public TokenTranslationResponse setScope (final String scope) {
			this.scope = scope;
			return this;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(NeoComOAuth20.class);

	// - F I E L D - S E C T I O N ............................................................................
	private OAuth20Service oAuth20Service = null;
	private ESIStore store = null;
	private VerifyCharacter verify;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private NeoComOAuth20 () {
		super();
	}

	public NeoComOAuth20 (final String clientID, final String clientKey, final String callback,
												final String agent,
												final ESIStore store,
												final List<String> scopes) {

		this.store = store;
		ServiceBuilder builder = new ServiceBuilder(clientID)
				.apiKey(clientID)
				.apiSecret(clientKey)
				.state("NEOCOM-VERIFICATION-STATE");
		if ( StringUtils.isNotBlank(callback) ) builder.callback(callback);
		if ( !scopes.isEmpty() ) builder.scope(transformScopes(scopes));
		this.oAuth20Service = builder.build(NeoComAuthApi20.instance());

		OkHttpClient.Builder verifyClient =
				new OkHttpClient.Builder()
						.certificatePinner(
								new CertificatePinner.Builder()
										.add("login.eveonline.com", "sha256/5UeWOuDyX7IUmcKnsVdx+vLMkxEGAtzfaOUQT/caUBE=")
										.add("login.eveonline.com", "sha256/980Ionqp3wkYtN9SZVgMzuWQzJta1nfxNPwTem1X0uc=")
										.add("login.eveonline.com", "sha256/du6FkDdMcVQ3u8prumAo6t3i3G27uMP2EOhR8R0at/U=")
										.build())
						.addInterceptor(chain -> chain.proceed(
								chain.request()
										 .newBuilder()
										 .addHeader("User-Agent", agent)
										 .build()));
		this.verify =
				new Retrofit.Builder()
						.baseUrl("https://login.eveonline.com/")
						.addConverterFactory(JacksonConverterFactory.create())
						.client(verifyClient.build())
						.build()
						.create(VerifyCharacter.class);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getAuthorizationUrl () {
		return this.oAuth20Service.getAuthorizationUrl();
	}

	public TokenTranslationResponse fromAuthCode (final String authCode) {
		try {
			final OAuth2AccessToken token = this.oAuth20Service.getAccessToken(authCode);
			return save(token);
		} catch (OAuthException | IOException | InterruptedException | ExecutionException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public TokenTranslationResponse fromRefresh (final String refresh) {
		logger.info(">> [NeoComOAuth20.fromRefresh]");
		try {
			TokenTranslationResponse existing = this.store.get(refresh);
//			logger.info("-- [NeoComOAuth20.fromRefresh]> Token response: {}", existing.getAccessToken());
			if ( (null == existing) || (existing.getExpiresOn() < (System.currentTimeMillis() - 5 * 1000)) ) {
				logger.info("-- [NeoComOAuth20.fromRefresh]> Refresh of access token requested.");
				final OAuth2AccessToken token = this.oAuth20Service.refreshAccessToken(refresh);
				logger.info("-- [NeoComOAuth20.fromRefresh]> New token: {}", token.toString());
				return save(token);
			}
			return existing;
		} catch (OAuthException | IOException | InterruptedException | ExecutionException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public VerifyCharacterResponse verify (final String refresh) {
		final TokenTranslationResponse stored = this.store.get(refresh);
		try {
			final Response<VerifyCharacterResponse> r =
					this.verify.getVerification("Bearer " + stored.getAccessToken()).execute();
			if ( r.isSuccessful() ) {
				return r.body();
			}
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private TokenTranslationResponse save (final OAuth2AccessToken token) {
		TokenTranslationResponse returned =
				new TokenTranslationResponse()
						.setAccessToken(token.getAccessToken())
						.setRefreshToken(token.getRefreshToken())
						.setTokenType(token.getTokenType())
						.setScope(token.getScope())
						.setExpires(token.getExpiresIn());
		this.store.save(returned);
		return returned;
	}

	private String transformScopes (final List<String> scopeList) {
		StringBuilder scope = new StringBuilder();
		for (String s : scopeList) {
			scope.append(s);
			scope.append(" ");
		}
		return StringUtils.removeEnd(scope.toString(), " ");
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("NeoComOAuth20 [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
