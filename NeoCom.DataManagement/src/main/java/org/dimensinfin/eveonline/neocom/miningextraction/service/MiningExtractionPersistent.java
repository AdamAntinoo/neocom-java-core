package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.MiningExtractionToMiningExtractionEntityConverter;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class MiningExtractionPersistent {
	private MiningRepository miningRepository;

	private MiningExtractionPersistent() {}

	public void persistMiningExtractions( final List<MiningExtraction> extractions ) {
		Stream.of( Objects.requireNonNull( extractions ) )
				.map( ( extraction ) -> {
					final MiningExtractionEntity extractionEntity = new MiningExtractionToMiningExtractionEntityConverter().convert( extraction );
					final String recordId = extractionEntity.getId();
					if (null != this.miningRepository.accessMiningExtractionFindById( recordId )) {
						NeoComLogger.info( "Found previous record on database: {}.", extractionEntity.getId() );
						// There was a previous record so calculate the delta for this hour.
						final long currentQty = extractionEntity.getQuantity();
						try {
							this.miningRepository.persist( extractionEntity.setQuantity( extractionEntity.getQuantity() ) );
						} catch (final SQLException sqle) {
							NeoComLogger.error( sqle );
							throw new NeoComRuntimeException( ErrorInfoCatalog.MINING_EXTRACTION_PERSISTENCE_FAILED.getErrorMessage(
									extractionEntity.getId(),
									sqle.getCause().toString() ) );
						}
						NeoComLogger.info( "Updating mining extraction: {} > Quantity: {}/{}",
								recordId + "", extractionEntity.getQuantity() + "", currentQty + "" );
					} else {
						// Create a new record with the entity.
						try {
							this.miningRepository.persist( extractionEntity );
						} catch (final SQLException sqle) {
							NeoComLogger.error( sqle );
							throw new NeoComRuntimeException( ErrorInfoCatalog.MINING_EXTRACTION_PERSISTENCE_FAILED.getErrorMessage(
									extractionEntity.getId(),
									sqle.getCause().toString() ) );
						}
						NeoComLogger.info( "Creating mining extraction: {} > Quantity: {}/{}",
								recordId + "", extractionEntity.getQuantity() + "", extractionEntity.getQuantity() + "" );
					}
					return extractionEntity;
				} )
				.collect( Collectors.toList() );
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionPersistent onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionPersistent();
		}

		public MiningExtractionPersistent build() {
			Objects.requireNonNull( this.onConstruction.miningRepository );
			return this.onConstruction;
		}

		public MiningExtractionPersistent.Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull( miningRepository );
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}
	}
}