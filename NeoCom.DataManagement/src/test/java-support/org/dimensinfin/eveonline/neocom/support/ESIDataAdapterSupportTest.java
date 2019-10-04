package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;

import org.junit.Before;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapters.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapters.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapters.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;

import static org.mockito.ArgumentMatchers.anyLong;

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
				.withPropertiesDirectory( "properties.unitest" )
				.build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		Mockito.when( this.locationCatalogService.searchLocation4Id( anyLong() ) ).thenReturn( defaultLocation );
		this.retrofitFactoryBuilder = new SupportNeoComRetrofitFactory.Builder();
		this.esiDataAdapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.testingRetrofitFactory( this.retrofitFactoryBuilder )
				.build();
	}
}
