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
package org.dimensinfin.eveonline.neocom.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public abstract class GlobalConfigurationProvider implements IConfigurationProvider {
	// - S T A T I C - S E C T I O N ..........................................................................
	protected static Logger logger = LoggerFactory.getLogger(GlobalConfigurationProvider.class);
	private static final String DEFAULT_PROPERTIES_FOLDER = "properties";

	// - F I E L D - S E C T I O N ............................................................................
	protected Properties globalConfigurationProperties = new Properties();
	private String configuredPropertiesFolder = DEFAULT_PROPERTIES_FOLDER;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GlobalConfigurationProvider( final String propertiesFolder ) {
		if (null != propertiesFolder) configuredPropertiesFolder = propertiesFolder;
//		initialize();
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
	 * Ths initialization method reads all the files located on a predefined folder under the src/main/resources path.
	 * All the files are expected to be Properties files and are read in alphabetical order and their contents added
	 * to the list of application properties. Read order will replace same ids with new data so the developer
	 * can use a naming convention to replace older values with new values without editing the older files.
	 */
	public GlobalConfigurationProvider initialize() {
		try {
			readAllProperties();
		} catch (IOException ioe) {
			logger.error("E [GlobalConfigurationProvider.initialize]> Unprocessed exception: {}", ioe.getMessage());
			ioe.printStackTrace();
		}
		return this;
	}

	public int contentCount() {
		return globalConfigurationProperties.size();
	}

	protected String getResourceLocation() {
		return configuredPropertiesFolder;
	}

	@Override
	public String toString() {
		return new StringBuffer("GlobalConfigurationProvider[")
				.append("Property count: ").append(contentCount()).append(" ")
				.append("]")
				.toString();
	}

	//--- P L A T F O R M   S P E C I F I C   S E C T I O N
	protected abstract void readAllProperties() throws IOException;

	protected abstract List<String> getResourceFiles( String path ) throws IOException;
}
