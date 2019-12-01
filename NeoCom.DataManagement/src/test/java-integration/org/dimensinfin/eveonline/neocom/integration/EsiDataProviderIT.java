package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class EsiDataProviderIT {
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final Logger logger = LoggerFactory.getLogger( EsiDataProviderIT.class );
	private static final GenericContainer<?> esisimulator;

	static {
		esisimulator = new GenericContainer<>( "apimastery/apisimulator" )
				.withExposedPorts( ESI_UNITTESTING_PORT )
				.withFileSystemBind( "/home/adam/Development/NeoCom/neocom-datamanagement/NeoCom.DataManagement/src/test/resources/esi-unittesting",
						"/esi-unittesting",
						BindMode.READ_WRITE )
				.withCommand( "bin/apisimulator start /esi-unittesting" );
		esisimulator.start();
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer( logger );
		esisimulator.followOutput( logConsumer );
	}

	protected SBConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected RetrofitFactory retrofitFactory;
	private ESIDataProvider esiDataProvider;

//	@Rule
//	public GenericContainer<?> esisimulator = new GenericContainer<>( "apimastery/apisimulator" )
//			.withExposedPorts( 6090 )
//			.withFileSystemBind( "/home/adam/Development/NeoCom/neocom-datamanagement/NeoCom.DataManagement/src/test/resources/esi-unittesting",
//					"/esi-unittesting",
//					BindMode.READ_WRITE )
//			.withCommand( "/apisimulator/apisimulator-http-1.4/bin/apisimulator start /esi-unittesting" );

	@Test
	public void getCharactersCharacterIdMining() {
//		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
//		this.esisimulator.start();
//		this.esisimulator.followOutput(logConsumer);
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		final List<GetCharactersCharacterIdMining200Ok> extractions = this.esiDataProvider.getCharactersCharacterIdMining( credential );
		Assertions.assertNotNull( extractions );
		Assertions.assertEquals( 6, extractions.size() );
	}

	@BeforeEach
	void setUp() throws IOException {
		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
		this.configurationProvider.setProperty( "P.authenticated.retrofit.server.location",
				"http://" +
						esisimulator.getContainerIpAddress() +
						":" + esisimulator.getMappedPort( ESI_UNITTESTING_PORT ) +
						"/latest/" );
		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest/" )
				.build();
		this.retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		this.esiDataProvider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.withStoreCacheManager( storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
	}
}
