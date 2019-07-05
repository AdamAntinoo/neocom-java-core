package org.dimensinfin.eveonline.neocom.core.updaters;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.TestAdapterReadyUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CredentialUpdaterTest extends TestAdapterReadyUp {
	private static Credential model;
	private static CredentialUpdater updater;

	@Before
	public void setUp() throws Exception {
		model = new Credential.Builder(123456).withAccountName("TEST CREDENTIAL").build();
		updater = new CredentialUpdater(model);
	}

	@Test
	public void CredentialUpdater_constructor() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		NeoComUpdater.injectsEsiDataAdapter(esiDataAdapter);
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
		Assert.assertEquals("Check the updater generated identifier.", "CREDENTIAL:123456", obtained);
	}

	@Test
	public void needsRefresh_needs() {
		final boolean obtained = updater.needsRefresh();
		Assert.assertTrue("This model needs refresh", obtained);
	}

	@Test
	public void needsRefresh_doesnotneed() {
		updater.getModel().timeStamp();
		final boolean obtained = updater.needsRefresh();
		Assert.assertFalse("This model does not need refresh", obtained);
	}
}
