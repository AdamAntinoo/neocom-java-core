//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.activity;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.activity.core.PilotPagerActivity;
import org.dimensinfin.eveonline.neocom.fragment.NeoComDashboardFragment;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This activity as an special implementation because has an addition Layout container and has no ListView. So
 * it will not require any DataSource and the fill up of the contents can be simplified and performed on the
 * initial life cycle. We then should copy some code from the more generic Activity and Fragments to be able
 * to compose this special Activity. <br>
 * It's role s to show the Selected Pilot information and to create the list of directors active for this
 * Pilot.
 * 
 * @author Adam Antinoo
 */

public class DirectorsBoardActivity extends PilotPagerActivity {
	public enum EDashboardVariants {
		NEOCOM_DASHBOARD
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}

	private static Logger									logger					= Logger.getLogger("DirectorsBoardActivity");
	private static final EDirectorCode[]	activeDirectors	= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
			EDirectorCode.INDUSTRYDIRECTOR, EDirectorCode.JOBDIRECTOR, EDirectorCode.MARKETDIRECTOR,
			EDirectorCode.FITDIRECTOR };

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Create the pages to show the detailed pilot information and in the next releases the skills and other
	 * information.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		DirectorsBoardActivity.logger.info(">> [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// REFACTOR. This code can be generalized because all Activities code it the same way.
			int page = 0;
			final Bundle extras = this.getIntent().getExtras();
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			this.addPage(
					new NeoComDashboardFragment().setVariant(EDashboardVariants.NEOCOM_DASHBOARD.name()).setExtras(extras),
					page++);
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> DirectorsBoardActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> DirectorsBoardActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		DirectorsBoardActivity.logger.info("<< [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
	}
}

// - UNUSED CODE ............................................................................................
