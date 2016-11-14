//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.datasource.JobListDataSource;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobsFragment extends AbstractPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int	_activity	= ModelWideConstants.activities.MANUFACTURING;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getJobActivity() {
		return _activity;
	}

	/**
	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	 * page activities.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> ManufactureJobsFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(AppWideConstants.fragment.FRAGMENT_MANUFACTUREJOBS);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ManufactureJobsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ManufactureJobsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< ManufactureJobsFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	@Override
	public String getSubtitle() {
		String st = "";
		if (_activity == ModelWideConstants.activities.MANUFACTURING) {
			st = "Job List - Manufacture";
		}
		if (_activity == ModelWideConstants.activities.INVENTION) {
			st = "Job List - Invention";
		}
		return st;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> ManufactureJobsFragment.onStart");
		try {
			if (!_alreadyInitialized) {
				// Create the datasource and pass it the activity type.
				JobListDataSource ds = new JobListDataSource(EVEDroidApp.getAppStore());
				ds.setActivityFilter(getJobActivity());
				setDataSource(ds);
				// This fragment has a header. Populate it with the datasource header contents.
				ArrayList<AbstractAndroidPart> headerData = ds.getHeaderPartHierarchy();
				for (AbstractAndroidPart headerPart : headerData) {
					addtoHeader(headerPart);
				}
			}
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ManufactureJobsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ManufactureJobsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< ManufactureJobsFragment.onStart");
	}

	public AbstractPagerFragment setActivity(final int activityType) {
		_activity = activityType;
		return this;
	}
}

// - UNUSED CODE ............................................................................................
