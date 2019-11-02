package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;

import org.junit.Before;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapter.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;

public class ESIDataAdapterSupportTest {
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected LocationCatalogService locationCatalogService;
	protected SupportNeoComRetrofitFactory.Builder retrofitFactoryBuilder;
	protected ESIDataAdapter esiDataAdapter;
	private EsiLocation defaultLocation;

	@Before
	public void setUp() throws IOException {
		 this.defaultLocation = new EsiLocation.Builder()
		 .withClassType( LocationClass.SYSTEM )
		 .withRegionId( 10000041 )
		 .withConstellationId( 20000479 )
		 .withSystemId( 30003283 )
				 .withRegionName( "Syndicate" )
				 .withConstellationName( "2-M6DE" )
		 .withSystemName( "PVH8-0" )
		 .build();
		this.configurationProvider = new SupportConfigurationProvider.Builder()
				.withPropertiesDirectory( "properties.unittest" )
				.build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( this.locationCatalogService.searchLocation4Id( anyLong() ) ).thenReturn( defaultLocation );
		this.retrofitFactoryBuilder = new SupportNeoComRetrofitFactory.Builder();
		this.esiDataAdapter = new ESIDataAdapter.Builder() // Use an special configuration to call the mock server
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
//				.testingRetrofitFactory( this.retrofitFactoryBuilder ) // This is not longer required
				.build();
	}
}
