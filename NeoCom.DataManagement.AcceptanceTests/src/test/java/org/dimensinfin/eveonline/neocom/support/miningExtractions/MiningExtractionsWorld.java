package org.dimensinfin.eveonline.neocom.support.miningExtractions;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.mining.DailyExtractionResourcesContainer;
import org.joda.time.DateTime;

import java.util.List;

public class MiningExtractionsWorld {
	private Credential credential;
	private List<MiningExtraction> miningExtractionRecords;
	private int hour;
	private int pilotIdentifier;
	private DateTime todayDate;
	private DailyExtractionResourcesContainer resourcesContainer;

	public MiningExtractionsWorld() {
		this.credential = new Credential.Builder(92223647)
				                  .withAccountId(92223647)
				                  .withAccountName("Beth Ripley")
				                  .withAccessToken(
						                  "1|CfDJ8HHFK/DOe6xKoNPHamc0mCW+m3KssaFdj/Gz5yz6LbeUZqk4wH1O/p+at7oiNS9OHwO+YY3wjMe+mXBCSsLKWMnbIf7qXeRyIb4hZZ1EAcrifvXRyD5V+V9NR8f2ti5LTx/QwAwo8g89dRmJyuHoDBFi0D0lpfxJOh9csWRWbozG")
				                  .withRefreshToken(
						                  "bQuMWmfQ8pDzPKaUtkYzFrlrdbajWLbroV6c49QSxPhli3OT2GLqoErgddXgwa2yTWsNEj7zOemFsmey-C4zZE-VV6tdlPHDEUS5w_aU8ckotYF1Ppc3DSdvRGeuVgxLM5CsZq1eNVlQIqyaZDj4aGHk7mRhjuYI8hzhct9Y9vATrF_DdYqvuxhw8RHtfQUc")
				                  .withDataSource("tranquility")
				                  .withScope(
						                  "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1")
				                  .withAssetsCount(1290)
				                  .withWalletBalance(2.76586637596E9)
				                  .withRaceName("Minmatar")
				                  .build();
	}

	public Credential getCredential() {
		return this.credential;
	}

	public List<MiningExtraction> getMiningExtractionRecords() {
		return this.miningExtractionRecords;
	}

	public MiningExtractionsWorld setMiningExtractionRecords( final List<MiningExtraction> miningExtractionRecords ) {
		this.miningExtractionRecords = miningExtractionRecords;
		return this;
	}

	public int getHour() {
		return this.hour;
	}

	public MiningExtractionsWorld setHour( final int hour ) {
		this.hour = hour;
		return this;
	}

	public int getPilotIdentifier() {
		return this.pilotIdentifier;
	}

	public MiningExtractionsWorld setPilotIdentifier( final int pilotIdentifier ) {
		this.pilotIdentifier = pilotIdentifier;
		return this;
	}

	public boolean validateRecord( final MiningExtraction verificationRecord, final MiningExtraction targetRecord ) {
		return verificationRecord.toString().equals(targetRecord.toString());
	}

	public DateTime getTodayDate() {
		return this.todayDate;
	}

	public MiningExtractionsWorld setTodayDate( final DateTime todayDate ) {
		this.todayDate = todayDate;
		return this;
	}

	public DailyExtractionResourcesContainer getResourcesContainer() {
		return this.resourcesContainer;
	}

	public MiningExtractionsWorld setResourcesContainer( final DailyExtractionResourcesContainer resourcesContainer ) {
		this.resourcesContainer = resourcesContainer;
		return this;
	}
}
