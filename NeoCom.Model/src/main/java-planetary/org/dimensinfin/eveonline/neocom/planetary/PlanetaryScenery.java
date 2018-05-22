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
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;


/**
 * This class stocks the planetary resources available for an Optimization session. It also defines and stores all the
 * environment data that should be used by a processor to perform the optimization evaluation.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryScenery extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("PlanetaryScenery");
//	private static int RAW2TIER1_TRANSFORMQTY = 3000;

	// - F I E L D - S E C T I O N ............................................................................
	private final List<Resource> sceneryResources = new Vector<Resource>();
	private final List<Resource> removedSceneryResources = new Vector<Resource>();
	private final List<ProcessingAction> actions = new Vector<ProcessingAction>();
	private double inTax = 5.0;
	private double outTax = 5.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Stocks for processing and selling evaluation the list of Planetary Resources received on the parameter.
	 * While processing the resources it will convert them to our special variation of
	 * <code>PlanetaryResource</code> and also remove from the list any non planetary resource. <br>
	 * It will also transform RAW Planetary Resources to Tier 1 to simplify the transformations algorithms since
	 * RAW are never more profitable that their Tier 1 transformations.
	 *
	 * @param planetaryAssets The list of Resources to be used as the start and input.
	 */
	public void stock( final List<Resource> planetaryAssets ) {
		// Convert Resources to Planetary and reject non Planetary.
		for (Resource resource : planetaryAssets) {
			this.stock(resource);
		}
	}

	public void removeLowestResource() {
		// Search for the lowest quantity number resource.
		Resource target = new Resource(sceneryResources.get(0).getTypeId()).setQuantity(Integer.MAX_VALUE);
		for (Resource res : sceneryResources) {
			if (res.getQuantity() < target.getQuantity())
				target = res;
		}
		// Move the resource to the removed list.
		removedSceneryResources.add(target);
		logger.info(">> [PlanetaryScenery.removeLowestResource]> Removed resource: {} with quantity {}"
				,target.getName(),target.getQuantity());
		sceneryResources.remove(target);
	}
	// --- G E T T E R S   &   S E T T E R S

	/**
	 * Return the list of resources stocked on this scenery.
	 *
	 * @return
	 */
	public List<Resource> getResources() {
		return sceneryResources;
	}

	public double getInTax() {
		return inTax;
	}

	public double getOutTax() {
		return outTax;
	}

	public PlanetaryScenery setInTax( final double inTax ) {
		this.inTax = inTax;
		return this;
	}

	public PlanetaryScenery setOutTax( final double outTax ) {
		this.outTax = outTax;
		return this;
	}

	/**
	 * Return the stocked Planetary Resource that matches the parameter id. If not found return a Resource of
	 * quantity ZERO.
	 *
	 * @param inputResourceId
	 * @return
	 */
	protected Resource getResource( final int inputResourceId ) {
		for (Resource res : sceneryResources) {
			if (res.getTypeId() == inputResourceId) return res;
		}
		return new Resource(inputResourceId);
	}

	// --- P R I V A T E   S E C T I O N

	/**
	 * Convert RAW Planetary Resources to Tier 1 and store the results into the list of resources for this
	 * scenery. Other resources are stored with no processing.
	 *
	 * @param resource resource to check and store.
	 */
	private void stock( final Resource resource ) {
		// If the resource is of type RAW then stock the transformation of that resource into a Tier1.
		if (resource.getCategory().equalsIgnoreCase("Planetary Resources")) {
			int outputType = accessSDEDBHelper().searchRawPlanetaryOutput(resource.getTypeId());
			ProcessingAction action = new ProcessingAction(outputType);
			action.addResource(resource);
			List<Resource> results = action.getActionResults();
			// TODO - Add also the remainder of the RAW resource to the list?. It will just complete the costing
			for (Resource planetaryResource : results) {
				sceneryResources.add(planetaryResource);
			}
		} else {
			sceneryResources.add(resource);
		}
	}
}

// - UNUSED CODE ............................................................................................
