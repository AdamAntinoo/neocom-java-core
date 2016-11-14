//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.factory.IndustryLOMResourcesDataSource;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class ManufactureLOMFragment extends AbstractPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore	_store	= null;
	private Bundle				_extras	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ManufactureLOMFragment(AppModelStore store) {
		_store = store;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> ManufactureLOMFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYLOMRESOURCES);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ManufactureLOMFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ManufactureLOMFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< ManufactureLOMFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	@Override
	public String getSubtitle() {
		return "Manufacture - Job Resources";
	}

	public AbstractPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> ManufactureLOMFragment.onStart");
		try {
			if (!_alreadyInitialized) {
				// Get the product created by the job from the blueprint part process.
				final long bpassetid = _extras.getLong(AppWideConstants.extras.EXTRA_BLUEPRINTID);
				final int activity = _extras.getInt(AppWideConstants.extras.EXTRA_BLUEPRINTACTIVITY);
				final Blueprint blueprint = this._store.getPilot().getAssetsManager().searchBlueprintByID(bpassetid);
				final BlueprintPart bppart = new BlueprintPart(blueprint);
				bppart.setActivity(activity);
				// REFACTOR This piece of code may be optimized to return the item identifier for the matching activity of a blueprint.
				final int productID = bppart.getProductID();
				final EveItem productItem = AppConnector.getDBConnector().searchItembyID(productID);
				final IndustryLOMResourcesDataSource ds = new IndustryLOMResourcesDataSource(this._store);
				ds.setBlueprint(bppart);

				// This fragment has a header. Populate it with the datasource header contents.
				ArrayList<AbstractAndroidPart> headerData = ds.getHeaderPartHierarchy();
				for (AbstractAndroidPart headerPart : headerData) {
					addtoHeader(headerPart);
				}

				setDataSource(ds);
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ManufactureLOMFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ManufactureLOMFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< ManufactureLOMFragment.onStart");
	}
}

// - UNUSED CODE ............................................................................................
