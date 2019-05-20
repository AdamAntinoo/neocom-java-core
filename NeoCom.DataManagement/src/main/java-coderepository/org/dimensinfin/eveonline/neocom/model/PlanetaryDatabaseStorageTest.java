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
package org.dimensinfin.eveonline.neocom.testblock;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.manager.PlanetaryManager;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.Colony;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Test case to validate the storage of the Planetary Data into the database as a json serialization string and that
 * the later recuperation from the database returns exactly the same instance structure.
 * The test is going to be executed with some Planetary data because that is a set of data that does not have an
 * homogeneous structure and requires of serialization.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryDatabaseStorageTest {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(PlanetaryDatabaseStorageTest.class);

	// - F I E L D - S E C T I O N ............................................................................
//	private NeoComDBHelper helper;
	private ModelAppConnector modelConnector;
	private Credential testingCredential;
	private PlanetaryManager planetaryManager;
	private List<Colony> colonies;
	private Colony targetColony;
	private String targetColonyJson;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Those are the code to be executed just before running the test. Because the database is related to this test this
	 * is the place where I should initialize all data, like OAuth elements, keys, credentials and database
	 * tables.
	 */
//	@Before
	public void setUp () throws Exception {
		// Step 01. Create the connectors amd implementers required to complete the test.
		// The first task is to connect the Runtime instances to a functionality provider.
//		modelConnector = new ModelAppConnector(new ModelTestConnectorProvider());
		// Create the Database helper for the NeoCom database.
//		helper = new NeoComDBHelper()
//				.setDatabaseHost("jdbc:mysql://localhost:3306")
//				.setDatabaseName("neocom")
//				.setDatabaseUser("NEOCOMTEST")
//				.setDatabasePassword("01.Alpha")
//				.setDatabaseVersion(1)
//				.build();
//		NeoComDatabase.setImplementer(ModelAppConnector.getSingleton().getNewDBConnector());
//		NeoComDatabase.openDatabase();

		// Read all the Credentials and keep the first one for testing.
		final List<Credential> credentials = DataManagementModelStore.accessCredentialList();
		if ( credentials.size() > 0 ) {
			testingCredential = credentials.get(0);
		} else {
			testingCredential=createTestCredential();
	//		throw new Exception("No valid credential found on the request to get the list of credentials.");
		}
		// Get access to the Planetary Manager to then access the list of Colonies.
//		planetaryManager = ManagerStore.getPlanetaryManager(testingCredential, true);
		colonies = planetaryManager.accessAllColonies();
		if ( null != colonies ) {
			targetColony = colonies.get(0);
		} else throw new Exception("No valid Colony from the list of colonies from this Credential.");
	}

	@Test
	public void testColonySerialization () throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		targetColonyJson = objectMapper.writeValueAsString(targetColony);

	}

//	@Test
	public Credential createTestCredential () throws Exception {
		final int newAccountIdentifier = 92002067;
		final Credential credential = new Credential(newAccountIdentifier)
//				.setAccountId(92223647)
				.setAccountName("Beth Ripley")
				.setActive(true)
				.setAccessToken("l503NeX7tjKwbwXfKeEQCibp8v8KknuIldR09FxhOjH3PaC4Tpsoa9yJV7Zi284JjNleq0JLzk4tNZmHGJ1P0w2")
				.setRefreshToken("rILzXqFN4C8aYKr2bY6rr-tvIcRuD3m_Qk5kHeedCn3aIV2vj3JjmucYgp_7TIeoS0wKEfIKs7vAhgopGAlIRQ7R47S1cuPHEVkILck-NQWPZH10Betk972YcFjHZVGO6IjbY-WjtFwsmGKgkBxaf67LgLTt5x_zsK1uRvURyYLMMA0Eh1bWyCStpo2Wg7evW1c79uBiEYaEP9OYpmfrXOKbzGFYb8U5McM7rr0o5lyeYlr4-H4xFG8yVe31b0d4PMlOPCQInENnGcd9-epl3QahYJYvpdkZtocVSC67JV9Q60-vgoTD9D6ZVoO4Iic_Hmm7i5OueCJctqupuV5G8Jja9M0kP6y7a3dl3WKFQUiTZs49X1YgqJPePVD--KvgoUXiwHTZQlQEiqZ2mWVsUQIrlD-7JAzCBQ1Fl89sCQ-iZ8RMUU6OtyDZqrMoQfSYdbuazovF4ueo2lX_PqA71NkL6GT-H_fUiKeurr35yLAU1vfphzckltF9gf7LC7cKuEiGJ14dpE20uLQU7zkzz2jF-zQpjdo61HNk8LXMXbERBhT4imtEC9V8msbsVPn4z04AInFvvNEwdlc2GLRJsoH8xNWWXPsuBIFGGsHiMUs1")
				.setTokenType("Bearer")
				.setExpires(0)
				.store();
		return credential;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("${NAME} [");
		//		buffer.append("name: ").append(0);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
