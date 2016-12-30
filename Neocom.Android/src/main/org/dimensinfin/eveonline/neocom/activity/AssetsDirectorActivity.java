//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.activity;

import java.util.logging.Logger;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.core.PilotPagerActivity;
import org.dimensinfin.eveonline.neocom.fragment.AssetsFragment;
import org.dimensinfin.eveonline.neocom.interfaces.INeoComDirector;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import android.os.Bundle;

//- CLASS IMPLEMENTATION ...................................................................................
public class AssetsDirectorActivity extends PilotPagerActivity implements INeoComDirector {
	public enum EAssetVariants {
		ASSETS_BYLOCATION, ASSETS_BYCATEGORY, ASSETS_MATERIALS
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger logger = Logger.getLogger("AssetsDirectorActivity");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Checks if there are the conditions to activate this particular manager. Each one will have it different
	 * rules to reach the activation point.<br>
	 * The BPOManager need that there are at least one BPO on the list of assets of the pilot.
	 */
	public boolean checkActivation(final NeoComCharacter checkPilot) {
		if (checkPilot.getAssetCount() > 0)
			return true;
		else
			return false;
	}

	public int getIconReferenceActive() {
		return R.drawable.assets;
	}

	public int getIconReferenceInactive() {
		return R.drawable.assetsdimmed;
	}

	public String getName() {
		return "Assets";
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		AssetsDirectorActivity.logger.info(">> [AssetsDirectorActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			int page = 0;
			// Get the parameters from the bundle. If not defined then use the demo.
			final Bundle extras = this.getIntent().getExtras();
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			this.addPage(new AssetsFragment().setVariant(EAssetVariants.ASSETS_BYLOCATION.name()).setExtras(extras), page++);
			//			this.addPage(new AssetsFragment().setVariant(EAssetVariants.ASSETS_MATERIALS.name()).setExtras(extras), page++);
		} catch (Exception rtex) {
			AssetsDirectorActivity.logger.warning("RTEX> AssetsDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> AssetsDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		AssetsDirectorActivity.logger.info("<< [AssetsDirectorActivity.onCreate]"); //$NON-NLS-1$
	}
}
// - UNUSED CODE
// ............................................................................................
