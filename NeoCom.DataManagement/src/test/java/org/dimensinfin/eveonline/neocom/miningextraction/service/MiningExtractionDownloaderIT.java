package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;
import org.dimensinfin.eveonline.neocom.support.IntegrationReport;

public class MiningExtractionDownloaderIT extends IntegrationEnvironmentDefinitionTCLocal {
	@Test
	public void downloadMiningExtractionsFailure() throws SQLException {
		// Given
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build();
		// Exceptions
		final NeoComRuntimeException MINING_EXTRACTION_PERSISTENCE_FAILED = Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
		} );
	}

	@Test
	public void downloadMiningExtractionsNoPreviousRecord() {
		// Given
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final MiningExtractionDownloader miningExtractionDownloader = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
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
				.build();
		// When
		Mockito.when( credential4Test.getAccountId() ).thenReturn( 93813310 );
		// Test
		final List<MiningExtraction> extractionList = miningExtractionDownloader.downloadMiningExtractions();
		IntegrationReport.generateMiningExtractionReport( extractionList );
		// Assertions
		Assertions.assertNotNull( extractionList );
		Assertions.assertEquals( 8, extractionList.size() );
		final List<MiningExtraction> todays = Stream.of( extractionList )
				.filter( extraction ->
						extraction.getExtractionDateName()
								.equalsIgnoreCase( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) ) )
				.collect( Collectors.toList() );
		IntegrationReport.generateMiningExtractionReport( todays );
		Assertions.assertEquals( 2, todays.size() );
	}
}