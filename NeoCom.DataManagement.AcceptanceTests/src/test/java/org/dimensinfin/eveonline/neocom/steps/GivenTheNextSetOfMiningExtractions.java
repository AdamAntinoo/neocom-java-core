package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToGetCharactersCharacterIdMining200OkConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Given;

public class GivenTheNextSetOfMiningExtractions {
	private MiningExtractionsWorld miningExtractionsWorld;
	private CucumberTableToGetCharactersCharacterIdMining200OkConverter
			cucumberTable2GetCharactersCharacterIdMining200OkConverter;

	@Autowired
	public GivenTheNextSetOfMiningExtractions( final MiningExtractionsWorld miningExtractionsWorld,
	                                           final CucumberTableToGetCharactersCharacterIdMining200OkConverter cucumberTable2GetCharactersCharacterIdMining200OkConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.cucumberTable2GetCharactersCharacterIdMining200OkConverter =
				cucumberTable2GetCharactersCharacterIdMining200OkConverter;
	}

	@Given("the next set of mining extractions for pilot {string} and hour {string}")
	public void theNextSetOfMiningExtractionsForPilot( final String pilotIdentifier,
	                                                   final String hour,
	                                                   final List<Map<String, String>> cucumberTable ) {
		final List<MiningExtraction> miningExtractionRecords = new ArrayList<>();
		for (Map<String, String> row : cucumberTable) {
			final GetCharactersCharacterIdMining200Ok esiMiningExtractionRecord =
					this.cucumberTable2GetCharactersCharacterIdMining200OkConverter.convert(row);
			miningExtractionRecords.add(new MiningExtraction.Builder()
					                            .fromMining(esiMiningExtractionRecord)
					                            .withExtractionHour(Integer.parseInt(hour))
					                            .withOwnerId(Integer.parseInt(pilotIdentifier))
					                            .build());
		}
		Assert.assertTrue(miningExtractionRecords.size() > 0);
		this.miningExtractionsWorld.setMiningExtractionRecords(miningExtractionRecords);
	}
}
