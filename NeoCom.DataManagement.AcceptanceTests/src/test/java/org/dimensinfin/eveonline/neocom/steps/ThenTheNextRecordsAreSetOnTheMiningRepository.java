package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;

public class ThenTheNextRecordsAreSetOnTheMiningRepository {
	private MiningExtractionsWorld miningExtractionsWorld;
	private CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter;
	private MiningRepository miningRepository;

	@Autowired
	public ThenTheNextRecordsAreSetOnTheMiningRepository( final MiningExtractionsWorld miningExtractionsWorld,
	                                                      final CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.cucumberTableToMiningExtractionConverter = cucumberTableToMiningExtractionConverter;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@Then("the next records are set on the MiningRepository")
	public void theNextRecordsAreSetOnTheMiningRepository( final List<Map<String, String>> cucumberTable ) {
		final List<MiningExtraction> extractionRecods = this.miningRepository.accessDatedMiningExtractions4Pilot(
				this.miningExtractionsWorld.getCredential(),
				new DateTime("2019-08-07"));
		int verificationIndex = 0;
		for (Map<String, String> row : cucumberTable) {
			final MiningExtraction verificationRecord = this.cucumberTableToMiningExtractionConverter.convert(row);
			Assert.assertTrue("The test record and the database record should match.",
			                  this.miningExtractionsWorld.validateRecord(verificationRecord,
			                                                             extractionRecods.get(verificationIndex++)));
		}
	}
}
