package org.dimensinfin.eveonline.neocom.conf;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads all the properties files found under a configurable place. The class scans for all files that end with '.properties'
 * and reads the name/value pairs into a accessible cache.
 *
 * Then exports methods to read that property entries as strings. If there is a need to use other type values the caller should
 * do the conversions.
 *
 * Properties are read by an external reader. This class is the data access front end and some methods should be implemented
 * by the selected platform.
 *
 * @author Adam Antinoo
 * @since 0.14.0
 */
public abstract class GlobalConfigurationProvider implements IConfigurationProvider {
	protected static Logger logger = LoggerFactory.getLogger(GlobalConfigurationProvider.class);

	private static final String DEFAULT_PROPERTIES_FOLDER = "properties"; // The default initial location if not specified.

	// - F I E L D - S E C T I O N
	protected Properties configurationProperties = new Properties(); // The ist of defined properties
	private String configuredPropertiesDirectory = DEFAULT_PROPERTIES_FOLDER; // The pace where to search for properties.

	// - C O N S T R U C T O R S
	public GlobalConfigurationProvider( final String propertiesFolder ) {
		if (null != propertiesFolder) configuredPropertiesDirectory = propertiesFolder;
	}

	// - I C O N F I G U R A T I O N P R O V I D E R   I N T E R F A C E
	public String getPropertiesDirectory() {
		return this.configuredPropertiesDirectory;
	}

	public String getResourceString( final String key ) {
		final String value = configurationProperties.getProperty(key);
		if (null == value) return this.generateMissing(key);
		else return value;
	}

	public String getResourceString( final String key, final String defaultValue ) {
		final String value = configurationProperties.getProperty(key, defaultValue);
		if (null == value) return this.generateMissing(key);
		else return value;
	}

	public Integer getResourceInteger( final String key ) {
		final String value = configurationProperties.getProperty(key);
		if (null == value) return 0;
		else return Integer.valueOf(value);
	}

	/**
	 * Ths initialization method reads all the files located on a predefined folder under the src/main/resources path.
	 * All the files are expected to be Properties files and are read in alphabetical order and their contents added
	 * to the list of application properties. Read order will replace same ids with new data so the developer
	 * can use a naming convention to replace older values with new values without editing the older files.
	 */
	protected GlobalConfigurationProvider initialize() {
		try {
			this.readAllProperties();
		} catch (IOException ioe) {
			logger.error("E [GlobalConfigurationProvider.initialize]> Unprocessed exception: {}", ioe.getMessage());
			ioe.printStackTrace();
		}
		return this;
	}

	public int contentCount() {
		return configurationProperties.size();
	}

	protected String getResourceLocation() {
		return configuredPropertiesDirectory;
	}

	private String generateMissing( final String key ) {
		return '!' + key + '!';
	}

	@Override
	public String toString() {
		return new StringBuffer("GlobalConfigurationProvider[")
				       .append("Property count: ").append(contentCount()).append(" ")
				       .append("]")
				       .toString();
	}

	// - P L A T F O R M   S P E C I F I C   S E C T I O N
	protected abstract void readAllProperties() throws IOException;

	protected abstract List<String> getResourceFiles( String path ) throws IOException;
}
