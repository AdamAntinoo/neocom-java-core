//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComDatabaseConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComDatabaseConnector");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComDatabaseConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Returns the list of distinct identifiers for parentAssetId that should represent the container where an
	 * asset is located. That list can represent known Locations, Assets and unknown locations that should
	 * represent other Corporation assets like the Customs or not listed Space Structures.
	 */
	public List<NeoComAsset> queryAllAssetContainers(final long identifier) {
		// Get access to one assets with a distinct location. Discard the rest of the data and only process the Location id
		List<NeoComAsset> uniqueContainers = new Vector<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = this.getAssetDAO();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder().distinct()
					.selectColumns("parentAssetID");
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", identifier);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			uniqueContainers = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			NeoComDatabaseConnector.logger
					.warning("W [SpringDatabaseConnector.queryAllLogins]> Excpetion reading all Logins" + sqle.getMessage());
		}
		return uniqueContainers;
	}
}

// - UNUSED CODE ............................................................................................
