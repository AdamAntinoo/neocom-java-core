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
	protected SupportNeoComRetrofitFactory.Builder retrofitFactoryBuilder;
	protected ESIDataAdapter esiDataAdapter;

	@Before
	public void setUp() throws IOException {
		this.configurationProvider = new SupportConfigurationProvider.Builder()
				.withPropertiesDirectory( "properties.unitest" )
				.build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.retrofitFactoryBuilder = new SupportNeoComRetrofitFactory.Builder();
		this.esiDataAdapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.testingRetrofitFactory( this.retrofitFactoryBuilder )
				.build();
	}
}
