//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionalities than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.apache.commons.lang3.StringUtils;
import org.dimensinfin.eveonline.neocom.database.entity.*;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.DatabaseVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adam on 19/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComDBHelper implements INeoComDBHelper {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(NeoComDBHelper.class);
	private static String connectionDescriptor = "jdbc:mysql://localhost:3306/neocom?user=NEOCOMTEST&password=01.Alpha";

	// - F I E L D - S E C T I O N ............................................................................
	private String hostName = "";
	private String databaseName = "";
	private String databaseUser = "";
	private String databasePassword = "";
	private int databaseCurrentVersion = 0;
	private boolean databaseValid = false;
	private JdbcPooledConnectionSource connectionSource = null;

	private Dao<DatabaseVersion, String> versionDao = null;
	private Dao<ApiKey, String> apiKeysDao = null;
	private Dao<Credential, String> credentialDao = null;
	private Dao<org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage, String> colonyStorageDao = null;

	private DatabaseVersion storedVersion = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComDBHelper () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getDatabaseVersion () {
		return databaseCurrentVersion;
	}

	public ConnectionSource getConnectionSource () throws SQLException {
		if ( null == connectionSource ) createConnectionSource();
		return connectionSource;
	}

	public NeoComDBHelper setDatabaseHost (final String hostName) {
		this.hostName = hostName;
		return this;
	}

	public NeoComDBHelper setDatabaseName (final String instanceName) {
		this.databaseName = instanceName;
		return this;
	}

	public NeoComDBHelper setDatabaseUser (final String user) {
		this.databaseUser = user;
		return this;
	}

	public NeoComDBHelper setDatabasePassword (final String password) {
		this.databasePassword = password;
		return this;
	}

	public NeoComDBHelper setDatabaseVersion (final int newVersion) {
		this.databaseCurrentVersion = newVersion;
		return this;
	}

	public NeoComDBHelper build () throws SQLException {
		if ( StringUtils.isEmpty(hostName) )
			throw new SQLException("Cannot create connection: 'hostName' is empty.");
		if ( StringUtils.isEmpty(databaseName) )
			throw new SQLException("Cannot create connection: 'databaseName' is empty.");
		if ( StringUtils.isEmpty(databaseUser) )
			throw new SQLException("Cannot create connection: 'databaseUser' is empty.");
		if ( StringUtils.isEmpty(databasePassword) )
			throw new SQLException("Cannot create connection: 'databasePassword' is empty.");
		databaseValid = true;
		createConnectionSource();
		return this;
	}

	public int getStoredVersion (){
		if ( null == storedVersion ) {
			// Access the version object persistent on the database.
			try {
				//			Dao<DatabaseVersion, String> versionDao = this.getVersionDao();
				QueryBuilder<DatabaseVersion, String> queryBuilder = getVersionDao().queryBuilder();
				PreparedQuery<DatabaseVersion> preparedQuery = queryBuilder.prepare();
				List<DatabaseVersion> versionList = versionDao.query(preparedQuery);
				if ( versionList.size() > 0 ) {
					storedVersion = versionList.get(0);
					return storedVersion.getVersionNumber();
				} else
					return 0;
			} catch (SQLException sqle) {
				logger.warn("W- [NeocomDBHelper.getStoredVersion]> Database exception: " + sqle.getMessage());
				return 0;
			} catch (RuntimeException rtex) {
				logger.warn("W- [NeocomDBHelper.getStoredVersion]> Database exception: " + rtex.getMessage());
				return 0;
			}
		} else return storedVersion.getVersionNumber();
	}

	public void onCreate (final ConnectionSource databaseConnection) {
		logger.info(">> [NeoComDBHelper.onCreate]");
		// Create the tables that do not exist
		try {
			TableUtils.createTableIfNotExists(databaseConnection, DatabaseVersion.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComDBHelper.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, TimeStamp.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComDBHelper.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, ApiKey.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComDBHelper.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, Credential.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComDBHelper.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		try {
			TableUtils.createTableIfNotExists(databaseConnection, org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage.class);
		} catch (SQLException sqle) {
			logger.warn("SQL [NeoComDBHelper.onCreate]> SQL NeoComDatabase: {}", sqle.getMessage());
		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, Property.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, ResourceList.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, PlanetaryResource.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, NeoComAsset.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, NeoComBlueprint.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, Job.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, NeoComMarketOrder.class);
		//		} catch (SQLException sqle) {
		//		}
		//		try {
		//			TableUtils.createTableIfNotExists(databaseConnection, EveLocation.class);
		//		} catch (SQLException sqle) {
		//		}
		logger.info("<< [NeoComDBHelper.onCreate]");
	}

	public void onUpgrade (final ConnectionSource databaseConnection, final int oldVersion, final int newVersion) {
		// Execute different actions depending on the new version.
		if ( oldVersion < 1 ) {
			try {
				// Create the version table and insert the initial new version code.
				TableUtils.createTableIfNotExists(databaseConnection, DatabaseVersion.class);
				DatabaseVersion version = new DatabaseVersion(newVersion);
				// Persist the version object to the database
				getVersionDao().create(version);
			} catch (RuntimeException rtex) {
				logger.error("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.error("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
	}

	@Override
	public Dao<DatabaseVersion, String> getVersionDao () throws SQLException {
		if ( null == versionDao ) {
			versionDao = DaoManager.createDao(this.getConnectionSource(), DatabaseVersion.class);
		}
		return versionDao;
	}

	@Override
	public Dao<ApiKey, String> getApiKeysDao () throws SQLException {
		if ( null == apiKeysDao ) {
			apiKeysDao = DaoManager.createDao(this.getConnectionSource(), ApiKey.class);
		}
		return apiKeysDao;
	}

	@Override
	public Dao<Credential, String> getCredentialDao () throws SQLException {
		if ( null == credentialDao ) {
			credentialDao = DaoManager.createDao(this.getConnectionSource(), Credential.class);
		}
		return credentialDao;
	}

	@Override
	public Dao<org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage, String> getColonyStorageDao () throws SQLException {
		if ( null == colonyStorageDao ) {
			colonyStorageDao = DaoManager.createDao(this.getConnectionSource(), org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage.class);
		}
		return colonyStorageDao;
	}

	private void createConnectionSource () throws SQLException {
		//		try {
		final String localConnectionDescriptor = hostName + "/" + databaseName + "?user=" + databaseUser + "&password=" + databasePassword;
		if ( databaseValid ) connectionSource = new JdbcPooledConnectionSource(localConnectionDescriptor);
		else connectionSource = new JdbcPooledConnectionSource(connectionDescriptor);
		// only keep the connections open for 5 minutes
		connectionSource.setMaxConnectionAgeMillis(TimeUnit.MINUTES.toMillis(5));
		// change the check-every milliseconds from 30 seconds to 60
		connectionSource.setCheckConnectionsEveryMillis(TimeUnit.SECONDS.toMillis(60));
		// for extra protection, enable the testing of connections
		// right before they are handed to the user
		connectionSource.setTestBeforeGet(true);
		//		} catch (SQLException sqle) {
		//			sqle.printStackTrace();
		//		}
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("NeoComDBHelper [");
		final String localConnectionDescriptor = hostName + "/" + databaseName + "?user=" + databaseUser + "&password=" + databasePassword;
		buffer.append("Descriptor: ").append(localConnectionDescriptor);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
