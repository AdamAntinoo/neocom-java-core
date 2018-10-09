//  PROJECT:      NeoCom.Model (NEOC.M)
//  AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:    (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT:  Java 1.8 Library.
//  DESCRIPTION:  Java library for the NeoCom project that contains the model classes and all the
//                data management code to maintain the different model structures and functionalities.
//                The module integrates all data conversion and functionalities that can be
//                used on any platform not being dependant on Android development.
//                New functionalities allow the access of Eve Online CCP data with the new
//                developer ESI api and keeps the transformations and the code for the persistence
//                of the downloaded data on an external database.
//                The code isolates from the external database implementation to the extent to keep
//                the code compatible with Android and SpringBoot.
package org.dimensinfin.eveonline.neocom.entities;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.datahub.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................
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
	/**
	 * Future expiration Instant time in millisecons. Tis field is not required to be stored because the library
	 * will take care of the refresh token expiration times.
	 */
	@DatabaseField
	public long expires = 0;
	//	@DatabaseField
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String refreshToken = "-TOKEN-";

	// Additional XML api data.
	@DatabaseField
	private int keycode = -1;
	@DatabaseField
	private String validationcode = "";

	@DatabaseField
	private boolean active = true;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected Credential () {
		super();
		jsonClass = "Credential";
	}

	public Credential (final int newAccountIdentifier) {
		this();
		accountId = newAccountIdentifier;
		try {
			final Dao<Credential, String> credentialDao = GlobalDataManager.getNeocomDBHelper().getCredentialDao();
			// Try to create the key. It fails then  it was already created.
			credentialDao.create(this);
		} catch (final SQLException sqle) {
			Credential.logger.info("WR [Credential.<constructor>]> Credential exists. Update values.");
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Update the values at the database record.
	 */
	public Credential store () {
		try {
			final Dao<Credential, String> credentialDao = GlobalDataManager.getNeocomDBHelper().getCredentialDao();
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
	public List<TimeStamp> needsUpdate () {
		// Check for character data to be updated. There will be different levels but now only V1 is implemented.
		List<TimeStamp> timesList = new ArrayList();
		try {
			// Get all the timeStamps for this credential.
			final Dao<TimeStamp, String> timeStampDao = GlobalDataManager.getNeocomDBHelper().getTimeStampDao();
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
	public int getAccountId () {
		return accountId;
	}

	public String getAccountName () {
		return accountName;
	}

	public String getName () {
		return accountName;
	}

	public String getAccessToken () {
		return accessToken;
	}

	public String getTokenType () {
		return tokenType;
	}

	public long getExpires () {
		return expires;
	}

	public String getRefreshToken () {
		return refreshToken;
	}

	public int getKeyCode () {
		return keycode;
	}

	public String getValidationCode () {
		return validationcode;
	}

	private Credential setAccountId (final int accountId) {
		this.accountId = accountId;
		return this;
	}

	public Credential setAccountName (final String accountName) {
		this.accountName = accountName;
		return this;
	}

	public Credential setAccessToken (final String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public Credential setTokenType (final String tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	public Credential setExpires (final long expires) {
		this.expires = expires;
		return this;
	}

	public Credential setRefreshToken (final String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public Credential setKeyCode (final int keycode) {
		this.keycode = keycode;
		return this;
	}

	public Credential setValidationCode (final String validationcode) {
		this.validationcode = validationcode;
		return this;
	}

	public Credential setActive (final boolean active) {
		this.active = active;
		return this;
	}

	public boolean isActive () {
		return active;
	}

	public boolean isXMLCompatible () {
		if ( keycode < 1 ) return false;
		if ( validationcode.isEmpty() ) return false;
		return true;
	}

	public boolean isESICompatible () {
		if ( accountId < 1 ) return false;
		if ( accessToken.isEmpty() ) return false;
		if ( refreshToken.isEmpty() ) return false;
		return true;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("Credential [");
		buffer.append("[").append(getAccountId()).append("] ");
		buffer.append(" ").append(getAccountName()).append(" ");
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
