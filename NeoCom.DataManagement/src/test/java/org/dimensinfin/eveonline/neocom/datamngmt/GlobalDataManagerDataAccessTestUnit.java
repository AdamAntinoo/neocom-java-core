//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.GlobalSBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.NeoComSBDBHelper;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalDataManagerDataAccessTestUnit {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerDataAccessTestUnit");

	@BeforeClass
	public static void before01OpenAndConnectDatabase() throws SQLException, IOException {
		logger.info(">> [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]");
		logger.info("-- [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]> Connecting the Configuration Manager...");
		GlobalDataManager.connectConfigurationManager(new GlobalSBConfigurationProvider("testproperties"));

		// Initialize the Model with the current global instance.
		logger.info("-- [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]> Connecting Global to Model...");
		ANeoComEntity.connectGlobal(new GlobalDataManager());

		// Initializing the ESI api network controller.
		ESINetworkManager.initialize();

		// Connect the NeoCom database.
		logger.info("-- [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]> Connecting NeoCom private database...");
		try {
			GlobalDataManager.connectNeoComDBConnector(new NeoComSBDBHelper()
					.setDatabaseHost(GlobalDataManager.getResourceString("R.database.neocom.databasehost"
							, "jdbc:mysql://localhost:3306"))
					.setDatabaseName("neocom")
					.setDatabaseUser(GlobalDataManager.getResourceString("R.database.neocom.databaseuser"
							, "NEOCOM"))
					.setDatabasePassword(GlobalDataManager.getResourceString("R.database.neocom.databasepassword"))
					.setDatabaseVersion(GlobalDataManager.getResourceInt("R.database.neocom.databaseversion"))
					.build()
			);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		// Load the Locations cache to speed up the Citadel and Outpost search.
		logger.info("-- [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]> Read Locations data cache...");
		GlobalDataManager.readLocationsDataCache();

		// Check the connection descriptor.
		Assert.assertEquals("-> Validating the database is valid..."
				, new GlobalDataManager().getNeocomDBHelper().isDatabaseValid()
				, true);
		// Check the database is open and has a valid connection.
		Assert.assertEquals("-> Validating the database is open..."
				, new GlobalDataManager().getNeocomDBHelper().isOpen()
				, true);
		logger.info("<< [GlobalDataManagerDataAccessTestUnit.before01OpenAndConnectDatabase]");
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void test01CredentialList() {
		logger.info(">> [GlobalDataManagerDataAccessTestUnit.test01CredentialList]");
		final List<Credential> credentials = GlobalDataManager.accessAllCredentials();
		logger.info("<< [GlobalDataManagerDataAccessTestUnit.test01CredentialList]");
	}

	@Test
	public void test02AssetList() throws SQLException {
		logger.info(">> [GlobalDataManagerDataAccessTestUnit.test02AssetList]");
		final List<Credential> credentials = GlobalDataManager.accessAllCredentials();
		for (Credential cred : credentials) {
			final List<NeoComAsset> assets = GlobalDataManager.accessAllAssets4Credential(cred);
		}
		logger.info("<< [GlobalDataManagerDataAccessTestUnit.test02AssetList]");
	}
}

// - UNUSED CODE ............................................................................................
//[01]
