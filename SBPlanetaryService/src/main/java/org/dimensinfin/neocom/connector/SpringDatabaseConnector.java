//	PROJECT:        NeoCom (NEOC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.
package org.dimensinfin.neocom.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.ICCPDatabaseConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.Outpost;

// - CLASS IMPLEMENTATION ...................................................................................
public class SpringDatabaseConnector implements ICCPDatabaseConnector {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger							= Logger.getLogger("SpringDatabaseConnector");

	//private static final String							DATABASE_URL							= "jdbc:sqlite:D:\\Development\\WorkStage\\ProjectsAngular\\NeoCom\\src\\main\\resources\\eve.db";
	//private static final String							DATABASE_URL							= "jdbc:sqlite:D:\\Development\\ProjectsAngular\\NeoCom\\src\\main\\resources\\eve.db";
	private static final String							DATABASE_URL				= "jdbc:sqlite:./src/main/resources/eve.db";
	private static final String							SELECT_ITEM_BYID		= "SELECT it.typeID AS typeID, it.typeName AS typeName"
			+ " , ig.groupName AS groupName" + " , ic.categoryName AS categoryName" + " , it.basePrice AS basePrice"
			+ " , it.volume AS volume" + " , IFNULL(img.metaGroupName, " + '"' + "NOTECH" + '"' + ") AS Tech"
			+ " FROM invTypes it" + " LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID"
			+ " LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID"
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeID = ?";

	private static final String							SELECT_LOCATIONBYID	= "SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";

	//	private static final String							LOM4BLUEPRINT							= "SELECT iam.typeID, itb.typeName, iam.materialTypeID, it.typeName, ig.groupName, ic.categoryName, iam.quantity, iam.consume"
	//			+ " FROM industryActivityMaterials iam, invTypes itb, invTypes it, invGroups ig, invCategories ic"
	//			+ " WHERE iam.typeID = ?" + " AND iam.activityID = 1" + " AND itb.typeID = iam.typeID"
	//			+ " AND it.typeID = iam.materialTypeID" + " AND ig.groupID = it.groupID" + " AND ic.categoryID = ig.categoryID";
	//
	//	private static final String							TECH4BLUEPRINT						= "SELECT iap.typeID, it.typeName, imt.metaGroupID, img.metaGroupName"
	//			+ " FROM industryActivityProducts iap, invTypes it, invMetaTypes imt, invMetaGroups img" + " WHERE it.typeID =?"
	//			+ " AND iap.typeID = it.typeID" + " AND imt.typeID = productTypeID" + " AND img.metaGroupID = imt.metaGroupID"
	//			+ " AND iap.activityID = 1";
	//
	//	private static final String							REFINING_ASTEROID					= "SELECT itm.materialTypeID AS materialTypeID, itm.quantity AS qty"
	//			+ " , it.typeName AS materialName" + " , ito.portionSize AS portionSize"
	//			+ " FROM invTypeMaterials itm, invTypes it, invTypes ito" + " WHERE itm.typeID = ?"
	//			+ " AND it.typeID = itm.materialTypeID" + " AND ito.typeID = itm.typeID" + " ORDER BY itm.materialTypeID";
	//
	//	private static final String							INDUSTRYACTIVITYMATERIALS	= "SELECT materialTypeID, quantity, consume FROM industryActivityMaterials WHERE typeID = ? AND activityID = 8";
	//	private static final String							STATIONTYPE								= "SELECT stationTypeID FROM staStations WHERE stationID = ?";
	//	private static final String							JOB_COMPLETION_TIME				= "SELECT typeID, time FROM industryActivity WHERE typeID = ? AND activityID = ?";
	//	private static final String							CHECK_INVENTION						= "SELECT count(*) AS counter"
	//			+ " FROM industryActivityProducts iap" + " WHERE iap.typeID = ?" + " AND iap.activityID = 8";
	//	private static final String							INVENTION_PRODUCT					= "SELECT productTypeID FROM industryActivityProducts WHERE typeID = ? AND activityID = 8";
	//	private static final String							CHECK_MANUFACTURABLE			= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	//	private static final String							CHECK_REACTIONABLE				= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	//	private static final String							CHECK_PLANETARYPRODUCED		= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	//	private static final String							REACTION_COMPONENTS				= "SELECT" + "   invTypeReactions.reactionTypeID"
	//			+ " , invTypes.typeID, invTypes.typeName" + " , invTypeReactions.input"
	//			+ " , COALESCE(dgmTypeAttributes.valueInt, dgmTypeAttributes.valueFloat) * invTypeReactions.quantity AS quantity"
	//			+ " FROM invTypeReactions, dgmTypeAttributes, invTypes" + " WHERE"
	//			+ " invTypes.typeId = invTypeReactions.typeID AND" + " invTypeReactions.reactionTypeID IN ("
	//			+ "    SELECT reactionTypeID" + "    FROM invTypeReactions" + "    WHERE typeID = ? ) AND"
	//			+ " dgmTypeAttributes.typeID = invTypeReactions.typeID";

	// - F I E L D - S E C T I O N ............................................................................
	//	private Context														_context									= null;
	//	private SQLiteDatabase										staticDatabase						= null;
	private Connection											ccpDatabase					= null;
	//	private EveDroidDBHelper									appDatabaseHelper					= null;
	private final HashMap<Integer, EveItem>	itemCache						= new HashMap<Integer, EveItem>();
	//	private final SparseArray<MarketDataSet>	buyMarketDataCache				= new SparseArray<MarketDataSet>();
	//	private final SparseArray<MarketDataSet>	sellMarketDataCache				= new SparseArray<MarketDataSet>();
	private final HashMap<Integer, Outpost>	outpostsCache				= new HashMap<Integer, Outpost>();
	//	private final HashMap<Long, Asset>				containerCache						= new HashMap<Long, Asset>();;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean openCCPDataBase() {
		if (null == ccpDatabase) {
			//		Connection c = null;
			try {
				Class.forName("org.sqlite.JDBC");
				ccpDatabase = DriverManager.getConnection(DATABASE_URL);
				ccpDatabase.setAutoCommit(false);
			} catch (Exception sqle) {
				logger.warning(sqle.getClass().getName() + ": " + sqle.getMessage());
			}
			logger.info(" --[-openCCPDataBase]> Opened CCP database successfully");
		}
		return true;
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
				prepStmt = getCCPDatabase().prepareStatement(SELECT_ITEM_BYID);
				prepStmt.setString(1, Integer.valueOf(typeID).toString());
				cursor = prepStmt.executeQuery();
				while (cursor.next()) {
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

	//	public EveLocation searchLocationbyID(final long locationID) {
	//		EveLocation hit = new EveLocation(locationID);
	//		PreparedStatement prepStmt = null;
	//		ResultSet cursor = null;
	//		try {
	//			prepStmt = getCCPDatabase().prepareStatement(SELECT_LOCATIONBYID);
	//			prepStmt.setString(1, Long.valueOf(locationID).toString());
	//			cursor = prepStmt.executeQuery();
	//			boolean detected = false;
	//			while (cursor.next()) {
	//				//				if (cursor.moveToFirst()) {
	//				detected = true;
	//				// Check returned values when doing the assignments.
	//				long fragmentID = cursor.getLong(5);
	//				if (fragmentID > 0) {
	//					hit.setSystemID(fragmentID);
	//					hit.setSystem(cursor.getString(6));
	//				} else {
	//					hit.setSystem(cursor.getString(3));
	//				}
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
	//				detected = true;
	//			}
	//			if (!detected) // Search the location on the list of outposts.
	//				hit = searchOutpostbyID(locationID);
	//			//	}
	//		} catch (final Exception ex) {
	//			logger.warning("Location <" + locationID + "> not found.");
	//		}
	//		return hit;
	//	}
	private Connection getCCPDatabase() {
		if (null == ccpDatabase) openCCPDataBase();
		return ccpDatabase;
	}
}

// - UNUSED CODE ............................................................................................
