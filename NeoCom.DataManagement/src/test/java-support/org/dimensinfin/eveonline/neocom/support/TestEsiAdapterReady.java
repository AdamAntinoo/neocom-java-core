package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.repositories.PlanetaryRepository;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.junit.Before;

import java.io.IOException;

public class TestEsiAdapterReady  {
	protected static ESIDataAdapter esiDataAdapter;
	protected static TestSDEDBAdapter sdeDBAdapter;
	protected static PlanetaryRepository planetaryRepository;

	@Before
	public void setUp() throws Exception {
		esiDataAdapter = this.setupRealAdapter();
		sdeDBAdapter = new TestSDEDBAdapter.Builder()
				.withDatabasePath("database/")
				.withDatabaseName("sde.db")
				.withFileSystem(new TestFileSystem())
				.build();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		planetaryRepository = new PlanetaryRepository.Builder()
				.withSDEDatabaseAdapter(sdeDBAdapter).build();
		//		facility = Mockito.mock(PlanetaryFacility.class);
		//		final GetCharactersCharacterIdPlanetsPlanetIdOkContents content = Mockito.mock(GetCharactersCharacterIdPlanetsPlanetIdOkContents.class);
		//		final List<GetCharactersCharacterIdPlanetsPlanetIdOkContents> contentsList = new ArrayList<>();
		//		Mockito.when(content.getTypeId()).thenReturn(2329);
		//		contentsList.add(content);
		//		contentsList.add(content);
		//		Mockito.when(facility.getContents()).thenReturn(contentsList);
	}

	protected ESIDataAdapter setupRealAdapter() throws IOException {
		final IConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("properties").build();
		final IFileSystem fileSystemAdapter = new TestFileSystem();
		return new ESIDataAdapter.Builder(configurationProvider, fileSystemAdapter).build();
	}
}
