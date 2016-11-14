//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.SafeStopActivity;
import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SplashActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.datasource.IndustryT2JobDataSource;
import org.dimensinfin.evedroid.fragment.core.LinearFragment;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class IndustryJobActionsFragment extends TitledFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int						_fragmentID		= -1;

	// - U I    F I E L D S
	private ViewGroup			_container		= null;
	private BlueprintPart	_blueprint		= null;
	private int						_jobActivity	= ModelWideConstants.activities.MANUFACTURING;
	private AppModelStore	_store				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryJobActionsFragment() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * If the user has set the identifier return the identifier set (This allows to use the Generic by code in
	 * multifragment activities) . Otherwise return the Id of the fragment that would be generated on the layout
	 * XML.
	 */
	public int getIdentifier() {
		if (_fragmentID > 0)
			return _fragmentID;
		else
			return getId();
	}

	/**
	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	 * page activities.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("EVEI", ">> IndustryJobActionsFragment.onCreateView");
		View theView = super.onCreateView(inflater, container, savedInstanceState);
		_container = (ViewGroup) inflater.inflate(R.layout.activity_director, container, false);
		this.setTitle("Industry");
		// Set the subtitle depending on the blueprint Tech and the activity selected.
		if (_jobActivity == ModelWideConstants.activities.INVENTION) {
			this.setSubtitle("T2 BP Invention - Job");
		}
		if (_jobActivity == ModelWideConstants.activities.MANUFACTURING) {
			String tech = _blueprint.getCastedModel().getTech();
			if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) this.setSubtitle("T1 Manufacture - Job");
			if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) this.setSubtitle("T2 Manufacture - Job");
			if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechIII)) this.setSubtitle("T3 Manufacture - Job");
		}

		// Create the content fragments. This is the header
		try {
			LinearFragment frag = (LinearFragment) getFragmentManager().findFragmentByTag(
					Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBHEADER).toString());
			if (null == frag) {
				frag = new LinearFragment();
				frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBHEADER);
				// Register the fragment on the view
				this.getFragmentManager()
						.beginTransaction()
						.add(R.id.fragmentContainer, frag,
								Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBHEADER).toString()).commit();
			}
			IndustryT2JobDataSource ds = new IndustryT2JobDataSource(_store,
					AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBHEADER);
			frag.setDataSource(ds);
			ds.setBlueprint(_blueprint);
		} catch (Exception rtex) {
			Log.e("EVEI", "R> Runtime Exception on IndustryT2Activity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}

		// Create the content fragments. This is the body
		try {
			LinearFragment frag = (LinearFragment) getFragmentManager().findFragmentByTag(
					Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS).toString());
			if (null == frag) {
				frag = new LinearFragment();
				frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS);
				// Register the fragment on the view
				this.getFragmentManager()
						.beginTransaction()
						.add(R.id.fragmentContainer, frag,
								Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS).toString()).commit();
			}
			IndustryT2JobDataSource ds = new IndustryT2JobDataSource(_store,
					AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS);
			frag.setDataSource(ds);
			ds.setBlueprint(_blueprint);
		} catch (Exception rtex) {
			Log.e("EVEI", "R> Runtime Exception on IndustryT2Activity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("EVEI", "<< PageFragment.onCreateView");
		return _container;
	}

	public void setBlueprint(final BlueprintPart bppart) {
		_blueprint = bppart;
	}

	public void setIdentifier(final int id) {
		_fragmentID = id;
	}

	public void setJobActivity(final int activity) {
		_jobActivity = activity;
	}

	public void setStore(final AppModelStore store) {
		_store = store;
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(getActivity(), SplashActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}
}

// - UNUSED CODE ............................................................................................
