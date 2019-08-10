package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class LocationRepository {
	protected static Logger logger = LoggerFactory.getLogger(LocationRepository.class);
	protected Dao<EsiLocation, Long> locationDao;

	public EsiLocation findById( final long locationId ) throws SQLException {
		return this.locationDao.queryForId(locationId);
	}

	// - B U I L D E R
	public static class Builder {
		private LocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new LocationRepository();
		}

		public LocationRepository.Builder withLocationDao( final Dao<EsiLocation, Long> locationDao ) {
			this.onConstruction.locationDao = locationDao;
			return this;
		}

		public LocationRepository build() {
			Objects.requireNonNull(this.onConstruction.locationDao);
			return this.onConstruction;
		}
	}
}