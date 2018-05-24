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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.industry.Resource;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessorV3 extends PlanetaryProcessorV2 {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("PlanetaryProcessorV3");
	private static final int Tier3Resources4OneUnit = 6;
	private static final int Tier3ResourcesByCycle = 3;
	private static final int Tier2Resources4OneUnit = 10;
	private static final int Tier2ResourcesByCycle = 5;
	private static final int Tier1Resources4OneUnit = 40;
	private static final int Tier1ResourcesByCycle = 20;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryProcessorV3( final PlanetaryScenery scenery ) {
		super(scenery);
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * The new method implementation will use another algorithm to obtain profitable planetary resource processing. Instead
	 * testing all the possible combinations this method will start with the Tier 4 elements and try to produce the most
	 * profitable. Once found a valid target it will execute the actions for processing the right number of resources and then
	 * update the pool of resources subtracting and adding the actions results.
	 *
	 * Once completed one product it will continue iteration with the next and so until all the Tier 4, Tier 3 and Tier 2
	 * planetary resources are checked.
	 *
	 * During the processing we should remove from the needs calculations all the already available resources.
	 * @return
	 */
	public List<ProcessStorage> startProfitSearchNew() {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessorV3.startProfitSearch]");
		final List<ProcessStorage> storages = new ArrayList<>();
		// Start processing the possibility to get Tier 4 products.
		for (Integer typeId : t4ProductList.keySet()) {
			// Use an special data storage to control the possibility to process this resource.
			final ProcessStorage processStorage = new ProcessStorage(typeId, scenery);
			storages.add(processStorage);
			final List<Resource> requirementsTier3 = addProductsRequired(typeId);
			// Tier 3 need 6 resources to initiate a cycle.
			for (Resource res3 : requirementsTier3) {
				// Calculate the requirements for this resource depending on its target availability and current availabilities.
				// We calculate a 100 units Tier 4 to have simple maths when going down.
				final int TIER4_MULTIPLIER = 100;
				final int tier3Required = Tier3Resources4OneUnit * TIER4_MULTIPLIER;
				final int tier4Available = scenery.getResource(typeId).getQuantity();
				final int tier3RequestQuantity = tier3Required - (tier4Available * Tier3ResourcesByCycle);
				final int tier3Available = scenery.getResource(res3.getTypeId()).getQuantity();

				// Generate a request only if we have less than required.
				if (tier3Available < tier3RequestQuantity) {
					// Store the Tier 3 accounting data into a new ResourceStorage.
					processStorage.addRequirement(res3.getTypeId(), tier3RequestQuantity);
					processStorage.addAvailable(res3.getTypeId(), scenery.getResource(res3.getTypeId()).getQuantity());

					// Do the same operation with this Tier 3 and generate the requirements from Tier 2.
					final List<Resource> requirementsTier2 = addProductsRequired(res3.getTypeId());
					for (Resource res2 : requirementsTier2) {
						final int tier2Required = tier3RequestQuantity * Tier2Resources4OneUnit;
						final int tier2RequestQuantity = tier2Required - (tier3Available * Tier2ResourcesByCycle);
						final int tier2Available = scenery.getResource(res2.getTypeId()).getQuantity();

						// Generate a request only if we have less than required.
						if (tier2Available < tier2RequestQuantity) {
							// Store the Tier 2 accounting data into a new ResourceStorage.
							processStorage.addRequirement(res2.getTypeId(), tier2RequestQuantity);
							processStorage.addAvailable(res2.getTypeId(), scenery.getResource(res2.getTypeId()).getQuantity());

							// Do the same operation with this Tier 2 and generate the requirements from Tier 1.
							final List<Resource> requirementsTier1 = addProductsRequired(res2.getTypeId());
							for (Resource res1 : requirementsTier1) {
								final int tier1Required = tier2RequestQuantity * Tier1Resources4OneUnit;
								final int tier1RequestQuantity = tier1Required - (tier2Available * Tier1ResourcesByCycle);
								final int tier1Available = scenery.getResource(res1.getTypeId()).getQuantity();

								// Generate a request only if we have less than required.
								if (tier1Available < tier1RequestQuantity) {
									// Store the Tier 2 accounting data into a new ResourceStorage.
									processStorage.addRequirement(res1.getTypeId(), tier1RequestQuantity);
									processStorage.addAvailable(res1.getTypeId(), scenery.getResource(res1.getTypeId()).getQuantity());
									//[01]
								}
							}
						}
					}
				}
			}
			// Now the process storage hass all the information about the need to build 100 Tier 4 and the available resources.
			// With this we can calculate the Tier 4 cycle cut and reduce it from 100 to the real level.

		}
//[01]
		//									processStorage.addTargetAvailable(res3.getTypeId(), scenery.getResource(typeId).getQuantity());
//
//									// Do the same searching for input for this Tier 3 resources.
//									// Calculate the requirements for this resource depending on its target availability.
//									final int required = 10 * 100;
//									final int currentAvailable = scenery.getResource(typeId).getQuantity());
//									// Generate a request only if we have less that required.
//									if (currentAvailable < required) {
//										processStorage.addTargetAvailable(res2.getTypeId(), scenery.getResource(res3.getTypeId()).getQuantity());
//										processStorage.addRequirement(res2.getTypeId(), 10);
//										available = scenery.getResource(res2.getTypeId());
//										processStorage.addAvailable(res2.getTypeId(), available.getQuantity());
//
//										// And again for the resulting Tier 2 resources.
//										final List<Resource> requirementsTier1 = addProductsRequired(res2.getTypeId());
//										for (Resource res1 : requirementsTier1) {
//											processStorage.addTargetAvailable(res1.getTypeId(), scenery.getResource(res2.getTypeId()).getQuantity());
//											processStorage.addRequirement(res1.getTypeId(), 40);
//											available = scenery.getResource(res1.getTypeId());
//											processStorage.addAvailable(res1.getTypeId(), available.getQuantity());
//										}
//									}


		PlanetaryProcessor.logger.info("<< [PlanetaryProcessorV3.startProfitSearch]");
		return storages;
	}

	// --- D E L E G A T E D   M E T H O D S
	public static class ProcessStorage {
		//		private static final int INITIAL_T4_MULTIPLIER = 100;
		private final int targetTypeId;
		private final PlanetaryScenery scenery;
		private Map<Integer, ResourceStorage> resources = new HashMap();

		public ProcessStorage( final int typeId, final PlanetaryScenery scenery ) {
			this.targetTypeId = typeId;
			this.scenery = scenery;
		}

		public void addRequirement( final int typeId, final int requestQuantity ) {
			ResourceStorage hit = resources.get(typeId);
			if (null == hit) {
				// Create the storage for a new resource.
				hit = new ResourceStorage(typeId);
				resources.put(typeId, hit);
			}
			hit.addRequirement(new PlanetaryResource(typeId, requestQuantity));
		}

		public void addAvailable( final int typeId, final int quantity ) {
			ResourceStorage hit = resources.get(typeId);
			if (null == hit) {
				// Create the storage for a new resource.
				hit = new ResourceStorage(typeId);
				resources.put(typeId, hit);
			}
			hit.addAvailable(new PlanetaryResource(typeId, quantity));
		}

		/**
		 * Add the number of available resources for the target planetary resource that can be generated with this resource. The
		 * use is to reduce the number of the stack multiplier on that quantity since the resource to be generated is already
		 * available.
		 * @param quantity the number of target resources available already as output.
		 */
		public void addTargetAvailable( final int typeId, final int quantity ) {
			ResourceStorage hit = resources.get(typeId);
			if (null == hit) {
				// Create the storage for a new resource.
				hit = new ResourceStorage(typeId);
				resources.put(typeId, hit);
			}
			hit.addTargetAvailable(quantity);
		}
	}

	public static class ResourceStorage {
		private final int typeId;
		private int targetAvailable = 0;
		private PlanetaryResource requirement = null;
		private PlanetaryResource available = null;

		public ResourceStorage( final int typeId ) {
			this.typeId = typeId;
		}

		public void addRequirement( final PlanetaryResource resource ) {
			requirement = resource;
			// Update the target availability.
			requirement.setStackSize(requirement.getStackSize() - targetAvailable);
		}

		public void addAvailable( final PlanetaryResource resource ) {
			available = resource;
		}

		public void addTargetAvailable( final int quantity ) {
			this.targetAvailable = quantity;
			// Update the target availability.
			if (null != requirement)
				requirement.setStackSize(requirement.getStackSize() - targetAvailable);
		}

		@Override
		public String toString() {
			return new StringBuffer("ResourceStorage [ ")
					.append("[").append(requirement.getTier()).append("]").append(requirement.getName()).append(" ")
					.append("required: ").append(requirement.getQuantity()).append(" ")
					.append("available: ").append(available.getQuantity()).append(" ")
					.append("]")
//				.append("->").append(super.toString())
					.toString();
		}
	}
}

// - UNUSED CODE ............................................................................................
//[01]