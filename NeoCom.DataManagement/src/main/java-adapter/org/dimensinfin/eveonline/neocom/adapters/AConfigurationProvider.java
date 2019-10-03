package org.dimensinfin.eveonline.neocom.adapters;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.annotations.NeoComAdapter;

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
@NeoComAdapter
public abstract class AConfigurationProvider implements IConfigurationProvider {
	protected static final Logger logger = LoggerFactory.getLogger( AConfigurationProvider.class );
	private static final String DEFAULT_PROPERTIES_FOLDER = "properties"; // The default initial location if not specified.

	// - F I E L D - S E C T I O N
	protected final Properties configurationProperties = new Properties(); // The list of defined properties
	protected String configuredPropertiesDirectory = DEFAULT_PROPERTIES_FOLDER; // The place where to search for properties.

	// - C O N S T R U C T O R S
//	public AConfigurationProvider( final String propertiesFolder ) {
//		if (null != propertiesFolder) configuredPropertiesDirectory = propertiesFolder;
//	}

	// - I C O N F I G U R A T I O N P R O V I D E R   I N T E R F A C E
	public int contentCount() {
		return this.configurationProperties.size();
	}
//	public String getPropertiesDirectory() {
//		return this.configuredPropertiesDirectory;
//	}

	public String getResourceString( final String key ) {
		final String value = this.configurationProperties.getProperty( key );
		if (null == value) return this.generateMissing( key );
		else return value;
	}

	public String getResourceString( final String key, final String defaultValue ) {
		final String value = this.configurationProperties.getProperty( key, defaultValue );
		if (null == value) return this.generateMissing( key );
		else return value;
	}

	public Integer getResourceInteger( final String key ) {
		final String value = this.configurationProperties.getProperty( key );
		if (null == value) return 0;
		else return Integer.valueOf( value );
	}

	public Integer getResourceInteger( final String key, final Integer defaultValue ) {
		final String value = this.configurationProperties.getProperty( key );
		if (null == value) return defaultValue;
		else return Integer.valueOf( value );
	}

	public AConfigurationProvider setConfiguredPropertiesDirectory( final String configuredPropertiesDirectory ) {
		this.configuredPropertiesDirectory = configuredPropertiesDirectory;
		return this;
	}
//	/**
//	 * This initialization method reads all the files located on a predefined folder under the src/main/resources path.
//	 * All the files are expected to be Properties files and are read in alphabetical order and their contents added
//	 * to the list of application properties. Read order will replace same ids with new data so the developer
//	 * can use a naming convention to replace older values with new values without editing the older files.
//	 */
//	protected AConfigurationProvider initialize() {
//		try {
//			this.readAllProperties();
//		} catch (IOException ioe) {
//			logger.error( "E [GlobalConfigurationProvider.initialize]> Unprocessed exception: {}", ioe.getMessage() );
//			ioe.printStackTrace();
//		}
//		return this;
//	}

	protected String getResourceLocation() {
		return this.configuredPropertiesDirectory;
	}

	private String generateMissing( final String key ) {
		return '!' + key + '!';
	}

//	@Override
//	public String toString() {
//		return new StringBuffer( "GlobalConfigurationProvider[" )
//				.append( "Property count: " ).append( contentCount() ).append( " " )
//				.append( "]" )
//				.toString();
//	}

	// - P L A T F O R M   S P E C I F I C   S E C T I O N
	protected abstract void readAllProperties() throws IOException;

	protected abstract List<String> getResourceFiles( String path ) throws IOException;

	// - B U I L D E R
	protected static abstract class Builder<T extends AConfigurationProvider, B extends AConfigurationProvider.Builder> {
		protected B actualClassBuilder;

		public Builder() {
			this.actualClassBuilder = this.getActualBuilder();
		}

		protected abstract T getActual();

		protected abstract B getActualBuilder();

		public B withPropertiesDirectory( final String propertiesDirectory ) {
			if (null != propertiesDirectory) this.getActual().configuredPropertiesDirectory = propertiesDirectory;
			return this.actualClassBuilder;
		}

		public T build() throws IOException {
			Objects.requireNonNull( this.getActual().configuredPropertiesDirectory );
			this.getActual().readAllProperties(); // Initialize and read the properties.
			return this.getActual();
		}
	}
}
