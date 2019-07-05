package org.dimensinfin.eveonline.neocom.support.credential;

import java.util.Map;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;

import org.springframework.stereotype.Component;

@Component
public class CucumberTableToCredentialConverter extends CucumberTableConverter<Credential> {
	private static final String ACCOUNT_ID = "accountId";
	private static final String ACCOUNT_NAME = "accountName";
	private static final String DATA_SOURCE = "dataSource";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String SCOPE = "scope";
	private static final String WALLET_BALANCE = "walletBalance";
	private static final String ASSETS_COUNT = "assetsCount";
	private static final String RACE_NAME = "raceName";

	@Override
	public Credential convert( Map<String, String> cucumberCardRow ) {
		return new Credential.Builder(Integer.parseInt(cucumberCardRow.get(ACCOUNT_ID)))
				       .withAccountId(Integer.parseInt(cucumberCardRow.get(ACCOUNT_ID)))
				       .withAccountName(cucumberCardRow.get(ACCOUNT_NAME))
				       .withDataSource(cucumberCardRow.get(DATA_SOURCE))
				       .withAccessToken(cucumberCardRow.get(ACCESS_TOKEN))
				       .withRefreshToken(cucumberCardRow.get(REFRESH_TOKEN))
				       .withScope(cucumberCardRow.get(SCOPE))
				       .withWalletBalance((null == cucumberCardRow.get(WALLET_BALANCE)) ? null : Double.parseDouble(cucumberCardRow.get(WALLET_BALANCE)))
				       .withAssetsCount((null == cucumberCardRow.get(ASSETS_COUNT)) ? null : Integer.parseInt(cucumberCardRow.get(ASSETS_COUNT)))
				       .withRaceName(cucumberCardRow.get(RACE_NAME))
				       .build();
	}
}
