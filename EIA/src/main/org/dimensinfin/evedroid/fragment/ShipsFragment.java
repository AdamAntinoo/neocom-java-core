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
import org.dimensinfin.evedroid.factory.DataSourceFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.LocationIndustryPart;
import org.dimensinfin.evedroid.part.ShipPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipsFragment extends AbstractPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int	_flavour	= AppWideConstants.fragment.FRAGMENT_SHIPSBYLOCATION;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getFlavour() {
		return _flavour;
	}

	/**
	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	 * page activities.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> ShipsFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(getFlavour());
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ShipsFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> ShipsFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< ShipsFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	@Override
	public String getSubtitle() {
		String st = "";
		if (_flavour == AppWideConstants.fragment.FRAGMENT_SHIPSBYLOCATION) {
			st = "Ships - by Location";
		}
		if (_flavour == AppWideConstants.fragment.FRAGMENT_SHIPSBYCLASS) {
			st = "Ships - by Class";
		}
		return st;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> ShipsFragment.onStart");
		if (!_alreadyInitialized) {
			try {
				//				if (_flavour == AppWideConstants.fragment.FRAGMENT_SHIPSBYLOCATION) {
				// Create the datasource and pass it the activity type.
				ShipsDatasource ds = new ShipsDatasource(_flavour);
				//					ds.setActivityFilter(getFlavour());
				setDataSource(ds);
				//				}
				//				if (_flavour == AppWideConstants.fragment.FRAGMENT_SHIPSBYCLASS) {
				//					// Create the datasource and pass it the activity type.
				//					AssetsShipsDataSource ds = new AssetsShipsDataSource();
				////					ds.setActivityFilter(getFlavour());
				//					setDataSource(ds);
				//				}
			} catch (final RuntimeException rtex) {
				Log.e("NEOCOM", "RTEX> ShipsFragment.onStart - " + rtex.getMessage());
				rtex.printStackTrace();
				stopActivity(new RuntimeException("RTEX> ShipsFragment.onStart - " + rtex.getMessage()));
			}
		}
		super.onStart();
		Log.i("NEOCOM", "<< ShipsFragment.onStart");
	}

	public AbstractPagerFragment setFlavour(final int flavour) {
		_flavour = flavour;
		return this;
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class ShipsDatasource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long													serialVersionUID	= 7810087592108417570L;

	// - F I E L D - S E C T I O N ............................................................................
	private int																				_version					= 0;																					;
	private final HashMap<String, GroupPart>					_categories				= new HashMap<String, GroupPart>();
	private final HashMap<Long, LocationIndustryPart>	_locations				= new HashMap<Long, LocationIndustryPart>();

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipsDatasource(final int version) {
		_version = version;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> ShipsDatasource.createHierarchy");
		// Clear the current list of elements.
		this._root.clear();

		// Get the list of Locations for this Pilot.
		try {
			final AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
			// Depending on the Setting group Locations into Regions
			final ArrayList<Asset> assetsShips = manager.searchAsset4Category("Ship");
			// Depending on version add the Ships classified by category or by location
			for (Asset ship : assetsShips) {
				long locid = ship.getLocationID();
				String category = ship.getGroupName();
				AssetPart sppart = null;
				// Check if the ship is packaged.
				if (ship.isPackaged()) {
					sppart = (AssetPart) new AssetPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
				} else {
					sppart = (AssetPart) new ShipPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
				}
				sppart.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS);
				if (_version == AppWideConstants.fragment.FRAGMENT_SHIPSBYLOCATION) {
					add2Location(locid, sppart);
				}
				if (_version == AppWideConstants.fragment.FRAGMENT_SHIPSBYCLASS) {
					add2Category(category, sppart);
				}
			}
		} catch (final RuntimeException sqle) {
			sqle.printStackTrace();
			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		logger.info("<< ShipsDatasource.createHierarchy [" + this._root.size() + "]");
	}

	/**
	 * Checks of this locations already exists on the table and if not found then creates a new LocationPart
	 * branch and adds to it the parameter Part.
	 * 
	 * @param locationid
	 * 
	 * @param sppart
	 *          part to be added to the locations. May be an asset or a container.
	 */
	private void add2Location(final long locationid, final AssetPart sppart) {
		// Check if the location is already on the array.
		LocationIndustryPart hit = _locations.get(locationid);
		if (null == hit) {
			hit = (LocationIndustryPart) new LocationIndustryPart(sppart.getCastedModel().getLocation())
					.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			_locations.put(locationid, hit);
			_root.add(hit);
		}
		hit.addChild(sppart);
	}

	private void add2Category(final String category, final AssetPart sppart) {
		// Check if the location is already on the array.
		GroupPart hit = _categories.get(category);
		if (null == hit) {
			hit = new GroupPart(new Separator(category));
			_categories.put(category, hit);
			_root.add(hit);
		}
		hit.addChild(sppart);
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		logger.info(">> ShipsDatasource.getPartHierarchy");
		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (final AbstractAndroidPart node : this._root) {
			result.add(node);
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) {
				final ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
				result.addAll(grand);
			}
		}
		this._adapterData = result;
		logger.info("<< ShipsDatasource.getPartHierarchy");
		return result;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// Intercept the object changing state and store a reference on a persistent map.
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}
}

// - UNUSED CODE ............................................................................................
