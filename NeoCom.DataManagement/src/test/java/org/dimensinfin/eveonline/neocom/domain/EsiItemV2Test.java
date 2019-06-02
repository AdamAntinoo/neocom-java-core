package org.dimensinfin.eveonline.neocom.domain;

import java.beans.PropertyChangeEvent;

import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.services.DataDownloaderService;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class EsiItemV2Test {
//	private static EveItemProvider eveItemProvider;
	private static ESIGlobalAdapter esiAdapter;
	private static DataDownloaderService downloaderService;

	@Before
	public void setUp() throws Exception {
//		eveItemProvider = Mockito.mock(EveItemProvider.class);
		esiAdapter = Mockito.mock(ESIGlobalAdapter.class);
		downloaderService = Mockito.mock(DataDownloaderService.class);
	}

	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(EsiItemV2.class);
	}

//	@Test
//	public void injectEveItemProvider() {
//		EsiItemV2.injectEveItemProvider(eveItemProvider);
//	}

//	@Test(expected = NullPointerException.class)
//	public void injectEveItemProvider_null() {
//		EsiItemV2.injectEveItemProvider(null);
//	}

	@Test
	public void injectDownloaderService() {
		EsiItemV2.injectDownloaderService(downloaderService);
	}

	@Test(expected = NullPointerException.class)
	public void injectDownloaderService_null() {
		EsiItemV2.injectDownloaderService(null);
	}

	@Test
	public void getName() throws InterruptedException {
//		EsiItemV2.injectEveItemProvider(eveItemProvider);
		EsiItemV2.injectDownloaderService(downloaderService);
		final EsiItemV2 item = new EsiItemV2(34);
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
		EsiItemV2.injectDownloaderService(downloaderService);
		final GetUniverseTypesTypeIdOk universeItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final EsiItemV2 item = new EsiItemV2(34);
		final String expected = "Test Data";
		Mockito.doAnswer(( call ) -> {
			final IEsiItemDownloadCallback callback = call.getArgument(0);
			Assert.assertNotNull(callback);
			return null;
		}).when(downloaderService).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
		Mockito.when(universeItem.getName()).thenReturn("Test Data");
		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, universeItem);
		final String obtained = item.getName();
		Assert.assertEquals(expected, obtained);
		Mockito.verify(downloaderService, times(0)).accessEveItem(item, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
	}

	@Test
	public void getPrice() throws InterruptedException {
		EsiItemV2.injectDownloaderService(downloaderService);
		final EsiItemV2 item = new EsiItemV2(34);
		final double expected = 100.0;
		Mockito.doAnswer(( call ) -> {
			final IEsiItemDownloadCallback callback = call.getArgument(0);
			Assert.assertNotNull(callback);
			return null;
		}).when(downloaderService).accessItemPrice(item, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
		Mockito.when(esiAdapter.searchSDEMarketPrice(any(Integer.class))).thenReturn(100.0);
		double obtained = item.getPrice();
		Assert.assertEquals("Price expected before any initialization of the price.", -1.0, obtained, 0.01);
		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_PRICE, Double.valueOf(100.0));
		obtained = item.getPrice();
		Assert.assertEquals("Price expected after updatdd by the downloader.", expected, obtained, 0.01);
		Mockito.verify(downloaderService, times(1)).accessItemPrice(item, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
	}

	@Test
	public void signalCompletion_itemData() {
		final EventEmitter emitter = Mockito.mock(EventEmitter.class);
		final GetUniverseTypesTypeIdOk universeItem = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final EsiItemV2 item = new EsiItemV2(34);
		Mockito.doAnswer(( call ) -> {
			final PropertyChangeEvent event = call.getArgument(0);
			Assert.assertNotNull(event);
			Assert.assertEquals(EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name(), event.getPropertyName());
			Assert.assertEquals(universeItem, event.getNewValue());
			return null;
		}).when(emitter).sendChangeEvent(new PropertyChangeEvent(item
				, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
				, null, universeItem));
		item.signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, universeItem);
	}
}