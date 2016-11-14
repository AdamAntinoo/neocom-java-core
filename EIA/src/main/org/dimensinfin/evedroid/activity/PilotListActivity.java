//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

import java.util.logging.Logger;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.activity.core.AbstractPagerActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.PilotListFragment;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotListActivity extends AbstractPagerActivity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("AbstractAndroidPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the name of this activity
	 * 
	 * @return
	 */
	public String getName() {
		return "Capsuleer List";
	}

	/**
	 * On the Activity create phase we will set the layout, then create the action bar and all other UI elements
	 * and finally creates and sets the fragments. This is to avoid the multiple creation and addition of more
	 * fragments when the activity is put again on the foreground.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		logger.info(">> PilotListActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Disable go home for this activity since this is home.
		this._actionBar.setDisplayHomeAsUpEnabled(false);
		try {
			// Process the parameters into the context. This initial Activity is
			// the only one with no parameters.
			// Create the pages that form this Activity. Each page implemented
			// by a Fragment.
			int page = 0;
			// Register this Activity as the current active Activity.
			EVEDroidApp.getAppStore().activateActivity(this);
			addPage(new PilotListFragment().setVariant(AppWideConstants.EFragment.CAPSULEER_LIST), page++);
		} catch (final Exception rtex) {
			Log.e("EVEI", "RTEX> Runtime Exception on PilotListActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		logger.info("<< PilotListActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE
// ............................................................................................
