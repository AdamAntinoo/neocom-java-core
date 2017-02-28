//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.Schematics;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Uses a BitSet implementation to calculate all the Planetary combinations of the different resources. If we
 * start with the Tier 2 resources it will get all the possible combiantions, then remove the ones that do not
 * have one or both resources to be processed and finally will generate ona by one all the possible
 * combinations with the remainding by running a binary counter. <br>
 * Each of the bits represents a planetary resource and each number in sequence will represent a set of
 * resources to be processed.
 * 
 * @author Adam Antinoo
 */
public class BitSequencer extends BitSet {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger							= Logger.getLogger("BitProcessor");
	private static final long								serialVersionUID		= -2437083317973194379L;
	private static HashMap<Integer, String>	sequencer						= null;
	private static Vector										optimizedSequencer	= new Vector();
	//	static {
	//		t2ProductList.put(2268, "Aqueous Liquids");
	//		t2ProductList.put(2305, "Autotrophs");
	//		t2ProductList.put(2267, "Base Metals");
	//		t2ProductList.put(2288, "Carbon Compounds");
	//		t2ProductList.put(2287, "Complex Organisms");
	//		t2ProductList.put(2307, "Felsic Magma");
	//		t2ProductList.put(2272, "Heavy Metals");
	//		t2ProductList.put(2309, "Ionic Solutions");
	//		t2ProductList.put(2073, "Microorganisms");
	//		t2ProductList.put(2310, "Noble Gas");
	//		t2ProductList.put(2270, "Noble Metals");
	//		t2ProductList.put(2306, "Non-CS Crystals");
	//		t2ProductList.put(2286, "Planktic Colonies");
	//		t2ProductList.put(2311, "Reactive Gas");
	//		t2ProductList.put(2308, "Suspended Plasma");
	//	}
	// - F I E L D - S E C T I O N ............................................................................
	private Vector<Resource>								sourceResources			= null;
	private int															bitsNumber					= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Create a bit set with the list of planetary resources to be used for the sequence calculation.
	 */
	public BitSequencer(HashMap<Integer, String> productList) {
		super(productList.size());
		sequencer = productList;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Iterate over the different possible sequences depending on the bit configuration of the set. Use a binary
	 * counter to iterate over all the possible combinations.
	 * 
	 * @return
	 */
	public boolean hasSequence() {
		// Check if we have arrived to the max value possible with the number of bits setup for this sequencer.
		if (maxReached())
			return false;
		else
			return true;
	}

	public Object nextSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Load into the sequencer the list of scenery resources that should be processed.
	 * 
	 * @param resources
	 */
	public void setResources(Vector<Resource> resources) {
		sourceResources = resources;
		// Validate witch combinations can be generated.
		// Search for Tier2 optimizations
		for (int target : sequencer.keySet()) {
			logger.info("-- [BitSequencer.setResources]> Searching " + target);
			ProcessingAction action = new ProcessingAction(target);
			// Get the input resources from the Scenery if available.
			for (Schematics input : action.getInputs()) {
				action.addResource(getResource(input.getTypeId()));
			}
			logger.info("-- [BitSequencer.setResources]> Action: " + action);
			// Validate if the action is successful, if it can deliver output resources.
			int cycles = action.getPossibleCycles();
			// If this is > 0 then we have to set this planetary resource as a possible combination.
			if (cycles > 0) optimizedSequencer.add(target);

			//			logger.info("-- [BitSequencer.setResources]> Cycles: " + cycles);
			//			if (action.getPossibleCycles() > 0) {
			//				// Record this action and evaluate the market value of the new scenery.
			//				actions.add(action);
			//				double marketValue = evaluateValue();
			//			}
		}
		// After filtering out the invalid target resources reset the counter to the new size
		this.bitsNumber = optimizedSequencer.size();
		this.clear();
	}

	/**
	 * Return the stocked Planetary Resource that matches the parameter id. If not found return a Resource of
	 * quantity ZERO.
	 * 
	 * @param inputResourceId
	 * @return
	 */
	private Resource getResource(int inputResourceId) {
		for (Resource res : sceneryResources) {
			if (res.getTypeID() == inputResourceId) return res;
		}
		return new Resource(inputResourceId);
	}

	private boolean maxReached() {
		return false;
	}
}
// - UNUSED CODE ............................................................................................
