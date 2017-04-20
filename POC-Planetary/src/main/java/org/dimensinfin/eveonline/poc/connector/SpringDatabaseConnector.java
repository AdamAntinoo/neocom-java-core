//	PROJECT:        NeoCom (NEOC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.
package org.dimensinfin.eveonline.poc.connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.evemarket.service.MarketDataService;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Outpost;
import org.dimensinfin.eveonline.neocom.model.Schematics;

// - CLASS IMPLEMENTATION ...................................................................................
public class SpringDatabaseConnector extends AbstractDatabaseConnector {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger													logger										= Logger
			.getLogger("AndroidDatabaseConnector");

	private static final String										SELECT_RAW_PRODUCTRESULT	= "SELECT pstmo.typeID, pstmo.quantity, pstmo.schematicID"
			+ " FROM   planetSchematicsTypeMap pstmi, planetSchematicsTypeMap pstmo" + " WHERE  pstmi.typeID = ?"
			+ " AND    pstmo.schematicID = pstmi.schematicID" + " AND    pstmo.isInput = 0";
	private static final String										SELECT_TIER2_INPUTS				= "SELECT pstmt.TYPEid, pstmt.quantity"
			+ " FROM  planetSchematicsTypeMap pstms, planetSchematicsTypeMap pstmt" + " WHERE pstms.typeID = ?"
			+ " AND   pstms.isInput = 0" + " AND   pstmt.schematicID = pstms.schematicID" + " AND   pstmT.isInput = 1";
	private static final String										SELECT_SCHEMATICS_INFO		= "SELECT pstms.typeID, pstms.quantity, pstms.isInput"
			+ " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms" + " WHERE  pstmt.typeID = ?"
			+ " AND    pstmt.isInput = 0" + " AND    pstms.schematicID = pstmt.schematicID";

	//private static final String							DATABASE_URL							= "jdbc:sqlite:D:\\Development\\WorkStage\\ProjectsAngular\\NeoCom\\src\\main\\resources\\eve.db";
	//private static final String							DATABASE_URL							= "jdbc:sqlite:D:\\Development\\ProjectsAngular\\NeoCom\\src\\main\\resources\\eve.db";
	private static final String										DATABASE_URL							= "jdbc:sqlite:src/main/resources/eve.db";
	private static final String										SELECT_ITEM_BYID					= "SELECT it.typeID AS typeID, it.typeName AS typeName"
			+ " , ig.groupName AS groupName" + " , ic.categoryName AS categoryName" + " , it.basePrice AS basePrice"
			+ " , it.volume AS volume" + " , IFNULL(img.metaGroupName, " + '"' + "NOTECH" + '"' + ") AS Tech"
			+ " FROM invTypes it" + " LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID"
			+ " LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID"
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeID = ?";

	private static final String										SELECT_LOCATIONBYID				= "SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";
	private static final String										SELECT_LOCATIONBYSYSTEM		= "SELECT solarSystemID from mapSolarSystems WHERE solarSystemName = ?";

	private static final String										LOM4BLUEPRINT							= "SELECT iam.typeID, itb.typeName, iam.materialTypeID, it.typeName, ig.groupName, ic.categoryName, iam.quantity, iam.consume"
			+ " FROM industryActivityMaterials iam, invTypes itb, invTypes it, invGroups ig, invCategories ic"
			+ " WHERE iam.typeID = ?" + " AND iam.activityID = 1" + " AND itb.typeID = iam.typeID"
			+ " AND it.typeID = iam.materialTypeID" + " AND ig.groupID = it.groupID" + " AND ic.categoryID = ig.categoryID";

	private static final String										TECH4BLUEPRINT						= "SELECT iap.typeID, it.typeName, imt.metaGroupID, img.metaGroupName"
			+ " FROM industryActivityProducts iap, invTypes it, invMetaTypes imt, invMetaGroups img" + " WHERE it.typeID =?"
			+ " AND iap.typeID = it.typeID" + " AND imt.typeID = productTypeID" + " AND img.metaGroupID = imt.metaGroupID"
			+ " AND iap.activityID = 1";

	private static final String										REFINING_ASTEROID					= "SELECT itm.materialTypeID AS materialTypeID, itm.quantity AS qty"
			+ " , it.typeName AS materialName" + " , ito.portionSize AS portionSize"
			+ " FROM invTypeMaterials itm, invTypes it, invTypes ito" + " WHERE itm.typeID = ?"
			+ " AND it.typeID = itm.materialTypeID" + " AND ito.typeID = itm.typeID" + " ORDER BY itm.materialTypeID";

	private static final String										INDUSTRYACTIVITYMATERIALS	= "SELECT materialTypeID, quantity, consume FROM industryActivityMaterials WHERE typeID = ? AND activityID = 8";
	private static final String										STATIONTYPE								= "SELECT stationTypeID FROM staStations WHERE stationID = ?";
	private static final String										JOB_COMPLETION_TIME				= "SELECT typeID, time FROM industryActivity WHERE typeID = ? AND activityID = ?";
	private static final String										CHECK_INVENTION						= "SELECT count(*) AS counter"
			+ " FROM industryActivityProducts iap" + " WHERE iap.typeID = ?" + " AND iap.activityID = 8";
	private static final String										INVENTION_PRODUCT					= "SELECT productTypeID FROM industryActivityProducts WHERE typeID = ? AND activityID = 8";
	private static final String										CHECK_MANUFACTURABLE			= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String										CHECK_REACTIONABLE				= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String										CHECK_PLANETARYPRODUCED		= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String										REACTION_COMPONENTS				= "SELECT"
			+ "   invTypeReactions.reactionTypeID" + " , invTypes.typeID, invTypes.typeName" + " , invTypeReactions.input"
			+ " , COALESCE(dgmTypeAttributes.valueInt, dgmTypeAttributes.valueFloat) * invTypeReactions.quantity AS quantity"
			+ " FROM invTypeReactions, dgmTypeAttributes, invTypes" + " WHERE"
			+ " invTypes.typeId = invTypeReactions.typeID AND" + " invTypeReactions.reactionTypeID IN ("
			+ "    SELECT reactionTypeID" + "    FROM invTypeReactions" + "    WHERE typeID = ? ) AND"
			+ " dgmTypeAttributes.typeID = invTypeReactions.typeID";

	// - F I E L D - S E C T I O N ............................................................................
	//	private Context														_context									= null;
	//	private SQLiteDatabase										staticDatabase						= null;
	private Connection														ccpDatabase								= null;
	//	private EveDroidDBHelper									appDatabaseHelper					= null;
	private final HashMap<Integer, EveItem>				itemCache									= new HashMap<Integer, EveItem>();
	private final HashMap<Integer, MarketDataSet>	buyMarketDataCache				= new HashMap<Integer, MarketDataSet>();
	private final HashMap<Integer, MarketDataSet>	sellMarketDataCache				= new HashMap<Integer, MarketDataSet>();
	private final HashMap<Integer, Outpost>				outpostsCache							= new HashMap<Integer, Outpost>();
	//	private final HashMap<Long, Asset>				containerCache						= new HashMap<Long, Asset>();;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public AndroidDatabaseConnector(final Context app) {
	//		_context = app;
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//[01]

	@Override
	public boolean openCCPDataBase() {
		if (null == ccpDatabase) {
			//		Connection c = null;
			try {
				Class.forName("org.sqlite.JDBC");
				ccpDatabase = DriverManager.getConnection(DATABASE_URL);
				ccpDatabase.setAutoCommit(false);
			} catch (Exception sqle) {
				logger.warning(sqle.getClass().getName() + ": " + sqle.getMessage());
				//   System.exit(0);
			}
			logger.info("Opened database successfully");
		}
		return true;
	}

	/**
	 * Gets the list of type ids for the required resources to get an output batch of the former planetary
	 * resource.
	 * 
	 * @param typeID
	 * @return
	 */
	public Vector<Integer> searchInputResources(int typeID) {
		Vector<Integer> result = new Vector<Integer>();
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(SELECT_TIER2_INPUTS);
			prepStmt.setString(1, Integer.valueOf(typeID).toString());
			cursor = prepStmt.executeQuery();
			while (cursor.next()) {
				result.add(cursor.getInt(1));
			}
		} catch (Exception ex) {
			logger.warning("W- [SpingDatabaseConnector.searchRawPlanetaryOutput]> Database exception: " + ex.getMessage());
		} finally {
			try {
				if (cursor != null) cursor.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (prepStmt != null) prepStmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Search on the eve.db database for the attributes that describe an Item. Items are the lowest data
	 * structure for EVE resources or modules. Everything on Eve is an Item. We detect blueprints that require a
	 * different treatment and also we check for the availability of the item at the current cache if
	 * implemented.
	 */
	@Override
	public EveItem searchItembyID(final int typeID) {
		// Search the item on the cache.
		EveItem hit = itemCache.get(typeID);
		if (null == hit) {
			PreparedStatement prepStmt = null;
			ResultSet cursor = null;
			try {
				hit = new EveItem();
				//			final Cursor cursor = getCCPDatabase().rawQuery(SELECT_ITEM_BYID,
				//					new String[] { Integer.valueOf(typeID).toString() });
				//	      Statement stmt = getCCPDatabase().createStatement();
				prepStmt = getCCPDatabase().prepareStatement(SELECT_ITEM_BYID);
				prepStmt.setString(1, Integer.valueOf(typeID).toString());
				cursor = prepStmt.executeQuery();
				// The query can be run but now there are ids that do not return data.
				boolean found = false;
				while (cursor.next()) {
					found = true;
					hit.setTypeID(cursor.getInt(1));
					hit.setName(cursor.getString(2));
					hit.setGroupname(cursor.getString(3));
					hit.setCategory(cursor.getString(4));
					hit.setBasePrice(cursor.getDouble(5));
					hit.setVolume(cursor.getDouble(6));
					// Process the Tech field. The query marks blueprints
					String tech = cursor.getString(7);
					if (tech.equalsIgnoreCase("NOTECH")) {
						// Double check it is a Blueprint
						hit.setTech(ModelWideConstants.eveglobal.TechI);
						if (hit.getName().contains(" II Blueprint")) {
							hit.setBlueprint(true);
							if (hit.getName().contains(" II Blueprint")) {
								hit.setTech(ModelWideConstants.eveglobal.TechII);
							}
							if (hit.getName().contains(" III Blueprint")) {
								hit.setTech(ModelWideConstants.eveglobal.TechIII);
							}
						}
					} else {
						hit.setTech(tech);
					}
				}
				if (!found) {
					logger.warning("W> AndroidDatabaseConnector.searchItembyID -- Item <" + typeID + "> not found.");
				}
			} catch (Exception e) {
				logger.warning("W> AndroidDatabaseConnector.searchItembyID -- Item <" + typeID + "> not found.");
				return new EveItem();
			} finally {
				try {
					if (cursor != null) cursor.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if (prepStmt != null) prepStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			itemCache.put(new Integer(typeID), hit);
		}
		return hit;
	}

	@Override
	public EveLocation searchLocationbyID(final long locationID) {
		EveLocation hit = new EveLocation(locationID);
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(SELECT_LOCATIONBYID);
			prepStmt.setString(1, Long.valueOf(locationID).toString());
			cursor = prepStmt.executeQuery();
			boolean detected = false;
			while (cursor.next()) {
				//				if (cursor.moveToFirst()) {
				detected = true;
				// Check returned values when doing the assignments.
				long fragmentID = cursor.getLong(5);
				if (fragmentID > 0) {
					hit.setSystemID(fragmentID);
					hit.setSystem(cursor.getString(6));
				} else {
					hit.setSystem(cursor.getString(3));
				}
				fragmentID = cursor.getLong(7);
				if (fragmentID > 0) {
					hit.setConstellationID(fragmentID);
					hit.setConstellation(cursor.getString(8));
				}
				fragmentID = cursor.getLong(9);
				if (fragmentID > 0) {
					hit.setRegionID(fragmentID);
					hit.setRegion(cursor.getString(10));
				}
				hit.setTypeID(cursor.getInt(2));
				hit.setStation(cursor.getString(3));
				hit.setLocationID(cursor.getLong(1));
				hit.setSecurity(cursor.getString(4));
				// Update the final ID
				hit.getID();
				detected = true;
			}
			//			if (!detected) // Search the location on the list of outposts.
			//				hit = searchOutpostbyID(locationID);
			//	}
		} catch (final Exception ex) {
			logger.warning("Location <" + locationID + "> not found.");
		}
		return hit;
	}

	@Override
	public EveLocation searchLocationBySystem(final String name) {
		EveLocation hit = new EveLocation();
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(SELECT_LOCATIONBYSYSTEM);
			prepStmt.setString(1, name);
			cursor = prepStmt.executeQuery();
			boolean detected = false;
			while (cursor.next()) {
				//				if (cursor.moveToFirst()) {
				detected = true;
				// Check returned values when doing the assignments.
				long fragmentID = cursor.getInt(1);
				if (fragmentID > 0) {
					hit.setSystemID(fragmentID);
					//					hit.setSystem(cursor.getString(6));
					//				} else {
					//					hit.setSystem(cursor.getString(3));
				}
				//				fragmentID = cursor.getLong(7);
				//				if (fragmentID > 0) {
				//					hit.setConstellationID(fragmentID);
				//					hit.setConstellation(cursor.getString(8));
				//				}
				//				fragmentID = cursor.getLong(9);
				//				if (fragmentID > 0) {
				//					hit.setRegionID(fragmentID);
				//					hit.setRegion(cursor.getString(10));
				//				}
				//				hit.setTypeID(cursor.getInt(2));
				//				hit.setStation(cursor.getString(3));
				//				hit.setLocationID(cursor.getLong(1));
				//				hit.setSecurity(cursor.getString(4));
				//				// Update the final ID
				//				hit.getID();
				detected = true;
			}
			//			if (!detected) // Search the location on the list of outposts.
			//				hit = searchOutpostbyID(locationID);
			//	}
		} catch (final Exception ex) {
			logger.warning("Location <" + name + "> not found.");
		}
		return searchLocationbyID(hit.getSystemID());
	}

	/**
	 * Search for this market data on the cache. <br>
	 * The cache used for the search depends on the side parameter received on the call. All default prices are
	 * references to the cost of the price to be spent to buy the item.<br>
	 * If not found on the memory cache then try to load from the serialized version stored on disk. This is an
	 * special implementation for SpringBoot applications that may run on a server so the cache disk location is
	 * implemented in a different way that on Android, indeed, because we can access the market data online we
	 * are not going to cache the data but get a fresh copy if not found on the cache.<br>
	 * If the data is not located on the case call the market data downloader and processor to get a new copy
	 * and store it on the cache.
	 * 
	 * @param itemID
	 *          item id code of the item assigned to this market request.
	 * @param side
	 *          differentiates if we like to BUY or SELL the item.
	 * @return the cached data or an empty locator ready to receive downloaded data.
	 */
	@Override
	public MarketDataSet searchMarketData(final int itemID, final EMarketSide side) {
		logger.info(">>[SpringDatabaseConnector.searchMarketData]> itemid: " + itemID + " side: " + side.toString());
		// for Market Data: " + itemID + " - " + side);
		// Search on the cache. By default load the SELLER as If I am buying the
		// item.
		HashMap<Integer, MarketDataSet> cache = sellMarketDataCache;
		if (side == EMarketSide.BUYER) {
			cache = buyMarketDataCache;
		}
		MarketDataSet entry = cache.get(itemID);
		if (null == entry) {
			// Download and process the market data right now.
			Vector<MarketDataSet> entries = MarketDataService.marketDataServiceEntryPoint(itemID);
			for (MarketDataSet data : entries) {
				if (data.getSide() == EMarketSide.BUYER) {
					buyMarketDataCache.put(itemID, entry);
					if (side == data.getSide()) entry = data;
				}
				if (data.getSide() == EMarketSide.SELLER) {
					sellMarketDataCache.put(itemID, entry);
					if (side == data.getSide()) entry = data;
				}
			}

			//		cache.put(itemID, entry);

			//			// Try to get the data from disk.
			//			entry = AppConnector.getStorageConnector().readDiskMarketData(itemID, side);
			//			if (null == entry) {
			//				// Neither on disk. Make a request for download and return a
			//				// dummy placeholder.
			//				entry = new MarketDataSet(itemID, side);
			//				if (true) {
			//					NeoComApp.getTheCacheConnector().addMarketDataRequest(itemID);
			//				}
			//			}
		} else {
			logger.info("-- [StringBatabaseConnector.searchMarketData]. Cache hit on memory.");
			//			// Check again the location. If is the default then request a new
			//			// update and remove it from the cache.
			//			long lid = entry.getBestMarket().getLocation().getID();
			//			if (lid < 0) {
			//				NeoComApp.getTheCacheConnector().addMarketDataRequest(itemID);
			//				cache.put(itemID, null);
			//			}
			//		}
			// Check entry timestamp before return. Post an update if old.
			// if (side.equalsIgnoreCase(ModelWideConstants.marketSide.CALCULATE))
			// return entry;
			// else {
			if (AppConnector.checkExpiration(entry.getTS(), ModelWideConstants.HOURS2)) {
				// Clear the cache for this item and call it again.
				sellMarketDataCache.remove(itemID);
				buyMarketDataCache.remove(itemID);
				return searchMarketData(itemID, side);
			}
		}
		logger.info("<<[SpringDatabaseConnector.searchMarketData]");
		return entry;
		// }
	}

	public int searchRawPlanetaryOutput(int typeID) {
		int outputResourceId = typeID;
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(SELECT_RAW_PRODUCTRESULT);
			prepStmt.setString(1, Integer.valueOf(typeID).toString());
			cursor = prepStmt.executeQuery();
			while (cursor.next()) {
				outputResourceId = cursor.getInt(1);
			}
		} catch (Exception ex) {
			logger.warning("W- [SpingDatabaseConnector.searchRawPlanetaryOutput]> Database exception: " + ex.getMessage());
		} finally {
			try {
				if (cursor != null) cursor.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (prepStmt != null) prepStmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return outputResourceId;
	}

	public Vector<Schematics> searchSchematics4Output(int targetId) {
		Vector<Schematics> scheList = new Vector<Schematics>();
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(SELECT_SCHEMATICS_INFO);
			prepStmt.setString(1, Integer.valueOf(targetId).toString());
			cursor = prepStmt.executeQuery();
			while (cursor.next()) {
				scheList.add(new Schematics().addData(cursor.getInt(1), cursor.getInt(2), cursor.getBoolean(3)));
			}
		} catch (Exception ex) {
			logger.warning("W- [SpingDatabaseConnector.searchRawPlanetaryOutput]> Database exception: " + ex.getMessage());
		} finally {
			try {
				if (cursor != null) cursor.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (prepStmt != null) prepStmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return scheList;
	}

	//[03]
	//	/**
	//	 * Like the other initialization methods this return a true if there was any problem during the process.
	//	 * 
	//	 * @return
	//	 */
	//	public boolean openDAO() {
	//		if (null == appDatabaseHelper) {
	//			appDatabaseHelper = OpenHelperManager.getHelper(_context, EveDroidDBHelper.class);
	//			return true;
	//		}
	//		return false;
	//	}
	private Connection getCCPDatabase() {
		if (null == ccpDatabase) openCCPDataBase();
		return ccpDatabase;
	}

	//[02]
	private String readJsonData() {
		//	String fileLocation = "C:\\Users\\ldediego\\UserData\\Workstage\\OrangeProjectsMars\\NeoCom\\src\\main\resources\\outposts.json";
		String fileLocation = "./src/main/resources/outposts.json";
		StringBuffer data = new StringBuffer();
		try {
			String str = "";
			InputStream is = new FileInputStream(new File(fileLocation));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is != null) while ((str = reader.readLine()) != null)
				data.append(str);
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
//	public boolean checkInvention(final int typeID) {
//		return checkRecordExistence(typeID, CHECK_INVENTION);
//	}
//
//	public boolean checkManufacturable(final int typeID) {
//		return checkRecordExistence(typeID, CHECK_MANUFACTURABLE);
//	}
//
//	/**
//	 * removes from the application database any asset and blueprint that contains the special -1 code as the
//	 * owner identifier. Those records are from older downloads and have to be removed to avoid merging with the
//	 * new download.
//	 */
//	public synchronized void clearInvalidRecords() {
//		SQLiteDatabase database = null;
//		try {
//			database = getAppDatabase();
//			synchronized (database) {
//				database.beginTransaction();
//				Log.i("", "-- clearInvalidAssets rows deleted ASSETS [OWNERID = -1] - "
//						+ database.delete("Assets", "ownerID" + "=-1", null));
//				Log.i("", "-- clearInvalidAssets rows deleted ASSETS [LOCATIONID = -1] - "
//						+ database.delete("Assets", "locationId" + "=-1", null));
//				Log.i("", "-- clearInvalidAssets rows deleted BLUEPRINTS [OWNERID = -1] - "
//						+ database.delete("Blueprints", "ownerID" + "=-1", null));
//				//				Log.i("", "-- clearInvalidAssets rows deleted JOBS [OWNERID = -1] - "
//				//						+ database.delete("Jobs", "ownerID" + "=-1", null));
//				database.setTransactionSuccessful();
//			}
//		} catch (final SQLiteException ex) {
//			logger.warning("W> Problem clearing invalid assets. " + ex.getMessage());
//		} finally {
//			if (null != database) {
//				database.endTransaction();
//			}
//		}
//	}
//
//	public void closeDatabases() {
//		appDatabaseHelper.close();
//	}
//
//	public Dao<Asset, String> getAssetDAO() throws java.sql.SQLException {
//		if (null == appDatabaseHelper) {
//			openDAO();
//		}
//		return appDatabaseHelper.getAssetDAO();
//	}
//
//	public Dao<Blueprint, String> getBlueprintDAO() throws java.sql.SQLException {
//		if (null == appDatabaseHelper) {
//			openDAO();
//		}
//		return appDatabaseHelper.getBlueprintDAO();
//	}
//
//	public Dao<Job, String> getJobDAO() throws java.sql.SQLException {
//		if (null == appDatabaseHelper) {
//			openDAO();
//		}
//		return appDatabaseHelper.getJobDAO();
//	}
//
//	public Dao<MarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException {
//		if (null == appDatabaseHelper) {
//			openDAO();
//		}
//		return appDatabaseHelper.getMarketOrderDAO();
//	}
//
//	public Dao<Property, String> getPropertyDAO() throws java.sql.SQLException {
//		if (null == appDatabaseHelper) {
//			openDAO();
//		}
//		return appDatabaseHelper.getPropertyDAO();
//	}
//
//	public SQLiteDatabase getStaticDatabase() throws SQLException {
//		if (null == staticDatabase) {
//			openAppDataBase();
//		}
//		return staticDatabase;
//	}

//	/**
//	 * Opens a SQLite database to get access to the Items and locations. If there is any problem the method
//	 * returns true.
//	 * 
//	 * @return <code>true</code> if there was any problem.
//	 */
//	public boolean openAppDataBase() {
//		if (null == staticDatabase) {
//			final String database = AppConnector.getAppFilePath(R.string.appdatabasefilename);
//			final String path = Environment.getExternalStorageDirectory() + "/" + database;
//			try {
//				if (AppConnector.sdcardAvailable())
//					// Open the helper to check version and availability.
//					//					appDatabaseHelper = new EveDroidDBHelper(_context);
//					staticDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
//				else
//					return true;
//				return false;
//			} catch (final SQLException sqle) {
//				logger.severe("E> Failed to open database: " + database);
//				logger.severe("E> " + sqle.getMessage());
//				throw new RuntimeException("E> Failed to open database: " + database + ". " + sqle.getMessage());
//			}
//		}
//		return true;
//	}

//[02]
//
//	public ArrayList<Job> searchJob4Class(final long characterID, final String classname) {
//		//	Select assets for the owner and woth an specific type id.
//		List<Job> joblist = new ArrayList<Job>();
//		try {
//			Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
//			QueryBuilder<Job, String> queryBuilder = jobDao.queryBuilder();
//			Where<Job, String> where = queryBuilder.where();
//			where.eq("ownerID", characterID);
//			where.and();
//			where.eq("jobType", classname);
//			PreparedQuery<Job> preparedQuery = queryBuilder.prepare();
//			joblist = jobDao.query(preparedQuery);
//		} catch (java.sql.SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return (ArrayList<Job>) joblist;
//
//	}
//
//	//	public EveItem searchItembyIDoff(final int typeID) {
//	//		// Search the item on the cache.
//	//		EveItem hit = itemCache.get(new Integer(typeID));
//	//		if (null == hit) {
//	//			try {
//	//				Dao<EveItem, String> dao = getItemDAO();
//	//				EveItem newItem = dao.queryForId(new Integer(typeID).toString());
//	//				if (null == newItem) return new EveItem();
//	//				itemCache.put(new Integer(typeID), newItem);
//	//				return newItem;
//	//			} catch (final Exception ex) {
//	//				logger.warning("W> Item <" + typeID + "> not found.");
//	//			}
//	//			return new EveItem();
//	//		} else
//	//			return hit;
//	//	}
//
//	public int searchJobExecutionTime(final int typeID, final int activityID) {
//		Log.i("EVEI", ">> AndroidDatabaseConnector.searchJobExecutionTime");
//		int jobTime = ModelWideConstants.HOURS2 / 1000;
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			final Cursor cursor = ccpDatabase.rawQuery(JOB_COMPLETION_TIME,
//					new String[] { Integer.valueOf(typeID).toString(), Integer.valueOf(activityID).toString() });
//			if (null != cursor) while (cursor.moveToNext())
//				jobTime = cursor.getInt(cursor.getColumnIndex("time"));
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for job time <" + typeID + "> not found.");
//		}
//		Log.i("EVEI", "<< AndroidDatabaseConnector.searchJobExecutionTime");
//		return jobTime;
//	}
//
//	public ArrayList<Resource> searchListOfDatacores(final int itemID) {
//		//		Log.i("AndroidDatabaseConnector", ">> AndroidDatabaseConnector.searchListOfDatacores");
//		ArrayList<Resource> inventionJob = new ArrayList<Resource>();
//		AppConnector.startChrono();
//		try {
//			final Cursor cursor = getCCPDatabase().rawQuery(INDUSTRYACTIVITYMATERIALS,
//					new String[] { Integer.valueOf(itemID).toString() });
//			if (null != cursor) {
//				while (cursor.moveToNext()) {
//					// The the data of the resource. Check for blueprints.
//					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
//					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
//					//			EveItem newItem = searchItembyID(resourceID);
//					//					Resource resource = ;
//					inventionJob.add(new Resource(resourceID, qty));
//				}
//				cursor.close();
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for material <" + itemID + "> not found.");
//		}
//		//		Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfDatacores " + inventionJob.getSize());
//		Log.i("DBQUERY.TIME", "~~ Time lapse for [SELECT INVENTION LOM " + itemID + "] - [" + inventionJob.size() + "] "
//				+ AppConnector.timeLapse());
//		return inventionJob;
//	}
//
//	public ArrayList<Resource> searchListOfMaterials(final int itemID) {
//		//		Log.i("AndroidDatabaseConnector", ">> AndroidDatabaseConnector.searchListOfMaterials");
//		ArrayList<Resource> buildJob = new ArrayList<Resource>();
//		AppConnector.startChrono();
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			Cursor cursor = ccpDatabase.rawQuery(
//					"SELECT typeID, materialTypeID, quantity, consume FROM industryActivityMaterials WHERE typeID = ? AND activityID = 1",
//					new String[] { Integer.valueOf(itemID).toString() });
//			int blueprintId = -1;
//			if (null != cursor) {
//				while (cursor.moveToNext()) {
//					// The the data of the resource. Check for blueprints.
//					int materialTypeID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
//					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
//					blueprintId = cursor.getInt(cursor.getColumnIndex("typeID"));
//					int consume = cursor.getInt(cursor.getColumnIndex("consume"));
//					Resource resource = new Resource(materialTypeID, qty);
//					buildJob.add(resource);
//				}
//				cursor.close();
//				// We have collected the required blueprint. Add it to the list of resources.
//				if (blueprintId != -1) {
//					buildJob.add(new Resource(blueprintId, 1));
//				}
//
//				// Add the skills to the list of resources
//				cursor = ccpDatabase.rawQuery(
//						"SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID",
//						new String[] { Integer.valueOf(itemID).toString() });
//				if (null == cursor) throw new Exception("E> Invalid cursor or empty.");
//				while (cursor.moveToNext()) {
//					// The the data of the resource. Check for blueprints.
//					int skillID = cursor.getInt(cursor.getColumnIndex("skillID"));
//					int level = cursor.getInt(cursor.getColumnIndex("level"));
//					Resource resource = new Resource(skillID, level);
//					buildJob.add(resource);
//				}
//				cursor.close();
//
//				//				Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//				//				return buildJob;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for material <" + itemID + "> not found.");
//		}
//		Log.i("DBQUERY.TIME",
//				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + buildJob.size() + "] " + AppConnector.timeLapse());
//		//		Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//		return buildJob;
//	}
//
//	public ArrayList<Resource> searchListOfReaction(final int itemID) {
//		ArrayList<Resource> buildJob = new ArrayList<Resource>();
//		AppConnector.startChrono();
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			Cursor cursor = ccpDatabase.rawQuery(REACTION_COMPONENTS, new String[] { Integer.valueOf(itemID).toString() });
//			//			int blueprintId = -1;
//			if (null != cursor) {
//				while (cursor.moveToNext()) {
//					// The the data of the resource. Check for blueprints.
//					int materialTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
//					int input = cursor.getInt(cursor.getColumnIndex("input"));
//					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
//					//					blueprintId = cursor.getInt(cursor.getColumnIndex("typeID"));
//					//					int consume = cursor.getInt(cursor.getColumnIndex("consume"));
//					if (input == 1) {
//						Resource resource = new Resource(materialTypeID, qty);
//						buildJob.add(resource);
//					}
//				}
//				cursor.close();
//				//				// We have collected the required blueprint. Add it to the list of resources.
//				//				if (blueprintId != -1) buildJob.add(new Resource(blueprintId, 1));
//				//
//				//				// Add the skills to the list of resources
//				//				cursor = ccpDatabase
//				//						.rawQuery(
//				//								"SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID",
//				//								new String[] { Integer.valueOf(itemID).toString() });
//				//				if (null == cursor) throw new Exception("E> Invalid cursor or empty.");
//				//				while (cursor.moveToNext()) {
//				//					// The the data of the resource. Check for blueprints.
//				//					int skillID = cursor.getInt(cursor.getColumnIndex("skillID"));
//				//					int level = cursor.getInt(cursor.getColumnIndex("level"));
//				//					Resource resource = new Resource(skillID, level);
//				//					buildJob.add(resource);
//				//				}
//				//				cursor.close();
//
//				//				Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//				//				return buildJob;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for material <" + itemID + "> not found.");
//		}
//		Log.i("DBQUERY.TIME",
//				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + buildJob.size() + "] " + AppConnector.timeLapse());
//		//		Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//		return buildJob;
//	}
//
//
//	public EveLocation searchLocationBySystem(final String name) {
//		final EveLocation newLocation = new EveLocation();
//		try {
//			final Cursor cursor = getCCPDatabase()
//					.rawQuery("SELECT solarSystemID from mapSolarSystems WHERE solarSystemName = ?", new String[] { name });
//			if (null != cursor) if (cursor.moveToFirst()) {
//				int locationID = cursor.getInt(cursor.getColumnIndex("solarSystemID"));
//				cursor.close();
//				return searchLocationbyID(locationID);
//			}
//		} catch (final Exception ex) {
//			logger.warning("W> Location <" + name + "> not found.");
//		}
//		return newLocation;
//	}
//
//	/**
//	 * Search for this data on the cache. <br>
//	 * The cache used for the search depends on the side parameter received on the call. All default prices are
//	 * references to the cost of the price to be spent to buy the item.<br>
//	 * If not found on the memory cache then try to load from the serialized version stored on disk. In case
//	 * this action also fails return an empty data structure with the minimum filed data information from the
//	 * related item and fire the update request.
//	 * 
//	 * @param itemID
//	 *          item id code of the item assigned to this market request.
//	 * @param side
//	 *          differentiates if we like to BUY or SELL the item.
//	 * @return the cached data or an empty locator ready to receive downloaded data.
//	 */
//	public MarketDataSet searchMarketData(final int itemID, final EMarketSide side) {
//		//		Log.i("EVEI","-- MarketUpdaterService.searchMarketDataByID. Searching for Market Data: " + itemID + " - " + side);
//		// Search on the cache. By default load the SELLER as If I am buying the item.
//		SparseArray<MarketDataSet> cache = sellMarketDataCache;
//		if (side == EMarketSide.BUYER) {
//			cache = buyMarketDataCache;
//		}
//		MarketDataSet entry = cache.get(itemID);
//		if (null == entry) {
//			// Try to get the data from disk.
//			entry = AppConnector.getStorageConnector().readDiskMarketData(itemID, side);
//			if (null == entry) {
//				// Neither on disk. Make a request for download and return a dummy placeholder.
//				entry = new MarketDataSet(itemID, side);
//				if (true) {
//					EVEDroidApp.getTheCacheConnector().addMarketDataRequest(itemID);
//				}
//			}
//			cache.put(itemID, entry);
//		} else {
//			Log.i("EVEI", "-- MarketUpdaterService.searchMarketDataByID. Cache hit on memory.");
//			// Check again the location. If is the default then request a new update and remove it from the cache.
//			long lid = entry.getBestMarket().getLocation().getID();
//			if (lid < 0) {
//				EVEDroidApp.getTheCacheConnector().addMarketDataRequest(itemID);
//				cache.put(itemID, null);
//			}
//		}
//		// Check entry timestamp before return. Post an update if old.
//		//		if (side.equalsIgnoreCase(ModelWideConstants.marketSide.CALCULATE))
//		//			return entry;
//		//		else {
//		if (AppConnector.checkExpiration(entry.getTS(), ModelWideConstants.HOURS2)) if (true) {
//			EVEDroidApp.getTheCacheConnector().addMarketDataRequest(itemID);
//		}
//		return entry;
//		//		}
//	}
//
//	public int searchModule4Blueprint(final int bpitemID) {
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			final Cursor cursor = ccpDatabase.rawQuery(
//					"SELECT productTypeID FROM industryActivityProducts BT WHERE typeID = ? AND activityID = 1",
//					new String[] { Integer.valueOf(bpitemID).toString() });
//			if (null != cursor) {
//				int productTypeID = -1;
//				while (cursor.moveToNext())
//					productTypeID = cursor.getInt(cursor.getColumnIndex("productTypeID"));
//				cursor.close();
//				return productTypeID;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error locating module for BPO <" + bpitemID + ">.");
//		}
//		return -1;
//	}
//
//	public int searchReactionOutputMultiplier(final int itemID) {
//		int multiplier = 200;
//		AppConnector.startChrono();
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			Cursor cursor = ccpDatabase.rawQuery(REACTION_COMPONENTS, new String[] { Integer.valueOf(itemID).toString() });
//			if (null != cursor) {
//				while (cursor.moveToNext()) {
//					// Search for the itemid at the reaction and then return the multiplier.
//					int materialTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
//					if (materialTypeID == itemID) {
//						multiplier = cursor.getInt(cursor.getColumnIndex("quantity"));
//						//					int input = cursor.getInt(cursor.getColumnIndex("input"));
//						//					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
//						//					//					blueprintId = cursor.getInt(cursor.getColumnIndex("typeID"));
//						//					//					int consume = cursor.getInt(cursor.getColumnIndex("consume"));
//						//					if (input == 1) {
//						//						Resource resource = new Resource(materialTypeID, qty);
//						//						buildJob.add(resource);
//						//					}
//					}
//				}
//				cursor.close();
//				//				// We have collected the required blueprint. Add it to the list of resources.
//				//				if (blueprintId != -1) buildJob.add(new Resource(blueprintId, 1));
//				//
//				//				// Add the skills to the list of resources
//				//				cursor = ccpDatabase
//				//						.rawQuery(
//				//								"SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID",
//				//								new String[] { Integer.valueOf(itemID).toString() });
//				//				if (null == cursor) throw new Exception("E> Invalid cursor or empty.");
//				//				while (cursor.moveToNext()) {
//				//					// The the data of the resource. Check for blueprints.
//				//					int skillID = cursor.getInt(cursor.getColumnIndex("skillID"));
//				//					int level = cursor.getInt(cursor.getColumnIndex("level"));
//				//					Resource resource = new Resource(skillID, level);
//				//					buildJob.add(resource);
//				//				}
//				//				cursor.close();
//
//				//				Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//				//				return buildJob;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for material <" + itemID + "> not found.");
//		}
//		Log.i("DBQUERY.TIME",
//				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + multiplier + "] " + AppConnector.timeLapse());
//		//		Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
//		return multiplier;
//	}
//
//	/**
//	 * Returns the resource identifier of the station class to locate icons or other type related resources.
//	 * 
//	 * @param stationID
//	 * @return
//	 */
//	public int searchStationType(final long stationID) {
//		ArrayList<Resource> inventionJob = new ArrayList<Resource>();
//		int stationTypeID = 1529;
//		AppConnector.startChrono();
//		try {
//			final Cursor cursor = getCCPDatabase().rawQuery(STATIONTYPE, new String[] { Long.valueOf(stationID).toString() });
//			if (null != cursor) {
//				while (cursor.moveToNext())
//					stationTypeID = cursor.getInt(cursor.getColumnIndex("stationTypeID"));
//				cursor.close();
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for station type <" + stationID + "> not found.");
//		}
//		//		Log.i("AndroidDatabaseConnector", "<< AndroidDatabaseConnector.searchListOfDatacores " + inventionJob.getSize());
//		Log.i("DBQUERY.TIME", "~~ Time lapse for [SELECT STATIONTYPEID " + stationID + "] " + AppConnector.timeLapse());
//		return stationTypeID;
//	}
//
//	public String searchTech4Blueprint(final int blueprintID) {
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			final Cursor cursor = ccpDatabase.rawQuery(TECH4BLUEPRINT,
//					new String[] { Integer.valueOf(blueprintID).toString() });
//			if (null != cursor) {
//				String productTypeID = ModelWideConstants.eveglobal.TechI;
//				while (cursor.moveToNext())
//					productTypeID = cursor.getString(3);
//				cursor.close();
//				return productTypeID;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error locating BPO for module <" + blueprintID + ">.");
//		}
//		return ModelWideConstants.eveglobal.TechI;
//	}
//
//	private boolean checkRecordExistence(final int typeID, final String query) {
//		int count = 0;
//		try {
//			final Cursor cursor = getCCPDatabase().rawQuery(query, new String[] { Integer.valueOf(typeID).toString() });
//			if (null != cursor) {
//				if (cursor.moveToFirst()) count = cursor.getInt(0);
//				cursor.close();
//			}
//		} catch (final Exception ex) {
//			Log.w("EVEI", "W> AndroidDatabaseConnector.checkRecordExistence -- Exception:" + ex.getMessage());
//			return false;
//		}
//		if (count > 0)
//			return true;
//		else
//			return false;
//	}
//
//	private SQLiteDatabase getAppDatabase() {
//		if (null == staticDatabase) {
//			openAppDataBase();
//		}
//		return staticDatabase;
//	}
//
//	private SQLiteDatabase getCCPDatabase() {
//		if (null == ccpDatabase) {
//			openCCPDataBase();
//		}
//		return ccpDatabase;
//	}
//

//[03]
//	public int queryBlueprintDependencies(final int bpitemID) {
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			final Cursor cursor = ccpDatabase.rawQuery(
//					"SELECT parentBlueprintTypeID FROM invBlueprintTypes BT WHERE blueprintTypeID = ?",
//					new String[] { Integer.valueOf(bpitemID).toString() });
//			if (null != cursor) {
//				int parentBlueprintTypeID = -1;
//				while (cursor.moveToNext())
//					parentBlueprintTypeID = cursor.getInt(cursor.getColumnIndex("parentBlueprintTypeID"));
//				cursor.close();
//				return parentBlueprintTypeID;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error getting T2 BPO for BP <" + bpitemID + ">.");
//		}
//		return -1;
//	}
//
//	public ArrayList<Resource> refineOre(final int oreID) {
//		ArrayList<Resource> results = new ArrayList<Resource>();
//		try {
//			final Cursor cursor = getCCPDatabase().rawQuery(REFINING_ASTEROID,
//					new String[] { Integer.valueOf(oreID).toString() });
//			if (null != cursor) {
//				while (cursor.moveToNext()) {
//					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
//					int qty = cursor.getInt(cursor.getColumnIndex("qty"));
//					int portionSize = cursor.getInt(cursor.getColumnIndex("portionSize"));
//					Resource resource = new Resource(resourceID, qty);
//					resource.setStackSize(portionSize);
//					results.add(resource);
//				}
//				cursor.close();
//				return results;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error refining <" + oreID + "> not found.");
//		}
//		return results;
//	}
//
//	/**
//	 * Changes the owner id for all records from a new download with the id of the current character. This
//	 * completes the download and the assignment of the resources to the character without interrupting the
//	 * processing of data by the application.
//	 */
//	public synchronized void replaceAssets(final long characterID) {
//		SQLiteDatabase database = null;
//		try {
//			database = getAppDatabase();
//			synchronized (database) {
//				database.beginTransaction();
//				Log.i("", "-- replaceAssets rows deleted ASSETS [OWNERID = " + characterID + "] - "
//						+ database.delete("Assets", "ownerID" + "=" + characterID, null));
//				ContentValues values = new ContentValues();
//				values.put("ownerID", characterID);
//				database.update("Assets", values, "ownerID=-1", null);
//				database.setTransactionSuccessful();
//			}
//		} catch (final SQLiteException ex) {
//			ex.printStackTrace();
//			logger.warning("W> Problem replacing assets for " + characterID + ". " + ex.getMessage());
//		} finally {
//			if (null != database) {
//				database.endTransaction();
//			}
//		}
//	}
//
//	public synchronized void replaceBlueprints(final long characterID) {
//		SQLiteDatabase database = null;
//		try {
//			database = getAppDatabase();
//			synchronized (database) {
//				database.beginTransaction();
//				Log.i("", "-- replaceBlueprints rows deleted BLUEPRINTS [OWNERID = " + characterID + "] - "
//						+ database.delete("Blueprints", "ownerID" + "=" + characterID, null));
//				ContentValues values = new ContentValues();
//				values.put("ownerID", characterID);
//				database.update("Blueprints", values, "ownerID" + "=-1", null);
//				database.setTransactionSuccessful();
//			}
//		} catch (final SQLiteException ex) {
//			ex.printStackTrace();
//			logger.warning("W> Problem replacing Blueprints for " + characterID + ". " + ex.getMessage());
//		} finally {
//			if (null != database) {
//				database.endTransaction();
//			}
//		}
//	}
//
//	public synchronized void replaceJobs(final long characterID) {
//		SQLiteDatabase database = null;
//		try {
//			database = getAppDatabase();
//			synchronized (database) {
//				database.beginTransaction();
//				Log.i("", "-- replaceJobs rows deleted JOBS [OWNERID = " + characterID + "] - "
//						+ database.delete("Jobs", "ownerID" + "=" + characterID + " AND jobType='CCP'", null));
//				ContentValues values = new ContentValues();
//				values.put("ownerID", characterID);
//				database.update("Jobs", values, "ownerID=-1", null);
//				database.setTransactionSuccessful();
//			}
//		} catch (final SQLiteException ex) {
//			ex.printStackTrace();
//			logger.warning("W> Problem replacing assets for " + characterID + ". " + ex.getMessage());
//		} finally {
//			if (null != database) {
//				database.endTransaction();
//			}
//		}
//	}
//
//	public ArrayList<Asset> searchAsset4Type(final long characterID, final int typeID) {
//		//	Select assets for the owner and with an specific type id.
//		List<Asset> assetList = new ArrayList<Asset>();
//		try {
//			Dao<Asset, String> assetDao = getAssetDAO();
//			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
//			Where<Asset, String> where = queryBuilder.where();
//			where.eq("ownerID", characterID);
//			where.and();
//			where.eq("typeID", typeID);
//			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
//			assetList = assetDao.query(preparedQuery);
//		} catch (java.sql.SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return (ArrayList<Asset>) assetList;
//	}
//
//	public Asset searchAssetByID(final long assetID) {
//		// search for the asset on the cache. Usually searching for containers.
//		Asset hit = containerCache.get(assetID);
//		if (null == hit) {
//			//	Select assets for the owner and with an specific type id.
//			List<Asset> assetList = new ArrayList<Asset>();
//			try {
//				Dao<Asset, String> assetDao = getAssetDAO();
//				QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
//				Where<Asset, String> where = queryBuilder.where();
//				where.eq("assetID", assetID);
//				PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
//				assetList = assetDao.query(preparedQuery);
//				if (assetList.size() > 0) {
//					hit = assetList.get(0);
//					containerCache.put(hit.getAssetID(), hit);
//				}
//			} catch (java.sql.SQLException sqle) {
//				sqle.printStackTrace();
//			}
//		}
//		return hit;
//	}
//
//	/**
//	 * Returns the blueprint id that matched this module from the <code>invBlueprintTypes</code> table.
//	 */
//	public int searchBlueprint4Module(final int moduleID) {
//		try {
//			if (null == ccpDatabase) {
//				ccpDatabase = getCCPDatabase();
//			}
//			final Cursor cursor = ccpDatabase.rawQuery(
//					"SELECT typeID FROM industryActivityProducts BT WHERE productTypeID = ? AND activityID = 1",
//					new String[] { Integer.valueOf(moduleID).toString() });
//			if (null != cursor) {
//				int productTypeID = -1;
//				while (cursor.moveToNext())
//					productTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
//				cursor.close();
//				return productTypeID;
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error locating BPO for module <" + moduleID + ">.");
//		}
//		return -1;
//	}
//
//	public ArrayList<Integer> searchInventionableBlueprints(final String idList) {
//		Log.i("DBQUERY", ">> AndroidDatabaseConnector.searchInventionableBlueprints");
//		ArrayList<Integer> blueprintIds = new ArrayList<Integer>();
//		AppConnector.startChrono();
//		try {
//			//		if (null == ccpDatabase) ccpDatabase = getCCPDatabase();
//			final Cursor cursor = getCCPDatabase().rawQuery(
//					"SELECT typeID FROM industryActivityProducts WHERE productTypeID in ( " + idList + " ) AND activityID = 8",
//					null);
//			if (null != cursor) {
//				while (cursor.moveToNext())
//					blueprintIds.add(new Integer(cursor.getInt(cursor.getColumnIndex("typeID"))));
//				// We have collected the required blueprint. Add it to the list of resources.
//				cursor.close();
//				Log.i("DBQUERY.TIME",
//						"~~ Time lapse for [SELECT PRODUCTTYPEID IN " + idList + "] - " + AppConnector.timeLapse());
//			}
//		} catch (final Exception ex) {
//			logger.severe("E> Error searching for  blueprints.");
//		}
//		Log.i("DBQUERY", "<< AndroidDatabaseConnector.searchInventionableBlueprints " + blueprintIds.size());
//		return blueprintIds;
//	}
//
//	public int searchInventionProduct(final int typeID) {
//		int product = -1;
//		try {
//			final Cursor cursor = getCCPDatabase().rawQuery(INVENTION_PRODUCT,
//					new String[] { Integer.valueOf(typeID).toString() });
//			if (null != cursor) {
//				if (cursor.moveToFirst()) product = cursor.getInt(0);
//				cursor.close();
//			}
//		} catch (final Exception ex) {
//			Log.w("EVEI", "W> AndroidDatabaseConnector.checkInvention -- Exception:" + ex.getMessage());
//		}
//		return product;
//	}

//	private EveLocation searchOutpostbyID(final long locationID) {
//		// Check if the outpotst already loaded.
//		if ((null == outpostsCache) || (outpostsCache.size() < 1)) {
//			// Making a request to url and getting response
//			String jsonStr = readJsonData();
//			try {
//				JSONObject jsonObj = new JSONObject(jsonStr);
//				// Getting JSON Array node
//				JSONArray outposts = jsonObj.getJSONArray("items");
//				// Looping through all outposts
//				int counter = 1;
//				for (int i = 0; i < outposts.length(); i++) {
//					Outpost o = new Outpost();
//					JSONObject post = outposts.getJSONObject(i);
//					int id = post.getInt("facilityID");
//					o.setFacilityID(id);
//					JSONObject intermediate = post.getJSONObject("solarSystem");
//					o.setSolarSystem(intermediate.getLong("id"));
//					o.setName(post.getString("name"));
//					intermediate = post.getJSONObject("region");
//					o.setRegion(intermediate.getLong("id"));
//					intermediate = post.getJSONObject("owner");
//					o.setOwner(intermediate.getLong("id"));
//					intermediate = post.getJSONObject("type");
//					o.setType(intermediate.getLong("id"));
//
//					// Create the part with the Outpost
//					outpostsCache.put(id, o);
//					logger.info(".. Part counter " + counter++);
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		// Search for the item
//		Outpost hit = outpostsCache.get(Long.valueOf(locationID).intValue());
//		EveLocation location = new EveLocation(locationID);
//		if (null != hit) {
//			EveLocation systemLocation = searchLocationbyID(hit.getSolarSystem());
//			location.setStation(hit.getName());
//			location.setSystemID(hit.getSolarSystem());
//			location.setSystem(systemLocation.getSystem());
//			location.setConstellationID(systemLocation.getConstellationID());
//			location.setConstellation(systemLocation.getConstellation());
//			location.setRegionID(systemLocation.getRegionID());
//			location.setRegion(systemLocation.getRegion());
//			location.setSecurity(systemLocation.getSecurity());
//		}
//		return location;
//	}
//	private void doMain(final String[] args) throws Exception {
//		ConnectionSource connectionSource = null;
//		try {
//			// create our data-source for the database
//			connectionSource = new JdbcConnectionSource(DATABASE_URL);
//			// setup our database and DAOs
//			setupDatabase(connectionSource);
//			// read and write some data
//			readWriteData();
//			// do a bunch of bulk operations
//			readWriteBunch();
//			// show how to use the SelectArg object
//			useSelectArgFeature();
//			// show how to use the SelectArg object
//			useTransactions(connectionSource);
//			System.out.println("\n\nIt seems to have worked\n\n");
//		} finally {
//			// destroy the data source which should close underlying connections
//			if (connectionSource != null) {
//				connectionSource.close();
//			}
//		}
//	}
