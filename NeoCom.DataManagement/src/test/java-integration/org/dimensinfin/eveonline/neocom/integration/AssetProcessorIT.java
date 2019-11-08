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

import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
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
	private Credential itCredential;
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
	private ESIDataProvider itEsiDataProvider;


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
			application.waitSchedulerCompletion();
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

	private void waitSchedulerCompletion() {
		this.itJobScheduler.wait4Completion();
	}

	private void setUpEnvironment() throws IOException, SQLException {
		this.itCredential = new Credential.Builder(2113197470)
				.withAccountId(2113197470)
				.withAccountName("Tip Tophane")
				.withAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0.eyJqdGkiOiI0N2JlMzdlYi05MjdhLTRlZWEtOGQwYS03NjgwZDg3OTkwZjkiLCJraWQiOiJKV1QtU2lnbmF0dXJlLUtleSIsInN1YiI6IkNIQVJBQ1RFUjpFVkU6MjExMzE5NzQ3MCIsImF6cCI6Ijk4ZWI4ZDMxYzVkMjQ2NDliYTRmN2ViMDE1NTk2ZmJkIiwibmFtZSI6IlRpcCBUb3BoYW5lIiwib3duZXIiOiJYK1JkU0ZMa2VXK3dhRGtyWHNWdEZXUXZSWlk9IiwiZXhwIjoxNTczMTM4NjIzLCJpc3MiOiJsb2dpbi5ldmVvbmxpbmUuY29tIn0.LxsNGUhu4w5cnXhkJtPx6yk73ENo3r5Kl-GZB8cn4Z5Mc2gRxMTNnE5BjauZzSmcHP3XXaBFN_ViHv_3Kv3Xx4iCAqYmGN6OdyadSGr6G81jwY-HFQgSIJJVHzYrGaniZQFnF50I9VoeNKHmMLDXMg2BBP6FH5on3NWUV0qaNwbcKmaL1q7R9SR_1yR-2zpM4uhufDDCjA9nJV5EpzsQc0UZNbUZQb5FZ5OGjbLs-wM8BnGzKAKdilrRpQug9xdMfaxK2yu-b_nypQcdCvf4Po7yhHKcPaoNHAHlJyI-UgdwMzDZ5lASIrFYeWLv0yFumFoA76Puj74Lql2ORp1zHw")
				.withRefreshToken("xh52x86M60yljn5U5wM0dw==")
				.withDataSource("tranquility")
				.withScope("publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1")
				.withAssetsCount(6119)
				.withWalletBalance(2.27058387661E9)
				.withRaceName("Minmatar")
				.build();
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
		final List<GetCharactersCharacterIdAssets200Ok> testAssetList = this.loadAssetTestData();
//		final Credential credential = Mockito.mock( Credential.class );
		this.itEsiDataProvider = Mockito.mock( ESIDataProvider.class );
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
//		final Credential credential = Mockito.mock( Credential.class );
//		Mockito.when( credential.getAccountId() ).thenReturn( 122345 );
//		Mockito.when( credential.getAccountName() ).thenReturn( "-TEST-" );
//		Mockito.when( credential.getAccessToken() ).thenReturn( "-ACCESS-TOKEN-" );
//		Mockito.when( credential.getRefreshToken() ).thenReturn( "-REFRESH-TOKEN-" );
//		Mockito.when( credential.getDataSource() ).thenReturn( ESIDataProvider.DEFAULT_ESI_SERVER );
//		Mockito.when( credential.getScope() ).thenReturn( "publicData" );
//		Mockito.when( credential.getUniqueId() ).thenReturn( "tranquility/12345" );

		final Job assetProcessorJob = new AssetDownloadProcessor.Builder()
				.withCredential( this.itCredential )
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