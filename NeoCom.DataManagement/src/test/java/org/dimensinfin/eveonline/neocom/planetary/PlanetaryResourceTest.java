package org.dimensinfin.eveonline.neocom.planetary;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.TestAdapterReadyUp;

import org.junit.Assert;
import org.junit.Test;

public class PlanetaryResourceTest extends TestAdapterReadyUp {
	@Test
	public void getTier_RAW() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(2307, 1000);
		final PlanetaryResourceTier obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be RAW.", PlanetaryResourceTier.RAW, obtained);
	}

	@Test
	public void getTier_TIER1() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(2393, 1000);
		final PlanetaryResourceTier obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be TIER1.", PlanetaryResourceTier.TIER1, obtained);
	}

	@Test
	public void getTier_TIER2() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final PlanetaryResource resource = new PlanetaryResource(9832, 1000);
		final PlanetaryResourceTier obtained = resource.getTier();
		Assert.assertNotNull(resource);
		Assert.assertEquals("The tier should be TIER2.", PlanetaryResourceTier.TIER2, obtained);
	}

}
