//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.android.datasource;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.dimensinfin.android.interfaces.IModelGenerator;

// - CLASS IMPLEMENTATION ...................................................................................
public class ModelGeneratorStore {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger																		logger		= Logger.getLogger("ModelGeneratorManager");
	private static final Hashtable<String, IModelGenerator>	adapters	= new Hashtable<String, IModelGenerator>();

	public static IModelGenerator registerGenerator(final IModelGenerator newGenerator) {
		DataSourceLocator locator = newGenerator.getDataSourceLocator();
		// Search for locator on cache.
		IModelGenerator found = ModelGeneratorStore.adapters.get(locator.getIdentity());
		// REFACTOR Code to remove caching from the Model generator
		found = null;
		if (null == found) {
			ModelGeneratorStore.adapters.put(locator.getIdentity(), newGenerator);
			ModelGeneratorStore.logger
					.info("-- [ModelGeneratorManager.registerGenerator]> Registering new Generator: " + locator.getIdentity());
			//			// Connect the Generator to the Manager.
			//			newGenerator.connect(this);
			return newGenerator;
		} else
			return found;
	}
	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
