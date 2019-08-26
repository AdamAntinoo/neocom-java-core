package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.test.support.adapters.SupportMiningRepository;

import java.util.List;

import cucumber.api.java.en.When;

public class WhenRequestingTheListOfExtractions {
	private MiningExtractionsWorld miningExtractionsWorld;
	private SupportMiningRepository miningRepository;

	public WhenRequestingTheListOfExtractions( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
	}

	@When("requesting the list of extractions")
	public void requestingTheListOfExtractions() {
		final List<MiningExtraction> extractions = this.miningRepository.accessDatedMiningExtractions4Pilot(
						                                           this.miningExtractionsWorld.getCredential(),
						                                           this.miningExtractionsWorld.getTodayDate());
		this.miningExtractionsWorld.setMiningExtractionRecords(extractions);
	}
}
