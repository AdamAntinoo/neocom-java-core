package org.dimensinfin.eveonline.neocom.support.location;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CucumberTableToEsiLocationConverter extends CucumberTableConverter<EsiLocation> {
	private static final String CLASS_TYPE = "classType";
	private static final String REGION_ID = "regionId";
	private static final String REGION_NAME = "regionName";
	private static final String CONSTELLATION_ID = "constellationId";
	private static final String CONSTELLATION_NAME = "constellationName";

	@Override
	public EsiLocation convert( Map<String, String> cucumberCardRow ) {
		final EsiLocation.Builder locationBuilder= new EsiLocation.Builder();
		for ( Map.Entry<String, String> entry : cucumberCardRow.entrySet()){
			switch (entry.getKey()){
				case CLASS_TYPE:
					locationBuilder.withClassType(LocationClass.valueOf(entry.getValue()));
					break;
				case REGION_ID:
					locationBuilder.withRegionId(Integer.parseInt(entry.getValue()));
					break;
				case REGION_NAME:
					locationBuilder.withRegionName(entry.getValue());
					break;
				case CONSTELLATION_ID:
					locationBuilder.withConstellationId(Integer.parseInt(entry.getValue()));
					break;
				case CONSTELLATION_NAME:
					locationBuilder.withConstellationName(entry.getValue());
					break;
			}
		}
		return locationBuilder.build();
	}
}
