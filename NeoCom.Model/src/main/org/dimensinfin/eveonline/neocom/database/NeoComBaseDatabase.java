//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.database;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.Login;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComBaseDatabase {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComBaseDatabase");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Reads all the keys stored at the database and classified them into a set of Login names.
	 */
	public Hashtable<String, Login> queryAllLogins() {
		// Get access to all ApiKey registers
		List<ApiKey> keyList = new Vector<ApiKey>();
		try {
			Dao<ApiKey, String> keysDao = ModelAppConnector.getSingleton().getDBConnector().getApiKeysDao();
			QueryBuilder<ApiKey, String> queryBuilder = keysDao.queryBuilder();
			PreparedQuery<ApiKey> preparedQuery = queryBuilder.prepare();
			keyList = keysDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			NeoComBaseDatabase.logger
					.warning("W [SpringDatabaseConnector.queryAllLogins]> Exception reading all Logins. " + sqle.getMessage());
		}
		// Classify the keys on they matching Logins.
		Hashtable<String, Login> loginList = new Hashtable<String, Login>();
		for (ApiKey apiKey : keyList) {
			String name = apiKey.getLogin();
			// Search for this on the list before creating a new Login.
			Login hit = loginList.get(name);
			if (null == hit) {
				Login login = new Login(name).addKey(apiKey);
				loginList.put(name, login);
			} else {
				hit.addKey(apiKey);
			}
		}
		return loginList;
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
			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			queryBuilder.setCountOf(true).where().eq("locationID", identifier);
			long totalAssets = assetDao.countOf(queryBuilder.prepare());
			return Long.valueOf(totalAssets).intValue();
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			NeoComBaseDatabase.logger
					.warning("W [SpringDatabaseConnector.getLocationContentCount]> Exception reading Location contents count."
							+ sqle.getMessage());
			return 0;
		}
	}
}

// - UNUSED CODE ............................................................................................
