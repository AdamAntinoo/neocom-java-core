package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;

import org.junit.Before;

import org.dimensinfin.eveonline.neocom.updaters.NeoComUpdater;

public class UpdaterSupportTest extends ESIDataAdapterSupportTest {
	@Override
	@Before
	public void setUp() throws IOException {
		super.setUp();
		NeoComUpdater.injectsEsiDataAdapter(this.esiDataAdapter);
	}
}
