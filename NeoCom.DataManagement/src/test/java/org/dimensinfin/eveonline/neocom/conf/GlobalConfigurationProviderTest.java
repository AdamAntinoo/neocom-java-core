package org.dimensinfin.eveonline.neocom.conf;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalConfigurationProviderTest {
	private TestConfigurationProvider provider;

	@Before
	public void setUp() {
		this.provider = new TestConfigurationProvider("properties");
	}

	@Test
	public void getResourceString() {
		this.provider.initialize();
		final String expected = "Test Value 2";
		final String obtained = this.provider.getResourceString("test.property.2");
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void getResourceString_notfound() {
		this.provider.initialize();
		final String expected = "!test.property.3!";
		final String obtained = this.provider.getResourceString("test.property.3");
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void getResourceString_default() {
		this.provider.initialize();
		final String expected = "Test Value 2";
		final String obtained = this.provider.getResourceString("test.property.2", "default");
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void getResourceString_default_notfound() {
		this.provider.initialize();
		final String expected = "default";
		final String obtained = this.provider.getResourceString("test.property.3", "default");
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void contentCount() {
		this.provider.initialize();
		final int expected = 2;
		final int obtained = this.provider.contentCount();
		Assert.assertEquals(expected, obtained);
	}
	@Test
	public void initialize() {
		Assert.assertEquals(0, this.provider.contentCount());
		this.provider.initialize();
		Assert.assertEquals(2, this.provider.contentCount());
	}

//	@Test(expected = IOException.class)
	public void initialize_fail() throws IOException {
		final TestConfigurationProvider failProvider = Mockito.mock(TestConfigurationProvider.class);
		Mockito.doThrow(new IOException("ReadAllProperties failed.")).when(failProvider).readAllProperties();
		Assert.assertEquals(0, this.provider.contentCount());
		failProvider.initialize();
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
			this.configurationProperties.setProperty("test.property.1", "Test Value 1");
			this.configurationProperties.setProperty("test.property.2", "Test Value 2");
		}

		@Override
		protected List<String> getResourceFiles( final String path ) throws IOException {
			return null;
		}

		@Override
		public Integer getResourceInteger( final String key ) {
			return new Integer(2);
		}
	}
}
