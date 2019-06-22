package org.dimensinfin.eveonline.neocom.planetary;

import org.junit.Assert;
import org.junit.Test;

public class PlanetaryResourceTierTypeTest {
	@Test
	public void searchTierType4Group() {
		PlanetaryResourceTierType obtained = PlanetaryResourceTierType.searchTierType4Group("Planetary Commodities");
		Assert.assertEquals(PlanetaryResourceTierType.RAW, obtained);
		obtained = PlanetaryResourceTierType.searchTierType4Group("Advanced Commodities - Tier 4");
		Assert.assertEquals(PlanetaryResourceTierType.TIER4, obtained);
	}

	@Test
	public void getTypeCode() {
		Assert.assertEquals("T2", PlanetaryResourceTierType.TIER2.getTypeCode());
	}
}
