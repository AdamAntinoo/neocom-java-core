package org.dimensinfin.eveonline.neocom.provider;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapters.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetsProviderTest {
	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetsProvider provider = new AssetsProvider.Builder()
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
		final AssetsProvider provider = new AssetsProvider.Builder()
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
		final AssetsProvider provider = new AssetsProvider.Builder()
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
		final AssetsProvider provider = new AssetsProvider.Builder()
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
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000 );
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
		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		final AssetsProvider provider = new AssetsProvider.Builder()
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
	@Test
	public void classifyAssetsByLocationExaustedList() {
	}

	@Test
	public void verifyTimeStamp() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000 );
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
		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		Mockito.when( locationService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		final AssetsProvider provider = new AssetsProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();

		provider.classifyAssetsByLocation(); // The first time the timestamp is not set.
		provider.classifyAssetsByLocation(); // The second time I run the rest of the code
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
//		final AssetsProvider provider = new AssetsProvider.Builder()
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
}