//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.io.File;

import org.dimensinfin.android.mvc.connector.MVCAppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.INeoComAppConnector;
import org.dimensinfin.eveonline.neocom.storage.NeoComModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class role is to allow the Model to use external environment functions that can change depending on
 * the final implementing platform like Android or Sprint Boot. Allows the coding of the model outside from
 * application of other external dependencies such as file systems or file locations. This is a proxy that
 * will send the messages to the real connector supplied by the application at runtime.
 * 
 * @author Adam Antinoo
 */
public class NeoComAppConnector extends MVCAppConnector implements INeoComAppConnector {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static NeoComAppConnector _singleton = null;

	public static NeoComAppConnector getSingleton() {
		if (null == NeoComAppConnector._singleton) throw new RuntimeException(
				"RTEX [NeoComAppConnector.getSingleton]> Application chain not initialized. All class functionalities disabled.");
		return NeoComAppConnector._singleton;
	}

	// - F I E L D - S E C T I O N ............................................................................
	private final INeoComAppConnector _connector;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComAppConnector(final INeoComAppConnector application) {
		super(application);
		_connector = application;
		NeoComAppConnector._singleton = this;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public File accessAppStorage(final String resourceString) {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.accessAppStorage]> Application connection not defined. Functionality 'accessAppStorage' disabled.");
		return _connector.accessAppStorage(resourceString);
	}

	@Override
	public String getAppFilePath(final int fileresourceid) {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getAppFilePath]> Application connection not defined. Functionality 'getAppFilePath' disabled.");
		return _connector.getAppFilePath(fileresourceid);
	}

	@Override
	public String getAppFilePath(final String fileresourceName) {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getAppFilePath]> Application connection not defined. Functionality 'getAppFilePath' disabled.");
		return _connector.getAppFilePath(fileresourceName);
	}

	@Override
	public AndroidCacheConnector getCacheConnector() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getCacheConnector]> Application connection not defined. Functionality 'getCacheConnector' disabled.");
		return _connector.getCacheConnector();
	}

	@Override
	public ICCPDatabaseConnector getCCPDBConnector() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getCCPDBConnector]> Application connection not defined. Functionality 'getCCPDBConnector' disabled.");
		return _connector.getCCPDBConnector();
	}

	@Override
	public AndroidDatabaseConnector getDBConnector() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getDBConnector]> Application connection not defined. Functionality 'getDBConnector' disabled.");
		return _connector.getDBConnector();
	}

	@Override
	public NeoComModelStore getModelStore() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getModelStore]> Application connection not defined. Functionality 'getModelStore' disabled.");
		return _connector.getModelStore();
	}

	@Override
	public IStorageConnector getStorageConnector() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getStorageConnector]> Application connection not defined. Functionality 'getStorageConnector' disabled.");
		return _connector.getStorageConnector();
	}

	@Override
	public void startTimer() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.startTimer]> Application connection not defined. Functionality 'startTimer' disabled.");
		_connector.startTimer();
	}
}

// - UNUSED CODE ............................................................................................
