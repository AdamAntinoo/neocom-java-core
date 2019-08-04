package org.dimensinfin.eveonline.neocom.core.updaters;

import org.dimensinfin.eveonline.neocom.adapters.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.UpdaterSupportTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CredentialUpdaterTest extends UpdaterSupportTest {
	private static Credential model2Test;
	private static CredentialUpdater updater2Test;

	@Before
	public void setUp() {
		super.setUp();
		model2Test = new Credential.Builder(123456).withAccountName("TEST CREDENTIAL").build();
		updater2Test = new CredentialUpdater(model2Test);
	}

	@Test
	public void CredentialUpdater_constructor() {
		final Credential credential = Mockito.mock(Credential.class);
		final CredentialUpdater updater = new CredentialUpdater(credential);

		Assert.assertNotNull(updater);
	}

	@Test
	public void getIdentifier() {
		final Credential model = new Credential.Builder(123456).withAccountName("TEST CREDENTIAL").build();
		final CredentialUpdater updater = new CredentialUpdater(model);
		final String obtained = updater.getIdentifier();

		Assert.assertNotNull(updater);
		Assert.assertEquals("Check the updater2Test generated identifier.", "CREDENTIAL:123456", obtained);
	}

	@Test
	public void needsRefresh_needs() {
		final boolean obtained = updater2Test.needsRefresh();
		Assert.assertTrue("This model2Test needs refresh", obtained);
	}

	@Test
	public void needsRefresh_doesnotneed() {
		updater2Test.getModel().timeStamp();
		final boolean obtained = updater2Test.needsRefresh();
		Assert.assertFalse("This model2Test does not need refresh", obtained);
	}

	@Test
	public void onRun() {
		final Credential credential = new Credential.Builder(93813310)
				                              .withAccountId(93813310)
				                              .withAccountName("TEST CREDENTIAL")
				                              .withAccessToken("P940P9FpVhR8oq2V96D7pbcLzndNWTsAVgVAMt0HE5tJT15zg83MMqfsZhW1yf1XoFn9_IQJN5LrIa3NA90Ifw")
				                              .withRefreshToken("52HSB2sQiYBOrvaPidnxvnc-DIgT7DP5gUoCEOCW4v61dBfHOrCplfuwma0En0eZsLff2L6OJ6csIDTEQhqDmr0iVB6XmuNloTYhTT2Lx-x15j37Oo91jRrbHiC414DMX2nDPz-JGAdPLDtOzG2-4ofHR61rvw7sGY8Z1CnAgdGexAN6M4ZX93D_UWBEvlFd")
				                              .withDataSource("tranquility")
				                              .withScope("publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1")
				                              .withAssetsCount(1476)
				                              .withWalletBalance(6.309543632E8)
				                              .withRaceName("Amarr")
				                              .build();
		NeoComRetrofitFactory.add2MockList("getCharactersCharacterIdAssets");
		final CredentialUpdater updater = new CredentialUpdater(credential);
		NeoComUpdater.injectsEsiDataAdapter(this.esiDataAdapter);
		updater.onRun();
		Assert.assertEquals("Check the number of assets.", 5, updater.getModel().getAssetsCount());
		Assert.assertEquals("Check the wallet amount.",
		                    6.309543632E8, updater.getModel().getWalletBalance(), 0.01);
		Assert.assertEquals("Check the value of mineral resources",
		                    4.7, updater.getModel().getMiningResourcesEstimatedValue(), 0.01);
		Assert.assertEquals("Check the value of the race",
		                    "Amarr", updater.getModel().getRaceName());
	}
}
