package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.provider.AssetProvider;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.support.SupportConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SupportFileSystem;

public class AssetProviderIT {
	private GetUniverseRegionsRegionIdOk regionData;
	private GetUniverseConstellationsConstellationIdOk constellationData;
	private GetUniverseSystemsSystemIdOk systemData;
	private GetUniverseStationsStationIdOk stationData;
	private GetUniverseTypesTypeIdOk defaultItem;
	private GetUniverseGroupsGroupIdOk defaultGroup;
	private GetUniverseCategoriesCategoryIdOk defaultCategory;

	private NeoAsset assetHangarItem;
	private NeoAsset assetHangarContainer;
	private NeoAsset assetContainerItem;

	private Credential credential;

	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private StoreCacheManager itStoreCacheManager;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private ESIDataProvider itEsiAuthorizedDataProvider;
	private LocationCatalogService itLocationService;
	private RetrofitFactory itRetrofitFactory;

	public void setUpEsiData() {
		this.regionData = new GetUniverseRegionsRegionIdOk();
		this.regionData.setRegionId( 10000001 );
		this.regionData.setName( "Derelik" );

		this.constellationData = new GetUniverseConstellationsConstellationIdOk();
		this.constellationData.setConstellationId( 20000002 );
		this.constellationData.setName( "Anares" );
		this.constellationData.setRegionId( 10000001 );

		this.systemData = new GetUniverseSystemsSystemIdOk();
		this.systemData.setSystemId( 30000013 );
		this.systemData.setName( "Onsooh" );
		this.systemData.setSecurityClass( "B2" );
		this.systemData.setSecurityStatus( 0.4428166449069977F );
		this.systemData.setStarId( 40000769 );

		this.stationData = new GetUniverseStationsStationIdOk();
		this.stationData.setStationId( 60001084 );
		this.stationData.setName( "Ardene VIII - Moon 5 - CBD Corporation Storage" );
		this.stationData.setOfficeRentalCost( 10000F );
		this.stationData.setOwner( 1000002 );
		this.stationData.setRaceId( 1 );
		this.stationData.setReprocessingEfficiency( 0.5F );
		this.stationData.setReprocessingStationsTake( 0.05F );
		this.stationData.setSystemId( 30000013 );
		this.stationData.setTypeId( 1531 );

		this.defaultItem = new GetUniverseTypesTypeIdOk();
		this.defaultItem.setTypeId( 34 );
		this.defaultItem.setCapacity( 0F );
		this.defaultItem.setGroupId( 18 );
		this.defaultItem.setName( "Tritanium" );
		this.defaultItem.setVolume( 0.01F );

		this.defaultGroup = new GetUniverseGroupsGroupIdOk();
		this.defaultGroup.setGroupId( 18 );
		this.defaultGroup.setCategoryId( 4 );
		this.defaultGroup.setName( "Mineral" );

		this.defaultCategory = new GetUniverseCategoriesCategoryIdOk();
		this.defaultCategory.setCategoryId( 4 );
		this.defaultCategory.setName( "Material" );
	}

	public void setUpIntegrationEnvironment() throws IOException {
		this.itConfigurationProvider = new SupportConfigurationProvider.Builder().build();
		this.itFileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest" )
				.build();
		this.itRetrofitUniverseConnector = new RetrofitUniverseConnector.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itRetrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
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
//		this.itEsiAuthorizedDataProvider = new ESIDataProvider.Builder()
//				.withConfigurationProvider( this.itConfigurationProvider )
//				.withFileSystemAdapter( this.itFileSystemAdapter )
//				.withStoreCacheManager( this.itStoreCacheManager )
//				.withLocationCatalogService( this.itLocationService )
//				.build();
		final GetUniverseStructuresStructureIdOk structureData = new GetUniverseStructuresStructureIdOk();
		structureData.setName( "Anjedin - minealotperhaps - refinery" );
		structureData.setOwnerId( 98035995 );
		structureData.setSolarSystemId( 30001647 );
		structureData.setTypeId( 35835 );
		this.itEsiAuthorizedDataProvider = Mockito.mock( ESIDataProvider.class );
		Mockito.when( this.itEsiAuthorizedDataProvider.searchStructureById( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( structureData );
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationRepository( locationRepository )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
	}

	public void setUpNeoComAssetData() {
		final GetCharactersCharacterIdAssets200Ok esiAssetHangarItem = new GetCharactersCharacterIdAssets200Ok();
		esiAssetHangarItem.setItemId( 1027813315180L );
		esiAssetHangarItem.setTypeId( 17470 );
		esiAssetHangarItem.setLocationId( 60001084L );
		esiAssetHangarItem.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION );
		esiAssetHangarItem.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR );
		esiAssetHangarItem.setQuantity( 28145 );
		esiAssetHangarItem.isSingleton( false );
//		this.assetHangarItem = new NeoAsset.Builder().fromEsiAsset( esiAssetHangarItem ).build();

		final GetCharactersCharacterIdAssets200Ok esiAssetHangarContainer = new GetCharactersCharacterIdAssets200Ok();
		esiAssetHangarContainer.setItemId( 1020057863986L );
		esiAssetHangarContainer.setTypeId( 11489 );
		esiAssetHangarContainer.setLocationId( 60001084L );
		esiAssetHangarContainer.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION );
		esiAssetHangarContainer.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR );
		esiAssetHangarContainer.setQuantity( 1 );
		esiAssetHangarItem.isSingleton( true );
//		this.assetHangarContainer = new NeoAsset.Builder().fromEsiAsset( esiAssetHangarContainer ).build();

		final GetCharactersCharacterIdAssets200Ok esiAssetContainerItem = new GetCharactersCharacterIdAssets200Ok();
		esiAssetContainerItem.setItemId( 1019577379251L );
		esiAssetContainerItem.setTypeId( 12547 );
		esiAssetContainerItem.setLocationId( 1028333032455L );
		esiAssetContainerItem.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER );
		esiAssetContainerItem.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.AUTOFIT );
		esiAssetContainerItem.setQuantity( 1 );
		esiAssetHangarItem.isSingleton( false );
//		this.assetContainerItem = new NeoAsset.Builder().fromEsiAsset( esiAssetContainerItem ).build();
		// Add additional information for the location of this asset.
		if (esiAssetContainerItem.getLocationId() > 61E6) {
			// We are expected to search for the parent and know if it is structure.
			final GetUniverseStructuresStructureIdOk structure = this.itEsiAuthorizedDataProvider
					.searchStructureById( 1028333032455L, this.credential );
			final NeoAsset parent = this.assetHangarContainer;
//			this.assetContainerItem = new NeoAsset.Builder()
//					.fromEsiAsset( esiAssetContainerItem )
//					.withPublicStructure( 1028333032455L )
//					.build();
		}
	}

	@Before
	public void setUp() throws IOException {
		this.setUpEsiData();
		this.setUpIntegrationEnvironment();
		this.credential = Mockito.mock( Credential.class );
		Mockito.when( this.credential.getAccountId() ).thenReturn( 92220000 );
	}

	@Test
	public void classifyAssetsByLocationContainerCase() {
		this.setUpNeoComAssetData();
		final ArrayList<NeoAsset> testAssetList = new ArrayList<>();
		testAssetList.add( this.assetContainerItem );
		testAssetList.add( this.assetHangarItem );
		testAssetList.add( this.assetHangarContainer );
		final Optional<NeoAsset> parentAsset = Optional.ofNullable( this.assetHangarContainer );
		final AssetRepository localAssetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( localAssetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( testAssetList );
		Mockito.when( localAssetRepository.findAssetById( Mockito.anyLong() ) ).thenReturn( parentAsset );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( this.credential )
				.withAssetRepository( localAssetRepository )
				.withLocationCatalogService( this.itLocationService )
				.build();

		provider.classifyAssetsByLocation();

		Assert.assertNotNull( provider );
		Assert.assertNotNull( provider.getRegionList() );
	}
}
