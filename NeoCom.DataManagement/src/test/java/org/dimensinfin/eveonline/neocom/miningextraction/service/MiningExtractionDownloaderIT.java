package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

public class MiningExtractionDownloaderIT extends IntegrationEnvironmentDefinitionTCLocal {
	private MiningRepository miningRepository;

	@BeforeEach
	public void beforeEach() throws SQLException {
		this.miningRepository = new MiningRepository.Builder()
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningExtractionDao( this.itNeoComIntegrationDBAdapter.getMiningExtractionDao() )
				.build();
		// Clear all extraction records.

		// Install test records to calculate deltas
	}

	@Test
	public void downloadMiningExtractionsNoPreviousRecord() {
		// Given
//		final Credential credential = Mockito.mock( Credential.class );
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningRepository( this.miningRepository )
				.build();
		// Test
		final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
		// Assertions
		Assertions.assertNotNull( extractionList );
		Assertions.assertEquals( 6, extractionList.size() );
	}

	@Test
	public void downloadMiningExtractionsToday() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningRepository( this.miningRepository )
				.build();
		// When
		Mockito.when( credential4Test.getAccountId() ).thenReturn( 93813310 );
		// Test
		final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
		// Assertions
		Assertions.assertNotNull( extractionList );
		Assertions.assertEquals( 8, extractionList.size() );
		final List<MiningExtraction> todays = Stream.of( extractionList )
				.filter( extraction ->
						extraction.getExtractionDateName().equalsIgnoreCase( LocalDate.now().toString( MiningExtraction.EXTRACTION_DATE_FORMAT ) ) )
				.collect( Collectors.toList() );
		Assertions.assertEquals( 2, extractionList.size() );
	}
}