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

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class VerifyCharacterResponse {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
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

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public VerifyCharacterResponse () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
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
// - UNUSED CODE ............................................................................................
//[01]
