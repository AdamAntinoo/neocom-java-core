//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.datasource;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.factory.AbstractIndustryDataSource;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.storage.AppModelStore;
// - CLASS IMPLEMENTATION ...................................................................................
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;

//- CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractNewDataSource extends AbstractIndustryDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractNewDataSource(final AppModelStore store) {
		super(store);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public abstract ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody);
		public abstract ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody);
}
// - UNUSED CODE ............................................................................................
