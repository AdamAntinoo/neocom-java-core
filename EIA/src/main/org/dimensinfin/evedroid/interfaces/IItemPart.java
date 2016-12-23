//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.interfaces;

import org.dimensinfin.evedroid.enums.EIndustryGroup;

//- IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public interface IItemPart extends INamedPart {
	// - M E T H O D - S E C T I O N ..........................................................................
	public String getCategory();

	public String getGroup();

	public EIndustryGroup getIndustryGroup();
}

// - UNUSED CODE ............................................................................................
