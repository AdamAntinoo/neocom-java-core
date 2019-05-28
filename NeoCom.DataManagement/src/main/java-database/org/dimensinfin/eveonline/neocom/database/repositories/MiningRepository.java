package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class MiningRepository {
	protected Dao<MiningExtraction, String> miningExtractionDao;
	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is
	 * that today records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the
	 * extractions until the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this
	 * new hour will show the new ore totals and so on hour after hour.
	 */
	private List<MiningExtraction> extractions4Test = new ArrayList<>();

	protected MiningRepository() { }

	public List<MiningExtraction> accessTodayMiningExtractions4PilotMock( final Credential credential ) throws SQLException {
		this.generateExtractionsT1();
		return extractions4Test;
	}
	public List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) throws SQLException {
		final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
		final Where<MiningExtraction, String> where = builder.where();
		where.eq("ownerId", credential.getAccountId());
		builder.orderBy("extractionDateName", true);
		builder.orderBy("extractionHour", true);
		builder.orderBy("solarSystemId", true);
		builder.orderBy("typeId", true);
		final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
		final List<MiningExtraction> dataList = miningExtractionDao.query(preparedQuery);
		List<MiningExtraction> results = new ArrayList<>();
		final String filterDate = DateTime.now().toString("YYYY/MM/dd");
		// Filter out all records not belonging to today.
		for (MiningExtraction extraction : dataList) {
			final String date = extraction.getExtractionDateName().split(":")[0];
			if (date.equalsIgnoreCase(filterDate)) results.add(extraction);
		}
		return results;
	}

	public MiningExtraction accessMiningExtractionFindById( final String recordIdentifier ) throws SQLException {
		return this.miningExtractionDao.queryForId(recordIdentifier);
	}

	public void persist( final MiningExtraction record ) throws SQLException {
		this.miningExtractionDao.createOrUpdate(record);
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and also
	 * grouping previously to rendering. This method can do the sorting but the grouping is not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different systems
	 * and for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom database has
	 * to expiration time so the number of days is still not predetermined.
	 */
	public List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) throws SQLException {
		final QueryBuilder<MiningExtraction, String> builder = this.miningExtractionDao.queryBuilder();
		final Where<MiningExtraction, String> where = builder.where();
		where.eq("ownerId", credential.getAccountId());
		builder.orderBy("id", false);
		final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
		return this.miningExtractionDao.query(preparedQuery);
	}

	private void generateExtractionsT1() {
		extractions4Test.clear();
		// Generate 5 different hours for today.
		extractions4Test.addAll(this.extractionGenerator(34, 10000));
		extractions4Test.addAll(this.extractionGenerator(45498, 10000));
	}

	private List<MiningExtraction> extractionGenerator( final int typeId, final int quantity ) {
		final List<MiningExtraction> todayExtractions = new ArrayList<>();
		// Generate 5 different hours for today.
		for (int i = 5; i > 0; i--) {
			final String date = DateTime.now().toString("YYYY/MM/dd");
			final String id = MiningExtraction.generateRecordId(date, 20 - i, typeId, 30001647, 92223647);
			todayExtractions.add(new MiningExtraction.Builder()
					                     .withTypeId(typeId)
					                     .withSolarSystemId(30001647)
					                     .withQuantity(quantity + (20 - i) * 100)
					                     .withOwnerId(92223647)
					                     .withExtractionDate(new LocalDate())
					                     .withExtractionHour(20 - i)
					                     .build());
		}
		return todayExtractions;
	}

	// - B U I L D E R
	public static class Builder {
		private MiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new MiningRepository();
		}

		public Builder withMiningExtractionDao( final Dao<MiningExtraction, String> miningExtractionDao ) {
			this.onConstruction.miningExtractionDao = miningExtractionDao;
			return this;
		}

		public MiningRepository build() {
			Objects.requireNonNull(this.onConstruction.miningExtractionDao);
			//			singleton = this.onConstruction;
			return this.onConstruction;
		}
	}
}
