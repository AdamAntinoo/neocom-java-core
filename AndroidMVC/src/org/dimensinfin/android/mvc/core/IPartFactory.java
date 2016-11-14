//	PROJECT:        NeoCom.android
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.android.mvc.core;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.IGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IPartFactory {
	// - M E T H O D - S E C T I O N ..........................................................................
//	public AbstractCorePart createPart(AbstractDataSource context, IGEFNode node);
//
//public IEditPart createPart(AbstractCorePart abstractCorePart, IGEFNode model);
public IEditPart createPart(IGEFNode model);
}

// - UNUSED CODE ............................................................................................
