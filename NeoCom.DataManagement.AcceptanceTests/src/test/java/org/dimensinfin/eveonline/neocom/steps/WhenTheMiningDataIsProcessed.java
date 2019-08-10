package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.mining.updaters.MiningExtractionUpdater;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import cucumber.api.java.en.When;

public class WhenTheMiningDataIsProcessed {
	private MiningExtractionsWorld miningExtractionsWorld;
	private MiningRepository miningRepository;

	@Autowired
	public WhenTheMiningDataIsProcessed( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@When("the mining data is processed on date {string} and hour {string}")
	public void theMiningDataIsProcessedOnDateAndHour( final String processingDate, final String hour ) {
		this.miningExtractionsWorld.setProcessingDate(new LocalDate(processingDate));
		this.miningExtractionsWorld.setHour(Integer.parseInt(hour));
		final List<MiningExtraction> extractions = this.miningExtractionsWorld.getMiningExtractionRecords();
		final MiningExtractionUpdater updater = new MiningExtractionUpdater.Builder(this.miningExtractionsWorld.getCredential())
				                                        .withMiningRepository(this.miningRepository)
				                                        .build();
		for (MiningExtraction extraction : extractions)
			updater.processMiningExtraction(extraction.setExtractionHour(this.miningExtractionsWorld.getHour()),
			                                this.miningExtractionsWorld.getCredential(),
			                                this.miningExtractionsWorld.getProcessingDate());
	}
}
