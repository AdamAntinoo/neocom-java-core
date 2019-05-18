package org.dimensinfin.eveonline.neocom.services;

import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TimedUpdaterTest {

	@Test
	public void builder_complete() {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final TimedUpdater obtained = new TimedUpdater.Builder()
				                              .withESIAdapter(adapter)
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
