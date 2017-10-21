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

import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComBaseDatabase {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComBaseDatabase");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public NeoComBaseDatabase() {
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
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
