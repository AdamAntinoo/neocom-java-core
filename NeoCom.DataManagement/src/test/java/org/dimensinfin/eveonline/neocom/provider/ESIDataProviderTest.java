package org.dimensinfin.eveonline.neocom.provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SupportFileSystem;

public class ESIDataProviderTest {
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private RetrofitFactory retrofitFactory;

	@BeforeEach
	public void setUp() throws Exception {
		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		this.retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
	}

	@Test
	public void buildComplete() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
//		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
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


//	@Test
//	public void downloadPilotFamilyData() {
//		this.esiDataProvider.downloadPilotFamilyData();
//		final GetUniverseRaces200Ok race = this.esiDataProvider.searchSDERace( 1 );
//		Assert.assertEquals( "the race name should match.", "Caldari", race.getName() );
//		Assert.assertNotNull( this.esiDataProvider.searchSDERace( 1 ) );
//		Assert.assertNotNull( this.esiDataProvider.searchSDEAncestry( 1 ) );
//		Assert.assertNotNull( this.esiDataProvider.searchSDEBloodline( 8 ) );
//	}

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

//	//	@Test
//	public void fetchItem_notcached() {
////		final ESIDataProvider adapter = this.setupRealAdapter();
//		final GetUniverseTypesTypeIdOk item = this.esiDataProvider.searchEsiItem4Id( 34 );
//		Assert.assertNotNull( item );
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

//	@Test
//	public void getCharactersCharacterId() {
//		final GetCharactersCharacterIdOk pilot = this.esiDataProvider.getCharactersCharacterId( 93813310 );
//		Assert.assertNotNull( pilot );
//		Assert.assertEquals( "Beth Ripley", pilot.getName() );
//	}

}
