package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationEnvironmentDefinition;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;

public class LocationCatalogServiceIT extends IntegrationEnvironmentDefinition {
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final long LOCATION_ID_STATION_4TEST = 60006526L;
	private static final long LOCATION_ID_STRUCTURE_4TEST = 1032555370327L;
//	private static final GenericContainer<?> esisimulator;
//	private static Credential credential4Test;
//
//	static {
//		esisimulator = new GenericContainer<>( "apimastery/apisimulator" )
//				.withExposedPorts( ESI_UNITTESTING_PORT )
//				.withFileSystemBind( "/home/adam/Development/NeoCom/neocom-datamanagement/NeoCom.DataManagement/src/test/resources/esi-unittesting",
//						"/esi-unittesting",
//						BindMode.READ_WRITE )
//				.withCommand( "bin/apisimulator start /esi-unittesting" );
//		esisimulator.start();
//	}

	@BeforeAll
	public static void beforeAll() {
		credential4Test = Mockito.mock( Credential.class );
		Mockito.when( credential4Test.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential4Test.getDataSource() ).thenReturn( "tranquility" );
	}

	@BeforeEach
	public void beforeEach() throws IOException, SQLException {
		this.setupEnvironment();
	}

	@Test
	public void buildComplete() {
		final IConfigurationProvider configurationProvider = Mockito.mock( IConfigurationProvider.class );
		final IFileSystem fileSystem = Mockito.mock( IFileSystem.class );
		final ESIUniverseDataProvider esiUniverseProvider = Mockito.mock( ESIUniverseDataProvider.class );
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final LocationCatalogService locationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystem )
				.withESIUniverseDataProvider( esiUniverseProvider )
				.withRetrofitFactory( retrofitFactory )
				.build();
		Assertions.assertNotNull( locationService );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
					.withConfigurationProvider( this.itConfigurationProvider )
					.withFileSystemAdapter( this.itFileSystemAdapter )
					.withESIUniverseDataProvider( null )
					.withRetrofitFactory( this.itRetrofitFactory )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
					.withConfigurationProvider( this.itConfigurationProvider )
					.withFileSystemAdapter( this.itFileSystemAdapter )
					.withRetrofitFactory( this.itRetrofitFactory )
					.build();
		} );
	}

	@Test
	public void searchLocation4Id() throws IOException {
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();

		final SpaceLocation location = locationCatalogService.searchLocation4Id( LOCATION_ID_STATION_4TEST );
		Assertions.assertNotNull( location );
	}

	@Test
	public void searchStructure4Id() throws IOException {
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();

		final SpaceLocation location = locationCatalogService.searchStructure4Id( LOCATION_ID_STRUCTURE_4TEST, credential4Test );
		Assertions.assertNotNull( location );
	}

//	@Test
//	public void stopService() {
//		final LocationCatalogService locationServiceSpy = Mockito.spy( LocationCatalogService.class );
//
//		locationServiceSpy.stopService();
//
//		Mockito.verify( locationServiceSpy, Mockito.times( 1 ) ).writeLocationsDataCache();
//		Mockito.verify( locationServiceSpy, Mockito.times( 1 ) ).cleanLocationsCache();
//	}
}
