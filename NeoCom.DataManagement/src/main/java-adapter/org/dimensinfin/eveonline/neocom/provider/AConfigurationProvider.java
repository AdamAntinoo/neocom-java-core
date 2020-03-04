package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;

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
	// - P R O P E R T Y   N A M E S
	public static final String BACKEND_RETROFIT_CACHE_FILE_NAME = "P.backend.retrofit.cache.directory.name";
	public static final String NEOCOM_BACKEND_SERVER_BASE_URL = "P.backend.server.base.url";
	public static final String CACHE_DIRECTORY_PATH = "P.cache.directory.path";
	public static final String AUTHENTICATED_RETROFIT_SERVER_LOCATION = "P.authenticated.retrofit.server.location";
	public static final String AUTHENTICATED_RETROFIT_SERVER_AGENT = "P.authenticated.retrofit.server.agent";
	public static final String UNIVERSE_RETROFIT_SERVER_AGENT = "P.universe.retrofit.server.agent";
	protected static final Logger logger = LoggerFactory.getLogger( AConfigurationProvider.class );
	private static final String DEFAULT_PROPERTIES_FOLDER = "properties"; // The default initial location if not specified.

	// - F I E L D - S E C T I O N
	protected final Properties configurationProperties = new Properties(); // The list of defined properties
	protected String configuredPropertiesDirectory = DEFAULT_PROPERTIES_FOLDER; // The place where to search for properties.

	protected String getResourceLocation() {
		return this.configuredPropertiesDirectory;
	}

	public AConfigurationProvider setConfiguredPropertiesDirectory( final String configuredPropertiesDirectory ) {
		this.configuredPropertiesDirectory = configuredPropertiesDirectory;
		return this;
	}

	// - I C O N F I G U R A T I O N P R O V I D E R   I N T E R F A C E
	public int contentCount() {
		return this.configurationProperties.size();
	}

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
		if (value.equalsIgnoreCase( "1" )) return true;
		return false;
	}

	protected abstract List<String> getResourceFiles( String path ) throws IOException;

	// - P L A T F O R M   S P E C I F I C   S E C T I O N
	protected abstract void readAllProperties() throws IOException;

	private String generateMissing( final String key ) {
		return '!' + key + '!';
	}

	// - B U I L D E R
	protected abstract static class Builder<T extends AConfigurationProvider, B extends AConfigurationProvider.Builder> {
		protected B actualClassBuilder;

		public Builder() {
			this.actualClassBuilder = this.getActualBuilder();
		}

		protected abstract T getActual();

		protected abstract B getActualBuilder();

		public T build() throws IOException {
			Objects.requireNonNull( this.getActual().configuredPropertiesDirectory );
			this.getActual().readAllProperties(); // Initialize and read the properties.
			return this.getActual();
		}

		public B withPropertiesDirectory( final String propertiesDirectory ) {
			if (null != propertiesDirectory) this.getActual().configuredPropertiesDirectory = propertiesDirectory;
			return this.getActualBuilder();
		}
	}
}
