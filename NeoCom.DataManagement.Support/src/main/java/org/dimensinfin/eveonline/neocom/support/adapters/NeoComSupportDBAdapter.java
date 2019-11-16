package org.dimensinfin.eveonline.neocom.support.adapters;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class NeoComSupportDBAdapter {
	private String databaseConnection;
	private JdbcPooledConnectionSource connectionSource = null;
	private Dao<Credential, String> credentialDao = null;

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

	protected ConnectionSource getConnectionSource() throws SQLException {
		if (null == connectionSource) this.createConnectionSource();
		return connectionSource;
	}

	/**
	 * Creates and configures the connection data source to the database.
	 */
	protected void createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource(this.databaseConnection);
		this.connectionSource.setMaxConnectionAgeMillis(TimeUnit.MINUTES.toMillis(5)); // Keep the connections open for 5 minutes
		this.connectionSource.setCheckConnectionsEveryMillis(TimeUnit.SECONDS.toMillis(60));
		this.connectionSource.setTestBeforeGet(true); // Enable the testing of connections right before they are handed to the user
	}

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
				throw new NeoComRuntimeException("NeoCom database is not valid not ready to accept connections.");
			return this.onConstruction;
		}
	}
}
