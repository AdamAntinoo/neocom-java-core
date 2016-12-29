//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;

/**
 * This class encapsulates the core Eve Online model into the adapter for the Android MVC implementation. This
 * requires to implement the methods and the interfaces for use of the Android Parts. <br>
 * Uses a delegate to call the references real Location.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ShipLocation extends EveLocation implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ShipLocation");

	public static ShipLocation createFromLocation(final EveLocation original) {
		ShipLocation shiploc = new ShipLocation();
		shiploc.setId(original.getID());
		shiploc.setStationID(original.getStationID());
		shiploc.setStation(original.getStation());
		shiploc.setSystemID(original.getSystemID());
		shiploc.setSystem(original.getSystem());
		shiploc.setConstellationID(original.getConstellationID());
		shiploc.setConstellation(original.getConstellation());
		shiploc.setRegionID(original.getRegionID());
		shiploc.setRegion(original.getRegion());
		shiploc.setSecurity(original.getSecurity());
		shiploc.typeID = original.typeID;
		shiploc.citadel = original.citadel;

		return shiploc;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Ship locations collaborate to the model by adding all their children because we store there the items
	 * located at the selected real location.
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results.addAll((Collection<? extends AbstractComplexNode>) this.getChildren());
		return results;
	}

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public ShipLocation(final EveLocation delegate) {
	//		locationDelegate = delegate;
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	//	private EveLocation		locationDelegate	= null;
}

// - UNUSED CODE ............................................................................................
