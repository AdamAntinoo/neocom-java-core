package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

public class LocationCatalogServiceIT extends IntegrationEnvironmentDefinitionTCLocal {
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final long LOCATION_ID_STATION_4TEST = 60006526L;
	private static final long LOCATION_ID_STRUCTURE_4TEST = 1032555370327L;

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
	public void callClean() {
		// Given
		final IConfigurationProvider configurationProvider = Mockito.mock( IConfigurationProvider.class );
		final IFileSystem fileSystem = Mockito.mock( IFileSystem.class );
		final ESIUniverseDataProvider esiUniverseProvider = Mockito.mock( ESIUniverseDataProvider.class );
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystem )
				.withRetrofitFactory( itRetrofitFactory )
				.withESIUniverseDataProvider( esiUniverseProvider )
				.build();
		// Test
		final boolean obtained = locationCatalogService.call();
		final boolean expected = false;
		// Assertions
		Assertions.assertNotNull( locationCatalogService );
		Assertions.assertEquals( expected, obtained );
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
