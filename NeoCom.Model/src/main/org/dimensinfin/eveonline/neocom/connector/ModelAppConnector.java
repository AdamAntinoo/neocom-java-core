//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.GregorianCalendar;

import org.dimensinfin.eveonline.neocom.interfaces.INeoComModelStore;
import org.joda.time.Duration;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
public class ModelAppConnector implements IModelAppConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static ModelAppConnector _singleton = null;

	public static ModelAppConnector getSingleton() {
		if (null == ModelAppConnector._singleton) throw new RuntimeException(
				"RTEX [ModelAppConnector.getSingleton]> Application chain not initialized. All class functionalities disabled.");
		return ModelAppConnector._singleton;
	}

	// - F I E L D - S E C T I O N ............................................................................
	private final IModelAppConnector	_connector;
	private Instant										chrono	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModelAppConnector(final IModelAppConnector application) {
		//	super(application);
		_connector = application;
		ModelAppConnector._singleton = this;
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
	@Override
	public boolean checkExpiration(final Instant timestamp, final long window) {
		if (null == timestamp) return true;
		return this.checkExpiration(timestamp.getMillis(), window);
	}

	@Override
	public boolean checkExpiration(final long timestamp, final long window) {
		if (0 == timestamp) return true;
		final long now = GregorianCalendar.getInstance().getTimeInMillis();
		final long endWindow = timestamp + window;
		if (now < endWindow)
			return false;
		else
			return true;
	}

	@Override
	public ICacheConnector getCacheConnector() {
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
	public IDatabaseConnector getDBConnector() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getDBConnector]> Application connection not defined. Functionality 'getDBConnector' disabled.");
		return _connector.getDBConnector();
	}

	@Override
	public INeoComModelStore getModelStore() {
		if (null == _connector) throw new RuntimeException(
				"RTEX [GymAppConnector.getModelStore]> Application connection not defined. Functionality 'getModelStore' disabled.");
		return _connector.getModelStore();
	}

	@Override
	public void startChrono() {
		chrono = new Instant();
	}

	@Override
	public Duration timeLapse() {
		return new Duration(chrono, new Instant());
	}
}

// - UNUSED CODE ............................................................................................
