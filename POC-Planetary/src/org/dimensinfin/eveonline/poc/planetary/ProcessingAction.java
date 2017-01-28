//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.Schematics;
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
	private Schematics								schematics							= null;
	private Vector<PlanetaryResource>	rawResources						= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t1Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t2Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t3Resources							= new Vector<PlanetaryResource>();
	private Vector<PlanetaryResource>	t4Resources							= new Vector<PlanetaryResource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Instance a new <code>ProcessingAction</code> and set the target product id to be produced by this action.
	 * This should get the schematics information so the action can process the quantities and the length of the
	 * cycles.
	 * 
	 * @param targetId
	 */
	public ProcessingAction(int targetId) {
		// Get the schematics information.
		schematics = AppConnector.getDBConnector().searchSchematics4Output(targetId);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(PlanetaryResource resource) {
		// Transform resource to Planetary Resource and then store on the right list.
		//		PlanetaryResource pres = new PlanetaryResource(resource);
		stockResource(resource);
	}

	public void addResource(Resource resource) {
		// Transform resource to Planetary Resource and then store on the right list.
		PlanetaryResource pres = new PlanetaryResource(resource);
		stockResource(pres);
	}

	/**
	 * Returns true if the action has enough input materials to run cycles. The number of cycles can be received
	 * as a parameter but it is optional. The action has to know what is the target resource to be produced. It
	 * should have already the schematics required for that job.
	 * 
	 * @return
	 */
	public boolean checkActionActive() {
		return checkActionActive(1);
	}

	public boolean checkActionActive(int cycles) {

		return false;
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
