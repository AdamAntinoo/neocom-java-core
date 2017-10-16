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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.interfaces.INamed;
import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.CVariant.EDefaultVariant;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Region;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.model.SpaceContainer;
import org.dimensinfin.eveonline.neocom.model.TimeStamp;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.parser.corporation.AssetListParser;
import com.beimin.eveapi.parser.pilot.LocationsParser;
import com.beimin.eveapi.parser.pilot.PilotAssetListParser;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.beimin.eveapi.response.shared.LocationsResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.j256.ormlite.dao.Dao;
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
public class AssetsManager extends AbstractManager implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long																serialVersionUID			= -8502099148768297876L;
	private static Logger																		logger								= Logger.getLogger("AssetsManager");

	private transient Dao<NeoComAsset, String>							assetDao							= null;

	// - L O C A T I O N   M A N A G E M E N T
	//	private int																							locationCount						= -1;
	//	private HashSet<String>																	regionNames							= null;
	//	private ArrayList<EveLocation>													locationsList						= null;

	// - A S S E T   M A N A G E M E N T
	private long																						totalAssets						= -1;
	//	private long																						verificationAssetCount	= 0;
	@JsonInclude
	public double																						totalAssetsValue			= 0.0;
	//	public final HashMap<Long, Region>											regions								= new HashMap<Long, Region>();
	//	private final HashMap<Long, EveLocation>								locations							= new HashMap<Long, EveLocation>();
	//	private final HashMap<Long, NeoComAsset>								containers						= new HashMap<Long, NeoComAsset>();
	private TimeStamp																				assetsCacheTime				= null;;

	/** Probably redundant with containers. */
	private final HashMap<Long, NeoComAsset>								assetsAtContainer			= new HashMap<Long, NeoComAsset>();
	/** The new list of ships with their state and their contents. An extension of containers. */
	private final HashMap<Long, NeoComAsset>								ships									= new HashMap<Long, NeoComAsset>();
	private final HashMap<Long, ArrayList<NeoComAsset>>			assetsAtLocationCache	= new HashMap<Long, ArrayList<NeoComAsset>>();
	private final HashMap<String, ArrayList<NeoComAsset>>		assetsAtCategoryCache	= new HashMap<String, ArrayList<NeoComAsset>>();
	private final HashMap<Integer, ArrayList<NeoComAsset>>	stacksByItemCache			= new HashMap<Integer, ArrayList<NeoComAsset>>();
	/** The complete list of blueprints maybe is not used */
	private final ArrayList<NeoComBlueprint>								blueprintCache				= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								t1BlueprintCache			= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								t2BlueprintCache			= new ArrayList<NeoComBlueprint>();
	private final ArrayList<NeoComBlueprint>								bpoCache							= new ArrayList<NeoComBlueprint>();

	public final HashMap<Long, ArrayList<NeoComAsset>>			assetCache						= new HashMap<Long, ArrayList<NeoComAsset>>();
	public final HashMap<Long, ArrayList<NeoComAsset>>			asteroidCache					= new HashMap<Long, ArrayList<NeoComAsset>>();
	public String																						iconName							= "assets.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	private transient HashMap<Long, NeoComAsset>						assetMap							= new HashMap<Long, NeoComAsset>();
	private Vector<NeoComAsset>															unlocatedAssets				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager(final NeoComCharacter pilot) {
		super(pilot);
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
	 * The processing of the assets will be performed with a SAX parser instead of the general use of a DOM
	 * parser. This requires then that the cache verification and other cache tasks be performed locally to
	 * avoid downloading the same information multiple times.<br>
	 * Cache expiration is of 6 hours but we will set it up to 3.<br>
	 * After verification we have to update the list, we then fire the events to signal asset list modification
	 * to any dependent data structures or UI objects that may be showing this information.<br>
	 * This update mechanism may require reading the last known state of the assets list from the sdcard file
	 * storage. This information is not stored automatically with the character information to speed up the
	 * initialization process and is loading only when needed and this data should be accessed. This is an
	 * special case because the assets downloaded are being written to a special set of records in the User
	 * database. Then, after the download terminates the database is updated to move those assets to the right
	 * character. It is supposed that this is performed in the background and that while we are doing this the
	 * uses has access to an older set of assets. New implementation. With the use of the eveapi library there
	 * is no need to use the URL to locate and download the assets. We use the eveapi locator and parser to get
	 * the data structures used to generate and store the assets into the local database. We first clear any
	 * database records not associated to any owner, the add records for a generic owner and finally change the
	 * owner to this character.
	 */
	public void downloadCorporationAssets() {
		AssetsManager.logger.info(">> [AssetsManager.downloadCorporationAssets]");
		try {
			// Clear any previous record with owner -1 from database.
			ModelAppConnector.getSingleton().getDBConnector().clearInvalidRecords(this.getPilot().getCharacterID());
			// Download and parse the assets. Check the api key to detect corporations and use the other parser.
			//			AssetListResponse response = null;
			//			if (getName().equalsIgnoreCase("Corporation")) {
			//				AssetListParser parser = com.beimin.eveapi.corporation.assetlist.AssetListParser.getInstance();
			//				response = parser.getResponse(getAuthorization());
			//				if (null != response) {
			//					final HashSet<EveAsset> assets = new HashSet<EveAsset>(response.getAll());
			//					assetsCacheTime = new Instant(response.getCachedUntil());
			//					// Assets may be parent of other assets so process them recursively.
			//					for (final EveAsset eveAsset : assets) {
			//						processAsset(eveAsset, null);
			//					}
			//				}
			//			} else {
			AssetListParser parser = new AssetListParser();
			AssetListResponse response = parser.getResponse(this.getPilot().getAuthorization());
			if (null != response) {
				List<Asset> assets = response.getAll();
				//				this.getPilot().updateAssetsAccesscacheTime(response.getCachedUntil());
				// Assets may be parent of other assets so process them recursively.
				for (final Asset eveAsset : assets) {
					this.processAsset(eveAsset, null);
				}
			}
			//			}
			ModelAppConnector.getSingleton().getDBConnector().replaceAssets(this.getPilot().getCharacterID());

			//				// Update the caching time to the time set by the eveapi.
			String reference = this.getPilot().getCharacterID() + ".ASSETS";
			new TimeStamp(reference, new Instant(response.getCachedUntil()));
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		// Clean all user structures invalid after the reload of the assets.
		//			assetsManager = null;
		//		totalAssets = -1;
		//		clearTimers();
		//		JobManager.clearCache();

		//			this.setDirty(true);
		//			this.fireStructureChange("EVENTSTRUCTURE_EVECHARACTER_ASSETS", null, null);
		AssetsManager.logger.info("<< [AssetsManager.downloadCorporationAssets");
	}

	/**
	 * The new downloader uses the eveapi library to process the xml source code received. This simplifies the
	 * code but adds the need to control the download format. There are two sets of records, one with the
	 * hierarchical dependencies between the assets and the other a flat list of all the assets with no
	 * dependencies but with a mix of Location and Asset codes on the locationId field.<br>
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the
	 * final list on the database will have the correct parentship hierarchy set up.<br>
	 * <br>
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right
	 * one.<br>
	 * <br>
	 * There are two flavour for the asset download process. One for Pilots and other for Corporation assets.
	 */
	public void downloadPilotAssets() {
		AssetsManager.logger.info(">> [AssetsManager.downloadPilotAssets]");
		try {
			// Clear any previous record with owner -1 from database.
			IDatabaseConnector dbConn = ModelAppConnector.getSingleton().getDBConnector();
			synchronized (dbConn) {
				dbConn.clearInvalidRecords(this.getPilot().getCharacterID());
			}
			// Parse the CCP data to a list of assets
			PilotAssetListParser parser = new PilotAssetListParser();
			AssetListResponse response = parser.getResponse(this.getPilot().getAuthorization());
			if (null != response) {
				unlocatedAssets = new Vector<NeoComAsset>();
				List<Asset> assets = response.getAll();
				// Assets may be parent of other assets so process them recursively if the hierarchical mode is selected.
				for (final Asset eveAsset : assets) {
					try {
						this.processAsset(eveAsset, null);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
				// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
				for (NeoComAsset asset : unlocatedAssets) {
					ELocationType validation = this.validateLocation(asset);
				}
			}
			// Assign the assets to the pilot.
			synchronized (dbConn) {
				dbConn.replaceAssets(this.getPilot().getCharacterID());
			}
			// Update the caching time to the time set by the eveapi.
			String reference = this.getPilot().getCharacterID() + ".ASSETS";
			if (null == assetsCacheTime) {
				assetsCacheTime = new TimeStamp(reference, new Instant(response.getCachedUntil()));
			} else {
				assetsCacheTime.updateTimeStamp(new Instant(response.getCachedUntil()));
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		AssetsManager.logger.info("<< [AssetsManager.downloadPilotAssets]");
	}

	public TimeStamp getAssetsCacheTime() {
		return assetsCacheTime;
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
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				queryBuilder.setCountOf(true).where().eq("ownerID", this.getPilot().getCharacterID());
				totalAssets = assetDao.countOf(queryBuilder.prepare());
			} catch (SQLException sqle) {
				AssetsManager.logger.info("W> Proglem calculating the number of assets for " + this.getPilot().getName());
			}
		}
		return totalAssets;
	}

	//	@Override
	//	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
	//		return new ArrayList<AbstractComplexNode>();
	//	}
	@JsonIgnore
	public ArrayList<NeoComBlueprint> getBlueprints() {
		if (null == blueprintCache) {
			this.updateBlueprints();
		}
		if (blueprintCache.size() == 0) {
			this.updateBlueprints();
		}
		return blueprintCache;
	}

	public EveLocation getLocationById(final long id) {
		// Search for the location on the Location list.
		for (Long key : locations.keySet()) {
			if (key == id) return locations.get(key);
		}
		return null;
	}

	/**
	 * Returns the list of different locations where this character has assets. The locations are the unique
	 * location ids that can be on the same or different systems. If a system has assets in more that one
	 * station or in space the number of ids that have the same system in common may be greater that 1.
	 * 
	 * @return
	 * 
	 * @return
	 */
	public Hashtable<Long, EveLocation> getLocations() {
		// If the list is empty the go to the database and get the assets
		if (null == locations) {
			this.initialize();
		}
		return locations;
	}

	@Override
	public String getOrderingName() {
		return "Assets Manager";
	}

	/**
	 * Returns the list of different Regions found on the list of locations.
	 * 
	 * @return
	 */
	@Override
	public Hashtable<Long, Region> getRegions() {
		if (!this.isInitialized()) {
			this.initialize();
		}
		return regions;
	}

	public List<NeoComAsset> getShips() {
		return this.searchAsset4Category("Ship");
	}

	//	public int getLocationCount() {
	//		if (locationCount < 0) {
	//			this.updateLocations();
	//		}
	//		return locationCount;
	//	}

	/**
	 * This is the initialization code that we should always use when we need to operate with a loaded
	 * AssetsManager.<br>
	 * The initialization will load all the Locations and some of the counters. The method processed the result
	 * to generate the root list of Regions, then the space Locations and then their contents in the case the
	 * Location has been downloaded.
	 * 
	 * @return
	 */
	@Override
	public AssetsManager initialize() {
		// INITIALIZE - Initialize the number of assets.
		this.getAssetTotalCount();
		// INITIALIZE - Initialize the Locations and the Regions
		List<NeoComAsset> locs = ModelAppConnector.getSingleton().getDBConnector()
				.queryAllAssetLocations(this.getPilot().getCharacterID());
		regions.clear();
		locations.clear();
		// Process the locations to a new list of Regions.
		for (NeoComAsset asset : locs) {
			long locid = asset.getLocationID();
			this.processLocation(locid);
		}
		//		containers.clear();
		//		// Process the containers to locate new dependencies.
		//		List<NeoComAsset> conts = ModelAppConnector.getSingleton().getDBConnector()
		//				.queryAllAssetContainers(this.getPilot().getCharacterID());
		//		for (NeoComAsset asset : conts) {
		//			long id = asset.getParentContainerId();
		//			if (id < 0) {
		//				// This is an asset already located.
		//				continue;
		//			}
		//			if (containers.containsKey(id)) {
		//				continue;
		//			} else {
		//				// Search this identifier as an extended location or as an asset.
		//				EveLocation location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(id);
		//				if (location.isUnknown()) {
		//					NeoComAsset container = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(id);
		//					if (null == container) {
		//						continue;
		//					}
		//					containers.put(id, container);
		//					// Now Process the Location for this Container.
		//					this.processLocation(container.getLocationID());
		//				}
		//				this.processLocation(id);
		//			}
		//		}
		initialized = true;
		return this;
	}

	/**
	 * Checks if that category was requested before and it is on the cache. If found returns that list.
	 * Otherwise go to the database for the list.
	 * 
	 * @param category
	 * @return
	 */
	public List<NeoComAsset> searchAsset4Category(final String category) {
		//	Select assets for the owner and with an specific category.
		List<NeoComAsset> assetsCategoryList = new ArrayList<NeoComAsset>();
		assetsCategoryList = assetsAtCategoryCache.get(category);
		if (null == assetsCategoryList) {
			assetsCategoryList = ModelAppConnector.getSingleton().getDBConnector()
					.searchAsset4Category(this.getPilot().getCharacterID(), category);
			assetsAtCategoryCache.put(category, (ArrayList<NeoComAsset>) assetsCategoryList);
		} else {
			AssetsManager.logger.info("~~ [AssetsManager.searchAsset4Category]> Cache hit [SELECT CATEGORY=" + category
					+ " OWNERID = " + this.getPilot().getCharacterID() + "]");
		}
		return assetsCategoryList;
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

	public ArrayList<NeoComAsset> searchAsset4Location(final EveLocation location) {
		AssetsManager.logger.info(">> AssetsManager.searchAsset4Location");
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		// Check if we have already that list on the cache.
		assetList = assetsAtLocationCache.get(location.getID());
		if (null == assetList) {
			try {
				ModelAppConnector.getSingleton().startChrono();
				this.accessDao();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", this.getPilot().getCharacterID());
				where.and();
				where.eq("locationID", location.getID());
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = ModelAppConnector.getSingleton().timeLapse();
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

	//	public HashSet<String> queryT2ModuleNames() {
	//		HashSet<String> names = new HashSet<String>();
	//		ArrayList<Asset> modules = searchT2Modules();
	//		for (Asset mod : modules) {
	//			names.add(mod.getName());
	//		}
	//		return names;
	//	}

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
				ModelAppConnector.getSingleton().startChrono();
				Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("ownerID", this.getPilot().getCharacterID());
				where.and();
				where.eq("category", ModelWideConstants.eveglobal.Module);
				where.and();
				where.eq("tech", ModelWideConstants.eveglobal.TechII);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				Duration lapse = ModelAppConnector.getSingleton().timeLapse();
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
				Dao<NeoComBlueprint, String> blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDAO();
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
		buffer.append("owner:").append(this.getPilot().getName()).append(" ");
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
		if (null != locations) {
			buffer.append("locationsList: ").append(locations).append(" ");
		}
		if (null != regions) {
			buffer.append("regionNames: ").append(regions).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	protected synchronized double calculateAssetValue(final NeoComAsset asset) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = asset.getItem().getHighestBuyerPrice().getPrice();
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	//	/**
	//	 * This method initialized all the transient fields that are expected to be initialized with empty data
	//	 * structures.
	//	 */
	//	public void reinstantiate() {
	//	}

	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information.
	 * <br>
	 * This method checks the location to detect if under the new flat model the location is an asset and then
	 * we should convert it into a parent or the location is a real location. Initially this is done checking
	 * the location id value if under 1000000000000.
	 * 
	 * @param eveAsset
	 *          the original assest as downloaded from CCP api
	 * @return
	 */
	protected NeoComAsset convert2Asset(final Asset eveAsset) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		Long locid = eveAsset.getLocationID();
		if (null == locid) {
			locid = (long) -2;
		}
		newAsset.setLocationID(locid);
		//		// Under the flat api check if the location is a real location or an asset.
		//		if (null == locid) {
		//			locid = (long) -2;
		//		}
		//		if (locid > 1000000000000L) {
		//			// This is an asset so it represents the parent. We have not the location since the parent may not exist.
		//			newAsset.setLocationID(-2);
		//			newAsset.setParentId(locid);
		//		} else {
		//			// The location is a real location.
		//			newAsset.setLocationID(locid);
		//		}

		newAsset.setQuantity(eveAsset.getQuantity());
		newAsset.setFlag(eveAsset.getFlag());
		newAsset.setSingleton(eveAsset.getSingleton());

		// Get access to the Item and update the copied fields.
		final EveItem item = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(newAsset.getTypeID());
		if (null != item) {
			try {
				newAsset.setName(item.getName());
				newAsset.setCategory(item.getCategory());
				newAsset.setGroupName(item.getGroupName());
				newAsset.setTech(item.getTech());
				if (item.isBlueprint()) {
					newAsset.setBlueprintType(eveAsset.getRawQuantity());
				}
			} catch (RuntimeException rtex) {
			}
		}
		// Add the asset value to the database.
		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	protected String downloadAssetEveName(final long assetID) {
		// Wait up to one second to avoid request rejections from CCP.
		try {
			Thread.sleep(500); // 500 milliseconds is half second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		final Vector<Long> ids = new Vector<Long>();
		ids.add(assetID);
		try {
			final LocationsParser parser = new LocationsParser();
			final LocationsResponse response = parser.getResponse(this.getPilot().getAuthorization(), ids);
			if (null != response) {
				Set<Location> userNames = response.getAll();
				if (userNames.size() > 0) return userNames.iterator().next().getItemName();
			}
		} catch (final ApiException e) {
			AssetsManager.logger.info("W- EveChar.downloadAssetEveName - asset has no user name defined: " + assetID);
			//			e.printStackTrace();
		}
		return null;
	}

	private void accessDao() {
		if (null == assetDao) {
			try {
				assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
				if (null == assetDao) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
			} catch (SQLException sqle) {
				// Interrupt processing and signal a runtime exception.
				throw new RuntimeException(sqle.getMessage());
			}
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
	 * Get the complete list of the assets that belong to this owner.
	 * 
	 * @return
	 */
	private ArrayList<NeoComAsset> getAllAssets() {
		// Select assets for the owner.
		ArrayList<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
			ModelAppConnector.getSingleton().startChrono();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = (ArrayList<NeoComAsset>) assetDao.query(preparedQuery);
			Duration lapse = ModelAppConnector.getSingleton().timeLapse();
			AssetsManager.logger
					.info("~~ Time lapse for [SELECT * FROM ASSETS OWNER = " + this.getPilot().getCharacterID() + "] - " + lapse);
			AssetsManager.logger.info("-- Assets processed: " + assetList.size());
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return assetList;
	}

	private String getTSAssetsReference() {
		return this.getPilot().getCharacterID() + ".ASSETS";
	}

	/**
	 * Processes an asset and all their children. This method converts from a API record to a database asset
	 * record.<br>
	 * For flat assets it will detect the Location and if matched to an unknown location store the asset for
	 * second pass processing.
	 * 
	 * @param eveAsset
	 */
	private void processAsset(final Asset eveAsset, final NeoComAsset parent) {
		final NeoComAsset myasset = this.convert2Asset(eveAsset);
		if (null != parent) {
			myasset.setParent(parent);
			myasset.setParentContainer(parent);
			// Set the location to the parent's location is not set.
			if (myasset.getLocationID() == -1) {
				myasset.setLocationID(parent.getLocationID());
			}
		}
		// Only search names for containers and ships.
		if (myasset.isShip()) {
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		if (myasset.isContainer()) {
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		try {
			//			final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
			this.accessDao();
			final HashSet<Asset> children = new HashSet<Asset>(eveAsset.getAssets());
			if (children.size() > 0) {
				myasset.setContainer(true);
			}
			if (myasset.getCategory().equalsIgnoreCase("Ship")) {
				myasset.setShip(true);
			}
			myasset.setOwnerID(this.getPilot().getCharacterID() * -1);
			assetDao.create(myasset);

			// Check the asset location. The location can be a known game station, a known user structure, another asset
			// or an unknown player structure. Check which one is this location.
			EveLocation targetLoc = ModelAppConnector.getSingleton().getCCPDBConnector()
					.searchLocationbyID(myasset.getLocationID());
			if (targetLoc.getTypeID() == ELocationType.UNKNOWN) {
				// Add this asset to the list of items to be reprocessed.
				unlocatedAssets.add(myasset);
			}
			// Process all the children and convert them to assets.
			if (children.size() > 0) {
				for (final Asset childAsset : children) {
					this.processAsset(childAsset, myasset);
				}
			}
			AssetsManager.logger.info("-- Wrote asset to database id [" + myasset.getAssetID() + "]");
		} catch (final SQLException sqle) {
			AssetsManager.logger.severe("E> [AssetsManager.processAsset]Unable to create the new asset ["
					+ myasset.getAssetID() + "]. " + sqle.getMessage());
			sqle.printStackTrace();
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
		try {
			// Remove the element from the map.
			assetMap.remove(asset.getAssetID());
			// Add the asset to the verification count.
			//			verificationAssetCount++;
			// Add the asset value to the owner balance.
			totalAssetsValue += asset.getIskValue();
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
					SpaceContainer container = new SpaceContainer().copyFrom(asset);
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
				NeoComAsset parent = asset.getParentContainer();
				if (null == parent) {
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

	private void processLocation(final long identifier) {
		if (identifier < 0) return;
		if (locations.containsKey(identifier))
			return;
		else {
			EveLocation location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(identifier);
			locations.put(identifier, location);
			long regid = location.getRegionID();
			Region reg = regions.get(regid);
			if (null == reg) {
				reg = new Region(location.getRegion()).setDownloaded(true);
				reg.addLocation(location);
				regions.put(regid, reg);
			} else {
				reg.addLocation(location);
			}
		}
	}

	private void readTimeStamps() {
		try {
			Dao<TimeStamp, String> tsDao = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO();
			QueryBuilder<TimeStamp, String> queryBuilder = tsDao.queryBuilder();
			Where<TimeStamp, String> where = queryBuilder.where();
			where.eq("reference", this.getTSAssetsReference());
			PreparedQuery<TimeStamp> preparedQuery = queryBuilder.prepare();
			List<TimeStamp> ts = tsDao.query(preparedQuery);
			if (ts.size() > 0) {
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
	private void removeNode(final AbstractComplexNode node) {
		// Check that the class of the item is an Asset. Anyway check for its collaboration.
		if (node instanceof AbstractViewableNode) {
			// Try to remove the asset if found
			if (node instanceof NeoComAsset) {
				assetMap.remove(((NeoComAsset) node).getAssetID());
			}
			// Remove also the nodes collaborated by it.
			for (AbstractComplexNode child : ((AbstractViewableNode) node)
					.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name())) {
				this.removeNode(child);
			}
		}
	}

	private void updateBlueprints() {
		AssetsManager.logger.info(">> AssetsManager.updateBlueprints");
		//		List<Blueprint> blueprintList = new ArrayList<Blueprint>();
		try {
			ModelAppConnector.getSingleton().startChrono();
			Dao<NeoComBlueprint, String> blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDAO();
			QueryBuilder<NeoComBlueprint, String> queryBuilder = blueprintDao.queryBuilder();
			Where<NeoComBlueprint, String> where = queryBuilder.where();
			where.eq("ownerID", this.getPilot().getCharacterID());
			PreparedQuery<NeoComBlueprint> preparedQuery = queryBuilder.prepare();
			blueprintCache.addAll(blueprintDao.query(preparedQuery));
			Duration lapse = ModelAppConnector.getSingleton().timeLapse();
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
	 * Checks if the Location can be found on the two lists of Locations, the CCP game list or the player
	 * compiled list. If the Location can't be found on any of those lists then it can be another asset
	 * (Container, Ship, etc) or another player/corporation structure resource that is not listed on the asset
	 * list.
	 * 
	 * @param asset
	 */
	private ELocationType validateLocation(final NeoComAsset asset) {
		long targetLocationid = asset.getLocationID();
		EveLocation targetLoc = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(targetLocationid);
		if (targetLoc.getTypeID() == ELocationType.UNKNOWN) {
			// Need to check if asset or unreachable location.
			NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(targetLocationid);
			if (null == target)
				return ELocationType.UNKNOWN;
			else {
				// Change the asset parentship and update the asset location with the location of the parent.
				asset.setParentId(targetLocationid);
				//// search for the location of the parent.
				//ELocationType parentLocationType = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(target.getLocationID()).getTypeID();
				//if(parentLocationType!=ELocationType.UNKNOWN)
				asset.setLocationID(target.getLocationID());
				asset.setDirty(true);
			}
			return ELocationType.UNKNOWN;
		} else
			return targetLoc.getTypeID();
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
	//			this.accessDao();
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
	//		this.setDirty(true);
	//		AssetsManager.logger.info("<< AssetsManager.updateLocations. " + AppConnector.timeLapse());
	//	}
}
// - UNUSED CODE ............................................................................................
