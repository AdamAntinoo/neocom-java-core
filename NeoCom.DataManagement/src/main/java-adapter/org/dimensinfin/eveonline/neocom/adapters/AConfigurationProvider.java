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

	public boolean getResourceBoolean( final String key ) {
		final String value = this.getResourceString( key, "false" );
		if (value.equalsIgnoreCase( "true" )) return true;
		if (value.equalsIgnoreCase( "on" )) return true;
		if (value.equalsIgnoreCase( "0" )) return false;
		return false;
	}

	public AConfigurationProvider setConfiguredPropertiesDirectory( final String configuredPropertiesDirectory ) {
		this.configuredPropertiesDirectory = configuredPropertiesDirectory;
		return this;
	}

	protected String getResourceLocation() {
		return this.configuredPropertiesDirectory;
	}

	private String generateMissing( final String key ) {
		return '!' + key + '!';
	}

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
			return this.getActualBuilder();
		}

		public T build() throws IOException {
			Objects.requireNonNull( this.getActual().configuredPropertiesDirectory );
			this.getActual().readAllProperties(); // Initialize and read the properties.
			return this.getActual();
		}
	}
}
