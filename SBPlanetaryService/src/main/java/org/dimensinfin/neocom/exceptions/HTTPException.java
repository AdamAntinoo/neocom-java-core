//	PROJECT:        NeoCom.angularjs
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.neocom.exceptions;

import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class HTTPException extends Exception {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger			= Logger.getLogger("org.dimensinfin.neocom.exceptions");

	// - F I E L D - S E C T I O N ............................................................................
	private int						returnCode	= 500;
	private String				message			= "Default message for Api Exception.";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public HTTPException(int HTTPCode, String message) {
		returnCode = HTTPCode;
		this.message = message;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
