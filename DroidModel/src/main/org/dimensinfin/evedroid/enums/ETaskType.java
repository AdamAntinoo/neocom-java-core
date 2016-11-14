//  PROJECT:        DroidModel
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.enums;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public enum ETaskType {
	REQUEST, MOVE, PRODUCE, REFINE, COPY, GET, AVAILABLE, BUILD, BUY, BUYCOVERED, SELL, RESEARCH_ME, RESEARCH_PE, INVENTION, EXTRACT;

	public static ETaskType decode(final String action) {
		if (action.equalsIgnoreCase("REQUEST")) return REQUEST;
		if (action.equalsIgnoreCase("MOVE")) return MOVE;
		if (action.equalsIgnoreCase("PRODUCE")) return PRODUCE;
		if (action.equalsIgnoreCase("REFINE")) return REFINE;
		if (action.equalsIgnoreCase("COPY")) return COPY;
		if (action.equalsIgnoreCase("GET")) return GET;
		if (action.equalsIgnoreCase("AVAILABLE")) return AVAILABLE;
		if (action.equalsIgnoreCase("EXTRACT")) return EXTRACT;

		if (action.equalsIgnoreCase("BUY")) return BUY;
		if (action.equalsIgnoreCase("BUYCOVERED")) return BUYCOVERED;
		if (action.equalsIgnoreCase("SELL")) return SELL;
		if (action.equalsIgnoreCase("RESEARCH_ME")) return RESEARCH_ME;
		if (action.equalsIgnoreCase("RESEARCH_PE")) return RESEARCH_PE;
		if (action.equalsIgnoreCase("INVENTION")) return INVENTION;
		return REQUEST;
	}
}

// - UNUSED CODE ............................................................................................
