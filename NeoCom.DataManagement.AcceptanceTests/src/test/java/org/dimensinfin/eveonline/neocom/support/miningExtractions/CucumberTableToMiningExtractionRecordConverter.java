package org.dimensinfin.eveonline.neocom.support.miningExtractions;

import org.dimensinfin.eveonline.neocom.market.MiningExtractionRecord;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CucumberTableToMiningExtractionRecordConverter extends CucumberTableConverter<MiningExtractionRecord> {
	private static final String DATE = "date";
	private static final String HOUR = "hour";
	private static final String QUANTITY = "quantity";
	private static final String SOLAR_SYSTEM_ID = "solar_system_id";
	private static final String TYPE_ID = "type_id";

	@Override
	public MiningExtractionRecord convert( Map<String, String> cucumberCardRow ) {
		return new MiningExtractionRecord.Builder()
//				       .withAccountId(Integer.parseInt(cucumberCardRow.get(ACCOUNT_ID)))
//				       .withAccountName(cucumberCardRow.get(ACCOUNT_NAME))
//				       .withDataSource(cucumberCardRow.get(DATA_SOURCE))
//				       .withAccessToken(cucumberCardRow.get(ACCESS_TOKEN))
//				       .withRefreshToken(cucumberCardRow.get(REFRESH_TOKEN))
//				       .withScope(cucumberCardRow.get(SCOPE))
//				       .withWalletBalance((null == cucumberCardRow.get(WALLET_BALANCE)) ? null : Double.parseDouble(
//						       cucumberCardRow.get(WALLET_BALANCE)))
//				       .withAssetsCount((null == cucumberCardRow.get(ASSETS_COUNT)) ? null : Integer.parseInt(
//						       cucumberCardRow.get(ASSETS_COUNT)))
//				       .withRaceName(cucumberCardRow.get(RACE_NAME))
				       .build();
	}
}
