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
package org.dimensinfin.eveonline.neocom.processor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.NeoComSBDBHelper;
import org.dimensinfin.eveonline.neocom.SDESBDBHelper;
import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.industry.Action;
import org.dimensinfin.eveonline.neocom.industry.EveTask;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FittingProcessorTestUnit extends FittingProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("FittingProcessorTestUnit");
	private static Credential testCredential;

	@BeforeClass
	public static void before01OpenAndConnectDatabase() throws SQLException {
		logger.info(">> [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting the Configuration Manager...");
		GlobalDataManager.connectConfigurationManager(new GlobalConfigurationProvider("testproperties"));

		// Initialize the Model with the current global instance.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting Global to Model...");
		ANeoComEntity.connectGlobal(new GlobalDataManager());

		// Initializing the ESI api network controller.
		ESINetworkManager.initialize();

		// Connect the SDE database.
		logger.info("-- [NeoComMicroServiceApplication.main]> Connecting SDE database...");
		try {
			GlobalDataManager.connectSDEDBConnector(new SDESBDBHelper()
					.setDatabaseSchema(GlobalDataManager.getResourceString("R.database.sdedatabase.databaseschema"))
					.setDatabasePath(GlobalDataManager.getResourceString("R.database.sdedatabase.databasepath"))
					.setDatabaseName(GlobalDataManager.getResourceString("R.database.sdedatabase.databasename"))
					.build()
			);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		ANeoComEntity.connectSDEHelper(new GlobalDataManager().getSDEDBHelper());

		// Connect the NeoCom database.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting NeoCom private database...");
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
		ANeoComEntity.connectNeoComHelper(new GlobalDataManager().getNeocomDBHelper());

		// Load the Locations cache to speed up the Citadel and Outpost search.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Read Locations data cache...");
		GlobalDataManager.readLocationsDataCache();

		// Check the connection descriptor.
		Assert.assertEquals("-> Validating the database is valid..."
				, new GlobalDataManager().getNeocomDBHelper().isDatabaseValid()
				, true);
		// Check the database is open and has a valid connection.
		Assert.assertEquals("-> Validating the database is open..."
				, new GlobalDataManager().getNeocomDBHelper().isOpen()
				, true);

		// Get a testing credential.
		final List<Credential> credentials = new GlobalDataManager().getNeocomDBHelper().getCredentialDao().queryForAll();
		for (Credential c : credentials) {
			if (c.getAccountId() == 92002067) testCredential = c;
		}
		logger.info("<< [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
	}

	// - F I E L D - S E C T I O N ............................................................................
	protected transient final HashMap<Integer, Action> actionsRegistered = new HashMap<Integer, Action>();
	protected transient Action currentAction = null;

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void test01BuyProcessing() {
		logger.info(">> [FittingProcessorTestUnit.test01BuyProcessing]");
		// Test the processing ofr an Item that should render a BUY.
		final int typeId = 12745;
		final int quantity = 1;
		final Resource resource = new Resource(typeId, quantity);
		logger.info("-- [FittingProcessor.processFitting]> Processing resource: {}", resource);
		currentAction = new Action(resource);
		EveTask newTask = new EveTask(Action.ETaskType.REQUEST, resource);
		newTask.setQty(resource.getQuantity());
		// We register the action before to get erased on restarts. This has no impact on data since we use pointers to the
		// global structures.
		this.setCredential(testCredential);
		actions4Item = getPilotActions(testCredential);
		this.registerAction(currentAction);
		this.processRequest(newTask);
		logger.info("<< [FittingProcessorTestUnit.test01BuyProcessing]");
	}
}

// - UNUSED CODE ............................................................................................
//[01]
