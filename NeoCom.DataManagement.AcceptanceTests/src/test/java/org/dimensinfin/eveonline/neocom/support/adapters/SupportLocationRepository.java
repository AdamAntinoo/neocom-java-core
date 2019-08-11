package org.dimensinfin.eveonline.neocom.support.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.dimensinfin.eveonline.neocom.adapters.SDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;

import java.sql.SQLException;
import java.util.Objects;

public class SupportLocationRepository extends LocationRepository {
	/**
	 * Delete all locations from the Location catalog cache. This is only required on acceptance tests to setup
	 * specific scenarios.
	 */
	public int deleteAll() {
		try {
			final DeleteBuilder<EsiLocation, Long> deleteBuilder = this.locationDao.deleteBuilder();
			deleteBuilder.where().isNotNull("id");
			return deleteBuilder.delete();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return 0;
		}
	}

	// - B U I L D E R
	public static class Builder {
		private SupportLocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new SupportLocationRepository();
		}

		public SupportLocationRepository.Builder withLocationDao( final Dao<EsiLocation, Long> locationDao ) {
			this.onConstruction.locationDao = locationDao;
			return this;
		}
		public SupportLocationRepository.Builder withSDEDatabaseAdapter( final SDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public SupportLocationRepository build() {
			Objects.requireNonNull(this.onConstruction.locationDao);
			Objects.requireNonNull(this.onConstruction.sdeDatabaseAdapter);
			return this.onConstruction;
		}
	}
}