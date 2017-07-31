//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//								environment limits.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

// - CLASS IMPLEMENTATION ...................................................................................
public class R {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final String					BUNDLE_NAME			= "org.dimensinfin.eveonline.neocom.constant.R";	//$NON-NLS-1$
	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);

	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public static String getResourceString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException mre) {
			return '!' + key + '!';
		}
	}
}

// - UNUSED CODE ............................................................................................
