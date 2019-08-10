package org.dimensinfin.eveonline.neocom.support.miningExtractions;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CucumberTableToMiningExtractionConverter extends CucumberTableConverter<MiningExtraction> {
	private static final String EXTRACTION_DATE_NAME = "extractionDateName";
	private static final String QUANTITY = "quantity";
	private static final String SOLAR_SYSTEM_ID = "solarSystemId";
	private static final String TYPE_ID = "typeId";
	private static final String EXTRACTION_HOUR = "extractionHour";
	private static final String OWNER_ID = "ownerId";
	private static final String DELTA = "delta";

	@Override
	public MiningExtraction convert( Map<String, String> cucumberCardRow ) {
		final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		ESIDataAdapter esiDataAdapter = NeoComComponentFactory.getSingleton().getEsiDataAdapter();
		return new MiningExtraction.Builder()
				       .withTypeId(Integer.parseInt(cucumberCardRow.get(TYPE_ID)))
				       .withSolarSystemLocation(esiDataAdapter.searchLocation4Id(Long.parseLong(cucumberCardRow.get(SOLAR_SYSTEM_ID))))
				       .withQuantity(Integer.parseInt(cucumberCardRow.get(QUANTITY)))
				       .withExtractionDate(dtf.parseLocalDate(cucumberCardRow.get(EXTRACTION_DATE_NAME)))
				       .withExtractionHour(Integer.parseInt(cucumberCardRow.get(EXTRACTION_HOUR)))
				       .withOwnerId(Integer.parseInt(cucumberCardRow.get(OWNER_ID)))
				       .build();
	}
}
