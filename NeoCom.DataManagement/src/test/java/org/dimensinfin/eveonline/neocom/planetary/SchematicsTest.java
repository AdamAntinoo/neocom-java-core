package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;
import org.dimensinfin.eveonline.neocom.support.TestEsiAdapterReady;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchematicsTest extends TestEsiAdapterReady {
	private Schematics schematic4fields;

	@Before
	public void setUp() throws Exception {
		final ESIDataAdapter esiDataAdapter = this.setupRealAdapter();
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		MarketDataSet.injectEsiDataAdapter(esiDataAdapter);
		schematic4fields = new Schematics.Builder()
				                   .withSchematicId(123)
				                   .withSchematicName("Electrolytes")
				                   .withCycleTime(1800)
				                   .withResourceTypeId(2390)
				                   .withQuantity(1000)
				                   .withDirection(false)
				                   .build();
	}

	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(Schematics.class);
	}

	@Test
	public void build_complete() {
		final Schematics schematic = new Schematics.Builder()
				                             .withSchematicId(123)
				                             .withSchematicName("Electrolytes")
				                             .withCycleTime(1800)
				                             .withResourceTypeId(2390)
				                             .withQuantity(1000)
				                             .withDirection(false)
				                             .build();
		Assert.assertNotNull(schematic);
	}

	@Test
	public void build_incomplete() {
		final Schematics schematic = new Schematics.Builder()
				                             .withSchematicId(123)
				                             .withResourceTypeId(2390)
				                             .withQuantity(1000)
				                             .build();
		Assert.assertNotNull(schematic);
	}

	@Test
	public void getTypeId() {
		final int obtained = schematic4fields.getTypeId();
		Assert.assertEquals("Verify the value for the typeId.", 2390, obtained);
	}

	@Test
	public void getName() {
		final String obtained = schematic4fields.getName();
		Assert.assertEquals("Verify the value for the name.", "Electrolytes", obtained);
	}

	@Test
	public void getQuantity() {
		final int obtained = schematic4fields.getQuantity();
		Assert.assertEquals("Verify the value for the quantity.", 1000, obtained);
	}

	@Test
	public void getGroupName() {
		final String obtained = schematic4fields.getGroupName();
		Assert.assertEquals("Verify the value for the group name.", "Basic Commodities - Tier 1", obtained);
	}
}
