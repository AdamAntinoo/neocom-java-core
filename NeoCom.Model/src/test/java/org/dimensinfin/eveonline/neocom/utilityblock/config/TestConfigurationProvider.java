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
package org.dimensinfin.eveonline.neocom.utilityblock.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.BeforeClass;

import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;

public class TestConfigurationProvider implements IConfigurationProvider {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final String DEFAULT_PROPERTIES_FOLDER = "src/test/resources/properties";

	// - F I E L D - S E C T I O N ............................................................................
	private Properties globalConfigurationProperties = new Properties();
	private String configuredPropertiesFolder = DEFAULT_PROPERTIES_FOLDER;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TestConfigurationProvider( final String propertiesFolder ) {
		super();
		if (null == propertiesFolder) configuredPropertiesFolder = DEFAULT_PROPERTIES_FOLDER;
		else configuredPropertiesFolder = propertiesFolder;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getResourceString( final String key ) {
		try {
			return globalConfigurationProperties.getProperty(key);
		} catch (MissingResourceException mre) {
			return '!' + key + '!';
		}
	}

	public String getResourceString( final String key, final String defaultValue ) {
		try {
			return globalConfigurationProperties.getProperty(key, defaultValue);
		} catch (MissingResourceException mre) {
			return '!' + key + '!';
		}
	}

	/**
	 * This initialization method reads all the files located on a predefined folder under the src/main/resources path.
	 * All the files are expected to be Properties files and are read in alphabetical order and their contents added
	 * to the list of application properties. Read order will replace same ids with new data so the developer
	 * can use a naming convention to replace older values with new values without editing the older files.
	 */
	public IConfigurationProvider initialize() {
		try {
			readAllProperties();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return this;
	}

	private void readAllProperties() throws IOException {
		// Read all .properties files under the predefined path on the /resources folder.
		Path propertiesPath = FileSystems.getDefault().getPath(configuredPropertiesFolder);
		Stream<Path> paths = Files.walk(propertiesPath);
		paths.filter(Files::isRegularFile)
				.filter(Files::isReadable)
				.filter(path -> path.toString().endsWith(".properties"))
				.forEach(( fileName ) -> {
					try {
						Properties properties = new Properties();
						properties.load(new FileInputStream(fileName.toString()));
						// Copy poperties to globals.
						globalConfigurationProperties.putAll(properties);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				});
	}
}
// - UNUSED CODE ............................................................................................
//[01]
