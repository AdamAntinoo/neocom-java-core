package org.dimensinfin.eveonline.neocom.mining.updaters;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.support.UpdaterSupportTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MiningExtractionUpdaterTest extends UpdaterSupportTest {
	private static Credential model2Test;
	private static MiningExtractionUpdater updater2Test;

	@Before
	public void setUp() {
		super.setUp();
		final MiningRepository miningRepository = Mockito.mock(MiningRepository.class);
		model2Test = new Credential.Builder(123456).withAccountName("TEST CREDENTIAL").build();
		updater2Test = new MiningExtractionUpdater.Builder(model2Test)
				               .withMiningRepository(miningRepository)
				               .build();
		Assert.assertNotNull(updater2Test);
	}

	@Test(expected = NullPointerException.class)
	public void builder_failure() {
		final MiningExtractionUpdater updater = new MiningExtractionUpdater.Builder(model2Test).build();
	}

	@Test
	public void getIdentifier() {
		final String obtained = updater2Test.getIdentifier();

		Assert.assertNotNull(updater2Test);
		Assert.assertEquals("Check the updater2Test generated identifier.", "MININGEXTRACTIONUPDATER:123456", obtained);
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
}