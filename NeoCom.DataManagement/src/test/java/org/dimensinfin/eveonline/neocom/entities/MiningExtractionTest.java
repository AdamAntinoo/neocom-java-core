package org.dimensinfin.eveonline.neocom.entities;

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
		final MiningExtraction miningExtraction = new MiningExtraction()
				                                          .setTypeId(34)
				                                          .setQuantity(1000);
		Mockito.when(item.getPrice()).thenReturn(0.5);
		final double expected = 1000 * 0.5;
		final double obtained = miningExtraction.getPrice() * miningExtraction.getQuantity();
		Assert.assertEquals(expected, obtained,0.01);
	}
}
