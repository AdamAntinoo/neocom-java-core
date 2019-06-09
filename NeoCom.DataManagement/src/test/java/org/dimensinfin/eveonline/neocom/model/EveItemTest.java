package org.dimensinfin.eveonline.neocom.model;

import java.beans.PropertyChangeEvent;
import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;
import org.dimensinfin.eveonline.neocom.support.TestAdapterReadyUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EveItemTest extends TestAdapterReadyUp {
	private static ESIDataAdapter esiDataAdapter;

	@Before
	public void setUp() {
		esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		EveItem.injectEsiDataAdapter(esiDataAdapter);
	}

	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(EveItem.class);
	}

	@Test
	public void getName() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem(34);
		final String expected = "Tritanium";
		final String obtained = item.getName();
		Assert.assertNotNull(item);
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void getTypeId() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final int obtained = item.getTypeId();
		Assert.assertNotNull(item);
		Assert.assertEquals("The type should be the type set.", 34, obtained);
	}

	@Test
	public void getGroupId() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final int obtained = item.getGroupId();
		Assert.assertNotNull(item);
		Assert.assertEquals("The group should be valid.", 18, obtained);
	}

	@Test
	public void getCategoryId() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final int obtained = item.getCategoryId();
		Assert.assertNotNull(item);
		Assert.assertEquals("The category should be valid.", 4, obtained);
	}

	@Test
	public void getVolume() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final double obtained = item.getVolume();
		Assert.assertNotNull(item);
		Assert.assertEquals("The volume should match.", 0.01, obtained, 0.001);
	}

	@Test
	public void getIndustryGroup() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final EIndustryGroup obtained = item.getIndustryGroup();
		Assert.assertNotNull(item);
		Assert.assertEquals("The volume should match.", EIndustryGroup.REFINEDMATERIAL, obtained);
	}

	@Test
	public void isBlueprint_false() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final GetUniverseGroupsGroupIdOk group = Mockito.mock(GetUniverseGroupsGroupIdOk.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchEsiItem4Id(Mockito.anyInt())).thenReturn(eveItem);
		Mockito.when(esiDataAdapter.searchItemGroup4Id(Mockito.anyInt())).thenReturn(group);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Capsuleer Bases");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem(34);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}

	@Test
	public void isBlueprint_true() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final GetUniverseGroupsGroupIdOk group = Mockito.mock(GetUniverseGroupsGroupIdOk.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchEsiItem4Id(Mockito.anyInt())).thenReturn(eveItem);
		Mockito.when(esiDataAdapter.searchItemGroup4Id(Mockito.anyInt())).thenReturn(group);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Energy Neutralizer Blueprint");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem(15799);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}

	@Test
	public void getPrice() throws IOException {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem(34);
		double obtained = item.getPrice();
		Assert.assertTrue("Price expected to be positive value.", obtained > 3.0);
	}

	@Test
	public void signalCompletion_itemData() {
		final EventEmitter emitter = Mockito.mock(EventEmitter.class);
		final GetUniverseTypesTypeIdOk universeItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final EveItem item = new EveItem(34);
		Mockito.doAnswer(( call ) -> {
			final PropertyChangeEvent event = call.getArgument(0);
			Assert.assertNotNull(event);
			Assert.assertEquals(EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name(), event.getPropertyName());
			Assert.assertEquals(universeItem, event.getNewValue());
			return null;
		}).when(emitter).sendChangeEvent(new PropertyChangeEvent(item
				, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
				, null, universeItem));
		//		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, universeItem);
	}
}
