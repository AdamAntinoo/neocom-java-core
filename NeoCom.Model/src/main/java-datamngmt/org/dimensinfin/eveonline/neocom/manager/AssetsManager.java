//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.dimensinfin.eveonline.neocom.model.Region;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.model.SpaceContainer;
import org.dimensinfin.eveonline.neocom.database.entity.TimeStamp;
import org.joda.time.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class interfaces all access to the assets database in name of a particular character. It tries to
 * cache and manage all data in favor of speed versus space. Takes care to update memory references so model
 * data used on the different pages does not gets changes by other pages if not necessary. But if the same
 * data is to be represented at different locations this class should make sure that the same data is
 * returned.<br>
 * At the same time different instances allow to separate asset usage and simulate assets changes by the
 * scheduled industry jobs created by the user. <br>
 * Adapted to new design and the new asset list management. Now the list downloaded from CCP is not
 * hierarchically ordered so the location is lost in replacement for the parent id. With this new structure is
 * no longer possible to locate the assets in a single location or system on a database search. The new object
 * management requires to get from the database the complete list of assets, order and classify them on memory
 * and store at the assets manager. This will need to have a protected AssetManager on each character so it is
 * not the same one used on destructive industry operations that will change the asset contents.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class AssetsManager extends AbstractManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -8502099148768297876L;
	private static Logger logger = Logger.getLogger("AssetsManager");

	private transient Dao<NeoComAsset, String> assetDao = null;

	// - L O C A T I O N   M A N A G E M E N T
	// - A S S E T   M A N A G E M E N T
	public long totalAssets = -1;
	public double totalAssetsValue = 0.0;
	private TimeStamp assetsCacheTime = null;

	/** Probably redundant with containers. */
	private final HashMap<Long, NeoComAsset> assetsAtContainer = new HashMap<Long, NeoComAsset>();
	/** The new list of ships with their state and their contents. An extension of containers. */
	private final HashMap<Long, NeoComAsset> ships = new HashMap<Long, NeoComAsset>();
	private final HashMap<Long, ArrayList<NeoComAsset>> assetsAtLocationCache = new HashMap<Long, ArrayList<NeoComAsset>>();
	private final HashMap<String, ArrayList<NeoComAsset>> assetsAtCategoryCache = new HashMap<String, ArrayList<NeoComAsset>>();
	private final HashMap<Integer, ArrayList<NeoComAsset>> stacksByItemCache = new HashMap<Integer, ArrayList<NeoComAsset>>();
	private final ArrayList<NeoComBlueprint> t1BlueprintCache = new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint> t2BlueprintCache = new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint> bpoCache = new ArrayList<NeoComBlueprint>();

	// USED BY OTHER CALSSES TO BE REVIEWED
	public final HashMap<Long, ArrayList<NeoComAsset>> assetCache = new HashMap<Long, ArrayList<NeoComAsset>>();
	public final HashMap<Long, ArrayList<NeoComAsset>> asteroidCache = new HashMap<Long, ArrayList<NeoComAsset>>();
	public final String iconName = "assets.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	private transient HashMap<Long, NeoComAsset> assetMap = new HashMap<Long, NeoComAsset>();
	private Vector<NeoComAsset> unlocatedAssets = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager (final Credential credential) {
		super(credential);
		// Load the timestamp from the database to control the refresh status of all the assets.
		this.readTimeStamps();
		jsonClass = "AssetsManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Updates the list of assets, regions and locations from the database. Go to the database to download all
	 * the assets for this pilot or corporation and then process them one by one to ge the complete parenship
	 * and store the result on the assets caches for later use.
	 */
	public void accessAllAssets () {
		try {
			// Initialize the model
			regions.clear();
			locations.clear();
			containers.clear();
			assetsAtContainer.clear();
			ships.clear();
			int assetCounter = 0;
			try {
				// Read all the assets for this character if not done already.
				ArrayList<NeoComAsset> assets = this.getAllAssets();
				// Move the list to a processing map.
				assetMap = new HashMap<Long, NeoComAsset>(assets.size());
				for (NeoComAsset asset : assets) {
					assetMap.put(asset.getAssetId(), asset);
				}
				// Process the map until all elements are removed.
				Long key = assetMap.keySet().iterator().next();
				assetCounter++;
				NeoComAsset point = assetMap.get(key);
				while (null != point) {
					this.processElement(point);
					key = assetMap.keySet().iterator().next();
					point = assetMap.get(key);
				}
			} catch (NoSuchElementException nsee) {
				// Reached the end of the list of assets to process.
				AssetsManager.logger.info("-- [AssetsManager.accessAllAssets]> No more assets to process");
			} finally {
				AssetsManager.logger.info("-- [AssetsManager.accessAllAssets]> Assets processed: " + assetCounter);
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			AssetsManager.logger
					.severe("RTEX> AssetsByLocationDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
	}

	public Collection<NeoComAsset> accessShips () {
		if ( null == ships )
			return new ArrayList<NeoComAsset>();
		else
			return ships.values();
	}

	public TimeStamp getAssetsCacheTime () {
		return assetsCacheTime;
	}

	@Override
	public String getJsonClass () {
		return jsonClass;
	}

	public ExtendedLocation getLocationById (final long id) {
		// Search for the location on the Location list.
		for (Long key : locations.keySet()) {
			if ( key == id ) return locations.get(key);
		}
		return null;
	}

	/**
	 * Returns the list of different locations where this character has assets. The locations are the unique
	 * location ids that can be on the same or different systems. If a system has assets in more that one
	 * station or in space the number of ids that have the same system in common may be greater that 1.
	 */
	public Hashtable<Long, ExtendedLocation> getLocations () {
		// If the list is empty the go to the database and get the assets
		if ( null == locations ) {
			this.initialize();
		}
		return locations;
	}

	public String getOrderingName () {
		return "Assets Manager";
	}

	public List<NeoComAsset> getShips () {
		return this.searchAsset4Category("Ship");
	}

	/**
	 * Counts the number of assets that belong to this character. If the current number of assets is negative
	 * then this signals that the number has not been previously calculated.
	 *
	 * @return the number of assets
	 */
	public long getTotalAssetsNumber () {
		if ( totalAssets == -1 ) {
			try {
				this.accessDaos();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				queryBuilder.setCountOf(true).where().eq("ownerID", getCredentialIdentifier());
				totalAssets = assetDao.countOf(queryBuilder.prepare());
			} catch (SQLException sqle) {
				AssetsManager.logger.info("W> [AssetsManager.getTotalAssetsNumber]> Problem calculating the number of assets. Pilot: " + getCredentialName());
			}
		}
		return totalAssets;
	}

	/**
	 * This is the initialization code that we should always use when we need to operate with a loaded
	 * AssetsManager.<br>
	 * The initialization will load all the Locations and some of the counters. The method processed the result
	 * to generate the root list of Regions, then the space Locations and then their contents in the case the
	 * Location has been downloaded. There is an optimization that if the manager is already initialized then it
	 * is not initialized again. This also simplifies the code because now there is no more need to check for
	 * the initialization state.
	 */
	@Override
	public AssetsManager initialize () {
		if ( !initialized ) {
			// INITIALIZE - Initialize the number of assets.
			this.getTotalAssetsNumber();
			// INITIALIZE - Initialize the Locations and the Regions
			List<NeoComAsset> locs = queryAllAssetLocations(getCredentialIdentifier());
			regions.clear();
			locations.clear();
			// Process the locations to a new list of Regions.
			for (NeoComAsset asset : locs) {
				long locid = asset.getLocationId();
				this.processLocation(locid);
			}
			initialized = true;
		}
		return this;
	}

	/**
	 * Checks if that category was requested before and it is on the cache. If found returns that list.
	 * Otherwise go to the database for the list.
	 */
	public List<NeoComAsset> searchAsset4Category (final String category) {
		//	Select assets for the owner and with an specific category.
		List<NeoComAsset> assetsCategoryList = new ArrayList<NeoComAsset>();
		assetsCategoryList = assetsAtCategoryCache.get(category);
		if ( null == assetsCategoryList ) {
			assetsCategoryList = ModelAppConnector.getSingleton().getDBConnector()
			                                      .searchAsset4Category(getCredentialIdentifier(), category);
			assetsAtCategoryCache.put(category, (ArrayList<NeoComAsset>) assetsCategoryList);
		} else {
			AssetsManager.logger.info("~~ [AssetsManager.searchAsset4Category]> Cache hit [SELECT CATEGORY=" + category
					+ " OWNERID = " + getCredentialIdentifier() + "]");
		}
		return assetsCategoryList;
	}

	public ArrayList<NeoComAsset> searchAsset4Group (final String group) {
		//	Select assets for the owner and with an specific category.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			this.accessDaos();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", getCredentialIdentifier());
			where.and();
			where.eq("groupName", group);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	public ArrayList<NeoComAsset> searchAsset4Location (final EveLocation location) {
		AssetsManager.logger.info(">> AssetsManager.searchAsset4Location");
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		// Check if we have already that list on the cache.
		assetList = assetsAtLocationCache.get(location.getID());
		if ( null == assetList ) {
			try {
				ModelAppConnector.getSingleton().startChrono();
				this.accessDaos();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", getCredentialIdentifier());
				where.and();
				where.eq("locationID", location.getID());
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = ModelAppConnector.getSingleton().timeLapse();
				AssetsManager.logger.info("~~ Time lapse for [SELECT LOCATIONID=" + location.getID() + " OWNERID = "
						+ getCredentialIdentifier() + "] - " + lapse);
				assetsAtLocationCache.put(location.getID(), (ArrayList<NeoComAsset>) assetList);
				// Update the dirty state to signal modification of store structures.
				//				this.store(true);
				AssetsManager.logger.info("<< AssetsManager.searchAsset4Location [" + assetList.size() + "]");
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	//	public HashSet<String> queryT2ModuleNames() {
	//		HashSet<String> names = new HashSet<String>();
	//		ArrayList<Asset> modules = searchT2Modules();
	//		for (Asset mod : modules) {
	//			names.add(mod.getName());
	//		}
	//		return names;
	//	}


	//	/**
	//	 * From the list of assets that have the Category "Blueprint" select only those that are of the Tech that is
	//	 * received on the parameter. Warning with the values because the comparison is performed on string literals
	//	 * and if the <code>qualifier</code> is not properly typed the result may be empty.
	//	 * 
	//	 * @return list of <code>Asset</code>s that are Blueprints Tech II.
	//	 */
	//	public ArrayList<Asset> queryBlueprints2(final String qualifier) {
	//		ArrayList<Asset> bps = searchAsset4Category("Blueprint");
	//		WhereClause techWhere = new WhereClause(EAssetsFields.TECH, EMode.EQUALS, qualifier);
	//		EveFilter filter = new EveFilter(bps, techWhere);
	//		return filter.getResults();
	//	}

	//	public ArrayList<Blueprint> queryT1Blueprints1() {
	//		if (null == t1blueprints) getPilot().updateBlueprints();
	//		return t1blueprints;
	//	}
	//
	//	public ArrayList<Blueprint> queryT2Blueprints1() {
	//		if (null == t2blueprints) getPilot().updateBlueprints();
	//		return t2blueprints;
	//	}

	public ArrayList<NeoComAsset> searchT2Modules () {
		AssetsManager.logger.info(">> EveChar.queryT2Modules");
		//	Select assets of type blueprint and that are of T2.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		assetList = assetsAtCategoryCache.get("T2Modules");
		if ( null == assetList ) {
			try {
				ModelAppConnector.getSingleton().startChrono();
				Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", getCredentialIdentifier());
				where.and();
				where.eq("category", ModelWideConstants.eveglobal.Module);
				where.and();
				where.eq("tech", ModelWideConstants.eveglobal.TechII);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = ModelAppConnector.getSingleton().timeLapse();
				AssetsManager.logger.info("~~ Time lapse for [SELECT CATEGORY=MODULE TECH=TECH II OWNERID = "
						+ getCredentialIdentifier() + "] - " + lapse);
				assetsAtCategoryCache.put("T2Modules", (ArrayList<NeoComAsset>) assetList);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		AssetsManager.logger.info("<< EveChar.queryT2Modules");
		return (ArrayList<NeoComAsset>) assetList;
	}

	/**
	 * Retrieves from the database all the stacks for an specific item type id. The method stores the results
	 * into the cache so next accesses will not trigger database access.
	 *
	 * @return the list of stacks for this type id that belong to this pilot.
	 */
	public ArrayList<NeoComAsset> stacks4Item (final EveItem item) {
		// Check if results already on cache.
		ArrayList<NeoComAsset> hit = stacksByItemCache.get(item.getItemID());
		if ( null != hit ) return hit;
		//	Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			this.accessDaos();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", getCredentialIdentifier());
			where.and();
			where.eq("typeID", item.getItemID());
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		stacksByItemCache.put(item.getItemID(), (ArrayList<NeoComAsset>) assetList);
		return (ArrayList<NeoComAsset>) assetList;
	}
	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("AssetsManager [");
		buffer.append("owner:").append(getCredentialName()).append(" ");
		//		if (null != t1blueprints) buffer.append("noT1BlueprintsStacks: ").append(t1blueprints.size()).append(" ");
		//		if (null != t2blueprints) buffer.append("noT2BlueprintsStacks: ").append(t2blueprints.size()).append(" ");
		if ( assetsAtCategoryCache.size() > 0 ) {
			buffer.append("assetsAtCategoryCache:").append(assetsAtCategoryCache.size()).append(" ");
		}
		if ( assetsAtLocationCache.size() > 0 ) {
			buffer.append("assetsAtLocationcache:").append(assetsAtLocationCache.size()).append(" ");
		}
		//		if (blueprintCache.size() > 0) {
		//			buffer.append("blueprintCache:").append(blueprintCache.size()).append(" ");
		//		}
		if ( null != locations ) {
			buffer.append("locationsList: ").append(locations).append(" ");
		}
		if ( null != regions ) {
			buffer.append("regionNames: ").append(regions).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	//	/**
	//	 * This method initialized all the transient fields that are expected to be initialized with empty data
	//	 * structures.
	//	 */
	//	public void reinstantiate() {
	//	}

	protected void add2Location (final NeoComAsset asset) {
		long locid = asset.getLocationId();
		ExtendedLocation target = locations.get(locid);
		if ( null == target ) {
			EveLocation intermediary = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locid);
			// Create another new Extended Location as a copy if this one to disconnect it from the unique cache copy.
			ExtendedLocation newloc = new ExtendedLocation(credential, intermediary);
			newloc.setContentManager(new PlanetaryAssetsContentManager(newloc));
			locations.put(new Long(locid), target);
			this.add2Region(target);
			newloc.addContent(asset);
		} else {
			target.addContent(asset);
		}
	}
	//	/**
	//	 * Search for this container reference on this Location's children until found. Then aggregates the asset to
	//	 * that container calculating stacking if this is possible. There can be containers inside container like
	//	 * the case where a container is on the hols of a ship. That special case will not be implemented on this
	//	 * first approach and all the container will be located at the Location's hangar floor.<br>
	//	 * Containers also do not have its market value added to the location's aggregation.
	//	 * 
	//	 * @param apart
	//	 */
	//	private void add2Container(final NeoComAsset asset) {
	//		AssetsManager.logger.info(">> LocationAssetsPart.add2Container");
	//		// Locate the container if already added to the location.
	//		NeoComAsset cont = asset.getParentContainer();
	//		// TODO Check what is the cause of a parent container null and solve it
	//		if (null != cont) {
	//			long pcid = cont.getDAOID();
	//			NeoComAsset target = containers.get(pcid);
	//			if (null == target) {
	//				// Add the container to the list of containers.
	//				AssetsManager.logger
	//						.info("-- [AssetsByLocationDataSource.add2Container]> Created new container: " + cont.getDAOID());
	//				containers.put(new Long(pcid), cont);
	//				// Add the container to the list of locations or to another container if not child
	//				//			if (asset.hasParent()) {
	//				//				add2Container(cont);
	//				//			} else {
	//				//				add2Location(cont);
	//				//			}
	//			} else {
	//				// Add the asset to the children list of the target container
	//				target.addChild(asset);
	//			}
	//		} else {
	//			// Investigate why the container is null. And maybe we should search for it because it is not our asset.
	//			long id = asset.getParentContainerId();
	//			NeoComAsset parentAssetCache = ModelAppConnector.getSingleton().getDBConnector()
	//					.searchAssetByID(asset.getParentContainerId());
	//		}
	//		// This is an Unknown location that should be a Custom Office
	//	}
	//
	//	private void add2Location(final NeoComAsset asset) {
	//		long locid = asset.getLocationID();
	//		EveLocation target = locations.get(locid);
	//		if (null == target) {
	//			target = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locid);
	//			locations.put(new Long(locid), target);
	//			this.add2Region(target);
	//		}
	//		target.addChild(asset);
	//	}
	//
	//	private void add2Region(final EveLocation target) {
	//		long regionid = target.getRegionID();
	//		Region region = regions.get(regionid);
	//		if (null == region) {
	//			region = new Region(target.getRegion());
	//			regions.put(new Long(regionid), region);
	//		}
	//		region.addLocation(target);
	//	}

	private void accessDaos () {
		if ( null == assetDao ) {
			try {
				assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
				if ( null == assetDao ) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
			} catch (SQLException sqle) {
				// Interrupt processing and signal a runtime exception.
				throw new RuntimeException(sqle.getMessage());
			}
		}
	}

	/**
	 * Get the complete list of the assets that belong to this owner.
	 */
	private ArrayList<NeoComAsset> getAllAssets () {
		// Select assets for the owner.
		ArrayList<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
			ModelAppConnector.getSingleton().startChrono();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", getCredentialIdentifier());
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = (ArrayList<NeoComAsset>) assetDao.query(preparedQuery);
			Duration lapse = ModelAppConnector.getSingleton().timeLapse();
			AssetsManager.logger.info("~~ Time lapse for [SELECT * FROM ASSETS OWNER = " + getCredentialIdentifier()
					+ "] - " + lapse);
			AssetsManager.logger.info("-- Assets processed: " + assetList.size());
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return assetList;
	}

	private String getTSAssetsReference () {
		return getCredentialIdentifier() + ".ASSETS";
	}

	/**
	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	 * because with the flat listing there is only relationship through the location id. <br>
	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	 * because the auto model generation will already include those items. Only Locations or Regions behave
	 * differently.
	 */
	private void processElement (final NeoComAsset asset) {
		try {
			// Remove the element from the map.
			assetMap.remove(asset.getAssetId());
			// Add the asset to the verification count.
			//			verificationAssetCount++;
			// Add the asset value to the owner balance.
			totalAssetsValue += asset.getIskValue();
			// Transform the asset if on specific categories like Ship or Container
			if ( asset.isShip() ) {
				// Check if the ship is packaged. If packaged leave it as a simple asset.
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					Ship ship = new Ship(getCredentialIdentifier()).copyFrom(asset);
					ships.put(ship.getAssetId(), ship);
					// The ship is a container so add it and forget about this asset.
					if ( ship.hasParent() ) {
						this.processElement(ship.getParentContainer());
					} //else {
					this.add2Location(ship);
					// Remove all the assets contained because they will be added in the call to collaborate2Model
					// REFACTOR set the default variant as a constant even that information if defined at other project
					List<ICollaboration> removableList = ship.collaborate2Model("DEFAULT");
					// The list returned is not the real list of assets contained but the list of Separators
					for (ICollaboration node : removableList) {
						this.removeNode(node);
					}
				} else {
					this.add2Location(asset);
				}
				return;
			}
			if ( asset.isContainer() ) {
				// Check if the asset is packaged. If so leave as asset
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					SpaceContainer container = new SpaceContainer().copyFrom(asset);
					containers.put(container.getAssetId(), container);
					// The container is a container so add it and forget about this asset.
					if ( container.hasParent() ) {
						this.processElement(container.getParentContainer());
					} // else {
					this.add2Location(container);
					// Remove all the assets contained because they will be added in the call to collaborate2Model
					// REFACTOR set the default variant as a constant even that information if defined at other project
					List<ICollaboration> removableList = container.collaborate2Model("DEFAULT");
					// The list returned is not the real list of assets contained but the list of Separators
					for (ICollaboration node : removableList) {
						this.removeNode(node);
					}
				} else {
					this.add2Location(asset);
				}
				//				// Remove all the assets contained because they will be added in the call to collaborate2Model
				//				ArrayList<AbstractComplexNode> removable = asset.collaborate2Model("REPLACE");
				//				for (AbstractComplexNode node : removable) {
				//					assetMap.remove(((Container) node).getAssetID());
				//				}
				//	}
				return;
			}
			// Process the asset parent if this is the case because we should add first parent to the hierarchy
			if ( asset.hasParent() ) {
				NeoComAsset parent = asset.getParentContainer();
				if ( null == parent ) {
					this.add2Location(asset);
				} else {
					this.processElement(parent);
				}
			} else {
				this.add2Location(asset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void readTimeStamps () {
		try {
			Dao<TimeStamp, String> tsDao = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDao();
			QueryBuilder<TimeStamp, String> queryBuilder = tsDao.queryBuilder();
			Where<TimeStamp, String> where = queryBuilder.where();
			where.eq("reference", this.getTSAssetsReference());
			PreparedQuery<TimeStamp> preparedQuery = queryBuilder.prepare();
			List<TimeStamp> ts = tsDao.query(preparedQuery);
			if ( ts.size() > 0 ) {
				assetsCacheTime = ts.get(0);
			}
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	/**
	 * Remove the nodes collaborated and their own collaborations recursively from the list of assets to
	 * process.
	 */
	private void removeNode (final ICollaboration node) {
		// Check that the class of the item is an Asset. Anyway check for its collaboration.
		if ( node instanceof NeoComNode ) {
			// Try to remove the asset if found
			if ( node instanceof NeoComAsset ) {
				assetMap.remove(((NeoComAsset) node).getAssetId());
			}
			// Remove also the nodes collaborated by it.
			for (ICollaboration child : node.collaborate2Model("DEFAULT")) {
				this.removeNode(child);
			}
		}
	}



	//	/**
	//	 * Gets the list of locations for a character. It will store the results into a local variable to speed up
	//	 * any other request because this is valid forever while the duration of the session because this data is
	//	 * only modified when the assets are updated.
	//	 * 
	//	 * @param characterID
	//	 * @return
	//	 */
	//	private synchronized void updateLocations() {
	//		AssetsManager.logger.info(">> AssetsManager.updateLocations");
	//		AppConnector.startChrono();
	//		//	Select assets for the owner and with an specific type id.
	//		List<Integer> locationIdentifierList = new ArrayList<Integer>();
	//		try {
	//			this.accessDaos();
	//			GenericRawResults<String[]> rawResults = assetDao
	//					.queryRaw("SELECT DISTINCT locationID FROM Assets WHERE ownerId=" + this.getPilot().getCharacterID());
	//			for (String[] resultColumns : rawResults) {
	//				String idString = resultColumns[0];
	//				try {
	//					int locationID = Integer.parseInt(idString);
	//					locationIdentifierList.add(locationID);
	//				} catch (NumberFormatException nfe) {
	//					nfe.printStackTrace();
	//				}
	//			}
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		// Be sure the regions and locations are accessible.
	//		locationsList = new ArrayList<EveLocation>();
	//		regionNames = new HashSet<String>();
	//		for (Integer lid : locationIdentifierList) {
	//			EveLocation loc = AppConnector.getDBConnector().searchLocationbyID(lid);
	//			locationsList.add(loc);
	//			regionNames.add(loc.getRegion());
	//		}
	//		// Update counter
	//		locationCount = locationsList.size();
	//		// Update the dirty state to signal modification of store structures.
	//		this.store(true);
	//		AssetsManager.logger.info("<< AssetsManager.updateLocations. " + AppConnector.timeLapse());
	//	}

	/**
	 * Return the list of all the distinct Locations where a Character has assets. There is one location with a
	 * minus value id that is the <code>undefined</code> that contains all the items inside another elements
	 * (before assets processing) and the items on stations or space locations that do not belong to this
	 * character.<br>
	 * Once the assets are processed inside a location this list can change because it may contain other
	 * elements that are Ships or Containers. When processing the list we should connect them properly on the
	 * new hierarchy.<br>
	 * The returning result is not a list of locations but a simplified list of NeoComAssets with the location
	 * identifier loaded.
	 */
	private List<NeoComAsset> queryAllAssetLocations (final long identifier) {
		// Get access to one assets with a distinct location. Discard the rest of the data and only process the Location id
		List<NeoComAsset> uniqueLocations = new Vector<NeoComAsset>();
		try {
			accessDaos();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder().distinct().selectColumns("locationID");
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", identifier);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			uniqueLocations = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger
					.warning("W [AssetsManager.queryAllAssetLocations]> Exception reading asets locations: " + sqle.getMessage());
		}
		return uniqueLocations;
	}

	/**
	 * Converts location identifiers to NeoCom locations that can handle hierarchies.
	 * Now Locations are placeholders that delegate to Asset Content Managers the location of their contents.
	 * Stored the new extended location instance into their corresponding Region.
	 *
	 * @param identifier target location identifier number.
	 */
	private void processLocation (final long identifier) {
		if ( identifier < 0 ) return;
		if ( locations.containsKey(identifier) )
			return;
		else {
			EveLocation location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(identifier);
			// Convert the Location to a new Extended Location with the new Contents Manager.
			ExtendedLocation newloc = new ExtendedLocation(credential, location);
			newloc.setContentManager(new AllLazyAssetsContentManager(newloc));
			locations.put(identifier, newloc);
			long regid = newloc.getRegionID();
			Region reg = regions.get(regid);
			if ( null == reg ) {
				reg = new Region(newloc.getRegion());
				reg.addLocation(newloc);
				regions.put(regid, reg);
			} else {
				reg.addLocation(newloc);
			}
		}
	}
}
// - UNUSED CODE ............................................................................................
