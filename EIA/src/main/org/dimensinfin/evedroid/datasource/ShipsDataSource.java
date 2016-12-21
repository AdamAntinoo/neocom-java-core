//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.datasource;

import java.util.Collection;
import java.util.HashMap;

import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.Region;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.model.ShipLocation;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//- CLASS IMPLEMENTATION ...................................................................................
public class ShipsDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 7810087592108417570L;

	//	private ArrayList<NeoComAsset>						ships							= null;
	private final HashMap<Long, Region>				_regions					= new HashMap<Long, Region>();
	private final HashMap<Long, ShipLocation>	_locations				= new HashMap<Long, ShipLocation>();
	private final HashMap<String, Separator>	_categories				= new HashMap<String, Separator>();

	//	private int																				_version					= 0;;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipsDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The data model exported by this method can have two or three levels. If the region grouping is off then
	 * we return the list of locations that contain ships. If the Region grouping is on then we return the list
	 * of Regions that point also to the contained Locations. <br>
	 * The DataSource keeps the list of ships and compares it to the current list so if it is the same then we
	 * do not do any processing. <br>
	 * There are two models that can be returned, the ships by Location model and also the ship by Category.
	 * Both are enerated at the same time. <br>
	 * The first action is to go to the Pilot asset list and get all the assets with the Category Ship. This
	 * will return a list of Assets we can transform into Ships. There are two classes for this. The packaged
	 * ships are simple assets that will not expand to anything else while the other class, the active ships can
	 * have contents and a fit. That ones are the ones being converted to Ships. <br>
	 * This new ships will inherit the content management properties of a Container and some of the logic of the
	 * ShipPart.
	 * 
	 * @return
	 */
	public RootNode collaborate2Model() {
		SpecialDataSource.logger.info(">> [ShipsDatasource.collaborate2Model]");
		try {
			// Get the complete list of ships. Compare it to the current list if it exists.
			final AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
			// Depending on the Setting group Locations into Regions
			Collection<NeoComAsset> assetsShips = manager.accessShips();
			// Process the list into the classifiers.
			for (NeoComAsset ship : assetsShips) {
				long locid = ship.getLocationID();
				String category = ship.getGroupName();
				this.add2Location(locid, ship);
				this.add2Category(category, ship);
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			SpecialDataSource.logger.severe(
					"RTEX> ShipsDatasource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
		this.setupOutputModel();
		// [01]
		SpecialDataSource.logger.info("<< ShipsDatasource.collaborate2Model");
		return _dataModelRoot;
	}

	private void add2Category(final String category, final NeoComAsset ship) {
		// Check if the location is already on the array.
		Separator hit = _categories.get(category);
		if (null == hit) {
			hit = new Separator(category);
			_categories.put(category, hit);
		}
		hit.addChild(ship);
	}

	/**
	 * Checks of this locations already exists on the table and if not found then creates a new LocationPart
	 * branch and adds to it the parameter Part.
	 * 
	 * @param locationid
	 * 
	 * @param ship
	 *          part to be added to the locations. May be an asset or a container.
	 */
	private void add2Location(final long locationid, final NeoComAsset ship) {
		// Check if the location is already on the array.
		ShipLocation hit = _locations.get(locationid);
		if (null == hit) {
			hit = ShipLocation.createFromLocation(ship.getLocation());
			// Add the new location to the list of locations and to the Regions
			this.add2Region(hit);
			_locations.put(locationid, hit);
		}
		hit.addChild(ship);
	}

	private void add2Region(final ShipLocation location) {
		Region region = _regions.get(location.getID());
		if (null == region) {
			region = new Region(location.getRegion());
			_regions.put(location.getRegionID(), region);
		}
		region.addChild(location);
	}

	private boolean ifGroupLocations() {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(EVEDroidApp.getAppStore().getActivity());
		final String locLimitString = prefs.getString(AppWideConstants.preference.PREF_LOCATIONSLIMIT,
				AppConnector.getResourceString(R.string.pref_numberOfLocations_default));
		// Check for the special value of unlimited.
		if (locLimitString.equalsIgnoreCase("Unlimited")) return false;
		// Convert the stored preference value to a number.
		int locLimit = 10;
		try {
			locLimit = Integer.parseInt(locLimitString);
		} catch (NumberFormatException nex) {
			locLimit = 10;
		}
		if (_locations.size() > locLimit)
			return true;
		else
			return false;
	}

	/**
	 * Sets up the model that should be connected to the model root. We can return 3 different models:
	 * <ul>
	 * <li>The list of Regions if the Region aggregation is activated and the variant is the Ships By Location
	 * fragment.</li>
	 * <li>The list of Locations also if the variant is the Ships By Location fragment.</li>
	 * <li>The list of Categories if the variant is the Ships By Category.</li>
	 * </ul>
	 */
	private void setupOutputModel() {
		if (null == _dataModelRoot) {
			_dataModelRoot = new RootNode();
		} else {
			_dataModelRoot.clean();
		}
		if (this.getVariant() == EVARIANT.SHIPS_BYLOCATION.name()) if (this.ifGroupLocations()) {
			for (Region node : _regions.values()) {
				_dataModelRoot.addChild(node);
			}
		} else {
			for (ShipLocation node : _locations.values()) {
				_dataModelRoot.addChild(node);
			}
		}
		if (this.getVariant() == EVARIANT.SHIPS_BYCLASS.name()) {
			for (Separator node : _categories.values()) {
				_dataModelRoot.addChild(node);
			}
		}
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
	//		logger.info(">> ShipsDatasource.getPartHierarchy");
	//		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		for (final AbstractAndroidPart node : this._root) {
	//			result.add(node);
	//			// Check if the node is expanded. Then add its children.
	//			if (node.isExpanded()) {
	//				final ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
	//				result.addAll(grand);
	//			}
	//		}
	//		this._adapterData = result;
	//		logger.info("<< ShipsDatasource.getPartHierarchy");
	//		return result;
	//	}

}
// - UNUSED CODE ............................................................................................
// [01]
//		
//		
//		// Process the list of ships to get their locations because that is the first level of the Data model
//		locations = getShipsLocations();
//
//		// The model is the list of current regtistered api keys with their characters.
//		// Add the keys to the model root node. If the root is already on place then the model is already loaded.
//		//		if (null == _dataModelRoot) {
//		setDataModel(new RootNode());
//		//		}
//		// Add all the nodes to the new root
//		for (APIKey key : keys.values()) {
//			_dataModelRoot.addChild(key);
//			logger.info("-- ShipsDatasource.collaborate2Model-Adding " + key.getKeyID() + " to the _dataModelRoot");
//		}

//	private Object getShipsLocations() {
//		// Get the list of Locations for this Pilot.
//		try {
//			final AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//			// Depending on the Setting group Locations into Regions
//			final ArrayList<Asset> assetsShips = manager.searchAsset4Category("Ship");
//			// Depending on version add the Ships classified by category or by location
//			for (Asset ship : assetsShips) {
//				long locid = ship.getLocationID();
//				String category = ship.getGroupName();
//
//				//				AssetPart sppart = null;
//				//				// Check if the ship is packaged.
//				//				if (ship.isPackaged()) {
//				//					sppart = (AssetPart) new AssetPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
//				//				} else {
//				//					sppart = (AssetPart) new ShipPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
//				//				}
//				//				sppart.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS);
//				if (getVariant() == AppWideConstants.EFragment.FRAGMENT_SHIPSBYLOCATION) {
//					add2Location(locid, sppart);
//				}
//				if (getVariant() == AppWideConstants.EFragment.FRAGMENT_SHIPSBYCLASS) {
//					add2Category(category, sppart);
//				}
//			}
//		} catch (final RuntimeException sqle) {
//			sqle.printStackTrace();
//			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
//		}
//	}

//	@Override
//	public void createContentHierarchy() {
//		logger.info(">> ShipsDatasource.createHierarchy");
//		// Clear the current list of elements.
//		this._root.clear();
//
//		// Get the list of Locations for this Pilot.
//		try {
//			final AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//			// Depending on the Setting group Locations into Regions
//			final ArrayList<Asset> assetsShips = manager.searchAsset4Category("Ship");
//			// Depending on version add the Ships classified by category or by location
//			for (Asset ship : assetsShips) {
//				long locid = ship.getLocationID();
//				String category = ship.getGroupName();
//				AssetPart sppart = null;
//				// Check if the ship is packaged.
//				if (ship.isPackaged()) {
//					sppart = (AssetPart) new AssetPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
//				} else {
//					sppart = (AssetPart) new ShipPart(ship).setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
//				}
//				sppart.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS);
//				if (_version == AppWideConstants.fragment.FRAGMENT_SHIPSBYLOCATION) {
//					add2Location(locid, sppart);
//				}
//				if (_version == AppWideConstants.fragment.FRAGMENT_SHIPSBYCLASS) {
//					add2Category(category, sppart);
//				}
//			}
//		} catch (final RuntimeException sqle) {
//			sqle.printStackTrace();
//			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
//		}
//		logger.info("<< ShipsDatasource.createHierarchy [" + this._root.size() + "]");
//	}
