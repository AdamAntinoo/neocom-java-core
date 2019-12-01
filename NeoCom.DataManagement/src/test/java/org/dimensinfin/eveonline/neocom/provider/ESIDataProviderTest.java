package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class ESIDataProviderTest {
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final Logger logger = LoggerFactory.getLogger( ESIDataProviderTest.class );
	private static final GenericContainer<?> esisimulator;

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

	private SBConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private RetrofitFactory retrofitFactory;
	private ESIDataProvider esiDataProvider;

	@Test
	public void buildComplete() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		final ESIDataProvider provider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.withStoreCacheManager( storeCacheManager )
				.withRetrofitFactory( retrofitFactory )
				.build();

		Assertions.assertNotNull( provider );
	}

	@Test
	public void builderFailure() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final ESIDataProvider provider = new ESIDataProvider.Builder()
					.withFileSystemAdapter( this.fileSystemAdapter )
					.withLocationCatalogService( locationCatalogService )
					.build();
		} );
	}

	@Test
	public void downloadPilotFamilyData() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		final ESIDataProvider provider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.withStoreCacheManager( storeCacheManager )
				.withRetrofitFactory( retrofitFactory )
				.build();

		Assertions.assertNotNull( provider.searchSDERace( 1 ) );
		Assertions.assertNotNull( provider.searchSDEAncestry( 1 ) );
		Assertions.assertNotNull( provider.searchSDEBloodline( 8 ) );
	}

	@Test
	public void getCharactersCharacterId() {
		final GetCharactersCharacterIdOk character = this.esiDataProvider.getCharactersCharacterId( 93813310 );
		Assertions.assertNotNull( character );
		Assertions.assertEquals( "Perico Tuerto", character.getName() );
	}

	@Test
	public void getCharactersCharacterIdAssets() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		final List<GetCharactersCharacterIdAssets200Ok> assets = this.esiDataProvider.getCharactersCharacterIdAssets( credential );
		Assertions.assertNotNull( assets );
		Assertions.assertEquals( 26, assets.size() );
	}

	@Test
	public void getCharactersCharacterIdBlueprints() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		final List<GetCharactersCharacterIdBlueprints200Ok> blueprints = this.esiDataProvider.getCharactersCharacterIdBlueprints( credential );
		Assertions.assertNotNull( blueprints );
		Assertions.assertEquals( 5, blueprints.size() );
	}

	@Test
	public void getCharactersCharacterIdMining() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		final List<GetCharactersCharacterIdMining200Ok> extractions = this.esiDataProvider.getCharactersCharacterIdMining( credential );
		Assertions.assertNotNull( extractions );
		Assertions.assertEquals( 6, extractions.size() );
	}

	@Test
	public void getUniverseStatus() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		final ESIDataProvider provider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.withStoreCacheManager( storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();

		final GetStatusOk status = provider.getUniverseStatus( "Tranquility" );
		Assertions.assertNotNull( status );
		Assertions.assertTrue( Math.abs( 28184 - status.getPlayers() ) < 10000 );
	}

	@Test
	public void searchStructureById() {

	}

	@BeforeEach
	void setUp() throws IOException {
		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
		this.configurationProvider.setProperty( "P.authenticated.retrofit.server.location",
				"http://" +
						esisimulator.getContainerIpAddress() +
						":" + esisimulator.getMappedPort( ESI_UNITTESTING_PORT ) +
						"/latest/" );
		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest/" )
				.build();
		this.retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		this.esiDataProvider = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( locationCatalogService )
				.withStoreCacheManager( storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
	}
//	@Test
//	public void searchItemGroup4Id() {
//		final GetUniverseGroupsGroupIdOk group = this.esiDataProvider.searchItemGroup4Id( 10 );
//		Assert.assertEquals( "the group name should match.", "Stargate", group.getName() );
//	}
//
//	@Test
//	public void searchItemCategory4Id() {
//		final GetUniverseCategoriesCategoryIdOk category = this.esiDataProvider.searchItemCategory4Id( 20 );
//		Assert.assertEquals( "the category name should match.", "Implant", category.getName() );
//	}

//	@BeforeEach
//	public void setUp() throws Exception {
//		this.configurationProvider = new SBConfigurationProvider.Builder()
//				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
//		this.fileSystemAdapter = new SupportFileSystem.Builder()
//				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
//				.build();
//		this.retrofitFactory = new RetrofitFactory.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.build();
//		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
//		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
//		this.esiDataProvider = new ESIDataProvider.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.withLocationCatalogService( locationCatalogService )
//				.withStoreCacheManager( storeCacheManager )
//				.withRetrofitFactory( retrofitFactory )
//				.build();
//	}
//	@Test
//	public void searchSDEMarketPriceSuccess() {
//		final Double price = this.esiDataProvider.searchSDEMarketPrice( 34 );
//		Assert.assertNotNull( price );
//		Assert.assertEquals( 885055.23, price, 0.01 );
//	}
//
//	@Test
//	public void searchSDEMarketPriceNotFound() {
//		final Double price = this.esiDataProvider.searchSDEMarketPrice( 80 );
//		Assert.assertEquals( -1.0D, price, 0.01 );
//	}

//	@Test
//	public void searchLocation4IdSuccess() {
//		EsiLocation location = this.esiDataProvider.searchLocation4Id( 100 );
//		Assert.assertNotNull( location );
//		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
//		location = this.esiDataProvider.searchLocation4Id( 1000L );
//		Assert.assertNotNull( location );
//		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
//	}

//	@Test
//	public void getCorporationsCorporationId() {
//		final GetCorporationsCorporationIdOk corporation = this.esiDataProvider.getCorporationsCorporationId( 98384726 );
//		Assert.assertNotNull( corporation );
//		Assert.assertEquals( "Industrias Machaque", corporation.getName() );
//	}
}
