package org.dimensinfin.eveonline.neocom.mining.updaters;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MiningExtractionUpdater extends NeoComUpdater<MiningExtraction> {
	private static final long MINING_EXTRACTION_CACHE_TIME = TimeUnit.SECONDS.toMillis(600);
	private Credential credential;
	private MiningRepository miningRepository;

	public MiningExtractionUpdater( final MiningExtraction model ) {
		super(model);
	}

	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus(MINING_EXTRACTION_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return this.getModel().getJsonClass().toUpperCase() + ":" + this.getModel().getId();
	}

	@Override
	public void onRun() {
		if (null != esiDataAdapter) {
			// Download a new set of mining extractions data from esi.
			final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = this.getMiningActions();
			for (GetCharactersCharacterIdMining200Ok extractionOk : miningActionsOk) {
				final MiningExtraction miningExtraction = new MiningExtraction.Builder()
						                                          .fromMining(extractionOk)
						                                          .withExtractionHour(this.getExtractionHour())
						                                          .withOwnerId(this.credential.getAccountId())
						                                          .build();
				this.processMiningExtraction(miningExtraction, this.credential);
			}
		}
	}

	protected int getExtractionHour() {
		return LocalTime.now().getHourOfDay();
	}

	protected List<GetCharactersCharacterIdMining200Ok> getMiningActions() {
		final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = esiDataAdapter.getCharactersCharacterIdMining(
				this.credential);
		if (null != miningActionsOk)
			logger.info("-- [MiningExtractionUpdater.getMiningActions]> Downloaded {} extractions.", miningActionsOk.size());
		return miningActionsOk;
	}

	protected void processMiningExtraction( final MiningExtraction extraction, final Credential credential ) {
		// Before doing any store of the data, see if this is a delta. Search for an already existing record.
		try {
			final MiningExtraction recordFound = this.miningRepository.accessMiningExtractionFindById(extraction.getId());
			if (null != recordFound) {
				final long currentQty = recordFound.getQuantity();
				recordFound.setQuantity(extraction.getQuantity());
				this.miningRepository.persist(recordFound);
				logger.info("-- [persistMiningActionsESI]> Updating mining extraction: {} > Quantity: {}/{}"
						, extraction.getId(), extraction.getQuantity(), currentQty);
			} else {
				this.miningRepository.persist(extraction);
				logger.info("-- [persistMiningActionsESI]> Creating new mining extraction: {} > Quantity: {}"
						, extraction.getId(), extraction.getQuantity());
			}
		} catch (SQLException sqle) {
			logger.info("EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, sqle.getMessage());
		} catch (NeoComRuntimeException nrex) {
			logger.info("EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, nrex.getMessage());
		}
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionUpdater onConstruction;

		public Builder(final MiningExtraction miningExtraction) {
			this.onConstruction = new MiningExtractionUpdater(miningExtraction);
		}

		public Builder withCredential( final Credential credential ) {
			this.onConstruction.credential = credential;
			return this;
		}
		public Builder withMiningRepository( final MiningRepository miningRepository ) {
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}

		public MiningExtractionUpdater build() {
			Objects.requireNonNull(this.onConstruction.credential);
			Objects.requireNonNull(this.onConstruction.miningRepository);
			return this.onConstruction;
		}
	}

}
