//	PROJECT:        NeoCom.model
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.poc.asb.planetaryoptimizer.main;

import org.dimensinfin.eveonline.neocom.connector.ICCPDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.IStorageConnector;
import org.dimensinfin.eveonline.neocom.core.INeoComModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AppAbstractConnector implements IConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger logger = Logger.getLogger("org.dimensinfin.evedroid.connector");
	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AppAbstractConnector() {
	}

	@Override
	public void addCharacterUpdateRequest(long characterID) {
		throw new RuntimeException(
				"Application connector not defined. Functionality 'addCharacterUpdateRequest' disabled.");
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkExpiration(final long timestamp, final long window) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public String getAppFilePath(final int fileresourceid) {
		throw new RuntimeException("Application connector not defined. Functionality 'getAppFilePath' disabled.");
	}

	//	public ICache getCacheConnector() {
	//		throw new RuntimeException("Application connector not defined. Functionality 'getCacheConnector' disabled.");
	//	}
	//
	//	public IDataSourceConnector getDataSourceConector() {
	//		throw new RuntimeException("Application connector not defined. Functionality 'getDataSourceConector' disabled.");
	//	}

	public String getAppFilePath(final String fileresourcename) {
		throw new RuntimeException("Application connector not defined. Functionality 'getAppFilePath' disabled.");
	}

	@Override
	public ICCPDatabaseConnector getCCPDBConnector() {
		throw new RuntimeException("Application connector not defined. Functionality 'getCCPDBConnector' disabled.");
	}

	public IDatabaseConnector getDBConnector() {
		throw new RuntimeException("Application connector not defined. Functionality 'getDBConnector' disabled.");
	}

	@Override
	public INeoComModelStore getModelStore() {
		throw new RuntimeException("Application connector not defined. Functionality 'getModelStore' disabled.");
	}

	public String getResourceString(final int reference) {
		throw new RuntimeException("Application connector not defined. Functionality 'getResourceString' disabled.");
	}

	public IStorageConnector getStorageConnector() {
		throw new RuntimeException("Application connector not defined. Functionality 'getStorageConnector' disabled.");
	}

	public boolean sdcardAvailable() {
		throw new RuntimeException("Application connector not defined. Functionality 'sdcardAvailable' disabled.");
	}
}

// - UNUSED CODE ............................................................................................
