package org.dimensinfin.eveonline.neocom.adapters;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.SBRawStatement;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * This is the adapter to read the data from the SDE database repository. This instance is configured as java SpringBoot
 * compatible SQLite database adapter. The SDE database is generated from the downloaded SDE raw data and configured for
 * a game version that should match the latest game client implementation.
 *
 * This adater will support custom queries to the SDE tables along with addition DAO for additional persistence tables.
 */
public class SDEDatabaseAdapter implements ISDEDatabaseAdapter {
	private static Logger logger = LoggerFactory.getLogger(SDEDatabaseAdapter.class);
	private IFileSystem fileSystemAdapter;

	private String schema = "jdbc:sqlite";
	private String databasePath;
	private String databaseName;
	private int databaseVersion = 0;
	private Connection ccpDatabaseConnection;
	private JdbcPooledConnectionSource connectionSource = null;

	private Dao<EsiLocation, Long> locationDao = null;

	private SDEDatabaseAdapter() {
		super();
	}

	public Dao<EsiLocation, Long> getLocationDao() throws SQLException {
		if (null == this.locationDao)
			this.locationDao = DaoManager.createDao(this.getConnectionSource(), EsiLocation.class);
		return this.locationDao;
	}

	private String getConnectionDescriptor() {
		return this.schema + ":" + this.databasePath + this.databaseName;
	}

	private Connection getSDEDatabase() {
		if (null == this.ccpDatabaseConnection) this.openSDEDB();
		return this.ccpDatabaseConnection;
	}

	private boolean isDatabaseDefinitionValid() {
		return ((null != this.databasePath) && (null != this.databaseName));
	}

	private ConnectionSource getConnectionSource() {
		if (null == this.connectionSource) this.openSDEDB();
		return this.connectionSource;
	}

	/**
	 * Open a new pooled JDBC data source connection list and stores its reference for use of the whole set of
	 * services. Being a pooled connection it can create as many connections as required to do requests in
	 * parallel to the database instance. This only is effective for MySql databases.
	 */
	private boolean openSDEDB() {
		logger.info(">> [SDEDatabaseAdapter.openSDEDB]");
		if ((null == this.ccpDatabaseConnection) && (this.isDatabaseDefinitionValid())) {
			try {
				this.ccpDatabaseConnection = DriverManager.getConnection(this.getConnectionDescriptor());
				this.ccpDatabaseConnection.setAutoCommit(false);
				Objects.requireNonNull(this.ccpDatabaseConnection);
				logger.info("-- [SDEDatabaseAdapter.openSDEDB]> Opened database {} successfully with version {}."
						, this.getConnectionDescriptor(), this.databaseVersion);
				this.createConnectionSource();
			} catch (Exception sqle) {
				logger.error("E> [SDEDatabaseAdapter.openSDEDB]> " + sqle.getClass().getName() + ": " + sqle.getMessage());
				return false;
			}
		}
		this.onCreate(this.connectionSource);
		logger.info("<< [SDEDatabaseAdapter.openSDEDB]");
		return true;
	}

	private void onCreate( final ConnectionSource databaseConnection ) {
		logger.info(">> [SDEDatabaseAdapter.onCreate]");
		// Create the tables that do not exist
		try {
			TableUtils.createTableIfNotExists(databaseConnection, EsiLocation.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [SDEDatabaseAdapter.onCreate]> SQL SDEDatabase: {}", sqle.getMessage());
		}
		logger.info("<< [SDEDatabaseAdapter.onCreate]");
	}

	private void createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource(this.getConnectionDescriptor());
		this.connectionSource.setMaxConnectionAgeMillis(TimeUnit.MINUTES.toMillis(5)); // Keep the connections open for 5 minutes
		this.connectionSource.setCheckConnectionsEveryMillis(TimeUnit.SECONDS.toMillis(60));
		this.connectionSource.setTestBeforeGet(
				true); // Enable the testing of connections right before they are handed to the user
	}

	// - I S D E D A T A B A S E A D A P T E R

	/**
	 * This is the specific SpringBoot implementation for the SDE database adaptation. We can create compatible
	 * <code>RawStatements</code> that can isolate the generic database access code from the platform specific. This
	 * statement uses the database connection to create a generic JDBC Java statement.
	 */
	public SBRawStatement constructStatement( final String query, final String[] parameters ) throws SQLException {
		return new SBRawStatement(this.getSDEDatabase(), query, parameters);
	}

	// - B U I L D E R
	public static class Builder {
		private SDEDatabaseAdapter onConstruction;

		public Builder() {
			this.onConstruction = new SDEDatabaseAdapter();
		}

		public Builder withDatabasePath( final String databasePath ) {
			this.onConstruction.databasePath = databasePath;
			return this;
		}

		public Builder withDatabaseName( final String databaseName ) {
			this.onConstruction.databaseName = databaseName;
			return this;
		}

		public Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public SDEDatabaseAdapter build() {
			Objects.requireNonNull(this.onConstruction.databasePath);
			Objects.requireNonNull(this.onConstruction.databaseName);
			Objects.requireNonNull(this.onConstruction.fileSystemAdapter);
			return this.onConstruction;
		}
	}
}