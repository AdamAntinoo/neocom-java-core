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

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;
import org.dimensinfin.eveonline.neocom.support.IntegrationReport;

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
	public void downloadMiningExtractionsFailure() throws SQLException {
		// Given
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningRepository( this.miningRepository )
				.build();
		final MiningRepository miningRepository=Mockito.mock(MiningRepository.class);
		// When
		Mockito.when( miningRepository.accessMiningExtractionFindById(Mockito.anyString()) ).thenReturn( null );
		Mockito.doThrow( new SQLException( "-TEST-EXCEPTION-MESSAGE-" ) )
				.when( miningRepository ).persist( Mockito.any( MiningExtractionEntity.class ) );
		// Exceptions
		final NeoComRuntimeException MINING_EXTRACTION_PERSISTENCE_FAILED = Assertions.assertThrows( NeoComRuntimeException.class, () -> {
					final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
				},
				"Expected miningExtractionDownloader.downloadMiningExtractions() to throw NeoComRuntimeException verification, but it didn't." );
	}

	@Test
	public void downloadMiningExtractionsNoPreviousRecord() {
		// Given
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
		IntegrationReport.generateMiningExtractionReport(extractionList);
		// Assertions
		Assertions.assertNotNull( extractionList );
		Assertions.assertEquals( 8, extractionList.size() );
		final List<MiningExtraction> todays = Stream.of( extractionList )
				.filter( extraction ->
						extraction.getExtractionDateName().equalsIgnoreCase( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) ) )
				.collect( Collectors.toList() );
		IntegrationReport.generateMiningExtractionReport(todays);
//		Assertions.assertEquals( 2, todays.size() );
	}
}