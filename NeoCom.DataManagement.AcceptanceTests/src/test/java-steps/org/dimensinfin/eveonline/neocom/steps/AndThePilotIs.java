package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;

import cucumber.api.java.en.And;

public class AndThePilotIs {
	private MiningExtractionsWorld miningExtractionsWorld;

	public AndThePilotIs( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
	}

	@And("the pilot is {string}")
	public void thePilotIs( final String pilotIdentifier ) {
		this.miningExtractionsWorld.setPilotIdentifier(Integer.parseInt(pilotIdentifier));
	}
}
