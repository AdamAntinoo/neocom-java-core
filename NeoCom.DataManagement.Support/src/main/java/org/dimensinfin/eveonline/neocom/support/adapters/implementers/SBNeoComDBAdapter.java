package org.dimensinfin.eveonline.neocom.support.adapters.implementers;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.DatabaseVersion;

/**
 * NeoCom private database connector that will have the same api as the connector to be used on Android. This
 * version already uses the mySql database JDBC implementation instead the SQLite copied from the Android
 * platform.
 * The class will encapsulate all dao and connection access.
 *
 * @author Adam Antinoo
 */
public class SBNeoComDBAdapter /*implements INeoComDBHelper*/ {
	protected static Logger logger = LoggerFactory.getLogger( SBNeoComDBAdapter.class );

	private String databaseHostName = "";
	private String databaseType = "postgres";
	private String databasePath;
	private String databaseUser;
	private String databasePassword;
	private String databaseOptions = "&verifyServerCertificate=false&useSSL=true";
	protected String localConnectionDescriptor;
	private int databaseVersion = 0;
	private boolean databaseValid = false;
	private boolean isOpen = false;
	private JdbcPooledConnectionSource connectionSource;
	private DatabaseVersion storedVersion;

	private Dao<DatabaseVersion, String> versionDao = null;
	private Dao<Credential, String> credentialDao = null;

	// - C O N S T R U C T O R S
	protected SBNeoComDBAdapter() {
	}

	// - D A O - A C C E S S
	public ConnectionSource getConnectionSource() throws SQLException {
		if (null == this.connectionSource) this.openNeoComDB();
		return this.connectionSource;
	}

	//	@Override
	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException {
		if (null == this.versionDao) {
			this.versionDao = DaoManager.createDao( this.getConnectionSource(), DatabaseVersion.class );
		}
		return this.versionDao;
	}

	//	@Override
	public Dao<Credential, String> getCredentialDao() throws SQLException {
		if (null == this.credentialDao) {
			this.credentialDao = DaoManager.createDao( this.getConnectionSource(), Credential.class );
		}
		return this.credentialDao;
	}

	public boolean isDatabaseDescriptorValid() throws SQLException {
		if (null != this.localConnectionDescriptor) return true;
		if (StringUtils.isEmpty( this.databaseHostName ))
			throw new SQLException( "Cannot create connection: 'hostName' is empty." );
		if (StringUtils.isEmpty( this.databaseType ))
			throw new SQLException( "Cannot create connection: 'databaseType' is empty." );
		if (StringUtils.isEmpty( this.databasePath ))
			throw new SQLException( "Cannot create connection: 'databasePath' is empty." );
		if (StringUtils.isEmpty( this.databaseUser ))
			throw new SQLException( "Cannot create connection: 'databaseUser' is empty." );
		if (StringUtils.isEmpty( this.databasePassword ))
			throw new SQLException( "Cannot create connection: 'databasePassword' is empty." );
//		if ( null == this.databaseOptions) // Read the default configured options
		// Compose the descriptor.
		this.localConnectionDescriptor = this.databaseHostName + "/" + this.databasePath +
				"?user=" + this.databaseUser +
				"&password=" + this.databasePassword +
				this.databaseOptions;
		return true;
	}

	/**
	 * Open a new pooled JDBC datasource connection list and stores its reference for use of the whole set of
	 * services. Being a pooled connection it can create as many connections as required to do requests in
	 * parallel to the database instance. This only is effective for MySql databases.
	 */
	private boolean openNeoComDB() throws SQLException {
		logger.info( ">> [SBNeoComDBHelper.openNeoComDB]" );
		if (!this.isOpen) if (null == this.connectionSource) {
			this.isOpen = this.createConnectionSource();
			final String localConnectionDescriptor = this.databaseHostName + "/" + this.databasePath;
			logger.info( "-- [SBNeoComDBHelper.openNeoComDB]> Opened database {} successfully with version {}.",
					localConnectionDescriptor, this.databaseVersion );
		}
		logger.info( "<< [SBNeoComDBHelper.openNeoComDB]" );
		return isOpen;
	}

	/**
	 * Creates and configures the connection pool to access the application database.
	 */
	private boolean createConnectionSource() throws SQLException {
		this.isDatabaseDescriptorValid(); // Database should be valid and the descriptor ready.
		this.connectionSource = new JdbcPooledConnectionSource( this.localConnectionDescriptor );
		// Configure the new connection pool.
		connectionSource
				.setMaxConnectionAgeMillis(
						TimeUnit.MINUTES.toMillis( 5 ) ); // only keep the connections open for 5 minutes
		connectionSource
				.setCheckConnectionsEveryMillis(
						TimeUnit.SECONDS.toMillis( 60 ) ); // change the check-every milliseconds from 30 seconds to 60
		connectionSource.setTestBeforeGet( true );
		return true;
	}

	// - B U I L D E R
	public static class Builder {
		private SBNeoComDBAdapter onConstruction;

		public Builder() {
			this.onConstruction = new SBNeoComDBAdapter();
		}

		public Builder( final SBNeoComDBAdapter preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new SBNeoComDBAdapter();
		}

		public SBNeoComDBAdapter.Builder withDatabaseHostName( final String databaseHostName ) {
			this.onConstruction.databaseHostName = databaseHostName;
			return this;
		}

		public SBNeoComDBAdapter.Builder withDatabasePath( final String databasePath ) {
			this.onConstruction.databasePath = databasePath;
			return this;
		}

		public SBNeoComDBAdapter.Builder withDatabaseUser( final String databaseUser ) {
			this.onConstruction.databaseUser = databaseUser;
			return this;
		}

		public SBNeoComDBAdapter.Builder withDatabasePassword( final String databasePassword ) {
			this.onConstruction.databasePassword = databasePassword;
			return this;
		}

		public SBNeoComDBAdapter.Builder optionalDatabaseType( final String databaseType ) {
			this.onConstruction.databaseType = databaseType;
			return this;
		}

		public SBNeoComDBAdapter.Builder optionalDatabaseUrl( final String databaseUrl ) {
			if (null != databaseUrl) this.onConstruction.localConnectionDescriptor = databaseUrl;
			return this;
		}

		public SBNeoComDBAdapter.Builder optionalDatabaseOptions( final String databaseOptions ) {
			this.onConstruction.databaseOptions = databaseOptions;
			return this;
		}

		public SBNeoComDBAdapter build() throws SQLException {
			// Connect to the database instance.
			this.onConstruction.databaseValid = this.onConstruction.isDatabaseDescriptorValid();
			this.onConstruction.openNeoComDB(); // Open and connect the database.
			return this.onConstruction;
		}
	}
}
