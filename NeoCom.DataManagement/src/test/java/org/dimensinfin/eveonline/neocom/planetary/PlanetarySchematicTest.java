package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.repositories.PlanetaryRepository;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PlanetarySchematicTest {
	@Test
	public void build_complete() {
		final int schematicId = 123;
		final ISDEDatabaseAdapter sdeDatabaseAdapter = Mockito.mock(ISDEDatabaseAdapter.class);
		final PlanetaryRepository repository = new PlanetaryRepository.Builder()
				                                       .withSDEDatabaseAdapter(sdeDatabaseAdapter).build();
		final PlanetarySchematic planetary = new PlanetarySchematic.Builder(schematicId)
				                                     .withPlanetaryRepository(repository)
				                                     .build();
		Assert.assertNotNull(planetary);
	}

}
