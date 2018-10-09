//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.managers;

import com.beimin.eveapi.model.shared.Asset;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.model.PilotV2;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class interfaces all access to the assets database in name of a particular character. It tries to
 * cache and manage all data in favour of speed versus space. Takes care to update memory references so model
 * data used on the different pages does not gets changes by other pages if not necessary. But if the same
 * data is to be represented at different locations this class should make sure that the same data is
 * returned.<br>
 * At the same time different instances allow to separate asset usage and simulate assets changes by the
 * scheduled industry jobs created by the user.
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class AssetsManager implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -8502099148768297876L;

	// - F I E L D - S E C T I O N ............................................................................
	private Credential _credential=null;
	private transient PilotV2 pilot = null;
	private transient Dao<Asset, String> assetDao = null;

	// - L O C A T I O N   M A N A G E M E N T
	//	private int																				locationCount					= -1;
	//	private HashSet<String>														regionNames						= null;
	//	private ArrayList<EveLocation>										locationsList					= null;

	protected int totalAssets = -1;
	protected List<NeoComAsset> fullAssetList = new ArrayList<>();
	protected boolean allowCaching = true;
	protected boolean contentManaged = false;
	//	private final HashMap<Long, ArrayList<Asset>>			assetsAtLocationcache	= new HashMap<Long, ArrayList<Asset>>();
	//	private final HashMap<String, ArrayList<Asset>>		assetsAtCategoryCache	= new HashMap<String, ArrayList<Asset>>();
	//	private final HashMap<Integer, ArrayList<Asset>>	stacksByItemCache			= new HashMap<Integer, ArrayList<Asset>>();
	//	private final ArrayList<Blueprint>								blueprintCache				= new ArrayList<Blueprint>();
	//	public final HashMap<Long, ArrayList<Asset>>			assetCache						= new HashMap<Long, ArrayList<Asset>>();
	//	public final HashMap<Long, ArrayList<Asset>>			asteroidCache					= new HashMap<Long, ArrayList<Asset>>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager( final Credential credential ) {
		this._credential = credential;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public PilotV2 getPilot() {
		return pilot;
	}

	public int getTotalAssets() {
		return this.totalAssets;
	}

	/**
	 * Returns the complete list of assets that belong to this pilot. If the list of not present at the Manager we should go to the
	 * Global to get access to this list.
	 * @return the full list of this pilot's assets.
	 */
	public List<NeoComAsset> getFullAssetList() throws SQLException {
		if(!contentManaged){
			// The data is not present. Go to the Global to retrieve it.
			List<NeoComAsset> list = GlobalDataManager.accessAllAssets4Credential(this._credential);
			// If the caching is active, store the data into the cache.
			if(allowCaching) {
				fullAssetList = list;
				this.totalAssets=this.fullAssetList.size();
				this.contentManaged=true;
			}
		}
		return fullAssetList;
	}

	//	/**
	//	 * Counts the number of assets that belong to this character. If the current number of assets is negative
	//	 * then this signals that the number has not been previously calculated.
	//	 *
	//	 * @return the number of assets
	//	 */
	//	public long getAssetTotalCount() {
	//		if (totalAssets == -1)
	//			try {
	//				accessDao();
	//				totalAssets = assetDao.countOf(assetDao.queryBuilder().setCountOf(true).where()
	//						.eq("ownerID", getPilot().getCharacterID()).prepare());
	//			} catch (SQLException sqle) {
	//				Log.w("NEOCOM", "W> Proglem calculating the number of assets for " + getPilot().getName());
	//			}
	//		return totalAssets;
	//	}
	//
	//	public ArrayList<Blueprint> getBlueprints() {
	//		if (null == blueprintCache) updateBlueprints();
	//		if (blueprintCache.size() == 0) updateBlueprints();
	//		return blueprintCache;
	//	}
	//
	//	public int getLocationCount() {
	//		if (locationCount < 0) updateLocations();
	//		return locationCount;
	//	}
	//
	//	/**
	//	 * Returns the list of different locations where this character has assets. The locations are the unique
	//	 * location ids that can be on the same or different systems. If a system has assets in more that one
	//	 * station or in space the number of ids that have the same system in common may be greater that 1.
	//	 *
	//	 * @return
	//	 */
	//	public ArrayList<EveLocation> getLocations() {
	//		if (null == locationsList) updateLocations();
	//		if (locationsList.size() < 1) updateLocations();
	//		return locationsList;
	//	}
	//
	//	/**
	//	 * Returns the list of different Regions found on the list of locations.
	//	 */
	//	public HashSet<String> getRegions() {
	//		if (null == regionNames) updateLocations();
	//		return regionNames;
	//	}
	//
	//	public ArrayList<Asset> getShips() {
	//		return searchAsset4Category("Ship");
	//	}
	//
	//	//	public HashSet<String> queryT2ModuleNames() {
	//	//		HashSet<String> names = new HashSet<String>();
	//	//		ArrayList<Asset> modules = searchT2Modules();
	//	//		for (Asset mod : modules) {
	//	//			names.add(mod.getName());
	//	//		}
	//	//		return names;
	//	//	}
	//
	//	/**
	//	 * Checks if that category was requested before and it is on the cache. If found returns that list.
	//	 * Otherwise go to the database for the list.
	//	 *
	//	 * @param category
	//	 * @return
	//	 */
	//	public ArrayList<Asset> searchAsset4Category(final String category) {
	//		//	Select assets for the owner and with an specific category.
	//		List<Asset> blueprintList = new ArrayList<Asset>();
	//		blueprintList = assetsAtCategoryCache.get(category);
	//		if (null == blueprintList)
	//			try {
	//				accessDao();
	//				AppConnector.startChrono();
	//				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
	//				Where<Asset, String> where = queryBuilder.where();
	//				where.eq("ownerID", getPilot().getCharacterID());
	//				where.and();
	//				where.eq("category", category);
	//				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
	//				blueprintList = assetDao.query(preparedQuery);
	//				Duration lapse = AppConnector.timeLapse();
	//				Log.i("AssetsManager", "~~ Time lapse for [SELECT CATEGORY=" + category + " OWNERID = "
	//						+ getPilot().getCharacterID() + "] - " + lapse);
	//				assetsAtCategoryCache.put(category, (ArrayList<Asset>) blueprintList);
	//				// Update the dirty state to signal modification of store structures.
	//				//				setDirty(true);
	//			} catch (java.sql.SQLException sqle) {
	//				sqle.printStackTrace();
	//			}
	//		else
	//			Log.i("AssetsManager", "~~ Cache hit [SELECT CATEGORY=" + category + " OWNERID = " + getPilot().getCharacterID()
	//					+ "]");
	//
	//		return (ArrayList<Asset>) blueprintList;
	//	}
	//
	//	public ArrayList<Asset> searchAsset4Group(final String group) {
	//		//	Select assets for the owner and with an specific category.
	//		List<Asset> assetList = new ArrayList<Asset>();
	//		try {
	//			accessDao();
	//			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
	//			Where<Asset, String> where = queryBuilder.where();
	//			where.eq("ownerID", getPilot().getCharacterID());
	//			where.and();
	//			where.eq("groupName", group);
	//			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
	//			assetList = assetDao.query(preparedQuery);
	//		} catch (java.sql.SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		return (ArrayList<Asset>) assetList;
	//	}
	//
	//	public ArrayList<Asset> searchAsset4Location(final EveLocation location) {
	//		Log.i("AssetsManager", ">> AssetsManager.searchAsset4Location");
	//		List<Asset> assetList = new ArrayList<Asset>();
	//		// Check if we have already that list on the cache.
	//		assetList = assetsAtLocationcache.get(location.getID());
	//		if (null == assetList) // Read from database the assets on this location.
	//			try {
	//				AppConnector.startChrono();
	//				accessDao();
	//				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
	//				Where<Asset, String> where = queryBuilder.where();
	//				where.eq("ownerID", getPilot().getCharacterID());
	//				where.and();
	//				where.eq("locationID", location.getID());
	//				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
	//				assetList = assetDao.query(preparedQuery);
	//				Duration lapse = AppConnector.timeLapse();
	//				Log.i("AssetsManager", "~~ Time lapse for [SELECT LOCATIONID=" + location.getID() + " OWNERID = "
	//						+ getPilot().getCharacterID() + "] - " + lapse);
	//				assetsAtLocationcache.put(location.getID(), (ArrayList<Asset>) assetList);
	//				// Update the dirty state to signal modification of store structures.
	//				setDirty(true);
	//				Log.i("AssetsManager", "<< AssetsManager.searchAsset4Location [" + assetList.size() + "]");
	//			} catch (java.sql.SQLException sqle) {
	//				sqle.printStackTrace();
	//			}
	//		return (ArrayList<Asset>) assetList;
	//	}
	//
	//	//	/**
	//	//	 * From the list of assets that have the Category "Blueprint" select only those that are of the Tech that is
	//	//	 * received on the parameter. Warning with the values because the comparison is performed on string literals
	//	//	 * and if the <code>qualifier</code> is not properly typed the result may be empty.
	//	//	 *
	//	//	 * @return list of <code>Asset</code>s that are Blueprints Tech II.
	//	//	 */
	//	//	public ArrayList<Asset> queryBlueprints2(final String qualifier) {
	//	//		ArrayList<Asset> bps = searchAsset4Category("Blueprint");
	//	//		WhereClause techWhere = new WhereClause(EAssetsFields.TECH, EMode.EQUALS, qualifier);
	//	//		EveFilter filter = new EveFilter(bps, techWhere);
	//	//		return filter.getResults();
	//	//	}
	//
	//	//	public ArrayList<Blueprint> queryT1Blueprints1() {
	//	//		if (null == t1blueprints) getPilot().updateBlueprints();
	//	//		return t1blueprints;
	//	//	}
	//	//
	//	//	public ArrayList<Blueprint> queryT2Blueprints1() {
	//	//		if (null == t2blueprints) getPilot().updateBlueprints();
	//	//		return t2blueprints;
	//	//	}
	//
	//	public Blueprint searchBlueprintByID(final long assetid) {
	//		for (Blueprint bp : getBlueprints()) {
	//			String refs = bp.getStackIDRefences();
	//			if (refs.contains(Long.valueOf(assetid).toString())) return bp;
	//		}
	//		return null;
	//	}
	//
	//	/**
	//	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T1
	//	 * blueprints. We expect this is not cost intensive because this function is called few times.
	//	 *
	//	 * @return list of T1 blueprints.
	//	 */
	//	public ArrayList<Blueprint> searchT1Blueprints() {
	//		ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
	//		for (Blueprint bp : getBlueprints())
	//			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) blueprintList.add(bp);
	//		return blueprintList;
	//	}
	//
	//	//	/**
	//	//	 * This method initialized all the transient fields that are expected to be initialized with empty data
	//	//	 * structures.
	//	//	 */
	//	//	public void reinstantiate() {
	//	//	}
	//
	//	/**
	//	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T2
	//	 * blueprints. We expect this is not cost intensive because this function is called few times.
	//	 *
	//	 * @return list of T2 blueprints.
	//	 */
	//	public ArrayList<Blueprint> searchT2Blueprints() {
	//		ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
	//		for (Blueprint bp : getBlueprints())
	//			if (bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) blueprintList.add(bp);
	//		return blueprintList;
	//	}
	//
	//	public ArrayList<Asset> searchT2Modules() {
	//		Log.i("NEOCOM", ">> EveChar.queryT2Modules");
	//		//	Select assets of type blueprint and that are of T2.
	//		List<Asset> assetList = new ArrayList<Asset>();
	//		assetList = assetsAtCategoryCache.get("T2Modules");
	//		if (null == assetList)
	//			try {
	//				AppConnector.startChrono();
	//				Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
	//				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
	//				Where<Asset, String> where = queryBuilder.where();
	//				where.eq("ownerID", getPilot().getCharacterID());
	//				where.and();
	//				where.eq("category", ModelWideConstants.eveglobal.Module);
	//				where.and();
	//				where.eq("tech", ModelWideConstants.eveglobal.TechII);
	//				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
	//				assetList = assetDao.query(preparedQuery);
	//				Duration lapse = AppConnector.timeLapse();
	//				Log.i("EVEI Time", "~~ Time lapse for [SELECT CATEGORY=MODULE TECH=TECH II OWNERID = "
	//						+ getPilot().getCharacterID() + "] - " + lapse);
	//				assetsAtCategoryCache.put("T2Modules", (ArrayList<Asset>) assetList);
	//			} catch (SQLException sqle) {
	//				sqle.printStackTrace();
	//			}
	//		Log.i("NEOCOM", "<< EveChar.queryT2Modules");
	//		return (ArrayList<Asset>) assetList;
	//	}
	//
	//	public void setPilot(final EveChar newPilot) {
	//		pilot = newPilot;
	//	}
	//
	//	/**
	//	 * Retrieves from the database all the stacks for an specific item type id. The method stores the results
	//	 * into the cache so next accesses will not trigger database access.
	//	 *
	//	 * @param item
	//	 * @return the list of stacks for this type id that belong to this pilot.
	//	 */
	//	public ArrayList<Asset> stacks4Item(final EveItem item) {
	//		// Check if results already on cache.
	//		ArrayList<Asset> hit = stacksByItemCache.get(item.getItemID());
	//		if (null != hit) return hit;
	//		//	Select assets for the owner and with an specific type id.
	//		List<Asset> assetList = new ArrayList<Asset>();
	//		try {
	//			accessDao();
	//			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
	//			Where<Asset, String> where = queryBuilder.where();
	//			where.eq("ownerID", getPilot().getCharacterID());
	//			where.and();
	//			where.eq("typeID", item.getItemID());
	//			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
	//			assetList = assetDao.query(preparedQuery);
	//		} catch (java.sql.SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		stacksByItemCache.put(item.getItemID(), (ArrayList<Asset>) assetList);
	//		return (ArrayList<Asset>) assetList;
	//	}
	//
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
	//		for (Blueprint blueprint : bplist)
	//			checkBPCStacking(bpStacks, blueprint);
	//
	//		// Extract stacks and store them into the caches.
	//		blueprintCache.addAll(bpStacks.values());
	//		// Update the database information.
	//		for (Blueprint blueprint : blueprintCache)
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
	//				Log.i("DB", "-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
	//			} catch (final SQLException sqle) {
	//				Log.e("NEOCOM", "E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
	//				sqle.printStackTrace();
	//			} catch (final RuntimeException rtex) {
	//				Log.e("NEOCOM", "E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + rtex.getMessage());
	//				rtex.printStackTrace();
	//			}
	//	}
	//
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AssetsManager [ ");
		buffer.append("owner:").append(getPilot().getName())
				.append("assetCount:").append(fullAssetList.size()).append(" ")
				.append("totalCount:").append(totalAssets).append(" ")
				.append("]");
		return buffer.toString();
	}
	//
	//	protected EveChar getPilot() {
	//		return pilot;
	//	}
	//
	//	private void accessDao() {
	//		if (null == assetDao) try {
	//			assetDao = AppConnector.getDBConnector().getAssetDAO();
	//			if (null == assetDao) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
	//		} catch (SQLException sqle) {
	//			// Interrupt processing and signal a runtime exception.
	//			throw new RuntimeException(sqle.getMessage());
	//		}
	//	}
	//
	//	/**
	//	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
	//	 * same container so the locationID, the parentContainer and the typeID should match to perform the
	//	 * aggregation.<br>
	//	 * Aggregation key: ID-LOCATION-CONTAINER
	//	 *
	//	 * @param targetContainer
	//	 *          the stack storage that contains the list of registered blueprints
	//	 * @param bp
	//	 *          the blueprint part to be added to the hierarchy
	//	 */
	//	private void checkBPCStacking(final HashMap<String, Blueprint> targetContainer, final Blueprint bp) {
	//		// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.ASSETID
	//		String id = bp.getStackID();
	//		Blueprint hit = targetContainer.get(id);
	//		if (null == hit) {
	//			// Miss. The blueprint is not registered.
	//			Log.i("EVEI", "-- AssetsManager.checkBPCStacking >Stacked blueprint. " + bp.toString());
	//			bp.registerReference(bp.getAssetID());
	//			targetContainer.put(id, bp);
	//		} else {
	//			//Hit. Increment the counter for this stack. And store the id
	//			hit.setQuantity(hit.getQuantity() + bp.getQuantity());
	//			hit.registerReference(bp.getAssetID());
	//		}
	//	}
	//
	//	private void setDirty(final boolean value) {
	//		getPilot().setDirty(value);
	//	}
	//
	//	private void updateBlueprints() {
	//		Log.i("EVEI", ">> AssetsManager.updateBlueprints");
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
	//			Log.i("TIME", "~~ Time lapse for BLUEPRINT [SELECT OWNERID = " + getPilot().getCharacterID() + "] - " + lapse);
	//			// Check if the list is empty. Then force a refresh download.
	//			if (blueprintCache.size() < 1) getPilot().forceRefresh();
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		Log.i("EVEI", "<< AssetsManager.updateBlueprints [" + blueprintCache.size() + "]");
	//		//		return (ArrayList<Blueprint>) blueprintList;
	//	}
	//
	//	/**
	//	 * Gets the list of locations for a character. It will store the results into a local variable to speed up
	//	 * any other request because this is valid forever while the duration of the session because this data is
	//	 * only modified when the assets are updated.
	//	 *
	//	 * @param characterID
	//	 * @return
	//	 */
	//	private synchronized void updateLocations() {
	//		Log.i("NEOCOM", ">> AssetsManager.updateLocations");
	//		AppConnector.startChrono();
	//		//	Select assets for the owner and with an specific type id.
	//		List<Integer> locationIdentifierList = new ArrayList<Integer>();
	//		try {
	//			accessDao();
	//			GenericRawResults<String[]> rawResults = assetDao
	//					.queryRaw("SELECT DISTINCT locationID FROM Assets WHERE ownerId=" + getPilot().getCharacterID());
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
	//		setDirty(true);
	//		Log.i("NEOCOM", "<< AssetsManager.updateLocations. " + AppConnector.timeLapse());
	//	}
}
// - UNUSED CODE ............................................................................................
