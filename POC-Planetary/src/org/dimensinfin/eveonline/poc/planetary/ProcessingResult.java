//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public class ProcessingResult {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger		= Logger.getLogger("ProcessingResult");

	// - F I E L D - S E C T I O N ............................................................................
	private Vector<Resource>	resources	= new Vector<Resource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ProcessingResult() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(Resource resource) {
		resources.add(resource);
	}

}

// - UNUSED CODE ............................................................................................
