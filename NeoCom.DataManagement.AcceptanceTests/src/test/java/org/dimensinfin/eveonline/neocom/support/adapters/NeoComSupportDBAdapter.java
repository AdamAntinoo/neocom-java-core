package org.dimensinfin.eveonline.neocom.support.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.entities.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.Location;

public class NeoComSupportDBAdapter {
	private static Logger logger = LoggerFactory.getLogger(NeoComSupportDBAdapter.class);

	private String databaseConnection;
	private JdbcPooledConnectionSource connectionSource = null;

	private Dao<Credential, String> credentialDao = null;
	private Dao<MiningExtraction, String> miningExtractionDao = null;
	private Dao<Location, Integer> locationDao = null;

	private NeoComSupportDBAdapter() {
	}

	public boolean databaseValid() {
		if (null != this.databaseConnection)
			return true;
		else return false;
	}

	public Dao<Credential, String> getCredentialDao() throws SQLException {
		if (null == this.credentialDao) {
			this.credentialDao = DaoManager.createDao(this.getConnectionSource(), Credential.class);
		}
		return this.credentialDao;
	}

	public Dao<Location, Integer> getLocationDao() throws SQLException {
		if (null == this.locationDao) {
			this.locationDao = DaoManager.createDao(this.getConnectionSource(), Location.class);
		}
		return this.locationDao;
	}

	public Dao<MiningExtraction, String> getMiningExtractionDao() throws SQLException {
		if (null == this.miningExtractionDao) {
			this.miningExtractionDao = DaoManager.createDao(this.getConnectionSource(), MiningExtraction.class);
		}
		return this.miningExtractionDao;
	}

	protected void openNeoComDB() throws SQLException {
		// TODO - read the current database version to run the upgrade method if do not match.
		this.createConnectionSource();
		this.onCreate(this.connectionSource);
	}

	protected ConnectionSource getConnectionSource() throws SQLException {
		if (null == this.connectionSource) this.openNeoComDB();
		return this.connectionSource;
	}

	/**
	 * Creates and configures the connection data source to the database.
	 */
	protected void createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource(this.databaseConnection);
		this.connectionSource.setMaxConnectionAgeMillis(TimeUnit.MINUTES.toMillis(5)); // Keep the connections open for 5 minutes
		this.connectionSource.setCheckConnectionsEveryMillis(TimeUnit.SECONDS.toMillis(60));
		this.connectionSource.setTestBeforeGet(
				true); // Enable the testing of connections right before they are handed to the user
	}

	protected void onCreate( final ConnectionSource databaseConnection ) {
		logger.info(">> [NeoComSupportDBAdapter.onCreate]");
		// Create the tables that do not exist
		try {
			TableUtils.createTableIfNotExists(databaseConnection, DatabaseVersion.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComSupportDBAdapter.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, TimeStamp.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComSupportDBAdapter.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, Credential.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComSupportDBAdapter.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, MiningExtraction.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComSupportDBAdapter.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, Location.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComSupportDBAdapter.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		this.loadSeedData();
		logger.info("<< [NeoComSupportDBAdapter.onCreate]");
	}

	protected void loadSeedData() {}

	// - B U I L D E R
	public static class Builder {
		private NeoComSupportDBAdapter onConstruction;

		public Builder() {
			this.onConstruction = new NeoComSupportDBAdapter();
		}

		public Builder withDatabaseConnection( final String databaseConnection ) {
			this.onConstruction.databaseConnection = databaseConnection;
			return this;
		}

		public NeoComSupportDBAdapter build() {
			if (!this.onConstruction.databaseValid())
				throw new NeoComRuntimeException("NeoCom database is not valid nor ready to accept connections.");
			return this.onConstruction;
		}
	}
}
