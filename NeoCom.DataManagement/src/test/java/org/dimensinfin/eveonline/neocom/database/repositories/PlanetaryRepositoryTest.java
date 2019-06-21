package org.dimensinfin.eveonline.neocom.database.repositories;

import java.util.List;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

import org.junit.Test;
import org.mockito.Mockito;

public class PlanetaryRepositoryTest {
	@Test
	public void build() {
		final ISDEDatabaseAdapter adapter = Mockito.mock(ISDEDatabaseAdapter.class);
		final PlanetaryRepository repository = new PlanetaryRepository.Builder()
				                                       .withSDEDatabaseAdapter(adapter).build();
	}

	@Test
	public void searchSchematics4Id() {
		final int schematicId = 123;
		final ISDEDatabaseAdapter adapter = Mockito.mock(ISDEDatabaseAdapter.class);
		final PlanetaryRepository repository = new PlanetaryRepository.Builder()
				                                       .withSDEDatabaseAdapter(adapter).build();
		final List<Schematics> schematics = repository.searchSchematics4Id(schematicId);
	}
}
