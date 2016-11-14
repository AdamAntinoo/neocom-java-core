//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.model.APIKey;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.part.APIKeyPart;
import org.dimensinfin.evedroid.part.PilotInfoPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotListFragment extends AbstractPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> PilotListFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(AppWideConstants.fragment.FRAGMENT_PILOTLIST);
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> PilotListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> PilotListFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< PilotListFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return "Select Pilot";
	}

	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> PilotListFragment.onStart");
		try {
			if (!_alreadyInitialized) {
				setDataSource(new PilotListDataSource(EVEDroidApp.getAppStore()));
			}
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> PilotListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> PilotListFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< PilotListFragment.onStart");
	}
}

/**
 * Generates the list of keys and then the character authenticated for each key. By default the keys are
 * expanded but the user may choose to collapse them and that information will be stored inside the model. The
 * current version does not store the expand/collapse state at any other place.
 * 
 * @author Adam Antinoo
 */
//- CLASS IMPLEMENTATION ...................................................................................
final class PilotListDataSource extends AbstractDataSource {
	//- S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 4576522670385611140L;

	//- F I E L D - S E C T I O N ............................................................................
	private AppModelStore			_store						= null;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotListDataSource(final AppModelStore store) {
		super();
		if (null != store) {
			this._store = store;
		}
	}

	//- M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> PilotListActivity.PilotListDataSource.createContentHierarchy");
		if (null != this._store) {
			// Clear the current list of elements.
			this._root.clear();
			final HashMap<Integer, APIKey> keys = this._store.getApiKeys();
			for (final APIKey key : keys.values()) {
				final APIKeyPart apipart = new APIKeyPart(key);
				// Add as children the characters for each API.
				for (final EveChar pilot : key.getCharacters().values()) {
					//		EveChar pilot = EVEDroidApp.getSingletonApp().getAppModel().searchCharacter(cid);
					apipart.addChild(new PilotInfoPart(pilot));
				}
				this._root.add(apipart);
			}
		}
		logger.info("<< PilotListActivity.PilotListDataSource.createContentHierarchy");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_APIID_DESC));
		for (final AbstractAndroidPart node : this._root) {
			result.add(node);
			// Check if the node is expanded but test the model. Then add its children.
			if (node.isExpanded()) {
				final ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
				result.addAll(grand);
			}
		}
		this._adapterData = result;
		return result;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}
}
// - UNUSED CODE ............................................................................................
