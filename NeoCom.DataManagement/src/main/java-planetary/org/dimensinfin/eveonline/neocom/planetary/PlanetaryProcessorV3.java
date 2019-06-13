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
	private static final int Tier4ResourcesByCycle = 1;
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
	public PlanetaryScenery startProfitSearchNew() {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessorV3.startProfitSearch]");

		// Do the processing until there is no more candidates that generate action processing.
		boolean continueProcessing = true;
		while (continueProcessing) {
			// Clear the processing flag. If during the loops we do not found any target we can exit the process.
			continueProcessing = false;
			// Start processing the possibility to get Tier 4 products.
			List<ProcessStorage> storages = new ArrayList<>();
			for (Integer typeId : t4ProductList.keySet()) {
				// Use an special data storage to control the possibility to process this resource.
				final ProcessStorage processStorage = new ProcessStorage(typeId, scenery);
				final List<Resource> requirementsTier3 = addProductsRequired(typeId);
				// Tier 3 needs 6 resources to initiate a cycle.
				for (Resource res3 : requirementsTier3) {
					// Calculate the requirements for this resource depending on its target availability and current availabilities.
					// We calculate a 100 units Tier 4 to have simple maths when going down.
					final int TIER4_MULTIPLIER = 100;
					final int tier3Required = Tier3Resources4OneUnit * TIER4_MULTIPLIER;
//					final int tier4Available = scenery.getResource(typeId).getQuantity();
					final int tier3RequestQuantity = tier3Required;
					final int tier3Available = scenery.getResource(res3.getTypeId()).getQuantity();
					final int tier3ToBuild = tier3RequestQuantity - tier3Available;
					// Update request data.
					processStorage.setRequestQuantity(TIER4_MULTIPLIER);

					// Generate a request only if we have less than required.
					if (tier3Available < tier3RequestQuantity) {
						// Store the Tier 3 accounting data into a new ResourceStorage.
						processStorage.addRequirement(res3.getTypeId(), tier3RequestQuantity);
						processStorage.addAvailable(res3.getTypeId(), scenery.getResource(res3.getTypeId()).getQuantity());
						processStorage.addToBuild(res3.getTypeId(), tier3ToBuild);

						// Do the same operation with this Tier 3 and generate the requirements from Tier 2.
						final List<Resource> requirementsTier2 = addProductsRequired(res3.getTypeId());
						for (Resource res2 : requirementsTier2) {
							final int tier2Required = tier3RequestQuantity * Tier2Resources4OneUnit;
							final int tier2RequestQuantity = tier2Required - (tier3Available * Tier2Resources4OneUnit);
							final int tier2Available = scenery.getResource(res2.getTypeId()).getQuantity();
							final int tier2ToBuild = tier2RequestQuantity - tier2Available;

							// Generate a request only if we have less than required.
							if (tier2Available < tier2RequestQuantity) {
								// Store the Tier 2 accounting data into a new ResourceStorage.
								processStorage.addRequirement(res2.getTypeId(), tier2RequestQuantity);
								processStorage.addAvailable(res2.getTypeId(), scenery.getResource(res2.getTypeId()).getQuantity());
								processStorage.addToBuild(res2.getTypeId(), tier2ToBuild);

								// Do the same operation with this Tier 2 and generate the requirements from Tier 1.
								final List<Resource> requirementsTier1 = addProductsRequired(res2.getTypeId());
								for (Resource res1 : requirementsTier1) {
									final int tier1Required = tier2RequestQuantity * Tier1Resources4OneUnit;
									final int tier1RequestQuantity = tier1Required - (tier2Available * Tier1Resources4OneUnit);
									final int tier1Available = scenery.getResource(res1.getTypeId()).getQuantity();
									final int tier1ToBuild = tier1RequestQuantity - tier1Available;

									// Generate a request only if we have less than required.
									if (tier1Available < tier1RequestQuantity) {
										// Store the Tier 2 accounting data into a new ResourceStorage.
										processStorage.addRequirement(res1.getTypeId(), tier1RequestQuantity);
										processStorage.addAvailable(res1.getTypeId(), scenery.getResource(res1.getTypeId()).getQuantity());
										processStorage.addToBuild(res1.getTypeId(), tier1ToBuild);
									}
								}
							}
						}
					}
				}
				// Now the process storage has all the information about the need to build 100 Tier 4 and the available resources.
				// With this we can calculate the Tier 4 cycle cut and reduce it from 100 to the real level.
				// That level is the less percentage for Tier 1 resources. If this value is greater than ZERO.
				// If ZERO we can discard this target because we can't build any run.
				final double coverage = processStorage.getMinimumT1Coverage();
				PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> Resource: {} coverage: {}"
						, typeId, coverage);
				if (coverage > 1) storages.add(processStorage);
			}
			// At this point we have the list of Tier 4 targets that have any result.
			// Get the one with more runs as the success product and process its construction calculating the total coverage run.
			ProcessStorage target = null;
			double coverage = 0.0;
			for (ProcessStorage sto : storages) {
				final double currentCoverage = sto.getMinimumCoverage();
				if (currentCoverage > 0.0) {
					if (null == target) {
						target = sto;
						coverage = sto.getMinimumCoverage();
						continue;
					}
					if (sto.getMinimumCoverage() > coverage) {
						target = sto;
						coverage = sto.getMinimumCoverage();
					}
				}
			}
			// Target will now have the best candidate and the coverage the number of cycles to process. Or null if no target found.
			if (null != target) {
				PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> Located candidate: {}"
						, target.targetTypeId);
				// Perform the action processing and update the scenario.
				final int targetQuantity = Double.valueOf(Math.round(target.quantity * coverage / 100.0)).intValue();
				if (targetQuantity > 0) {
					performActions("TIER4", target.targetTypeId, targetQuantity);
					continueProcessing = true;
				}
			} else {
				PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> No more TIER 4 candidates. Searching Tier " +
						"3");
				// No targets found at Tier 4. Do the same for the Tier 3 as targets.
				storages = new ArrayList<>();
				for (Integer typeId : t3ProductList.keySet()) {
					// Use an special data storage to control the possibility to process this resource.
					final ProcessStorage processStorage = new ProcessStorage(typeId, scenery);
					final List<Resource> requirementsTier2 = addProductsRequired(typeId);
					// Tier 2 needs 10 resources to initiate a cycle.
					for (Resource res2 : requirementsTier2) {
						// Calculate the requirements for this resource depending on its target availability and current availabilities.
						// We calculate a 100 units Tier 3 to have simple maths when going down.
						final int TIER3_MULTIPLIER = 999;
						final int tier2Required = Tier2Resources4OneUnit * TIER3_MULTIPLIER / Tier3ResourcesByCycle;
//						final int tier3Available = scenery.getResource(typeId).getQuantity();
						final int tier2RequestQuantity = tier2Required;
						final int tier2Available = scenery.getResource(res2.getTypeId()).getQuantity();
						final int tier2ToBuild = tier2RequestQuantity - tier2Available;
						// Update request data.
						processStorage.setRequestQuantity(TIER3_MULTIPLIER);

						// Generate a request only if we have less than required.
						if (tier2Available < tier2RequestQuantity) {
							// Store the Tier 2 accounting data into a new ResourceStorage.
							processStorage.addRequirement(res2.getTypeId(), tier2RequestQuantity);
							processStorage.addAvailable(res2.getTypeId(), scenery.getResource(res2.getTypeId()).getQuantity());
							processStorage.addToBuild(res2.getTypeId(), tier2ToBuild);

							// Do the same operation with this Tier 2 and generate the requirements from Tier 1.
							final List<Resource> requirementsTier1 = addProductsRequired(res2.getTypeId());
							for (Resource res1 : requirementsTier1) {
								final int tier1Required = tier2RequestQuantity * Tier1Resources4OneUnit;
								final int tier1RequestQuantity = tier1Required - (tier2Available * Tier1Resources4OneUnit);
								final int tier1Available = scenery.getResource(res1.getTypeId()).getQuantity();
								final int tier1ToBuild = tier1RequestQuantity - tier1Available;

								// Generate a request only if we have less than required.
								if (tier1Available < tier1RequestQuantity) {
									// Store the Tier 2 accounting data into a new ResourceStorage.
									processStorage.addRequirement(res1.getTypeId(), tier1RequestQuantity);
									processStorage.addAvailable(res1.getTypeId(), scenery.getResource(res1.getTypeId()).getQuantity());
									processStorage.addToBuild(res1.getTypeId(), tier1ToBuild);
								}
							}
						}
					}
					// Now the process storage has all the information about the need to build 100 Tier 3 and the available resources.
					// With this we can calculate the Tier 3 cycle cut and reduce it from 100 to the real level.
					// That level is the less percentage for Tier 1 resources. If this value is greater than ZERO.
					// If ZERO we can discard this target because we can't build any run.
					coverage = processStorage.getMinimumT1Coverage();
					PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> Resource: {} coverage: {}"
							, typeId, coverage);
					if (coverage > 1) storages.add(processStorage);
				}
				// At this point we have the list of Tier 3 targets that have any coverage.
				// Get the one with more runs as the success product and process its construction calculating the total coverage run.
				target = null;
				coverage = 0.0;
				for (ProcessStorage sto : storages) {
					final double currentCoverage = sto.getMinimumCoverage();
					if (currentCoverage > 0.0) {
						if (null == target) {
							target = sto;
							coverage = sto.getMinimumCoverage();
							continue;
						}
						if (sto.getMinimumCoverage() > coverage) {
							target = sto;
							coverage = sto.getMinimumCoverage();
						}
					}
				}
				// Target will now have the best candidate and the coverage the number of cycles to process. Or null if no target found.
				if (null != target) {
					// Perform the action processing and update the scenario.
					PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> Located candidate: {}"
							, target.targetTypeId);
					final int targetQuantity = Double.valueOf(Math.round(target.quantity * coverage / 100.0)).intValue();
					if (targetQuantity > 0) {
						performActions("TIER3", target.targetTypeId, targetQuantity);
						continueProcessing = true;
					}
				} else {
					PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> No more TIER 3 candidates. Searching " +
							"Tier " +
							"2");
					// No targets found at Tier 3. Do the same for the Tier 2 as targets.
					storages = new ArrayList<>();
					for (Integer typeId : t2ProductList.keySet()) {
						// Use an special data storage to control the possibility to process this resource.
						final ProcessStorage processStorage = new ProcessStorage(typeId, scenery);
						final List<Resource> requirementsTier1 = addProductsRequired(typeId);
						// Tier 1 needs 20 resources to initiate a cycle.
						for (Resource res1 : requirementsTier1) {
							// Calculate the requirements for this resource depending on its target availability and current availabilities.
							// We calculate a 10000 units Tier 2 to have simple maths when going down.
							final int TIER2_MULTIPLIER = 10000;
							final int tier1Required = Tier1Resources4OneUnit * TIER2_MULTIPLIER / Tier2ResourcesByCycle;
//							final int tier2Available = scenery.getResource(typeId).getQuantity();
							final int tier1RequestQuantity = tier1Required;
							final int tier1Available = scenery.getResource(res1.getTypeId()).getQuantity();
							final int tier1ToBuild = tier1RequestQuantity - tier1Available;
							// Update request data.
							processStorage.setRequestQuantity(TIER2_MULTIPLIER);

							// Generate a request only if we have less than required.
							if (tier1Available < tier1RequestQuantity) {
								// Store the Tier 2 accounting data into a new ResourceStorage.
								processStorage.addRequirement(res1.getTypeId(), tier1RequestQuantity);
								processStorage.addAvailable(res1.getTypeId(), scenery.getResource(res1.getTypeId()).getQuantity());
								processStorage.addToBuild(res1.getTypeId(), tier1ToBuild);
							}

						}
						// Now the process storage has all the information about the need to build 100 Tier 2 and the available resources.
						// With this we can calculate the Tier 2 cycle cut and reduce it from 1000 to the real level.
						// That level is the less percentage for Tier 1 resources. If this value is greater than ZERO.
						// If ZERO we can discard this target because we can't build any run.
						coverage = processStorage.getMinimumT1Coverage();
						PlanetaryProcessor.logger.info("-- [PlanetaryProcessorV3.startProfitSearch]> Resource: {} coverage: {}"
								, typeId, coverage);
						if (coverage > 1) storages.add(processStorage);
					}
					// At this point we have the list of Tier 3 targets that have any coverage.
					// Get the one with more runs as the success product and process its construction calculating the total coverage run.
					target = null;
					coverage = 0.0;
					for (ProcessStorage sto : storages) {
						final double currentCoverage = sto.getMinimumCoverage();
						if (currentCoverage > 0.0) {
							if (null == target) {
								target = sto;
								coverage = sto.getMinimumCoverage();
								continue;
							}
							if (sto.getMinimumCoverage() > coverage) {
								target = sto;
								coverage = sto.getMinimumCoverage();
							}
						}
					}
					// Target will now have the best candidate and the coverage the number of cycles to process. Or null if no target found.
					if (null != target) {
						// Perform the action processing and update the scenario.
						final int targetQuantity = Double.valueOf(Math.round(target.quantity * coverage / 100.0)).intValue();
						if (targetQuantity > 0) {
							performActions("TIER2", target.targetTypeId, targetQuantity);
							continueProcessing = true;
						}
					}
				}
			}
		}
		PlanetaryProcessor.logger.info("<< [PlanetaryProcessorV3.startProfitSearch]");
		return scenery;
	}

	/**
	 * Generate the transformation action to generate the number of resources for the target. This means that we should run the
	 * whole production hierarchy doing the partial cycles until we have the resources needed to complete this main action. All
	 * that activities will be recorded at the scenario and at the same time it will update the accounting of the resources there
	 * so next oprimization cycles will start after the resources resulting from the completion of this task.
	 * @param tier
	 * @param typeId   top tier for the target.
	 * @param quantity number of resources of type target to generate.
	 * @return
	 */
	protected boolean performActions( final String tier, final int typeId, final int quantity ) {
		PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> [{}] {} x{}"
				, tier, typeId, quantity);
		if (quantity > 0) {
			// Do the processing depending on the Tier.
			if (tier == "TIER2") {
				final ProcessingActionV2 action = new ProcessingActionV2(typeId)
						.setCycles(quantity);
				// Get the input resources from the Scenery if available.
				for (Schematics input : action.getInputs()) {
					action.addResource(scenery.getResource(input.getTypeId()));
				}
				final List<Resource> results = action.getLimitedActionResults();
				// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
				scenery.stock(results);
				scenery.addAction(action);
				return true;
			}
			if (tier == "TIER3") {
				// First do the processing for the sources from Tier 2.
				final List<Resource> requirementsTier2 = addProductsRequired(typeId);
				for (Resource res2 : requirementsTier2) {
					// Calculate the requirements for this resource depending on current availabilities.
					final int tier2Required = quantity * Tier2Resources4OneUnit / Tier3ResourcesByCycle;
					final int tier2RequestQuantity = tier2Required;
					final int tier2Available = scenery.getResource(res2.getTypeId()).getQuantity();
					final int tier2ToBuild = tier2RequestQuantity - tier2Available;

					// Generate a request only if we have less than required.
					if (tier2Available < tier2RequestQuantity) {
						final ProcessingActionV2 action = new ProcessingActionV2(res2.getTypeId())
								.setCycles(tier2ToBuild);
						// Get the input resources from the Scenery if available.
						for (Schematics input : action.getInputs()) {
							action.addResource(scenery.getResource(input.getTypeId()));
						}
						final List<Resource> results = action.getLimitedActionResults();
						// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
						PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> Action resources: ", results.toString());
						scenery.stock(results);
						scenery.addAction(action);
					}
				}
				// Do the processing for the Tier 3 resource.
				final ProcessingActionV2 action = new ProcessingActionV2(typeId)
						.setCycles(quantity);
				// Get the input resources from the Scenery if available.
				for (Schematics input : action.getInputs()) {
					action.addResource(scenery.getResource(input.getTypeId()));
				}
				final List<Resource> results = action.getLimitedActionResults();
				// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
				PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> Action resources: ", results.toString());
				scenery.stock(results);
				scenery.addAction(action);
				return true;
			}
			if (tier == "TIER4") {
				// First do the processing for the sources from Tier 3.
				final List<Resource> requirementsTier3 = addProductsRequired(typeId);
				for (Resource res3 : requirementsTier3) {
					// Calculate the requirements for this resource depending on current availabilities.
					final int tier3Required = quantity * Tier3Resources4OneUnit;
					final int tier3RequestQuantity = tier3Required;
					final int tier3Available = scenery.getResource(res3.getTypeId()).getQuantity();
					final int tier3ToBuild = tier3RequestQuantity - tier3Available;

					// Generate a request only if we have less than required.
					if (tier3Available < tier3RequestQuantity) {
						final List<Resource> requirementsTier2 = addProductsRequired(res3.getTypeId());
						for (Resource res2 : requirementsTier2) {
							// Calculate the requirements for this resource depending on current availabilities.
							final int tier2Required = tier3ToBuild * Tier2Resources4OneUnit / Tier3ResourcesByCycle;
							final int tier2RequestQuantity = tier2Required;
							final int tier2Available = scenery.getResource(res2.getTypeId()).getQuantity();
							final int tier2ToBuild = tier2RequestQuantity - tier2Available;

							// Generate a request only if we have less than required.
							if (tier2Available < tier2RequestQuantity) {
								final ProcessingActionV2 action = new ProcessingActionV2(res2.getTypeId())
										.setCycles(tier2ToBuild);
								// Get the input resources from the Scenery if available.
								for (Schematics input : action.getInputs()) {
									action.addResource(scenery.getResource(input.getTypeId()));
								}
								final List<Resource> results = action.getLimitedActionResults();
								// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
								PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> Action resources: ", results.toString());
								scenery.stock(results);
								scenery.addAction(action);
							}
						}
						final ProcessingActionV2 action = new ProcessingActionV2(res3.getTypeId())
								.setCycles(tier3ToBuild);
						// Get the input resources from the Scenery if available.
						for (Schematics input : action.getInputs()) {
							action.addResource(scenery.getResource(input.getTypeId()));
						}
						final List<Resource> results = action.getLimitedActionResults();
						// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
						PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> Action resources: ", results.toString());
						scenery.stock(results);
						scenery.addAction(action);
					}
				}

				// Do the processing for the Tier 4 resource.
				final ProcessingActionV2 action = new ProcessingActionV2(typeId)
						.setCycles(quantity);
				// Get the input resources from the Scenery if available.
				for (Schematics input : action.getInputs()) {
					action.addResource(scenery.getResource(input.getTypeId()));
				}
				final List<Resource> results = action.getLimitedActionResults();
				// Update the scenery data with this list by adding the resulting resources. Consumed have already been made negative.
				PlanetaryProcessor.logger.info(">< [PlanetaryProcessorV3.performActions]> Action resources: ", results.toString());
				scenery.stock(results);
				scenery.addAction(action);
				return true;
			}
		}
		return false;
	}

	// --- D E L E G A T E D   M E T H O D S
	public static class ProcessStorage {
		//		private static final int INITIAL_T4_MULTIPLIER = 100;
		private final int targetTypeId;
		private int quantity = 1;
		private final PlanetaryScenery scenery;
		private Map<Integer, ResourceStorage> resources = new HashMap();

		public ProcessStorage( final int typeId, final PlanetaryScenery scenery ) {
			this.targetTypeId = typeId;
			this.scenery = scenery;
		}

		public ProcessStorage setRequestQuantity( final int requestQuantity ) {
			this.quantity = requestQuantity;
			return this;
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

		public void addToBuild( final int typeId, final int quantity ) {
			ResourceStorage hit = resources.get(typeId);
			if (null == hit) {
				// Create the storage for a new resource.
				hit = new ResourceStorage(typeId);
				resources.put(typeId, hit);
			}
			hit.addToBuild(new PlanetaryResource(typeId, quantity));
		}

		public double getMinimumT1Coverage() {
			double minCoverage = 100;
			for (ResourceStorage sto : resources.values()) {
				if (sto.getTier() == "TIER1")
					if (sto.getCoverage() < minCoverage) minCoverage = sto.getCoverage();
			}
			return minCoverage;
		}

		public double getMinimumCoverage() {
			double minCoverage = 100;
			for (ResourceStorage sto : resources.values()) {
				if (sto.getCoverage() < minCoverage) minCoverage = sto.getCoverage();
			}
			return minCoverage;
		}

//
//		/**
//		 * Add the number of available resources for the target planetary resource that can be generated with this resource. The
//		 * use is to reduce the number of the stack multiplier on that quantity since the resource to be generated is already
//		 * available.
//		 * @param quantity the number of target resources available already as output.
//		 */
//		public void addTargetAvailable( final int typeId, final int quantity ) {
//			ResourceStorage hit = resources.get(typeId);
//			if (null == hit) {
//				// Create the storage for a new resource.
//				hit = new ResourceStorage(typeId);
//				resources.put(typeId, hit);
//			}
//			hit.addTargetAvailable(quantity);
//		}
	}

	public static class ResourceStorage {
		private final int typeId;
		private int targetAvailable = 0;
		private PlanetaryResource requirement = null;
		private PlanetaryResource available = null;
		private PlanetaryResource toBuild = null;

		public ResourceStorage( final int typeId ) {
			this.typeId = typeId;
		}

		public void addRequirement( final PlanetaryResource resource ) {
			requirement = resource;
			// Update the target availability.
//			requirement.setStackSize(requirement.getStackSize() - targetAvailable);
		}

		public void addAvailable( final PlanetaryResource resource ) {
			available = resource;
		}

		public void addToBuild( final PlanetaryResource resource ) {
			toBuild = resource;
		}

		public double getCoverage() {
			return Double.valueOf(available.getQuantity()) / Double.valueOf(toBuild.getQuantity()) * 100.0;
		}

		public String getTier() {
			return requirement.getTier().name();
		}

		//		public void addTargetAvailable( final int quantity ) {
//			this.targetAvailable = quantity;
//			// Update the target availability.
//			if (null != requirement)
//				requirement.setStackSize(requirement.getStackSize() - targetAvailable);
//		}
//
		@Override
		public String toString() {
			return new StringBuffer("ResourceStorage [ ")
					.append("[").append(requirement.getTier()).append("]").append(requirement.getName()).append(" ")
					.append("required: ").append(requirement.getQuantity()).append(" ")
					.append("available: ").append(available.getQuantity()).append(" ")
					.append("toBuild: ").append(toBuild.getQuantity()).append(" ")
					.append("coverage: [").append(getCoverage()).append("] ")
					.append("]")
//				.append("->").append(super.toString())
					.toString();
		}
	}
}

// - UNUSED CODE ............................................................................................
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
