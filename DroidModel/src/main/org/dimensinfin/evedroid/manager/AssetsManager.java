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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.NeoComCharacter;
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
 * scheduled industry jobs created by the user.
 * <br>
 * Adapted to new design and the new asset list management. Now the list downloaded from CCP is not hierarchically
 * ordered so the location is lost in replacement for the parent id. With this new structure is no longer possible to locate the
 * assets in a single location or system on a database search. The new object management requires to get from the
 * database the complete list of assets, order and classify them on memory and store at the assets manager. This will
 * need to have a protected AssetManager on each character so it is not the same one used on destructive industry operations
 * that will change the asset contents.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class AssetsManager implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long													serialVersionUID			= -8502099148768297876L;
	private static Logger logger = Logger.getLogger("AssetsManager");

	// - F I E L D - S E C T I O N ............................................................................
	private transient NeoComCharacter													pilot									= null;
	private transient Dao<Asset, String>							assetDao							= null;

	// - L O C A T I O N   M A N A G E M E N T
	private int																				locationCount					= -1;
	private HashSet<String>														regionNames						= null;
	private ArrayList<EveLocation>										locationsList					= null;

	private long																			totalAssets						= -1;
	private final HashMap<Long, ArrayList<Asset>>			assetsAtLocationcache	= new HashMap<Long, ArrayList<Asset>>();
	private final HashMap<String, ArrayList<Asset>>		assetsAtCategoryCache	= new HashMap<String, ArrayList<Asset>>();
	private final HashMap<Integer, ArrayList<Asset>>	stacksByItemCache			= new HashMap<Integer, ArrayList<Asset>>();
//	private final ArrayList<Blueprint>								blueprintCache				= new ArrayList<Blueprint>();
	public final HashMap<Long, ArrayList<Asset>>			assetCache						= new HashMap<Long, ArrayList<Asset>>();
	public final HashMap<Long, ArrayList<Asset>>			asteroidCache					= new HashMap<Long, ArrayList<Asset>>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager(final NeoComCharacter pilot) {
		setPilot(pilot);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Counts the number of assets that belong to this character. If the current number of assets is negative
	 * then this signals that the number has not been previously calculated.
	 * 
	 * @return the number of assets
	 */
	public long getAssetTotalCount() {
		if (totalAssets == -1) {
			try {
				accessDao();
				totalAssets = assetDao.countOf(
						assetDao.queryBuilder().setCountOf(true).where().eq("ownerID", getPilot().getCharacterID()).prepare());
			} catch (SQLException sqle) {
				logger.info("W> Proglem calculating the number of assets for " + getPilot().getName());
			}
		}
		return totalAssets;
	}

//	public ArrayList<Blueprint> getBlueprints() {
//		if (null == blueprintCache) {
//			updateBlueprints();
//		}
//		if (blueprintCache.size() == 0) {
//			updateBlueprints();
//		}
//		return blueprintCache;
//	}

	public int getLocationCount() {
		if (locationCount < 0) {
			updateLocations();
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
			updateLocations();
		}
		if (locationsList.size() < 1) {
			updateLocations();
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
			updateLocations();
		}
		return regionNames;
	}

	//	public HashSet<String> queryT2ModuleNames() {
	//		HashSet<String> names = new HashSet<String>();
	//		ArrayList<Asset> modules = searchT2Modules();
	//		for (Asset mod : modules) {
	//			names.add(mod.getName());
	//		}
	//		return names;
	//	}

	public ArrayList<Asset> getShips() {
		return searchAsset4Category("Ship");
	}

	/**
	 * Checks if that category was requested before and it is on the cache. If found returns that list.
	 * Otherwise go to the database for the list.
	 * 
	 * @param category
	 * @return
	 */
	public ArrayList<Asset> searchAsset4Category(final String category) {
		//	Select assets for the owner and with an specific category.
		List<Asset> assetsCategoryList = new ArrayList<Asset>();
		assetsCategoryList = assetsAtCategoryCache.get(category);
		if (null == assetsCategoryList) {
			try {
				accessDao();
				AppConnector.startChrono();
				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
				Where<Asset, String> where = queryBuilder.where();
				where.eq("ownerID", getPilot().getCharacterID());
				where.and();
				where.eq("category", category);
				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
				assetsCategoryList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				logger.info("~~ Time lapse for [SELECT CATEGORY=" + category + " OWNERID = "
						+ getPilot().getCharacterID() + "] - " + lapse);
				assetsAtCategoryCache.put(category, (ArrayList<Asset>) assetsCategoryList);
				// Update the dirty state to signal modification of store structures.
				//				setDirty(true);
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		} else {
			logger.info(
					"~~ Cache hit [SELECT CATEGORY=" + category + " OWNERID = " + getPilot().getCharacterID() + "]");
		}

		return (ArrayList<Asset>) assetsCategoryList;
	}

	public ArrayList<Asset> searchAsset4Group(final String group) {
		//	Select assets for the owner and with an specific category.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			accessDao();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", getPilot().getCharacterID());
			where.and();
			where.eq("groupName", group);
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Asset>) assetList;
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

	public ArrayList<Asset> searchAsset4Location(final EveLocation location) {
		logger.info(">> AssetsManager.searchAsset4Location");
		List<Asset> assetList = new ArrayList<Asset>();
		// Check if we have already that list on the cache.
		assetList = assetsAtLocationcache.get(location.getID());
		if (null == assetList) {
			try {
				AppConnector.startChrono();
				accessDao();
				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
				Where<Asset, String> where = queryBuilder.where();
				where.eq("ownerID", getPilot().getCharacterID());
				where.and();
				where.eq("locationID", location.getID());
				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				logger.info("~~ Time lapse for [SELECT LOCATIONID=" + location.getID() + " OWNERID = "
						+ getPilot().getCharacterID() + "] - " + lapse);
				assetsAtLocationcache.put(location.getID(), (ArrayList<Asset>) assetList);
				// Update the dirty state to signal modification of store structures.
				setDirty(true);
				logger.info("<< AssetsManager.searchAsset4Location [" + assetList.size() + "]");
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return (ArrayList<Asset>) assetList;
	}

//	public Blueprint searchBlueprintByID(final long assetid) {
//		for (Blueprint bp : getBlueprints()) {
//			String refs = bp.getStackIDRefences();
//			if (refs.contains(Long.valueOf(assetid).toString())) return bp;
//		}
//		return null;
//	}

	//	/**
	//	 * This method initialized all the transient fields that are expected to be initialized with empty data
	//	 * structures.
	//	 */
	//	public void reinstantiate() {
	//	}

//	/**
//	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T1
//	 * blueprints. We expect this is not cost intensive because this function is called few times.
//	 * 
//	 * @return list of T1 blueprints.
//	 */
//	public ArrayList<Blueprint> searchT1Blueprints() {
//		ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
//		for (Blueprint bp : getBlueprints())
//			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
//				blueprintList.add(bp);
//			}
//		return blueprintList;
//	}

//	/**
//	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T2
//	 * blueprints. We expect this is not cost intensive because this function is called few times.
//	 * 
//	 * @return list of T2 blueprints.
//	 */
//	public ArrayList<Blueprint> searchT2Blueprints() {
//		ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
//		for (Blueprint bp : getBlueprints())
//			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
//				blueprintList.add(bp);
//			}
//		return blueprintList;
//	}

	public ArrayList<Asset> searchT2Modules() {
		logger.info(">> EveChar.queryT2Modules");
		//	Select assets of type blueprint and that are of T2.
		List<Asset> assetList = new ArrayList<Asset>();
		assetList = assetsAtCategoryCache.get("T2Modules");
		if (null == assetList) {
			try {
				AppConnector.startChrono();
				Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
				Where<Asset, String> where = queryBuilder.where();
				where.eq("ownerID", getPilot().getCharacterID());
				where.and();
				where.eq("category", ModelWideConstants.eveglobal.Module);
				where.and();
				where.eq("tech", ModelWideConstants.eveglobal.TechII);
				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = AppConnector.timeLapse();
				logger.info("~~ Time lapse for [SELECT CATEGORY=MODULE TECH=TECH II OWNERID = "
						+ getPilot().getCharacterID() + "] - " + lapse);
				assetsAtCategoryCache.put("T2Modules", (ArrayList<Asset>) assetList);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		logger.info("<< EveChar.queryT2Modules");
		return (ArrayList<Asset>) assetList;
	}

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
	public ArrayList<Asset> stacks4Item(final EveItem item) {
		// Check if results already on cache.
		ArrayList<Asset> hit = stacksByItemCache.get(item.getItemID());
		if (null != hit) return hit;
		//	Select assets for the owner and with an specific type id.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			accessDao();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", getPilot().getCharacterID());
			where.and();
			where.eq("typeID", item.getItemID());
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		stacksByItemCache.put(item.getItemID(), (ArrayList<Asset>) assetList);
		return (ArrayList<Asset>) assetList;
	}

//	/**
//	 * Gets the list of blueprints from the API processor and packs them into stacks aggregated by some keys.
//	 * This will simplify the quantity of data exported to presentation layers.<br>
//	 * Aggregation is performed by TYPEID-LOCATION-CONTAINER-RUNS
//	 * 
//	 * @param bplist
//	 *          list of newly created Blueprints from the CCP API download
//	 */
//	public void storeBlueprints(final ArrayList<Blueprint> bplist) {
//		HashMap<String, Blueprint> bpStacks = new HashMap<String, Blueprint>();
//		for (Blueprint blueprint : bplist) {
//			checkBPCStacking(bpStacks, blueprint);
//		}
//
//		// Extract stacks and store them into the caches.
//		blueprintCache.addAll(bpStacks.values());
//		// Update the database information.
//		for (Blueprint blueprint : blueprintCache) {
//			try {
//				Dao<Blueprint, String> blueprintDao = AppConnector.getDBConnector().getBlueprintDAO();
//				// Be sure the owner is reset to undefined when stored at the database.
//				blueprint.resetOwner();
//				// Set new calculated values to reduce the time for blueprint part rendering.
//				IJobProcess process = JobManager.generateJobProcess(getPilot(), blueprint, EJobClasses.MANUFACTURE);
//				blueprint.setManufactureIndex(process.getProfitIndex());
//				blueprint.setJobProductionCost(process.getJobCost());
//				blueprint.setManufacturableCount(process.getManufacturableCount());
//				blueprintDao.create(blueprint);
//				logger.info("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
//			} catch (final SQLException sqle) {
//				logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
//				sqle.printStackTrace();
//			} catch (final RuntimeException rtex) {
//				logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + rtex.getMessage());
//				rtex.printStackTrace();
//			}
//		}
//	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AssetsManager [");
		buffer.append("owner:").append(getPilot().getName());
		//		if (null != t1blueprints) buffer.append("noT1BlueprintsStacks: ").append(t1blueprints.size()).append(" ");
		//		if (null != t2blueprints) buffer.append("noT2BlueprintsStacks: ").append(t2blueprints.size()).append(" ");
		if (assetsAtCategoryCache.size() > 0) {
			buffer.append("assetsAtCategoryCache:").append(assetsAtCategoryCache.size()).append(" ");
		}
		if (assetsAtLocationcache.size() > 0) {
			buffer.append("assetsAtLocationcache:").append(assetsAtLocationcache.size()).append(" ");
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
	private void checkBPCStacking(final HashMap<String, Blueprint> targetContainer, final Blueprint bp) {
		// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.ASSETID
		String id = bp.getStackID();
		Blueprint hit = targetContainer.get(id);
		if (null == hit) {
			// Miss. The blueprint is not registered.
			logger.info("-- AssetsManager.checkBPCStacking >Stacked blueprint. " + bp.toString());
			bp.registerReference(bp.getAssetID());
			targetContainer.put(id, bp);
		} else {
			//Hit. Increment the counter for this stack. And store the id
			hit.setQuantity(hit.getQuantity() + bp.getQuantity());
			hit.registerReference(bp.getAssetID());
		}
	}
// TODO The dirty flag for the assets is not used because assets are not persisted.
	private void setDirty(final boolean value) {
//		getPilot().setDirty(value);
	}

//	private void updateBlueprints() {
//		logger.info(">> AssetsManager.updateBlueprints");
//		//		List<Blueprint> blueprintList = new ArrayList<Blueprint>();
//		try {
//			AppConnector.startChrono();
//			Dao<Blueprint, String> blueprintDao = AppConnector.getDBConnector().getBlueprintDAO();
//			QueryBuilder<Blueprint, String> queryBuilder = blueprintDao.queryBuilder();
//			Where<Blueprint, String> where = queryBuilder.where();
//			where.eq("ownerID", getPilot().getCharacterID());
//			PreparedQuery<Blueprint> preparedQuery = queryBuilder.prepare();
//			blueprintCache.addAll(blueprintDao.query(preparedQuery));
//			Duration lapse = AppConnector.timeLapse();
//			logger.info("~~ Time lapse for BLUEPRINT [SELECT OWNERID = " + getPilot().getCharacterID() + "] - " + lapse);
//			// Check if the list is empty. Then force a refresh download.
//			if (blueprintCache.size() < 1) {
//				getPilot().forceRefresh();
//			}
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		logger.info("<< AssetsManager.updateBlueprints [" + blueprintCache.size() + "]");
//		//		return (ArrayList<Blueprint>) blueprintList;
//	}

	/**
	 * Gets the list of locations for a character. It will store the results into a local variable to speed up
	 * any other request because this is valid forever while the duration of the session because this data is
	 * only modified when the assets are updated.
	 * 
	 * @param characterID
	 * @return
	 */
	private synchronized void updateLocations() {
		logger.info(">> AssetsManager.updateLocations");
		AppConnector.startChrono();
		//	Select assets for the owner and with an specific type id.
		List<Integer> locationIdentifierList = new ArrayList<Integer>();
		try {
			accessDao();
			GenericRawResults<String[]> rawResults = assetDao
					.queryRaw("SELECT DISTINCT locationID FROM Assets WHERE ownerId=" + getPilot().getCharacterID());
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
		setDirty(true);
		logger.info("<< AssetsManager.updateLocations. " + AppConnector.timeLapse());
	}
}
// - UNUSED CODE ............................................................................................
