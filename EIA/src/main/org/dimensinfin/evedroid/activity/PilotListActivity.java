//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.activity.core.AbstractPagerActivity;
import org.dimensinfin.evedroid.fragment.PilotListFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotListActivity extends AbstractPagerActivity {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * On the Activity create phase we will set the layout, then create the action bar and all other UI elements
	 * and finally creates and sets the fragments. This is to avoid the multiple creation and addition of more
	 * fragments when the activity is put again on the foreground.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("EVEI", ">> PilotListActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Disable go home for this activity since this is home.
		this._actionBar.setDisplayHomeAsUpEnabled(false);
		try {
			final AppModelStore store = EVEDroidApp.getAppStore();
			store.activateActivity(this);
			addPage(new PilotListFragment(), 0);
		} catch (final Exception rtex) {
			Log.e("EVEI", "RTEX> Runtime Exception on PilotListActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		Log.i("EVEI", "<< PilotListActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE ............................................................................................
