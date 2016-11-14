//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.connector;

// - IMPORT SECTION .........................................................................................
import org.joda.time.Duration;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class is to allow coding the model outside from application of other external dependencies such as
 * file systems or file locations. This is a proxy that will send the messages to the real connector supplied
 * by the application on runtime.
 * 
 * @author Adam Antinoo
 * 
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

	public static Duration timeLapse() {
		return new Duration(chrono, new Instant());
	}

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AppConnector() {
	}


	// - M E T H O D - S E C T I O N ..........................................................................
	// - F I E L D - S E C T I O N ............................................................................
}

// - UNUSED CODE ............................................................................................
