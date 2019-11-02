package org.dimensinfin.eveonline.neocom.domain.space;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class SpaceLocationTest {
	@Test
	public void buildComplete() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock(GetUniverseRegionsRegionIdOk.class);
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock(GetUniverseConstellationsConstellationIdOk.class);
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock(GetUniverseSystemsSystemIdOk.class);
		final GetUniverseStationsStationIdOk station = Mockito.mock(GetUniverseStationsStationIdOk.class);
		final GetCorporationsCorporationIdOk corporation = Mockito.mock(GetCorporationsCorporationIdOk.class);
		final SpaceLocation location = new SpaceLocation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.withCorporationId( 87654321 )
				.withCorporation( corporation )
				.withSecurity( 0.987 )
				.build();
		Assert.assertNotNull(location);
	}
}