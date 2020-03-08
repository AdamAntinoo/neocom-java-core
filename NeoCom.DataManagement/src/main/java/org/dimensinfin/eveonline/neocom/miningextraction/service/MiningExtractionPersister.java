package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class MiningExtractionPersister {
	private MiningRepository miningRepository;

	private MiningExtractionPersister() {}

	private void a() {
		final MiningExtraction extraction=null;
		final String recordId = extraction.getId();
		final MiningExtractionEntity targetRecord = this.miningRepository.accessMiningExtractionFindById( recordId );
		if (null != targetRecord) {
			NeoComLogger.info( "Found previous record on database: {}.", targetRecord.getId() );
			// There was a previous record so calculate the delta for this hour.
			final long currentQty = targetRecord.getQuantity();
			try {
				this.miningRepository.persist( targetRecord.setQuantity( extraction.getQuantity() ) );
			} catch (final SQLException sqle) {
				NeoComLogger.error( sqle );
				throw new NeoComRuntimeException(
						ErrorInfoCatalog.MINING_EXTRACTION_PERSISTENCE_FAILED.getErrorMessage(
								targetRecord.getId(),
								sqle.getCause().toString() ) );
			}
			NeoComLogger.info( "Updating mining extraction: {} > Quantity: {}/{}",
					recordId + "", extraction.getQuantity() + "", currentQty + "" );
		}
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionPersister onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionPersister();
		}

		public MiningExtractionPersister build() {
			return this.onConstruction;
		}
	}
}