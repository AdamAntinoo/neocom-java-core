package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.junit.Before;

public class EsiDataAdapterSupportTest {
	protected ESIDataAdapter esiDataAdapter;

	@Before
	public void setUp() {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("properties").build();
		final IFileSystem fileSystemAdapter = new TestFileSystem();
		this.esiDataAdapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
		EveItem.injectEsiDataAdapter(this.esiDataAdapter);
	}
}
