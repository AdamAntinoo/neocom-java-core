package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	protected void readAllProperties() {
		this.addProperty("P.cache.root.storage.name", "src/test/NeoCom.UnitTest");
		this.addProperty("P.cache.directory.path", "NeoComCache/");
		this.addProperty("P.cache.directory.store.esiitem", "ESIData.cache.store");
		this.addProperty("P.cache.esiitem.timeout", "86400");
		this.addProperty("P.cache.esinetwork.filename", "ESINetworkManager.cache.store");

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

		public TestConfigurationProvider build() {
			Objects.requireNonNull(this.onConstruction.getPropertiesDirectory());
			this.onConstruction.readAllProperties();
			return this.onConstruction;
		}
	}
}
