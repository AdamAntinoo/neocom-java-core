package org.dimensinfin.eveonline.neocom.model;

import java.beans.PropertyChangeEvent;
import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.domain.IEsiItemDownloadCallback;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.services.DataDownloaderService;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.TestFileSystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class EveItemTest {
	@Test
	public void accessorContract() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		PojoTestUtils.validateAccessors(EveItem.class);
	}

	@Test
	public void getTypeId() throws IOException {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("proerties").build();
		final TestFileSystem fileSystemAdapter = new TestFileSystem("./src/test/resources/Test.NeoCom.Infinity");
		final ESIDataAdapter esiDataAdapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final int obtained = item.getTypeId();
		Assert.assertNotNull(item);
		Assert.assertEquals("The type should be the type set.", 34, obtained);
	}

	@Test
	public void getGroupId() throws IOException {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("proerties").build();
		final TestFileSystem fileSystemAdapter = new TestFileSystem("./src/test/resources/Test.NeoCom.Infinity");
		final ESIDataAdapter esiDataAdapter = new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		final int obtained = item.getGroupId();
		Assert.assertNotNull(item);
		Assert.assertEquals("The group should be valid.", 18, obtained);
	}

	@Test
	public void isBlueprint_false() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Capsuleer Bases");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}

	@Test
	public void isBlueprint_true() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Energy Neutralizer Blueprint");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(15799);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}



	private static ESIGlobalAdapter esiAdapter;
	private static ESIDataAdapter esiDataAdapter;
	private static DataDownloaderService downloaderService;

	@Before
	public void setUp() throws Exception {
		esiAdapter = Mockito.mock(ESIGlobalAdapter.class);
		esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		downloaderService = Mockito.mock(DataDownloaderService.class);
	}

	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(EveItem.class);
	}

	//	@Test
	//	public void injectEveItemProvider() {
	//		EsiItemV2.injectEveItemProvider(eveItemProvider);
	//	}

	//	@Test(expected = NullPointerException.class)
	//	public void injectEveItemProvider_null() {
	//		EsiItemV2.injectEveItemProvider(null);
	//	}

	//	@Test
	//	public void injectDownloaderService() {
	//		EsiItemV2.injectDownloaderService(downloaderService);
	//	}

	//	@Test(expected = NullPointerException.class)
	//	public void injectDownloaderService_null() {
	//		EsiItemV2.injectDownloaderService(null);
	//	}

	@Test
	public void getName() throws InterruptedException {
		//		EsiItemV2.injectEveItemProvider(eveItemProvider);
		//		EveItem.injectDownloaderService(downloaderService);
		final EveItem item = new EveItem(34);
		final String expected = "-";
		Mockito.doAnswer(( call ) -> {
			final IEsiItemDownloadCallback callback = call.getArgument(0);
			Assert.assertNotNull(callback);
			return null;
		}).when(downloaderService).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
		final String obtained = item.getName();
		Assert.assertEquals(expected, obtained);
		Mockito.verify(downloaderService, times(1)).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
	}

	@Test
	public void getName_afterDownload() throws InterruptedException {
		//		EsiItemV2.injectEveItemProvider(eveItemProvider);
		//		EveItem.injectDownloaderService(downloaderService);
		final GetUniverseTypesTypeIdOk universeItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final EveItem item = new EveItem(34);
		final String expected = "Test Data";
		Mockito.doAnswer(( call ) -> {
			final IEsiItemDownloadCallback callback = call.getArgument(0);
			Assert.assertNotNull(callback);
			return null;
		}).when(downloaderService).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
		Mockito.when(universeItem.getName()).thenReturn("Test Data");
		//		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, universeItem);
		final String obtained = item.getName();
		Assert.assertEquals(expected, obtained);
		Mockito.verify(downloaderService, times(0)).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
	}

	@Test
	public void getPrice() throws InterruptedException {
		//		EveItem.injectDownloaderService(downloaderService);
		final EveItem item = new EveItem(34);
		final double expected = 100.0;
		Mockito.doAnswer(( call ) -> {
			final IEsiItemDownloadCallback callback = call.getArgument(0);
			Assert.assertNotNull(callback);
			return null;
		}).when(downloaderService).accessItemPrice(item, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
		Mockito.when(esiDataAdapter.searchSDEMarketPrice(any(Integer.class))).thenReturn(100.0);
		double obtained = item.getPrice();
		Assert.assertEquals("Price expected before any initialization of the price.", -1.0, obtained, 0.01);
		//		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_PRICE, Double.valueOf(100.0));
		obtained = item.getPrice();
		Assert.assertEquals("Price expected after updatdd by the downloader.", expected, obtained, 0.01);
		Mockito.verify(downloaderService, times(1)).accessItemPrice(item, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
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
