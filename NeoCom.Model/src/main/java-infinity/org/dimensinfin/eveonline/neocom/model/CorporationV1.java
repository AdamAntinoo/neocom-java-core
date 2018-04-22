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

import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

/**
 * Implements the MVC adaptation for the Corporation data. Its contents depend on multiple ESI calls even most of them are
 * related to Universe data that is loaded on demand.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class CorporationV1 extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("CorporationV1");

	// - F I E L D - S E C T I O N ............................................................................
	public int corporationId = -1;
	public String name = "-NOT-KNOWN-";
	public String ticker = "---";
	public int memberCount = 0;
	public AllianceV1 alliance = null;
	public String description = "-NA-";
	public double taxRate = 0.0;
	public long dateFounded = 0;
	public EveLocation homeStation = new EveLocation();
	public String url4Icon = "http://image.eveonline.com/Alliance/117383987_128.png";

	private GetCorporationsCorporationIdOk publicData = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CorporationV1() {
		super();
		jsonClass = "Corporation";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//--- G E T T E R S   &   S E T T E R S
	public int getCorporationId() {
		return corporationId;
	}

	public int getAllianceId() {
		return alliance.getAllianceId();
	}

	public String getName() {
		return name;
	}

	public String getTicker() {
		return ticker;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public String getDescription() {
		return description;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public long getDateFounded() {
		return dateFounded;
	}

	public EveLocation getHomeStation() {
		return homeStation;
	}

	public CorporationV1 setCorporationId( final int corporationId ) {
		this.corporationId = corporationId;
		return this;
	}

	//--- D A T A   T R A N S F O R M A T I O N
	public CorporationV1 setPublicData( final GetCorporationsCorporationIdOk publicData ) {
		// Keep a local copy of the data.
		this.publicData = publicData;
		// Copy the relative public fields.
		name = publicData.getName();
		ticker = publicData.getTicker();
		memberCount = publicData.getMemberCount();
		description = publicData.getDescription();
		taxRate = publicData.getTaxRate();
		dateFounded = publicData.getDateFounded().getMillis();
		url4Icon = "http://image.eveonline.com/Corporation/" + corporationId + "_128.png";
		return this;
	}

	public CorporationV1 setAlliance( final AllianceV1 alliance ) {
		this.alliance = alliance;
		return this;
	}

	public CorporationV1 setHomeStation( final long stationIdentifier ) {
			this.homeStation = accessGlobal().searchLocation4Id(stationIdentifier);
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("CorporationV1 [")
				.append("[#").append(corporationId).append("] ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
