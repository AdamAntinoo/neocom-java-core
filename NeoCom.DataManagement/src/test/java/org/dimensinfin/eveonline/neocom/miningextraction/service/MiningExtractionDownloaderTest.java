package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class MiningExtractionDownloaderTest {
	@Test
	public void downloadMiningExtractions() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential )
				.withEsiDataProvider( esiDataProvider )
				.build();
		// Test
		final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
		// Assertions
		Assertions.assertNotNull( extractionList );
		Assertions.assertEquals( 6, extractionList.size() );
	}
}