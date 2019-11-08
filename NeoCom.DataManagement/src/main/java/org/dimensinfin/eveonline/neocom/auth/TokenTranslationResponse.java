package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TokenTranslationResponse {
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

	// - C O N S T R U C T O R S
	public TokenTranslationResponse() {
		super();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpires() {
		return expires;
	}

	public long getExpiresOn() {
		return created + (expires * 1000);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getScope() {
		return scope;
	}

	public TokenTranslationResponse setAccessToken( final String accessToken ) {
		this.accessToken = accessToken;
		return this;
	}

	public TokenTranslationResponse setTokenType( final String tokenType ) {
		this.tokenType = tokenType;
		return this;
	}

	public TokenTranslationResponse setExpires( final long expires ) {
		this.expires = expires;
		return this;
	}

	public TokenTranslationResponse setRefreshToken( final String refreshToken ) {
		this.refreshToken = refreshToken;
		return this;
	}

	public TokenTranslationResponse setScope( final String scope ) {
		this.scope = scope;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "accessToken", accessToken )
				.append( "tokenType", tokenType )
				.append( "expires", expires )
				.append( "refreshToken", refreshToken )
				.append( "created", created )
				.append( "scope", scope )
				.toString();
	}
}
