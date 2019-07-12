package org.dimensinfin.eveonline.neocom.core.updaters;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.Pilot;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.support.UpdaterSupportTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PilotUpdaterTest extends UpdaterSupportTest {
	private Credential credential;
	private GetCharactersCharacterIdOk publicData;
	private Pilot pilot;
//	private PilotUpdater updater;

	@Before
	public void setUp() {
		super.setUp();
		credential = new Credential.Builder(92002067)
				.withAccountId(92002067)
				.withAccountName("Adam Antinoo")
				.withAccessToken("lfS7LIBbjLKnglJsujkNERgbwgOE0dCDiudhCdyrBxbxRp1xtFYzTRMxY2G2EssiS44UvvdMfRrXiLtn0SW9Zw")
				.withRefreshToken("oCHpz8dm7MJNZ6PYqRvpWU6IkaD_Z5PsNx9SkI54UkvBY92yIUqEpIiFv03nxnLLnx-w_uTBBmsITYxM7WqzjUio4pXTJJN-GUGb-YNBfe0YNia_fl-NUNlmIGCwIMQCQhLpDZEUmECKUt7Do4T9ZW7FimJrhJUyw5xumUPN-d64oeY7Nd-UO4mc-By8i3aQ")
				.withDataSource("tranquility")
				.withScope("publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1")
				.build();
		publicData = this.esiDataAdapter.getCharactersCharacterId(this.credential.getAccountId());
		pilot = new Pilot.Builder()
				.withCredential(credential)
				.withPilotIdentifier(92002067)
				.withCharacterPublicData(publicData)
				.build();
	}

	@Test
	public void CredentialUpdater_constructor() {
		final Pilot pilot = Mockito.mock(Pilot.class);
		final PilotUpdater updater = new PilotUpdater(pilot);

		Assert.assertNotNull(updater);
	}

	@Test
	public void getIdentifier() {
		final PilotUpdater updater = new PilotUpdater(this.pilot);
		final String obtained = updater.getIdentifier();

		Assert.assertNotNull(updater);
		Assert.assertEquals("Check the updater generated identifier.", "PILOT:92002067", obtained);
	}

	@Test
	public void needsRefresh_needs() {
		final PilotUpdater updater = new PilotUpdater(this.pilot);
		final boolean obtained = updater.needsRefresh();

		Assert.assertTrue("The updater needs refresh.", updater.needsRefresh());
	}

	@Test
	public void needsRefresh_doesnotneed() {
		final PilotUpdater updater = new PilotUpdater(this.pilot);
		updater.getModel().timeStamp();
		final boolean obtained = updater.needsRefresh();

		Assert.assertFalse("This model does not need refresh", obtained);
	}

	@Test
	public void onRun() {
		final PilotUpdater updater = new PilotUpdater(this.pilot);
		// Check data before the run
		Assert.assertNotNull(updater.getModel().getPilotIdentifier());
		Assert.assertNotNull(updater.getModel().getCorporationId());
		Assert.assertNotNull(updater.getModel().getAllianceId());
		Assert.assertNull(updater.getModel().getCorporationData());
		Assert.assertNull(updater.getModel().getAllianceData());
		Assert.assertNull(updater.getModel().getCorporationIconUrl());
		Assert.assertNull(updater.getModel().getAllianceIconUrl());

		// Update the data and check it again.
		updater.onRun();
		Assert.assertNotNull(updater.getModel().getPilotIdentifier());
		Assert.assertNotNull(updater.getModel().getCorporationId());
		Assert.assertNotNull(updater.getModel().getAllianceId());
		Assert.assertNotNull(updater.getModel().getCorporationData());
		Assert.assertNotNull(updater.getModel().getAllianceData());
		Assert.assertNotNull(updater.getModel().getCorporationIconUrl());
		Assert.assertNotNull(updater.getModel().getAllianceIconUrl());
	}
}