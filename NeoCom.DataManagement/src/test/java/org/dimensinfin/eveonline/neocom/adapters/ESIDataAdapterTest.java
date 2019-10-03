package org.dimensinfin.eveonline.neocom.adapters;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.support.ESIDataAdapterSupportTest;

public class ESIDataAdapterTest extends ESIDataAdapterSupportTest {
	@Test
	public void builder_complete()  {
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull(this.esiDataAdapter);
	}
	@Test(expected = NullPointerException.class)
	public void builder_failure() {
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder()
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull(this.esiDataAdapter);
	}

	@Test
	public void downloadItemPrices() {
		final double price = this.esiDataAdapter.searchSDEMarketPrice( 34 ); // The search should fail because the case is empty.
		Assert.assertTrue( "the price should be negative because not found." , price<0.0);
		this.esiDataAdapter.downloadItemPrices();
	}

	//	@Test
	public void fetchItem_notcached()  {
//		final ESIDataAdapter adapter = this.setupRealAdapter();
		final GetUniverseTypesTypeIdOk item = this.esiDataAdapter.searchEsiItem4Id(34);
		Assert.assertNotNull(item);
	}

//	@Test
	public void searchSDEMarketPrice() {
		final Double price = this.esiDataAdapter.searchSDEMarketPrice(34);
		Assert.assertNotNull(price);
	}
}
