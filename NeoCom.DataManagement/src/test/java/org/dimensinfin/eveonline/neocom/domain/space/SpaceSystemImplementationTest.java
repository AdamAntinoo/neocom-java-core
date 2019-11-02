package org.dimensinfin.eveonline.neocom.domain.space;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

public class SpaceSystemImplementationTest {
	@Test
	public void buildComplete() {
		final  GetUniverseRegionsRegionIdOk region = Mockito.mock(GetUniverseRegionsRegionIdOk.class);
		final SpaceSystem space = new SpaceSystemImplementation.Builder()
				.withRegion( region )
				.build();
		Assert.assertNotNull(space);
	}
}