package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.SBRawStatement;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SDEDatabaseAdapter implements ISDEDatabaseAdapter {
	private static Logger logger = LoggerFactory.getLogger(SDEDatabaseAdapter.class);
	private IFileSystem fileSystemAdapter;

	private String schema = "jdbc:sqlite";
	private String databasePath;
	private String databaseName;
	private int databaseVersion = 0;
	private Connection ccpDatabaseConnection;

	private SDEDatabaseAdapter() {
		super();
	}

	protected Connection getSDEDatabase() {
		if (null == this.ccpDatabaseConnection) this.openSDEDB();
		return this.ccpDatabaseConnection;
	}

	public String getConnectionDescriptor() {
		return this.schema + ":" + this.databasePath + this.databaseName;
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

	protected boolean isDatabaseDefinitionValid() {
		return ((null != this.databasePath) && (null != this.databaseName));
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
			} catch (Exception sqle) {
				logger.error("E> [SDEDatabaseAdapter.openSDEDB]> " + sqle.getClass().getName() + ": " + sqle.getMessage());
				return false;
			}
		}
		logger.info("<< [SDEDatabaseAdapter.openSDEDB]");
		return true;
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

		public ISDEDatabaseAdapter build() {
			Objects.requireNonNull(this.onConstruction.databasePath);
			Objects.requireNonNull(this.onConstruction.databaseName);
			Objects.requireNonNull(this.onConstruction.fileSystemAdapter);
			return this.onConstruction;
		}
	}
}