package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.provider.AssetProvider;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetProviderIT {
	private Credential itCredential;
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	private AssetRepository itAssetRepository;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private StoreCacheManager itStoreCacheManager;
	private NeoComRetrofitFactory itRetrofitFactory;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private LocationCatalogService itLocationService;

	@Test
	void runAssetProviderIT() throws SQLException, IOException {
		this.setupEnvironment();
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( this.itCredential )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationService )
				.build();
		Assertions.assertNotNull( provider );

		provider.classifyAssetsByLocation();
	}

	private void setupEnvironment() throws SQLException, IOException {
		this.itCredential = Mockito.mock( Credential.class );
		Mockito.when( itCredential.getAccountId() ).thenReturn( 2113197470 );
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
		this.itRetrofitUniverseConnector = new RetrofitUniverseConnector.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itStoreCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		this.itEsiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withStoreCacheManager( this.itStoreCacheManager )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itRetrofitFactory = new NeoComRetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationRepository( locationRepository )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
	}
}