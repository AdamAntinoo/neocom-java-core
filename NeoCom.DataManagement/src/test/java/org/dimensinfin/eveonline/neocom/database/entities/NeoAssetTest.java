package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

public class NeoAssetTest {
	private GetUniverseTypesTypeIdOk esiItem;
	private GetUniverseGroupsGroupIdOk esiGroup;
	private GetUniverseCategoriesCategoryIdOk esiCategory;
	private ESIDataProvider esiDataProvider;

	@Before
	public void setUp() throws Exception {
		this.esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( this.esiItem.getGroupId() ).thenReturn( 321 );
		this.esiGroup = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( this.esiGroup.getCategoryId() ).thenReturn( 654 );
		this.esiCategory = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		this.esiDataProvider = Mockito.mock( ESIDataProvider.class );
		Mockito.when( this.esiDataProvider.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( this.esiItem );
		Mockito.when( this.esiDataProvider.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( this.esiGroup );
		Mockito.when( this.esiDataProvider.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( this.esiCategory );
//		NeoItem.injectEsiUniverseDataAdapter( this.esiDataProvider );
	}

	/**
	 * The test required the instantiation of a new EsiItem and that cannot be mocked. I need a mock ESIDataProvider to generate
	 * the requested esi item from the esi server.
	 * Also EsiItems should be connected to the ESIDataProvider on initialization.
	 */
	@Test
	public void buildFromItem() {
//		final GetCharactersCharacterIdAssets200Ok esiAsset = Mockito.mock( GetCharactersCharacterIdAssets200Ok.class );
//		Mockito.when( esiAsset.getTypeId() ).thenReturn( 34 );
//		final NeoAsset asset = new NeoAsset.Builder()
//				.fromEsiAsset( esiAsset );
//		Assert.assertNotNull( asset );
	}

	//		@Test
	public void gettersContract() {
//		NeoItem.injectEsiUniverseDataAdapter( this.esiDataProvider );
//		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
//		esiAsset.setItemId( 1234567L );
//		esiAsset.setTypeId( 34 );
//		esiAsset.setQuantity( 10 );
//		final NeoAsset asset = new NeoAsset.Builder().fromEsiAsset( esiAsset );
//		Assert.assertNotNull( asset );
//		Assert.assertEquals( 34, asset.getTypeId() );
//		Assert.assertEquals( 4, asset.getCategoryId() );
//		Assert.assertEquals( "Material", asset.getCategoryName() );
//		Assert.assertEquals( 18, asset.getGroupId() );
//		Assert.assertEquals( "Mineral", asset.getGroupName() );
//		Assert.assertEquals( 10, asset.getQuantity().longValue() );
//		Assert.assertEquals( "Tritanium", asset.getName() );
//		Assert.assertEquals( "Tech I", asset.getTech() );
//		Assert.assertEquals( 0.01, asset.getVolume(), 0.01 );
//		Assert.assertEquals( false, asset.isBlueprint() );
//		Assert.assertEquals( "https://image.eveonline.com/Type/34_64.png", asset.getURLForItem() );
	}
}