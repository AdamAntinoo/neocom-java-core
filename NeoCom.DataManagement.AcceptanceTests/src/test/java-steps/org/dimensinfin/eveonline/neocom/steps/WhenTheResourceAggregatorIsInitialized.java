package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.mining.DailyExtractionResourcesContainer;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.adapters.SupportMiningRepository;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;

import cucumber.api.java.en.When;

public class WhenTheResourceAggregatorIsInitialized {
	private MiningExtractionsWorld miningExtractionsWorld;
	private SupportMiningRepository miningRepository;

	public WhenTheResourceAggregatorIsInitialized( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
	}

	@When("the resource aggregator is initialized")
	public void theResourceAggregatorIsInitialized() {
		final DailyExtractionResourcesContainer resourcesContainer = new DailyExtractionResourcesContainer.Builder()
				.withCredential(this.miningExtractionsWorld.getCredential())
				.withMiningRepository(this.miningRepository)
				.withTargetDate(this.miningExtractionsWorld.getTodayDate())
				.build();
		this.miningExtractionsWorld.setResourcesContainer(resourcesContainer);
	}
}
