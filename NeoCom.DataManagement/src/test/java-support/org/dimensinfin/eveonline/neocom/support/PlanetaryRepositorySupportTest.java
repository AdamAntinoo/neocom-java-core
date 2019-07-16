package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.database.repositories.PlanetaryRepository;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class PlanetaryRepositorySupportTest extends CredentialSupportTest {
	protected PlanetaryRepository planetaryRepository;
	protected List<Schematics> schematics;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.planetaryRepository = new PlanetaryRepository.Builder()
				.withSDEDatabaseAdapter(new TestSDEDBAdapter.Builder()
						.withDatabasePath("database/").withDatabaseName("sde.db")
						.withFileSystem(new TestFileSystem()).build())
				.build();
		this.schematics = this.generateSchematics();
	}

	protected List<Schematics> generateSchematics() {
		final List<Schematics> schematics = new ArrayList<>();
		schematics.add(new Schematics.Builder()
				.withSchematicId(92)
				.withSchematicName("Synthetic Synapses")
				.withCycleTime(3600)
				.withResourceTypeId(2312)
				.withQuantity(10)
				.withDirection((1 == 1) ? true : false)
				.build());
		schematics.add(new Schematics.Builder()
				.withSchematicId(92)
				.withSchematicName("Synthetic Synapses")
				.withCycleTime(3600)
				.withResourceTypeId(2319)
				.withQuantity(10)
				.withDirection((1 == 1) ? true : false)
				.build());
		schematics.add(new Schematics.Builder()
				.withSchematicId(92)
				.withSchematicName("Synthetic Synapses")
				.withCycleTime(3600)
				.withResourceTypeId(2346)
				.withQuantity(3)
				.withDirection((0 == 1) ? true : false)
				.build());
		return schematics;
	}
}
