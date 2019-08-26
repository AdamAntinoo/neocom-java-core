package org.dimensinfin.eveonline.neocom.support.miningExtractions;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.test.support.converters.CucumberTableConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CucumberTableToGetCharactersCharacterIdMining200OkConverter extends CucumberTableConverter<GetCharactersCharacterIdMining200Ok> {
	private static final String DATE = "date";
	private static final String QUANTITY = "quantity";
	private static final String SOLAR_SYSTEM_ID = "solar_system_id";
	private static final String TYPE_ID = "type_id";

	@Override
	public GetCharactersCharacterIdMining200Ok convert( Map<String, String> cucumberCardRow ) {
		final GetCharactersCharacterIdMining200Ok extraction = new GetCharactersCharacterIdMining200Ok();
		final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		extraction.setDate(dtf.parseLocalDate(cucumberCardRow.get(DATE)));
		extraction.setQuantity(Long.parseLong(cucumberCardRow.get(QUANTITY)));
		extraction.setSolarSystemId(Integer.parseInt(cucumberCardRow.get(SOLAR_SYSTEM_ID)));
		extraction.setTypeId(Integer.parseInt(cucumberCardRow.get(TYPE_ID)));
		return extraction;
	}
}
