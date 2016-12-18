//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.core;

import java.util.HashMap;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class CEventPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger					= Logger.getLogger("CEventPart");
	private static String										NOT_FOUND_EVENT	= "NOT_FOUND_EVENT";
	private static HashMap<Integer, String>	events					= new HashMap<Integer, String>();

	public static String getName4Event(final int eventCode) {
		// Search for this event id in the list of registered events.
		String event = CEventPart.events.get(eventCode);
		if (null != event)
			return event;
		else
			return CEventPart.NOT_FOUND_EVENT;
	}

	public static void register(final int code, final String name) {
		CEventPart.events.put(code, name);
	}
	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CEventPart() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
