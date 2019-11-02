package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

public class LocationRepository {
//	// - R E G I O N B Y I D
//	private static final int REGION_BYID_REGIONID_COLINDEX = 1;
//	private static final int REGION_BYID_REGIONNAME_COLINDEX = 2;
//	private static final int REGION_BYID_FACTIONID_COLINDEX = 3;
//	private static final String SELECT_REGION_BYID = "SELECT regionID as locationId, regionName as locationName, factionID as factionId" +
//			" FROM mapRegions" +
//			" WHERE regionID = ?";
//	// - C O N S T E L L A T I O N B Y I D
//	private static final int CONSTELLATION_BYID_REGIONID_COLINDEX = 1;
//	private static final int CONSTELLATION_BYID_REGIONNAME_COLINDEX = 2;
//	private static final int CONSTELLATION_BYID_FACTIONID_COLINDEX = 3;
//	private static final int CONSTELLATION_BYID_CONSTELLATIONID_COLINDEX = 4;
//	private static final int CONSTELLATION_BYID_CONSTELLATIONNAME_COLINDEX = 5;
//	private static final String SELECT_CONSTELLATION_BYID = "SELECT mapr.regionID as regionId, mapr.regionName as regionName, " +
//			" mapc.factionID as factionId, " +
//			" mapc.constellationID as constellationID, " +
//			" mapc.constellationName as constellationName" +
//			" FROM mapConstellations mapc, mapRegions mapr" +
//			" WHERE constellationID = ?" +
//			" AND mapr.regionID = mapc.regionID";
//	// - S Y S T E M B Y I D
//	private static final int SYSTEM_BYID_REGIONID_COLINDEX = 1;
//	private static final int SYSTEM_BYID_REGIONNAME_COLINDEX = 2;
//	private static final int SYSTEM_BYID_FACTIONID_COLINDEX = 3;
//	private static final int SYSTEM_BYID_CONSTELLATIONID_COLINDEX = 4;
//	private static final int SYSTEM_BYID_CONSTELLATIONNAME_COLINDEX = 5;
//	private static final int SYSTEM_BYID_SYSTEMID_COLINDEX = 6;
//	private static final int SYSTEM_BYID_SYSTEMNAME_COLINDEX = 7;
//	private static final String SELECT_SYSTEM_BYID = "SELECT mapr.regionID as regionId, mapr.regionName as regionName, " +
//			" mapc.factionID as factionId, " +
//			" mapc.constellationID as constellationID, " +
//			" mapc.constellationName as constellationName, " +
//			" mapss.solarSystemID as systemId, " +
//			" mapss.solarSystemName as systemName " +
//			" FROM mapSolarSystems mapss, mapConstellations mapc, " +
//			" mapRegions mapr " +
//			" WHERE solarSystemID = ? " +
//			" AND mapr.regionID = mapss.regionID " +
//			" AND mapc.constellationID = mapss.constellationID";
//	// - S T A T I O N B Y I D
//	private static final int STATION_BYID_STATIONID_COLINDEX = 1;
//	private static final int STATION_BYID_STATIONSECURITY_COLINDEX = 2;
//	private static final int STATION_BYID_DOCKINGCOST_COLINDEX = 3;
//	private static final int STATION_BYID_OFFICERENTALCOST_COLINDEX = 5;
//	private static final int STATION_BYID_STATIONTYPEID_COLINDEX = 7;
//	private static final int STATION_BYID_CORPORATIONID_COLINDEX = 8;
//	private static final int STATION_BYID_SOLARSYSTEMID_COLINDEX = 9;
//	private static final int STATION_BYID_CONSTELLATIONID_COLINDEX = 10;
//	private static final int STATION_BYID_REGIONID_COLINDEX = 11;
//	private static final int STATION_BYID_STATIONNAME_COLINDEX = 12;
//	private static final int STATION_BYID_REPROCESSINGEFFICIENCY_COLINDEX = 16;
//	private static final int STATION_BYID_REPROESSINGSTATIONSTAKE_COLINDEX = 17;
//	private static final int STATION_BYID_REPROCESSINGHANGARFLAG_COLINDEX = 18;
//	private static final String SELECT_STATION_BYID = "SELECT stationID, \"security\" as stationSecurity, dockingCostPerVolume," +
//			" " +
//			"maxShipVolumeDockable, officeRentalCost, operationID, stationTypeID, corporationID, solarSystemID, " +
//			"constellationID, regionID, stationName, x, y, z, reprocessingEfficiency, reprocessingStationsTake, " +
//			"reprocessingHangarFlag\n" +
//			" FROM staStations sta" +
//			" WHERE stationID = ? ";

	protected static Logger logger = LoggerFactory.getLogger( LocationRepository.class );

	// - C O M P O N E N T S
	private ESIUniverseDataProvider esiUniverseDataProvider;
	private ISDEDatabaseAdapter sdeDatabaseAdapter;
	private Dao<EsiLocation, Long> locationDao;

//	public EsiLocation findById( final long locationId ) throws SQLException {
//		return this.locationDao.queryForId( locationId );
//	}

	public Map<String, Integer> getCounters() {
		final Map<String, Integer> counters = new HashMap<>();
		try {
			final QueryBuilder<EsiLocation, Long> queryBuilder = this.locationDao.queryBuilder();
			queryBuilder
					.selectRaw( "classType", "COUNT(*)" )
					.groupBy( "classType" );
			final GenericRawResults<String[]> rows = this.locationDao.queryRaw( queryBuilder.prepareStatementString() );
			int totalRecords = 0;
			for (String[] record : rows.getResults()) {
				final int counter = Integer.parseInt( record[1] );
				counters.put( record[0], counter );
				totalRecords += counter;
			}
			counters.put( "TOTAL", totalRecords );
		} catch (SQLException e) {
			counters.put( "TOTAL", -1 );
		}
		return counters;
	}

//	public SpaceRegion searchRegionById( final long locationId ) {
//		logger.info( ">< [LocationRepository.searchRegionById]> Searching ID: {}", locationId );
//		EsiLocation target = EsiLocation.getUnknownLocation();
//		try {
//			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement( SELECT_REGION_BYID,
//					new String[]{ Long.toString( locationId ) } );
//			boolean detected = false;
//			while (cursor.moveToNext()) {
//				detected = true;
//				target = new SpaceRegionImplementation.Builder()
//						.withRegionId( cursor.getInt( REGION_BYID_REGIONID_COLINDEX ) )
//						.withRegionName( cursor.getString( REGION_BYID_REGIONNAME_COLINDEX ) )
//						factionId
//						.withClassType( LocationClass.REGION )
//						.build();
//			}
//			if (!detected) {
//				logger.info( "-- [LocationRepository.searchRegionById]> Location: {} not found on any Database - UNKNOWN-.",
//						locationId );
//				target.setId( locationId );
//				target.setSystem( "ID>" + Long.valueOf( locationId ).toString() );
//			}
//			return target;
//		} catch (final SQLException sqle) {
//			logger.error( "E [LocationRepository.searchRegionById]> Exception processing statement: {}", sqle.getMessage() );
//			return target;
//		}
//	}
//
//	public EsiLocation searchConstellationById( final long locationId ) {
//		logger.info( ">< [LocationRepository.searchConstellationById]> Searching ID: {}", locationId );
//		EsiLocation target = EsiLocation.getUnknownLocation();
//		try {
//			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement( SELECT_CONSTELLATION_BYID,
//					new String[]{ Long.toString( locationId ) } );
//			boolean detected = false;
//			while (cursor.moveToNext()) {
//				detected = true;
//				target = new EsiLocation.Builder()
//						.withRegionId( cursor.getInt( CONSTELLATION_BYID_REGIONID_COLINDEX ) )
//						.withRegionName( cursor.getString( CONSTELLATION_BYID_REGIONNAME_COLINDEX ) )
//						.withClassType( LocationClass.CONSTELLATION )
//						.withConstellationId( cursor.getInt( CONSTELLATION_BYID_CONSTELLATIONID_COLINDEX ) )
//						.withConstellationName( cursor.getString( CONSTELLATION_BYID_CONSTELLATIONNAME_COLINDEX ) )
//						.build();
//			}
//			if (!detected) {
//				logger.info(
//						"-- [LocationRepository.searchConstellationById]> Location: {} not found on any Database - UNKNOWN-.",
//						locationId );
//				target.setId( locationId );
//				target.setSystem( "ID>" + Long.valueOf( locationId ).toString() );
//			}
//			return target;
//		} catch (final SQLException sqle) {
//			logger.error( "E [LocationRepository.searchConstellationById]> Exception processing statement: {}",
//					sqle.getMessage() );
//			return target;
//		}
//	}
//
//	public EsiLocation searchSystemById( final long locationId ) {
//		logger.info( ">< [LocationRepository.searchSystemById]> Searching ID: {}", locationId );
//		EsiLocation target = EsiLocation.getUnknownLocation();
//		try {
//			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement( SELECT_SYSTEM_BYID,
//					new String[]{ Long.toString( locationId ) } );
//			boolean detected = false;
//			while (cursor.moveToNext()) {
//				detected = true;
//				target = new EsiLocation.Builder()
//						.withRegionId( cursor.getInt( SYSTEM_BYID_REGIONID_COLINDEX ) )
//						.withRegionName( cursor.getString( SYSTEM_BYID_REGIONNAME_COLINDEX ) )
//						.withClassType( LocationClass.SYSTEM )
//						.withConstellationId( cursor.getInt( SYSTEM_BYID_CONSTELLATIONID_COLINDEX ) )
//						.withConstellationName( cursor.getString( SYSTEM_BYID_CONSTELLATIONNAME_COLINDEX ) )
//						.withSystemId( cursor.getInt( SYSTEM_BYID_SYSTEMID_COLINDEX ) )
//						.withSystemName( cursor.getString( SYSTEM_BYID_SYSTEMNAME_COLINDEX ) )
//						.build();
//			}
//			if (!detected) {
//				logger.info( "-- [LocationRepository.searchSystemById]> Location: {} not found on any Database - UNKNOWN-.",
//						locationId );
//				target.setId( locationId );
//				target.setSystem( "ID>" + Long.valueOf( locationId ).toString() );
//			}
//			return target;
//		} catch (final SQLException sqle) {
//			logger.error( "E [LocationRepository.searchSystemById]> Exception processing statement: {}", sqle.getMessage() );
//			return target;
//		}
//	}
//
//	public StationLocation searchStationById( final long stationId ) {
//		logger.info( ">< [LocationRepository.searchStationById]> Searching ID: {}", stationId );
//		StationLocation target;
//		try {
//			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement( SELECT_STATION_BYID,
//					new String[]{ Long.toString( stationId ) } );
//			boolean detected = false;
//			while (cursor.moveToNext()) {
//				detected = true;
//				return new StationLocation.Builder()
//						.withRegion( this.esiUniverseDataProvider.getUniverseRegionById(
//								cursor.getInt( STATION_BYID_REGIONID_COLINDEX ) ))
//						.withConstellation( this.esiUniverseDataProvider.getUniverseConstellationById(
//								cursor.getInt( STATION_BYID_CONSTELLATIONID_COLINDEX )) )
//						.withSolarSystem(  this.esiUniverseDataProvider.getUniverseSystemById(
//								cursor.getInt( STATION_BYID_SOLARSYSTEMID_COLINDEX )) )
//						.withStation( this.esiUniverseDataProvider.getUniverseStationById(
//								cursor.getInt( STATION_BYID_STATIONID_COLINDEX )) )
//						.withSecurity( cursor.getDouble( STATION_BYID_STATIONSECURITY_COLINDEX ) )
//						.withCorporation(this.esiUniverseDataProvider.getCorporationsCorporationId(
//								cursor.getInt( STATION_BYID_CORPORATIONID_COLINDEX)) )
//						.withCorporationId( cursor.getInt( STATION_BYID_CORPORATIONID_COLINDEX ) )
//						.build();
//			}
//			if (!detected) {
//				logger.info( "-- [LocationRepository.searchStationById]> Location: {} not found on any Database - UNKNOWN-.",
//						stationId );
//				return null;
//			}
//		} catch (final SQLException sqle) {
//			logger.error( "E [LocationRepository.searchStationById]> Exception processing statement: {}", sqle.getMessage() );
//		}
//		return null;
//	}

	public void persist( final EsiLocation record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.locationDao.createOrUpdate( record );
		}
	}

	// - B U I L D E R
	public static class Builder {
		private LocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new LocationRepository();
		}

		public Builder( final LocationRepository preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new LocationRepository();
		}

		public LocationRepository.Builder withESIUniverseDataProvider( final ESIUniverseDataProvider esiUniverseDataProvider ) {
			Objects.requireNonNull( esiUniverseDataProvider );
			this.onConstruction.esiUniverseDataProvider = esiUniverseDataProvider;
			return this;
		}
		public LocationRepository.Builder withSDEDatabaseAdapter( final ISDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}
		public LocationRepository build() {
			Objects.requireNonNull( this.onConstruction.sdeDatabaseAdapter );
			this.onConstruction.locationDao = this.onConstruction.sdeDatabaseAdapter.getLocationDao();
			Objects.requireNonNull( this.onConstruction.locationDao );
			return this.onConstruction;
		}
	}
}