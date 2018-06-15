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
package org.dimensinfin.eveonline.neocom.datamngmt;

import org.dimensinfin.eveonline.neocom.conf.IGlobalPreferencesManager;
import org.dimensinfin.eveonline.neocom.core.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerConfiguration extends SDEExternalDataManager {
	// - S T A T I C - S E C T I O N ..........................................................................
//	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerConfiguration");

	// --- P U B L I C   E N U M E R A T O R S
	public enum EDataUpdateJobs {
		READY, CHARACTER_CORE, CHARACTER_FULL, ASSETDATA, BLUEPRINTDATA, INDUSTRYJOBS, MARKETORDERS, MININGEXTRACTIONS, COLONYDATA, SKILL_DATA
	}

	// --- P R I V A T E   E N U M E R A T O R S
	//	protected enum EModelVariants {
	//		PILOTV1, PILOTV2, CORPORATIONV1, ALLIANCEV1
	//	}

	private enum EManagerCodes {
		PLANETARY_MANAGER, ASSETS_MANAGER
	}

	// --- P R I M A R Y    K E Y   C O N S T R U C T O R S
	public static String constructModelStoreReference( final GlobalDataManager.EDataUpdateJobs type, final long
			identifier ) {
		return new StringBuffer("TS/")
				.append(type.name())
				.append("/")
				.append(identifier)
				.toString();
	}

	public static String constructJobReference( final EDataUpdateJobs type, final long identifier ) {
		return new StringBuffer("JOB:")
				.append(type.name())
				.append("/")
				.append(identifier)
				.toString();
	}

	public static String constructPlanetStorageIdentifier( final int characterIdentifier, final int planetIdentifier ) {
		return new StringBuffer("CS:")
				.append(Integer.valueOf(characterIdentifier).toString())
				.append(":")
				.append(Integer.valueOf(planetIdentifier).toString())
				.toString();
	}

	// --- C O N F I G U R A T I O N   S E C T I O N
	public static String SERVER_DATASOURCE = "tranquility";

	private static IConfigurationProvider configurationManager = null/*new GlobalConfigurationProvider(null)*/;

	private static IConfigurationProvider accessConfigurationManager() {
		// If the Configuration is not already loaded then connect a default configuration provider.
		if (null == configurationManager)
			throw new RuntimeException("No configuration manager present. Running with no configuration.");
		return configurationManager;
	}

	public static void connectConfigurationManager( final IConfigurationProvider newconfigurationProvider ) {
		configurationManager = newconfigurationProvider;
		// Load configuration properties into default values.
		// ESI Server selection
		SERVER_DATASOURCE = GlobalDataManager.getResourceString("R.esi.authorization.datasource", "tranquility");
		// ESI Data load.
		SDEExternalDataManager.initialize();
	}

	public static String getResourceString( final String key ) {
		return accessConfigurationManager().getResourceString(key);
	}

	public static String getResourceString( final String key, final String defaultValue ) {
		return accessConfigurationManager().getResourceString(key, defaultValue);
	}

	public String getResourcePropertyString( final String key ) {
		return accessConfigurationManager().getResourceString(key);
	}

	public Integer getResourcePropertyInteger( final String key ) {
		return getResourceInt(key);
	}

	public static int getResourceInt( final String key ) {
		try {
			return Integer.valueOf(accessConfigurationManager().getResourceString(key)).intValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static int getResourceInt( final String key, final int defaultValue ) {
		try {
			return Integer.valueOf(accessConfigurationManager().getResourceString(key
					, Integer.valueOf(defaultValue).toString())).intValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static long getResourceLong( final String key ) {
		try {
			return Long.valueOf(accessConfigurationManager().getResourceString(key)).longValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static long getResourceLong( final String key, final long defaultValue ) {
		try {
			return Long.valueOf(accessConfigurationManager().getResourceString(key
					, Long.valueOf(defaultValue).toString())).longValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static boolean getResourceBoolean( final String key ) {
		return Boolean.valueOf(accessConfigurationManager().getResourceString(key)).booleanValue();
	}

	public static boolean getResourceBoolean( final String key, final boolean defaultValue ) {
		return Boolean.valueOf(accessConfigurationManager().getResourceString(key
				, Boolean.valueOf(defaultValue).toString())).booleanValue();
	}

	public String getEveOnlineServerDatasource() {
		return SERVER_DATASOURCE;
	}

	// --- P R E F E R E N C E S   S E C T I O N
	private static IGlobalPreferencesManager preferencesprovider = null;

	public static void connectPreferencesManager( final IGlobalPreferencesManager newPreferencesProvider ) {
		preferencesprovider = newPreferencesProvider;
	}

	public static IGlobalPreferencesManager getDefaultSharedPreferences() {
		if (null != preferencesprovider) return preferencesprovider;
		else
			throw new NeoComRuntimeException("[GlobalDataManagerConfiguration.getDefaultSharedPreferences]> Preferences provider not " +
					"configured " +
					"into the Global area.");
	}
}

// - UNUSED CODE ............................................................................................
//[01]
