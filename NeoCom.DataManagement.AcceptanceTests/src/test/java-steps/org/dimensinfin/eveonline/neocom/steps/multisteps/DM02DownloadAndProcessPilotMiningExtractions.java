package org.dimensinfin.eveonline.neocom.steps.multisteps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.adapters.SupportMiningRepository;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToGetCharactersCharacterIdMining200OkConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

public class DM02DownloadAndProcessPilotMiningExtractions {
	private static Logger logger = LoggerFactory.getLogger(DM02DownloadAndProcessPilotMiningExtractions.class);
	private MiningExtractionsWorld miningExtractionsWorld;
	private SupportMiningRepository miningRepository;
	private CucumberTableToGetCharactersCharacterIdMining200OkConverter cucumberTable2GetCharactersCharacterIdMining200OkConverter;

	public DM02DownloadAndProcessPilotMiningExtractions( final MiningExtractionsWorld miningExtractionsWorld,
	                                                     final CucumberTableToGetCharactersCharacterIdMining200OkConverter cucumberTable2GetCharactersCharacterIdMining200OkConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
		this.cucumberTable2GetCharactersCharacterIdMining200OkConverter =
				cucumberTable2GetCharactersCharacterIdMining200OkConverter;
	}

	@Given("an empty Mining Extraction repository")
	public void anEmptyMiningExtractionRepository() {
		final int recordsDeleted = this.miningRepository.deleteAll();
		logger.info("-- [DM02DownloadAndProcessPilotMiningExtractions.anEmptyMiningExtractionRepository]> Records deleted: {}",
		            recordsDeleted);
	}

	@And("the next set of mining extractions for pilot {string}")
	public void theNextSetOfMiningExtractionsForPilot( final String pilotIdentifier,
	                                                   final List<Map<String, String>> cucumberTable ) {
		final List<MiningExtraction> miningExtractionRecords = new ArrayList<>();
		for (Map<String, String> row : cucumberTable) {
			final GetCharactersCharacterIdMining200Ok esiMiningExtractionRecord =
					this.cucumberTable2GetCharactersCharacterIdMining200OkConverter.convert(row);
			miningExtractionRecords.add(new MiningExtraction.Builder()
					                            .fromMining(esiMiningExtractionRecord)
					                            .withSolarSystemLocation(this.miningExtractionsWorld.getEsiDataAdapter()
							                                                     .searchLocation4Id(esiMiningExtractionRecord
									                                                                        .getSolarSystemId()
									                                                                        .longValue()))
					                            .withOwnerId(Integer.parseInt(pilotIdentifier))
					                            .build());
		}
		Assert.assertTrue(miningExtractionRecords.size() > 0);
		this.miningExtractionsWorld.setMiningExtractionRecords(miningExtractionRecords);
		this.miningExtractionsWorld.setPilotIdentifier(Integer.parseInt(pilotIdentifier));
	}
}