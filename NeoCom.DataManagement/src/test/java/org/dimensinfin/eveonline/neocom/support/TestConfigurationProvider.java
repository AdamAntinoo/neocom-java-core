package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfigurationProvider extends GlobalConfigurationProvider {
	protected static Logger logger = LoggerFactory.getLogger(GlobalConfigurationProvider.class);

	// - C O N S T R U C T O R S
	private TestConfigurationProvider( final String propertiesFolder ) {
		super(propertiesFolder);
	}

	@Override
	public Integer getResourceInteger( final String key ) {
		return new Integer(100);
	}

	// - P L A T F O R M   S P E C I F I C   S E C T I O N
	protected void readAllProperties() throws IOException {
		this.addProperty("P.cache.directory.path", "NeoComCache");
		this.addProperty("P.cache.esinetwork.filename", "ESINetworkManager.cache.store");
		this.addProperty("P.cache.store.filename", "ESIData.cache.store");
		this.addProperty("P.cache.esiitem.time", "86400");
	}

	protected List<String> getResourceFiles( String path ) throws IOException {
		return new ArrayList<>();
	}

	protected void addProperty( final String key, final String value ) {
		this.configurationProperties.setProperty(key, value);
	}

	// - B U I L D E R
	public static class Builder {
		private TestConfigurationProvider onConstruction;

		public Builder( final String propertiesDirectory ) {
			this.onConstruction = new TestConfigurationProvider(propertiesDirectory);
		}

		public TestConfigurationProvider build() throws IOException {
			Objects.requireNonNull(this.onConstruction.getPropertiesDirectory());
			this.onConstruction.readAllProperties();
			return this.onConstruction;
		}
	}
}
