package org.dimensinfin.eveonline.neocom.domain;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.support.ESIDataAdapterSupportTest;

import nl.jqno.equalsverifier.EqualsVerifier;

public class NeoItemTest extends ESIDataAdapterSupportTest {
	private ESIDataAdapter realEsiDataAdapter;

//	@Before
	public void setUp()throws IOException {
		super.setUp();
		this.realEsiDataAdapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		NeoItem.injectEsiDataAdapter(realEsiDataAdapter);
	}

	@Test
	public void constructor_GetUniverseTypesTypeIdOk() {
		final GetUniverseTypesTypeIdOk type = new GetUniverseTypesTypeIdOk();
		type.setTypeId( 34 );
		type.setGroupId( 18 );
		type.setName( "Tritanium" );
		type.setVolume( 0.01F );
		type.setCapacity( 10.0F );
		final NeoItem item = new NeoItem( type );
		Assert.assertNotNull( item );
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( NeoItem.class )
				.usingGetClass().verify();
	}

	@Test
	public void getterContract() {
//		final GetUniverseTypesTypeIdOk type = new GetUniverseTypesTypeIdOk();
//		type.setTypeId( 34 );
//		type.setGroupId( 18 );
//		type.setName( "Tritanium" );
//		type.setVolume( 0.01F );
//		type.setCapacity( 10.0F );
//		final GetUniverseTypesTypeIdOk item = this.esiDataAdapter.searchEsiItem4Id( 34 );
		final NeoItem item = new NeoItem( 34 );
		Assert.assertNotNull( item );
		Assert.assertEquals( 34, item.getTypeId());
		Assert.assertEquals( 18, item.getGroupId());
		Assert.assertEquals( 4, item.getCategoryId());
//		Assert.assertEquals( 0.1F, item.getCapacity().floatValue() , 0.1   );
		Assert.assertNotNull( item.getDogmaAttributes() );
		Assert.assertEquals( "not-applies", item.getHullGroup() );
		Assert.assertEquals( NeoItem.IndustryGroup.REFINEDMATERIAL, item.getIndustryGroup());
	}

//	@Test
//	public void accessorContract() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
//		NeoItem.injectEsiDataAdapter(esiDataAdapter);
//		PojoTestUtils.validateAccessors(NeoItem.class);
//	}

	@Test
	public void getName() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem( 34 );
		final String expected = "Tritanium";
		final String obtained = item.getName();
		Assert.assertNotNull( item );
		Assert.assertEquals( expected, obtained );
	}

	@Test
	public void getTypeId() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem()
				.setTypeId( 34 );
		final int obtained = item.getTypeId();
		Assert.assertNotNull( item );
		Assert.assertEquals( "The type should be the type set.", 34, obtained );
	}

	@Test
	public void getGroupId() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
//		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem(34 );
		final int obtained = item.getGroupId();
		Assert.assertNotNull( item );
		Assert.assertEquals( "The group should be valid.", 18, obtained );
	}

	@Test
	public void getCategoryId() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
//		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem( 34 );
		final int obtained = item.getCategoryId();
		Assert.assertNotNull( item );
		Assert.assertEquals( "The category should be valid.", 4, obtained );
	}

	@Test
	public void getVolume() {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
//		NeoItem.injectEsiDataAdapter( this.esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem( 34 );
		final double obtained = item.getVolume();
		Assert.assertNotNull( item );
		Assert.assertEquals( "The volume should match.", 0.01, obtained, 0.001 );
	}

	@Test
	public void getIndustryGroup() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
//		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem( 34 );
		final NeoItem.IndustryGroup obtained = item.getIndustryGroup();
		Assert.assertNotNull( item );
		Assert.assertEquals( "The volume should match.", NeoItem.IndustryGroup.REFINEDMATERIAL, obtained );
	}

	@Test
	public void isBlueprint_false() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock( ESIDataAdapter.class );
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( esiDataAdapter.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( eveItem );
		Mockito.when( esiDataAdapter.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( group );
		Mockito.when( esiDataAdapter.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( category );
		Mockito.when( category.getName() ).thenReturn( "Capsuleer Bases" );
		NeoItem.injectEsiDataAdapter( esiDataAdapter );
		final NeoItem item = new NeoItem( 34 );
		Assert.assertNotNull( item );
		Assert.assertFalse( item.isBlueprint() );
	}

	@Test
	public void isBlueprint_true() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock( ESIDataAdapter.class );
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( esiDataAdapter.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( eveItem );
		Mockito.when( esiDataAdapter.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( group );
		Mockito.when( esiDataAdapter.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( category );
		Mockito.when( category.getName() ).thenReturn( "Energy Neutralizer Blueprint" );
		NeoItem.injectEsiDataAdapter( esiDataAdapter );
		final NeoItem item = new NeoItem( 15799 );
		Assert.assertNotNull( item );
		Assert.assertFalse( item.isBlueprint() );
	}

	@Test
	public void getPrice() throws IOException {
//		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		NeoItem.injectEsiDataAdapter( esiDataAdapter );
//		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final NeoItem item = new NeoItem( 34 );
		double obtained = item.getPrice();
		Assert.assertTrue( "Price expected to be positive value.", obtained > 3.0 );
	}

	//	@Test
	public void signalCompletion_itemData() {
//		final EventEmitter emitter = Mockito.mock(EventEmitter.class);
//		final GetUniverseTypesTypeIdOk universeItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
//		final NeoItem item = new NeoItem(34);
//		Mockito.doAnswer(( call ) -> {
//			final PropertyChangeEvent event = call.getArgument(0);
//			Assert.assertNotNull(event);
//			Assert.assertEquals(EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name(), event.getPropertyName());
//			Assert.assertEquals(universeItem, event.getNewValue());
//			return null;
//		}).when(emitter).sendChangeEvent(new PropertyChangeEvent(item
//				, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
//				, null, universeItem));
//		//		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, universeItem);
	}
}
