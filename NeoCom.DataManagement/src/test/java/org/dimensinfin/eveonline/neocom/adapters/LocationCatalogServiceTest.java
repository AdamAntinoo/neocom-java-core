package org.dimensinfin.eveonline.neocom.adapters;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

public class LocationCatalogServiceTest {
	@Test
	public void buildComplete() {
		final IFileSystem fileSystem = Mockito.mock(IFileSystem.class);
		final IConfigurationProvider configurationProvider=Mockito.mock(IConfigurationProvider.class);
		final ESIUniverseDataProvider esiUniverseProvider = Mockito.mock(ESIUniverseDataProvider.class);
		final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
		final LocationCatalogService locationService = new LocationCatalogService.Builder(  )
				.withFileSystemAdapter( fileSystem )
				.withConfigurationProvider( configurationProvider )
				.withESIUniverseDataProvider( esiUniverseProvider )
				.withLocationRepository( locationRepository )
				.build();
		Assert.assertNotNull(locationService);
	}

	@Test
	public void stopService() {
		final LocationCatalogService locationServiceSpy=Mockito.spy( LocationCatalogService.class );

		locationServiceSpy.stopService();

		Mockito.verify( locationServiceSpy, Mockito.times(1) ).writeLocationsDataCache();
		Mockito.verify( locationServiceSpy, Mockito.times(1) ).cleanLocationsCache();
	}
}