package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationCredentialStore;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

import io.reactivex.Single;

/**
 * Tests for repository classes will be executed under a integration environment. There should be a real database instance
 * implemented by the TestContainers platform or by an external docker instance prepared for integration.
 *
 * Test should manage the database to clear and insert test data to check all the exported api methods.
 *
 * Repository instances only depend on Dao components. So they should be provided from the integration environment.
 */
public class MiningRepositoryIT {
	private static final Integer characterId = 2113197470;

	private String connectionUrl;
	private JdbcConnectionSource connectionSource;
	private Dao<MiningExtraction, String> miningDao;
	private String expectedVerifiedId;
	// - C O M P O N E N T S
	private IntegrationCredentialStore integrationCredentialStore;
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private RetrofitFactory retrofitFactory;
	private ESIUniverseDataProvider esiUniverseDataProvider;
	private MiningRepository miningRepository;
	private LocationCatalogService locationCatalogService;

//	@Rule
	public PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:9.6.8" )
			.withDatabaseName( "postgres" )
			.withUsername( "neocom" )
			.withPassword( "01.Alpha" );

//	@Before
	public void prepareCredential() {
		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.integrationCredentialStore = new IntegrationCredentialStore.Builder()
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
	}

//	@Before
	public void prepareEnvironment() throws SQLException, IOException {
		this.connectionUrl = "jdbc:postgresql://"
				+ postgres.getContainerIpAddress()
				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
				+ "/" + "postgres" +
				"?user=" + "neocom" +
				"&password=" + "01.Alpha";
		NeoComLogger.info( "Postgres SQL URL: {}", connectionUrl );
		this.connectionSource = new JdbcConnectionSource( this.connectionUrl, new PostgresDatabaseType() );
		this.onCreate();
		this.miningDao = DaoManager.createDao( connectionSource, MiningExtraction.class );

		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		final GetUniverseTypesTypeIdOk item = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		Mockito.when( storeCacheManager.accessItem( Mockito.anyInt() ) ).thenReturn( Single.just( item ) );
		Mockito.when( storeCacheManager.accessGroup( Mockito.anyInt() ) ).thenReturn( Single.just( group ) );
		Mockito.when( storeCacheManager.accessCategory( Mockito.anyInt() ) ).thenReturn( Single.just( category ) );
		this.esiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( this.retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();
		this.locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withESIUniverseDataProvider( this.esiUniverseDataProvider )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		this.miningRepository = new MiningRepository.Builder()
				.withMiningExtractionDao( this.miningDao )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
	}

//	@Test
	public void buildComplete() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( miningDao )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( repository );
	}

//	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( null )
				.withLocationCatalogService( locationService )
				.build();
	}

//	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( miningDao )
				.build();
	}

//	@Test
	public void accessTodayMiningExtractions4Pilot() throws SQLException, IOException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
		final List<MiningExtraction> miningRecords = this.miningRepository
				.accessTodayMiningExtractions4Pilot( credential );

		Assertions.assertEquals( 2, miningRecords.size() );
	}

//	@Test
	public void accessResources4Date() throws SQLException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
		final List<MiningExtraction> miningRecords = this.miningRepository
				.accessResources4Date( credential, LocalDate.now() );

		Assertions.assertEquals( 2, miningRecords.size() );
	}

//	@Test
	public void accessMiningExtractions4Pilot() throws SQLException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
		final List<MiningExtraction> miningRecords = this.miningRepository
				.accessMiningExtractions4Pilot( credential );

		Assertions.assertEquals( 2, miningRecords.size() );
	}

//	@Test
	public void accessMiningExtractionFindById() throws SQLException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
		MiningExtraction miningRecord = this.miningRepository
				.accessMiningExtractionFindById( this.expectedVerifiedId );

		Assertions.assertNotNull( miningRecord );
		Assertions.assertEquals( characterId.longValue(), miningRecord.getOwnerId() );

		miningRecord = this.miningRepository
				.accessMiningExtractionFindById( "2019-11-10:24-30002764-35-2113197400" );

		Assertions.assertNotNull( miningRecord );
	}

//	@Test(expected = SQLException.class)
	public void accessMiningExtractionFindByIdException() throws SQLException {
		final Dao dao = Mockito.mock( Dao.class );
		Mockito.when( dao.queryForId( ArgumentMatchers.any( String.class ) ) ).thenThrow( SQLException.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		final MiningExtraction miningRecord = repository.accessMiningExtractionFindById( "TEST-LOCATOR" );
	}

	private void onCreate() throws SQLException {
		TableUtils.dropTable( connectionSource, MiningExtraction.class, true );
		TableUtils.createTableIfNotExists( connectionSource, MiningExtraction.class );
	}

	private void insertTodayMiningExtractions() throws SQLException {
		final GetUniverseSystemsSystemIdOk solarSystemData = this.esiUniverseDataProvider
				.getUniverseSystemById( 30002764 );
		final GetUniverseConstellationsConstellationIdOk constellationData = this.esiUniverseDataProvider
				.getUniverseConstellationById( 20000405 );
		final GetUniverseRegionsRegionIdOk regionData = this.esiUniverseDataProvider
				.getUniverseRegionById( 10000033 );
		final MiningExtraction miningExtractionA = new MiningExtraction.Builder()
				.withTypeId( 35 )
				.withSolarSystemLocation( new SpaceSystemImplementation.Builder()
						.withSolarSystem( solarSystemData )
						.withConstellation( constellationData )
						.withRegion( regionData )
						.build() )
				.withQuantity( 43215 )
				.withOwnerId( characterId )
				.withExtractionDate( new LocalDate() )
				.build();
		final MiningExtraction miningExtractionB = new MiningExtraction.Builder()
				.withTypeId( 34 )
				.withSolarSystemLocation( new SpaceSystemImplementation.Builder()
						.withSolarSystem( solarSystemData )
						.withConstellation( constellationData )
						.withRegion( regionData )
						.build() )
				.withQuantity( 12345 )
				.withOwnerId( characterId )
				.withExtractionDate( new LocalDate() )
				.build();
		this.miningRepository.persist( miningExtractionA );
		NeoComLogger.info( "Extraction id: {}", miningExtractionA.getId() );
		this.expectedVerifiedId = miningExtractionA.getId();
		this.miningRepository.persist( miningExtractionB );
		NeoComLogger.info( "Extraction id: {}", miningExtractionB.getId() );
		final List<MiningExtraction> result = this.miningDao.queryForAll();
		Assert.assertEquals( 2, result.size() );
	}
//
//
//	@Test
//	public void persist() throws SQLException {
//		final MiningRepository repository = new MiningRepository.Builder()
//				.withMiningExtractionDao( dao )
//				.build();
//		Mockito.doAnswer( ( call ) -> {
//			final MiningExtraction parameter = call.getArgument( 0 );
//			Assert.assertNotNull( parameter );
//			return null;
//		} ).when( dao ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
////		repository.persist( miningExtraction );
//		Mockito.verify( dao, Mockito.times( 1 ) ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
//	}
}
