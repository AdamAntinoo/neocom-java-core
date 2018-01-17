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
package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok.PlanetTypeEnum;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the equivalent model on the NeoCom MVC for the Colony list that are the planets that have the current
 * capsuleer colonies. I ave added some mode code to transform the code to real data to complement the original
 * information from swagger OK classes.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class Colony extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(Colony.class);

	// - F I E L D - S E C T I O N ............................................................................
	private Integer solarSystemId = null;
	private Integer planetId = null;
	private PlanetTypeEnum planetType = null;
	private Integer ownerId = null;
	private Integer upgradeLevel = null;
	private Integer numPins = null;
	private DateTime lastUpdate = null;
	private EveLocation location = null;
	private GetUniversePlanetsPlanetIdOk planetData = null;
	private List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pins = new ArrayList<GetCharactersCharacterIdPlanetsPlanetIdOkPins>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Colony () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public Integer getSolarSystemId () {
		return solarSystemId;
	}

	public Integer getPlanetId () {
		return planetId;
	}

	public String getPlanetType () {
		return planetType.name();
	}

	public Integer getOwnerId () {
		return ownerId;
	}

	public Integer getUpgradeLevel () {
		return upgradeLevel;
	}

	public Integer getNumPins () {
		return numPins;
	}

	public DateTime getLastUpdate () {
		return lastUpdate;
	}

	public String getSolarSystemName () {
		if ( null != location ) return location.getSystem();
		else return "-System undefined-";
	}

	public double getSecurityValue () {
		return location.getSecurityValue();
	}

	public void setSolarSystemId (final Integer solarSystemId) {
		this.solarSystemId = solarSystemId;
		// Locate the solar system data on the Location database.
		location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(solarSystemId);
	}

	public void setPlanetId (final Integer planetId) {
		this.planetId = planetId;
	}

	public void setPlanetData (final GetUniversePlanetsPlanetIdOk planetData) {
		this.planetData = planetData;
	}

	public void setPlanetType (final PlanetTypeEnum planetType) {
		this.planetType = planetType;
	}

	public void setOwnerId (final Integer ownerId) {
		this.ownerId = ownerId;
	}

	public void setUpgradeLevel (final Integer upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
	}

	public void setNumPins (final Integer numPins) {
		this.numPins = numPins;
	}

	public void setLastUpdate (final DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setStructuresData (final GetCharactersCharacterIdPlanetsPlanetIdOk structures) {
		this.pins = structures.getPins();
	}

	// --- D E L E G A T E D   M E T H O D S
	public String getPlanetName () {
		if ( null != planetData ) return planetData.getName();
		else return "Planet 0";
	}

	public List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> getStructures () {
		return pins;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("Colony [");
		buffer.append("name: ").append(getSolarSystemName());
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
