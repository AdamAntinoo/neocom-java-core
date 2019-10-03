package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequestBody {
	@JsonProperty("grant_type")
	public String grant_type = "authorization_code";
	@JsonProperty("code")
	public String code;

	// - C O N S T R U C T O R S
	public TokenRequestBody() {
		super();
	}

	public String getGrant_type() {
		return "authorization_code";
	}

	public String getCode() {
		return code;
	}

	public TokenRequestBody setCode( final String code ) {
		this.code = code;
		return this;
	}
}
