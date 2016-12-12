//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.ShipsFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.evedroid.model.NeoComCharacter;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipDirectorActivity extends PilotPagerActivity implements INeoComDirector {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Checks if there are the conditions to activate this particular manager. This manager is activated if the
	 * capsuleer has fitted ships.
	 */
	public boolean checkActivation(final NeoComCharacter checkPilot) {
		if (checkPilot.getShips().size() > 0)
			return true;
		else
			//			// Fire a forced download of the job list.
			//			checkPilot.cleanJobs();
			//			EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(checkPilot.getCharacterID());
			return false;
	}

	public int getIconReferenceActive() {
		return R.drawable.shipsdirector;
	}

	public int getIconReferenceInactive() {
		return R.drawable.shipsdirectordimmed;
	}

	public String getName() {
		return "Ships";
	}

	/**
	 * Create the set of pages to manage the list of completed, running and pending jobs including the ones that
	 * are created by the application to simulate the Industry recommendations.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> ShipDirectorActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {// Reset the page position.
			int page = 0;
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			addPage(new ShipsFragment().setVariant(AppWideConstants.EFragment.FRAGMENT_SHIPSBYLOCATION), page++);
			//			addPage(new ShipsFragment().setVariant(AppWideConstants.EFragment.FRAGMENT_SHIPSBYCLASS), page++);
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> ShipDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ShipDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		Log.i("NEOCOM", "<< ShipDirectorActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE ............................................................................................
