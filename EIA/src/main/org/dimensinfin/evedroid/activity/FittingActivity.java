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

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.FittingListActivity.EFittingVariants;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.fragment.FittingFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import android.os.Bundle;

//- CLASS IMPLEMENTATION ...................................................................................
public class FittingActivity extends PilotPagerActivity implements INeoComDirector {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger logger = Logger.getLogger("FittingActivity");

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
		FittingActivity.logger.info(">> [FittingActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {// Reset the page position.
			int page = 0;
			// Get the parameters from the bundle. If not defined then use the demo.
			final Bundle extras = this.getIntent().getExtras();
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			this.addPage(new FittingFragment().setVariant(EFittingVariants.FITTING_MANUFACTURE.name()).setExtras(extras),
					page++);
			//			} else {
			//				addPage(new FittingFragment().setVariant(AppWideConstants.EFragment.FITTING_MANUFACTURE), page++);
			//			}
		} catch (final Exception rtex) {
			FittingActivity.logger.severe("RTEX> FittingActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> FittingActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		FittingActivity.logger.info("<< [FittingActivity.onCreate]"); //$NON-NLS-1$
	}
}
// - UNUSED CODE ............................................................................................
