package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.TestEsiAdapterReady;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PlanetaryResourceTest extends TestEsiAdapterReady {
	@Test
	public void getTier_RAW() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(2307, 1000);
		final PlanetaryResourceTierType obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be RAW.", PlanetaryResourceTierType.RAW, obtained);
	}

	@Test
	public void getTier_TIER1() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(2393, 1000);
		final PlanetaryResourceTierType obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be TIER1.", PlanetaryResourceTierType.TIER1, obtained);
	}

	@Test
	public void getTier_TIER2() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(9832, 1000);
		final PlanetaryResourceTierType obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be TIER2.", PlanetaryResourceTierType.TIER2, obtained);
	}
}
