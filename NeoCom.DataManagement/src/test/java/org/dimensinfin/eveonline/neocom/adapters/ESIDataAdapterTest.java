package org.dimensinfin.eveonline.neocom.adapters;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.TestFileSystem;

import org.junit.Assert;
import org.junit.Test;

public class ESIDataAdapterTest {
	@Test
	public void builder_complete() throws IOException {
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("testproperties").build();
		final TestFileSystem fileSystemAdapter = new TestFileSystem("./src/test/resources/Test.NeoCom.Infinity");
//		final StoreCacheManager cacheManager = new StoreCacheManager.Builder().withEsiDataAdapter(esiDataAdapter).build();
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
		Assert.assertNotNull(adapter);
	}

	@Test
	public void fetchItem_notcached() throws IOException, InterruptedException {
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("testproperties").build();
		final TestFileSystem fileSystemAdapter = new TestFileSystem("./src/test/resources/Test.NeoCom.Infinity");
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();

		final EveItem item = adapter.searchEsiItem4Id(34);
//		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
//		final EsiItemV2 item = itemSingle.blockingGet();
		Assert.assertNotNull(item);
	}
}
