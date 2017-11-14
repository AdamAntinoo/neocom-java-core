//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.android.interfaces;

import org.dimensinfin.android.datasource.DataSourceLocator;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.core.model.RootNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IModelGenerator {
	// - M E T H O D - S E C T I O N ........................................................................
	public RootNode collaborate2Model();

	public DataSourceLocator getDataSourceLocator();
}

// - UNUSED CODE ............................................................................................
