package org.dimensinfin.eveonline.neocom.core.updaters;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.TestAdapterReadyUp;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CredentialUpdaterTest extends TestAdapterReadyUp {
	@Test
	public void CredentialUpdater_constructor() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		NeoComUpdater.injectsEsiDataAdapter(esiDataAdapter);
		final Credential credential = Mockito.mock(Credential.class);
		final CredentialUpdater updater = new CredentialUpdater(credential);

		Assert.assertNotNull(updater);
	}
}
