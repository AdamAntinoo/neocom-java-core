package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;

import org.junit.Before;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapters.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapters.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapters.LocationCatalogService;

public class ESIDataAdapterSupportTest {
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected LocationCatalogService locationCatalogService;
	protected ESIDataAdapter esiDataAdapter;

	@Before
	public void setUp() throws IOException {
		configurationProvider = new SupportConfigurationProvider.Builder()
				.withPropertiesDirectory( "properties.unitest" )
				.build();
		fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.esiDataAdapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.build();
	}
}
