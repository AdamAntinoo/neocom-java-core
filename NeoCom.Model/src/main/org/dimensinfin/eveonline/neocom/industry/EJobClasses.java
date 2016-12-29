//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.industry;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public enum EJobClasses {
	MANUFACTURE, INVENTION, TIMERESEARCH, MATERIALRESEARCH, COPYING;

	public static EJobClasses decodeActivity(final int activityID) {
		if (activityID == 1) return MANUFACTURE;
		if (activityID == 8) return INVENTION;
		return MANUFACTURE;
	}
}

// - UNUSED CODE ............................................................................................
