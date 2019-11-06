package org.dimensinfin.eveonline.neocom.provider;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegion;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetProviderTest {
	private Credential credential;
	private LocationIdentifier locationIdentifier;
	private List<NeoAsset> assetList;
	private AssetRepository assetRepository;
	private GetUniverseRegionsRegionIdOk regionData;
	private GetUniverseConstellationsConstellationIdOk constellationData;
	private GetUniverseSystemsSystemIdOk systemData;
	private GetUniverseStationsStationIdOk stationData;
	private LocationCatalogService locationService;
	private AssetProvider provider4Test;

	private NeoAsset assetHangarItem;
	private NeoAsset assetHangarContainer;
	private NeoAsset assetContainerItem;

	//	@Before
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
		esiAssetContainerItem.setLocationId( 1020057863986L );
		esiAssetContainerItem.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER );
		esiAssetContainerItem.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.AUTOFIT );
		esiAssetContainerItem.setQuantity( 1 );
		esiAssetHangarItem.isSingleton( false );
//		this.assetContainerItem = new NeoAsset.Builder().fromEsiAsset( esiAssetContainerItem ).build();

	}

	@Before
	public void setUp() {
		this.setUpEsiData();
		this.setUpNeoComAssetData();
		this.credential = Mockito.mock( Credential.class );
		Mockito.when( this.credential.getAccountId() ).thenReturn( 92220000 );
		this.locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( this.locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( this.locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000L );
//		final NeoAsset asset = Mockito.mock( NeoAsset.class );
//		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
//		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		this.assetList = new ArrayList<>();
		this.assetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( this.assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( this.assetList );
//		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );
//		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		this.locationService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		this.provider4Test = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( this.provider4Test );
	}

	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( null )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( null )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureC() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( null )
				.build();
		Assert.assertNotNull( provider );
	}

	@Test
	public void classifyAssetsByLocationSpace() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000L );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		final List<NeoAsset> assetList = new ArrayList<>();
		assetList.add( asset );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( assetList );
		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
		regionData.setRegionId( 1100000 );
		regionData.setName( "-TEST-REGION-NAME-" );
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );
		Mockito.when( ((SpaceRegion) spaceLocation).getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();

		provider.classifyAssetsByLocation();

		Assert.assertNotNull( provider );
		Assert.assertNotNull( provider.getRegionList() );
		Assert.assertEquals( 1, provider.getRegionList().size() );
		Assert.assertNotNull( provider.getRegionList().get( 0 ) );
		Assert.assertEquals( "-TEST-REGION-NAME-", provider.getRegionList().get( 0 ).getRegionName() );
		Assert.assertEquals( 1, provider.getRegionList().get( 0 ).getContentCount() );
	}
//	@Test
//	public void classifyAssetsByLocationStation() {
//		final Credential credential = Mockito.mock( Credential.class );
//		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
//		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
//		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.STATION );
//		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 60000025 );
//		final NeoAsset asset = Mockito.mock( NeoAsset.class );
//		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
//		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
//		final List<NeoAsset> assetList = new ArrayList<>();
//		assetList.add( asset );
//		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
//		Mockito.when( assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( assetList );
//		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
//		regionData.setRegionId( 1100000 );
//		regionData.setName( "-TEST-REGION-NAME-" );
//		final StationLocation stationLocation = Mockito.mock( StationLocation.class );
//		Mockito.when( stationLocation.getRegionId() ).thenReturn( 1100000 );
//		Mockito.when( stationLocation.getRegion() ).thenReturn( regionData );
//		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( stationLocation );
//		final AssetProvider provider = new AssetProvider.Builder()
//				.withCredential( credential )
//				.withAssetRepository( assetRepository )
//				.withLocationCatalogService( locationService )
//				.build();
//
//		provider.classifyAssetsByLocation();
//
//		Assert.assertNotNull( provider );
//		Assert.assertNotNull( provider.getRegionList() );
//		Assert.assertEquals( 1, provider.getRegionList().size() );
//		Assert.assertNotNull( provider.getRegionList().get( 0 ) );
//		Assert.assertEquals( "-TEST-REGION-NAME-", provider.getRegionList().get( 0 ).getFacet().getName() );
//		Assert.assertEquals( 1, provider.getRegionList().get( 0 ).getContentCount() );
//	}
//	@Test
//	public void classifyAssetsByLocationExaustedList() {
//	}

	@Test
	public void verifyTimeStamp() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000L );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		final List<NeoAsset> assetList = new ArrayList<>();
		assetList.add( asset );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( assetList );
		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
		regionData.setRegionId( 1100000 );
		regionData.setName( "-TEST-REGION-NAME-" );
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );
		Mockito.when( ((SpaceRegion) spaceLocation).getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
//		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();

		provider.classifyAssetsByLocation(); // The first time the timestamp is not set.
		provider.classifyAssetsByLocation(); // The second time I run the rest of the code
	}
}