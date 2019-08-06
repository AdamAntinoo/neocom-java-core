package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import cucumber.api.java.en.When;

public class WhenTheMiningDataIsProcessed {
	private MiningExtractionsWorld miningExtractionsWorld;

	@Autowired
	public WhenTheMiningDataIsProcessed( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@When("the mining data is processed")
	public void theMiningDataIsProcessed() {
		final List<MiningExtraction> extractions = this.miningExtractionsWorld.getMiningExtractionRecords();
//final MiningExtractionUpdater updater = new MiningExtractionUpdater.Builder().withCredential()
	}
}
