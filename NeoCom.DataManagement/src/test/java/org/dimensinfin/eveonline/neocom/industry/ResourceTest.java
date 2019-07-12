package org.dimensinfin.eveonline.neocom.industry;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;
import org.dimensinfin.eveonline.neocom.support.TestEsiAdapterReady;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ResourceTest extends TestEsiAdapterReady {
	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(Resource.class);
	}

	@Test
	public void add() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final Resource resource = new Resource(34, 1000, 2);
		final int obtained = resource.add(1000);
		Assert.assertNotNull(resource);
		Assert.assertEquals("The calculated quantity should match.", 2000, obtained);
	}

	@Test
	public void addition() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final Resource resource = new Resource(34, 1000, 2);
		final int obtained = resource.addition(new Resource(34, 1000, 2));
		Assert.assertNotNull(resource);
		Assert.assertEquals("The calculated quantity should match.", 4000, obtained);
	}

	@Test
	public void getGroupName() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final Resource resource = new Resource(34, 1000, 2);
		final String obtained = resource.getGroupName();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The calculated quantity should match.", "Mineral", obtained);
	}

	@Test
	public void getCategory() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final Resource resource = new Resource(34, 1000, 2);
		final String obtained = resource.getCategory();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The calculated quantity should match.", "Material", obtained);
	}

	@Test
	public void getQuantity() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final Resource resource = new Resource(34, 1000, 2);
		final int obtained = resource.getQuantity();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The calculated quantity should match.", 2000, obtained);
	}
}
