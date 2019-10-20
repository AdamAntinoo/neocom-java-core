package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyCharacterResponse {
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

	// - C O N S T R U C T O R S
	public VerifyCharacterResponse() {
		super();
	}

	public long getCharacterID() {
		return this.characterID;
	}

	public String getCharacterName() {
		return this.characterName;
	}

	public String getScopes() {
		return this.scopes;
	}
}
