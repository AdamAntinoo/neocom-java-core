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
package org.dimensinfin.eveonline.neocom.util;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class JobData extends NeoComNode{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(JobData.class);

	// - F I E L D - S E C T I O N ............................................................................
	private String reference = "-NO DATA-";
	private boolean running = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobData (final String reference) {
		super();
		this.reference = reference;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getReference () {
		return reference;
	}

	public boolean isRunning () {
		return running;
	}

	public JobData setReference (final String reference) {
		this.reference = reference;
		return this;
	}

	public JobData setStatus (final boolean doneStatus) {
		running = !doneStatus;
		return this;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("JobData [");
		buffer.append("Reference: ").append(reference).append(" ");
		buffer.append("Running: ").append(running).append(" ");
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
