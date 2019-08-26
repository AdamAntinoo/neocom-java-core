package org.dimensinfin.eveonline.neocom.test.support.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;

import java.sql.SQLException;
import java.util.Objects;

public class SupportMiningRepository extends MiningRepository {
	public int deleteAll() {
		try {
			final DeleteBuilder<MiningExtraction, String> deleteBuilder = this.miningExtractionDao.deleteBuilder();
			deleteBuilder.where().isNotNull("id");
			return deleteBuilder.delete();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return 0;
		}
	}

	public Long countRecords() {
		try {
			return this.miningExtractionDao.countOf();
		} catch (SQLException sqle) {
			logger.info("EX [SupportMiningRepository.countRecords]> SQL exception while counting records: {}",
			            sqle.getMessage());
			return 0L;
		}
	}

	// - B U I L D E R
	public static class Builder {
		private SupportMiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new SupportMiningRepository();
		}

		public SupportMiningRepository.Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public SupportMiningRepository.Builder withMiningExtractionDao( final Dao<MiningExtraction, String> miningExtractionDao ) {
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}

		public SupportMiningRepository build() {
			Objects.requireNonNull(this.onConstruction.miningExtractionDao);
			return this.onConstruction;
		}
	}
}
