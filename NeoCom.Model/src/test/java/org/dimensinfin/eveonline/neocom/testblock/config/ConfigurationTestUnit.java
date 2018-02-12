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
package org.dimensinfin.eveonline.neocom.testblock.config;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.utilityblock.config.TestConfigurationProvider;

// - CLASS IMPLEMENTATION ...................................................................................
public class ConfigurationTestUnit {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("SDEDatabaseTestUnit");
	private static IConfigurationProvider configurationProvider = null;
	@BeforeClass
	public static void prepareInitialization() {
		configurationProvider = new TestConfigurationProvider("src/test/resources/properties")
				.initialize();
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void testGetResourceString() {
		String testData = configurationProvider.getResourceString("R.string.testValue");
		Assert.assertEquals(testData, "TEST");
		testData = configurationProvider.getResourceString("R.number.testValue");
		Assert.assertEquals(testData, "1234");
	}
}
