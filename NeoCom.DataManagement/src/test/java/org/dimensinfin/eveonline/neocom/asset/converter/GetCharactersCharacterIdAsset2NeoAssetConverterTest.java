package org.dimensinfin.eveonline.neocom.asset.converter;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

class GetCharactersCharacterIdAsset2NeoAssetConverterTest {

	// -  C O M P O N E N T S
	private ESIUniverseDataProvider esiUniverseDataProvider;

	@BeforeEach
	void setUp() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( esiItem.getName() ).thenReturn( "-TEST-NAME-" );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 18 );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 4 );
		Mockito.when( category.getName() ).thenReturn( "Material" );
		this.esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
	}

	@Test
	void convert() throws IOException {
		final int TEST_ITEM_ID = 588;
		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
		esiAsset.setItemId( 123456L );
		esiAsset.setTypeId( TEST_ITEM_ID );
		esiAsset.setLocationId( 54321L );
		final NeoAsset obtained = new GetCharactersCharacterIdAsset2NeoAssetConverter().convert( esiAsset );
		final NeoAsset expected = new NeoAsset()
				.setAssetId( 123456L )
				.setTypeId( TEST_ITEM_ID )
				.setAssetDelegate( esiAsset )
				.setItemDelegate( new NeoItem( TEST_ITEM_ID ) );

		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( expected.getAssetId(), obtained.getAssetId() );
		Assertions.assertEquals( expected.getName(), obtained.getName() );
		Assertions.assertEquals( expected.getCategoryId(), obtained.getCategoryId() );
		Assertions.assertEquals( expected.getCategoryName(), obtained.getCategoryName() );
		Assertions.assertEquals( expected.getGroupId(), obtained.getGroupId() );
		Assertions.assertEquals( expected.getGroupName(), obtained.getGroupName() );
	}

	@Test
	void convertContainer() throws IOException {
		final int TEST_ITEM_ID = 60;
		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
		esiAsset.setItemId( 123456L );
		esiAsset.setTypeId( TEST_ITEM_ID );
		esiAsset.setLocationId( 54321L );
		final NeoAsset obtained = new GetCharactersCharacterIdAsset2NeoAssetConverter().convert( esiAsset );
		final NeoAsset expected = new NeoAsset()
				.setAssetId( 123456L )
				.setTypeId( TEST_ITEM_ID )
				.setAssetDelegate( esiAsset )
				.setItemDelegate( new NeoItem( TEST_ITEM_ID ) );

		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( expected.getAssetId(), obtained.getAssetId() );
		Assertions.assertEquals( expected.getName(), obtained.getName() );
		Assertions.assertEquals( expected.getCategoryId(), obtained.getCategoryId() );
		Assertions.assertEquals( expected.getCategoryName(), obtained.getCategoryName() );
		Assertions.assertEquals( expected.getGroupId(), obtained.getGroupId() );
		Assertions.assertEquals( expected.getGroupName(), obtained.getGroupName() );
	}
}
