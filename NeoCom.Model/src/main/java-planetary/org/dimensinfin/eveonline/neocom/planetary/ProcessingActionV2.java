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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.industry.Resource;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ProcessingActionV2 extends ProcessingAction {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("ProcessingActionV2");

	// - F I E L D - S E C T I O N ............................................................................
	private int quantity = -1;


	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Instance a new <code>ProcessingAction</code> and set the target product id to be produced by this action.
	 * This should get the schematics information so the action can process the quantities and the length of the
	 * cycles.
	 * @param targetId
	 */
	public ProcessingActionV2( final int targetId ) {
		super(targetId);
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Return the list of resources left and new from the action processing following the current schematics.
	 * This is the result of subtracting from the input resources the input quantity multiplied by the cycles
	 * and adding to the result the output resource quantity multiplied by the same cycles.
	 *
	 * On this limited method the number of cycles is calculated depending on the resulting quantity to be produced and not on
	 * the availability of the source resources. So depending on the Tier for the target we should use a different cycle
	 * multiplier to get the real number of cycles to perform.
	 * @return the new list of resources after the processing.
	 */
	public List<Resource> getLimitedActionResults() {
		// Calculate the number of cycles from the current target quantity. If not defined then use all resources.
		if (quantity < 0) return getActionResults();
		final int cycleOutput = output.getQty();
		int cycles = Double.valueOf(Math.ceil(1.0 * quantity / cycleOutput)).intValue();

		final List<Resource> results = new Vector<Resource>();
		if (cycles > 0) {
			for (final Schematics sche : inputList) {
				// Replace the calculated quantity by a negative subtraction quantity.
				final Resource res = new Resource(sche.getTypeId(), -1 * sche.getQty() * cycles);
//				res.setQuantity(res.getQuantity() * -1);
				results.add(res);
			}
			// Add the output
			results.add(new Resource(output.getTypeId(), output.getQty() * cycles));
		}
		return results;
	}

	// --- G E T T E R S   &   S E T T E R S
	public ProcessingActionV2 setCycles( final int quantity ) {
		this.quantity = quantity;
		return this;
	}
	// --- D E L E G A T E D   M E T H O D S
//	@Override
//	public String toString() {
//		return new StringBuffer("ProcessingActionV2 [")
//				.append("field:").append().append(" ")
//				.append("]")
//				.append("->").append(super.toString())
//				.toString();
//	}
}

// - UNUSED CODE ............................................................................................
//[01]
