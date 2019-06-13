package org.dimensinfin.eveonline.neocom.services;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.conf.IGlobalPreferencesManager;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TimedUpdaterTest {

	@Test
	public void builder_complete() {
		final ESIDataAdapter adapter = Mockito.mock(ESIDataAdapter.class);
		final IGlobalPreferencesManager preferencesProvider = Mockito.mock(IGlobalPreferencesManager.class);
		final ESIDataPersistenceService esiDataPersistenceService = Mockito.mock(ESIDataPersistenceService.class);
		final CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
		final TimedUpdater obtained = new TimedUpdater.Builder()
				                              .withESIAdapter(adapter)
											  .withPreferencesProvider(preferencesProvider)
											  .withESIDataPersistenceService(esiDataPersistenceService)
											  .withCredentialRepository(credentialRepository)
				                              .build();
		Assert.assertNotNull(obtained);
	}

	@Test(expected = NullPointerException.class)
	public void builder_null() {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final TimedUpdater obtained = new TimedUpdater.Builder()
				                              .withESIAdapter(null)
				                              .build();
		Assert.assertNotNull(obtained);
	}
}
