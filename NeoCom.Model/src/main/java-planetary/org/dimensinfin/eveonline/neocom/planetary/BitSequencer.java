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

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * Uses a BitSet implementation to calculate all the Planetary combinations of the different resources. If we
 * start with the Tier 2 resources it will get all the possible combinations, then remove the ones that do not
 * have one or both resources to be processed and finally will generate ona by one all the possible
 * combinations with the remaining by running a binary counter. <br>
 * Each of the bits represents a planetary resource and each number in sequence will represent a set of
 * resources to be processed.
 *
 * @author Adam Antinoo
 */
public class BitSequencer {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("BitSequencer");
	private static final long serialVersionUID = -2437083317973194379L;

	// - F I E L D - S E C T I O N ............................................................................
	private HashMap<Integer, String> sequencer = null;
	private final List<ProcessingAction> optimizedSequencer = new Vector<ProcessingAction>();
	private List<Resource> sourceResources = null;
	private int bitsNumber = 0;
	private long position = 0;
	private double maxCounter = 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Create a bit set with the list of planetary resources to be used for the sequence calculation. The sets
	 * received are the full list for Tier 2 or Tier 3 and so on.
	 */
	public BitSequencer( final HashMap<Integer, String> productList ) {
		sequencer = productList;
	}

	public BitSequencer( final List<Resource> resourceList ) {
		sequencer = new HashMap<Integer, String>();
		for (Resource res : resourceList) {
			sequencer.put(res.getTypeId(), res.getName());
		}
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

	public List<ProcessingAction> nextSequence() {
		// Check if last sequence reached.
		if (this.hasSequence()) {
			position++;
			// Get the bits conversion of the position number;
			BitSet bits = Bits.convert(position);
			// Compose the sequence from the active bits.
			List<ProcessingAction> sequence = new Vector<ProcessingAction>();
			for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
				sequence.add(optimizedSequencer.get(i));
			}
			return sequence;
		} else
			return new Vector<ProcessingAction>();
	}

	public List<ProcessingAction> maxSequence() {
		// Check if last sequence reached.
//		if (this.hasSequence()) {
//			position++;
		// Get the bits conversion of the position number;
//			BitSet bits = Bits.convert(position);
		// Compose the sequence from the active bits.
		List<ProcessingAction> sequence = new Vector<ProcessingAction>();
//			for (int i = 0; i >= 0; i = bits.nextSetBit(i + 1)) {
		sequence.addAll(optimizedSequencer);
//			}
		return sequence;
//		} else
//			return new Vector<ProcessingAction>();
	}

	/**
	 * Load into the sequencer the list of scenery resources that should be processed.
	 *
	 * @param resources
	 * @return the max number of iterations needed to process the list of different planetary resources.
	 */
	public long setResources( final List<Resource> resources ) {
		sourceResources = resources;
		// Validate witch combinations can be generated.
		// Search for TierN optimizations
		for (int target : sequencer.keySet()) {
			BitSequencer.logger.info("-- [BitSequencer.setResources]> Searching: " + sequencer.get(target));
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
		return Double.valueOf(Math.floor(maxCounter)).longValue();
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("BitSequencer [ ");
		buffer.append("resources: ").append(sourceResources).append(" ");
		buffer.append("sequence").append(optimizedSequencer).append(" ");
		buffer.append("position").append(position).append("/").append(maxCounter).append(" ");
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
	private Resource getResource( final int inputResourceId ) {
		for (Resource res : sourceResources) {
			if (res.getTypeId() == inputResourceId) return res;
		}
		return new Resource(inputResourceId);
	}

	/**
	 * Reinitializes the sequence pointer and counter that generates the list of processing packs.
	 */
	private void reset() {
		position = 0;
		bitsNumber = optimizedSequencer.size();
		maxCounter = Math.pow(2, bitsNumber) - 1;
	}
}

final class Bits {

	public static long convert( final BitSet bits ) {
		long value = 0L;
		for (int i = 0; i < bits.length(); ++i) {
			value += bits.get(i) ? (1L << i) : 0L;
		}
		return value;
	}

	public static BitSet convert( long value ) {
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
