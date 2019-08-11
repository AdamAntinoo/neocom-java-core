package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import org.dimensinfin.eveonline.neocom.adapters.SDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.RawStatement;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationRepository {
	// - L O C A T I O N B Y I D
	private static final int REGION_BYID_REGIONID_COLINDEX = 1;
	private static final int REGION_BYID_REGIONNAME_COLINDEX = 2;
	private static final int REGION_BYID_FACTIONID_COLINDEX = 3;
	//	private static final int LOCATIONBYID_CONSTELLATIONID_COLINDEX = 7;
//	private static final int LOCATIONBYID_CONSTELLATION_COLINDEX = 8;
//	private static final int LOCATIONBYID_REGIONID_COLINDEX = 9;
//	private static final int LOCATIONBYID_REGION_COLINDEX = 10;
//	private static final int LOCATIONBYID_TYPEID_COLINDEX = 2;
//	private static final int LOCATIONBYID_LOCATIONID_COLINDEX = 1;
//	private static final int LOCATIONBYID_SECURITY_COLINDEX = 4;
	private static final String SELECT_REGION_BYID = "SELECT regionID as locationId, regionName as locationName, factionID as factionId" +
			                                                 " FROM mapRegions" +
			                                                 " WHERE regionID = ?";
	protected static Logger logger = LoggerFactory.getLogger(LocationRepository.class);
	// - C O M P O N E N T S
	protected SDEDatabaseAdapter sdeDatabaseAdapter;
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
				totalRecords += counter;
			}
			counters.put("TOTAL", totalRecords);
		} catch (SQLException e) {
			counters.put("TOTAL", -1);
		}
		return counters;
	}

	public EsiLocation searchRegionById( final long locationId ) {
		logger.info(">< [LocationRepository.searchRegionById]> Searching ID: {}", locationId);
		EsiLocation target = EsiLocation.getUnknownLocation();
		try {
			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement(SELECT_REGION_BYID,
			                                                                       new String[]{ Long.toString(locationId) });
			boolean detected = false;
			while (cursor.moveToNext()) {
				detected = true;
				target.setRegionId(cursor.getInt(REGION_BYID_REGIONID_COLINDEX));
				target.setRegion(cursor.getString(REGION_BYID_REGIONNAME_COLINDEX));
				target.setClassType(LocationClass.REGION);
				target.getId(); // Update the final location identifier
			}
			if (!detected) {
				logger.info("-- [LocationRepository.searchRegionById]> Location: {} not found on any Database - UNKNOWN-.",
				            locationId);
				target.setId(locationId);
				target.setSystem("ID>" + Long.valueOf(locationId).toString());
			}
			return target;
		} catch (final SQLException sqle) {
			logger.error("E [LocationRepository.searchRegionById]> Exception processing statement: {}", sqle.getMessage());
			return target;
		}
	}

	public void persist( final EsiLocation record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.locationDao.createOrUpdate(record);
		}
	}

	// - B U I L D E R
	public static class Builder {
		private LocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new LocationRepository();
		}

		public LocationRepository.Builder withSDEDatabaseAdapter( final SDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public LocationRepository.Builder withLocationDao( final Dao<EsiLocation, Long> locationDao ) {
			this.onConstruction.locationDao = locationDao;
			return this;
		}

		public LocationRepository build() {
			Objects.requireNonNull(this.onConstruction.locationDao);
			Objects.requireNonNull(this.onConstruction.sdeDatabaseAdapter);
			return this.onConstruction;
		}
	}
}