package org.dimensinfin.eveonline.neocom.support.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

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

	// - B U I L D E R
	public static class Builder {
		private SupportMiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new SupportMiningRepository();
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
