//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.core.NeoComException;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComRegisteredException extends NeoComException {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("NeoComRegisteredException");

	public static String lookupMessage( final NEOE code ) {
		switch (code) {
			case ESIDATA_NULL:
				return "Data request to ESI entry point not returned data or gave timeout.";
		}
		return "Unregistered message. See other messages for details.";
	}

	// - F I E L D - S E C T I O N ............................................................................
	public NEOE code = NEOE.UNREGISTERED;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComRegisteredException( final NEOE exceptionCode ) {
		super(lookupMessage(exceptionCode));
		this.code = exceptionCode;
	}

	public NeoComRegisteredException( final NEOE exceptionCode, final String message ) {
		super(lookupMessage(exceptionCode) + '\n' + message);
		this.code = exceptionCode;
	}
	// - M E T H O D - S E C T I O N ..........................................................................
	public NEOE getCode() {
		return code;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("NeoComRegisteredException [ ")
				.append("").append(code.name()).append(" ")
				.append("message:").append(getMessage()).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
