//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.model.Schematics;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class will control and generate the different sequences of planetary resource processing jobs that are
 * possible with some input configuration. The class has methods to check what are the possible configurations
 * from the input data and then create a binary counter to sequence through all the processing variations.
 * <br>
 * From a binary counter the class will be able to identify the planetary process to activate when the
 * corresponding bit is set and be able to iterate to all the possible configurations. Those are the
 * combinations and I will not account for other variations or different ordering on the processes.
 * 
 * @author Adam Antinoo
 */
public class PlanetarySequencer {
	public enum ETiers {
		TIER2, TIER3
	}

	/**
	 * Class to implement the bit counter and the integer-binary transformations.
	 * 
	 * @author Adam
	 *
	 */
	final class Sequencer {
		private int	bits	= 1;
		private int	max		= -1;

		public Sequencer(Vector<ProcessingAction> combinations) {
			this.bits = combinations.size();
			create();
		}

		private void create() {
			max = 2 ^ bits;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger				= Logger.getLogger("PlanetarySequencer");
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

	// - F I E L D - S E C T I O N ............................................................................
	private PlanetaryScenery					scenery					= null;
	private Vector										resources				= null;
	private ETiers										tier						= ETiers.TIER2;
	private Vector<ProcessingAction>	combinations		= new Vector<ProcessingAction>();
	private Sequencer									sequenceCounter	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetarySequencer(PlanetaryScenery scenery) {
		this.scenery = scenery;
	}

	public PlanetarySequencer(Vector resources) {
		this.resources = resources;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Checks all the tier products for the valid combinations from the list of target resources. There can be
	 * different tier sets of products and the list of inputs is the one obtained during instance creation.
	 */
	private void checkCombinations() {
		if (tier == ETiers.TIER2) return checkTier2Combinations();
	}

	/**
	 * Check the possible combinations for TIER 2 planetary products.
	 * 
	 * @return the number of combinations that can be processed with this set of input resources
	 */
	private int checkTier2Combinations() {
		for (int target : t2ProductList.keySet()) {
			// Create a new action to check the number of runs
			ProcessingAction action = new ProcessingAction(target);
			// Get the input resources from the Scenery if available.
			for (Schematics input : action.getInputs()) {
				action.addResource(scenery.getResource(input.getTypeId()));
			}
			//			logger.info("-- [PlanetaryProcessor.startProfitSearch]> Action: " + action);
			// Validate if the action is successful, if it can deliver output resources.
			int cycles = action.getPossibleCycles();
			logger.info("-- [PlanetarySequencer.checkTier2Combinations]> Checking: " + target + " - " + cycles);
			//			logger.info("-- [PlanetaryProcessor.startProfitSearch]> Cycles: " + cycles);
			if (cycles > 0) {
				// Record this action on the list of combinations.
				combinations.add(action);
				//				double marketValue = evaluateValue();
			}
		}
		return combinations.size();
	}

	/**
	 * Creates and initializes to ZERO the binary sequence counter. The number of bits of the counter matches
	 * the number of planetary resources processing sequences possible.
	 */
	private void createCounter() {
		sequenceCounter = new Sequencer(combinations);
	}
}
// - UNUSED CODE ............................................................................................
