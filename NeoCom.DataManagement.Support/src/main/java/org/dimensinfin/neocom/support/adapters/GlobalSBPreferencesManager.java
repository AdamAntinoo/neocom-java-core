package org.dimensinfin.neocom.support.adapters;

import org.dimensinfin.eveonline.neocom.conf.IGlobalPreferencesManager;
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
public class GlobalSBPreferencesManager implements IGlobalPreferencesManager {
	public boolean getBooleanPreference( final String preferenceName ) {
		return GlobalDataManager.getResourceBoolean(preferenceName);
	}

	public boolean getBooleanPreference( final String preferenceName, final boolean defaultValue ) {
		return GlobalDataManager.getResourceBoolean(preferenceName, defaultValue);
	}

	@Override
	public float getFloatPreference(final String preferenceName, final float defaultValue) {
		return GlobalDataManager.getResourceFloat(preferenceName, defaultValue);
	}
	@Override
	public String getStringPreference( final String preferenceName, final String defaultValue ) {
		return null;
	}
}
