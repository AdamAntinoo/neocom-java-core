package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.ESIDataProviderSupportTest;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

/**
 * Tests for repository classes will be executed under a integration environment. There should be a real database instance
 * implemented by the TestContainers platform or by an external docker instance prepared for integration.
 *
 * Test should manage the database to clear and insert test data to check all the exported api methods.
 *
 * Repository instances only depend on Dao components. So they should be provided from the integration environment.
 */
public class MiningRepositoryIT extends ESIDataProviderSupportTest {
	private String connectionUrl;
	private JdbcConnectionSource connectionSource;
	private Dao<MiningExtraction, String> miningDao;
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private RetrofitFactory retrofitFactory;
	private ESIUniverseDataProvider esiUniverseDataProvider;
	private MiningRepository miningRepository;

	private static final List<MiningExtraction> miningExtractionList = new ArrayList();
	//	private static MiningExtraction miningExtraction;
	private static Dao<MiningExtraction, String> dao;

	@Rule
	public PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:9.6.8" )
			.withDatabaseName( "postgres" )
			.withUsername( "neocom" )
			.withPassword( "01.Alpha" );

	protected void prepareEnvironment() throws SQLException, IOException {
		this.connectionUrl = "jdbc:postgresql://"
				+ postgres.getContainerIpAddress()
				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
				+ "/" + "postgres" +
				"?user=" + "neocom" +
				"&password=" + "01.Alpha";
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
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
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

	private void onCreate() throws SQLException {
		TableUtils.createTableIfNotExists( connectionSource, MiningExtraction.class );
	}

	@Before
	public void prepare() throws SQLException, IOException {
		this.prepareEnvironment();
	}

	private void insertTodayMiningExtractions() throws SQLException {
		final GetUniverseSystemsSystemIdOk solarSystemData = this.esiUniverseDataProvider
				.getUniverseSystemById( 30002764 );
		final GetUniverseConstellationsConstellationIdOk constellationData = this.esiUniverseDataProvider
				.getUniverseConstellationById( 20000405 );
		final GetUniverseRegionsRegionIdOk regionData = this.esiUniverseDataProvider
				.getUniverseRegionById( 10000033 );
		final MiningExtraction miningExtractionA = new MiningExtraction.Builder()
				.withTypeId( 35)
				.withSolarSystemLocation( new SpaceSystemImplementation.Builder()
						.withSolarSystem( solarSystemData )
						.withConstellation( constellationData )
						.withRegion( regionData )
						.build() )
				.withQuantity( 43215 )
				.withOwnerId( 92223647 )
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
				.withOwnerId( 92223647 )
				.withExtractionDate( new LocalDate() )
				.build();
		this.miningRepository.persist( miningExtractionA );
		this.miningRepository.persist( miningExtractionB);
		final List<MiningExtraction> result = this.miningDao.queryForAll();
		Assert.assertEquals( 2, result.size() );
	}

	@Test
	public void accessTodayMiningExtractions4Pilot() throws SQLException {
		this.insertTodayMiningExtractions();

		// Create and initialize database with test data.
//		postgres.start();
//		connectionUrl = postgres.getj

//		final QueryBuilder builder = Mockito.mock( QueryBuilder.class );
//		final Where where = Mockito.mock( Where.class );
//		final PreparedQuery query = Mockito.mock( PreparedQuery.class );
//		final Credential credential = Mockito.mock( Credential.class );
//		final MiningRepository repository = new MiningRepository.Builder()
//				.withMiningExtractionDao( dao )
//				.build();
//		Mockito.when( dao.queryBuilder() ).thenReturn( builder );
//		Mockito.when( builder.where() ).thenReturn( where );
//		Mockito.when( builder.orderBy( ArgumentMatchers.any( String.class ), ArgumentMatchers.any( Boolean.class ) ) )
//				.thenReturn( builder );
//		Mockito.doAnswer( ( call ) -> {
//			final Object parameter = call.getArgument( 0 );
//			Assert.assertNotNull( parameter );
//			return null;
//		} ).when( where ).eq( ArgumentMatchers.any( String.class ), ArgumentMatchers.any( Integer.class ) );
//		Mockito.when( builder.prepare() ).thenReturn( query );
//		Mockito.when( dao.query( ArgumentMatchers.any( PreparedQuery.class ) ) ).thenReturn( miningExtractionList );
//		Mockito.when( credential.getAccountId() ).thenReturn( 123 );
//		final List<MiningExtraction> obtained = repository.accessTodayMiningExtractions4Pilot( credential );
//		Assert.assertEquals( "The number of records is 1.", 1, obtained.size() );
	}

	//---------------------------------------------------
	//	@Before
	public void setUp() throws IOException {
//		super.setUp();
		final EsiLocation location = Mockito.mock( EsiLocation.class );
		miningExtractionList.clear();
//		miningExtraction = new MiningExtraction.Builder()
//				.withTypeId( 34 )
//				.withSolarSystemLocation( location )
//				.withQuantity( 12345 )
//				.withOwnerId( 92223647 )
//				.withExtractionDate( new LocalDate() )
//				.build();
//		miningExtractionList.add( miningExtraction );
		dao = Mockito.mock( Dao.class );
//		NeoItem.injectEsiUniverseDataAdapter(this.esiDataProvider);
	}

	@Test
	public void buildComplete() {
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.build();
		Assert.assertNotNull( repository );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailure() {
		final MiningRepository repository = new MiningRepository.Builder()
				.build();
	}

	@Test
	public void test2() throws SQLException {
		final List<MiningExtraction> result = miningDao.queryForAll();
		Assert.assertNotNull( "A basic SELECT query succeeds", miningDao );
		Assert.assertNotNull( miningDao );
	}

//	@Test
//	public void initial() throws SQLException {
//		try (PostgreSQLContainer postgres = new PostgreSQLContainer<>()
//				.withUsername( "neocom" )
//				.withPassword( "01.Alpha" )) {
//			postgres.start();
//			final Connection connection = postgres.createConnection( "?user=neocom&password=01.Alpha" );
//			final String neocomDatabaseURL = "jdbc:postgresql://localhost:5432" +
//					"/" + "postgres" +
//					"?user=" + "neocom" +
//					"&password=" + "01.Alpha";
//			String url = "jdbc:postgresql://localhost:32843/test?user=neocom&password=01.Alpha";
//			final JdbcConnectionSource connectionSource = new JdbcConnectionSource( url,
//					new PostgresDatabaseType() );
//			final Dao<MiningExtraction, String> miningDao = DaoManager.createDao( connectionSource, MiningExtraction.class );
//
////			ResultSet resultSet = performQuery( postgres, "SELECT 1" );
////			int resultSetInt = resultSet.getInt( 1 );
//			final List<MiningExtraction> result = miningDao.queryForAll();
//			Assert.assertNotNull( "A basic SELECT query succeeds", miningDao );
//			Assert.assertNotNull( miningDao );
//		}
//	}


	@Test
	public void accessMiningExtractions4Pilot() throws SQLException {
		final QueryBuilder builder = Mockito.mock( QueryBuilder.class );
		final Where where = Mockito.mock( Where.class );
		final PreparedQuery query = Mockito.mock( PreparedQuery.class );
		final Credential credential = Mockito.mock( Credential.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.build();
		Mockito.when( dao.queryBuilder() ).thenReturn( builder );
		Mockito.when( builder.where() ).thenReturn( where );
		Mockito.when( builder.orderBy( ArgumentMatchers.any( String.class ), ArgumentMatchers.any( Boolean.class ) ) )
				.thenReturn( builder );
		Mockito.when( builder.prepare() ).thenReturn( query );
		Mockito.when( dao.query( ArgumentMatchers.any( PreparedQuery.class ) ) ).thenReturn( miningExtractionList );
		final List<MiningExtraction> obtained = repository.accessMiningExtractions4Pilot( credential );
		Assert.assertEquals( "The number of records is 1.", 1, obtained.size() );
	}

//	@Test
//	public void accessMiningExtractionFindById_found() throws SQLException {
//		final MiningRepository repository = new MiningRepository.Builder()
//				.withMiningExtractionDao( dao )
//				.build();
//		Mockito.when( dao.queryForId( ArgumentMatchers.any( String.class ) ) ).thenReturn( miningExtraction );
//		final MiningExtraction obtained = repository.accessMiningExtractionFindById( "TEST-LOCATOR" );
//		Assert.assertEquals( "The extraction is the same.", miningExtraction, obtained );
//	}

	@Test
	public void accessMiningExtractionFindById_notfound() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.build();
		Mockito.when( dao.queryForId( ArgumentMatchers.any( String.class ) ) ).thenReturn( null );
		final MiningExtraction obtained = repository.accessMiningExtractionFindById( "TEST-LOCATOR" );
		Assert.assertNull( "The extraction was not found.", obtained );
	}

	@Test(expected = SQLException.class)
	public void accessMiningExtractionFindById_exception() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.build();
		Mockito.when( dao.queryForId( ArgumentMatchers.any( String.class ) ) ).thenThrow( SQLException.class );
		final MiningExtraction obtained = repository.accessMiningExtractionFindById( "TEST-LOCATOR" );
	}

	@Test
	public void persist() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( dao )
				.build();
		Mockito.doAnswer( ( call ) -> {
			final MiningExtraction parameter = call.getArgument( 0 );
			Assert.assertNotNull( parameter );
			return null;
		} ).when( dao ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
//		repository.persist( miningExtraction );
		Mockito.verify( dao, Mockito.times( 1 ) ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
	}
}
