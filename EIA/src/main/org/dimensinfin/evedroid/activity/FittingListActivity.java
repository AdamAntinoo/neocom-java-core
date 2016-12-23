//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.fragment.FittingListFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.evedroid.model.NeoComCharacter;

import android.os.Bundle;

//- CLASS IMPLEMENTATION ...................................................................................
public class FittingListActivity extends PilotPagerActivity implements INeoComDirector {
	public enum EFittingVariants {
		FITTING_LIST, FITTING_MANUFACTURE
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger logger = Logger.getLogger("FittingListActivity");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Until the fitting loader is implemented the result is ever true and the Director should be active.
	 */
	public boolean checkActivation(final NeoComCharacter checkPilot) {
		return true;
	}

	public int getIconReferenceActive() {
		return R.drawable.fitsdirector;
	}

	public int getIconReferenceInactive() {
		return R.drawable.fitdirectordimmed;
	}

	public String getName() {
		return "Fitting";
	}

	/**
	 * Create the test page to show the list of Manufacture actions. The real fitting Activity may have more
	 * pages than this test page..
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		FittingListActivity.logger.info(">> [FittingListActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {// Reset the page position.
			int page = 0;
			// Get the parameters from the bundle. If not defined then use the demo.
			final Bundle extras = this.getIntent().getExtras();
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			this.addPage(new FittingListFragment().setVariant(EFittingVariants.FITTING_LIST.name()).setExtras(extras),
					page++);
		} catch (final Exception rtex) {
			FittingListActivity.logger.warning("RTEX> FittingListActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> FittingListActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		FittingListActivity.logger.info("<< [FittingListActivity.onCreate]"); //$NON-NLS-1$
	}
}
// - UNUSED CODE ............................................................................................
