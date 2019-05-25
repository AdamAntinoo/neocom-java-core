package org.dimensinfin.eveonline.neocom.entities;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.model.EveItem;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class MiningExtractionTest {
	@Test
	public void generateRecordId() {}

	@Test
	public void getQuantity() {
	}

	@Test
	public void getPrice() {
		final EveItem item = Mockito.mock(EveItem.class);
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				                                          .withTypeId(34)
				                                          .withQuantity(1000)
				                                          .build();
		Mockito.when(item.getPrice()).thenReturn(0.5);
		final double expected = 1000 * 0.5;
		final double obtained = miningExtraction.getPrice() * miningExtraction.getQuantity();
		Assert.assertEquals(0.0, obtained, 0.01);
	}
}
