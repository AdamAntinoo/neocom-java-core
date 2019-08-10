package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import javax.xml.stream.Location;

public class LocationRepository {
	protected static Logger logger = LoggerFactory.getLogger(LocationRepository.class);
	protected Dao<Location, Integer> locationDao;

	// - B U I L D E R
	public static class Builder {
		private LocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new LocationRepository();
		}

		public LocationRepository.Builder withLocationDao( final Dao<Location, Integer> locationDao ) {
			this.onConstruction.locationDao = locationDao;
			return this;
		}

		public LocationRepository build() {
			Objects.requireNonNull(this.onConstruction.locationDao);
			return this.onConstruction;
		}
	}
}