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

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger				= Logger.getLogger("PlanetaryProcessor");
	private static HashMap<Integer, String>	t2ProductList	= new HashMap<Integer, String>();
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
	private static HashMap<Integer, String> t3ProductList = new HashMap<Integer, String>();
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
	private static HashMap<Integer, String> t4ProductList = new HashMap<Integer, String>();
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

	// - F I E L D - S E C T I O N ............................................................................
	private PlanetaryScenery								scenery				= null;
	private final Vector<ProcessingAction>	actions				= new Vector<ProcessingAction>();
	private double													betterProfit	= 0.0;
	private final Vector<ProcessingAction>	savedScenery	= new Vector<ProcessingAction>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Create a new processor and connect it to an scenery. The creation does nothing important.
	 * 
	 * @param scenery
	 */
	public PlanetaryProcessor(final PlanetaryScenery scenery) {
		this.scenery = scenery;
	}

	public Vector<Resource> getResources() {
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
			if (null == consumed.get(r.getTypeID())) {
				finalResources.add(r);
			}
		}
		// Add outputs resources value.
		for (Resource r : outputs) {
			if (null == consumed.get(r.getTypeID())) {
				finalResources.add(r);
			}
		}
		return finalResources;
	}

	/**
	 * Start the recursive process of analyzing the different processing combinations to search for the most
	 * profitable. The process gets the list of all possible Tier2 products and searched for their inputs. If
	 * found on the list of the Scenery resources then we run a new action and get a new output. This is done
	 * for all the Tier2 products.
	 * 
	 * @param currentTarget
	 * @return
	 */
	public Vector<ProcessingAction> startProfitSearch(final PlanetaryProcessor currentTarget) {
		PlanetaryProcessor.logger.info(">> [PlanetaryProcessor.startProfitSearch]");
		// If current target is null this is then the first iteration on the search.
		betterProfit = 0.0;
		PlanetaryProcessor.logger
				.info("-- [PlanetaryProcessor.startProfitSearch]> Initial resources: " + scenery.getResources());
		// Search for Tier2 optimizations
		// Create the sequencer and start to iterate over it.
		BitSequencer t2sequence = new BitSequencer(PlanetaryProcessor.t2ProductList);
		t2sequence.setResources(scenery.getResources());
		while (t2sequence.hasSequence()) {
			Vector<ProcessingAction> next2Sequence = t2sequence.nextSequence();
			// Update resource list by processing the list of current actions.
			Vector<Resource> new2Resources = this.processActions(next2Sequence, scenery.getResources());
			//Search for Tier3 optimizations
			BitSequencer t3sequence = new BitSequencer(PlanetaryProcessor.t3ProductList);
			t3sequence.setResources(new2Resources);
			while (t3sequence.hasSequence()) {
				Vector<ProcessingAction> next3Sequence = t3sequence.nextSequence();
				// Update resource list by processing the list of current actions.
				Vector<Resource> new3Resources = this.processActions(next3Sequence, new2Resources);
				//Search for Tier3 optimizations
				BitSequencer t4sequence = new BitSequencer(PlanetaryProcessor.t4ProductList);
				t4sequence.setResources(new3Resources);
				double marketValue = 0.0;
				// Before processing the sequences I have to know the number for ZERO there is special code to run.
				if (t4sequence.hasSequence()) {
					while (t4sequence.hasSequence()) {
						Vector<ProcessingAction> next4Sequence = t3sequence.nextSequence();
						// Update resource list by processing the list of current actions.
						Vector<Resource> new4Resources = this.processActions(next4Sequence, new3Resources);
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
		}
		PlanetaryProcessor.logger.info("<< [PlanetaryProcessor.startProfitSearch]");
		return savedScenery;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("PlanetaryProcessor [");
		buffer.append("actions:").append(actions);
		return buffer.toString();
	}

	/**
	 * Calculates the value on the market of the sell of all resources of the scenery after applying the
	 * transformation actions registered on this Processor. <br>
	 * The processing of this values requires first to remove from the list of resources all of them transformed
	 * by an action and add to the list the result of that same action. This is done on the fly without changin
	 * the original list of resources of the scenery.
	 * 
	 * @param resourceList2Evaluate
	 * 
	 * @return
	 */
	private double evaluateValue(final Vector<Resource> resourceList2Evaluate) {
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
			//			if (null == consumed.get(r.getTypeID())) {
			value += r.getItem().getHighestBuyerPrice().getPrice() * r.getQuantity();
			//			}
		}
		//		// Add outputs resources value.
		//		for (Resource r : outputs) {
		//			if (null == consumed.get(r.getTypeID())) {
		//				value += r.getItem().getHighestBuyerPrice().getPrice();
		//			}
		//		}
		return value;
	}

	private Vector<Resource> processActions(final Vector<ProcessingAction> actions,
			final Vector<Resource> inputResources) {
		// Get the list of resources consumed by the actions. Those are each action input schematics.
		HashMap<Integer, Integer> consumed = new HashMap<Integer, Integer>();
		Vector<Resource> outputs = new Vector<Resource>();
		for (ProcessingAction action : actions) {
			for (Schematics sche : action.getInputs()) {
				consumed.put(sche.getTypeId(), sche.getTypeId());
			}
			outputs.addAll(action.getActionResults());
		}
		// Generate the new list of resources.
		Vector<Resource> resources = new Vector<Resource>();
		for (Resource r : inputResources) {
			// Do not add consumed and include outputs.
			if (null == consumed.get(r.getTypeID())) {
				resources.add(r);
			}
		}
		resources.addAll(outputs);
		return resources;
	}

	private void saveBetterConfiguration(final double marketValue, final Vector<ProcessingAction> sceneryActions) {
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
