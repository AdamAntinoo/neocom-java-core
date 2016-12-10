//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.connector;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.evedroid.core.INeoComModelStore;
import org.joda.time.Duration;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class role is to allow the Model to use external environment funtions that can change depeding on the final
 * imeplementing platforma like Android or Sprint Boot. Allows the coding of the model outside from application of
* other external dependencies such as
 * file systems or file locations. This is a proxy that will send the messages to the real connector supplied
 * by the application at runtime.
 * 
 * @author Adam Antinoo
 */
public class AppConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static IConnector	connection	= null;
	private static Instant		chrono			= null;

	public static boolean checkExpiration(final Instant timestamp, final long window) {
		if (null == timestamp) return true;
		return checkExpiration(timestamp.getMillis(), window);
	}

	public static boolean checkExpiration(final long timestamp, final long window) {
		if (null != connection)
			return connection.checkExpiration(timestamp, window);
		else
			throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public static String getAppFilePath(final int fileresourceid) {
		if (null != connection)
			return connection.getAppFilePath(fileresourceid);
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getAppFilePath' disabled.");
	}

	public static String getAppFilePath(final String fileresourcename) {
		if (null != connection)
			return connection.getAppFilePath(fileresourcename);
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getAppFilePath' disabled.");
	}

	//	public static ICache getCacheConnector() {
	//		if (null != connection)
	//			return connection.getCacheConnector();
	//		else
	//			throw new RuntimeException("Application connector not defined. Functionality 'getCacheConnector' disabled.");
	//	}

	public static IDatabaseConnector getDBConnector() {
		if (null != connection)
			return connection.getDBConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getDBConnector' disabled.");
	}

	public static String getResourceString(final int reference) {
		if (null != connection)
			return connection.getResourceString(reference);
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getResourceString' disabled.");
	}

	public static IStorageConnector getStorageConnector() {
		if (null != connection)
			return connection.getStorageConnector();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getStorageConnector' disabled.");
	}

	public static boolean sdcardAvailable() {
		if (null != connection)
			return connection.sdcardAvailable();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'sdcardAvailable' disabled.");
	}

	public static void setConnector(final IConnector androidApp) {
		if (null == androidApp) throw new RuntimeException("Required connector is not properly defined.");
		connection = androidApp;
	}
	public static IConnector getSingleton() {
		if (null != connection)
			return connection.getSingleton();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getSingleton' disabled.");
	}

	public static void startChrono() {
		chrono = new Instant();
	}
	public static INeoComModelStore getModelStore() {
		if (null != connection)
			return connection.getModelStore();
		else
			throw new RuntimeException("Application connector not defined. Functionality 'getModelStore' disabled.");
	}



	public static Duration timeLapse() {
		return new Duration(chrono, new Instant());
	}

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AppConnector() {
	}

	// - F I E L D - S E C T I O N ............................................................................
	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
