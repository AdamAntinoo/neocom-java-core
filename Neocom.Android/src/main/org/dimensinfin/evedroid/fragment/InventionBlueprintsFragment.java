//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.IndustryT2InventionDataSource;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class InventionBlueprintsFragment extends AbstractPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - U I    F I E L D S

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	 * page activities.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> IndustryBlueprintsFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYT2INVENTION);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> IndustryBlueprintsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> IndustryBlueprintsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< IndustryBlueprintsFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	@Override
	public String getSubtitle() {
		return "T2 Invention";
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> IndustryBlueprintsFragment.onStart");
		try {
			AppModelStore store = EVEDroidApp.getAppStore();
			if (!_alreadyInitialized) {
				setDataSource(new IndustryT2InventionDataSource(store));
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> IndustryBlueprintsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> IndustryBlueprintsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< IndustryBlueprintsFragment.onStart");
	}
}

// - UNUSED CODE ............................................................................................
