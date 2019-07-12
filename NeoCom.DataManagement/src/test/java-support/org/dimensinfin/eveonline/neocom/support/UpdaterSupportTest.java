package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.junit.Before;

public class UpdaterSupportTest extends EsiDataAdapterSupportTest {
	@Override
	@Before
	public void setUp()  {
		super.setUp();
		NeoComUpdater.injectsEsiDataAdapter(this.esiDataAdapter);
	}
}
