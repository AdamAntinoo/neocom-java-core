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
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.core.DataSourceFragment;
import org.dimensinfin.evedroid.fragment.core.NewPagerFragment;
import org.dimensinfin.evedroid.interfaces.IActivityMessageBus;

import android.util.Log;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION
// ...................................................................................
public abstract class EVEPagerActivity extends DefaultNewPagerActivity implements IActivityMessageBus {
	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................
	private DataSourceFragment					_dataFragment	= null;
	private ArrayList<NewPagerFragment>	_fragments		= new ArrayList<NewPagerFragment>();

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * This is the request to get the part hierarchy for a body panel. The parameter determines the variant to
	 * be used. This is finally implemented by the DataSourceFragment.
	 */
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		if (null != _dataFragment)
			return _dataFragment.getBodyPartsHierarchy(panelMarketordersbody);
		else
			return new ArrayList<AbstractAndroidPart>();
	}

	/**
	 * This is the request to get the part hierarchy to set into the header panel. The parameter determines the
	 * variant to be used. This is finally implemented by the DataSourceFragment. The Fragment will have to
	 * handle that list because the header panel is not managed by an adapter but it is a layout container.
	 */
	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
		if (null != _dataFragment)
			return _dataFragment.getHeaderPartsHierarchy(panelMarketordersbody);
		else
			return new ArrayList<AbstractAndroidPart>();
	}

	/**
	 * Event messages may come from the parts through the data source proxy. Those messages should be relayed to
	 * the data source. But the messages from the datasource should instead be passed back to the fragments.
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTMESSAGE_HIERARCHYCOMPLETED)) {
			// Notify all fragments.
			for (final NewPagerFragment frag : getFragments()) {
				frag.signalHierarchyCreationCompleted();
			}
			// Notity the page adapter to refesh contents.
			_pageAdapter.notifyDataSetChanged();
			_pageContainer.invalidate();
		} else {
			_dataFragment.propertyChange(event);
		}
	}

	protected void addFragment(final NewPagerFragment frag) {
		_fragments.add(frag);
		frag.setMessageBus(this);
		// Check if the number of pages is grather than 1 to activate the
		// indicator.
		if (_fragments.size() > 1) {
			activateIndicator();
		}
	}

	protected TitledFragment createBodyFragment(final int fragmentID, final int pageID) {
		Log.i("EVEI", ">> EVEPagerActivity.createBodyFragment"); //$NON-NLS-1$
		try {
			// Now create the UI page. This Activity only has a page so the
			// Indicator should be disabled and gone.
			NewPagerFragment frag = (NewPagerFragment) getFragmentManager()
					.findFragmentByTag(_pageAdapter.getFragmentId(pageID));
			if (null == frag) {
				frag = new NewPagerFragment();
				frag.setIdentifier(fragmentID);
				addFragment(frag);
				_pageAdapter.addPage(frag);
			} else {
				// The fragment is already registered on the FragmentManager.
				// Just reconnect.
				addFragment(frag);
			}
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
			final DataSourceFragment data = (DataSourceFragment) getFragmentManager()
					.findFragmentByTag(Integer.valueOf(fragmentID).toString());
			if (null == data) {
				_dataFragment = new DataSourceFragment();
				_dataFragment.setIdentifier(fragmentID);
				_dataFragment.setDataSource(datasource);
				_dataFragment.addMessageBus(this);
				getFragmentManager().beginTransaction().add(_dataFragment, Integer.valueOf(fragmentID).toString()).commit();
			} else {
				_dataFragment = data;
			}
		} catch (final Exception rtex) {
			Log.e("EVEI", "RT> EVEPagerActivity.createDataSource - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("EVEI", "<< EVEPagerActivity.createDataSource"); //$NON-NLS-1$
	}

	private ArrayList<NewPagerFragment> getFragments() {
		if (null == _fragments) {
			_fragments = new ArrayList<NewPagerFragment>();
		}
		return _fragments;
	}
}
// - UNUSED CODE
// ............................................................................................
