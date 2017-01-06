//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.fragment;

import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.datasource.IndustryT1BlueprintsDataSource;
import org.dimensinfin.eveonline.neocom.datasource.IndustryT2BlueprintsDataSource;
import org.dimensinfin.eveonline.neocom.datasource.IndustryT3BlueprintsDataSource;
import org.dimensinfin.eveonline.neocom.fragment.core.AbstractPagerFragment;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class IndustryBlueprintsFragment extends AbstractPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String _tech = ModelWideConstants.eveglobal.TechI;

	// - U I    F I E L D S

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public String getSubtitle() {
		String st = "";
		if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechIII)) {
			st = "T3 Blueprints";
		}
		if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
			st = "T2 Blueprints";
		}
		if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
			st = "T1 Blueprints";
		}
		return st;
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

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
			if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechIII)) {
				this.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYT3BLUEPRINTS);
			}
			if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				this.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYT2BLUEPRINTS);
			}
			if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
				this.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYT1BLUEPRINTS);
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> IndustryBlueprintsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> IndustryBlueprintsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< IndustryBlueprintsFragment.onCreateView");
		return theView;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> IndustryBlueprintsFragment.onStart");
		if (!_alreadyInitialized) {
			try {
				// Create the right blueprint list depending on the parametrized tech.
				AppModelStore store = AppModelStore.getSingleton();
				if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechIII)) {
					this.setDataSource(new IndustryT3BlueprintsDataSource(store));
				}
				if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					this.setDataSource(new IndustryT2BlueprintsDataSource(store));
				}
				if (_tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
					this.setDataSource(new IndustryT1BlueprintsDataSource(store));
				}
			} catch (final RuntimeException rtex) {
				Log.e("NEOCOM", "RTEX> IndustryBlueprintsFragment.onStart - " + rtex.getMessage());
				rtex.printStackTrace();
				this.stopActivity(new RuntimeException("RTEX> IndustryBlueprintsFragment.onStart - " + rtex.getMessage()));
			}
		}
		super.onStart();
		Log.i("NEOCOM", "<< IndustryBlueprintsFragment.onStart");
	}

	public AbstractPagerFragment setTechLevel(final String tech) {
		_tech = tech;
		return this;
	}
}

// - UNUSED CODE ............................................................................................
