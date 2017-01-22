//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Encapsulates a Resource that is of the class of Planetary Resources. It also knows its type and some other
 * data to help on Planetary Resource transformations.
 * 
 * @author Adam Antinoo
 */
public class PlanetaryResource {
	public enum EPlanetaryTypes {
		RAW, TIER1, TIER2, TIER3, TIER4
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger		= Logger.getLogger("org.dimensinfin.eveonline.poc.planetary");

	// - F I E L D - S E C T I O N ............................................................................
	private Resource				resource	= null;
	private EPlanetaryTypes	type			= EPlanetaryTypes.RAW;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryResource(Resource resource) {
		this.resource = resource;
		// Classify the resource into the right type.
		String cat = resource.getCategory();
		String group = resource.getGroupName();
		if (cat.equalsIgnoreCase("Planetary Resources")) {
			type = EPlanetaryTypes.RAW;
		}
		if ((cat.equalsIgnoreCase("Planetary Commodities")) && (group.equalsIgnoreCase("Basic Commodities"))) {
			type = EPlanetaryTypes.TIER1;
		}
		if ((cat.equalsIgnoreCase("Planetary Commodities")) && (group.equalsIgnoreCase("Refined Commodities"))) {
			type = EPlanetaryTypes.TIER2;
		}
		if ((cat.equalsIgnoreCase("Planetary Commodities")) && (group.equalsIgnoreCase("Specialized Commodities"))) {
			type = EPlanetaryTypes.TIER3;
		}
		if ((cat.equalsIgnoreCase("Planetary Commodities")) && (group.equalsIgnoreCase("Advanced Commodities"))) {
			type = EPlanetaryTypes.TIER4;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getCategory() {
		return resource.getCategory();
	}

	public String getGroupName() {
		return resource.getGroupName();
	}

	public String getName() {
		return resource.getName();
	}

	/**
	 * This method is only valid for RAW resources that are the only ones that require a single resource to
	 * generate an output. The other resources require matching with another or more to generate an output and
	 * that output depends on the set.
	 * 
	 * @return
	 */
	public int getOutputId() {
		int outputId = this.getResource().getTypeID();
		if (type == EPlanetaryTypes.RAW) {
			outputId = AppConnector.getDBConnector().searchRawPlanetaryOutput(getResource().getItem().getItemID());
		}
		return outputId;
	}

	public int getQuantity() {
		return resource.getQuantity();
	}

	public Resource getResource() {
		return resource;
	}

	public EPlanetaryTypes getType() {
		return type;
	}

	public void setQuantity(int newQuantity) {
		resource.setQuantity(newQuantity);
	}

}

// - UNUSED CODE ............................................................................................
