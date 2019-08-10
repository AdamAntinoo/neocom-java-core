package org.dimensinfin.eveonline.neocom.steps.when;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;

import cucumber.api.java.en.When;

public class WhenRequestedToLocateLocation {
		@When("requested to locate Location {string}")
	public void requestedToLocateLocation(final  String locationId ) {
			final ESIDataAdapter esiDataAdapter = NeoComComponentFactory.getSingleton().getEsiDataAdapter();
//		final Location location=	esiDataAdapter.searchLocation4Id(locationId);
	}
}