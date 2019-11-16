package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SupportFileSystem;

public class ESIUniverseDataProviderTest {
	private static final Integer stationId = 60000535;
	private static final Integer systemId = 30000180;
	private static final Integer constellationId = 20000026;
	private static final Integer regionId = 10000002;
	private static final Integer itemId = 34;
	private static final Integer corporationId = 98384726;
	private static final Integer allianceId = 117383987;

	private ESIUniverseDataProvider provider4Test;
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;

	@BeforeEach
	void setUp() throws IOException {
		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
		this.fileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();
	}

	@Test
	void buildPreInstance() {
		final ESIUniverseDataProvider provider = new ESIUniverseDataProvider.Builder( this.provider4Test ).build();
		Assertions.assertNotNull( provider );
	}

	@Test
	public void getUniverseStationById() {
		final Optional<GetUniverseStationsStationIdOk> stationPromise = this.provider4Test.getUniverseStationByIdOp( stationId );

		Assertions.assertNotNull( stationPromise );
		Assertions.assertTrue( stationPromise.isPresent() );
		final GetUniverseStationsStationIdOk station = stationPromise.get();
		Assertions.assertNotNull( station );
		Assertions.assertEquals( stationId, station.getStationId() );
		Assertions.assertEquals( systemId, station.getSystemId() );
	}

	@Test
	void getUniverseStationByIdFailure() throws IOException {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		Mockito.when( retrofitFactory.accessUniverseConnector() ).thenThrow( new NeoComRuntimeException() );
		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			this.provider4Test.getUniverseStationByIdOp( stationId );
		} );
	}

	@Test
	void getUniverseSystemById() {
		final GetUniverseSystemsSystemIdOk system = this.provider4Test.getUniverseSystemById( systemId );

		Assertions.assertNotNull( system );
		Assertions.assertEquals( systemId, system.getSystemId() );
		Assertions.assertEquals( constellationId, system.getConstellationId() );
	}

	@Test
	void getUniverseConstellationById() {
		final GetUniverseConstellationsConstellationIdOk constellation = this.provider4Test
				.getUniverseConstellationById( constellationId );

		Assertions.assertNotNull( constellation );
		Assertions.assertEquals( constellationId, constellation.getConstellationId() );
		Assertions.assertEquals( regionId, constellation.getRegionId() );
	}

	@Test
	void getUniverseRegionById() {
		final GetUniverseRegionsRegionIdOk region = this.provider4Test.getUniverseRegionById( regionId );

		Assertions.assertNotNull( region );
		Assertions.assertEquals( regionId, region.getRegionId() );
	}

	@Test
	void searchSDEMarketPrice() {
		final double itemPrice = this.provider4Test.searchSDEMarketPrice( itemId );

		Assertions.assertTrue( itemPrice > 0.0 );
		Assertions.assertTrue( itemPrice < 10.0 );
	}

	@Test
	void searchEsiItem4Id() {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();

		final GetUniverseTypesTypeIdOk item = this.provider4Test.searchEsiItem4Id( itemId );

		Assertions.assertNotNull( item );
		Assertions.assertEquals( itemId, item.getTypeId() );
		Assertions.assertEquals( "Tritanium", item.getName() );
	}

	@Test
	void searchItemGroup4Id() {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();

		final GetUniverseGroupsGroupIdOk group = this.provider4Test.searchItemGroup4Id( 18 );

		Assertions.assertNotNull( group );
		Assertions.assertEquals( 18, group.getGroupId() );
		Assertions.assertEquals( "Mineral", group.getName() );
	}

	@Test
	void searchItemCategory4Id() {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();

		final GetUniverseCategoriesCategoryIdOk category = this.provider4Test.searchItemCategory4Id( 4 );

		Assertions.assertNotNull( category );
		Assertions.assertEquals( 4, category.getCategoryId() );
		Assertions.assertEquals( "Material", category.getName() );
	}

	@Test
	void searchSolarSystem4Id() {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.build();
		final StoreCacheManager storeCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.build();
		this.provider4Test = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystemAdapter )
				.withRetrofitFactory( retrofitFactory )
				.withStoreCacheManager( storeCacheManager )
				.build();

		final GetUniverseSystemsSystemIdOk solarSystem = this.provider4Test.searchSolarSystem4Id( systemId );
		Assertions.assertNotNull( solarSystem );
		Assertions.assertEquals( systemId, solarSystem.getSystemId() );
		Assertions.assertEquals( constellationId, solarSystem.getConstellationId() );
	}

	@Test
	void getCorporationsCorporationId() {
		final GetCorporationsCorporationIdOk corporation = this.provider4Test.getCorporationsCorporationId( corporationId );

		Assertions.assertNotNull( corporation );
		Assertions.assertEquals( "Industrias Machaque", corporation.getName() );
	}

	@Test
	void getAlliancesAllianceId() {
		final GetAlliancesAllianceIdOk alliance = this.provider4Test.getAlliancesAllianceId( allianceId );

		Assertions.assertNotNull( alliance );
		Assertions.assertEquals( "Silent Infinity", alliance.getName() );
	}
}
