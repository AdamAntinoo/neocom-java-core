//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.LocationAssetsPart;
import org.dimensinfin.evedroid.part.PilotInfoPart;
import org.dimensinfin.evedroid.part.RegionPart;
import org.dimensinfin.evedroid.part.ShipPart;
import org.dimensinfin.evedroid.part.TerminatorPart;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class DataSourceFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static IDataSource createDataSource(final int datasourceCode) {
		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO) return new PilotInfoDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_PILOTINFO_T24SELL) return new T2Mod4SellDataSource();
		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_PILOTINFO_SHIPS) return new ShipsDataSource();
		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION) return new AssetsByLocationDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ASSETSAREASTEROIDS) return new AssetsMiningDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ASSETSAREPLANETARY)
		//			return new AssetsPlanetaryDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS) return new ShipsDatasource();

		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_INDUSTRYT2BLUEPRINTS)
		//			return new IndustryT2BlueprintsDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_INDUSTRYT2MANUFACTURE)
		//			return new IndustryT2ManufactureDataSource();
		//	if (datasourceCode == AppWideConstants.fragment.FRAGMENT_MODULESAVAILABLE) return new Items4TypeDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS) return new StackByItemDataSource();
		//		if (datasourceCode == AppWideConstants.fragment.FRAGMENT_ITEMMODULERESOURCES)
		//			return new IndustryManufactureResourcesDataSource();
		return new EmptyDataSource();
	}

	//	public static EveItem getItem() {
	//		return EVEDroidApp.getAppStore().getItem();
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	// - M E T H O D - S E C T I O N ..........................................................................
	public static EveChar getPilot() {
		return EVEDroidApp.getAppStore().getPilot();
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
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
final class AssetsByLocationDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long														serialVersionUID	= -9118872719574627171L;

	// - F I E L D - S E C T I O N ............................................................................
	private final HashMap<String, AbstractAndroidPart>	regions						= new HashMap<String, AbstractAndroidPart>();

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method will initialize the Part hierarchy on the base root part element that will be accessed from
	 * the Adapter through the <code>IDataSource</code> interface when the Adapter is created. The data
	 * generated represents the model hierarchy as it should be represented on memory and the IDataSource call
	 * will instantiate that model to the UI rendering model on a determinate precise instant.<br>
	 * On this particular implementation we should instantiate the lazy parts for the locations from a database
	 * query to get all locations for the selected Pilot.<br>
	 * The new implementation will check the Settings to test if we should group the location into regions or
	 * not. By default we group them but for some small characters this will not be required. We can also make
	 * it automatic so over a predetermined number of locations it will group them or not. This can also be
	 * defined as a setting.
	 */
	@Override
	public void createContentHierarchy() {
		logger.info(">> AssetsByLocationDataSource.createHierarchy");
		// Clear the current list of elements.
		this._root.clear();

		try {
			// Get the list of Locations for this Pilot.
			final AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
			// Depending on the Setting group Locations into Regions
			final ArrayList<EveLocation> locations = manager.getLocations();
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EVEDroidApp.getAppStore()
					.getActivity());
			final String locLimitString = prefs.getString(AppWideConstants.preference.PREF_LOCATIONSLIMIT, "10");
			final int locLimit = 10;

			if (locations.size() > locLimit) {
				for (final EveLocation location : locations) {
					final EveAbstractPart part = (EveAbstractPart) new LocationAssetsPart(location)
							.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
					final String regionName = location.getRegion();
					AbstractAndroidPart hitRegion = this.regions.get(regionName);
					if (null == hitRegion) {
						hitRegion = (AbstractAndroidPart) new RegionPart(new Separator(regionName))
								.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
						this.regions.put(regionName, hitRegion);
						hitRegion.addChild(part);
						this._root.add(hitRegion);
					} else {
						hitRegion.addChild(part);
					}
				}
			} else {
				// The number of locations is not enough to group them. Use the locations as the first level.
				for (final EveLocation location : locations) {
					this._root.add((AbstractAndroidPart) new LocationAssetsPart(location)
							.setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION));
				}
			}
		} catch (final RuntimeException rte) {
			rte.printStackTrace();
			logger.severe("E> There is a problem at: AssetsByLocationDataSource.createHierarchy.");
		}
		logger.info("<< AssetsByLocationDataSource.createHierarchy [" + this._root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		logger.info(">> AssetsDirectorActivity.AssetsByLocationDataSource.getPartHierarchy");
		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		logger.info("<< AssetsDirectorActivity.AssetsByLocationDataSource.getPartHierarchy");
		return super.getPartHierarchy();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}
}

////- CLASS IMPLEMENTATION ...................................................................................
//final class AssetsMiningDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long													serialVersionUID	= -5340657370582559543L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private final HashMap<String, AssetGroupPart>	names							= new HashMap<String, AssetGroupPart>();
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	public void createContentHierarchy() {
//		logger.info(">> AssetsDirectorActivity.AssetsMiningDataSource.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Get the list of Locations for this Pilot.
//		try {
//			AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//			// Depending on the Setting group Locations into Regions
//			ArrayList<Asset> assetsGroup = manager.searchAsset4Group("Mineral");
//			ArrayList<Asset> assetsCategory = manager.searchAsset4Category("Asteroid");
//			for (Asset asset : assetsGroup) {
//				// Check if there an entry for this asset name.
//				AssetGroupPart hit = names.get(asset.getName());
//				if (null == hit) {
//					hit = new AssetGroupPart(new Separator(asset.getName()));
//					names.put(asset.getName(), hit);
//					_root.add(hit);
//				}
//				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
//			}
//			for (Asset asset : assetsCategory) {
//				// Check if there an entry for this asset name.
//				AssetGroupPart hit = names.get(asset.getName());
//				if (null == hit) {
//					hit = new AssetGroupPart(new Separator(asset.getName()));
//					names.put(asset.getName(), hit);
//					_root.add(hit);
//				}
//				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
//			}
//		} catch (RuntimeException sqle) {
//			sqle.printStackTrace();
//			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
//		}
//		logger.info("<< AssetsDirectorActivity.AssetsMiningDataSource.createHierarchy [" + _root.size() + "]");
//	}
//
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		logger.info(">> AssetsDirectorActivity.AssetsMiningDataSource.getPartHierarchy");
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
//		for (AbstractAndroidPart node : _root) {
//			result.add(node);
//			// Check if the node is expanded. Then add its children.
//			if (node.isExpanded()) {
//				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
//				result.addAll(grand);
//			}
//		}
//		_adapterData = result;
//		logger.info("<< AssetsDirectorActivity.AssetsMiningDataSource.getPartHierarchy");
//		return result;
//	}
//
//	public void propertyChange(final PropertyChangeEvent event) {
//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
//					event.getNewValue());
//		}
//	}
//}

////- CLASS IMPLEMENTATION ...................................................................................
//final class AssetsPlanetaryDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long													serialVersionUID	= -8145034625442093770L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private final HashMap<String, AssetGroupPart>	categories				= new HashMap<String, AssetGroupPart>();
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	public void createContentHierarchy() {
//		logger.info(">> AssetsDirectorActivity.AssetsPlanetaryDataSource.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Get the list of Locations for this Pilot.
//		try {
//			AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//			// Depending on the Setting group Locations into Regions
//			ArrayList<Asset> assetsPlanetaryCommodities = manager.searchAsset4Category("Planetary Commodities");
//			ArrayList<Asset> assetsPlanetaryResources = manager.searchAsset4Category("Planetary Resources");
//			for (Asset asset : assetsPlanetaryCommodities) {
//				// Check if there an entry for this asset name.
//				AssetGroupPart hit = categories.get(asset.getName());
//				if (null == hit) {
//					hit = new AssetGroupPart(new Separator(asset.getName()));
//					categories.put(asset.getName(), hit);
//					_root.add(hit);
//				}
//				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
//			}
//			for (Asset asset : assetsPlanetaryResources) {
//				// Check if there an entry for this asset name.
//				AssetGroupPart hit = categories.get(asset.getName());
//				if (null == hit) {
//					hit = new AssetGroupPart(new Separator(asset.getName()));
//					categories.put(asset.getName(), hit);
//					_root.add(hit);
//				}
//				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
//			}
//		} catch (RuntimeException sqle) {
//			sqle.printStackTrace();
//			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
//		}
//		logger.info("<< AssetsDirectorActivity.AssetsPlanetaryDataSource.createHierarchy [" + _root.size() + "]");
//	}
//
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		logger.info(">> AssetsDirectorActivity.AssetsPlanetaryDataSource.getPartHierarchy");
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
//		for (AbstractAndroidPart node : _root) {
//			result.add(node);
//			// Check if the node is expanded. Then add its children.
//			if (node.isExpanded()) {
//				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
//				result.addAll(grand);
//			}
//		}
//		_adapterData = result;
//		logger.info("<< AssetsDirectorActivity.AssetsPlanetaryDataSource.getPartHierarchy");
//		return result;
//	}
//
//	public void propertyChange(final PropertyChangeEvent event) {
//		// Intercept the object changing state and store a reference on a persistent map.
//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
//					event.getNewValue());
//		}
//	}
//}

//- CLASS IMPLEMENTATION ...................................................................................
final class EmptyDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 6229760677978724144L;

	// - F I E L D - S E C T I O N ............................................................................
	//	private final HashMap<String, CategoryGroupPart>	names							= new HashMap<String, CategoryGroupPart>();

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> EmptyDataSource.createHierarchy");
		// Clear the current list of elements.
		this._root.clear();
		this._root.add(new TerminatorPart(new Separator("NO-DATASOURCE")));
		logger.info("<< EmptyDataSource.createHierarchy");
	}
}

////- CLASS IMPLEMENTATION ...................................................................................
///**
// * This DataSource will get access to the special IndustryManager to have track of the assets resulting from
// * some other actions. The list of resources is mandatory to be able to check how many blueprints can be
// * manufactured. Manufacture resources are accounted from the location where the blueprint is located with
// * exception to the MANUFACTURE location that will have the resources reduced or augmented depending on
// * scheduled manufacture jobs or by scheduled market buys.<br>
// * Each blueprint is located on a hierarchical tree with locations at the top, then Hangars or Containers and
// * then the blueprints themselves. Blueprints will be stacked and the resulting stack checked against
// * available resources to set the number of blueprints that are manufacturable. This data will be stored on
// * the Part and will use the new IndustryManager.
// * 
// * @author Adam Antinoo
// */
//final class IndustryT2BlueprintsDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long														serialVersionUID	= -1904434849082581300L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private final HashMap<Long, LocationBlueprintPart>	locations					= new HashMap<Long, LocationBlueprintPart>();
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	/**
//	 * The hierarchy contains two levels of elements. The first level are the locations and can be of two types,
//	 * locations where the blueprints are on the floor (hangar) and locations where the blueprint is inside some
//	 * container. They will not have the default name but the name of the container and will behave more like
//	 * them.<br>
//	 * On the second lever are the blueprints themselves.<br>
//	 * <br>
//	 * The process to get this hierarchy is somehow different from other loops. It will get each of the
//	 * blueprints in order and then locate the proper place where to connect it. If the elements do not exist
//	 * they will be created. If the element exists the blueprint will be aggregated to a stack of the same type.
//	 */
//	public void createContentHierarchy() {
//		logger.info(">> IndustryT2Blueprints.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Get the IndustryManager if not already available and get the T2Blueprints and then compose the Part hierarchy.
//		IndustryManager imanager = new IndustryManager(DataSourceFactory.getPilot());
//		ArrayList<Blueprint> bps = imanager.getT2Blueprints();
//		int counter = 0;
//		for (Blueprint currentbpc : bps) {
//			//	if (counter > 35) continue;
//			long locid = currentbpc.getLocationID();
//			Asset parent = currentbpc.getParentContainer();
//			Blueprint4IndustryPart bppart = new Blueprint4IndustryPart(currentbpc, imanager);
//			bppart.setRenderMode(AppWideConstants.fragment.FRAGMENT_INDUSTRYT2BLUEPRINTS);
//			bppart.getCastedModel().getManufactureIndex();
//			if (null == parent) {
//				add2Location(locid, bppart);
//			} else {
//				add2Container(parent, bppart);
//			}
//			counter++;
//		}
//		logger.info("<< IndustryT2Blueprints.createHierarchy [" + _root.size() + "]");
//	}
//
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		logger.info(">> IndustryT2Blueprints.getPartHierarchy");
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAMED_PART));
//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
//		for (AbstractAndroidPart node : _root) {
//			result.add(node);
//			// Check if the node is expanded. Then add its children.
//			if (node.isExpanded()) {
//				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
//				// Order the list of blueprints by their profit
//				Collections.sort(grand, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_CARD_RATIO));
//				result.addAll(grand);
//			}
//		}
//		_adapterData = result;
//		logger.info("<< IndustryT2Blueprints.getPartHierarchy");
//		return result;
//	}
//
//	@Override
//	public void propertyChange(final PropertyChangeEvent event) {
//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
//					event.getNewValue());
//		}
//	}
//
//	/**
//	 * Adding to a container is like adding to a location. The location is the container's location but the name
//	 * changes to the containers name. So create a <code>LocationBlueprintPart</code> but set the container
//	 * field. We also change the aggregation algorithm to locate the corresponding asset container on another
//	 * list.
//	 * 
//	 * @param container
//	 * @param part
//	 */
//	private void add2Container(final Asset container, final Blueprint4IndustryPart part) {
//		long cid = container.getDAOID();
//		LocationBlueprintPart lochit = locations.get(cid);
//		if (null == lochit) {
//			lochit = (LocationBlueprintPart) new LocationBlueprintPart(part.getCastedModel().getLocation())
//					.setRenderMode(AppWideConstants.fragment.FRAGMENT_INDUSTRYT2BLUEPRINTS);
//			lochit.setContainerLocation(true);
//			lochit.setContainerName(container.getUserLabel());
//			locations.put(cid, lochit);
//			_root.add(lochit);
//		}
//		checkBPCStacking(lochit, part);
//	}
//
//	/**
//	 * Checks of this locations already exists on the table and if not found then creates a new LocationPart
//	 * branch and adds to it the parameter Part.
//	 * 
//	 * @param locationid
//	 * 
//	 * @param part
//	 *          part to be added to the locations. May be an asset or a container.
//	 */
//	private void add2Location(final long locationid, final Blueprint4IndustryPart part) {
//		// Check if the location is already on the array.
//		//	long locid = part.getCastedModel().getLocationID();
//		LocationBlueprintPart hit = locations.get(locationid);
//		if (null == hit) {
//			hit = (LocationBlueprintPart) new LocationBlueprintPart(part.getCastedModel().getLocation())
//					.setRenderMode(AppWideConstants.fragment.FRAGMENT_INDUSTRYT2BLUEPRINTS);
//			locations.put(locationid, hit);
//			_root.add(hit);
//		}
//		checkBPCStacking(hit, part);
//	}
//
//	/**
//	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
//	 * same container so the locationID, the parentContainer and the typeID should match to perform the
//	 * aggregation.
//	 * 
//	 * @param targetContainer
//	 *          the stack that will receive the additional blueprint
//	 * @param part
//	 *          the blueprint part to be added to the hierarchy
//	 */
//	private void checkBPCStacking(final LocationBlueprintPart targetContainer, final Blueprint4IndustryPart part) {
//		int type = part.getTypeID();
//		// Search on the children list for a identical blueprint type.
//		Vector<IGEFNode> childs = targetContainer.getChildren();
//		for (IGEFNode node : childs) {
//			if (node instanceof Blueprint4IndustryPart) {
//				Blueprint4IndustryPart bpcpart = (Blueprint4IndustryPart) node;
//				int childtype = bpcpart.getTypeID();
//				if (childtype == type) {
//					bpcpart.incrementStack();
//					return;
//				}
//			}
//		}
//		targetContainer.addChild(part);
//	}
//
//	private void taskCreation(final AbstractAndroidPart part) {
//		if (part instanceof Blueprint4IndustryPart) {
//			((Blueprint4IndustryPart) part).generateTasks();
//		} else {
//			Vector<IGEFNode> ch = part.getChildren();
//			for (IGEFNode node : ch) {
//				taskCreation((AbstractAndroidPart) node);
//			}
//		}
//	}
//
//	//	private IndustryManager getIndustryManager() {
//	//		if (null == industryManager) industryManager = new IndustryManager(DataSourceFactory.getPilot());
//	//		return industryManager;
//	//	}
//
//}

////- CLASS IMPLEMENTATION ...................................................................................
///**
// * DataSource to retrieve the resources required to perform a T2 Manufacture job from a parameter stack. That
// * stack will contain the pack of T2 BPC to be used on the manufacture process. This is NOT a single blueprint
// * but a pack that can contain a set of equal or similar blueprints. We do not consider differences between
// * them and will use the first hit to search for the ME/TE parameters.<br>
// * The hierarchy are the Actions to complete the job pack. Those actions will contain Tasks that are completed
// * if there are enough resources at the blueprint location or other activities needed to complete the task and
// * then the action.<br>
// * The locations to take on account are the location where is located the blueprint stack.<br>
// * The resources are the character available resources at that location less the resources used on scheduled
// * jobs. Scheduled jobs are only valid at the <code>MANUFACTURE</code> location
// * 
// * @author Adam Antinoo
// */
//final class IndustryT2ManufactureDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long				serialVersionUID	= -1904434849082581300L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private Blueprint4IndustryPart	_bppart						= null;
//
//	private final AppModelStore			store							= null;
//
//	//	private final HashMap<Long, LocationBlueprintPart>	locations					= new HashMap<Long, LocationBlueprintPart>();
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	/**
//	 * The hierarchy contains two levels of elements. The first level are the actions and the second level are
//	 * the tasks to complete and fulfill those actions.
//	 */
//	public void createContentHierarchy() {
//		logger.info(">> IndustryT2ManufactureDataSource.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Check we have received the blueprint part from the Fragment.
//		if (null == _bppart) throw new RuntimeException("Blueprint Part not defined on IndustryT2ManufactureDataSource.");//		Blueprint4IndustryPart bppart = EVEDroidApp.getAppContext().getBlueprintPart();
//		// If there are children that means we have already created the tasks.
//		if (_bppart.getChildren().size() > 0)
//			return;
//		else
//			_bppart.generateT2ManufactureTasks();
//		_root.addAll((Collection<? extends AbstractAndroidPart>) _bppart.getChildren());
//		logger.info("<< IndustryT2ManufactureDataSource.createHierarchy [" + _root.size() + "]");
//	}
//
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		logger.info(">> IndustryT2ManufactureDataSource.getPartHierarchy");
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_RESOURCE_TYPE));
//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
//		for (AbstractAndroidPart node : _root) {
//			result.add(node);
//			// Check if the node is expanded. Then add its children.
//			if (node.isExpanded()) {
//				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
//				result.addAll(grand);
//			}
//		}
//		_adapterData = result;
//		logger.info("<< IndustryT2ManufactureDataSource.getPartHierarchy");
//		return result;
//	}
//
//	/**
//	 * This datasource expects an asset id to retrieve the blueprint.
//	 */
//	public void processArguments(final Bundle arguments) {
//		//Instantiate the pilot from the characterID.
//		long characterid = arguments.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
//		EveChar pilot = null;
//		if (characterid > 0) pilot = EVEDroidApp.getAppModel().searchCharacter(characterid);
//		if (null == pilot) throw new RuntimeException("Unable to continue. Required parameters not define on Extras.");
//		long bpassetid = arguments.getLong(AppWideConstants.extras.EXTRA_BLUEPRINTID);
//		if (bpassetid > 0) {
//			Blueprint blueprint = pilot.getAssetsManager().searchBlueprintByID(bpassetid);
//			setBlueprint(new Blueprint4IndustryPart(blueprint, store));
//		} else
//			throw new RuntimeException("Unable to continue. Required parameters not define on Extras.");
//	}
//
//	@Override
//	public void propertyChange(final PropertyChangeEvent event) {
//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
//					event.getNewValue());
//		}
//	}
//
//	public void setBlueprint(final Blueprint4IndustryPart blueprintPart) {
//		_bppart = blueprintPart;
//	}
//
//	private void taskCreation(final AbstractAndroidPart part) {
//		if (part instanceof Blueprint4IndustryPart) {
//			((Blueprint4IndustryPart) part).generateT2ManufactureTasks();
//		} else {
//			Vector<IGEFNode> ch = part.getChildren();
//			for (IGEFNode node : ch) {
//				taskCreation((AbstractAndroidPart) node);
//			}
//		}
//	}
//}

////- CLASS IMPLEMENTATION ...................................................................................
///**
// * THis class will simulate a request to build the full set of T2 modules (10 runs) for the module selected.
// * This Data Source is only activated for T2 modules and when the pilot has defined a MANUFACTURE location
// * where the algorithm should search for the resources to be requested for the manufacture job.
// * 
// * @author Adam Antinoo
// */
//final class ModuleRequestDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long													serialVersionUID	= -1933862442814003222L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private final HashMap<Long, AbstractAndroidPart>	_moduleList				= new HashMap<Long, AbstractAndroidPart>();
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	public void createContentHierarchy() {
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Initialize the action and task generator that will generate all the requests.
//		TaskGenerationService generator = new TaskGenerationService();
//		generator.initializeData(DataSourceFactory.getPilot().getCharacterID());
//		Job job = new Job(DataSourceFactory.getItem().getName(), DataSourceFactory.getItem().getTypeID());
//		job.setRuns(10);
//		JobPart jpart = new JobPart(job);
//		ArrayList<Action> tasks = generator.generateTasks4Job(job);
//		for (Action action : tasks) {
//			ActionPart apart = new ActionPart(action);
//			jpart.addChild(apart);
//			apart.createHierarchy();
//			_root.add(apart);
//		}
//	}
//
//	@Override
//	public void propertyChange(final PropertyChangeEvent event) {
//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
//					event.getNewValue());
//		}
//	}
//}

//- CLASS IMPLEMENTATION ...................................................................................
final class PilotInfoDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -1934794359407599783L;

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> PilotInfoDataSource.createHierarchy");
		// Clear the current list of elements.
		this._root.clear();
		this._root.add(new PilotInfoPart(EVEDroidApp.getAppStore().getPilot()));
		logger.info("<< PilotInfoDataSource.createHierarchy");
	}
}

////- CLASS IMPLEMENTATION ...................................................................................
//final class Items4TypeDataSource extends AbstractDataSource {
//	private static final long	serialVersionUID	= 2127946744152521775L;
//
//	/**
//	 * This method will initialize the Part hierarchy on the base root part element that will be accessed from
//	 * the Adapter through the <code>IDataSource</code> interface when the Adapter is created. The data
//	 * generated represents the model hierarchy as it should be represented on memory and the IDataSource call
//	 * will instantiate that model to the UI rendering model on a determinate precise instant.<br>
//	 * On this particular implementation we should instantiate the parts for the T2 modules candidates for
//	 * manufacturing and idebtify the first three as the preference group.
//	 */
//	@Override
//	public void createContentHierarchy() {
//		logger.info(">> ModuleDirectorActivity.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Get the list of Locations for this Pilot.
//		//		ArrayList<ModuleCard> cards = getPilot().getModuleCards();
//		HashSet<String> mods = DataSourceFactory.getPilot().getAssetsManager().queryT2ModuleNames();
//		// Cards are already ordered. Mark the first three.
//		int counter = 3;
//		for (String modname : mods) {
//			ModulePart mcpart = new ModulePart(new ModuleCard(AppConnector.getDBConnector().searchItembyName(modname)));
//			// Tag the parts as full size and the first three as prferred.
//			if (counter > 0) {
//				mcpart.setScheduled4Job(true);
//				counter--;
//			}
//			mcpart.setRenderMode(SystemWideConstants.layoutmodel.NORMAL);
//			_root.add(mcpart);
//		}
//		logger.info("<< ModuleDirectorActivity.createHierarchy");
//	}
//
//	@Override
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_CARD_RATIO));
//		return super.getPartHierarchy();
//	}
//
//	@Override
//	public void propertyChange(final PropertyChangeEvent event) {
//	}
//}

//- CLASS IMPLEMENTATION ...................................................................................
final class ShipsDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -1934794359407599783L;

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> DirectorsBoardActivity.ShipsDataSource.createContentHierarchy");
		// Clear the current list of elements.
		this._root.clear();
		// Add the list of assets of ship category
		final ArrayList<Asset> ships = DataSourceFactory.getPilot().getShips();
		for (final Asset asset : ships) {
			final ShipPart spart = (ShipPart) new ShipPart(asset)
					.setRenderMode(AppWideConstants.fragment.FRAGMENT_PILOTINFO_SHIPS);
			this._root.add(spart);
		}
		logger.info("<< DirectorsBoardActivity.ShipsDataSource.createContentHierarchy");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		return super.getPartHierarchy();
	}
}

// - UNUSED CODE ............................................................................................

////- CLASS IMPLEMENTATION ...................................................................................
///**
// * This Data Source will get the list of stacks that belonging to the current pilot have the type id selected.
// * This is performed with a cached query so the result should be quite fast once selected any time.
// * 
// * @author Adam Antinoo
// */
//final class StackByItemDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long	serialVersionUID	= 5106727564899914293L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	@Override
//	public void createContentHierarchy() {
//		logger.info(">> StackByItemDataSource.createHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//
//		// Get the list of Locations for this Pilot.
//		try {
//			AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//			ArrayList<Asset> assets = manager.stacks4Item(DataSourceFactory.getItem());
//			for (Asset as : assets) {
//				AssetPart part = (AssetPart) new AssetPart(as)
//						.setRenderMode(AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS);
//				_root.add(part);
//			}
//		} catch (RuntimeException rtex) {
//			rtex.printStackTrace();
//			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
//		}
//		logger.info("<< StackByItemDataSource.createHierarchy [" + _root.size() + "]");
//	}
//}

////- CLASS IMPLEMENTATION ...................................................................................
//final class T2BlueprintsDataSource extends AbstractDataSource {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long									serialVersionUID	= -1179773840402956318L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//	private final HashMap<String, Blueprint>	bpcs							= new HashMap<String, Blueprint>();
//	private final CharacterStore							store							= null;
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	public void createContentHierarchy() {
//		logger.info(">> DirectorsBoardActivity.ShipsDataSource.createContentHierarchy");
//		// Clear the current list of elements.
//		_root.clear();
//		// Add the list of assets of blueprint category and T2 technology that can be used for manufacturing.
//		AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
//		ArrayList<Blueprint> bps = manager.queryT2Blueprints();
//		for (Blueprint blueprint : bps) {
//			// Filter blueprints for only T2 BPC.
//			if ((!blueprint.isBpo()) && (blueprint.getTech().equalsIgnoreCase("Tech II"))) aggregateBPC(blueprint);
//		}
//		for (Blueprint bpc : bpcs.values()) {
//			Blueprint4IndustryPart bppart = new Blueprint4IndustryPart(bpc, new IndustryT2BlueprintsDataSource(store));
//			bppart.setRenderMode(AppWideConstants.rendermodes.DIRECTORBOARD);
//			_root.add(bppart);
//		}
//		logger.info("<< DirectorsBoardActivity.ShipsDataSource.createContentHierarchy");
//	}
//
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAMED_PART));
//		return super.getPartHierarchy();
//	}
//
//	/**
//	 * Aggregates BPC of the same type and at the same location into a new Asset. The resulting asset has the
//	 * same ID than the initial asset but the number of items on the stack may have changed to aggregate similar
//	 * items.
//	 * 
//	 * @param asset
//	 *          to aggregate
//	 */
//	private void aggregateBPC(final Blueprint asset) {
//		String locator = Integer.valueOf(asset.getTypeID()).toString() + "-"
//				+ Long.valueOf(asset.getLocationID()).toString();
//		Blueprint hit = bpcs.get(locator);
//		if (null == hit)
//			bpcs.put(locator, asset);
//		else
//			hit.setQuantity(hit.getQuantity() + asset.getQuantity());
//	}
//}

//- CLASS IMPLEMENTATION ...................................................................................
//final class T2Mod4SellDataSource extends AbstractDataSource {
//	private static final long	serialVersionUID	= 5474126937814327288L;
//
//	/**
//	 * Collect the T2 modules available on the assets for this character (by a query to the database) and stack
//	 * them into packets of sell stacks. Then collect the market information to show the user when and at what
//	 * price sell the items. Add also cost index information.<br>
//	 * The stacking of the modules is only added when the size of the stack surpasses a predetermined limit.
//	 */
//	@Override
//	public void createContentHierarchy() {
//	}
//
//	@Override
//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
//	}
//}
