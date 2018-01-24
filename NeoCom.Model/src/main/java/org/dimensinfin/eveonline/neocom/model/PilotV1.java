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
package org.dimensinfin.eveonline.neocom.model;

import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOkHomeLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adam on 16/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
// TODO We inherit from NeoComCharacter temporarily until the parts and renders are upgraded.
public class PilotV1 extends NeoComNode implements Comparable<PilotV1> {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(PilotV1.class);

	// - F I E L D - S E C T I O N ............................................................................
	/** Should contain a copy of this data value can can also be found at the delegatedCharacter. */
	private int characterID = -1;
	private String name = "-PILOT NAME-";
	/**
	 * Character account balance from the AccountBalanceResponse CCP api call. This can apply to Pilots and
	 * Corporations.
	 */
	private double accountBalance = 0.0;
	/** Reference to the original eveapi Character data. */
	private Character delegatedCharacter = null;
	/**
	 * Character detailed information from the CharacterInfoResponse CCP api call. This can apply to Pilots and
	 * Corporations.
	 */
	private CharacterInfoResponse characterInfo = null;
	private GetCharactersCharacterIdClonesOkHomeLocation homeLocation=null;
	private EveLocation lastKnownLocation = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotV1 () {
		super();
		jsonClass = "NeoComCharacter";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public int getCharacterId () {
		return characterID;
	}

	public String getName () {
		return name;
	}

	public double getAccountBalance () {
		return accountBalance;
	}

	public CharacterInfoResponse getCharacterInfo () {
		return characterInfo;
	}

	public void setCharacterId (final int characterID) {
		this.characterID = characterID;
	}

	public void setName (final String name) {
		this.name = name;
	}

	public void setAccountBalance (final double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public void setDelegatedCharacter (final Character delegatedCharacter) {
		this.delegatedCharacter = delegatedCharacter;
	}

	public void setCharacterInfo (final CharacterInfoResponse characterInfo) {
		this.characterInfo = characterInfo;
	}

	public void setHomeLocation(final GetCharactersCharacterIdClonesOkHomeLocation homeLocation){
		this.homeLocation=homeLocation;
		// Convert this location pointer to a NeoCom location.
		lastKnownLocation= ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(homeLocation.getLocationId());
	}
	// --- D E L E G A T E D   M E T H O D S
	public String getURLForAvatar () {
		return "http://image.eveonline.com/character/" + this.getCharacterId() + "_256.jpg";
	}
	public EveLocation getLastKnownLocation () {
		if(null!=lastKnownLocation)return lastKnownLocation;
		else return new EveLocation();
	}
	public int compareTo (final PilotV1 o) {
		if ( o.getCharacterId() == getCharacterId() ) return 0;
		else return o.getName().compareTo(getName());
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("PilotV1 [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
