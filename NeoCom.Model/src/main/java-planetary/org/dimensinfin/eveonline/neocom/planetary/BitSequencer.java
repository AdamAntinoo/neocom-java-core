//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download and parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.planetary;

//- IMPORT SECTION .........................................................................................
import java.util.BitSet;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;

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
public class BitSequencer {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger							= Logger.getLogger("BitSequencer");
	private static final long								serialVersionUID		= -2437083317973194379L;
	private static HashMap<Integer, String>	sequencer						= null;

	// - F I E L D - S E C T I O N ............................................................................
	private final Vector<ProcessingAction>	optimizedSequencer	= new Vector<ProcessingAction>();
	private Vector<Resource>								sourceResources			= null;
	private int															bitsNumber					= 0;
	private long														position						= 0;
	private double													maxCounter					= 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Create a bit set with the list of planetary resources to be used for the sequence calculation. The sets
	 * received are the full list for Tier 2 or Tier 3 and so on.
	 */
	public BitSequencer(final HashMap<Integer, String> productList) {
		BitSequencer.sequencer = productList;
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
		if (position < maxCounter)
			return true;
		else
			return false;
	}

	public Vector<ProcessingAction> nextSequence() {
		// Check if last sequence reached.
		if (this.hasSequence()) {
			position++;
			// Get the bits conversion of the position number;
			BitSet bits = Bits.convert(position);
			// Compose the sequence from the active bits.
			Vector<ProcessingAction> sequence = new Vector<ProcessingAction>();
			for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
				sequence.add(optimizedSequencer.get(i));
			}
			return sequence;
		} else
			return new Vector<ProcessingAction>();
	}

	/**
	 * Load into the sequencer the list of scenery resources that should be processed.
	 * 
	 * @param resources
	 */
	public void setResources(final Vector<Resource> resources) {
		sourceResources = resources;
		// Validate witch combinations can be generated.
		// Search for TierN optimizations
		for (int target : BitSequencer.sequencer.keySet()) {
			BitSequencer.logger.info("-- [BitSequencer.setResources]> Searching: " + BitSequencer.sequencer.get(target));
			ProcessingAction action = new ProcessingAction(target);
			// Get the input resources from the Scenery if available.
			for (Schematics input : action.getInputs()) {
				action.addResource(this.getResource(input.getTypeId()));
			}
			BitSequencer.logger.info("-- [BitSequencer.setResources]> Action: " + action);
			// Validate if the action is successful, if it can deliver output resources.
			int cycles = action.getPossibleCycles();
			// If this is > 0 then we have to set this planetary resource as a possible combination.
			if (cycles > 0) {
				optimizedSequencer.add(action);
			}
		}
		// After filtering out the invalid target resources reset the counter to the new size
		bitsNumber = optimizedSequencer.size();
		this.reset();
		BitSequencer.logger.info("-- [BitSequencer.setResources]> optimizedSequencer: " + optimizedSequencer);
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("BitSequencer [");
		buffer.append("resources: ").append(sourceResources);
		buffer.append("sequence").append(optimizedSequencer);
		//		buffer.append(item.getName()).append(" x").append(baseQty).append(" ");
		//		buffer.append("stack: ").append(stackSize).append(" ");
		//		buffer.append("total: ").append(this.getQuantity()).append(" ");
		//buffer.append("#").append(this.getTypeId()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * Return the stocked Planetary Resource that matches the parameter id. If not found return a Resource of
	 * quantity ZERO.
	 * 
	 * @param inputResourceId
	 * @return
	 */
	private Resource getResource(final int inputResourceId) {
		for (Resource res : sourceResources) {
			if (res.getTypeID() == inputResourceId) return res;
		}
		return new Resource(inputResourceId);
	}

	/**
	 * Reinitializes the sequence pointer and counter that generates the list of processing packs.
	 */
	private void reset() {
		//		clear();
		position = 0;
		maxCounter = Math.pow(2, bitsNumber) - 1;
	}
}

final class Bits {

	public static long convert(final BitSet bits) {
		long value = 0L;
		for (int i = 0; i < bits.length(); ++i) {
			value += bits.get(i) ? (1L << i) : 0L;
		}
		return value;
	}

	public static BitSet convert(long value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value != 0L) {
			if ((value % 2L) != 0) {
				bits.set(index);
			}
			++index;
			value = value >>> 1;
		}
		return bits;
	}
}
// - UNUSED CODE ............................................................................................