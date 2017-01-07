//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.activity.core;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This abstract Activity will collect all the common code that is being used on the new Activity pattern.
 * Most of the new activities change minor actions on some methods while sharing all the rest of the code.<br>
 * This class implements a generic Activity with a swipe gesture multi page layout and Titled pages that will
 * show names only if the number of pages is more than 1. Current implementation ises a cicle indicator but
 * will be transistioned to a Titled indicator. The base code will take care of the menu and the Action tool
 * bar.
 * 
 * @author Adam Antinoo
 */
public abstract class PilotPagerActivity extends AbstractPagerActivity {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	//	protected AppModelStore _store = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("EVEI", ">> PilotPagerActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// Process the parameters into the context.
			final Bundle extras = this.getIntent().getExtras();
			if (null == extras) throw new RuntimeException(
					"RT IndustryDirectorActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
			//Instantiate the pilot from the characterID.
			final long characterid = extras.getLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name());
			if (characterid > 0) {
				// Initialize the access to the global structures.
				AppModelStore store = AppModelStore.getSingleton();
				store.activatePilot(characterid);
				store.activateActivity(this);
			}
		} catch (final Exception rtex) {
			Log.e("EVEI", "RTEX> PilotPagerActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> PilotPagerActivity.onCreate - " + rtex.getMessage()));
		}
		Log.i("EVEI", "<< PilotPagerActivity.onCreate"); //$NON-NLS-1$
	}
}
//- UNUSED CODE ............................................................................................
