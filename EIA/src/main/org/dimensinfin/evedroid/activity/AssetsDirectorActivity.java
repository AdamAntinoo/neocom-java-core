//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.AssetsFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.evedroid.model.EveChar;

import android.os.Bundle;
import android.util.Log;

//- CLASS IMPLEMENTATION ...................................................................................
public class AssetsDirectorActivity extends PilotPagerActivity implements INeoComDirector {
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
		if (checkPilot.getAssetCount() > 0)
			return true;
		else
			return false;
	}

	public int getIconReferenceActive() {
		return R.drawable.assets;
	}

	public int getIconReferenceInactive() {
		return R.drawable.assetsdirectordimmed;
	}

	public String getName() {
		return "Assets";
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> AssetsDirectorActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// Create the pages that form this Activity. Each page implemented
			// by a Fragment.
			int page = 0;
			addPage(new AssetsFragment().setFilter(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION), page++);
			addPage(new AssetsFragment().setFilter(AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS), page++);
			addPage(new AssetsFragment().setFilter(AppWideConstants.fragment.FRAGMENT_ASSETSMATERIALS), page++);
		} catch (Exception rtex) {
			Log.e("NEOCOM", "RTEX> AssetsDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> AssetsDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		Log.i("NEOCOM", "<< AssetsDirectorActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE
// ............................................................................................
