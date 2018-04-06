//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.conf;

import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;

/**
 * This class is specific for the Data Management and the pure java instances. It will replicate the Preferences interface
 * found in Android so the code should be compatible with the Preferences Manager implemented in Android platform.
 * <p>
 * The java and Spring Boot implementation for the preferences will export all the data found at the Properties so it would not
 * need another data source for exporting the information.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalPreferencesManager implements IGlobalPreferencesManager {
	// - S T A T I C - S E C T I O N ..........................................................................
//	private static Logger logger = LoggerFactory.getLogger("GlobalPreferencesManager");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean getBooleanPreference( final String preferenceName ) {
		return GlobalDataManager.getResourceBoolean(preferenceName);
	}

	public boolean getBooleanPreference( final String preferenceName, final boolean defaultValue ) {
		return GlobalDataManager.getResourceBoolean(preferenceName, defaultValue);
	}
	public boolean getBoolean( final String preferenceName ) {
		return GlobalDataManager.getResourceBoolean(preferenceName);
	}

	public boolean getBoolean( final String preferenceName, final boolean defaultValue ) {
		return GlobalDataManager.getResourceBoolean(preferenceName, defaultValue);
	}
}

// - UNUSED CODE ............................................................................................
//[01]
