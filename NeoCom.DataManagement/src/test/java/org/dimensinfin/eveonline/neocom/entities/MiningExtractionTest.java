package org.dimensinfin.eveonline.neocom.entities;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.TestFileSystem;

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
	public void getPrice() throws IOException {
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("properties").build();
		final TestFileSystem fileSystem = new TestFileSystem("src/test/resources");
		EveItem.injectEsiDataAdapter(new ESIDataAdapter.Builder(configurationProvider, fileSystem).build());
		final EveItem item = Mockito.mock(EveItem.class);
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				                                          .withTypeId(34)
				                                          .withQuantity(1000)
				                                          .build();
		//		Mockito.when(item.getPrice()).thenReturn(0.5);

//		final double expected = 1000 * 0.5;
		final double obtained = miningExtraction.getPrice() * miningExtraction.getQuantity();
		Assert.assertTrue("Tritanium price should be > than 3.", obtained > 1000 * 3.0);
	}
}
