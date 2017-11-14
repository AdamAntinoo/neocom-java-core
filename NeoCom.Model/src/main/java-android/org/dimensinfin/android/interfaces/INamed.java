//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.android.interfaces;

/**
 * This interface declares the entries to sort by name for any element that extends IGEFNodes (preactically
 * any model class).
 * 
 * @author Adam Antinoo
 */
// - INTERCAFE IMPLEMENTATION ...............................................................................
public interface INamed /* extends IGEFNode */ {
	// - M E T H O D - S E C T I O N ..........................................................................
	public String getOrderingName();
}

// - UNUSED CODE ............................................................................................
