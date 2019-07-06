package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Adam Antinoo
 */
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
	public VerifyCharacterResponse () {
		super();
	}

	public long getCharacterID () {
		return characterID;
	}
//
//	public void setCharacterID (final long characterID) {
//		this.characterID = characterID;
//	}
//
	public String getCharacterName () {
		return characterName;
	}
//
//	public void setCharacterName (final String characterName) {
//		this.characterName = characterName;
//	}
//
//	public String getExpiresOn () {
//		return expiresOn;
//	}
//
//	public void setExpiresOn (final String expiresOn) {
//		this.expiresOn = expiresOn;
//		// Convert the string to a data and then to the date milliseconds.
//		//			final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss");
//		//			expiresMillis = fmt.parseMillis(expiresOn);
//	}
//
//	public long getExpiresMillis () {
//		return expiresMillis;
//	}
//
//	public void setExpiresMillis (final long expiresInstant) {
//		this.expiresMillis = expiresInstant;
//	}
//
//	public String getScopes () {
//		return scopes;
//	}
//
//	public void setScopes (final String scopes) {
//		this.scopes = scopes;
//	}
//
//	public String getTokenType () {
//		return tokenType;
//	}
//
//	public void setTokenType (final String tokenType) {
//		this.tokenType = tokenType;
//	}
//
//	public String getCharacterOwnerHash () {
//		return characterOwnerHash;
//	}
//
//	public void setCharacterOwnerHash (final String characterOwnerHash) {
//		this.characterOwnerHash = characterOwnerHash;
//	}
//
//	public String getIntellectualProperty () {
//		return intellectualProperty;
//	}
//
//	public void setIntellectualProperty (final String intellectualProperty) {
//		this.intellectualProperty = intellectualProperty;
//	}
}
