//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.fragment.JobsFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.evedroid.model.EveChar;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobDirectorActivity extends PilotPagerActivity implements INeoComDirector {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Checks if there are the conditions to activate this particular manager. Each one will have it different
	 * rules to reach the activation point.<br>
	 * The BPOManager need that there are at least one BPO on the list of assets of the pilot.
	 */
	public boolean checkActivation(final EveChar checkPilot) {
		if (checkPilot.getIndustryJobs().size() > 0)
			return true;
		else {
			// Fire a forced download of the job list.
			checkPilot.cleanJobs();
			EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(checkPilot.getCharacterID());
			return false;
		}
	}

	public int getIconReferenceActive() {
		return R.drawable.jobdirector;
	}

	@Override
	public int getIconReferenceInactive() {
		return R.drawable.jobdirectordimmed;
	}

	public String getName() {
		return "Job Plan";
	}

	/**
	 * Create the set of pages to manage the list of completed, running and pending jobs including the ones that
	 * are created by the application to simulate the Industry recommendations.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> JobDirectorActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Cache the list of jobs to be used on this Director session
		EVEDroidApp.getAppStore().getPilot().cleanJobs();
		EVEDroidApp.getAppStore().getPilot().getIndustryJobs();
		try {// Reset the page position.
			int page = 0;
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			addPage(new JobsFragment().setActivity(ModelWideConstants.activities.MANUFACTURING), page++);
			addPage(new JobsFragment().setActivity(ModelWideConstants.activities.INVENTION), page++);
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> JobDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> JobDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		Log.i("NEOCOM", "<< JobDirectorActivity.onCreate"); //$NON-NLS-1$
	}
}
//- UNUSED CODE ............................................................................................
