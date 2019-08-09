package org.dimensinfin.eveonline.neocom.mining.updaters;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MiningExtractionUpdater extends NeoComUpdater<Credential> {
	private static final long MINING_EXTRACTION_CACHE_TIME = TimeUnit.SECONDS.toMillis(600);
	private MiningRepository miningRepository;

	public MiningExtractionUpdater( final Credential model ) {
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
		return "MININGEXTRACTIONUPDATER" + ":" + this.getModel().getAccountId();
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
						                                          .withOwnerId(this.getModel().getAccountId())
						                                          .build();
				this.processMiningExtraction(miningExtraction, this.getModel(), LocalDate.now());
			}
		}
	}

	protected int getExtractionHour() {
		return LocalTime.now().getHourOfDay();
	}

	protected List<GetCharactersCharacterIdMining200Ok> getMiningActions() {
		final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = esiDataAdapter.getCharactersCharacterIdMining(
				this.getModel());
		if (null != miningActionsOk)
			logger.info("-- [MiningExtractionUpdater.getMiningActions]> Downloaded {} extractions.", miningActionsOk.size());
		return miningActionsOk;
	}

	public void processMiningExtraction( final MiningExtraction extraction, final Credential credential, final LocalDate now ) {
		// If the extraction is from the current date then process deltas.
//		final LocalDate today = LocalDate.now();
		try {
			if (now.equals(extraction.getExtractionDate())) {
				// Before doing any store of the data, see if this is a delta. Search for an already existing record.
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
			} else { // Create or update the EOD (hour = 24) record
				extraction.setExtractionHour(24);
				this.miningRepository.persist(extraction);
			}
		} catch (SQLException sqle) {
			logger.info(
					"EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, sqle.getMessage());
		} catch (NeoComRuntimeException nrex) {
			logger.info(
					"EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, nrex.getMessage());
		}
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionUpdater onConstruction;

		public Builder( final Credential credential ) {
			this.onConstruction = new MiningExtractionUpdater(credential);
		}

		public Builder withMiningRepository( final MiningRepository miningRepository ) {
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}

		public MiningExtractionUpdater build() {
			Objects.requireNonNull(this.onConstruction.miningRepository);
			return this.onConstruction;
		}
	}

}
