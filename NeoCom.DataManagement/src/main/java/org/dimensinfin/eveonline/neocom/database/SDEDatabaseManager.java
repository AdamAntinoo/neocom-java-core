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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

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
	private static Logger logger = LoggerFactory.getLogger("SDEDatabaseManager");

	// --- S Q L   S T A T E M E N T S   S E C T I O N
	// - I T E M B Y I D
	private static int ITEM_BYID_TYPEID_COLINDEX = 1;
	private static int ITEM_BYID_TYPENAME_COLINDEX = 2;
	private static int ITEM_BYID_GROUPID_COLINDEX = 3;
	private static int ITEM_BYID_GROUPNAME_COLINDEX = 4;
	private static int ITEM_BYID_CATEGORYID_COLINDEX = 5;
	private static int ITEM_BYID_CATEGORYNAME_COLINDEX = 6;
	private static int ITEM_BYID_BASEPRICE_COLINDEX = 7;
	private static int ITEM_BYID_VOLUME_COLINDEX = 8;
	private static int ITEM_BYID_TECH_COLINDEX = 9;
	private static final String SELECT_ITEM_BYID = "SELECT it.typeId AS typeId"
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
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeId = it.typeId"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeId = ?";

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
	private static final String SELECT_LOCATIONBYID = "SELECT md.itemID AS locationID, md.typeId AS typeId, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemId, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationId, -1) AS constellationId, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionId, -1) AS regionId, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionId = md.regionId"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationId = md.constellationId"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";

	// - L O C A T I O N B Y S Y S T E M
	private static int LOCATIONBYSYSTEM_SOLARSYSTEMID_COLINDEX = 1;
	private static final String SELECT_LOCATIONBYSYSTEM = "SELECT solarSystemID FROM mapSolarSystems"
			+ " WHERE solarSystemName = ?";

	// - S T A T I O N 4 T Y P E
	private static int STATIONTYPEID_COLINDEX = 1;
	private static final String SELECT_STATIONTYPE = "SELECT stationTypeID FROM staStations WHERE stationId = ?";

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
			"SELECT        pstms.schematicID, pstms.typeId, pstms.quantity, pstms.isInput"
					+ " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms"
					+ " WHERE  pstmt.schematicID = ?"
					+ " AND    pstmt.isInput = 0"
					+ " AND    pstms.schematicID = pstmt.schematicID";

	// - F I E L D   I N D E X   D E F I N I T I O N S
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
	public synchronized EveItem searchItem4Id( final int typeId ) {
		logger.info(">< [SDEDatabaseManager.searchItem4Id]> Identifier: {}", typeId);
		final EveItem hit = new EveItem();
		try {
			final RawStatement cursor = constructStatement(SELECT_ITEM_BYID, new String[]{Integer.valueOf(typeId).toString()});
			boolean found = false;
			while (cursor.moveToNext()) {
				found = true;
				hit.setTypeId(cursor.getInt(ITEM_BYID_TYPEID_COLINDEX));
				hit.setName(cursor.getString(ITEM_BYID_TYPENAME_COLINDEX));
				hit.setGroupId(cursor.getInt(ITEM_BYID_GROUPID_COLINDEX));
				hit.setCategoryId(cursor.getInt(ITEM_BYID_CATEGORYID_COLINDEX));
				hit.setBasePrice(cursor.getDouble(ITEM_BYID_BASEPRICE_COLINDEX));
				hit.setVolume(cursor.getDouble(ITEM_BYID_VOLUME_COLINDEX));
				// Process the Tech field. The query marks blueprints
				String tech = cursor.getString(ITEM_BYID_TECH_COLINDEX);
				if (tech.equalsIgnoreCase("NOTECH")) {
					// Update the Tech value when item is a Blueprint.
					hit.setTech(ModelWideConstants.eveglobal.TechI);
					if (hit.getName().contains(" II Blueprint")) {
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
				logger.warn("W> [SDEDatabaseManager.searchItem4Id]> Item <{}> not found.", typeId);
			}
		} catch (SQLException sqle) {
			logger.error("E> [SDEDatabaseManager.searchItem4Id]> Exception while processing query. {}", sqle.getMessage());
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
			logger.error("E> [SDEDatabaseManager.searchItem4Id]> Exception while processing query. {}", rtex.getMessage());
		} finally {
			return hit;
		}
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
		List<EveLocation> locationList = null;
		// Search for the location at the application private database. Citadels and Outposts.
		try {
			locationList = new GlobalDataManager().getNeocomDBHelper().getLocationDao().queryForEq("id", locationID);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return new EveLocation(locationID);
		}
		// Check list contents. If found we have the location, else search for a SDE game location.
		if (locationList.size() < 1) {
			return searchLocation4IdAtSDE(locationID);
		} else {
			// Location found on the Application database.
			return locationList.get(0);
		}
	}

	private EveLocation searchLocation4IdAtSDE( final long locationId ) {
		logger.info(">< [SDEDatabaseManager.searchLocation4IdAtSDE]> locationId: {}", locationId);
		EveLocation target = new EveLocation();
		try {
			final RawStatement cursor = constructStatement(SELECT_LOCATIONBYID, new String[]{Long.valueOf(locationId).toString()});
			boolean detected = false;
			while (cursor.moveToNext()) {
				detected = true;
				long fragmentID = cursor.getLong(LOCATIONBYID_SYSTEMID_COLINDEX);
				if (fragmentID > 0) {
					target.setSystemId(fragmentID);
					target.setSystem(cursor.getString(LOCATIONBYID_SYSTEM_COLINDEX));
				} else {
					target.setSystem(cursor.getString(LOCATIONBYID_LOCATIONNAME_COLINDEX));
					target.setSystemId(cursor.getLong(LOCATIONBYID_LOCATIONID_COLINDEX));
				}
				fragmentID = cursor.getLong(LOCATIONBYID_CONSTELLATIONID_COLINDEX);
				if (fragmentID > 0) {
					target.setConstellationId(fragmentID);
					target.setConstellation(cursor.getString(LOCATIONBYID_CONSTELLATION_COLINDEX));
				}
				fragmentID = cursor.getLong(LOCATIONBYID_REGIONID_COLINDEX);
				if (fragmentID > 0) {
					target.setRegionId(fragmentID);
					target.setRegion(cursor.getString(LOCATIONBYID_REGION_COLINDEX));
				}
				target.setTypeId(ELocationType.CCPLOCATION);
				target.setStation(cursor.getString(LOCATIONBYID_LOCATIONNAME_COLINDEX));
				target.setLocationID(cursor.getLong(LOCATIONBYID_LOCATIONID_COLINDEX));
				target.setSecurity(cursor.getString(LOCATIONBYID_SECURITY_COLINDEX));
				// Update the final ID
				target.getID();
			}
			cursor.close();
			if (!detected) {
				logger.info("-- [SDEDatabaseManager.searchLocation4IdAtSDE]> Location: {} not found on any Database - UNKNOWN-.", locationId);
				target.setSystem("ID>" + Long.valueOf(locationId).toString());
			}
		} catch (final SQLException sqle) {
			logger.error("E [SDEDatabaseManager.searchLocation4IdAtSDE]> Exception processing statement: {}" + sqle.getMessage());
		} finally {
//			logger.info("<< [SDEDatabaseManager.searchLocation4IdAtSDE]");
			return target;
		}
	}

	/**
	 * Search for the location using only the system identifier.
	 *
	 * @param name
	 * @return
	 */
	public EveLocation searchLocationBySystem( final String name ) {
		final EveLocation newLocation = new EveLocation();
		try {
			final RawStatement cursor = constructStatement(SELECT_LOCATIONBYSYSTEM, new String[]{name});
			while (cursor.moveToNext()) {
				int locationID = cursor.getInt(LOCATIONBYSYSTEM_SOLARSYSTEMID_COLINDEX);
				cursor.close();
				return new GlobalDataManager().searchLocation4Id(locationID);
			}
		} catch (final Exception ex) {
			logger.warn("W [SDEDatabaseManager.searchLocationBySystem]> Location <" + name + "> not found.");
		}
		return newLocation;
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
			logger.info("<< [SDEDatabaseManager.searchItemGroup4Id]> GroupName: {}", target.getGroupName());
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
			logger.info("<< [SDEDatabaseManager.searchItemCategory4Id]> CategoryName: {}", target.getCategoryName());
			return target;
		}
	}

	/**
	 * Returns the resource identifier of the station class to locate icons or other type related resources.
	 */
	public int searchStationType( final long stationID ) {
		logger.info(">< [SDEDatabaseManager.searchStationType]> stationId: {}", stationID);
		if (stationID == -2) {
			// Test the cause of this error.
			int f = 6;
		}
		int stationTypeID = 1529;
		try {
			final RawStatement cursor = constructStatement(SELECT_STATIONTYPE, new String[]{Long.valueOf(stationID).toString()});
			while (cursor.moveToNext()) {
				stationTypeID = cursor.getInt(STATIONTYPEID_COLINDEX);
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchStationType]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchStationType]");
			return stationTypeID;
		}
	}

	// --- M O D U L E 4 B L U E P R I N T
	private static int PRODUCTTYPEID_MODULE4BLUEPRINT_COLINDEX = 1;
	private static final String SELECT_MODULE4BLUEPRINT = "SELECT productTypeID FROM industryActivityProducts BT"
			+ " WHERE typeId = ? AND activityID = 1";


	public int searchModule4Blueprint( final int blueprintTypeId ) {
		logger.info(">< [SDEDatabaseManager.searchModule4Blueprint]> bpitemID: {}", blueprintTypeId);
		int productTypeId = -1;
		try {
			final RawStatement cursor = constructStatement(SELECT_MODULE4BLUEPRINT, new String[]{Integer.valueOf(blueprintTypeId).toString()});
			while (cursor.moveToNext()) {
				productTypeId = cursor.getInt(PRODUCTTYPEID_MODULE4BLUEPRINT_COLINDEX);
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchModule4Blueprint]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchModule4Blueprint]");
			return productTypeId;
		}
	}

	// --- B L U E P R I N T 4 M O D U L E
	private static int TYPEID_BLUEPRINT4MODULE_COLINDEX = 1;
	private static final String SELECT_BLUEPRINT4MODULE = "SELECT typeID FROM industryActivityProducts BT"
			+ " WHERE productTypeID = ? AND activityID = 1";

	/**
	 * Returns the blueprint id that matched this module from the <code>invBlueprintTypes</code> table.
	 */
	public int searchBlueprint4Module( final int moduleId ) {
		logger.info(">< [SDEDatabaseManager.searchBlueprint4Module]> moduleId: {}", moduleId);
		int blueprintTypeId = -1;
		try {
			final RawStatement cursor = constructStatement(SELECT_BLUEPRINT4MODULE, new String[]{Integer.valueOf(moduleId).toString()});
			while (cursor.moveToNext()) {
				blueprintTypeId = cursor.getInt(TYPEID_BLUEPRINT4MODULE_COLINDEX);
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchBlueprint4Module]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchBlueprint4Module]");
			return blueprintTypeId;
		}
	}

	// --- T E C H 4 B L U E P R I N T
	private static int TECH4BLUEPRINT_TYPEID_COLINDEX = 1;
	private static int TECH4BLUEPRINT_TYPENAME_COLINDEX = 2;
	private static int TECH4BLUEPRINT_METAGROUPID_COLINDEX = 3;
	private static int TECH4BLUEPRINT_METAGROUPNAME_COLINDEX = 4;
	private static final String SELECT_TECH4BLUEPRINT = "SELECT iap.typeId, it.typeName, imt.metaGroupID, img.metaGroupName"
			+ " FROM industryActivityProducts iap, invTypes it, invMetaTypes imt, invMetaGroups img" + " WHERE it.typeId =?"
			+ " AND iap.typeId = it.typeId" + " AND imt.typeId = productTypeID" + " AND img.metaGroupID = imt.metaGroupID"
			+ " AND iap.activityID = 1";

	public String searchTech4Blueprint( final int blueprintID ) {
		logger.info(">< [SDEDatabaseManager.searchTech4Blueprint]> blueprintID: {}", blueprintID);
		String productTypeID = ModelWideConstants.eveglobal.TechI;
		try {
			final RawStatement cursor = constructStatement(SELECT_TECH4BLUEPRINT, new String[]{Integer.valueOf(blueprintID).toString()});
			while (cursor.moveToNext()) {
				productTypeID = cursor.getString(TECH4BLUEPRINT_METAGROUPNAME_COLINDEX);
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchModule4Blueprint]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchModule4Blueprint]");
			return productTypeID;
		}
	}


	// --- R A W P L A N E T A R Y O U T P U T
	private static int RAW_PRODUCTRESULT_TYPEID_COLINDEX = 1;
	private static int RAW_PRODUCTRESULT_QUANTITY_COLINDEX = 2;
	private static int RAW_PRODUCTRESULT_SCHEMATICID_COLINDEX = 3;
	private static final String SELECT_RAW_PRODUCTRESULT = "SELECT pstmo.typeId, pstmo.quantity, pstmo.schematicID"
			+ " FROM   planetSchematicsTypeMap pstmi, planetSchematicsTypeMap pstmo" + " WHERE  pstmi.typeId = ?"
			+ " AND    pstmo.schematicID = pstmi.schematicID" + " AND    pstmo.isInput = 0";


	public int searchRawPlanetaryOutput( final int typeID ) {
		logger.info(">< [SDEDatabaseManager.searchRawPlanetaryOutput]> typeId: {}", typeID);
		int outputResourceId = typeID;
		try {
			final RawStatement cursor = constructStatement(SELECT_RAW_PRODUCTRESULT, new String[]{Integer.valueOf(typeID).toString()});
			while (cursor.moveToNext()) {
				outputResourceId = cursor.getInt(RAW_PRODUCTRESULT_TYPEID_COLINDEX);
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchModule4Blueprint]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchModule4Blueprint]");
			return outputResourceId;
		}
	}

	// --- S C H E M A T I C S 4 O U T P U T
	private static int SCHEMATICS4OUTPUT_TYPEID_COLINDEX = 1;
	private static int SCHEMATICS4OUTPUT_QUANTITY_COLINDEX = 2;
	private static int SCHEMATICS4OUTPUT_ISINPUT_COLINDEX = 3;
	private static final String SELECT_SCHEMATICS4OUTPUT = "SELECT pstms.typeId, pstms.quantity, pstms.isInput"
			+ " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms" + " WHERE  pstmt.typeId = ?"
			+ " AND    pstmt.isInput = 0" + " AND    pstms.schematicID = pstmt.schematicID";

	public List<Schematics> searchSchematics4Output( final int targetId ) {
		logger.info(">< [SDEDatabaseManager.searchSchematics4Output]> typeId: {}", targetId);
		List<Schematics> scheList = new Vector<Schematics>();
		try {
			final RawStatement cursor = constructStatement(SELECT_SCHEMATICS4OUTPUT, new String[]{Integer.valueOf(targetId).toString()});
			while (cursor.moveToNext()) {
				scheList.add(new Schematics().addData(cursor.getInt(SCHEMATICS4OUTPUT_TYPEID_COLINDEX),
						cursor.getInt(SCHEMATICS4OUTPUT_QUANTITY_COLINDEX),
						(cursor.getInt(SCHEMATICS4OUTPUT_ISINPUT_COLINDEX) == 1) ? true : false));
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchSchematics4Output]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchSchematics4Output]");
			return scheList;
		}
	}

	// --- L I S T O F M A T E R I A L S
	private static int LISTOFMATERIALS_TYPEID_COLINDEX = 1;
	private static int LISTOFMATERIALS_MATERIALTYPEID_COLINDEX = 2;
	private static int LISTOFMATERIALS_QUANTITY_COLINDEX = 3;
	private static final String SELECT_LIST_OF_MATERIALS = "SELECT typeID, materialTypeID, quantity " +
			" FROM industryActivityMaterials " +
			" WHERE typeID = ? AND activityID = 1";

	public List<Resource> searchListOfMaterials( final int bpid ) {
		logger.info(">< [SDEDatabaseManager.searchListOfMaterials]> bpid: {}", bpid);
		List<Resource> lom = new ArrayList<Resource>();
		final Chrono chrono = new Chrono();
		try {
			final RawStatement cursor = constructStatement(SELECT_LIST_OF_MATERIALS, new String[]{Integer.valueOf(bpid).toString()});
//			int blueprintId = -1;
			while (cursor.moveToNext()) {
				lom.add(new Resource(cursor.getInt(LISTOFMATERIALS_MATERIALTYPEID_COLINDEX)
						, cursor.getInt(LISTOFMATERIALS_QUANTITY_COLINDEX)));
//				blueprintId = cursor.getInt(cursor.getInt(LISTOFMATERIALS_TYPEID_COLINDEX));
			}
			// Add the required blueprint to the list of materials.
			if (bpid != -1) {
				lom.add(new Resource(bpid, 1));
			}

//			// Add the skills to the list of resources
//			cursor = ccpDatabase.rawQuery(SEARCH_LISTOFMATERIALS,
//					new String[]{Integer.valueOf(itemID).toString()});
//			if ( null == cursor ) throw new Exception("E> Invalid cursor or empty.");
//			while (cursor.moveToNext()) {
//				// The the data of the resource. Check for blueprints.
//				int skillID = cursor.getInt(cursor.getColumnIndex("skillID"));
//				int level = cursor.getInt(cursor.getColumnIndex("level"));
//				Resource resource = new Resource(skillID, level);
//				buildJob.add(resource);
//			}
//			cursor.close();

			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [SDEDatabaseManager.searchSchematics4Output]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [SDEDatabaseManager.searchSchematics4Output]");
			return lom;
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]
//				final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidCCPDatabaseConnector.SELECT_ITEM_BYID,
//						new String[]{Integer.valueOf(typeId).toString()});
//				if (null != cursor) {
//			final Cursor cursor = getCCPDatabase().rawQuery(SELECT_ITEM_BYID,
//					new String[] { Integer.valueOf(typeId).toString() });
//	      Statement stmt = getCCPDatabase().createStatement();
//					prepStmt = getSDEConnection().prepareStatement(SELECT_ITEM_BYID);
//					prepStmt.setString(1, Integer.valueOf(typeId).toString());
//					cursor = prepStmt.executeQuery();
// The query can be run but now there are ids that do not return data.
//					while (cursor.next()) {
//				}
//				}
//				} catch(Exception e){
//					logger.warn("W> AndroidDatabaseConnector.searchItembyID -- Item <" + typeId
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
