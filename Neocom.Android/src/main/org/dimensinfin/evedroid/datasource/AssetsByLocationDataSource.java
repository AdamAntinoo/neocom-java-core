//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.datasource;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Region;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This DataSource will manage the elements to shown on the assets list for a character in the page that lists
 * those assets by Region - Location - Container order. If the number of locations is greater than a
 * predefined setting (that will be modifiable on the UI on the settings page) then the locations will be
 * grouped into Regions. If the number is lower the locations will be the first display level. <br>
 * The second and next levels will be composed with Containers, Ships and Assets. Those levels will only be
 * read from the database only if the container or location is expanded so this will use a lazy evaluation
 * pattern do read and generate the momory structures that contain the assets.<br>
 * To manage this asset information the DataSource will heavily interface with the model AssetsManager that
 * will have stored all the downloaded information (so a change on an Activity will not clear that data) and
 * all the functionalities to manage assets when they are stored on the database and also if they are stored
 * in local memory.
 * 
 * @author Adam Antinoo
 */
public class AssetsByLocationDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("AssetsByLocationDataSource");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsByLocationDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * We will build the list of assets ordered by locations. This is quite easy since we have already selected
	 * the assets from the database and they are already stored on the AssetsManager. If the number of locations
	 * is below the limit then do not use the Regions.
	 */
	public RootNode collaborate2Model() {
		AbstractDataSource.logger.info(">> `[AssetsByLocationDataSource.collaborate2Model]");
		_dataModelRoot = new RootNode();
		try {
			final AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
			//		 HashMap<Long, EveLocation> locations = manager.getLocations();
			if (this.showRegions(manager.getLocations().size())) {
				for (Region region : manager.getRegions().values()) {
					_dataModelRoot.addChild(region);
				}
			} else {
				for (EveLocation location : manager.getLocations().values()) {
					_dataModelRoot.addChild(location);
				}
			}
			//
			//			// Clear the current list of elements.
			//			//		state = true;
			//			//		super.createContentHierarchy();
			//			//		this.createContentModel();
			//			// Get the list of Locations for this Pilot.
			//			final AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
			//
			//			// Get access to the location data for this character.
			//			locations = manager.getLocations();
			//			// Depending on the Setting group Locations into Regions
			//			if (this.groupLocations()) {
			//				for (final EveLocation location : locations) {
			//					final EveAbstractPart part = (EveAbstractPart) new LocationAssetsPart(location)
			//							.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
			//					final String regionName = location.getRegion();
			//					AbstractAndroidPart hitRegion = regions.get(regionName);
			//					if (null == hitRegion) {
			//						hitRegion = (AbstractAndroidPart) new RegionPart(new Separator(regionName))
			//								.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
			//						regions.put(regionName, hitRegion);
			//						hitRegion.addChild(part);
			//						_root.add(hitRegion);
			//					} else {
			//						hitRegion.addChild(part);
			//					}
			//				}
			//			} else {
			//				// The number of locations is not enough to group them. Use the locations as the first level.
			//				for (final EveLocation location : locations) {
			//					_root.add((AbstractAndroidPart) new LocationAssetsPart(location)
			//							.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION));
			//				}
			//			}
			//
		} catch (final RuntimeException rte) {
			rte.printStackTrace();
			AbstractDataSource.logger.severe("E> There is a problem at: AssetsByLocationDataSource.createHierarchy.");
		}
		AbstractDataSource.logger.info("<< [AssetsByLocationDataSource.collaborate2Model]> model size: " + _root.size());
		return _dataModelRoot;
	}

	//	public void createContentModel() {
	//		Log.i("NEOCOM", ">> AssetsByLocationDataSource.createContentModel");
	//		// Clear the current list of elements.
	//		modelSource.clear();
	//		try {
	//			// Get the list of Locations for this Pilot.
	//			final AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
	//
	//			// Get access to the location data for this character.
	//			locations = manager.getLocations();
	//			// Depending on the Setting group Locations into Regions
	//			if (this.groupLocations()) {
	//				for (final EveLocation location : locations) {
	//					final String regionName = location.getRegion();
	//					RegionGroup hitRegion = regionModel.get(regionName);
	//					if (null == hitRegion) {
	//						hitRegion = new RegionGroup(regionName);
	//						regionModel.put(regionName, hitRegion);
	//						hitRegion.addChild(location);
	//						modelSource.add(hitRegion);
	//					} else {
	//						hitRegion.addChild(location);
	//					}
	//				}
	//			} else {
	//				// The number of locations is not enough to group them. Use the locations as the first level.
	//				for (final EveLocation location : locations) {
	//					modelSource.add(location);
	//				}
	//			}
	//		} catch (final RuntimeException rtex) {
	//			rtex.printStackTrace();
	//			Log.e("NEOCOM", "RTEX> AssetsByLocationDataSource.createContentModel - " + rtex.getMessage());
	//		}
	//		AbstractDataSource.logger.info("<< AssetsByLocationDataSource.createContentModel [" + modelSource.size() + "]");
	//	}
	//
	//	@Override
	//	public ArrayList<AbstractAndroidPart> getBodyParts() {
	//		AbstractDataSource.logger.info(">> AssetsFragment.AssetsByLocationDataSource.getPartHierarchy");
	//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//		//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		//		for (AbstractAndroidPart node : _root) {
	//		//			// Check if the node is expanded. Then add its children.
	//		//			if (node.isExpanded()) {
	//		//				result.add(node);
	//		//				result.add(new TerminatorPart(new Separator("")));
	//		//				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
	//		//				// Order the list of blueprints by their profit
	//		//				Collections.sort(grand, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//		//				result.addAll(grand);
	//		//				result.add(new TerminatorPart(new Separator("")));
	//		//			} else {
	//		//				result.add(node);
	//		//			}
	//		//		}
	//		//		_adapterData = result;
	//		AbstractDataSource.logger.info("<< AssetsDirectorActivity.AssetsByLocationDataSource.getPartHierarchy");
	//		return super.getBodyParts();
	//	}

	@Override
	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		return new ArrayList<AbstractAndroidPart>();
	}

	//	@Override
	//	public void propertyChange(final PropertyChangeEvent event) {
	//		super.propertyChange(event);
	//		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
	//			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
	//					event.getNewValue());
	//		}
	//	}
	//
	//	@Override
	//	public String toString() {
	//		final StringBuffer buffer = new StringBuffer("AssetsFragment.AssetsByLocationDataSource [");
	//		//		buffer.append(getWeight()).append(" ");
	//		buffer.append("state:").append(state);
	//		buffer.append("count:").append(_root.size());
	//		buffer.append("model count:").append(modelSource.size());
	//		buffer.append("]");
	//		return buffer.toString();
	//	}

	private boolean showRegions(final int numberLocations) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(AppModelStore.getSingleton().getActivity());
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
		if (numberLocations > locLimit)
			return true;
		else
			return false;
	}
}

// - UNUSED CODE ............................................................................................
