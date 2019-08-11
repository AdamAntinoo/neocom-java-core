package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;

public class ThenTheNextRecordsAreSetOnTheMiningRepository {
	private MiningExtractionsWorld miningExtractionsWorld;
	private CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter;
	private MiningRepository miningRepository;

	public ThenTheNextRecordsAreSetOnTheMiningRepository( final MiningExtractionsWorld miningExtractionsWorld,
	                                                      final CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.cucumberTableToMiningExtractionConverter = cucumberTableToMiningExtractionConverter;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
	}

	@Then("the next records are set on the MiningRepository")
	public void theNextRecordsAreSetOnTheMiningRepository( final List<Map<String, String>> cucumberTable ) {
		final List<MiningExtraction> extractionRecords = this.miningRepository.accessMiningExtractions4Pilot(
				this.miningExtractionsWorld.getCredential());
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
