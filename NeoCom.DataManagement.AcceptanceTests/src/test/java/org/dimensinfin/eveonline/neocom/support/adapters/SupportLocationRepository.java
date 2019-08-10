package org.dimensinfin.eveonline.neocom.support.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;

import java.sql.SQLException;
import java.util.Objects;

import javax.xml.stream.Location;

public class SupportLocationRepository extends LocationRepository {
	/**
	 * Delete all locations from the Location catalog cache. This is only required on acceptance tests to setup
	 * specific scenarios.
	 */
	public int deleteAll() {
		try {
			final DeleteBuilder<Location, Integer> deleteBuilder = this.locationDao.deleteBuilder();
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

		public SupportLocationRepository.Builder withLocationDao( final Dao<Location, Integer> locationDao ) {
			this.onConstruction.locationDao = locationDao;
			return this;
		}

		public SupportLocationRepository build() {
			Objects.requireNonNull(this.onConstruction.locationDao);
			return this.onConstruction;
		}
	}

}