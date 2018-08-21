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
package org.dimensinfin.eveonline.neocom.exception;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComRuntimeException extends RuntimeException {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 8864888568628860054L;

	// - F I E L D - S E C T I O N ............................................................................
	private String jsonClass = "NeoComRuntimeException";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComRuntimeException() {
		super();
	}

	public NeoComRuntimeException( final String message ) {
		super(message);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getJsonClass() {
		return jsonClass;
	}
}

// - UNUSED CODE ............................................................................................
