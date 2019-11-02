package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.support.SupportConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SupportFileSystem;
import org.dimensinfin.eveonline.neocom.support.SupportStoreCacheManager;

public class AssetProviderIT {
	private GetUniverseRegionsRegionIdOk regionData;
	private GetUniverseConstellationsConstellationIdOk constellationData;
	private GetUniverseSystemsSystemIdOk systemData;
	private GetUniverseStationsStationIdOk stationData;

	private NeoAsset assetHangarItem;
	private NeoAsset assetHangarContainer;
	private NeoAsset assetContainerItem;

	private Credential credential;

	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private StoreCacheManager itStoreCacheManager;

//	private LocationIdentifier locationIdentifier;
//	private List<NeoAsset> assetList;
//	private AssetRepository assetRepository;
//	private LocationCatalogService locationService;
//	private AssetsProvider provider4Test;


	private AssetProviderIT() {}

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
		this.assetHangarItem = new NeoAsset.Builder().fromEsiAsset( esiAssetHangarItem );

		final GetCharactersCharacterIdAssets200Ok esiAssetHangarContainer = new GetCharactersCharacterIdAssets200Ok();
		esiAssetHangarContainer.setItemId( 1020057863986L );
		esiAssetHangarContainer.setTypeId( 11489 );
		esiAssetHangarContainer.setLocationId( 60001084L );
		esiAssetHangarContainer.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION );
		esiAssetHangarContainer.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR );
		esiAssetHangarContainer.setQuantity( 1 );
		esiAssetHangarItem.isSingleton( true );
		this.assetHangarContainer = new NeoAsset.Builder().fromEsiAsset( esiAssetHangarContainer );

		final GetCharactersCharacterIdAssets200Ok esiAssetContainerItem = new GetCharactersCharacterIdAssets200Ok();
		esiAssetContainerItem.setItemId( 1019577379251L );
		esiAssetContainerItem.setTypeId( 12547 );
		esiAssetContainerItem.setLocationId( 1020057863986L );
		esiAssetContainerItem.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER );
		esiAssetContainerItem.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.AUTOFIT );
		esiAssetContainerItem.setQuantity( 1 );
		esiAssetHangarItem.isSingleton( false );
		this.assetContainerItem = new NeoAsset.Builder().fromEsiAsset( esiAssetContainerItem );

	}

	public void setUpIntegrationEnvironment() throws IOException {
		this.itConfigurationProvider = new SupportConfigurationProvider.Builder().build();
		this.itFileSystemAdapter = new SupportFileSystem.Builder()
				.optionalApplicationDirectory( "TestCacheDirectory" )
				.build();
		this.itStoreCacheManager = new SupportStoreCacheManager.Builder()
		.withNoAuthRetrofitConnector( retrofitConnector )
		.build();
	}

	@Before
	public void setUp() {
		this.setUpEsiData();
		this.setUpNeoComAssetData();
		this.credential = Mockito.mock( Credential.class );
		Mockito.when( this.credential.getAccountId() ).thenReturn( 92220000 );
//		this.locationIdentifier = Mockito.mock( LocationIdentifier.class );
//		Mockito.when( this.locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
//		Mockito.when( this.locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000 );
//		final NeoAsset asset = Mockito.mock( NeoAsset.class );
//		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
//		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
//		this.assetList = new ArrayList<>();
//		this.assetRepository = Mockito.mock( AssetRepository.class );
//		Mockito.when( this.assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( this.assetList );
//		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );
//		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
//		this.locationService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
//		this.provider4Test = new AssetsProvider.Builder()
//				.withCredential( credential )
//				.withAssetRepository( assetRepository )
//				.withLocationCatalogService( locationService )
//				.build();
//		Assert.assertNotNull( this.provider4Test );
	}

	@Test
	public void classifyAssetsByLocationContainerCase() {
		final ArrayList<NeoAsset> testAssetList = new ArrayList<>();
		testAssetList.add( this.assetHangarItem );
		testAssetList.add( this.assetContainerItem );
		testAssetList.add( this.assetContainerItem );
		final AssetRepository localAssetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( localAssetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( testAssetList );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetsProvider provider = new AssetsProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( localAssetRepository )
				.withLocationCatalogService( locationService )
				.build();

		final ESIUniverseDataProvider esiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withStoreCacheManager( this.it )
		provider.classifyAssetsByLocation();

		Assert.assertNotNull( provider );
		Assert.assertNotNull( provider.getRegionList() );
	}

//	// - B U I L D E R
//	public static class Builder {
//		private AssetProviderIT onConstruction;
//
//		public Builder() {
//			this.onConstruction = new AssetProviderIT();
//		}
//
//		public AssetProviderIT build() {
//			return this.onConstruction;
//		}
//	}
}