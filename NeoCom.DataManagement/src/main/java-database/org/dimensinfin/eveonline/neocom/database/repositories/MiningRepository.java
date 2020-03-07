package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.MiningExtractionEntityToMiningExtraction;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

/**
 * The mining repository isolates the repository fro mining data from the details related to the search and access to the records. Current
 * implementation expects the records persisted on a database repository.
 *
 * There is a class specific to store extractions since their contents are volatile and not stored on the repository. So methods now get the
 * records from the repository and <b>expand</b> the volatile contents to have again a full featured <code>MiningExtraction</code>.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
public class MiningRepository {
	// - C O M P O N E N T S
	protected Dao<MiningExtractionEntity, String> miningExtractionDao;
	protected Dao<MiningExtraction, String> miningExtractionDao2;
	protected LocationCatalogService locationCatalogService;

	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is that today
	 * records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the extractions until
	 * the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this new hour will show the new
	 * ore totals and so on hour after hour.
	 */
	protected MiningRepository() { }

	public List<MiningExtractionEntity> accessDatedMiningExtractions4Pilot( final Credential credential/*, final LocalDate filterDate */ ) {
		try {
			final QueryBuilder<MiningExtractionEntity, String> builder = this.miningExtractionDao.queryBuilder();
			final Where<MiningExtractionEntity, String> where = builder.where();
			where.eq( "ownerId", credential.getAccountId() );
			builder.orderBy( "extractionDateName", true );
			builder.orderBy( "extractionHour", true );
			builder.orderBy( "solarSystemId", true );
			builder.orderBy( "typeId", true );
			final PreparedQuery<MiningExtractionEntity> preparedQuery = builder.prepare();
			NeoComLogger.info( "SELECT: {}", preparedQuery.getStatement() );
			final List<MiningExtractionEntity> dataList = this.miningExtractionDao.query( preparedQuery );
			NeoComLogger.info( "Records read: {}", dataList.size() + "" );
			return dataList;
		} catch (SQLException sqle) {
			NeoComLogger.error( "SQL [MiningRepository.accessDatedMiningExtractions4Pilot]> SQL Exception: {}", sqle );
			return new ArrayList<>();
		}
	}

	public MiningExtractionEntity accessMiningExtractionFindById( final String recordIdentifier ) {
		try {
			return this.miningExtractionDao.queryForId( recordIdentifier );
		} catch (final SQLException sqle) {
			NeoComLogger.error( ErrorInfoCatalog.MINING_EXTRACTION_BYID_SEARCH_FAILED.getErrorMessage( recordIdentifier ), sqle );
			throw new NeoComRuntimeException( ErrorInfoCatalog.MINING_EXTRACTION_BYID_SEARCH_FAILED.getErrorMessage( recordIdentifier ) );
		}
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and
	 * also
	 * grouping previously to rendering. This method can do the sorting but the grouping is not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different
	 * systems
	 * and for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom
	 * database has
	 * to expiration time so the number of days is still not predetermined.
	 */
	public List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao2.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq( "ownerId", credential.getAccountId() );
			builder.orderBy( "id", false );
			final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
			NeoComLogger.info( "SELECT: {}", preparedQuery.getStatement() );
			return this.miningExtractionDao2.query( preparedQuery );
		} catch (SQLException sqle) {
			NeoComLogger.error( sqle );
			return new ArrayList<>();
		}
	}

	public List<MiningExtraction> accessResources4Date( final Credential credential, final LocalDate filterDate ) {
		try {
			final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao2.queryBuilder();
			final Where<MiningExtraction, String> where = builder.where();
			where.eq( "ownerId", credential.getAccountId() )
					.and()
					.eq( "extractionDateName", filterDate.toString( "YYYY-MM-dd" ) );
			builder.selectRaw( "\"typeId\"", "MAX(\"quantity\")" );
			builder.groupBy( "typeId" );
			NeoComLogger.info( "SELECT: {}",
					builder.prepareStatementString() );
			final GenericRawResults<String[]> dataList = this.miningExtractionDao.queryRaw(
					builder.prepareStatementString() );
			List<MiningExtraction> results = new ArrayList<>();
			for (String[] record : dataList.getResults()) {
				results.add( this.miningExtractionDao2.queryForId( record[0] ) );
			}
			NeoComLogger.info( "Records read: {}", results.size() + "" );
			return results;
		} catch (SQLException sqle) {
			NeoComLogger.error( "SQL Exception: {}", sqle );
			sqle.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) {
//		return this.accessDatedMiningExtractions4Pilot( credential, LocalDate.now() );
		return Stream.of( this.accessDatedMiningExtractions4Pilot( credential ) )
				.filter( ( extraction ) -> this.filterOutNotTodayRecords( extraction ) )
				.map( ( extraction ) -> new MiningExtractionEntityToMiningExtraction( this.locationCatalogService ).convert( extraction ) )
				.collect( Collectors.toList() );
	}

	public void persist( final MiningExtractionEntity record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.miningExtractionDao.createOrUpdate( record );
		}
	}

	/**
	 * Filter out all records not belonging to today.
	 *
	 * @param extraction the extraction to check for filtering
	 * @return true if the record should be kept because it has todays date.
	 */
	private boolean filterOutNotTodayRecords( final MiningExtractionEntity extraction ) {
		final String filterDate = LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT );
		return extraction.getExtractionDateName().equalsIgnoreCase( filterDate );
	}

//	/**
//	 * Because transient fields lost their contents when the record is stored on the database, when retrieving the record the
//	 * date should be set again by searching for the esi resources again.
//	 *
//	 * @param extraction the mining extraction to update.
//	 */
//	private MiningExtraction postProcessExtraction( final MiningExtraction extraction ) {
//		extraction.setResourceItem( new NeoItem( extraction.getTypeId() ) );
//		final SpaceLocation location = this.locationCatalogService.searchLocation4Id( extraction.getLocationId().longValue() );
//		if (null != location)
//			extraction.setSolarSystemLocation( (SpaceSystem) location );
//		return extraction;
//	}

	// - B U I L D E R
	public static class Builder {
		private MiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new MiningRepository();
		}

		public Builder( final MiningRepository preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new MiningRepository();
		}

		public MiningRepository build() {
			Objects.requireNonNull( this.onConstruction.miningExtractionDao );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			return this.onConstruction;
		}

		public MiningRepository.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public MiningRepository.Builder withMiningExtractionDao( final Dao<MiningExtractionEntity, String> miningExtractionDao ) {
			Objects.requireNonNull( miningExtractionDao );
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}
		public MiningRepository.Builder withMiningExtractionDao2( final Dao<MiningExtraction, String> miningExtractionDao ) {
			Objects.requireNonNull( miningExtractionDao );
			this.onConstruction.miningExtractionDao2 = miningExtractionDao;
			return this;
		}
	}
}
