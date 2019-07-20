package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.support.EsiDataAdapterSupportTest;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.TestFileSystem;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ESIDataAdapterTest extends EsiDataAdapterSupportTest {
	@Test
	public void builder_complete()  {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("properties").build();
		final IFileSystem fileSystemAdapter = new TestFileSystem();
		this.esiDataAdapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
		Assert.assertNotNull(this.esiDataAdapter);
	}

	@Test
	public void fetchItem_notcached()  {
//		final ESIDataAdapter adapter = this.setupRealAdapter();
		final GetUniverseTypesTypeIdOk item = this.esiDataAdapter.searchEsiItem4Id(34);
		Assert.assertNotNull(item);
	}

	@Test
	public void searchSDEMarketPrice() {
		final Double price = this.esiDataAdapter.searchSDEMarketPrice(34);
		Assert.assertNotNull(price);
	}
}
