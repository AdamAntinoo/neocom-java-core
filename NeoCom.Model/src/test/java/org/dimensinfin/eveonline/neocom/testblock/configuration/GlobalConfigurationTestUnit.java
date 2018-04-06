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
package org.dimensinfin.eveonline.neocom.testblock.configuration;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.Nullable;

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
public class GlobalConfigurationTestUnit extends GlobalConfigurationProvider {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalConfigurationTestUnit");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GlobalConfigurationTestUnit( @Nullable final String resourcePath ) {
		super(resourcePath);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void test01GetResourceFiles() throws IOException {
		logger.info(">> [GlobalConfigurationTestUnit.test01GetResourceFiles]");
		final GlobalConfigurationProvider configuration = new GlobalConfigurationProvider(null);
		final List<String> files = getResourceFiles("properties");
		Assert.assertNotNull("-> Validating the list of files is not null...", files);
		Assert.assertEquals("-> Validating the number of files to process...", 4, files.size());
		logger.info("<< [GlobalConfigurationTestUnit.test01GetResourceFiles]");
	}

	@Test
	public void test02ReadProperties() {
		logger.info(">> [GlobalConfigurationTestUnit.test02ReadProperties]");
		// Read all properties files on the classpath and consolidate into a unique list.
		final GlobalConfigurationProvider configuration = new GlobalConfigurationProvider(null);
		Assert.assertNotNull("-> Validating the configuration is not null...", configuration);
		Assert.assertEquals("-> Validating read configuration matches...", 12, configuration.contentCount());
		logger.info("<< [GlobalConfigurationTestUnit.test02ReadProperties]");
	}
}

// - UNUSED CODE ............................................................................................
//[01]
