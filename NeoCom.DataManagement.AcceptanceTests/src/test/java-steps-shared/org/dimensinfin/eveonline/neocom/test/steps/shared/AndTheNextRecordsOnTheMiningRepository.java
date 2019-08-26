package org.dimensinfin.eveonline.neocom.test.steps.shared;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.test.support.adapters.SupportMiningRepository;
import org.dimensinfin.eveonline.neocom.test.support.converters.CucumberTableToMiningExtractionConverter;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;

public class AndTheNextRecordsOnTheMiningRepository {
	private CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter;

	public AndTheNextRecordsOnTheMiningRepository( final CucumberTableToMiningExtractionConverter cucumberTableToMiningExtractionConverter ) {
		this.cucumberTableToMiningExtractionConverter = cucumberTableToMiningExtractionConverter;
	}

	@And("the next records on the MiningRepository")
	public void theNextRecordsOnTheMiningRepository( final List<Map<String, String>> cucumberTable ) throws SQLException {
		final SupportMiningRepository miningRepository = (SupportMiningRepository) NeoComComponentFactory.getSingleton()
				                                                                           .getMiningRepository();
		miningRepository.deleteAll();
		for (Map<String, String> row : cucumberTable) {
			final MiningExtraction extraction = this.cucumberTableToMiningExtractionConverter.convert(row);
			Assert.assertNotNull("The record is created.", extraction);
			miningRepository.persist(extraction);
		}
	}
}
