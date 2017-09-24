//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.planetary;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class performs the Plantary transformations and the value calculation in a recursive way. Starting
 * from an specific configuration the methods scan the different combinations possible for Tier 3, 4 and 5 in
 * seque4nce and return the better profit Scenario depending on the current market data buyers prices.
 * 
 * @author Adam Antinoo
 */
public class PlanetaryScenery {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger									= Logger.getLogger("PlanetaryScenery");
	private static int											RAW2TIER1_TRANSFORMQTY	= 3000;

	// - F I E L D - S E C T I O N ............................................................................
	private final Vector<Resource>					sceneryResources				= new Vector<Resource>();
	private final Vector<ProcessingAction>	actions									= new Vector<ProcessingAction>();
	private final double										inTax										= 5.0;
	private final double										outTax									= 5.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the stocked Planetary Resource that matches the parameter id. If not found return a Resource of
	 * quantity ZERO.
	 * 
	 * @param inputResourceId
	 * @return
	 */
	public Resource getResource(final int inputResourceId) {
		for (Resource res : sceneryResources) {
			if (res.getTypeID() == inputResourceId) return res;
		}
		return new Resource(inputResourceId);
	}

	/**
	 * Retur the list of resources stocked on this scenery.
	 * 
	 * @return
	 * @return
	 */
	public Vector<Resource> getResources() {
		return sceneryResources;
	}

	/**
	 * Stocks for processing and selling evaluation the list of Planetary Resources received on the parameter.
	 * While processing the resources it will convert them to our special variation of
	 * <code>PlanetaryResource</code> and also remove from the list any non planetary resource. <br>
	 * It will also transform RAW Planetary Resources to Tier 1 to simplify the transformations algorithms since
	 * RAW are never more profitable that their Tier 1 transformations.
	 * 
	 * @param planetaryAssets
	 *          The list of Resources to be used as the start and input.
	 */
	public void stock(final Vector<Resource> planetaryAssets) {
		// Convert Resources to Planetary and reject non Planetary.
		for (Resource resource : planetaryAssets) {
			this.stock(resource);
		}
	}
	//[01]

	/**
	 * Convert RAW Planetary Resources to Tier 1 and store the results into the list of resources for this
	 * scenery. Other resources are stored with no processing.
	 * 
	 * @param resource
	 *          resource to check and store.
	 */
	private void stock(final Resource resource) {
		// If the resource is of type RAW then stock the transformation of that resource into a Tier1.
		if (resource.getCategory().equalsIgnoreCase("Planetary Resources")) {
			int outputType = AppConnector.getDBConnector().searchRawPlanetaryOutput(resource.getTypeID());
			ProcessingAction action = new ProcessingAction(outputType);
			action.addResource(resource);
			Vector<Resource> results = action.getActionResults();
			for (Resource planetaryResource : results) {
				sceneryResources.add(planetaryResource);
			}
		} else {
			sceneryResources.add(resource);
		}
	}
}

// - UNUSED CODE ............................................................................................
