package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;

public class ThenWeGetTheNextListOfExtractions {
	private MiningExtractionsWorld miningExtractionsWorld;
	private CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter;
	private MiningRepository miningRepository;

	@Autowired
	public ThenWeGetTheNextListOfExtractions( final MiningExtractionsWorld miningExtractionsWorld,
	                                          final CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.cucumberTableToMiningExtractionConverter = cucumberTableToMiningExtractionConverter;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@Then("we get the next list of extractions")
	public void weGetTheNextListOfExtractions( final List<Map<String, String>> cucumberTable ) {
		final List<MiningExtraction> extractionRecords = this.miningExtractionsWorld.getMiningExtractionRecords();
		int verificationIndex = 0;
		for (Map<String, String> row : cucumberTable) {
			final MiningExtraction verificationRecord = this.cucumberTableToMiningExtractionConverter.convert(row);
			Assert.assertTrue("The test record and the database record should match.",
			                  this.miningExtractionsWorld.validateRecord(verificationRecord,
			                                                             extractionRecords.get(verificationIndex)));
			verificationIndex++;
		}
	}
}
