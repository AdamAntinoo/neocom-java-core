package org.dimensinfin.eveonline.neocom.services;

import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.domain.IEsiItemDownloadCallback;
import org.dimensinfin.eveonline.neocom.domain.IPilotDataDownloadCallback;
import org.dimensinfin.eveonline.neocom.domain.PilotDataSections;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class DataDownloaderServiceTest {
	private ESIGlobalAdapter adapter;
//	private EveItemProvider eveItemProvider;

	@Before
	public void setUp() throws Exception {
		adapter = Mockito.mock(ESIGlobalAdapter.class);
//		eveItemProvider = Mockito.mock(EveItemProvider.class);
	}

	@Test
	public void dataDownloaderBuilder() {
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
//				                                      .withEveItemProvider(eveItemProvider)
				                                      .build();
	}

	@Test
	public void dataDownloaderBuilder_notitemprovider() {
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .build();
	}

	@Test(expected = NullPointerException.class)
	public void dataDownloaderBuilder_notesiadapter() {
		final DataDownloaderService service = new DataDownloaderService.Builder(null)
				                                      .build();
	}


	@Test
	public void accessEveItem_cached() throws InterruptedException {
		final IEsiItemDownloadCallback destination = Mockito.mock(IEsiItemDownloadCallback.class);
		final GetUniverseTypesTypeIdOk item = Mockito.mock(GetUniverseTypesTypeIdOk.class);
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
//				                                      .withEveItemProvider(eveItemProvider)
				                                      .build();
		Mockito.when(destination.getTypeId()).thenReturn(34);
		Mockito.when(adapter.getUniverseTypeById(34)).thenReturn(item);
		service.accessEveItem(destination, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
		Mockito.verify(destination, times(1)).signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_DATA, item);
	}

	@Test
	public void accessItemPrice_found() {
		final IEsiItemDownloadCallback destination = Mockito.mock(IEsiItemDownloadCallback.class);
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
//				                                      .withEveItemProvider(eveItemProvider)
				                                      .build();
		Mockito.when(destination.getTypeId()).thenReturn(34);
		Mockito.when(adapter.searchSDEMarketPrice(any(Integer.class))).thenReturn(100.0);
		service.accessItemPrice(destination, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
		Mockito.verify(destination, times(1)).signalCompletion(DataDownloaderService.EsiItemSections.ESIITEM_PRICE
				, adapter.searchSDEMarketPrice(34));
	}

	@Test
	public void accessPilotPublicData() throws InterruptedException {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final GetCharactersCharacterIdOk publicData = Mockito.mock(GetCharactersCharacterIdOk.class);
		final IPilotDataDownloadCallback destination = Mockito.mock(IPilotDataDownloadCallback.class);
		final Credential credential = new Credential();

		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
				                                      .build();
		Mockito.when(adapter.getCharactersCharacterId(any(Integer.class), any(String.class), any(String.class))).thenReturn(publicData);
		Mockito.when(destination.getCredential()).thenReturn(credential);
		service.accessPilotPublicData(destination, PilotDataSections.PILOT_PUBLICDATA);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Mockito.verify(destination, times(1)).signalCompletion(PilotDataSections.PILOT_PUBLICDATA, publicData);
	}

	@Test
	public void accessPilotPublicData_notfound() throws InterruptedException {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final GetCharactersCharacterIdOk publicData = Mockito.mock(GetCharactersCharacterIdOk.class);
		final IPilotDataDownloadCallback destination = Mockito.mock(IPilotDataDownloadCallback.class);
		final Credential credential = new Credential();

		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
				                                      .build();
		Mockito.when(adapter.getCharactersCharacterId(any(Integer.class), any(String.class), any(String.class))).thenReturn(null);
		Mockito.when(destination.getCredential()).thenReturn(credential);
		service.accessPilotPublicData(destination, PilotDataSections.PILOT_PUBLICDATA);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Mockito.verify(destination, times(0)).signalCompletion(PilotDataSections.PILOT_PUBLICDATA, publicData);
	}

	@Test
	public void accessPilotRace() throws InterruptedException {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final GetUniverseRaces200Ok race = Mockito.mock(GetUniverseRaces200Ok.class);
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
				                                      .build();
		final IPilotDataDownloadCallback destination = Mockito.mock(IPilotDataDownloadCallback.class);
		Mockito.when(adapter.searchSDERace(12)).thenReturn(race);
		Mockito.when(destination.getRaceId()).thenReturn(12);
		service.accessPilotRace(destination, PilotDataSections.PILOT_RACE);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Mockito.verify(destination, times(1)).signalCompletion(PilotDataSections.PILOT_RACE, race);
	}

	@Test
	public void accessPilotRace_notfound() throws InterruptedException {
		final ESIGlobalAdapter adapter = Mockito.mock(ESIGlobalAdapter.class);
		final GetUniverseRaces200Ok race = Mockito.mock(GetUniverseRaces200Ok.class);
		final DataDownloaderService service = new DataDownloaderService.Builder(adapter)
				                                      .withEsiAdapter(adapter)
				                                      .build();
		final IPilotDataDownloadCallback destination = Mockito.mock(IPilotDataDownloadCallback.class);
		Mockito.when(adapter.searchSDERace(12)).thenReturn(null);
		Mockito.when(destination.getRaceId()).thenReturn(12);
		service.accessPilotRace(destination, PilotDataSections.PILOT_RACE);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Mockito.verify(destination, times(0)).signalCompletion(PilotDataSections.PILOT_RACE, race);
	}
}
