//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.constant;

import java.util.HashMap;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class CVariant {
	public enum EDefaultVariant {
		DEFAULT_VARIANT
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger		= Logger.getLogger("CVariant.java");
	//	private static String										DEFAULT_VARIANT	= "DEFAULT_VARIANT";
	private static HashMap<Integer, String>	variants	= new HashMap<Integer, String>();
	static {
		CVariant.register(EDefaultVariant.DEFAULT_VARIANT.hashCode(), EDefaultVariant.DEFAULT_VARIANT.name());
	}

	public static String getName4Variant(final int variantCode) {
		// Search for this event id in the list of registered events.
		String event = CVariant.variants.get(variantCode);
		if (null != event)
			return event;
		else
			return EDefaultVariant.DEFAULT_VARIANT.name();
	}

	public static void register(final int code, final String name) {
		CVariant.variants.put(code, name);
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
