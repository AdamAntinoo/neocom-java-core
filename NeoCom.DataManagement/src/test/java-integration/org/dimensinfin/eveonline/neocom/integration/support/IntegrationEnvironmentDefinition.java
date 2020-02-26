package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class IntegrationEnvironmentDefinition {
	protected static final Logger logger = LoggerFactory.getLogger( IntegrationEnvironmentDefinition.class );
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final GenericContainer<?> esisimulator;
	private static final PostgreSQLContainer postgres;
	private static final String connectionUrl;
	private static final JdbcConnectionSource connectionSource;

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

	static {
		JdbcConnectionSource connectionSource1;
		postgres = new PostgreSQLContainer( "postgres:9.6.8" )
				.withDatabaseName( "postgres" )
				.withUsername( "neocom" )
				.withPassword( "01.Alpha" );
		postgres.start();
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer( logger );
		esisimulator.followOutput( logConsumer );
		connectionUrl = "jdbc:postgresql://"
				+ postgres.getContainerIpAddress()
				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
				+ "/" + "postgres" +
				"?user=" + "neocom" +
				"&password=" + "01.Alpha";
		NeoComLogger.info( "Postgres SQL URL: {}", connectionUrl );
		try {
			connectionSource1 = new JdbcConnectionSource( connectionUrl, new PostgresDatabaseType() );
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			connectionSource1 = null;
		}
		connectionSource = connectionSource1;
	}

	protected SBConfigurationProvider itConfigurationProvider;
	protected IFileSystem itFileSystemAdapter;
	protected IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	protected AssetRepository itAssetRepository;
	protected CredentialRepository itCredentialRepository;
	protected StoreCacheManager itStoreCacheManager;
	protected ESIUniverseDataProvider itEsiUniverseDataProvider;
	protected ESIDataProvider esiDataProvider;
	protected LocationCatalogService itLocationCatalogService;
	protected RetrofitFactory itRetrofitFactory;

	protected void setupEnvironment() throws SQLException, IOException {
//		final IntegrationNeoComDBAdapter neocomDBAdapter = new IntegrationNeoComDBAdapter.Builder()
//				.withDatabaseURLConnection( connectionUrl )
//				.build();
//		NeoComUnitTestComponentFactory.getSingleton().setNeoComDBAdapter( neocomDBAdapter );

		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		this.itConfigurationProvider.setProperty( "P.authenticated.retrofit.server.location",
				"http://" +
						esisimulator.getContainerIpAddress() +
						":" + esisimulator.getMappedPort( ESI_UNITTESTING_PORT ) +
						"/latest/" );
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
//		final String neocomDatabaseURL = databaseHostName +
//				"/" + databasePath +
//				"?user=" + databaseUser +
//				"&password=" + databasePassword;
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( connectionUrl )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.itCredentialRepository = Mockito.mock( CredentialRepository.class );
		Mockito.doAnswer( ( credential ) -> {
			return null;
		} ).when( this.itCredentialRepository ).persist( Mockito.any( Credential.class ) );
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
		this.itLocationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		this.esiDataProvider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withRetrofitFactory( this.itRetrofitFactory )
				.withStoreCacheManager( this.itStoreCacheManager )
				.build();
	}
}
