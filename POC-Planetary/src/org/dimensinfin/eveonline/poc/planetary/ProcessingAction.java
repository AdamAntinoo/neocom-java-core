//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.poc.planetary.PlanetaryResource.EPlanetaryTypes;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Stores the data to transform a set of resources into a new set where some of them are processed from a Tier
 * to a higher Tier. It uses the CCP database information to generate the quantity conversions from the input
 * data to the result data. <br>
 * Also stores the number of cycles required for the complete transformation of the resources and then the
 * total time to perfrom the transformation.
 * 
 * @author Adam Antinoo
 */
public class ProcessingAction {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger									= Logger.getLogger("ProcessingResult");
	private static int								RAW2TIER1_TRANSFORMQTY	= 3000;
	private static final int					MINUTES_RAWCYCLE				= 30;
	private static final int					RAWOUTPUT_MULTIPLIER		= 20;

	// - F I E L D - S E C T I O N ............................................................................
	private Vector<PlanetaryResource>	rawResources						= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t1Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t2Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t3Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t4Resources							= new Vector<PlanetaryResource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public ProcessingAction() {
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(Resource resource) {
		// Transform resource to Planetary Resource and then store on the right list.
		PlanetaryResource pres = new PlanetaryResource(resource);
		stockResource(pres);
	}

	/**
	 * Converts all the RAW resources into Tier 1 resources. Packs the cycles into the number of possible cycles
	 * and stores the time and the resulting Planetary Resources.
	 */
	public void doRawTransformation() {
		for (PlanetaryResource resource : rawResources) {
			if (resource.getType() == EPlanetaryTypes.RAW) {
				int typeid = resource.getResource().getTypeID();
				int qty = resource.getQuantity();
				int outResource = resource.getOutputId();

				// Calculate the maximum number of cycles.
				int cycles = Math.floorDiv(resource.getQuantity(), RAW2TIER1_TRANSFORMQTY);
				int totalTime = cycles * MINUTES_RAWCYCLE;
				// Get the resulting resources.
				removeResource(typeid);
				addResource(new Resource(outResource, RAWOUTPUT_MULTIPLIER * cycles));
				// Add the rest of the RAW that was not processed.
				addResource(new Resource(typeid, qty - (RAWOUTPUT_MULTIPLIER * cycles)));
			}
		}
	}

	/**
	 * Returns the list of resources at the end of the transformations.
	 * 
	 * @return
	 */
	public Vector<PlanetaryResource> getResources() {
		Vector<PlanetaryResource> result = new Vector<PlanetaryResource>();
		result.addAll(rawResources);
		result.addAll(t1Resources);
		result.addAll(t2Resources);
		result.addAll(t3Resources);
		result.addAll(t4Resources);
		return result;
	}

	/**
	 * Removes the resource with the ID indicated from any of the lists of Planetary Resources.
	 * 
	 * @param typeid
	 *          item type id to remove.
	 */
	private void removeResource(int typeid) {
		for (PlanetaryResource res : rawResources) {
			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
		}
		for (PlanetaryResource res : t1Resources) {
			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
		}
		for (PlanetaryResource res : t2Resources) {
			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
		}
		for (PlanetaryResource res : t3Resources) {
			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
		}
		for (PlanetaryResource res : t4Resources) {
			if (res.getResource().getTypeID() == typeid) rawResources.remove(res);
		}
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
