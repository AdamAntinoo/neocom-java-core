package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.IOException;
import java.sql.SQLException;

import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class IntegrationEnvironmentDefinition {
	protected IConfigurationProvider itConfigurationProvider;
	protected IFileSystem itFileSystemAdapter;
	protected IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	protected AssetRepository itAssetRepository;
	protected StoreCacheManager itStoreCacheManager;
	protected ESIUniverseDataProvider itEsiUniverseDataProvider;
	protected ESIDataProvider esiDataProvider;
	protected LocationCatalogService itLocationService;
	protected RetrofitFactory itRetrofitFactory;

	protected void setupEnvironment() throws SQLException, IOException {
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
		final String neocomDatabaseURL = databaseHostName +
				"/" + databasePath +
				"?user=" + databaseUser +
				"&password=" + databasePassword;
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( neocomDatabaseURL )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.itFileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.itRetrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itStoreCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		this.itEsiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withStoreCacheManager( this.itStoreCacheManager )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		this.esiDataProvider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationCatalogService( this.itLocationService )
				.withRetrofitFactory( this.itRetrofitFactory )
				.withStoreCacheManager( this.itStoreCacheManager )
				.build();
	}
}
