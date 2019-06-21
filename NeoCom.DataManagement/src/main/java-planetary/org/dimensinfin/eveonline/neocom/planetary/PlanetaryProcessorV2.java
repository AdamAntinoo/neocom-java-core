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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation for the Processor does not use a recursive algorithm to test all the possible combinations. If the
 * number of different resources grows the number of possible processing combinations to test is so high that the process will
 * not finish in days.
 * So I have searched for another approach. I will consider that the higher the tier the most of the profit. So this new
 * Processor will start scanning if it is possible to complete any production for Tier 4, then check for Tier 3 and so on.
 * The processing will remove any used resources for completed productions before continuing testing all the possible product
 * results. This is not so efficient at the profit level but the number of iterations to check has a fixed number and it is the
 * number of different planetary resources belonging to Tier 2 (24), Tier 3 (21) and Tier4 (8) for a total of 53.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessorV2 extends PlanetaryProcessor {
	private static Logger logger = LoggerFactory.getLogger(PlanetaryProcessorV2.class);
	private ESIDataAdapter esiDataAdapter;

	// - F I E L D - S E C T I O N ............................................................................
	private List<Resource> contextResources = new Vector();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryProcessorV2( final PlanetaryScenery scenery ) {
		super(scenery);
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Start cheking the possibility to produce any of the Tier 4 resources.
	 * <p>
	 * The process starts picking a resource, then adding
	 * to the list all the available resources that are needed to produce that resource, then continuing doing this for all the
	 * new resources in the list until we reach the Tier 2.
	 * The we fire the PlanetaryProcessAction and test if the at the end we are able to generate a Tier 4. If not then we go on
	 * to the next tier resource. If we are able to generate a new resource then we do the resource calculations to remove the
	 * resources used and to and the resources generated to be able to calculate the profit.
	 * The new resource collection is the used to test for the next Tier resource.
	 * <p>
	 * Once we finish with the list of Tier 4 resources we do the same with Tier 3 and finally with Tier 2.
	 *
	 * @return the complete list of processing actions resulting from the testing of all the resoures on this tiers with the
	 * 		initial list of resources atached to this scenery.
	 */
	public List<ProcessingAction> startProfitSearch() {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessorV2.startProfitSearch]");
		// Test the possibility to produce Tier 4 resources.
		for (Integer targetIdentifier : PlanetaryProcessor.t4ProductList.keySet()) {
			// Collect the resources that should be required. This action is recursive up to the Tier 1.
			// Tier 4.
			final List<Resource> requirementsTier3 = addProductsRequired(targetIdentifier);
			// Tier 3.
			final List<Resource> requirementsTier2 = new ArrayList<>();
			for (Resource resource : requirementsTier3) {
				requirementsTier2.addAll(addProductsRequired(resource.getTypeId()));
			}
			// Tier 2.
			final List<Resource> requirementsTier1 = new ArrayList();
			for (Resource resource : requirementsTier2) {
				requirementsTier1.addAll(addProductsRequired(resource.getTypeId()));
			}

			// Collect the list of resources into the scenery input resources.
			contextResources.clear();
			for (Resource res : scenery.getResources()) {
				for (Resource rt : requirementsTier3) {
					if (rt.getTypeId() == res.getTypeId()) {
						if (!checkDuplicate(res.getTypeId(), contextResources))
							// Create a resource copy for the production evaluation.
							contextResources.add(new PlanetaryResource(res.getTypeId(), res.getQuantity()));
					}
				}
				for (Resource rt : requirementsTier2) {
					if (rt.getTypeId() == res.getTypeId()) {
						if (!checkDuplicate(res.getTypeId(), contextResources))
							// Create a resource copy for the production evaluation.
							contextResources.add(new PlanetaryResource(res.getTypeId(), res.getQuantity()));
					}
				}
				for (Resource rt : requirementsTier1) {
					if (rt.getTypeId() == res.getTypeId()) {
						if (!checkDuplicate(res.getTypeId(), contextResources))
							// Create a resource copy for the production evaluation.
							contextResources.add(new PlanetaryResource(res.getTypeId(), res.getQuantity()));
					}
				}
			}

			// Run the whole production tree and get the number of cycles for the Tier target.
			// Process Tier 2 resources and add them to the list of context resources.
			BitSequencer t2sequence = new BitSequencer(requirementsTier2);
			t2sequence.setResources(contextResources);
			List<ProcessingAction> next2Sequence = t2sequence.maxSequence();
			List<Resource> new2Resources = this.processActions(next2Sequence, contextResources);

			// Process Tier 3 resources and add them to the list of context resources.
			BitSequencer t3sequence = new BitSequencer(PlanetaryProcessor.t3ProductList);
			t2sequence.setResources(new2Resources);
			List<ProcessingAction> next3Sequence = t3sequence.maxSequence();
			List<Resource> new3Resources = this.processActions(next3Sequence, new2Resources);

			// Get the cycles for the Target Tier 4.
			ProcessingAction action = new ProcessingAction(targetIdentifier);
			// Get the input resources from the Scenery if available.
			for (Schematics input : action.getInputs()) {
				action.addResource(searchResource(input.getTypeId(), new3Resources));
			}
			int cycles = action.getPossibleCycles();
			logger.info("-- [PlanetaryProcessorV2.startProfitSearch]> Tier 4 [Action: {} - cycles: {}]"
					, action, cycles);


		}
		try {
			// Remove resources until the multiplier is below the limit.
			long multiplier = calculateIterations();
			while (multiplier > ITERATIONS_LIMIT) {
				// Remove a candidate resource from the initial list on the scenery.
				scenery.removeLowestResource();
				multiplier = calculateIterations();
			}

			// Start the recursion calculating process.
			return startProfitSearch(null);
		} finally {
			PlanetaryProcessor.logger.info("<< [PlanetaryProcessor.startProfitSearch]> Iteration ZERO");
		}
	}

	protected List<Resource> addProductsRequired( final int outputIdentifier ) {
		List<Resource> productionResources = new Vector();
		final List<Schematics> schematics = accessSDEDBHelper().searchSchematics4Output(outputIdentifier);
		for (final Schematics sche : schematics) {
			if (sche.getDirection() == SchematicDirection.INPUT) {
				productionResources.add(new PlanetaryResource(sche.getTypeId()));
			}
			//		if (sche.getDirection() == Schematics.ESchematicDirection.OUTPUT) {
			//			productionResources.add(sche);
			//		}
		}
		return productionResources;
	}

	protected Resource searchResource( final int targetIdentifier, final List<Resource> availableResources ) {
		for (Resource res : availableResources) {
			if (targetIdentifier == res.getTypeId()) return res;
		}
		return new PlanetaryResource(targetIdentifier); // Resource not found. Return a stack with ZERO quantity.
	}

	protected boolean checkDuplicate( final int typeId, final List<Resource> targetList ) {
		//		boolean found = false;
		for (Resource res : targetList) {
			if (res.getTypeId() == typeId) return true;
		}
		return false;
	}
}
