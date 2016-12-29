//  PROJECT:        EIA
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.activity.core;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

// - CLASS IMPLEMENTATION ...................................................................................
public class SettingsActivity extends PreferenceActivity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("SettingsActivity");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@SuppressWarnings("deprecation")
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}

// - UNUSED CODE ............................................................................................
