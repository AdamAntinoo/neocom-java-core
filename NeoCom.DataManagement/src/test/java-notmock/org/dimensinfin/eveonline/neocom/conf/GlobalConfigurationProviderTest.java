package org.dimensinfin.eveonline.neocom.conf;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalConfigurationProviderTest {
	private TestConfigurationProvider provider;

	@Before
	public void setUp() throws Exception {
		this.provider = new TestConfigurationProvider("properties");
	}

	@Test
	public void getResourceString() {
	}

	@Test
	public void getResourceString1() {
	}

	@Test
	public void contentCount() {
		this.provider.initialize();
		final int expected = 2;
		final int obtained = this.provider.contentCount();
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void getResourceLocation() {
	}

	final private class TestConfigurationProvider extends GlobalConfigurationProvider {

		public TestConfigurationProvider( final String propertiesFolder ) {
			super(propertiesFolder);
		}

		@Override
		protected void readAllProperties() throws IOException {
			this.configurationProperties.setProperty("test.property.1","Test Value 1");
			this.configurationProperties.setProperty("test.property.2","Test Value 2");
		}

		@Override
		protected List<String> getResourceFiles( final String path ) throws IOException {
			return null;
		}
	}
}
