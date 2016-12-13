//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.activity.core.AbstractPagerActivity;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.fragment.PilotListFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotListActivity extends AbstractPagerActivity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("PilotListActivity");

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
		PilotListActivity.logger.info(">> [PilotListActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Disable go home for this activity since this is home.
		_actionBar.setDisplayHomeAsUpEnabled(false);
		try {
			// Process the parameters into the context. This initial Activity is the only one with no parameters.
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			int page = 0;
			// Register this Activity as the current active Activity.
			AppModelStore.getSingleton().activateActivity(this);
			this.addPage(new PilotListFragment().setVariant(EVARIANT.CAPSULEER_LIST), page++);
		} catch (final Exception rtex) {
			PilotListActivity.logger.severe("[PilotListActivity.onCreate]> RTEX> Runtime Exception." + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(rtex);
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		PilotListActivity.logger.info("<< [PilotListActivity.onCreate]"); //$NON-NLS-1$
	}
}
// - UNUSED CODE
// ............................................................................................
