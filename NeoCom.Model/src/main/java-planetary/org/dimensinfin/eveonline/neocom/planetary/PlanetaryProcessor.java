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
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

/**
 * This class performs the Planetary transformations and the value calculation in a recursive way. Starting
 * from an specific configuration the methods scan the different combinations possible for Tier 3, 4 and 5 in
 * sequeence and returns the better profit Scenario depending on the current market data buyers prices.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessor extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	protected static Logger logger = LoggerFactory.getLogger("PlanetaryProcessor");
	protected static HashMap<Integer, String> t2ProductList = new HashMap<Integer, String>();

	// Refined Commodities
	static {
		PlanetaryProcessor.t2ProductList.put(2329, "Biocells");
		PlanetaryProcessor.t2ProductList.put(3828, "Construction Blocks");
		PlanetaryProcessor.t2ProductList.put(9836, "Consumer Electronics");
		PlanetaryProcessor.t2ProductList.put(9832, "Coolant");
		PlanetaryProcessor.t2ProductList.put(44, "Enriched Uranium");
		PlanetaryProcessor.t2ProductList.put(3693, "Fertilizer");
		PlanetaryProcessor.t2ProductList.put(15317, "Genetically Enhanced Livestock");
		PlanetaryProcessor.t2ProductList.put(3725, "Livestock");
		PlanetaryProcessor.t2ProductList.put(3689, "Mechanical Parts");
		PlanetaryProcessor.t2ProductList.put(2327, "Microfiber Shielding");
		PlanetaryProcessor.t2ProductList.put(9842, "Miniature Electronics");
		PlanetaryProcessor.t2ProductList.put(2463, "Nanites");
		PlanetaryProcessor.t2ProductList.put(2317, "Oxides");
		PlanetaryProcessor.t2ProductList.put(2321, "Polyaramids");
		PlanetaryProcessor.t2ProductList.put(3695, "Polytextiles");
		PlanetaryProcessor.t2ProductList.put(9830, "Rocket Fuel");
		PlanetaryProcessor.t2ProductList.put(3697, "Silicate Glass");
		PlanetaryProcessor.t2ProductList.put(9838, "Superconductors");
		PlanetaryProcessor.t2ProductList.put(2312, "Supertensile Plastics");
		PlanetaryProcessor.t2ProductList.put(3691, "Synthetic Oil");
		PlanetaryProcessor.t2ProductList.put(2319, "Test Cultures");
		PlanetaryProcessor.t2ProductList.put(9840, "Transmitter");
		PlanetaryProcessor.t2ProductList.put(3775, "Viral Agent");
		PlanetaryProcessor.t2ProductList.put(2328, "Water-Cooled CPU");
	}

	protected static HashMap<Integer, String> t3ProductList = new HashMap<Integer, String>();

	// Specialized Commodities
	static {
		PlanetaryProcessor.t3ProductList.put(2358, "Biotech Research Reports");
		PlanetaryProcessor.t3ProductList.put(2345, "Camera Drones");
		PlanetaryProcessor.t3ProductList.put(2344, "Condensates");
		PlanetaryProcessor.t3ProductList.put(2367, "Cryoprotectant Solution");
		PlanetaryProcessor.t3ProductList.put(17392, "Data Chips");
		PlanetaryProcessor.t3ProductList.put(2348, "Gel-Matrix Biopaste");
		PlanetaryProcessor.t3ProductList.put(9834, "Guidance Systems");
		PlanetaryProcessor.t3ProductList.put(2366, "Hazmat Detection Systems");
		PlanetaryProcessor.t3ProductList.put(2361, "Hermetic Membranes");
		PlanetaryProcessor.t3ProductList.put(17898, "High-Tech Transmitters");
		PlanetaryProcessor.t3ProductList.put(2360, "Industrial Explosives");
		PlanetaryProcessor.t3ProductList.put(2354, "Neocoms");
		PlanetaryProcessor.t3ProductList.put(2352, "Nuclear Reactors");
		PlanetaryProcessor.t3ProductList.put(9846, "Planetary Vehicles");
		PlanetaryProcessor.t3ProductList.put(9848, "Robotics");
		PlanetaryProcessor.t3ProductList.put(2351, "Smartfab Units");
		PlanetaryProcessor.t3ProductList.put(2349, "Supercomputers");
		PlanetaryProcessor.t3ProductList.put(2346, "Synthetic Synapses");
		PlanetaryProcessor.t3ProductList.put(12836, "Transcranial Microcontrollers");
		PlanetaryProcessor.t3ProductList.put(17136, "Ukomi Superconductors");
		PlanetaryProcessor.t3ProductList.put(28974, "Vaccines");
	}

	protected static HashMap<Integer, String> t4ProductList = new HashMap<Integer, String>();

	// Advanced Commodities
	static {
		PlanetaryProcessor.t4ProductList.put(2867, "Broadcast Node");
		PlanetaryProcessor.t4ProductList.put(2868, "Integrity Response Drones");
		PlanetaryProcessor.t4ProductList.put(2869, "Nano-Factory");
		PlanetaryProcessor.t4ProductList.put(2870, "Organic Mortar Applicators");
		PlanetaryProcessor.t4ProductList.put(2871, "Recursive Computing Module");
		PlanetaryProcessor.t4ProductList.put(2872, "Self-Harmonizing Power Core");
		PlanetaryProcessor.t4ProductList.put(2875, "Sterile Conduits");
		PlanetaryProcessor.t4ProductList.put(2876, "Wetware Mainframe");
	}

	protected static int ITERATIONS_LIMIT = accessGlobal().getResourcePropertyInteger("R.planetary.optimizer.iterationslimit");

	// - F I E L D - S E C T I O N ............................................................................
	protected PlanetaryScenery scenery = null;
	private final Vector<ProcessingAction> actions = new Vector<ProcessingAction>();
	private double betterProfit = 0.0;
	private final Vector<ProcessingAction> savedScenery = new Vector<ProcessingAction>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Create a new processor and connect it to an scenery. The creation does nothing important.
	 *
	 * @param scenery
	 */
	public PlanetaryProcessor( final PlanetaryScenery scenery ) {
		this.scenery = scenery;
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Start the recursive process of analyzing the different processing combinations to search for the most Before starting the
	 * recursive processing we should check the number of iterations that should be processed. If this number is more than a
	 * known limit we should start removing less valuable items until the number of iterations will generate a result in a
	 * rational processing time.
	 * <p>
	 * The process gets the list of all possible Tier2 products and searched for their inputs. If found on the list of the
	 * Scenery resources then we run a new action and get a new output. This is done for all the Tier2 products.
	 * <p>
	 * Processing should be done automatically by a background process so when the user interface requests the data we can read a
	 * previous stored optimization result from the database.
	 *
	 * @return
	 */
	public List<ProcessingAction> startProfitSearch() {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessor.startProfitSearch]> Iteration ZERO");
		try {
			// Remove resources until the multiplier is below the limit.
			long multiplier = calculateIterations();

			// If multiplier is higher than the limit use the alternate processing algorithm.
			if (multiplier > ITERATIONS_LIMIT) return new PlanetaryProcessorV2(scenery).startProfitSearch();
//			while (multiplier > ITERATIONS_LIMIT) {
//				// Remove a candidate resource from the initial list on the scenery.
//				scenery.removeLowestResource();
//				multiplier = calculateIterations();
//			}

			// Start the recursion calculating process.
			return startProfitSearch(null);
		} finally {
			PlanetaryProcessor.logger.info("<< [PlanetaryProcessor.startProfitSearch]> Iteration ZERO");
		}
	}

	protected List<ProcessingAction> startProfitSearch( final PlanetaryProcessor currentTarget ) {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessor.startProfitSearch]");
		// If current target is null this is then the first iteration on the search.
		betterProfit = 0.0;
		PlanetaryProcessor.logger
				.info("-- [PlanetaryProcessor.startProfitSearch]> Initial resources: " + scenery.getResources().size());
		// Search for Tier2 optimizations
		// Create the sequencer and start to iterate over it.
		BitSequencer t2sequence = new BitSequencer(PlanetaryProcessor.t2ProductList);
		t2sequence.setResources(scenery.getResources());
		// TODO - High optimization using all resources.
//		while (t2sequence.hasSequence()) {
		List<ProcessingAction> next2Sequence = t2sequence.maxSequence();
		// Update resource list by processing the list of current actions.
		List<Resource> new2Resources = this.processActions(next2Sequence, scenery.getResources());
		//Search for Tier3 optimizations
		BitSequencer t3sequence = new BitSequencer(PlanetaryProcessor.t3ProductList);
		t3sequence.setResources(new2Resources);
		while (t3sequence.hasSequence()) {
			List<ProcessingAction> next3Sequence = t3sequence.nextSequence();
			// Update resource list by processing the list of current actions.
			List<Resource> new3Resources = this.processActions(next3Sequence, new2Resources);
			//Search for Tier3 optimizations
			BitSequencer t4sequence = new BitSequencer(PlanetaryProcessor.t4ProductList);
			t4sequence.setResources(new3Resources);
			double marketValue = 0.0;
			// Before processing the sequences I have to know the number for ZERO there is special code to run.
			if (t4sequence.hasSequence()) {
				while (t4sequence.hasSequence()) {
					List<ProcessingAction> next4Sequence = t4sequence.nextSequence();
//						// Check for termination on the t3 sequencer.
//						Vector<ProcessingAction> data;
//						if(next4Sequence.size()=0)data=t4sequence.nextSequence();
					// Update resource list by processing the list of current actions.
					List<Resource> new4Resources = this.processActions(next4Sequence, new3Resources);
					// Evaluate the market value of the resulting resources.
					marketValue = this.evaluateValue(new4Resources);
					// Check if this is the best configuration and if so save it.
					Vector<ProcessingAction> sceneryActions = new Vector<ProcessingAction>();
					sceneryActions.addAll(next2Sequence);
					sceneryActions.addAll(next3Sequence);
					sceneryActions.addAll(next4Sequence);
					this.saveBetterConfiguration(marketValue, sceneryActions);
				}
			} else {
				marketValue = this.evaluateValue(new3Resources);
				// Check if this is the best configuration and if so save it.
				Vector<ProcessingAction> sceneryActions = new Vector<ProcessingAction>();
				sceneryActions.addAll(next2Sequence);
				sceneryActions.addAll(next3Sequence);
				this.saveBetterConfiguration(marketValue, sceneryActions);
			}
		}
//		}
		PlanetaryProcessor.logger.info("<< [PlanetaryProcessor.startProfitSearch]");
		return savedScenery;
	}

	protected long calculateIterations() {
		// Generate the iterations counters for all the tiers.
		BitSequencer t2sequence = new BitSequencer(PlanetaryProcessor.t2ProductList);
		long t2Multiplier = t2sequence.setResources(scenery.getResources());
		BitSequencer t3sequence = new BitSequencer(PlanetaryProcessor.t3ProductList);
		long t3Multiplier = t3sequence.setResources(scenery.getResources());
		BitSequencer t4sequence = new BitSequencer(PlanetaryProcessor.t4ProductList);
		long t4Multiplier = t4sequence.setResources(scenery.getResources());
		// TODO - Use all times all T2 resources.
//		t2Multiplier=1;
		return Math.max(t2Multiplier, 1) * Math.max(t3Multiplier, 1) * Math.max(t4Multiplier, 1);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("PlanetaryProcessor [");
		buffer.append("actions:").append(actions);
		return buffer.toString();
	}

	protected List<Resource> getResources() {
		Vector<Resource> finalResources = new Vector<Resource>();
		//		for (ProcessingAction processingAction : actions) {
		// Get the list of resources consumed by the actions. Those are each action input schematics.
		HashMap<Integer, Integer> consumed = new HashMap<Integer, Integer>();
		Vector<Resource> outputs = new Vector<Resource>();
		for (ProcessingAction action : savedScenery) {
			for (Schematics sche : action.getInputs()) {
				consumed.put(sche.getTypeId(), sche.getTypeId());
			}
			outputs.addAll(action.getActionResults());
		}
		// Calculate the value removing from the loop the resources used and adding to it the new outputs.
		//			double value = 0.0;
		for (Resource r : scenery.getResources()) {
			if (null == consumed.get(r.getTypeId())) {
				finalResources.add(r);
			}
		}
		// Add outputs resources value.
		for (Resource r : outputs) {
			if (null == consumed.get(r.getTypeId())) {
				finalResources.add(r);
			}
		}
		return finalResources;
	}

	/**
	 * Calculates the value on the market of the sell of all resources of the scenery after applying the
	 * transformation actions registered on this Processor. <br>
	 * The processing of this values requires first to remove from the list of resources all of them transformed
	 * by an action and add to the list the result of that same action. This is done on the fly without changin
	 * the original list of resources of the scenery.
	 *
	 * @param resourceList2Evaluate
	 * @return
	 */
	private double evaluateValue( final List<Resource> resourceList2Evaluate ) {
		//		// Get the list of resources consumed by the actions. Those are each action input schematics.
		//		HashMap<Integer, Integer> consumed = new HashMap<Integer, Integer>();
		//		Vector<Resource> outputs = new Vector<Resource>();
		//		for (ProcessingAction action : actions) {
		//			for (Schematics sche : action.getInputs()) {
		//				consumed.put(sche.getTypeId(), sche.getTypeId());
		//			}
		//			outputs.addAll(action.getActionResults());
		//		}
		// Calculate the value removing from the loop the resources used and adding to it the new outputs.
		double value = 0.0;
		for (Resource r : resourceList2Evaluate) {
			//			if (null == consumed.get(r.getTypeId())) {
			try {
				value += r.getItem().getHighestBuyerPrice().getPrice() * r.getQuantity();
			} catch (ExecutionException ee) {
				value += r.getItem().getPrice() * r.getQuantity();
			} catch (InterruptedException ie) {
				value += r.getItem().getPrice() * r.getQuantity();
			}
			//			}
		}
		//		// Add outputs resources value.
		//		for (Resource r : outputs) {
		//			if (null == consumed.get(r.getTypeId())) {
		//				value += r.getItem().getHighestBuyerPrice().getPrice();
		//			}
		//		}
		return value;
	}

	/**
	 * Getting a list of processing actions and a list of resources as inputs, the method performs the actions transformations
	 * consuming input resources and generating new resources. The procedure removed the <b>consumed</b> or input resources from
	 * the new list because the action will add the surplus materials from the inputs not already consumed in the operations.
	 * <p>
	 * The method finally will coalesce the input list and the generated list into a single list with no resources duplicated.
	 *
	 * @param actions
	 * @param inputResources
	 * @return
	 */
	protected List<Resource> processActions( final List<ProcessingAction> actions
			, final List<Resource> inputResources ) {
		HashMap<Integer, Integer> consumed = new HashMap<Integer, Integer>();
		List<Resource> outputs = new Vector<Resource>();
		// Get the list of resources consumed by the actions. Those are each action input schematics.
		for (ProcessingAction action : actions) {
			for (Schematics sche : action.getInputs()) {
				consumed.put(sche.getTypeId(), sche.getTypeId());
			}
			outputs.addAll(action.getActionResults());
		}
		// Generate the new list of resources.
		List<Resource> resources = new Vector<Resource>();
		for (Resource r : inputResources) {
			// Do not add consumed and include outputs.
			if (null == consumed.get(r.getTypeId())) {
				resources.add(r);
			}
		}
		resources.addAll(outputs);

		// Coalesce and remove duplicates.
		return coalesceResourceList(resources);
	}

	/**
	 * Unify into a list with no duplicated resources with the same <code>typeId</code>.
	 *
	 * @param inputList the original list of resources.
	 * @return a new list of new instances with the accounting for each type id aggregated.
	 */
	protected List<Resource> coalesceResourceList( final List<Resource> inputList ) {
		Map<Integer, Resource> coalescedList = new HashMap<>();
		for (Resource res : inputList) {
			final Resource hit = coalescedList.get(res.getTypeId());
			if (null == hit) coalescedList.put(res.getTypeId(), new PlanetaryResource(res.getTypeId(), res.getQuantity()));
			else hit.setQuantity(hit.getQuantity() + res.getQuantity());
		}
		return new ArrayList<>(coalescedList.values());
	}

	private void saveBetterConfiguration( final double marketValue, final List<ProcessingAction> sceneryActions ) {
		if (marketValue > betterProfit) {
			betterProfit = marketValue;
			savedScenery.clear();
			for (ProcessingAction action : sceneryActions) {
				try {
					savedScenery.add(action.clone());
				} catch (CloneNotSupportedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
	}
}

// - UNUSED CODE ............................................................................................
