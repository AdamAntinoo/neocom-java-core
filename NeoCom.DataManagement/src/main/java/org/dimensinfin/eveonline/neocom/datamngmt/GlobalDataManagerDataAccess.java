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
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.Job;
import org.dimensinfin.eveonline.neocom.database.entity.MarketOrder;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerDataAccess extends GlobalDataManagerNetwork {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerDataAccess");

	// --- N E O C O M   P R I V A T E   D A T A B A S E   S E C T I O N
	/**
	 * Reference to the NeoCom persistence database Dao provider. This filed should be injected on startup.
	 */
	private static INeoComDBHelper neocomDBHelper = null;

	public INeoComDBHelper getNeocomDBHelper() {
		if (null == neocomDBHelper)
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
		return neocomDBHelper;
	}

	public static void connectNeoComDBConnector( final INeoComDBHelper newhelper ) {
		if (null != newhelper) neocomDBHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
	}

	/**
	 * Reads all the list of credentials stored at the Database and returns them. Activation depends on the
	 * interpretation used by the application.
	 */
	public static List<Credential> accessAllCredentials() {
		List<Credential> credentialList = new ArrayList<>();
		try {
//			final Dao<Credential, String> credentialDao = GlobalDataManager.getNeocomDBHelper().getCredentialDao();
//			final PreparedQuery<Credential> preparedQuery = credentialDao.queryBuilder().prepare();
//			credentialList = credentialDao.query(preparedQuery);
			credentialList = new GlobalDataManager().getNeocomDBHelper().getCredentialDao().queryForAll();
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManagerDataAccess.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;
	}

	public static List<Job> accessIndustryJobs4Credential( final Credential credential ) throws SQLException {
		return new GlobalDataManager().getNeocomDBHelper().getJobDao()
				.queryForEq("ownerId", credential.getAccountId());
	}

	public static List<MarketOrder> accessMarketOrders4Credential( final Credential credential ) throws SQLException {
		return new GlobalDataManager().getNeocomDBHelper().getMarketOrderDao()
				.queryForEq("ownerId", credential.getAccountId());
	}
}

// - UNUSED CODE ............................................................................................
//[01]
