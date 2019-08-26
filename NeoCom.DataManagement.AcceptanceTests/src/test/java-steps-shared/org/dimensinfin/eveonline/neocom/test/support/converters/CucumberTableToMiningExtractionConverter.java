package org.dimensinfin.eveonline.neocom.test.support.converters;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.joda.time.LocalDate;
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

	@Override
	public MiningExtraction convert( Map<String, String> cucumberRow ) {
		ESIDataAdapter esiDataAdapter = NeoComComponentFactory.getSingleton().getEsiDataAdapter();
		return new MiningExtraction.Builder()
				       .withTypeId(Integer.parseInt(cucumberRow.get(TYPE_ID)))
				       .withSolarSystemLocation(
						       esiDataAdapter.searchLocation4Id(Long.parseLong(cucumberRow.get(SOLAR_SYSTEM_ID))))
				       .withQuantity(Integer.parseInt(cucumberRow.get(QUANTITY)))
				       .withExtractionDate(this.accessExtractionDate(cucumberRow.get(EXTRACTION_DATE_NAME)))
				       .withExtractionHour(Integer.parseInt(cucumberRow.get(EXTRACTION_HOUR)))
				       .withOwnerId(Integer.parseInt(cucumberRow.get(OWNER_ID)))
				       .build();
	}

	private LocalDate accessExtractionDate( final String dateString ) {
		final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		if (dateString.equalsIgnoreCase("<today>")) return LocalDate.now();
		else return dtf.parseLocalDate(dateString);
	}
}
