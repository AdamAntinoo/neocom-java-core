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
import org.dimensinfin.eveonline.neocom.domain.SpaceKLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetsProviderTest {
	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock(LocationCatalogService.class);
		final AssetsProvider provider = new AssetsProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull(provider);
	}

	@Test
	public void classifyAssetsByLocation() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
		regionData.setRegionId( 1100000 );
		regionData.setName( "-TEST-REGION-NAME-" );
		final SpaceKLocation spaceLocation = Mockito.mock(SpaceKLocation.class);
		Mockito.when( spaceLocation.getRegionId() ).thenReturn( 1100000 );
		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock(LocationCatalogService.class);
		Mockito.when( locationService.searchSpaceLocation4Id(Mockito.anyInt())).thenReturn( spaceLocation );
		final AssetsProvider provider = new AssetsProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		final List<NeoAsset> assetList = new ArrayList<>();
		final LocationIdentifier locationIdentifier = Mockito.mock(LocationIdentifier.class);
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000 );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		assetList.add( asset );
		Mockito.when( assetRepository.findAllByOwnerId(Mockito.anyInt()) ).thenReturn( assetList );

		provider.classifyAssetsByLocation();

		Assert.assertNotNull( provider );
		Assert.assertNotNull( provider.getRegionList() );
		Assert.assertEquals( 1,provider.getRegionList().size() );
		Assert.assertNotNull( provider.getRegionList().get(0) );
		Assert.assertEquals( "-TEST-REGION-NAME-",provider.getRegionList().get(0).getFacet().getName() );
		Assert.assertEquals( 1,provider.getRegionList().get(0).getContentCount() );
	}
}