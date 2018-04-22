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
package org.dimensinfin.eveonline.neocom.database.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * Credentials are the block of data that stores the new authorization data for the ESI access to Eve Online data servers. The
 * change form CREST/XML has completed and I should not depend on any more ata from that interfaces.
 * <p>
 * Credentials are generated
 * by the authorization flow through the CCP servers and connected to the Developer Application that utterly authorizes point
 * access. There are Credentials for the Tranquility production server and for the Singularity testing service that are
 * completely independent.
 * <p>
 * This data should not be exported nor has mre validity than to allow back ends to retrieve data on behalf for the user on a
 * perodic basis to speed up data access.
 * <p>
 * There are two variants for using the Credentials. On on single user applications like the one developed for Android this
 * data allows to get repeated access without the need to repeat the login process. ON single user repositories there is no
 * problem sharing the information on different sessions. ONn contrary on the multi user applications like Infinity there
 * should be an isolation level and the Credential should only be used to update cache data.
 *
 * @author Adam Antinoo
 */
@DatabaseTable(tableName = "Credential")
public class Credential extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -4248173464157148843L;
	private static Logger logger = LoggerFactory.getLogger("Credential");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	private int accountId = -2;
	@DatabaseField
	private String accountName = "-NAME-";
	@DatabaseField
	public String accessToken = "";
	@DatabaseField
	public String tokenType = "";
	@DatabaseField
	public String dataSource = "tranquility";
	@DatabaseField (dataType =DataType.LONG_STRING)
	public String scope = "publicData";
	/**
	 * Future expiration Instant time in milliseconds. This field is not required to be stored because the library
	 * will take care of the refresh token expiration times.
	 */
	@DatabaseField
	public long expires = 0;
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String refreshToken = "-TOKEN-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected Credential() {
		super();
		jsonClass = "Credential";
	}

	public Credential( final int newAccountIdentifier ) {
		this();
		accountId = newAccountIdentifier;
		try {
			final Dao<Credential, String> credentialDao = accessGlobal().getNeocomDBHelper().getCredentialDao();
			// Try to create the key. It fails then  it was already created.
			credentialDao.create(this);
		} catch (final SQLException sqle) {
			Credential.logger.warn("WR [Credential.<constructor>]> Credential exists. Update values.");
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Update the values at the database record.
	 */
	public Credential store() {
		try {
			final Dao<Credential, String> credentialDao = accessGlobal().getNeocomDBHelper().getCredentialDao();
			credentialDao.update(this);
			Credential.logger.info("-- [Credential.store]> Credential data updated successfully.");
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			}
		return this;
	}

	/**
	 * Check all the cache time stamps for existence and stored at the database.
	 * TS are stored at the database and updated any time some data is downloaded and updated with the cached
	 * time reported by CCP.
	 * Just returns the list of TS leaving the calculation to the caller to take the decision to launch an update.
	 */
	public List<TimeStamp> needsUpdate() {
		// Check for character data to be updated. There will be different levels but now only V1 is implemented.
		List<TimeStamp> timesList = new ArrayList();
		try {
			// Get all the timeStamps for this credential.
			final Dao<TimeStamp, String> timeStampDao = accessGlobal().getNeocomDBHelper().getTimeStampDao();
			QueryBuilder<TimeStamp, String> queryBuilder = timeStampDao.queryBuilder();
			Where<TimeStamp, String> where = queryBuilder.where();
			where.eq("credentialId", getAccountId());
			PreparedQuery<TimeStamp> preparedQuery = queryBuilder.prepare();
			timesList = timeStampDao.query(preparedQuery);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return timesList;
	}

	// --- G E T T E R S   &   S E T T E R S
	public int getAccountId() {
		return accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getName() {
		return accountName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpires() {
		return expires;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getDataSource() {
		return dataSource;
	}

	private Credential setAccountId( final int accountId ) {
		this.accountId = accountId;
		return this;
	}

	public Credential setAccountName( final String accountName ) {
		this.accountName = accountName;
		return this;
	}

	public Credential setAccessToken( final String accessToken ) {
		this.accessToken = accessToken;
		return this;
	}

	public Credential setTokenType( final String tokenType ) {
		this.tokenType = tokenType;
		return this;
	}

	public Credential setDataSource( final String dataSource ) {
		this.dataSource = dataSource;
		return this;
	}

	public Credential setExpires( final long expires ) {
		this.expires = expires;
		return this;
	}

	public Credential setRefreshToken( final String refreshToken ) {
		this.refreshToken = refreshToken;
		return this;
	}

	public Credential setScope( final String scope ) {
		this.scope = scope;
		return this;
	}

	@Deprecated
	public boolean isESICompatible() {
		if (accountId < 1) return false;
		if (accessToken.isEmpty()) return false;
		if (refreshToken.isEmpty()) return false;
		return true;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Credential [");
		buffer.append("[").append(getAccountId()).append("] ");
		buffer.append(" ").append(getAccountName()).append(" ");
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
