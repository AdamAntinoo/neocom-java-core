//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;
import java.util.logging.Logger;

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
	private Vector<PlanetaryResource>	sceneryResources				= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	rawResources						= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t1Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t2Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t3Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t4Resources							= new Vector<PlanetaryResource>();
	private Vector<ProcessingAction>	actions									= new Vector();
	private double										inTax										= 5.0;
	private double										outTax									= 5.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public PlanetaryScenery() {
	//	}

	/**
	 * Return the stocked Planetary Resource that matches the paramter id.
	 * 
	 * @param inputResourceId
	 * @return
	 */
	public PlanetaryResource getResource(int inputResourceId) {
		PlanetaryResource hit = sceneryResources.get(inputResourceId);
		if (null == hit)
			return new PlanetaryResource(new Resource(inputResourceId));
		else
			return hit;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
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
			String cat = resource.getCategory();
			if ((cat.equalsIgnoreCase("Planetary Commodities")) || (cat.equalsIgnoreCase("Planetary Interaction"))
					|| (cat.equalsIgnoreCase("Planetary Resources"))) {
				// Process Raw resources to Tier 1 and then  start to process the set.
				if (cat.equalsIgnoreCase("Planetary Resources")) {
					ProcessingAction transform = processRaw(resource);
					actions.add(transform);
					// Add to our scenario list of resources the resources resulting from the transformation action.
					for (PlanetaryResource res : transform.getResources()) {
						stockResource(res);
					}
				} else {
					PlanetaryResource pres = new PlanetaryResource(resource);
					stockResource(pres);
				}
			}
		}
	}

	/**
	 * Converts a RAW level Planetary resource (Planetary Resources) into a Tier 1 Planetary resource and
	 * applies the tax to the input.
	 * 
	 * @param resource2Transform
	 * @return
	 */
	private ProcessingAction processRaw(Resource resource2Transform) {
		ProcessingAction action = new ProcessingAction();
		// Add the resources to be transformed.
		action.addResource(resource2Transform);
		// Do the transformation.
		action.doRawTransformation();
		// Return the new action to be used as an initial state.
		return action;
	}

	/**
	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
	 * resource. If the resource is not already in the list then we put it on the right one.
	 * 
	 * @param typeid
	 *          the resource item id
	 * @param quantity
	 *          the quantity of the resource to stack.
	 */
	private void stockResource(PlanetaryResource newResource) {
		// Get the list where I should stock the resource.
		Vector<PlanetaryResource> targetList = null;
		switch (newResource.getType()) {
			case RAW:
				targetList = rawResources;
				break;
			case TIER1:
				targetList = t1Resources;
				break;
			case TIER2:
				targetList = t2Resources;
				break;
			case TIER3:
				targetList = t3Resources;
				break;
			case TIER4:
				targetList = t4Resources;
				break;

			default:
				break;
		}
		boolean found = false;
		for (PlanetaryResource pr : targetList) {
			if (pr.getResource().getTypeID() == newResource.getResource().getTypeID()) {
				pr.setQuantity(pr.getQuantity() + newResource.getQuantity());
				found = true;
			}
		}
		if (!found) {
			// Add the new resource to the list.
			targetList.add(newResource);
		}
	}
}

// - UNUSED CODE ............................................................................................
