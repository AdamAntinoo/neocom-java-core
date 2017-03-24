//	PROJECT:        NeoCom.model
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.poc.asb.planetaryoptimizer.main;

import java.util.GregorianCalendar;

import org.dimensinfin.eveonline.neocom.connector.ICCPDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.IStorageConnector;
import org.dimensinfin.eveonline.neocom.core.INeoComModelStore;
import org.springframework.boot.web.support.SpringBootServletInitializer;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AppAbstractConnector extends SpringBootServletInitializer implements IConnector {
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
	/**
	 * Checks that the current parameter timestamp is still on the frame of the window.
	 * 
	 * @param timestamp
	 *          the current and last timestamp of the object.
	 * @param window
	 *          time span window in milliseconds.
	 */
	public boolean checkExpiration(final long timestamp, final long window) {
		// logger.info("-- Checking expiration for " + timestamp + ". Window " + window);
		if (0 == timestamp) return true;
		final long now = GregorianCalendar.getInstance().getTimeInMillis();
		final long endWindow = timestamp + window;
		if (now < endWindow)
			return false;
		else
			return true;
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
