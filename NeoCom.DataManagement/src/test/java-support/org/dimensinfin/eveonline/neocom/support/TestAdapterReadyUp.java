package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

public class TestAdapterReadyUp {
	protected ESIDataAdapter setupRealAdapter() throws IOException {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("properties").build();
		final IFileSystem fileSystemAdapter = new TestFileSystem("./src/test/resources/testStorage/Test.NeoCom");
		return new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
	}
}
