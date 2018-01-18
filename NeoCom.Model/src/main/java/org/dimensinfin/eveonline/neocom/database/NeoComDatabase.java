//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.dimensinfin.eveonline.neocom.connector.INeoComModelDatabase;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Adam Antinoo on 14/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This class should encapsulate the functionality to run database queries to the internal NeoCom database.
 * Because the implementation on Android is quite spedific even using isolation libraries I should create a new
 * isolation layer that will connect on execution time to the right platform implementor.
 * <p>
 * There are two implementations, one for Android and another for SpringBoot (that also used another database
 * engine) hat have two layers of differentiation, one at the DBHelper that is an artifact specific for Android
 * that should be replicated and the other at the ORMLite library to match compatibility between the core
 * and the android variants that have minos differences at the cursor/resultset interface.
 */
public class NeoComDatabase {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(NeoComDatabase.class);

	private static final NeoComDatabase singleton = new NeoComDatabase();
	private static INeoComModelDatabase implementer = null;
	private static int _accessCount = 0;

	public static INeoComModelDatabase getImplementer () {
		if(null==implementer)implementer=ModelAppConnector.getSingleton().getDBConnector();
		if ( null == implementer )
			throw new RuntimeException("[NeoComDatabase]> implementer not defined. No access to platform library to get database results.");
		_accessCount++;
		return implementer;
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
			Dao<ApiKey, String> keysDao = ModelAppConnector.getSingleton().getDBConnector().getApiKeysDao();
			QueryBuilder<ApiKey, String> queryBuilder = keysDao.queryBuilder();
			PreparedQuery<ApiKey> preparedQuery = queryBuilder.prepare();
			keyList = keysDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [NeoComDatabase.accessAllLogins]> Exception reading all Logins. " + sqle.getMessage());
		}finally {
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
			//			Dao<ApiKey, String> keysDao = ModelAppConnector.getSingleton().getDBConnector().getApiKeysDao();
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
