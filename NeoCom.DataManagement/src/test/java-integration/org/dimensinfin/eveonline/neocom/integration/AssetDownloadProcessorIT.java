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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessor;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.integration.support.GetCharactersCharacterIdAssets200OkDeserializer;
import org.dimensinfin.eveonline.neocom.integration.support.GroupCount;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.integration.support.SupportIntegrationCredential;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.HourlyCronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetDownloadProcessorIT {
	private final ObjectMapper mapper = new ObjectMapper();
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private JobScheduler itJobScheduler;
	private AssetRepository itAssetRepository;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private RetrofitFactory itRetrofitFactory;
	private LocationCatalogService itLocationService;
	private StoreCacheManager itStoreCacheManager;
	private ESIDataProvider itEsiDataProvider;

	private AssetDownloadProcessor assetProcessorJob;
	private List<GroupCount> groupCounts;

	private AssetDownloadProcessorIT() {}

	public static void main( String[] args ) {
		NeoComLogger.enter();
		final AssetDownloadProcessorIT application = new AssetDownloadProcessorIT();
		try {
			application.setUpEnvironment();
			application.registerJobOnScheduler();
			application.itJobScheduler.runSchedule();
			application.waitSchedulerCompletion();
			application.checkAssertions();
		} catch (IOException ioe) {
			NeoComLogger.info( "Application interrupted: ", ioe );
		} catch (SQLException sqle) {
			NeoComLogger.info( "Application interrupted: ", sqle );
		}
		NeoComLogger.exit();
	}

	@Test
	void runAssetProcessorIT() {
		AssetDownloadProcessorIT.main( null );
	}

	private void checkAssertions() throws IOException {
		Assertions.assertNotNull( this.itJobScheduler );
		Assertions.assertNotNull( this.assetProcessorJob );

		final List<NeoAsset> assets = this.itAssetRepository
				.findAllByOwnerId( SupportIntegrationCredential.itCredential.getAccountId() );
		Assertions.assertEquals( 36, assets.size() );

		this.readGroupCounts();
		int propulsionCount = 0;
		int miningLasertCount = 0;
		for (NeoAsset asset : assets) {
			if (asset.getGroupName().equalsIgnoreCase( "Mining Laser" )) miningLasertCount++;
			if (asset.getGroupName().equalsIgnoreCase( "Propulsion Module" )) propulsionCount++;
		}
//		Assertions.assertEquals( 4, miningLasertCount );
//		Assertions.assertEquals( 5, propulsionCount );
		for (GroupCount count : this.groupCounts) {
			if (count.getGroup().equalsIgnoreCase( "Mining Laser" ))
				Assertions.assertEquals( miningLasertCount, count.getCount() );
			if (count.getGroup().equalsIgnoreCase( "Propulsion Module" ))
				Assertions.assertEquals( propulsionCount, count.getCount() );
		}
	}

	private void readGroupCounts() throws IOException {
		final File groupCountsFile = new File( this.itFileSystemAdapter.accessResource4Path( "/TestData/groupsCounts.json" ) );
		this.groupCounts = mapper.readValue( groupCountsFile,
				mapper.getTypeFactory().constructCollectionType( List.class, GroupCount.class ) );
	}

	private void waitSchedulerCompletion() {
		this.itJobScheduler.wait4Completion();
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
		this.itRetrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.build();
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationRepository( locationRepository )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final List<GetCharactersCharacterIdAssets200Ok> testAssetList = this.loadAssetTestData();
		this.itEsiDataProvider = Mockito.mock( ESIDataProvider.class );
		Mockito.when( this.itEsiDataProvider.getCharactersCharacterIdAssets( Mockito.any( Credential.class ) ) )
				.thenReturn( testAssetList );
	}

	private List<GetCharactersCharacterIdAssets200Ok> loadAssetTestData() throws IOException {
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
		this.assetProcessorJob = new AssetDownloadProcessor.Builder()
				.withCredential( SupportIntegrationCredential.itCredential )
				.withEsiDataProvider( this.itEsiDataProvider )
				.withLocationCatalogService( this.itLocationService )
				.withAssetRepository( this.itAssetRepository )
				.withNeoAssetConverter( new GetCharactersCharacterIdAsset2NeoAssetConverter() )
				.addCronSchedule( "* - *" )
				.build();
		this.itJobScheduler.registerJob( this.assetProcessorJob );
	}
}
