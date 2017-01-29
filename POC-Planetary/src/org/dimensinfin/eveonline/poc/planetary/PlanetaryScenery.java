//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

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
	private static Logger							logger									= Logger.getLogger("PlanetaryScenery");
	private static int								RAW2TIER1_TRANSFORMQTY	= 3000;

	// - F I E L D - S E C T I O N ............................................................................
	private Vector<Resource>					sceneryResources				= new Vector<Resource>();
	private Vector<ProcessingAction>	actions									= new Vector();
	private double										inTax										= 5.0;
	private double										outTax									= 5.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the stocked Planetary Resource that matches the parameter id. If not found return a Resource of
	 * quantity ZERO.
	 * 
	 * @param inputResourceId
	 * @return
	 */
	public Resource getResource(int inputResourceId) {
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

	public void stock(Resource resource) {
		// If the resource is of type RAW then stock the transformation of that resource into a Tier1.
		if (resource.getCategory().equalsIgnoreCase("Planetary Resources")) {
			int outputType = AppConnector.getDBConnector().searchRawPlanetaryOutput(resource.getTypeID());
			ProcessingAction action = new ProcessingAction(outputType);
			action.addResource(resource);
			Vector<Resource> results = action.getActionResults();
			for (Resource planetaryResource : results) {
				sceneryResources.add(planetaryResource);
			}
		}
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
	public void stock(Vector<Resource> planetaryAssets) {
		// Convert Resources to Planetary and reject non Planetary.
		for (Resource resource : planetaryAssets) {
			stock(resource);
		}
	}
	//[01]
}

// - UNUSED CODE ............................................................................................
//[01]
//	/**
//	 * Converts a RAW level Planetary resource (Planetary Resources) into a Tier 1 Planetary resource and
//	 * applies the tax to the input.
//	 * 
//	 * @param resource2Transform
//	 * @return
//	 */
//	private ProcessingAction processRaw(Resource resource2Transform) {
//		ProcessingAction action = new ProcessingAction();
//		// Add the resources to be transformed.
//		action.addResource(resource2Transform);
//		// Do the transformation.
//		action.doRawTransformation();
//		// Return the new action to be used as an initial state.
//		return action;
//	}

//	/**
//	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
//	 * resource. If the resource is not already in the list then we put it on the right one.
//	 * 
//	 * @param typeid
//	 *          the resource item id
//	 * @param quantity
//	 *          the quantity of the resource to stack.
//	 */
//	private void stockResource(PlanetaryResource newResource) {
//		// Get the list where I should stock the resource.
//		Vector<PlanetaryResource> targetList = null;
//		switch (newResource.getType()) {
//			case RAW:
//				targetList = rawResources;
//				break;
//			case TIER1:
//				targetList = t1Resources;
//				break;
//			case TIER2:
//				targetList = t2Resources;
//				break;
//			case TIER3:
//				targetList = t3Resources;
//				break;
//			case TIER4:
//				targetList = t4Resources;
//				break;
//
//			default:
//				break;
//		}
//		boolean found = false;
//		for (PlanetaryResource pr : targetList) {
//			if (pr.getResource().getTypeID() == newResource.getResource().getTypeID()) {
//				pr.setQuantity(pr.getQuantity() + newResource.getQuantity());
//				found = true;
//			}
//		}
//		if (!found) {
//			// Add the new resource to the list.
//			targetList.add(newResource);
//		}
//	}
