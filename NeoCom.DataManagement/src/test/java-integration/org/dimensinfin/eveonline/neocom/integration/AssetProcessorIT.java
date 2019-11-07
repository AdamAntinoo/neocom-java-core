package org.dimensinfin.eveonline.neocom.integration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import org.dimensinfin.eveonline.neocom.adapter.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessor;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.integration.support.GetCharactersCharacterIdAssets200OkDeserializer;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.HourlyCronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetProcessorIT {
	private GenericContainer postgres;

	private final ObjectMapper mapper = new ObjectMapper();
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private JobScheduler itJobScheduler;
	private AssetRepository itAssetRepository;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private NeoComRetrofitFactory itRetrofitFactory;
	private LocationCatalogService itLocationService;
	private StoreCacheManager itStoreCacheManager;
	private ESIDataAdapter itEsiDataProvider;


	private AssetProcessorIT() {}

	public static void main( String[] args ) {
		NeoComLogger.enter();
		final AssetProcessorIT application = new AssetProcessorIT();
		try {
//			application.startContainers();
			application.setUpEnvironment();
			application.registerJobOnScheduler();
			application.itJobScheduler.runSchedule();
//			application.stopContainers();
		} catch (IOException ioe) {
			NeoComLogger.info( "Application interrupted: ", ioe );
		} catch (SQLException sqle) {
			NeoComLogger.info( "Application interrupted: ", sqle );
		}
		NeoComLogger.exit();
	}

	private void startContainers() {
//		this.postgres = new GenericContainer<>( "postgres:11.2" )
//				.withExposedPorts( 5432 )
//				.withEnv( "POSTGRES_DB", "postgres" )
//				.withEnv( "POSTGRES_USER", "neocom" )
//				.withEnv( "POSTGRES_PASSWORD", "01.Alpha" );

		this.postgres = new PostgreSQLContainer( "postgres:9.6-alpine" )
				.withDatabaseName( "postgres" )
				.withPassword( "01.Alpha" )
				.withUsername( "neocom" )
				.withExposedPorts( 5432 )
				.withLogConsumer( new Slf4jLogConsumer( LoggerFactory.getLogger( "🐳 " + "postgres" ) ) );
//				.withNetwork(network);

		this.postgres.start();
	}

	private void stopContainers() {
		this.postgres.stop();
	}

	private void setUpEnvironment() throws IOException, SQLException {
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		this.itFileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.itJobScheduler = new JobScheduler.Builder()
				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() ).build();
		// Database setup
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
//		final String containerHost = this.postgres.getContainerIpAddress();
//		final Integer containerPort = this.postgres.getFirstMappedPort();
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
		final String neocomDatabaseURL = databaseHostName +
				"/" + databasePath +
				"?user=" + databaseUser +
				"&password=" + databasePassword;
//		final String postgresTestContainerUrl = this.postgres.get
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( neocomDatabaseURL )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.itRetrofitUniverseConnector = new RetrofitUniverseConnector.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itStoreCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystem( this.itFileSystemAdapter )
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
		final List<GetCharactersCharacterIdAssets200Ok> testAssetList = this.loadAssetTestData();
		final Credential credential = Mockito.mock( Credential.class );
		this.itEsiDataProvider = Mockito.mock( ESIDataAdapter.class );
		Mockito.when( this.itEsiDataProvider.getCharactersCharacterIdAssets( Mockito.any( Credential.class ) ) )
				.thenReturn( testAssetList );
		this.itRetrofitFactory.add2MockList( "getCharactersCharacterIdAssets" );
	}

	private List<GetCharactersCharacterIdAssets200Ok> loadAssetTestData() throws IOException {
//		this.mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//		ObjectMapper mapper = new ObjectMapper();
		SimpleModule testModule = new SimpleModule( "NoeComIntegrationModule",
				Version.unknownVersion() );
		testModule.addDeserializer( GetCharactersCharacterIdAssets200Ok.class,
				new GetCharactersCharacterIdAssets200OkDeserializer( GetCharactersCharacterIdAssets200Ok.class ) );
		mapper.registerModule( testModule );

		final GetCharactersCharacterIdAssets200Ok[] data = this.mapper.readValue( FileUtils.readFileToString(
				new File( this.itFileSystemAdapter.accessResource4Path( "TestData/assetTestList.json" ) ),
				"utf-8" ), GetCharactersCharacterIdAssets200Ok[].class );
		return new ArrayList<>( Arrays.asList( data ) );
	}

	private void registerJobOnScheduler() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 122345 );
		Mockito.when( credential.getAccountName() ).thenReturn( "-TEST-" );
		Mockito.when( credential.getAccessToken() ).thenReturn( "-ACCESS-TOKEN-" );
		Mockito.when( credential.getRefreshToken() ).thenReturn( "-REFRESH-TOKEN-" );
		Mockito.when( credential.getDataSource() ).thenReturn( ESIDataAdapter.DEFAULT_ESI_SERVER );
		Mockito.when( credential.getScope() ).thenReturn( "publicData" );
		Mockito.when( credential.getUniqueId() ).thenReturn( "tranquility/12345" );

		final Job assetProcessorJob = new AssetDownloadProcessor.Builder()
				.withCredential( credential )
				.withEsiDataAdapter( this.itEsiDataProvider )
				.withLocationCatalogService( this.itLocationService )
				.withAssetRepository( this.itAssetRepository )
				.withNeoAssetConverter( new GetCharactersCharacterIdAsset2NeoAssetConverter() )
				.addCronSchedule( "* - *" )
				.build();
		this.itJobScheduler.registerJob( assetProcessorJob );
	}

//	@Test
//	void downloadAssets() {
//		this.registerJobOnScheduler();
//		this.itJobScheduler.runSchedule();
//	}
}