//	PROJECT:      Neocom.MarketDataService (NEOC-MKDS)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	SpringBoot-MS-Java 1.8.
//	DESCRIPTION:	This project contains a MicroService specially dedicated to get and store the Market Data
//								information. I should be exposed on a new port and should be accesible to all the backend MS to consult 
//								Item Market Data.
package org.dimensinfin.eveonline.neocom.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................
public class CCPDatabaseConnector implements ICCPDatabaseConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger									= Logger.getLogger("CCPDatabaseConnector.java");
	private static final String							CCPDATABASE_URL					= "jdbc:sqlite:src/main/resources/eve.db";

	// - F I E L D   I N D E X   D E F I N I T I O N S
	private static int											STATIONTYPEID_COLINDEX	= 1;

	// - S Q L   C O M M A N D S
	private static final String							SELECT_ITEM_BYID				= "SELECT it.typeID AS typeID, it.typeName AS typeName"
			+ " , ig.groupName AS groupName" + " , ic.categoryName AS categoryName" + " , it.basePrice AS basePrice"
			+ " , it.volume AS volume" + " , IFNULL(img.metaGroupName, " + '"' + "NOTECH" + '"' + ") AS Tech"
			+ " FROM invTypes it" + " LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID"
			+ " LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID"
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeID = ?";
	private static final String							SELECT_LOCATIONBYID			= "SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";
	private static final String							SELECT_LOCATIONBYSYSTEM	= "SELECT solarSystemID from mapSolarSystems WHERE solarSystemName = ?";
	private static final String							STATIONTYPE							= "SELECT stationTypeID FROM staStations WHERE stationID = ?";

	// - F I E L D - S E C T I O N ............................................................................
	private Connection											ccpDatabase							= null;
	private final HashMap<Integer, EveItem>	itemCache								= new HashMap<Integer, EveItem>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CCPDatabaseConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean openCCPDataBase() {
		if (null == ccpDatabase) {
			try {
				Class.forName("org.sqlite.JDBC");
				ccpDatabase = DriverManager.getConnection(CCPDATABASE_URL);
				ccpDatabase.setAutoCommit(false);
			} catch (Exception sqle) {
				logger.warning(sqle.getClass().getName() + ": " + sqle.getMessage());
			}
			logger.info("-- [StringDatabaseConnector.openCCPDataBase]> Opened CCP database successfully.");
		}
		return true;
	}

	/**
	 * Search on the eve.db database for the attributes that describe an Item. Items are the lowest data
	 * structure for EVE resources or modules. Everything on Eve is an Item. We detect blueprints that require a
	 * different treatment and also we check for the availability of the item at the current cache if
	 * implemented.
	 */
	public synchronized EveItem searchItembyID(final int typeID) {
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
	 * Returns the resource identifier of the station class to locate icons or other type related resources.
	 * 
	 * @param stationID
	 * @return
	 */
	public int searchStationType(final long stationID) {
		int stationTypeID = 1529;
		AppConnector.startChrono();
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = getCCPDatabase().prepareStatement(STATIONTYPE);
			prepStmt.setString(1, Long.valueOf(stationID).toString());
			cursor = prepStmt.executeQuery();
			while (cursor.next()) {
				stationTypeID = cursor.getInt(STATIONTYPEID_COLINDEX);
			}
		} catch (Exception ex) {
			logger.warning("W- [SpingDatabaseConnector.searchStationType]> Database exception: " + ex.getMessage());
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
		//	logger.info("~~ Time lapse for [SELECT STATIONTYPEID " + stationID + "] " + AppConnector.timeLapse());
		return stationTypeID;
	}

	private Connection getCCPDatabase() {
		if (null == ccpDatabase) openCCPDataBase();
		return ccpDatabase;
	}

}

// - UNUSED CODE ............................................................................................
