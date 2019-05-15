package org.dimensinfin.eveonline.neocom.industry;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndustryTestUnit {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("IndustryTestUnit");

	//	@BeforeClass
	//	public static void before01OpenAndConnectDatabase() throws SQLException, IOException {
	//		logger.info(">> [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
	//		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting the Configuration Manager...");
	//		GlobalDataManager.connectConfigurationManager(new GlobalSBConfigurationProvider("testproperties"));
	//
	//		// Initialize the Model with the current global instance.
	//		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting Global to Model...");
	//		ANeoComEntity.connectGlobal(new GlobalDataManager());
	//
	//		// Initializing the ESI api network controller.
	//		ESINetworkManager.initialize();
	//
	//		// Connect the SDE database.
	//		logger.info("-- [NeoComMicroServiceApplication.main]> Connecting SDE database...");
	//		try {
	//			GlobalDataManager.connectSDEDBConnector(new SDESBDBHelper()
	//					.setDatabaseSchema(GlobalDataManager.getResourceString("R.database.sdedatabase.databaseschema"))
	//					.setDatabasePath(GlobalDataManager.getResourceString("R.database.sdedatabase.databasepath"))
	//					.setDatabaseName(GlobalDataManager.getResourceString("R.database.sdedatabase.databasename"))
	//					.build()
	//			);
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		ANeoComEntity.connectSDEHelper(new GlobalDataManager().getSDEDBHelper());
	//
	//		// Connect the NeoCom database.
	//		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting NeoCom private database...");
	//		try {
	//			GlobalDataManager.connectNeoComDBConnector(new NeoComSBDBHelper()
	//					.setDatabaseHost(GlobalDataManager.getResourceString("R.database.neocom.databasehost"
	//							, "jdbc:mysql://localhost:3306"))
	//					.setDatabaseName("neocom")
	//					.setDatabaseUser(GlobalDataManager.getResourceString("R.database.neocom.databaseuser"
	//							, "NEOCOM"))
	//					.setDatabasePassword(GlobalDataManager.getResourceString("R.database.neocom.databasepassword"))
	//					.setDatabaseVersion(GlobalDataManager.getResourceInt("R.database.neocom.databaseversion"))
	//					.build()
	//			);
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	////		ANeoComEntity.connectNeoComHelper(new GlobalDataManager().getNeocomDBHelper());
	//
	//		// Load the Locations cache to speed up the Citadel and Outpost search.
	//		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Read Locations data cache...");
	//		GlobalDataManager.readLocationsDataCache();
	//
	//		// Check the connection descriptor.
	//		Assert.assertEquals("-> Validating the database is valid..."
	//				, new GlobalDataManager().getNeocomDBHelper().isDatabaseValid()
	//				, true);
	//		// Check the database is open and has a valid connection.
	//		Assert.assertEquals("-> Validating the database is open..."
	//				, new GlobalDataManager().getNeocomDBHelper().isOpen()
	//				, true);
	//
	////		// Get a testing credential.
	////		final List<Credential> credentials = new GlobalDataManager().getNeocomDBHelper().getCredentialDao().queryForAll();
	////		for (Credential c : credentials) {
	////			if (c.getAccountId() == 92002067) testCredential = c;
	////		}
	//		logger.info("<< [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
	//	}
	// - F I E L D - S E C T I O N ............................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void test01Resource() {
		logger.info(">> [IndustryTestUnit.test01Resource]");
		// Test the proper creation of a resource from type identifier.
		final int typeId = 578;
		final int quantity = 10;
		final Resource testResource = new Resource(typeId, quantity);
		logger.info(">> [IndustryTestUnit.test01Resource]-> Validating we get a Resource...");
		Assert.assertNotNull("-> Validating we get a Resource...", testResource);
		logger.info(">> [IndustryTestUnit.test01Resource]-> Validating Resource contents. Resource type...");
		Assert.assertEquals("-> Validating Resource contents. Resource type...", typeId, testResource.getTypeId());
		logger.info("<< [IndustryTestUnit.test01Resource]");
	}
}

// - UNUSED CODE ............................................................................................
//[01]
