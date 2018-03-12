//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOkHomeLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PilotV2 extends NeoComNode implements Comparable<PilotV2> {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("PilotV2");

	// - F I E L D - S E C T I O N ............................................................................
	public int characterId = -1;
	public String name = "-NAME-";
	public double accountBalance = -1.0;
	public String urlforAvatar = "http://image.eveonline.com/character/92223647_256.jpg";
	public CorporationV1 corporation = null;
	public AllianceV1 alliance = null;
	public GetUniverseRaces200Ok race = null;
	public GetUniverseBloodlines200Ok bloodline = null;
	public GetUniverseAncestries ancestry = null;

	public EveLocation lastKnownLocation = null;
	private GetCharactersCharacterIdOk publicData = null;
	private GetCharactersCharacterIdClonesOk cloneInformation = null;
	private GetCharactersCharacterIdClonesOkHomeLocation homeLocation = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotV2() {
		super();
		jsonClass = "NeoComCharacter";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public int getCharacterId() {
		return characterId;
	}

	public String getName() {
		return name;
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public EveLocation getLastKnownLocation() {
		if (null != lastKnownLocation) return lastKnownLocation;
		else if (null == homeLocation) return new EveLocation();
		else {
			lastKnownLocation = GlobalDataManager.searchLocation4Id(homeLocation.getLocationId());
			return lastKnownLocation;
		}
	}

	public String getUrlforAvatar() {
		return urlforAvatar;
	}

	public CorporationV1 getCorporation() {
		return corporation;
	}

	public void setCharacterId( final int characterIdentifier ) {
		this.characterId = characterIdentifier;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	public void setAccountBalance( final double accountBalance ) {
		this.accountBalance = accountBalance;
	}

	public void setHomeLocation( final GetCharactersCharacterIdClonesOkHomeLocation homeLocation ) {
		this.homeLocation = homeLocation;
		// Convert this location pointer to a NeoCom location.
		lastKnownLocation = GlobalDataManager.searchLocation4Id(homeLocation.getLocationId());
	}

	/**
	 * Append to this pilot instance the block of public data. Instead copying the fields set the data as an delegate and then
	 * fill the other data gaps by requesting more data from the Global provider.
	 *
	 * @param publicData ESI data model with all public identifiers.
	 */
	public void setPublicData( final GetCharactersCharacterIdOk publicData ) {
		this.publicData = publicData;
		// Process the public data and get the referenced instances for the Corporation, race, etc.
		corporation = GlobalDataManager.getCorporationV1(publicData.getCorporationId());
		alliance = GlobalDataManager.getAllianceV1(publicData.getAllianceId());
		race = GlobalDataManager.searchSDERace(publicData.getRaceId());
		bloodline = GlobalDataManager.searchSDEBloodline(publicData.getBloodlineId());
		ancestry = GlobalDataManager.searchSDEAncestry(publicData.getAncestryId());
	}

	public void setCloneInformation( final GetCharactersCharacterIdClonesOk cloneInformation ) {
		this.cloneInformation = cloneInformation;
	}

	// --- D E L E G A T E D   M E T H O D S
	public String getURLForAvatar() {
		return "http://image.eveonline.com/character/" + this.getCharacterId() + "_256.jpg";
	}


	public int compareTo( final PilotV2 o ) {
		if (o.getCharacterId() == getCharacterId()) return 0;
		else return o.getName().compareTo(getName());
	}

	@Override
	public String toString() {
		return new StringBuffer("PilotV2 [")
				.append("[#").append(characterId).append("] ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
