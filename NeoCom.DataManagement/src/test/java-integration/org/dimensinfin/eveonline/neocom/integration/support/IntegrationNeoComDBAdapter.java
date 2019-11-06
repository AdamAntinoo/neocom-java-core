package org.dimensinfin.eveonline.neocom.integration.support;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class IntegrationNeoComDBAdapter {
	private String localConnectionDescriptor;
	private boolean databaseValid = false;
	private boolean isOpen = false;
	private JdbcPooledConnectionSource connectionSource;

	private Dao<NeoAsset, UUID> assetDao = null;

	private IntegrationNeoComDBAdapter() {}

	// - D A O - A C C E S S
	public ConnectionSource getConnectionSource() throws SQLException {
		if (null == this.connectionSource) this.openNeoComDB();
		return this.connectionSource;
	}

	public Dao<NeoAsset, UUID> getAssetDao() throws SQLException {
		if (null == this.assetDao) {
			this.assetDao = DaoManager.createDao( this.getConnectionSource(), NeoAsset.class );
		}
		return this.assetDao;
	}

	private boolean openNeoComDB() throws SQLException {
		NeoComLogger.info( ">> [SBNeoComDBHelper.openNeoComDB]" );
		if (!this.isOpen) if (null == this.connectionSource) {
			this.isOpen = this.createConnectionSource();
		}
		NeoComLogger.info( "<< [SBNeoComDBHelper.openNeoComDB]" );
		return isOpen;
	}

	private boolean createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource( this.localConnectionDescriptor );
		// Configure the new connection pool.
		connectionSource.setMaxConnectionAgeMillis(
				TimeUnit.MINUTES.toMillis( 5 ) ); // Only keep the connections open for 5 minutes
		connectionSource.setCheckConnectionsEveryMillis(
				TimeUnit.SECONDS.toMillis( 60 ) ); // Change the check-every milliseconds from 30 seconds to 60
		connectionSource.setTestBeforeGet( true );
		return true;
	}

	// - B U I L D E R
	public static class Builder {
		private IntegrationNeoComDBAdapter onConstruction;

		public Builder() {
			this.onConstruction = new IntegrationNeoComDBAdapter();
		}

		public IntegrationNeoComDBAdapter.Builder withDatabaseURLConnection( final String databaseURLConnection ) {
			Objects.requireNonNull( databaseURLConnection );
			this.onConstruction.localConnectionDescriptor = databaseURLConnection;
			return this;
		}

		public IntegrationNeoComDBAdapter build() {
			Objects.requireNonNull( this.onConstruction.localConnectionDescriptor );
			return this.onConstruction;
		}
	}
}