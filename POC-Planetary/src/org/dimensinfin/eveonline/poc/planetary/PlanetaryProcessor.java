//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.Schematics;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger				= Logger.getLogger("org.dimensinfin.eveonline.poc.planetary");
	private static HashMap<Integer, String>	t2ProductList	= new HashMap<Integer, String>();
	// Refined Commodities
	static {
		t2ProductList.put(2329, "Biocells");
		t2ProductList.put(3828, "Construction Blocks");
		t2ProductList.put(9836, "Consumer Electronics");
		t2ProductList.put(9832, "Coolant");
		t2ProductList.put(44, "Enriched Uranium");
		t2ProductList.put(3693, "Fertilizer");
		t2ProductList.put(15317, "Genetically Enhanced Livestock");
		t2ProductList.put(3725, "Livestock");
		t2ProductList.put(3689, "Mechanical Parts");
		t2ProductList.put(2327, "Microfiber Shielding");
		t2ProductList.put(9842, "Miniature Electronics");
		t2ProductList.put(2463, "Nanites");
		t2ProductList.put(2317, "Oxides");
		t2ProductList.put(2321, "Polyaramids");
		t2ProductList.put(3695, "Polytextiles");
		t2ProductList.put(9830, "Rocket Fuel");
		t2ProductList.put(3697, "Silicate Glass");
		t2ProductList.put(9838, "Superconductors");
		t2ProductList.put(2312, "Supertensile Plastics");
		t2ProductList.put(3691, "Synthetic Oil");
		t2ProductList.put(2319, "Test Cultures");
		t2ProductList.put(9840, "Transmitter");
		t2ProductList.put(3775, "Viral Agent");
		t2ProductList.put(2328, "Water-Cooled CPU");
	}
	private static HashMap<Integer, String> t3ProductList = new HashMap<Integer, String>();
	// Specialized Commodities
	static {
		t3ProductList.put(2358, "Biotech Research Reports");
		t3ProductList.put(2345, "Camera Drones");
		t3ProductList.put(2344, "Condensates");
		t3ProductList.put(2367, "Cryoprotectant Solution");
		t3ProductList.put(17392, "Data Chips");
		t3ProductList.put(2348, "Gel-Matrix Biopaste");
		t3ProductList.put(9834, "Guidance Systems");
		t3ProductList.put(2366, "Hazmat Detection Systems");
		t3ProductList.put(2361, "Hermetic Membranes");
		t3ProductList.put(17898, "High-Tech Transmitters");
		t3ProductList.put(2360, "Industrial Explosives");
		t3ProductList.put(2354, "Neocoms");
		t3ProductList.put(2352, "Nuclear Reactors");
		t3ProductList.put(9846, "Planetary Vehicles");
		t3ProductList.put(9848, "Robotics");
		t3ProductList.put(2351, "Smartfab Units");
		t3ProductList.put(2349, "Supercomputers");
		t3ProductList.put(2346, "Synthetic Synapses");
		t3ProductList.put(12836, "Transcranial Microcontrollers");
		t3ProductList.put(17136, "Ukomi Superconductors");
		t3ProductList.put(28974, "Vaccines");
	}
	private static HashMap<Integer, String> t4ProductList = new HashMap<Integer, String>();
	// Advanced Commodities
	static {
		t4ProductList.put(2867, "Broadcast Node");
		t4ProductList.put(2868, "Integrity Response Drones");
		t4ProductList.put(2869, "Nano-Factory");
		t4ProductList.put(2870, "Organic Mortar Applicators");
		t4ProductList.put(2871, "Recursive Computing Module");
		t4ProductList.put(2872, "Self-Harmonizing Power Core");
		t4ProductList.put(2875, "Sterile Conduits");
		t4ProductList.put(2876, "Wetware Mainframe");
	}

	// - F I E L D - S E C T I O N ............................................................................
	private PlanetaryScenery					scenery	= null;
	private Vector<ProcessingAction>	actions	= new Vector<ProcessingAction>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Create a new processor and connect it to an scenery. The creation does nothing important.
	 * 
	 * @param scenery
	 */
	public PlanetaryProcessor(PlanetaryScenery scenery) {
		this.scenery = scenery;
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
	public PlanetaryProcessor startProfitSearch(PlanetaryProcessor currentTarget) {
		logger.info(">> [PlanetaryProcessor.startProfitSearch]");
		// If current target is null this is then the first iteration on the search.
		if (null == currentTarget) {
			// Search for Tier2 optimizations
			for (int target : t2ProductList.keySet()) {
				logger.info("-- [PlanetaryProcessor.startProfitSearch]> Searching " + target);
				ProcessingAction action = new ProcessingAction(target);
				// Get the input resources from the Scenery if available.
				for (Schematics input : action.getInputs()) {
					action.addResource(scenery.getResource(input.getTypeId()));
				}
				logger.info("-- [PlanetaryProcessor.startProfitSearch]> Action: " + action);
				// Validate if the action is successful, if it can deliver output resources.
				int cycles = action.getPossibleCycles();
				logger.info("-- [PlanetaryProcessor.startProfitSearch]> Cycles: " + cycles);
				if (action.getPossibleCycles() > 0) {
					// Record this action and evaluate the market value of the new scenery.
					actions.add(action);
					double marketValue = evaluateValue();
				}
			}
		}
		logger.info("<< [PlanetaryProcessor.startProfitSearch]");
		return this;
	}

	/**
	 * Calculates the value on the market of the sell of all resources of the scenery after applying the
	 * transformation actions registered on this Processor. <br>
	 * The processing of this values requires first to remove from the list of resources all of them transformed
	 * by an action and add to the list the result of that same action. This is done on the fly without changin
	 * the original list of resources of the scenery.
	 * 
	 * @return
	 */
	private double evaluateValue() {
		// Get the list of resources consumed by the actions. Those are each action input schematics.
		HashMap<Integer, Integer> consumed = new HashMap<Integer, Integer>();
		Vector<Resource> outputs = new Vector<Resource>();
		for (ProcessingAction action : actions) {
			for (Schematics sche : action.getInputs()) {
				consumed.put(sche.getTypeId(), sche.getTypeId());
			}
			outputs.addAll(action.getActionResults());
		}
		// Calculate the value removing from the loop the resources used and adding to it the new outputs.
		double value = 0.0;
		for (Resource r : scenery.getResources()) {
			if (null == consumed.get(r.getTypeID())) {
				value += r.getItem().getHighestBuyerPrice().getPrice();
			}
		}
		// Add outputs resources value.
		for (Resource r : outputs) {
			if (null == consumed.get(r.getTypeID())) {
				value += r.getItem().getHighestBuyerPrice().getPrice();
			}
		}
		return value;
	}

}

// - UNUSED CODE ............................................................................................
