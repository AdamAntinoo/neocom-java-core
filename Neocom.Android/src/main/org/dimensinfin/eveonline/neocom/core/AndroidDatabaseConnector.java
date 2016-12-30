//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.core;

// - IMPORT SECTION .........................................................................................
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Job;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComMarketOrder;
import org.dimensinfin.eveonline.neocom.model.Outpost;
import org.dimensinfin.eveonline.neocom.model.Property;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;

// - CLASS IMPLEMENTATION ...................................................................................
public class AndroidDatabaseConnector implements IDatabaseConnector {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger										= Logger.getLogger("AndroidDatabaseConnector");

	private static final String	SELECT_ITEM_BYID					= "SELECT it.typeID AS typeID, it.typeName AS typeName"
			+ " , ig.groupName AS groupName" + " , ic.categoryName AS categoryName" + " , it.basePrice AS basePrice"
			+ " , it.volume AS volume" + " , IFNULL(img.metaGroupName, " + '"' + "NOTECH" + '"' + ") AS Tech"
			+ " FROM invTypes it" + " LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID"
			+ " LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID"
			+ " LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID"
			+ " LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID" + " WHERE it.typeID = ?";

	private static final String	SELECT_LOCATIONBYID				= "SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security"
			+ " , IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system"
			+ " , IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation"
			+ " , IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region" + " FROM mapDenormalize md"
			+ " LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID"
			+ " LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID"
			+ " LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID" + " WHERE itemID = ?";
	private static final String	SELECT_MATERIAL_USAGE			= "SELECT typeID, materialTypeID, quantity FROM industryActivityMaterials WHERE typeID = ? AND activityID = 1";
	private static final String	LOM4BLUEPRINT							= "SELECT iam.typeID, itb.typeName, iam.materialTypeID, it.typeName, ig.groupName, ic.categoryName, iam.quantity, iam.consume"
			+ " FROM industryActivityMaterials iam, invTypes itb, invTypes it, invGroups ig, invCategories ic"
			+ " WHERE iam.typeID = ?" + " AND iam.activityID = 1" + " AND itb.typeID = iam.typeID"
			+ " AND it.typeID = iam.materialTypeID" + " AND ig.groupID = it.groupID" + " AND ic.categoryID = ig.categoryID";

	private static final String	TECH4BLUEPRINT						= "SELECT iap.typeID, it.typeName, imt.metaGroupID, img.metaGroupName"
			+ " FROM industryActivityProducts iap, invTypes it, invMetaTypes imt, invMetaGroups img" + " WHERE it.typeID =?"
			+ " AND iap.typeID = it.typeID" + " AND imt.typeID = productTypeID" + " AND img.metaGroupID = imt.metaGroupID"
			+ " AND iap.activityID = 1";

	private static final String	REFINING_ASTEROID					= "SELECT itm.materialTypeID AS materialTypeID, itm.quantity AS qty"
			+ " , it.typeName AS materialName" + " , ito.portionSize AS portionSize"
			+ " FROM invTypeMaterials itm, invTypes it, invTypes ito" + " WHERE itm.typeID = ?"
			+ " AND it.typeID = itm.materialTypeID" + " AND ito.typeID = itm.typeID" + " ORDER BY itm.materialTypeID";

	private static final String	INDUSTRYACTIVITYMATERIALS	= "SELECT materialTypeID, quantity, consume FROM industryActivityMaterials WHERE typeID = ? AND activityID = 8";
	private static final String	STATIONTYPE								= "SELECT stationTypeID FROM staStations WHERE stationID = ?";
	private static final String	JOB_COMPLETION_TIME				= "SELECT typeID, time FROM industryActivity WHERE typeID = ? AND activityID = ?";
	private static final String	CHECK_INVENTION						= "SELECT count(*) AS counter"
			+ " FROM industryActivityProducts iap" + " WHERE iap.typeID = ?" + " AND iap.activityID = 8";
	private static final String	INVENTION_PRODUCT					= "SELECT productTypeID FROM industryActivityProducts WHERE typeID = ? AND activityID = 8";
	private static final String	CHECK_MANUFACTURABLE			= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String	CHECK_REACTIONABLE				= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String	CHECK_PLANETARYPRODUCED		= "SELECT count(*) AS counter FROM industryActivityProducts iap WHERE iap.productTypeID = ? AND iap.activityID = 1";
	private static final String	REACTION_COMPONENTS				= "SELECT" + "   invTypeReactions.reactionTypeID"
			+ " , invTypes.typeID, invTypes.typeName" + " , invTypeReactions.input"
			+ " , COALESCE(dgmTypeAttributes.valueInt, dgmTypeAttributes.valueFloat) * invTypeReactions.quantity AS quantity"
			+ " FROM invTypeReactions, dgmTypeAttributes, invTypes" + " WHERE"
			+ " invTypes.typeId = invTypeReactions.typeID AND" + " invTypeReactions.reactionTypeID IN ("
			+ "    SELECT reactionTypeID" + "    FROM invTypeReactions" + "    WHERE typeID = ? ) AND"
			+ " dgmTypeAttributes.typeID = invTypeReactions.typeID";

	public static MyLocation getLocation(final long locationID, final MyAsset parentAsset) {
		// Offices
		long fixedLocationID = locationID;
		if (fixedLocationID >= 66000000) {
			if (fixedLocationID < 66014933) {
				fixedLocationID = fixedLocationID - 6000001;
			} else {
				fixedLocationID = fixedLocationID - 6000000;
			}
		}
		MyLocation location = StaticData.get().getLocations().get(fixedLocationID);
		if (location != null) return location;
		if (parentAsset != null) {
			location = parentAsset.getLocation();
			if (location != null) return location;
		}
		location = CitadelGetter.get(locationID);
		if (location != null) return location;
		return new MyLocation(locationID);
	}

	// - F I E L D - S E C T I O N ............................................................................
	private Context														_context						= null;
	private SQLiteDatabase										staticDatabase			= null;
	private SQLiteDatabase										ccpDatabase					= null;
	private EveDroidDBHelper									appDatabaseHelper		= null;
	private final SparseArray<EveItem>				itemCache						= new SparseArray<EveItem>();
	private final SparseArray<MarketDataSet>	buyMarketDataCache	= new SparseArray<MarketDataSet>();
	private final SparseArray<MarketDataSet>	sellMarketDataCache	= new SparseArray<MarketDataSet>();
	private final SparseArray<Outpost>				outpostsCache				= new SparseArray<Outpost>();;

	private final HashMap<Long, NeoComAsset>	containerCache			= new HashMap<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AndroidDatabaseConnector(final Context app) {
		_context = app;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkInvention(final int typeID) {
		return this.checkRecordExistence(typeID, AndroidDatabaseConnector.CHECK_INVENTION);
	}

	public boolean checkManufacturable(final int typeID) {
		return this.checkRecordExistence(typeID, AndroidDatabaseConnector.CHECK_MANUFACTURABLE);
	}

	/**
	 * removes from the application database any asset and blueprint that contains the special -1 code as the
	 * owner identifier. Those records are from older downloads and have to be removed to avoid merging with the
	 * new download.
	 */
	public synchronized void clearInvalidRecords() {
		SQLiteDatabase database = null;
		try {
			database = this.getAppDatabase();
			synchronized (database) {
				database.beginTransaction();
				Log.i("", "-- clearInvalidAssets rows deleted ASSETS [OWNERID = -1] - "
						+ database.delete("Assets", "ownerID" + "=-1", null));
				//				Log.i("", "-- clearInvalidAssets rows deleted ASSETS [LOCATIONID = -1] - "
				//						+ database.delete("Assets", "locationId" + "=-1", null));
				Log.i("", "-- clearInvalidAssets rows deleted BLUEPRINTS [OWNERID = -1] - "
						+ database.delete("Blueprints", "ownerID" + "=-1", null));
				// Log.i("", "-- clearInvalidAssets rows deleted JOBS [OWNERID =
				// -1] - "
				// + database.delete("Jobs", "ownerID" + "=-1", null));
				database.setTransactionSuccessful();
			}
		} catch (final SQLiteException ex) {
			AndroidDatabaseConnector.logger.warning("W> Problem clearing invalid assets. " + ex.getMessage());
		} finally {
			if (null != database) {
				database.endTransaction();
			}
		}
	}

	public void closeDatabases() {
		//		appDatabaseHelper.close();
		if (null != staticDatabase) {
			staticDatabase.close();
		}
		if (null != ccpDatabase) {
			ccpDatabase.close();
		}
		staticDatabase = null;
		ccpDatabase = null;
	}

	public Dao<NeoComAsset, String> getAssetDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getAssetDAO();
	}

	public Dao<NeoComBlueprint, String> getBlueprintDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getBlueprintDAO();
	}

	public Dao<Job, String> getJobDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getJobDAO();
	}

	public Dao<EveLocation, String> getLocationDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getLocationDAO();
	}

	public Dao<NeoComMarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getMarketOrderDAO();
	}

	public Dao<Property, String> getPropertyDAO() throws java.sql.SQLException {
		if (null == appDatabaseHelper) {
			this.openDAO();
		}
		return appDatabaseHelper.getPropertyDAO();
	}

	public SQLiteDatabase getStaticDatabase() throws SQLException {
		if (null == staticDatabase) {
			this.openAppDataBase();
		}
		return staticDatabase;
	}

	/**
	 * Opens a SQLite database to get access to the Items and locations. If there is any problem the method
	 * returns true.
	 * 
	 * @return <code>true</code> if there was any problem.
	 */
	public boolean openAppDataBase() {
		if (null == staticDatabase) {
			final String database = AppConnector.getAppFilePath(R.string.appdatabasefilename);
			final String path = Environment.getExternalStorageDirectory() + "/" + database;
			try {
				if (AppConnector.sdcardAvailable()) {
					// Open the helper to check version and availability.
					// appDatabaseHelper = new EveDroidDBHelper(_context);
					staticDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
				} else
					return true;
				return false;
			} catch (final SQLException sqle) {
				AndroidDatabaseConnector.logger.severe("E> Failed to open database: " + database);
				AndroidDatabaseConnector.logger.severe("E> " + sqle.getMessage());
				throw new RuntimeException("E> Failed to open database: " + database + ". " + sqle.getMessage());
			}
		}
		return true;
	}

	public boolean openCCPDataBase() {
		if (null == ccpDatabase) {
			final String database = AppConnector.getAppFilePath(R.string.ccpdatabasefilename);
			final String path = Environment.getExternalStorageDirectory() + "/" + database;
			try {
				// InputStream dbStream =
				// _context.getResources().openRawResource(R.raw.eve);
				if (AppConnector.sdcardAvailable()) {
					ccpDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
				} else
					return true;
				return false;
			} catch (final SQLException sqle) {
				AndroidDatabaseConnector.logger.severe("E> Failed to open database: " + database);
				AndroidDatabaseConnector.logger.severe("E> " + sqle.getMessage());
			}
		}
		return true;
	}

	/**
	 * Like the other initialization methods this return a true if there was any problem during the process.
	 * 
	 * @return
	 */
	public boolean openDAO() {
		if (null == appDatabaseHelper) {
			appDatabaseHelper = OpenHelperManager.getHelper(_context, EveDroidDBHelper.class);
			return true;
		}
		return false;
	}

	public int queryBlueprintDependencies(final int bpitemID) {
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			final Cursor cursor = ccpDatabase.rawQuery(
					"SELECT parentBlueprintTypeID FROM invBlueprintTypes BT WHERE blueprintTypeID = ?",
					new String[] { Integer.valueOf(bpitemID).toString() });
			if (null != cursor) {
				int parentBlueprintTypeID = -1;
				while (cursor.moveToNext()) {
					parentBlueprintTypeID = cursor.getInt(cursor.getColumnIndex("parentBlueprintTypeID"));
				}
				cursor.close();
				return parentBlueprintTypeID;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error getting T2 BPO for BP <" + bpitemID + ">.");
		}
		return -1;
	}

	public ArrayList<Resource> refineOre(final int oreID) {
		ArrayList<Resource> results = new ArrayList<Resource>();
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.REFINING_ASTEROID,
					new String[] { Integer.valueOf(oreID).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
					int qty = cursor.getInt(cursor.getColumnIndex("qty"));
					int portionSize = cursor.getInt(cursor.getColumnIndex("portionSize"));
					Resource resource = new Resource(resourceID, qty);
					resource.setStackSize(portionSize);
					results.add(resource);
				}
				cursor.close();
				return results;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error refining <" + oreID + "> not found.");
		}
		return results;
	}

	/**
	 * Changes the owner id for all records from a new download with the id of the current character. This
	 * completes the download and the assignment of the resources to the character without interrupting the
	 * processing of data by the application.
	 */
	public synchronized void replaceAssets(final long characterID) {
		SQLiteDatabase database = null;
		try {
			database = this.getAppDatabase();
			synchronized (database) {
				database.beginTransaction();
				Log.i("", "-- replaceAssets rows deleted ASSETS [OWNERID = " + characterID + "] - "
						+ database.delete("Assets", "ownerID" + "=" + characterID, null));
				ContentValues values = new ContentValues();
				values.put("ownerID", characterID);
				database.update("Assets", values, "ownerID=-1", null);
				database.setTransactionSuccessful();
			}
		} catch (final SQLiteException ex) {
			ex.printStackTrace();
			AndroidDatabaseConnector.logger
					.warning("W> Problem replacing assets for " + characterID + ". " + ex.getMessage());
		} finally {
			if (null != database) {
				database.endTransaction();
			}
		}
	}

	public synchronized void replaceBlueprints(final long characterID) {
		SQLiteDatabase database = null;
		try {
			database = this.getAppDatabase();
			synchronized (database) {
				database.beginTransaction();
				Log.i("", "-- replaceBlueprints rows deleted BLUEPRINTS [OWNERID = " + characterID + "] - "
						+ database.delete("Blueprints", "ownerID" + "=" + characterID, null));
				ContentValues values = new ContentValues();
				values.put("ownerID", characterID);
				database.update("Blueprints", values, "ownerID" + "=-1", null);
				database.setTransactionSuccessful();
			}
		} catch (final SQLiteException ex) {
			ex.printStackTrace();
			AndroidDatabaseConnector.logger
					.warning("W> Problem replacing Blueprints for " + characterID + ". " + ex.getMessage());
		} finally {
			if (null != database) {
				database.endTransaction();
			}
		}
	}

	public synchronized void replaceJobs(final long characterID) {
		SQLiteDatabase database = null;
		try {
			database = this.getAppDatabase();
			synchronized (database) {
				database.beginTransaction();
				Log.i("", "-- replaceJobs rows deleted JOBS [OWNERID = " + characterID + "] - "
						+ database.delete("Jobs", "ownerID" + "=" + characterID + " AND jobType='CCP'", null));
				ContentValues values = new ContentValues();
				values.put("ownerID", characterID);
				database.update("Jobs", values, "ownerID=-1", null);
				database.setTransactionSuccessful();
			}
		} catch (final SQLiteException ex) {
			ex.printStackTrace();
			AndroidDatabaseConnector.logger
					.warning("W> Problem replacing assets for " + characterID + ". " + ex.getMessage());
		} finally {
			if (null != database) {
				database.endTransaction();
			}
		}
	}

	public ArrayList<NeoComAsset> searchAsset4Type(final long characterID, final int typeID) {
		// Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = this.getAssetDAO();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("typeID", typeID);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	public NeoComAsset searchAssetByID(final long assetID) {
		// search for the asset on the cache. Usually searching for containers.
		NeoComAsset hit = containerCache.get(assetID);
		if (null == hit) {
			// Select assets for the owner and with an specific type id.
			List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
			try {
				Dao<NeoComAsset, String> assetDao = this.getAssetDAO();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("assetID", assetID);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				if (assetList.size() > 0) {
					hit = assetList.get(0);
					containerCache.put(hit.getAssetID(), hit);
				}
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return hit;
	}

	public ArrayList<NeoComAsset> searchAssetContainedAt(final long characterID, final long containerId) {
		// Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = this.getAssetDAO();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("parentAssetID", containerId);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	/**
	 * Returns the blueprint id that matched this module from the <code>invBlueprintTypes</code> table.
	 */
	public int searchBlueprint4Module(final int moduleID) {
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			final Cursor cursor = ccpDatabase.rawQuery(
					"SELECT typeID FROM industryActivityProducts BT WHERE productTypeID = ? AND activityID = 1",
					new String[] { Integer.valueOf(moduleID).toString() });
			if (null != cursor) {
				int productTypeID = -1;
				while (cursor.moveToNext()) {
					productTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
				}
				cursor.close();
				return productTypeID;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error locating BPO for module <" + moduleID + ">.");
		}
		return -1;
	}

	public ArrayList<Integer> searchInventionableBlueprints(final String idList) {
		Log.i("DBQUERY", ">> AndroidDatabaseConnector.searchInventionableBlueprints");
		ArrayList<Integer> blueprintIds = new ArrayList<Integer>();
		AppConnector.startChrono();
		try {
			// if (null == ccpDatabase) ccpDatabase = getCCPDatabase();
			final Cursor cursor = this.getCCPDatabase().rawQuery(
					"SELECT typeID FROM industryActivityProducts WHERE productTypeID in ( " + idList + " ) AND activityID = 8",
					null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					blueprintIds.add(new Integer(cursor.getInt(cursor.getColumnIndex("typeID"))));
				}
				// We have collected the required blueprint. Add it to the list
				// of resources.
				cursor.close();
				Log.i("DBQUERY.TIME",
						"~~ Time lapse for [SELECT PRODUCTTYPEID IN " + idList + "] - " + AppConnector.timeLapse());
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for  blueprints.");
		}
		Log.i("DBQUERY", "<< AndroidDatabaseConnector.searchInventionableBlueprints " + blueprintIds.size());
		return blueprintIds;
	}

	public int searchInventionProduct(final int typeID) {
		int product = -1;
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.INVENTION_PRODUCT,
					new String[] { Integer.valueOf(typeID).toString() });
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					product = cursor.getInt(0);
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			Log.w("EVEI", "W> AndroidDatabaseConnector.checkInvention -- Exception:" + ex.getMessage());
		}
		return product;
	}

	/**
	 * Search on the eve.db database for the attributes that describe an Item. Items are the lowest data
	 * structure for EVE resources or modules. Everything on Eve is an Item. We detect blueprints that require a
	 * different treatment and also we check for the availability of the item at the current cache if
	 * implemented.
	 */
	public EveItem searchItembyID(final int typeID) {
		// Search the item on the cache.
		EveItem hit = itemCache.get(typeID);
		if (null == hit) {
			try {
				hit = new EveItem();
				final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.SELECT_ITEM_BYID,
						new String[] { Integer.valueOf(typeID).toString() });
				if (null != cursor) {
					if (cursor.moveToFirst()) {
						hit.setTypeID(cursor.getInt(0));
						hit.setName(cursor.getString(1));
						hit.setGroupname(cursor.getString(2));
						hit.setCategory(cursor.getString(3));
						hit.setBasePrice(cursor.getDouble(4));
						hit.setVolume(cursor.getDouble(5));
						// Process the Tech field. The query marks blueprints
						String tech = cursor.getString(6);
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
					cursor.close();
					itemCache.put(new Integer(typeID), hit);
				}
			} catch (final Exception ex) {
				Log.w("EVEI", "W> AndroidDatabaseConnector.searchItembyID -- Item <" + typeID + "> not found.");
				return new EveItem();
			}
		}
		return hit;
	}

	// public EveItem searchItembyIDoff(final int typeID) {
	// // Search the item on the cache.
	// EveItem hit = itemCache.get(new Integer(typeID));
	// if (null == hit) {
	// try {
	// Dao<EveItem, String> dao = getItemDAO();
	// EveItem newItem = dao.queryForId(new Integer(typeID).toString());
	// if (null == newItem) return new EveItem();
	// itemCache.put(new Integer(typeID), newItem);
	// return newItem;
	// } catch (final Exception ex) {
	// logger.warning("W> Item <" + typeID + "> not found.");
	// }
	// return new EveItem();
	// } else
	// return hit;
	// }

	public ArrayList<Job> searchJob4Class(final long characterID, final String classname) {
		// Select assets for the owner and woth an specific type id.
		List<Job> joblist = new ArrayList<Job>();
		try {
			Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
			QueryBuilder<Job, String> queryBuilder = jobDao.queryBuilder();
			Where<Job, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("jobType", classname);
			PreparedQuery<Job> preparedQuery = queryBuilder.prepare();
			joblist = jobDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Job>) joblist;

	}

	public int searchJobExecutionTime(final int typeID, final int activityID) {
		Log.i("EVEI", ">> AndroidDatabaseConnector.searchJobExecutionTime");
		int jobTime = ModelWideConstants.HOURS2 / 1000;
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			final Cursor cursor = ccpDatabase.rawQuery(AndroidDatabaseConnector.JOB_COMPLETION_TIME,
					new String[] { Integer.valueOf(typeID).toString(), Integer.valueOf(activityID).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					jobTime = cursor.getInt(cursor.getColumnIndex("time"));
				}
				// Log.i("EVEI", "<<
				// AndroidDatabaseConnector.searchJobExecutionTime");
				// return jobTime;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for job time <" + typeID + "> not found.");
		}
		Log.i("EVEI", "<< AndroidDatabaseConnector.searchJobExecutionTime");
		return jobTime;
	}

	public ArrayList<Resource> searchListOfDatacores(final int itemID) {
		// Log.i("AndroidDatabaseConnector", ">>
		// AndroidDatabaseConnector.searchListOfDatacores");
		ArrayList<Resource> inventionJob = new ArrayList<Resource>();
		AppConnector.startChrono();
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.INDUSTRYACTIVITYMATERIALS,
					new String[] { Integer.valueOf(itemID).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
					// EveItem newItem = searchItembyID(resourceID);
					// Resource resource = ;
					inventionJob.add(new Resource(resourceID, qty));
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
		// Log.i("AndroidDatabaseConnector", "<<
		// AndroidDatabaseConnector.searchListOfDatacores " +
		// inventionJob.getSize());
		Log.i("DBQUERY.TIME", "~~ Time lapse for [SELECT INVENTION LOM " + itemID + "] - [" + inventionJob.size() + "] "
				+ AppConnector.timeLapse());
		return inventionJob;
	}

	public ArrayList<Resource> searchListOfMaterials(final int itemID) {
		// Log.i("AndroidDatabaseConnector", ">>
		// AndroidDatabaseConnector.searchListOfMaterials");
		ArrayList<Resource> buildJob = new ArrayList<Resource>();
		AppConnector.startChrono();
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			Cursor cursor = ccpDatabase.rawQuery(AndroidDatabaseConnector.SELECT_MATERIAL_USAGE,
					new String[] { Integer.valueOf(itemID).toString() });
			int blueprintId = -1;
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int materialTypeID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
					blueprintId = cursor.getInt(cursor.getColumnIndex("typeID"));
					// int consume =
					// cursor.getInt(cursor.getColumnIndex("consume"));
					Resource resource = new Resource(materialTypeID, qty);
					buildJob.add(resource);
				}
				cursor.close();
				// We have collected the required blueprint. Add it to the list
				// of resources.
				if (blueprintId != -1) {
					buildJob.add(new Resource(blueprintId, 1));
				}

				// Add the skills to the list of resources
				cursor = ccpDatabase.rawQuery(
						"SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID",
						new String[] { Integer.valueOf(itemID).toString() });
				if (null == cursor) throw new Exception("E> Invalid cursor or empty.");
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int skillID = cursor.getInt(cursor.getColumnIndex("skillID"));
					int level = cursor.getInt(cursor.getColumnIndex("level"));
					Resource resource = new Resource(skillID, level);
					buildJob.add(resource);
				}
				cursor.close();

				// Log.i("AndroidDatabaseConnector", "<<
				// AndroidDatabaseConnector.searchListOfMaterials " +
				// buildJob.size());
				// return buildJob;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
		Log.i("DBQUERY.TIME",
				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + buildJob.size() + "] " + AppConnector.timeLapse());
		// Log.i("AndroidDatabaseConnector", "<<
		// AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
		return buildJob;
	}

	public ArrayList<Resource> searchListOfReaction(final int itemID) {
		ArrayList<Resource> buildJob = new ArrayList<Resource>();
		AppConnector.startChrono();
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			Cursor cursor = ccpDatabase.rawQuery(AndroidDatabaseConnector.REACTION_COMPONENTS,
					new String[] { Integer.valueOf(itemID).toString() });
			// int blueprintId = -1;
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int materialTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
					int input = cursor.getInt(cursor.getColumnIndex("input"));
					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
					// blueprintId =
					// cursor.getInt(cursor.getColumnIndex("typeID"));
					// int consume =
					// cursor.getInt(cursor.getColumnIndex("consume"));
					if (input == 1) {
						Resource resource = new Resource(materialTypeID, qty);
						buildJob.add(resource);
					}
				}
				cursor.close();
				// // We have collected the required blueprint. Add it to the
				// list of resources.
				// if (blueprintId != -1) buildJob.add(new Resource(blueprintId,
				// 1));
				//
				// // Add the skills to the list of resources
				// cursor = ccpDatabase
				// .rawQuery(
				// "SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM
				// industryActivitySkills ais, invTypes it WHERE ais.typeID = ?
				// AND ais.activityID = 1 AND it.typeID=ais.skillID",
				// new String[] { Integer.valueOf(itemID).toString() });
				// if (null == cursor) throw new Exception("E> Invalid cursor or
				// empty.");
				// while (cursor.moveToNext()) {
				// // The the data of the resource. Check for blueprints.
				// int skillID =
				// cursor.getInt(cursor.getColumnIndex("skillID"));
				// int level = cursor.getInt(cursor.getColumnIndex("level"));
				// Resource resource = new Resource(skillID, level);
				// buildJob.add(resource);
				// }
				// cursor.close();

				// Log.i("AndroidDatabaseConnector", "<<
				// AndroidDatabaseConnector.searchListOfMaterials " +
				// buildJob.size());
				// return buildJob;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
		Log.i("DBQUERY.TIME",
				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + buildJob.size() + "] " + AppConnector.timeLapse());
		// Log.i("AndroidDatabaseConnector", "<<
		// AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
		return buildJob;
	}

	/**
	 * <<<<<<< HEAD Loates the system and other information for a location based on the ID received as a
	 * parameter. New ======= Loates the systema nd other information for a location based on the ID received as
	 * a parameter. New >>>>>>> 0.6.2-NewEveapi implementation use this ID to calculate if this matches a
	 * corporation outpost or a corporation office hangar.
	 */
	public EveLocation searchLocationbyID(final long locationID) {
		// Try to get that id from the cache tables
		try {
			Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
			QueryBuilder<EveLocation, String> queryBuilder = locationDao.queryBuilder();
			Where<EveLocation, String> where = queryBuilder.where();
			where.eq("id", locationID);
			PreparedQuery<EveLocation> preparedQuery = queryBuilder.prepare();
			List<EveLocation> locationList = locationDao.query(preparedQuery);

			// Check list contents. If found we have the location. Else then check if Office
			if (locationList.size() < 1) {
				AndroidDatabaseConnector.logger
						.info("-- [searchLocationbyID]> Location: " + locationID + " not found on cache.");
				// Offices
				long fixedLocationID = locationID;
				if (fixedLocationID >= 66000000) {
					if (fixedLocationID < 66014933) {
						fixedLocationID = fixedLocationID - 6000001;
					} else {
						fixedLocationID = fixedLocationID - 6000000;
					}
				}
				EveLocation hit = new EveLocation(fixedLocationID);
				try {
					final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.SELECT_LOCATIONBYID,
							new String[] { Long.valueOf(fixedLocationID).toString() });
					if (null != cursor) {
						boolean detected = false;
						if (cursor.moveToFirst()) {
							AndroidDatabaseConnector.logger
									.info("-- [searchLocationbyID]> Location: " + locationID + " Obtained from CCP data.");
							detected = true;
							// Check returned values when doing the assignments.
							long fragmentID = cursor.getLong(cursor.getColumnIndex("systemID"));
							if (fragmentID > 0) {
								hit.setSystemID(fragmentID);
								hit.setSystem(cursor.getString(cursor.getColumnIndex("system")));
							} else {
								hit.setSystem(cursor.getString(cursor.getColumnIndex("locationName")));
							}
							fragmentID = cursor.getLong(cursor.getColumnIndex("constellationID"));
							if (fragmentID > 0) {
								hit.setConstellationID(fragmentID);
								hit.setConstellation(cursor.getString(cursor.getColumnIndex("constellation")));
							}
							fragmentID = cursor.getLong(cursor.getColumnIndex("regionID"));
							if (fragmentID > 0) {
								hit.setRegionID(fragmentID);
								hit.setRegion(cursor.getString(cursor.getColumnIndex("region")));
							}
							hit.setTypeID(cursor.getInt(cursor.getColumnIndex("typeID")));
							hit.setStation(cursor.getString(cursor.getColumnIndex("locationName")));
							hit.setLocationID(cursor.getLong(cursor.getColumnIndex("locationID")));
							hit.setSecurity(cursor.getString(cursor.getColumnIndex("security")));
							cursor.close();
						}
						if (!detected) {
							AndroidDatabaseConnector.logger
									.info("-- [searchLocationbyID]> Location: " + locationID + " not found on CCP data.");
							hit.setSystem("ID>" + Long.valueOf(locationID).toString());
						}
					}
				} catch (final Exception ex) {
					AndroidDatabaseConnector.logger.warning(
							"W- [AndroidDatabaseConnector.searchLocationbyID]> Location <" + fixedLocationID + "> not found.");
				}
				// If the location is not cached nor in the CCP database. Return default location
				return hit;
			}
			return locationList.get(0);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			return new EveLocation(locationID);
		}
		//		return new EveLocation(locationID);

		//		loc= new EveLocation(locationID);

		//		EveLocation hit = new EveLocation(locationID);
		//		// Check if a corporation hangar. ID > 66000000
		//		if (locationID > 66000000) {
		//			locationID -= 6000001;
		//		}
		//		try {
		//			final Cursor cursor = getCCPDatabase().rawQuery(SELECT_LOCATIONBYID,
		//					new String[] { Long.valueOf(locationID).toString() });
		//			if (null != cursor) {
		//				boolean detected = false;
		//				if (cursor.moveToFirst()) {
		//					detected = true;
		//					// Check returned values when doing the assignments.
		//					long fragmentID = cursor.getLong(cursor.getColumnIndex("systemID"));
		//					if (fragmentID > 0) {
		//						hit.setSystemID(fragmentID);
		//						hit.setSystem(cursor.getString(cursor.getColumnIndex("system")));
		//					} else {
		//						hit.setSystem(cursor.getString(cursor.getColumnIndex("locationName")));
		//					}
		//					fragmentID = cursor.getLong(cursor.getColumnIndex("constellationID"));
		//					if (fragmentID > 0) {
		//						hit.setConstellationID(fragmentID);
		//						hit.setConstellation(cursor.getString(cursor.getColumnIndex("constellation")));
		//					}
		//					fragmentID = cursor.getLong(cursor.getColumnIndex("regionID"));
		//					if (fragmentID > 0) {
		//						hit.setRegionID(fragmentID);
		//						hit.setRegion(cursor.getString(cursor.getColumnIndex("region")));
		//					}
		//					hit.setTypeID(cursor.getInt(cursor.getColumnIndex("typeID")));
		//					hit.setStation(cursor.getString(cursor.getColumnIndex("locationName")));
		//					hit.setLocationID(cursor.getLong(cursor.getColumnIndex("locationID")));
		//					hit.setSecurity(cursor.getString(cursor.getColumnIndex("security")));
		//					cursor.close();
		//				}
		//				if (!detected) {
		//					// Search the location on the list of outposts.
		//					hit = searchOutpostbyID(locationID);
		//				}
		//			}
		//		} catch (final Exception ex) {
		//			logger.warning("Location <" + locationID + "> not found.");
		//		}
		//		return hit;
	}

	public EveLocation searchLocationBySystem(final String name) {
		final EveLocation newLocation = new EveLocation();
		try {
			final Cursor cursor = this.getCCPDatabase()
					.rawQuery("SELECT solarSystemID from mapSolarSystems WHERE solarSystemName = ?", new String[] { name });
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int locationID = cursor.getInt(cursor.getColumnIndex("solarSystemID"));
					cursor.close();
					return this.searchLocationbyID(locationID);
				}
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.warning("W> Location <" + name + "> not found.");
		}
		return newLocation;
	}

	/**
	 * Search for this data on the cache. <br>
	 * The cache used for the search depends on the side parameter received on the call. All default prices are
	 * references to the cost of the price to be spent to buy the item.<br>
	 * If not found on the memory cache then try to load from the serialized version stored on disk. In case
	 * this action also fails return an empty data structure with the minimum filed data information from the
	 * related item and fire the update request.
	 * 
	 * @param itemID
	 *          item id code of the item assigned to this market request.
	 * @param side
	 *          differentiates if we like to BUY or SELL the item.
	 * @return the cached data or an empty locator ready to receive downloaded data.
	 */
	public MarketDataSet searchMarketData(final int itemID, final EMarketSide side) {
		// Log.i("EVEI","-- MarketUpdaterService.searchMarketDataByID. Searching
		// for Market Data: " + itemID + " - " + side);
		// Search on the cache. By default load the SELLER as If I am buying the
		// item.
		SparseArray<MarketDataSet> cache = sellMarketDataCache;
		if (side == EMarketSide.BUYER) {
			cache = buyMarketDataCache;
		}
		MarketDataSet entry = cache.get(itemID);
		if (null == entry) {
			// Try to get the data from disk.
			entry = AppConnector.getStorageConnector().readDiskMarketData(itemID, side);
			if (null == entry) {
				// Neither on disk. Make a request for download and return a
				// dummy placeholder.
				entry = new MarketDataSet(itemID, side);
				if (true) {
					NeoComApp.getTheCacheConnector().addMarketDataRequest(itemID);
				}
			}
			cache.put(itemID, entry);
		} else {
			Log.i("EVEI", "-- MarketUpdaterService.searchMarketDataByID. Cache hit on memory.");
			// Check again the location. If is the default then request a new
			// update and remove it from the cache.
			long lid = entry.getBestMarket().getLocation().getID();
			if (lid < 0) {
				NeoComApp.getTheCacheConnector().addMarketDataRequest(itemID);
				cache.put(itemID, null);
			}
		}
		// Check entry timestamp before return. Post an update if old.
		// if (side.equalsIgnoreCase(ModelWideConstants.marketSide.CALCULATE))
		// return entry;
		// else {
		if (AppConnector.checkExpiration(entry.getTS(), ModelWideConstants.HOURS2)) if (true) {
			NeoComApp.getTheCacheConnector().addMarketDataRequest(itemID);
		}
		return entry;
		// }
	}

	public int searchModule4Blueprint(final int bpitemID) {
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			final Cursor cursor = ccpDatabase.rawQuery(
					"SELECT productTypeID FROM industryActivityProducts BT WHERE typeID = ? AND activityID = 1",
					new String[] { Integer.valueOf(bpitemID).toString() });
			if (null != cursor) {
				int productTypeID = -1;
				while (cursor.moveToNext()) {
					productTypeID = cursor.getInt(cursor.getColumnIndex("productTypeID"));
				}
				cursor.close();
				return productTypeID;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error locating module for BPO <" + bpitemID + ">.");
		}
		return -1;
	}

	public int searchReactionOutputMultiplier(final int itemID) {
		int multiplier = 200;
		AppConnector.startChrono();
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			Cursor cursor = ccpDatabase.rawQuery(AndroidDatabaseConnector.REACTION_COMPONENTS,
					new String[] { Integer.valueOf(itemID).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// Search for the itemid at the reaction and then return the
					// multiplier.
					int materialTypeID = cursor.getInt(cursor.getColumnIndex("typeID"));
					if (materialTypeID == itemID) {
						multiplier = cursor.getInt(cursor.getColumnIndex("quantity"));
						// int input =
						// cursor.getInt(cursor.getColumnIndex("input"));
						// int qty =
						// cursor.getInt(cursor.getColumnIndex("quantity"));
						// // blueprintId =
						// cursor.getInt(cursor.getColumnIndex("typeID"));
						// // int consume =
						// cursor.getInt(cursor.getColumnIndex("consume"));
						// if (input == 1) {
						// Resource resource = new Resource(materialTypeID,
						// qty);
						// buildJob.add(resource);
						// }
					}
				}
				cursor.close();
				// // We have collected the required blueprint. Add it to the
				// list of resources.
				// if (blueprintId != -1) buildJob.add(new Resource(blueprintId,
				// 1));
				//
				// // Add the skills to the list of resources
				// cursor = ccpDatabase
				// .rawQuery(
				// "SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM
				// industryActivitySkills ais, invTypes it WHERE ais.typeID = ?
				// AND ais.activityID = 1 AND it.typeID=ais.skillID",
				// new String[] { Integer.valueOf(itemID).toString() });
				// if (null == cursor) throw new Exception("E> Invalid cursor or
				// empty.");
				// while (cursor.moveToNext()) {
				// // The the data of the resource. Check for blueprints.
				// int skillID =
				// cursor.getInt(cursor.getColumnIndex("skillID"));
				// int level = cursor.getInt(cursor.getColumnIndex("level"));
				// Resource resource = new Resource(skillID, level);
				// buildJob.add(resource);
				// }
				// cursor.close();

				// Log.i("AndroidDatabaseConnector", "<<
				// AndroidDatabaseConnector.searchListOfMaterials " +
				// buildJob.size());
				// return buildJob;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
		Log.i("DBQUERY.TIME",
				"~~ Time lapse for [SELECT LOM " + itemID + "] - [" + multiplier + "] " + AppConnector.timeLapse());
		// Log.i("AndroidDatabaseConnector", "<<
		// AndroidDatabaseConnector.searchListOfMaterials " + buildJob.size());
		return multiplier;
	}

	/**
	 * Returns the resource identifier of the station class to locate icons or other type related resources.
	 * 
	 * @param stationID
	 * @return
	 */
	public int searchStationType(final long stationID) {
		ArrayList<Resource> inventionJob = new ArrayList<Resource>();
		int stationTypeID = 1529;
		AppConnector.startChrono();
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(AndroidDatabaseConnector.STATIONTYPE,
					new String[] { Long.valueOf(stationID).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					stationTypeID = cursor.getInt(cursor.getColumnIndex("stationTypeID"));
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for station type <" + stationID + "> not found.");
		}
		// Log.i("AndroidDatabaseConnector", "<<
		// AndroidDatabaseConnector.searchListOfDatacores " +
		// inventionJob.getSize());
		Log.i("DBQUERY.TIME", "~~ Time lapse for [SELECT STATIONTYPEID " + stationID + "] " + AppConnector.timeLapse());
		return stationTypeID;
	}

	public String searchTech4Blueprint(final int blueprintID) {
		try {
			if (null == ccpDatabase) {
				ccpDatabase = this.getCCPDatabase();
			}
			final Cursor cursor = ccpDatabase.rawQuery(AndroidDatabaseConnector.TECH4BLUEPRINT,
					new String[] { Integer.valueOf(blueprintID).toString() });
			if (null != cursor) {
				String productTypeID = ModelWideConstants.eveglobal.TechI;
				while (cursor.moveToNext()) {
					productTypeID = cursor.getString(3);
				}
				cursor.close();
				return productTypeID;
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error locating BPO for module <" + blueprintID + ">.");
		}
		return ModelWideConstants.eveglobal.TechI;
	}

	private boolean checkRecordExistence(final int typeID, final String query) {
		int count = 0;
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(query, new String[] { Integer.valueOf(typeID).toString() });
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					count = cursor.getInt(0);
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			Log.w("EVEI", "W> AndroidDatabaseConnector.checkRecordExistence -- Exception:" + ex.getMessage());
			return false;
		}
		if (count > 0)
			return true;
		else
			return false;
	}

	private SQLiteDatabase getAppDatabase() {
		if (null == staticDatabase) {
			this.openAppDataBase();
		}
		return staticDatabase;
	}

	private SQLiteDatabase getCCPDatabase() {
		if (null == ccpDatabase) {
			this.openCCPDataBase();
		}
		return ccpDatabase;
	}

	private String readJsonData() {
		StringBuffer data = new StringBuffer();
		try {
			String str = "";
			InputStream is = AppConnector.getStorageConnector().accessInternalStorage("outposts.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is != null) {
				while ((str = reader.readLine()) != null) {
					data.append(str);
				}
			}
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data.toString();
	}

	private EveLocation searchOutpostbyID(final long locationID) {
		// Check if the outpotst already loaded.
		if ((null == outpostsCache) || (outpostsCache.size() < 1)) {
			// Making a request to url and getting response
			String jsonStr = this.readJsonData();
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				// Getting JSON Array node
				JSONArray outposts = jsonObj.getJSONArray("items");
				// Looping through all outposts
				int counter = 1;
				for (int i = 0; i < outposts.length(); i++) {
					Outpost o = new Outpost();
					JSONObject post = outposts.getJSONObject(i);
					int id = post.getInt("facilityID");
					o.setFacilityID(id);
					JSONObject intermediate = post.getJSONObject("solarSystem");
					o.setSolarSystem(intermediate.getLong("id"));
					o.setName(post.getString("name"));
					intermediate = post.getJSONObject("region");
					o.setRegion(intermediate.getLong("id"));
					intermediate = post.getJSONObject("owner");
					o.setOwner(intermediate.getLong("id"));
					intermediate = post.getJSONObject("type");
					o.setType(intermediate.getLong("id"));

					// Create the part with the Outpost
					outpostsCache.put(id, o);
					Log.i("DataSource", ".. Part counter " + counter++);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// Search for the item
		Outpost hit = outpostsCache.get(Long.valueOf(locationID).intValue());
		EveLocation location = new EveLocation(locationID);
		if (null != hit) {
			EveLocation systemLocation = this.searchLocationbyID(hit.getSolarSystem());
			location.setStation(hit.getName());
			location.setSystemID(hit.getSolarSystem());
			location.setSystem(systemLocation.getSystem());
			location.setConstellationID(systemLocation.getConstellationID());
			location.setConstellation(systemLocation.getConstellation());
			location.setRegionID(systemLocation.getRegionID());
			location.setRegion(systemLocation.getRegion());
			location.setSecurity(systemLocation.getSecurity());
		}
		return location;
	}

}

// - UNUSED CODE ............................................................................................
