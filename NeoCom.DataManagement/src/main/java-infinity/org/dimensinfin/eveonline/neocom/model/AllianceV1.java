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

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * @author Adam Antinoo
 */
public class AllianceV1 extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("AllianceV1");

	// - F I E L D - S E C T I O N ............................................................................
	public int allianceId = -1;
	public String name = "-NOT-KNOWN-";
	public String ticker = "---";
	public long dateFounded = 0;
	//	public CorporationV1 executorCorporation = new CorporationV1();
	public String url4Icon = "http://image.eveonline.com/Alliance/117383987_128.png";

	private GetAlliancesAllianceIdOk publicData = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AllianceV1() {
		super();
		jsonClass = "Alliance";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//--- G E T T E R S   &   S E T T E R S
	public int getAllianceId() {
		return allianceId;
	}

	public String getName() {
		return name;
	}

	public String getTicker() {
		return ticker;
	}

	public long getDateFounded() {
		return dateFounded;
	}

	public AllianceV1 setAllianceId( final int allianceId ) {
		this.allianceId = allianceId;
		return this;
	}
//	public CorporationV1 getExecutorCorporation() {
//		return executorCorporation;
//	}

	//--- D A T A   T R A N S F O R M A T I O N
	public AllianceV1 setPublicData( final GetAlliancesAllianceIdOk publicData ) {
		// Keep a local copy of the data.
		this.publicData = publicData;
		// Copy the relative public fields.
		name = publicData.getName();
		ticker = publicData.getTicker();
		dateFounded = publicData.getDateFounded().getMillis();
		url4Icon = "http://image.eveonline.com/Alliance/" + allianceId + "_128.png";
		return this;
	}

//	public AllianceV1 setExecutorCorporation( final CorporationV1 executorCorporation ) {
//		this.executorCorporation = executorCorporation;
//		return this;
//	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("AllianceV1 [ ")
				.append("[#").append(allianceId).append("] ")
				.append(name).append(" ")
				.append("]")
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
