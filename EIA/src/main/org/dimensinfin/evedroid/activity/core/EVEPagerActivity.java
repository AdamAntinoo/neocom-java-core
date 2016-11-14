//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity.core;

//- IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.IActivityMessageBus;
import org.dimensinfin.evedroid.fragment.core.DataSourceFragment;
import org.dimensinfin.evedroid.fragment.core.NewPagerFragment;

import android.util.Log;

/**
 * @author Adam Antinoo
 */
//- CLASS IMPLEMENTATION ...................................................................................
public abstract class EVEPagerActivity extends DefaultNewPagerActivity implements IActivityMessageBus {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private DataSourceFragment					_dataFragment	= null;
	private ArrayList<NewPagerFragment>	_fragments		= new ArrayList<NewPagerFragment>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This is the request to get the part hierarchy for a body panel. The parameter determines the variant to
	 * be used. This is finally implemented by the DataSourceFragment.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		if (null != this._dataFragment)
			return this._dataFragment.getBodyPartsHierarchy(panelMarketordersbody);
		else
			return new ArrayList<AbstractAndroidPart>();
	}

	/**
	 * This is the request to get the part hierarchy to set into the header panel. The parameter determines the
	 * variant to be used. This is finally implemented by the DataSourceFragment. The Fragment will have to
	 * handle that list because the header panel is not managed by an adapter but it is a layout container.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
		if (null != this._dataFragment)
			return this._dataFragment.getHeaderPartsHierarchy(panelMarketordersbody);
		else
			return new ArrayList<AbstractAndroidPart>();
	}

	/**
	 * Event messages may come from the parts through the data source proxy. Those messages should be relayed to
	 * the data source. But the messages from the datasource should instead be passed back to the fragments.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTMESSAGE_HIERARCHYCOMPLETED)) {
			// Notify all fragments.
			for (final NewPagerFragment frag : getFragments())
				frag.signalHierarchyCreationCompleted();
			// Notity the page adapter to refesh contents.
			this._pageAdapter.notifyDataSetChanged();
			this._pageContainer.invalidate();
		} else
			this._dataFragment.propertyChange(event);
	}

	protected void addFragment(final NewPagerFragment frag) {
		this._fragments.add(frag);
		frag.setMessageBus(this);
		// Check if the number of pages is grather than 1 to activate the indicator.
		if (this._fragments.size() > 1) activateIndicator();
	}

	protected TitledFragment createBodyFragment(final int fragmentID, final int pageID) {
		Log.i("EVEI", ">> EVEPagerActivity.createBodyFragment"); //$NON-NLS-1$
		try {
			// Now create the UI page. This Activity only has a page so the Indicator should be disabled and gone.
			NewPagerFragment frag = (NewPagerFragment) getFragmentManager().findFragmentByTag(
					this._pageAdapter.getFragmentId(pageID));
			if (null == frag) {
				frag = new NewPagerFragment();
				frag.setIdentifier(fragmentID);
				addFragment(frag);
				this._pageAdapter.addPage(frag);
			} else
				//The fragment is already registered on the FragmentManager. Just reconnect.
				addFragment(frag);
			return frag;
		} catch (final Exception rtex) {
			Log.e("EVEI", "RT> EVEPagerActivity.createBodyFragment - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
			return null;
		}
	}

	protected void createDataSource(final int fragmentID, final IDataSource datasource) {
		Log.i("EVEI", ">> EVEPagerActivity.createDataSource"); //$NON-NLS-1$
		try {
			// First create the data fragment that contains the model.
			final DataSourceFragment data = (DataSourceFragment) getFragmentManager().findFragmentByTag(
					Integer.valueOf(fragmentID).toString());
			if (null == data) {
				this._dataFragment = new DataSourceFragment();
				this._dataFragment.setIdentifier(fragmentID);
				this._dataFragment.setDataSource(datasource);
				this._dataFragment.addMessageBus(this);
				getFragmentManager().beginTransaction().add(this._dataFragment, Integer.valueOf(fragmentID).toString())
						.commit();
			} else
				this._dataFragment = data;
		} catch (final Exception rtex) {
			Log.e("EVEI", "RT> EVEPagerActivity.createDataSource - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("EVEI", "<< EVEPagerActivity.createDataSource"); //$NON-NLS-1$
	}

	private ArrayList<NewPagerFragment> getFragments() {
		if (null == this._fragments) this._fragments = new ArrayList<NewPagerFragment>();
		return this._fragments;
	}
}
//- UNUSED CODE ............................................................................................
