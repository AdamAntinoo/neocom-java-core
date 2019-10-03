package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.CucumberTableToGetCharactersCharacterIdMining200OkConverter;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.test.support.adapters.SupportMiningRepository;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GivenTheNextSetOfMiningExtractions {
	private static Logger logger = LoggerFactory.getLogger(GivenTheNextSetOfMiningExtractions.class);
	private MiningExtractionsWorld miningExtractionsWorld;
	private CucumberTableToGetCharactersCharacterIdMining200OkConverter
			cucumberTable2GetCharactersCharacterIdMining200OkConverter;
	private SupportMiningRepository miningRepository;

	public GivenTheNextSetOfMiningExtractions( final MiningExtractionsWorld miningExtractionsWorld,
	                                           final CucumberTableToGetCharactersCharacterIdMining200OkConverter cucumberTable2GetCharactersCharacterIdMining200OkConverter ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		this.cucumberTable2GetCharactersCharacterIdMining200OkConverter =
				cucumberTable2GetCharactersCharacterIdMining200OkConverter;
		this.miningRepository = NeoComComponentFactory.getSingleton().getMiningRepository();
	}

//	@Before
//	public void beforeAll() {
//		final int recordsDeleted = this.miningRepository.deleteAll();
//		logger.info("-- [GivenTheNextSetOfMiningExtractions.beforeAll]> Records deleted: {}", recordsDeleted);
//	}

//	@Given("the next set of mining extractions for pilot {string}")
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