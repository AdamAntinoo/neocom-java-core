package org.dimensinfin.eveonline.neocom.steps.when;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.location.LocationWorld;
import org.junit.Assert;

import cucumber.api.java.en.When;

public class WhenRequestedToLocateLocation {
	private LocationWorld locationWorld;

	public WhenRequestedToLocateLocation( final LocationWorld locationWorld ) {
		this.locationWorld = locationWorld;
	}

	@When("requested to locate Location {string}")
	public void requestedToLocateLocation( final String locationId ) {
		final ESIDataAdapter esiDataAdapter = NeoComComponentFactory.getSingleton().getEsiDataAdapter();
		final EsiLocation location = esiDataAdapter.searchLocation4Id(Long.parseLong(locationId));
		Assert.assertNotNull(location);
		this.locationWorld.setLocation(location);
	}
}