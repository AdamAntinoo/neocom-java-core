//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.core;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractComplexNode;

/**
 * This class gives the Expand/Collapse functionality to any model node without the need to be an Android
 * node.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractContainer extends Object {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5946489224592438548L;
	// - F I E L D - S E C T I O N ............................................................................
	/** Stores the expand/collapse visual state to be used on Android UI. */
	private final boolean			expanded					= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractContainer() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
