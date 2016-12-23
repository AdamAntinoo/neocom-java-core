//	PROJECT:        NeoCom.model
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.evedroid.datasource;

import org.dimensinfin.evedroid.interfaces.IExtendedDataSource;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IDataSourceConnector {

	public IExtendedDataSource registerDataSource(IExtendedDataSource datasource);
}

// - UNUSED CODE ............................................................................................
