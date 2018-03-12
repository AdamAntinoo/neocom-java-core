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
package org.dimensinfin.eveonline.neocom.model;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the MVC adaptation for the Corporation data. Its contents depend on multiple ESI calls even most of them are
 * related to Universe data that is loaded on demand.
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class CorporationV1 {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("CorporationV1");

	// - F I E L D - S E C T I O N ............................................................................
public int corporationId=-1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CorporationV1() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("CorporationV1[")
				.append("[#").append(corporationId).append("] ")
				.append("]")
				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
