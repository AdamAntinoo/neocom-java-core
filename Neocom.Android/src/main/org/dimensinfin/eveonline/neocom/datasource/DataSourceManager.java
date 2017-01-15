//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.datasource;

//- IMPORT SECTION .........................................................................................
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.interfaces.IExtendedDataSource;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Controls and caches all DataSources in use. Will use a single multifield Locator to store and remember used
 * DataSources and their state.
 * 
 * @author Adam Antinoo
 */
public class DataSourceManager implements IDataSourceConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger																logger			= Logger.getLogger("DataSourceManager");

	// - F I E L D - S E C T I O N ............................................................................
	private final HashMap<String, IExtendedDataSource>	dataSources	= new HashMap<String, IExtendedDataSource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Registers this new DataSource on the Manager or returns the source located already on the cache if they
	 * match. This way I will get a cached and already prepared DataSource if I try to create it again.
	 * 
	 * @param newSource
	 *          - new DataSource to add to the Manager
	 * @return the oldest DataSource with the same identifier.
	 */
	public IExtendedDataSource registerDataSource(final IExtendedDataSource newSource) {
		DataSourceLocator locator = newSource.getDataSourceLocator();
		// Search for locator on cache.
		IExtendedDataSource found = dataSources.get(locator.getIdentity());
		// REFACTOR Do not return cached datasources.
		found = null;
		if (null == found) {
			dataSources.put(locator.getIdentity(), newSource);
			DataSourceManager.logger
					.info("-- [DataSourceManager.registerDataSource]> Registering new DataSource: " + locator.getIdentity());
			newSource.connect(this);
			return newSource;
		} else
			return found;
	}
}

// - UNUSED CODE ............................................................................................
