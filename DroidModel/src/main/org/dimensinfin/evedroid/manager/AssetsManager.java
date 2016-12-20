//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.manager;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.CVariant.EDefaultVariant;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.model.Container;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.NeoComCharacter;
import org.dimensinfin.evedroid.model.Region;
import org.dimensinfin.evedroid.model.Ship;
import org.joda.time.Duration;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

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
public class AssetsManager implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long																serialVersionUID				= -8502099148768297876L;
	private static Logger																		logger									= Logger.getLogger("AssetsManager");

	// - F I E L D - S E C T I O N ............................................................................
	private transient NeoComCharacter												pilot										= null;
	private transient Dao<NeoComAsset, String>							assetDao								= null;

	// - L O C A T I O N   M A N A G E M E N T
	private int																							locationCount						= -1;
	private HashSet<String>																	regionNames							= null;
	private ArrayList<EveLocation>													locationsList						= null;

	// - A S S E T   M A N A G E M E N T
	private long																						totalAssets							= -1;
	private long																						verificationAssetCount	= 0;
	private double																					totalAssetsValue				= 0.0;
	private final HashMap<Long, Region>											regions									= new HashMap<Long, Region>();
	private final HashMap<Long, EveLocation>								locations								= new HashMap<Long, EveLocation>();
	private final HashMap<Long, NeoComAsset>								containers							= new HashMap<Long, NeoComAsset>();
	/** Probably redundant with containers. */
	private final HashMap<Long, NeoComAsset>								assetsAtContainer				= new HashMap<Long, NeoComAsset>();
	/** The new list of ships with their state and their contents. An extension of containers. */
	private final HashMap<Long, NeoComAsset>								ships										= new HashMap<Long, NeoComAsset>();
	private final HashMap<Long, ArrayList<NeoComAsset>>			assetsAtLocationCache		= new HashMap<Long, ArrayList<NeoComAsset>>();
	private final HashMap<String, ArrayList<NeoComAsset>>		assetsAtCategoryCache		= new HashMap<String, ArrayList<NeoComAsset>>();
	private final HashMap<Integer, ArrayList<NeoComAsset>>	stacksByItemCache				= new HashMap<Integer, ArrayList<NeoComAsset>>();
	/** The complete list of blueprints maybe is not used */
	private final ArrayList<NeoComBlueprint>								blueprintCache					= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								t1BlueprintCache				= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								t2BlueprintCache				= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								bpoCache								= new ArrayList<NeoComBlueprint>();

	public final HashMap<Long, ArrayList<NeoComAsset>>			assetCache							= new HashMap<Long, ArrayList<NeoComAsset>>();
	public final HashMap<Long, ArrayList<NeoComAsset>>			asteroidCache						= new HashMap<Long, ArrayList<NeoComAsset>>();

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	private transient HashMap<Long, NeoComAsset>						assetMap								= new HashMap<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager(final NeoComCharacter pilot) {
		this.setPilot(pilot);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Updates the list of assets, regions and locations from the database. Go to the database to download all
	 * the assets for this pilot or corporation and then process them one by one to ge the complete parenship
	 * and store the result on the assets caches for later use.
	 */
	public void accessAllAssets() {
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
					assetMap.put(asset.getAssetID(), asset);
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
			AssetsManager.logger.severe(
					"RTEX> AssetsByLocationDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
	}

	public Collection<NeoComAsset> accessShips() {
		if (null == ships)
			return new ArrayList<NeoComAsset>();
		else
			return ships.values();
	}

	/**
	 * Get the complete list of the assets that belong to this owner.
	 * 
	 * @return
	 */
	public ArrayList<NeoComAsset> getAllAssets() {
		// Select assets for the owner.
		ArrayList<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			AppConnector.startChrono();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = (ArrayList<NeoComAsset>) assetDao.query(preparedQuery);
			Duration lapse = AppConnector.timeLapse();
			AssetsManager.logger
					.info("~~ Time lapse for [SELECT * FROM ASSETS OWNER = " + this.getPilot().getCharacterID() + "] - " + lapse);
			AssetsManager.logger.info("-- Assets processed: " + assetList.size());
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return assetList;
	}

	/**
	 * Counts the number of assets that belong to this character. If the current number of assets is negative
	 * then this signals that the number has not been previously calculated.
	 * 
	 * @return the number of assets
	 */
	public long getAssetTotalCount() {
		if (totalAssets == -1) {
			try {
				this.accessDao();
				totalAssets = assetDao.countOf(
						assetDao.queryBuilder().setCountOf(true).where().eq("ownerID", this.getPilot().getCharacterID()).prepare());
			} catch (SQLException sqle) {
				AssetsManager.logger.info("W> Proglem calculating the number of assets for " + this.getPilot().getName());
			}
		}
		return totalAssets;
	}

	public ArrayList<NeoComBlueprint> getBlueprints() {
		if (null == blueprintCache) {
			this.updateBlueprints();
		}
		if (blueprintCache.size() == 0) {
			this.updateBlueprints();
		}
		return blueprintCache;
	}

	public int getLocationCount() {
		if (locationCount < 0) {
			this.updateLocations();
		}
		return locationCount;
	}

	/**
	 * Returns the list of different locations where this character has assets. The locations are the unique
	 * location ids that can be on the same or different systems. If a system has assets in more that one
	 * station or in space the number of ids that have the same system in common may be greater that 1.
	 * 
	 * @return
	 */
	public ArrayList<EveLocation> getLocations() {
		if (null == locationsList) {
			this.updateLocations();
		}
		if (locationsList.size() < 1) {
			this.updateLocations();
		}
		return locationsList;
	}

	public NeoComCharacter getPilot() {
		return pilot;
	}

	/**
	 * Returns the list of different Regions found on the list of locations.
	 */
	public HashSet<String> getRegions() {
		if (null == regionNames) {
			this.updateLocations();
		}
		return regionNames;
	}

	public ArrayList<NeoComAsset> getShips() {
		return this.searchAsset4Category("Ship");
	}

	/**
	 * Checks if that category was requested before and it is on the cache. If found returns that list.
	 * Otherwise go to the database for the list.
	 * 
	 * @param category
	 * @return
	 */
	public ArrayList<NeoComAsset> searchAsset4Category(final String category) {
		//	Select assets for the owner and with an specific category.
		List<NeoComAsset> assetsCategoryList = new ArrayList<NeoComAsset>();
		assetsCategoryList = assetsAtCategoryCache.get(category);
		if (null == assetsCategoryList) {
			try {
				this.accessDao();
				AppConnector.startChrono();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", this.getPilot().getCharacterID());
				where.and();
				where.eq("category", category);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetsCategoryList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				AssetsManager.logger.info("~~ Time lapse for [SELECT CATEGORY=" + category + " OWNERID = "
						+ this.getPilot().getCharacterID() + "] - " + lapse);
				assetsAtCategoryCache.put(category, (ArrayList<NeoComAsset>) assetsCategoryList);
				// Update the dirty state to signal modification of store structures.
				//				setDirty(true);
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		} else {
			AssetsManager.logger
					.info("~~ Cache hit [SELECT CATEGORY=" + category + " OWNERID = " + this.getPilot().getCharacterID() + "]");
		}

		return (ArrayList<NeoComAsset>) assetsCategoryList;
	}

	public ArrayList<NeoComAsset> searchAsset4Group(final String group) {
		//	Select assets for the owner and with an specific category.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			this.accessDao();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
			where.and();
			where.eq("groupName", group);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
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

	public ArrayList<NeoComAsset> searchAsset4Location(final EveLocation location) {
		AssetsManager.logger.info(">> AssetsManager.searchAsset4Location");
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		// Check if we have already that list on the cache.
		assetList = assetsAtLocationCache.get(location.getID());
		if (null == assetList) {
			try {
				AppConnector.startChrono();
				this.accessDao();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", this.getPilot().getCharacterID());
				where.and();
				where.eq("locationID", location.getID());
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				AssetsManager.logger.info("~~ Time lapse for [SELECT LOCATIONID=" + location.getID() + " OWNERID = "
						+ this.getPilot().getCharacterID() + "] - " + lapse);
				assetsAtLocationCache.put(location.getID(), (ArrayList<NeoComAsset>) assetList);
				// Update the dirty state to signal modification of store structures.
				this.setDirty(true);
				AssetsManager.logger.info("<< AssetsManager.searchAsset4Location [" + assetList.size() + "]");
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	public NeoComBlueprint searchBlueprintByID(final long assetid) {
		for (NeoComBlueprint bp : this.getBlueprints()) {
			String refs = bp.getStackIDRefences();
			if (refs.contains(Long.valueOf(assetid).toString())) return bp;
		}
		return null;
	}

	/**
	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T1
	 * blueprints. We expect this is not cost intensive because this function is called few times.
	 * 
	 * @return list of T1 blueprints.
	 */
	public ArrayList<NeoComBlueprint> searchT1Blueprints() {
		ArrayList<NeoComBlueprint> blueprintList = new ArrayList<NeoComBlueprint>();
		for (NeoComBlueprint bp : this.getBlueprints())
			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
				blueprintList.add(bp);
			}
		return blueprintList;
	}

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

	/**
	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T2
	 * blueprints. We expect this is not cost intensive because this function is called few times.
	 * 
	 * @return list of T2 blueprints.
	 */
	public ArrayList<NeoComBlueprint> searchT2Blueprints() {
		ArrayList<NeoComBlueprint> blueprintList = new ArrayList<NeoComBlueprint>();
		for (NeoComBlueprint bp : this.getBlueprints())
			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				blueprintList.add(bp);
			}
		return blueprintList;
	}

	public ArrayList<NeoComAsset> searchT2Modules() {
		AssetsManager.logger.info(">> EveChar.queryT2Modules");
		//	Select assets of type blueprint and that are of T2.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		assetList = assetsAtCategoryCache.get("T2Modules");
		if (null == assetList) {
			try {
				AppConnector.startChrono();
				Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", this.getPilot().getCharacterID());
				where.and();
				where.eq("category", ModelWideConstants.eveglobal.Module);
				where.and();
				where.eq("tech", ModelWideConstants.eveglobal.TechII);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				AssetsManager.logger.info("~~ Time lapse for [SELECT CATEGORY=MODULE TECH=TECH II OWNERID = "
						+ this.getPilot().getCharacterID() + "] - " + lapse);
				assetsAtCategoryCache.put("T2Modules", (ArrayList<NeoComAsset>) assetList);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		AssetsManager.logger.info("<< EveChar.queryT2Modules");
		return (ArrayList<NeoComAsset>) assetList;
	}

	//	/**
	//	 * This method initialized all the transient fields that are expected to be initialized with empty data
	//	 * structures.
	//	 */
	//	public void reinstantiate() {
	//	}

	public void setPilot(final NeoComCharacter newPilot) {
		pilot = newPilot;
	}

	/**
	 * Retrieves from the database all the stacks for an specific item type id. The method stores the results
	 * into the cache so next accesses will not trigger database access.
	 * 
	 * @param item
	 * @return the list of stacks for this type id that belong to this pilot.
	 */
	public ArrayList<NeoComAsset> stacks4Item(final EveItem item) {
		// Check if results already on cache.
		ArrayList<NeoComAsset> hit = stacksByItemCache.get(item.getItemID());
		if (null != hit) return hit;
		//	Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			this.accessDao();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
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

	/**
	 * Gets the list of blueprints from the API processor and packs them into stacks aggregated by some keys.
	 * This will simplify the quantity of data exported to presentation layers.<br>
	 * Aggregation is performed by TYPEID-LOCATION-CONTAINER-RUNS
	 * 
	 * @param bplist
	 *          list of newly created Blueprints from the CCP API download
	 */
	public void storeBlueprints(final ArrayList<NeoComBlueprint> bplist) {
		HashMap<String, NeoComBlueprint> bpStacks = new HashMap<String, NeoComBlueprint>();
		for (NeoComBlueprint blueprint : bplist) {
			this.checkBPCStacking(bpStacks, blueprint);
		}

		// Extract stacks and store them into the caches.
		blueprintCache.addAll(bpStacks.values());
		// Update the database information.
		for (NeoComBlueprint blueprint : blueprintCache) {
			try {
				Dao<NeoComBlueprint, String> blueprintDao = AppConnector.getDBConnector().getBlueprintDAO();
				// Be sure the owner is reset to undefined when stored at the database.
				blueprint.resetOwner();
				// Set new calculated values to reduce the time for blueprint part rendering.
				// REFACTOR This has to be rewrite to allow this calculation on download time.
				//				IJobProcess process = JobManager.generateJobProcess(getPilot(), blueprint, EJobClasses.MANUFACTURE);
				//				blueprint.setManufactureIndex(process.getProfitIndex());
				//				blueprint.setJobProductionCost(process.getJobCost());
				//				blueprint.setManufacturableCount(process.getManufacturableCount());
				blueprintDao.create(blueprint);
				AssetsManager.logger.info("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
			} catch (final SQLException sqle) {
				AssetsManager.logger
						.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
				sqle.printStackTrace();
			} catch (final RuntimeException rtex) {
				AssetsManager.logger
						.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + rtex.getMessage());
				rtex.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AssetsManager [");
		buffer.append("owner:").append(this.getPilot().getName());
		//		if (null != t1blueprints) buffer.append("noT1BlueprintsStacks: ").append(t1blueprints.size()).append(" ");
		//		if (null != t2blueprints) buffer.append("noT2BlueprintsStacks: ").append(t2blueprints.size()).append(" ");
		if (assetsAtCategoryCache.size() > 0) {
			buffer.append("assetsAtCategoryCache:").append(assetsAtCategoryCache.size()).append(" ");
		}
		if (assetsAtLocationCache.size() > 0) {
			buffer.append("assetsAtLocationcache:").append(assetsAtLocationCache.size()).append(" ");
		}
		//		if (blueprintCache.size() > 0) {
		//			buffer.append("blueprintCache:").append(blueprintCache.size()).append(" ");
		//		}
		if (null != locationsList) {
			buffer.append("locationsList: ").append(locationsList).append(" ");
		}
		if (null != regionNames) {
			buffer.append("regionNames: ").append(regionNames).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void accessDao() {
		if (null == assetDao) {
			try {
				assetDao = AppConnector.getDBConnector().getAssetDAO();
				if (null == assetDao) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
			} catch (SQLException sqle) {
				// Interrupt processing and signal a runtime exception.
				throw new RuntimeException(sqle.getMessage());
			}
		}
	}

	/**
	 * Search for this container reference on this Location's children until found. Then aggregates the asset to
	 * that container calculating stacking if this is possible. There can be containers inside container like
	 * the case where a container is on the hols of a ship. That special case will not be implemented on this
	 * first approach and all the container will be located at the Location's hangar floor.<br>
	 * Containers also do not have its market value added to the location's aggregation.
	 * 
	 * @param apart
	 */
	private void add2Container(final NeoComAsset asset) {
		AssetsManager.logger.info(">> LocationAssetsPart.add2Container");
		// Locate the container if already added to the location.
		NeoComAsset cont = asset.getParentContainer();
		// TODO Check what is the cause of a parent container null and solve it
		if (null != cont) {
			long pcid = cont.getDAOID();
			NeoComAsset target = containers.get(pcid);
			if (null == target) {
				// Add the container to the list of containers.
				AssetsManager.logger
						.info("-- [AssetsByLocationDataSource.add2Container]> Created new container: " + cont.getDAOID());
				containers.put(new Long(pcid), cont);
				// Add the container to the list of locations or to another container if not child
				//			if (asset.hasParent()) {
				//				add2Container(cont);
				//			} else {
				//				add2Location(cont);
				//			}
			} else {
				// Add the asset to the children list of the target container
				target.addChild(asset);
			}
		} else {
			// Investigate why the container is null. And maybe we should search for it because it is not our asset.
			long id = asset.getParentContainerId();
			NeoComAsset parentAssetCache = AppConnector.getDBConnector().searchAssetByID(asset.getParentContainerId());
		}
		// This is an Unknown location that should be a Custom Office
	}

	private void add2Location(final NeoComAsset asset) {
		long locid = asset.getLocationID();
		EveLocation target = locations.get(locid);
		if (null == target) {
			target = AppConnector.getDBConnector().searchLocationbyID(locid);
			locations.put(new Long(locid), target);
			this.add2Region(target);
		}
		target.addChild(asset);
	}

	private void add2Region(final EveLocation target) {
		long regionid = target.getRegionID();
		Region region = regions.get(regionid);
		if (null == region) {
			region = new Region(target.getRegion());
			regions.put(new Long(regionid), region);
		}
		region.addChild(target);
	}

	/**
	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
	 * same container so the locationID, the parentContainer and the typeID should match to perform the
	 * aggregation.<br>
	 * Aggregation key: ID-LOCATION-CONTAINER
	 * 
	 * @param targetContainer
	 *          the stack storage that contains the list of registered blueprints
	 * @param bp
	 *          the blueprint part to be added to the hierarchy
	 */
	private void checkBPCStacking(final HashMap<String, NeoComBlueprint> targetContainer, final NeoComBlueprint bp) {
		// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.ASSETID
		String id = bp.getStackID();
		NeoComBlueprint hit = targetContainer.get(id);
		if (null == hit) {
			// Miss. The blueprint is not registered.
			AssetsManager.logger.info("-- AssetsManager.checkBPCStacking >Stacked blueprint. " + bp.toString());
			bp.registerReference(bp.getAssetID());
			targetContainer.put(id, bp);
		} else {
			//Hit. Increment the counter for this stack. And store the id
			hit.setQuantity(hit.getQuantity() + bp.getQuantity());
			hit.registerReference(bp.getAssetID());
		}
	}

	/**
	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	 * because with the flat listing there is only relationship through the location id. <br>
	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	 * because the auto model generation will already include those items. Only Locations or Regions behave
	 * differently.
	 * 
	 * @param asset
	 */
	private void processElement(final NeoComAsset asset) {
		// Remove the element from the map.
		assetMap.remove(asset.getAssetID());
		// Add the asset to the verification count.
		verificationAssetCount++;
		// Add the asset value to the owner balance.
		totalAssetsValue += asset.getIskvalue();
		// Transform the asset if on specific categories like Ship or Container
		if (asset.isShip()) {
			// Check if the ship is packaged. If packaged leave it as a simple asset.
			if (!asset.isPackaged()) {
				// Transform the asset to a ship.
				Ship ship = new Ship(this.getPilot().getCharacterID()).copyFrom(asset);
				ships.put(ship.getAssetID(), ship);
				// The ship is a container so add it and forget about this asset.
				if (ship.hasParent()) {
					this.processElement(ship.getParentContainer());
				} //else {
				this.add2Location(ship);
				// Remove all the assets contained because they will be added in the call to collaborate2Model
				// REFACTOR set the default variant as a constant even that information if defined at other project
				ArrayList<AbstractComplexNode> removableList = ship.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
				// The list returned is not the real list of assets contained but the list of Separators
				for (AbstractComplexNode node : removableList) {
					this.removeNode(node);
				}
			} else {
				this.add2Location(asset);
			}
			return;
		}
		if (asset.isContainer()) {
			// Check if the asset is packaged. If so leave as asset
			if (!asset.isPackaged()) {
				// Transform the asset to a ship.
				Container container = new Container(this.getPilot().getCharacterID()).copyFrom(asset);
				containers.put(container.getAssetID(), container);
				// The container is a container so add it and forget about this asset.
				if (container.hasParent()) {
					this.processElement(container.getParentContainer());
				} // else {
				this.add2Location(container);
				// Remove all the assets contained because they will be added in the call to collaborate2Model
				// REFACTOR set the default variant as a constant even that information if defined at other project
				ArrayList<AbstractComplexNode> removableList = container
						.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
				// The list returned is not the real list of assets contained but the list of Separators
				for (AbstractComplexNode node : removableList) {
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
		if (asset.hasParent()) {
			this.processElement(asset.getParentContainer());
		} else {
			this.add2Location(asset);
		}
	}

	/**
	 * Remove the nodes collaborated and their own collaborations recursively from the list of assets to
	 * process.
	 */
	private void removeNode(final AbstractComplexNode node) {
		// Check that the class of the item is an Asset. Anyway check for its collaboration.
		if (node instanceof AbstractNeoComNode) {
			// Try to remove the asset if found
			if (node instanceof NeoComAsset) {
				assetMap.remove(((NeoComAsset) node).getAssetID());
			}
			// Remove also the nodes collaborated by it.
			for (AbstractComplexNode child : ((AbstractNeoComNode) node)
					.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name())) {
				this.removeNode(child);
			}
		}
	}

	// TODO The dirty flag for the assets is not used because assets are not persisted.
	private void setDirty(final boolean value) {
		//		getPilot().setDirty(value);
	}

	private void updateBlueprints() {
		AssetsManager.logger.info(">> AssetsManager.updateBlueprints");
		//		List<Blueprint> blueprintList = new ArrayList<Blueprint>();
		try {
			AppConnector.startChrono();
			Dao<NeoComBlueprint, String> blueprintDao = AppConnector.getDBConnector().getBlueprintDAO();
			QueryBuilder<NeoComBlueprint, String> queryBuilder = blueprintDao.queryBuilder();
			Where<NeoComBlueprint, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
			PreparedQuery<NeoComBlueprint> preparedQuery = queryBuilder.prepare();
			blueprintCache.addAll(blueprintDao.query(preparedQuery));
			Duration lapse = AppConnector.timeLapse();
			AssetsManager.logger
					.info("~~ Time lapse for BLUEPRINT [SELECT OWNERID = " + this.getPilot().getCharacterID() + "] - " + lapse);
			// Check if the list is empty. Then force a refresh download.
			if (blueprintCache.size() < 1) {
				this.getPilot().forceRefresh();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		AssetsManager.logger.info("<< AssetsManager.updateBlueprints [" + blueprintCache.size() + "]");
		//		return (ArrayList<Blueprint>) blueprintList;
	}

	/**
	 * Gets the list of locations for a character. It will store the results into a local variable to speed up
	 * any other request because this is valid forever while the duration of the session because this data is
	 * only modified when the assets are updated.
	 * 
	 * @param characterID
	 * @return
	 */
	private synchronized void updateLocations() {
		AssetsManager.logger.info(">> AssetsManager.updateLocations");
		AppConnector.startChrono();
		//	Select assets for the owner and with an specific type id.
		List<Integer> locationIdentifierList = new ArrayList<Integer>();
		try {
			this.accessDao();
			GenericRawResults<String[]> rawResults = assetDao
					.queryRaw("SELECT DISTINCT locationID FROM Assets WHERE ownerId=" + this.getPilot().getCharacterID());
			for (String[] resultColumns : rawResults) {
				String idString = resultColumns[0];
				try {
					int locationID = Integer.parseInt(idString);
					locationIdentifierList.add(locationID);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		// Be sure the regions and locations are accessible.
		locationsList = new ArrayList<EveLocation>();
		regionNames = new HashSet<String>();
		for (Integer lid : locationIdentifierList) {
			EveLocation loc = AppConnector.getDBConnector().searchLocationbyID(lid);
			locationsList.add(loc);
			regionNames.add(loc.getRegion());
		}
		// Update counter
		locationCount = locationsList.size();
		// Update the dirty state to signal modification of store structures.
		this.setDirty(true);
		AssetsManager.logger.info("<< AssetsManager.updateLocations. " + AppConnector.timeLapse());
	}
}
// - UNUSED CODE ............................................................................................
