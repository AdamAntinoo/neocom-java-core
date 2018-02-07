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
package org.dimensinfin.eveonline.neocom.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class SDEDatabaseManager {
	public abstract static class RawStatement {

//		public abstract int getCount ();
//
//		public abstract int getPosition ();

		public abstract boolean moveToFirst();

		public abstract boolean moveToLast();

		public abstract boolean moveToNext();

		public abstract boolean isFirst();

		public abstract boolean isLast();
//		public abstract int getColumnIndex (final String s);

		public abstract String getString( final int i );

		public abstract short getShort( final int i );

		public abstract int getInt( final int i );

		public abstract long getLong( final int i );

		public abstract float getFloat( final int i );

		public abstract double getDouble( final int i );

		//	public abstract int getType (final int i);

		public abstract void close();
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(SDEDatabaseManager.class);

	// --- S Q L   S T A T E M E N T S   S E C T I O N
	// - I T E M B Y I D
	private static int ITEM_BYID_TYPEID_COLINDEX = 1;
	private static final String SELECT_ITEM_BYID = "SELECT it.typeID AS typeID"
			+ " , it.typeName AS typeName"
			+ " , ig.groupID AS groupID"
			+ " , ig.groupName AS groupName"
			+ " , ic.categoryID AS categoryID"
			+ " , ic.categoryName AS categoryName"
			+ " , it.basePrice AS basePrice"
			+ " , it.volume AS volume"
			+ " , IFNULL(img.metaGroupName, " + '"' + "NOTECH" + '"' + ") AS Tech"
			+ " FROM invTypes it" + " LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID"
			+ " LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID"
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeID = ?";

	// - L O C A T I O N B Y I D
	private static int LOCATIONBYID_SYSTEMID_COLINDEX = 5;
	private static int LOCATIONBYID_SYSTEM_COLINDEX = 6;
	private static int LOCATIONBYID_LOCATIONNAME_COLINDEX = 3;
	private static int LOCATIONBYID_CONSTELLATIONID_COLINDEX = 7;
	private static int LOCATIONBYID_CONSTELLATION_COLINDEX = 8;
	private static int LOCATIONBYID_REGIONID_COLINDEX = 9;
	private static int LOCATIONBYID_REGION_COLINDEX = 10;
	private static int LOCATIONBYID_TYPEID_COLINDEX = 2;
	private static int LOCATIONBYID_LOCATIONID_COLINDEX = 1;
	private static int LOCATIONBYID_SECURITY_COLINDEX = 4;
	private static final String SELECT_LOCATIONBYID = "SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";

	// - L O C A T I O N B Y S Y S T E M
	private static int LOCATIONBYSYSTEM_SOLARSYSTEMID_COLINDEX = 1;
	private static final String SELECT_LOCATIONBYSYSTEM = "SELECT solarSystemID FROM mapSolarSystems"
			+ " WHERE solarSystemName = ?";

	// - S T A T I O N T Y P E
	private static int STATIONTYPEID_COLINDEX = 1;
	private static final String SELECT_STATIONTYPE = "SELECT stationTypeID FROM staStations WHERE stationID = ?";

	// - I T E M G R O U P 4 I D
	private static int ITEMGROUP_GROUPID_COLINDEX = 1;
	private static int ITEMGROUP_CATEGORYID_COLINDEX = 2;
	private static int ITEMGROUP_GROUPNAME_COLINDEX = 3;
	private static int ITEMGROUP_ICONLINKNAME_COLINDEX = 4;
	private static final String SELECT_ITEMGROUP = "SELECT ig.groupID AS groupID"
			+ " , ig.categoryID AS categoryID"
			+ " , ig.groupName AS groupName"
			+ " , ei.iconFile AS iconLinkName"
			+ " FROM invGroups ig"
			+ " LEFT OUTER JOIN eveIcons ei ON ig.iconID = ei.iconID"
			+ " WHERE ig.groupID = ?";

	// - I T E M C A T E G O R Y 4 I D
	private static int ITEMCATEGORY_CATEGORYID_COLINDEX = 1;
	private static int ITEMCATEGORY_CATEGORYNAME_COLINDEX = 2;
	private static int ITEMCATEGORY_ICONLINKNAME_COLINDEX = 3;
	private static final String SELECT_ITEMCATEGORY = "SELECT ic.categoryID AS categoryID"
			+ " , ic.categoryName AS categoryName"
			+ " , ei.iconFile AS iconLinkName"
			+ " FROM invCategories ic"
			+ " LEFT OUTER JOIN eveIcons ei ON ic.iconID = ei.iconID"
			+ " WHERE ic.categoryID = ?";

	// --- S C H E M A T I C S I D
	private static int SCHEMATICSID_SCHEMATICID_COLINDEX = 1;
	private static int SCHEMATICSID_TYPEID_COLINDEX = 2;
	private static int SCHEMATICSID_QUANTITY_COLINDEX = 3;
	private static int SCHEMATICSID_ISINPUT_COLINDEX = 4;
	private static final String SELECT_SCHEMATICSID =
			"SELECT        pstms.schematicID, pstms.typeID, pstms.quantity, pstms.isInput"
					+ " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms"
					+ " WHERE  pstmt.schematicID = ?"
					+ " AND    pstmt.isInput = 0"
					+ " AND    pstms.schematicID = pstmt.schematicID";


	// - F I E L D   I N D E X   D E F I N I T I O N S
	private static int MODULE4BLUEPRINT_PRODUCTTYPEID_COLINDEX = 1;
	private static int TECH4BLUEPRINT_METAGROUPID_COLINDEX = 3;
	private static int REFINEORE_MATERIALTYPEID_COLINDEX = 1;
	private static int REFINEORE_QUANTITY_COLINDEX = 2;
	private static int REFINEORE_PORTIONSIZE_COLINDEX = 4;
	private static int BLUEPRINT4MODULE_TYPEID_COLINDEX = 1;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	protected abstract RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;

	// - S Q L   A C C E S S   M E T H O D S

	/**
	 * Search on the sde.sqlite database for the attributes that describe an Item. Items are the lowest data
	 * structure for EVE resources or modules. Everything on Eve is an Item. We detect blueprints that require a
	 * different treatment and also we check for the availability of the item at the current cache if
	 * implemented.
	 */
	public synchronized EveItem searchItem4Id( final int typeID ) {
		logger.info(">> [SDEDatabaseManager.searchItem4Id]");
//		// Search the item on the cache.
//		EveItem hit = itemCache.get(typeID);
//		if (null == hit) {
//			logger.info("-- [SDEDatabaseManager.searchItembyID]> Item not found at cache.");
		final EveItem hit = new EveItem();
		try {
			final RawStatement cursor = constructStatement(SELECT_ITEM_BYID, new String[]{Integer.valueOf(typeID).toString()});
			boolean found = false;
			while (cursor.moveToNext()) {
				found = true;
				hit.setTypeID(cursor.getInt(1));
				hit.setName(cursor.getString(2));
//				hit.setGroupName(cursor.getString(3));
//				hit.setCategory(cursor.getString(4));
				hit.setBasePrice(cursor.getDouble(5));
				hit.setVolume(cursor.getDouble(6));
				// Process the Tech field. The query marks blueprints
				String tech = cursor.getString(7);
				if (tech.equalsIgnoreCase("NOTECH")) {
					// Update the Tech value when item is a Blueprint.
					hit.setTech(ModelWideConstants.eveglobal.TechI);
					if (hit.getName().contains(" II Blueprint")) {
//						hit.setBlueprint(true);
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
			cursor.close();
			if (!found) {
				logger.warn("W> [SDEDatabaseManager.searchItem4Id]> Item <{}> not found.", typeID);
			}
		} catch (SQLException sqle) {
			logger.error("E> [SDEDatabaseManager.searchItem4Id]> Exception while processing query. {}", sqle.getMessage());
		}
		//[01]
//			}
		return hit;
//		}
	}

	/**
	 * Search on the SDE Database or on the Application database for a new location ID that is not already on
	 * the cache. Locations are extended objects on the NeoCom model because to the standard and game defined
	 * locations we should add an external resource with the compilation of user deployed structures (Citadels,
	 * Refineries, etc) that can also store assets and that are becoming the real place where to have the items.
	 * We also have to include another external source that are the Outposts than come fom CCP sources but that
	 * are being slowly phased out.<br>
	 * The process starts searching for locations depending on range, first at the CCP database and then at the
	 * Locations table at the application database.
	 */
	public EveLocation searchLocation4Id( final long locationID ) {
		logger.info(">< [SDEDatabaseManager.searchLocation4Id]> Searching ID: " + locationID);
		// First check if the location is already on the cache table.
//		EveLocation hit = locationsCache.get(locationID);
//		if (null != hit) {
//			int access = CCPDatabaseConnector.locationsCacheStatistics.accountAccess(true);
//			int hits = CCPDatabaseConnector.locationsCacheStatistics.getHits();
//			CCPDatabaseConnector.logger.info(">< [CCPDatabaseConnector.searchLocationbyID]> [HIT-" + hits + "/" + access
//					+ "] Location " + locationID + " found at cache.");
//			return hit;
//		} else {
		// Try to get that id from the cache tables
//			int access = CCPDatabaseConnector.locationsCacheStatistics.accountAccess(false);
		List<EveLocation> locationList = null;
		try {
			locationList = GlobalDataManager.getNeocomDBHelper()
					.getLocationDao().queryForEq("id", locationID);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return new EveLocation(locationID);
		}

		// Check list contents. If found we have the location. Else then check if Office
		if (locationList.size() < 1) {
			//				CCPDatabaseConnector.logger.info(
			//						"-- [CCPDatabaseConnector.searchLocationbyID]> Location: " + locationID + " not found on local Database.");
			// Offices
			long fixedLocationID = locationID;
			//				if (fixedLocationID >= 66000000) {
			//					if (fixedLocationID < 66014933) {
			//						fixedLocationID = fixedLocationID - 6000001;
			//					} else {
			//						fixedLocationID = fixedLocationID - 6000000;
			//					}
			//				}
			final EveLocation hit = new EveLocation(fixedLocationID);
//				ResultSet cursor = null;
			try {
				final RawStatement cursor = constructStatement(SELECT_LOCATIONBYID, new String[]{Long.valueOf(fixedLocationID).toString
						()});
//					PreparedStatement prepStmt = this.getCCPDatabase().prepareStatement(CCPDatabaseConnector.SELECT_LOCATIONBYID);
//					prepStmt.setString(1, Long.valueOf(fixedLocationID).toString());
//					cursor = prepStmt.executeQuery();
//					if (null != cursor) {
				boolean detected = false;
				while (cursor.moveToNext()) {
					detected = true;
					//							CCPDatabaseConnector.logger.info(
					//									"-- [CCPDatabaseConnector.searchLocationbyID]> Location: " + locationID + " Obtained from CCP data.");
					// Check returned values when doing the assignments.
					long fragmentID = cursor.getLong(LOCATIONBYID_SYSTEMID_COLINDEX);
					if (fragmentID > 0) {
						hit.setSystemID(fragmentID);
						hit.setSystem(cursor.getString(LOCATIONBYID_SYSTEM_COLINDEX));
					} else {
						hit.setSystem(cursor.getString(LOCATIONBYID_LOCATIONNAME_COLINDEX));
					}
					fragmentID = cursor.getLong(LOCATIONBYID_CONSTELLATIONID_COLINDEX);
					if (fragmentID > 0) {
						hit.setConstellationID(fragmentID);
						hit.setConstellation(cursor.getString(LOCATIONBYID_CONSTELLATION_COLINDEX));
					}
					fragmentID = cursor.getLong(LOCATIONBYID_REGIONID_COLINDEX);
					if (fragmentID > 0) {
						hit.setRegionID(fragmentID);
						hit.setRegion(cursor.getString(LOCATIONBYID_REGION_COLINDEX));
					}
					hit.setTypeID(ELocationType.CCPLOCATION);
					hit.setStation(cursor.getString(LOCATIONBYID_LOCATIONNAME_COLINDEX));
					hit.setLocationID(cursor.getLong(LOCATIONBYID_LOCATIONID_COLINDEX));
					hit.setSecurity(cursor.getString(LOCATIONBYID_SECURITY_COLINDEX));
					// Update the final ID
					hit.getID();

					// Location found on CCP database.
					//				int hits = CCPDatabaseConnector.locationsCacheStatistics.getHits();
//							logger.info(">< [CCPDatabaseConnector.searchLocationbyID]> [HIT-" + hits + "/"
//									+ access + "] Location " + locationID + " found at CCP Database.");
					//						locationsCache.put(hit.getID(), hit);
				}
				cursor.close();
				if (!detected) {
					logger.info("-- [searchLocation4Id]> Location: " + locationID + " not found on any Database - UNKNOWN-.");
					hit.setSystem("ID>" + Long.valueOf(locationID).toString());
				}
			} catch (SQLException sqle) {
				logger.error("E> [SDEDatabaseManager.searchLocation4Id]> Exception processing search for Location {}. {}",
						locationID, sqle.getMessage());
				logger.warn("W- [SDEDatabaseManager.searchLocation4Id]> Location <" + fixedLocationID + "> not found.");
			}
//				} catch (final Exception ex) {
///				} finally {
//					try {
//					}
			// If the location is not cached nor in the CCP database. Return default location
			return hit;
		} else {
			// Location found on the Application database.
//				int hits = CCPDatabaseConnector.locationsCacheStatistics.getHits();
//				CCPDatabaseConnector.logger.info(">< [CCPDatabaseConnector.searchLocationbyID]> [HIT-" + hits + "/" + access
//						+ "] Location " + locationID + " found at Application Database.");
			EveLocation foundLoc = locationList.get(0);
//				locationsCache.put(foundLoc.getID(), foundLoc);
			return locationList.get(0);
		}
	}

	/**
	 * Search on the sde.sqlite database for the item group information. This new select is used to get access to
	 * the icon information that should be stored to be correlated to the resource list.
	 */
	public ItemGroup searchItemGroup4Id( final int targetGroupId ) {
		logger.info(">> [SDEDatabaseManager.searchItemGroup4Id]> targetGroupId: {}", targetGroupId);
		ItemGroup target = new ItemGroup();
		try {
			final RawStatement cursor = constructStatement(SELECT_ITEMGROUP, new String[]{Integer.valueOf(targetGroupId).toString()});
			while (cursor.moveToNext()) {
				target.setGroupId(cursor.getInt(ITEMGROUP_GROUPID_COLINDEX));
				target.setCategoryId(cursor.getInt(ITEMGROUP_CATEGORYID_COLINDEX));
				target.setGroupName(cursor.getString(ITEMGROUP_GROUPNAME_COLINDEX));
				target.setIconLinkName(cursor.getString(ITEMGROUP_ICONLINKNAME_COLINDEX));
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchItemGroup4Id]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchItemGroup4Id]");
			return target;
		}
	}

	/**
	 * Search on the sde.sqlite database for the item category information. This new select is used to get access to
	 * the icon information that should be stored to be correlated to the resource list.
	 */
	public ItemCategory searchItemCategory4Id( final int targetCategoryId ) {
		logger.info(">> [SDEDatabaseManager.searchItemCategory4Id]> targetCategoryId: {}", targetCategoryId);
		ItemCategory target = new ItemCategory();
		try {
			final RawStatement cursor = constructStatement(SELECT_ITEMCATEGORY, new String[]{Integer.valueOf(targetCategoryId).toString()});
			while (cursor.moveToNext()) {
				target.setCategoryId(cursor.getInt(ITEMCATEGORY_CATEGORYID_COLINDEX));
				target.setCategoryName(cursor.getString(ITEMCATEGORY_CATEGORYNAME_COLINDEX));
				target.setIconLinkName(cursor.getString(ITEMCATEGORY_ICONLINKNAME_COLINDEX));
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchItemCategory4Id]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchItemCategory4Id]");
			return target;
		}
	}
}

// - UNUSED CODE ............................................................................................
//[01]
//				final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidCCPDatabaseConnector.SELECT_ITEM_BYID,
//						new String[]{Integer.valueOf(typeID).toString()});
//				if (null != cursor) {
//			final Cursor cursor = getCCPDatabase().rawQuery(SELECT_ITEM_BYID,
//					new String[] { Integer.valueOf(typeID).toString() });
//	      Statement stmt = getCCPDatabase().createStatement();
//					prepStmt = getSDEConnection().prepareStatement(SELECT_ITEM_BYID);
//					prepStmt.setString(1, Integer.valueOf(typeID).toString());
//					cursor = prepStmt.executeQuery();
// The query can be run but now there are ids that do not return data.
//					while (cursor.next()) {
//				}
//				}
//				} catch(Exception e){
//					logger.warn("W> AndroidDatabaseConnector.searchItembyID -- Item <" + typeID
//							+ "> not found.");
//					return new EveItem();
//				} finally{
//					try {
//						if (cursor != null) {
//							cursor.close();
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//					try {
//						if (prepStmt != null) {
//							prepStmt.close();
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}