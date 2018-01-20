//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class should encapsulate the functionality to run database queries to the internal NeoCom database.
 * Because the implementation on Android is quite specific even using isolation libraries I should create a new
 * isolation layer that will connect on execution time to the right platform implementor.
 * <p>
 * There are two implementations, one for Android and another for SpringBoot (that also uses another database
 * engine) that have two layers of differentiation, one at the DBHelper that is an artifact specific for Android
 * that should be replicated and the other at the ORMLite library to match compatibility between the core
 * and the android variants that have minos differences at the cursor/result set interface.
 * <p>
 * This class has a runtime placeholder for the real Helper implementor that is connected by the application
 * during initialization. So all variant calls should end on the Heper that at runtime will have the code
 * to run that functionality on the right database.
 * The core for the Helper at least is the list of Dao for the exported entities tables.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComDatabase {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(NeoComDatabase.class);

	private static final NeoComDatabase singleton = new NeoComDatabase();
	private static INeoComDBHelper implementer = null;
	private static int _accessCount = 0;

	public static INeoComDBHelper getImplementer () {
		// TODO During the time the old and new implementations share the code make the implementer the one at the Connector.
		if ( null == implementer ) try {
			implementer = ModelAppConnector.getSingleton().getNewDBConnector();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( null == implementer )
			throw new RuntimeException("[NeoComDatabase]> implementer not defined. No access to platform library to get database results.");
//		_accessCount++;
		return implementer;
	}
	public static void setImplementer (final INeoComDBHelper  newImplementer) {
		if(null!=newImplementer)implementer=newImplementer;
	}

	/**
	 * Tries to open and create all pending tables. Also checks database version and performs any upgrades to the
	 * schema depending on differences at version numbers.
	 * If there is any problem it will throw the exception.
	 */
	public static void openDatabase()throws SQLException{
		// Get the current database stored version.
		final int databaseVersion = implementer.getStoredVersion();
		// Get the current configured version.
		final int currentVersion = implementer.getDatabaseVersion();
		// Do any upgrade if the versions do not match.
		if(databaseVersion!=currentVersion)
			implementer.onUpgrade(implementer.getConnectionSource(),databaseVersion,currentVersion);
		// Create any missing table is the schema has changed since last update.
		implementer.onCreate(implementer.getConnectionSource());
	}

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
	@Deprecated
	public static List<ApiKey> accessAllLogins () {
		_accessCount++;
		return singleton.accessAllLoginsMethod();
	}

	public static List<Credential> accessAllCredentials () {
		_accessCount++;
		return singleton.accessAllCredentialsMethod();
	}

	// - F I E L D - S E C T I O N ............................................................................
	private Dao<Credential, String> _credentialDao = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComDatabase () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Reads all the keys stored at the database and classifies them into a set of Login names.
	 */
	@Deprecated
	private List<ApiKey> accessAllLoginsMethod () {
		logger.info(">> [NeoComDatabase.accessAllLogins]");
		// Get access to all ApiKey registers
		List<ApiKey> keyList = new Vector<ApiKey>();
		try {
			Dao<ApiKey, String> keysDao = getImplementer().getApiKeysDao();
			QueryBuilder<ApiKey, String> queryBuilder = keysDao.queryBuilder();
			PreparedQuery<ApiKey> preparedQuery = queryBuilder.prepare();
			keyList = keysDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [NeoComDatabase.accessAllLogins]> Exception reading all Logins. " + sqle.getMessage());
		} finally {
			logger.info("<< [NeoComDatabase.accessAllLogins]");
		}
		//		// Classify the keys on they matching Logins.
		//		Hashtable<String, Login> loginList = new Hashtable<String, Login>();
		//		for (ApiKey apiKey : keyList) {
		//			String name = apiKey.getLogin();
		//			// Search for this on the list before creating a new Login.
		//			Login hit = loginList.get(name);
		//			if ( null == hit ) {
		//				Login login = new Login(name).addKey(apiKey);
		//				loginList.put(name, login);
		//			} else {
		//				hit.addKey(apiKey);
		//			}
		//		}
		//		return loginList;
		return keyList;
	}

	/**
	 * Reads all the list of credentials stored at the Database and returns its list.
	 */
	private List<Credential> accessAllCredentialsMethod () {
		List<Credential> credentialList = new ArrayList<>();
		try {
			final PreparedQuery<Credential> preparedQuery = credentialDao().queryBuilder().prepare();
			credentialList = credentialDao().query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [NeoComDatabase.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;
	}

	private Dao<Credential, String> credentialDao () throws SQLException {
		if ( null == _credentialDao ) _credentialDao = getImplementer().getCredentialDao();
		return _credentialDao;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("NeoComDatabase [");
		buffer.append("AccessCount: ").append(_accessCount);
		buffer.append("]");
		//	buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
