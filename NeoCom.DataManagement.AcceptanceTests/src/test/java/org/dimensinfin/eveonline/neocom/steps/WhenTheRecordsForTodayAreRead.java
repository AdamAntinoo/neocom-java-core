package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.adapters.SupportMiningRepository;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import cucumber.api.java.en.When;

public class WhenTheRecordsForTodayAreRead {
	private MiningExtractionsWorld miningExtractionsWorld;
	private SupportMiningRepository miningRepository;

	@Autowired
	public WhenTheRecordsForTodayAreRead( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
	}

	@When("the records for today are read")
	public void theRecordsForTodayAreRead() {
		final List<MiningExtraction> todayExtractions = this.miningRepository.accessResources4Date(
				this.miningExtractionsWorld.getCredential(),
				this.miningExtractionsWorld.getTodayDate());

//		this.addHeaderContents(new TableHeader()); // Add the resource table header
//		for (int i = 0, arraySize = this.resources.size(); i < arraySize; i++) {
//			final Resource resource = this.resources.valueAt(i);
//			this.addHeaderContents(resource);
//		}


	}
}
