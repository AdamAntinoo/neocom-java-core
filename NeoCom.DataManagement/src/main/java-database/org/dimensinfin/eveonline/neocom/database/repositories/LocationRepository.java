package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationRepository {
	protected static Logger logger = LoggerFactory.getLogger(LocationRepository.class);
	protected Dao<EsiLocation, Long> locationDao;

	public EsiLocation findById( final long locationId ) throws SQLException {
		return this.locationDao.queryForId(locationId);
	}

	public Map<String, Integer> getCounters() {
		final Map<String, Integer> counters = new HashMap<>();
		try {
			final QueryBuilder<EsiLocation, Long> queryBuilder = this.locationDao.queryBuilder();
			queryBuilder.selectRaw("classType", "COUNT(*)")
					.groupBy("classType");
			final GenericRawResults<String[]> rows = this.locationDao.queryRaw(
					queryBuilder.prepareStatementString());
			int totalRecords = 0;
			for (String[] record : rows.getResults()) {
				final int counter = Integer.parseInt(record[1]);
				counters.put(record[0], counter);
				totalRecords+=counter;
			}
			counters.put("TOTAL", totalRecords);
		} catch (SQLException e) {
			counters.put("TOTAL", -1);
		}
		return counters;
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