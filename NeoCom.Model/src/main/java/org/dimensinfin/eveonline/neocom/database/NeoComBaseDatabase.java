//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionalities than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComBaseDatabase {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComBaseDatabase");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Get the complete list of assets that are Planetary Materials.
	 * 
	 * @return
	 */
	public ArrayList<NeoComAsset> accessAllPlanetaryAssets(final long characterID) {
		// Select assets for each one of the Planetary categories.
		ArrayList<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		assetList.addAll(this.searchAsset4Category(characterID, "Planetary Commodities"));
		assetList.addAll(this.searchAsset4Category(characterID, "Planetary Resources"));
		return assetList;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
//	/**
//	 * Reads all the keys stored at the database and classified them into a set of Login names.
//	 */
//	public Hashtable<String, Login> queryAllLogins() {
//		// Get access to all ApiKey registers
//		List<ApiKey> keyList = new Vector<ApiKey>();
//		try {
//			Dao<ApiKey, String> keysDao = ModelAppConnector.getSingleton().getDBConnector().getApiKeysDao();
//			QueryBuilder<ApiKey, String> queryBuilder = keysDao.queryBuilder();
//			PreparedQuery<ApiKey> preparedQuery = queryBuilder.prepare();
//			keyList = keysDao.query(preparedQuery);
//		} catch (java.sql.SQLException sqle) {
//			sqle.printStackTrace();
//			NeoComBaseDatabase.logger
//					.warning("W [NeoComBaseDatabase.queryAllLogins]> Exception reading all Logins. " + sqle.getMessage());
//		}
//		// Classify the keys on they matching Logins.
//		Hashtable<String, Login> loginList = new Hashtable<String, Login>();
//		for (ApiKey apiKey : keyList) {
//			String name = apiKey.getLogin();
//			// Search for this on the list before creating a new Login.
//			Login hit = loginList.get(name);
//			if (null == hit) {
//				Login login = new Login(name).addKey(apiKey);
//				loginList.put(name, login);
//			} else {
//				hit.addKey(apiKey);
//			}
//		}
//		return loginList;
//	}

	/**
	 * Gets the list of assets of a select Category.
	 * 
	 * @param characterID
	 * @param categoryName
	 * @return
	 */
	public ArrayList<NeoComAsset> searchAsset4Category(final long characterID, final String categoryName) {
		// Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("category", categoryName);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	/**
	 * Gets the assets located at an specific position by checking the pilot identifier and the asset reference
	 * to a location stored at the <code>locationID</code> column value. We also filter out the assets that even
	 * are located at the searched place they are inside another asset, like ships or containers.
	 * 
	 * @param ownerid
	 * @param identifier
	 * @return
	 */
	public List<NeoComAsset> searchAssetsAtLocation(final long ownerid, final long identifier) {
		// Get access to one assets with a distinct location. Discard the rest of the data and only process the Location id
		List<NeoComAsset> contents = new Vector<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", ownerid);
			where.and().eq("locationID", identifier);
			where.and().eq("parentAssetId", -1);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			contents = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			NeoComBaseDatabase.logger.warning(
					"W [NeoComBaseDatabase.queryLocationContents]> Exception reading Location contents" + sqle.getMessage());
		}
		return contents;
	}

	/**
	 * Returns the number of items that are located at the specified location. There is another filter for the
	 * character owner of the assets.
	 * 
	 * @param identifier
	 * @return
	 */
	public int totalLocationContentCount(final long identifier) {
		try {
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDao();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			queryBuilder.setCountOf(true).where().eq("locationID", identifier);
			long totalAssets = assetDao.countOf(queryBuilder.prepare());
			return Long.valueOf(totalAssets).intValue();
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			NeoComBaseDatabase.logger
					.warning("W [NeoComBaseDatabase.getLocationContentCount]> Exception reading Location contents count."
							+ sqle.getMessage());
			return 0;
		}
	}
}

// - UNUSED CODE ............................................................................................
