package org.dimensinfin.eveonline.neocom.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESIDataPersistenceService {
	private static Logger logger = LoggerFactory.getLogger(ESIDataPersistenceService.class);

	private MiningRepository miningRepository;
	private ESIDataAdapter esiAdapter;

	private ESIDataPersistenceService() {
	}

	/**
	 * Mining actions are small records that register the ore collected by a Pilot or the Moon mining done by a Corporation. The time
	 * base is 10 minutes and I suppose that those records are aggregated during a day. The data is a list of entries, each one
	 * declaring the quantity of one ore mined on a date and related to a single star system.
	 *
	 * The processing algorithm should add the capturing hour of day to the search index and update with the current value or create a
	 * new record when the index matches. The <b>id</b> of the record is changed to an string having the next fields:
	 * YEAR/MONTH/DAY:HOUR-TYPEID-SYSTEMID-OWNERID.
	 * Once one record is found instead storing on the database a single record per day/item/system/owner we store this same data for
	 * each hour until the date changes and then we stop processing entries until we have new current mining extractions.
	 */
	public List<MiningExtraction> persistMiningActionsESI( final Credential credential ) {
		logger.info(">> [ESIDataPersistenceService.persistMiningActionsESI]> Credential: []", credential.getAccountName());
		List<MiningExtraction> oreExtractions = new ArrayList<>();
		final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = this.esiAdapter.getCharactersCharacterIdMining(credential.getAccountId()
				, credential.getRefreshToken()
				, credential.getDataSource());
		if (null != miningActionsOk) {
			logger.info("-- [ESIDataPersistenceService.persistMiningActionsESI]> Downloaded {} extractions.", miningActionsOk.size());
			for (GetCharactersCharacterIdMining200Ok extractionOk : miningActionsOk) {
				this.processMiningExtraction(extractionOk, credential);
			}
		}
		logger.info("<< [ESIDataPersistenceService.persistMiningActionsESI]");
		return oreExtractions;
	}

	private void processMiningExtraction( final GetCharactersCharacterIdMining200Ok extractionOk, final Credential credential ) {
		// Before doing any store of the data, see if this is a delta. Search for an already existing record.
		final String recordId = MiningExtraction.generateRecordId(extractionOk.getDate(), extractionOk.getTypeId()
				, extractionOk.getSolarSystemId(), credential.getAccountId());
		try {
			final MiningExtraction recordFound = this.miningRepository.accessMiningExtractionFindById(recordId);
			if (null != recordFound) {
				final long currentQty = recordFound.getQuantity();
				recordFound.setQuantity(extractionOk.getQuantity().intValue());
				this.miningRepository.persist(recordFound);
				logger.info("-- [persistMiningActionsESI]> Updating mining extraction: {} > Quantity: {}/{}"
						, recordId, extractionOk.getQuantity(), currentQty);
			} else this.createMiningExtraction(recordId, extractionOk, credential);
		} catch (SQLException sqle) {
			logger.info("EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, sqle.getMessage());
		} catch (NeoComRuntimeException nrex) {
			logger.info("EX [ESIDataPersistenceService.persistMiningActionsESI]> Credential not found in the list. Exception: {}"
					, nrex.getMessage());
		}
	}

	private void createMiningExtraction( final String recordId, final GetCharactersCharacterIdMining200Ok extractionOk, final Credential credential ) throws SQLException {
		final MiningExtraction newExtraction = new MiningExtraction.Builder()
				                                       .withTypeId(extractionOk.getTypeId())
				                                       .withSolarSystemId(extractionOk.getSolarSystemId())
				                                       .withQuantity(extractionOk.getQuantity().intValue())
				                                       .withOwnerId(credential.getAccountId())
				                                       .withExtractionDate(extractionOk.getDate())
				                                       .build();
		this.miningRepository.persist(newExtraction);
		logger.info("-- [persistMiningActionsESI]> Creating new mining extraction: {} > Quantity: {}"
				, recordId, extractionOk.getQuantity());
	}

	// - B U I L D E R
	public static class Builder {
		private ESIDataPersistenceService onConstruction;

		public Builder() {
			this.onConstruction = new ESIDataPersistenceService();
		}

		public Builder withEsiAdapter( final ESIDataAdapter esiAdapter ) {
			this.onConstruction.esiAdapter = esiAdapter;
			return this;
		}

		public Builder withMiningRepository( final MiningRepository miningRepository ) {
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}

		public ESIDataPersistenceService build() {
			return this.onConstruction;
		}
	}
}
