package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Test;

public class MiningExtractionTest {
	@Test
	public void generateRecordId() {}

	@Test
	public void getQuantity() {
	}

//	@Test
//	public void getPrice() throws IOException {
//		final SupportConfigurationProvider configurationProvider = new SupportConfigurationProvider.Builder("properties").build();
//		final SupportFileSystem fileSystem = new SupportFileSystem();
//		NeoItem.injectEsiDataAdapter(new ESIDataAdapter.Builder(configurationProvider, fileSystem).build());
//		final NeoItem item = Mockito.mock(NeoItem.class);
//		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
//				                                          .withTypeId(34)
//				                                          .withQuantity(1000)
//				                                          .build();
//		//		Mockito.when(item.getPrice()).thenReturn(0.5);
//
////		final double expected = 1000 * 0.5;
//		final double obtained = miningExtraction.getPrice() * miningExtraction.getQuantity();
//		Assert.assertTrue("Tritanium price should be > than 3.", obtained > 1000 * 3.0);
//	}
}
