package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

public class MiningExtractionPersistentIT extends IntegrationEnvironmentDefinitionTCLocal {
	@Test
	public void buildComplete() {
		final MiningRepository miningRepository = Mockito.mock( MiningRepository.class );
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( miningRepository ).build();
		Assertions.assertNotNull( miningExtractionPersistent );
	}

	@Test
	public void buildFailure() {
		final MiningRepository miningRepository = Mockito.mock( MiningRepository.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
					.withMiningRepository( null ).build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
					.build();
		} );
	}

	@Test
	public void persistMiningExtractionsSuccess() throws SQLException {
		// Given
		final MiningRepository miningRepository = new MiningRepository.Builder()
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningExtractionDao( this.itNeoComIntegrationDBAdapter.getMiningExtractionDao() )
				.build();
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( miningRepository ).build();
		final List<MiningExtraction> extractions = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build()
				.downloadMiningExtractions();

		// Test
		miningExtractionPersistent.persistMiningExtractions( extractions );
		// Assertions
		final List<MiningExtractionEntity> obtained = miningRepository.accessMiningExtractions4Pilot( credential4Test );
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 6, obtained.size() );
	}
}