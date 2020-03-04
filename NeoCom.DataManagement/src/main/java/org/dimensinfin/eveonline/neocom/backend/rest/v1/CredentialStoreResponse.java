package org.dimensinfin.eveonline.neocom.backend.rest.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialStoreResponse {
	private String jwtToken;

	private CredentialStoreResponse() {}

	public String getJwtToken() {
		return jwtToken;
	}
}