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
package org.dimensinfin.eveonline.neocom.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOkHomeLocation.LocationTypeEnum;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore.CorePilot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Credential")
public class Credential extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("Credential");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	private final long id = -2;
	@DatabaseField
	private long accountId = -2;
	@DatabaseField
	private String accountName = "-NAME-";
	@DatabaseField
	public String accessToken;
	@DatabaseField
	public String tokenType;
	/** Future expitation Instant time in millisecons */
	@DatabaseField
	public long expires;
	@DatabaseField
	private String refreshToken = "-TOKEN-";

	@DatabaseField
	private int keycode = -1;
	@DatabaseField
	private String validationcode = "";
//	@DatabaseField
//	private String authorizationMask = "";
	@DatabaseField
	private boolean active = true;

	private transient CorePilot pilot=null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected Credential () {
		super();
		jsonClass="Credential";
	}
	public Credential (final long newAccountIdentifier) {
		this();
		accountId = newAccountIdentifier;
		try {
			final Dao<Credential, String> credentialDao = ModelAppConnector.getSingleton().getDBConnector().getCredentialDao();
			// Try to create the key. It fails then  it was already created.
			credentialDao.create(this);
		} catch (final SQLException sqle) {
			Credential.logger.info("WR [Credential.<constructor>]> Credential exists. Update values.");
			this.setDirty(true);
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void setDirty (final boolean state) {
		if ( state ) {
			try {
				final Dao<Credential, String> credentialDao = ModelAppConnector.getSingleton().getDBConnector().getCredentialDao();
				credentialDao.update(this);
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public long getAccountId () {
		return accountId;
	}

	public Credential setAccountId (final long accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getAccountName () {
		return accountName;
	}

	public String getName () {
		return accountName;
	}

	public Credential setAccountName (final String accountName) {
		this.accountName = accountName;
		return this;
	}

	public String getAccessToken () {
		return accessToken;
	}

	public Credential setAccessToken (final String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getTokenType () {
		return tokenType;
	}

	public Credential setTokenType (final String tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	public long getExpires () {
		return expires;
	}

	public Credential setExpires (final long expires) {
		this.expires = expires;
		return this;
	}
	public String getRefreshToken () {
		return refreshToken;
	}
	public Credential setRefreshToken (final String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public int getKeyCode () {
		return keycode;
	}

	public Credential setKeyCode (final int keycode) {
		this.keycode = keycode;
		return this;
	}

	public String getValidationCode () {
		return validationcode;
	}

	public Credential setValidationCode (final String validationcode) {
		this.validationcode = validationcode;
		return this;
	}

	public boolean isActive () {
		return active;
	}

	public Credential setActive (final boolean active) {
		this.active = active;
		return this;
	}

	public void setCharacterCoreData (final CorePilot pilot) {
		this.pilot=pilot;
	}

	public long getLocationId () {
		return pilot.getLocationId();
	}

	public LocationTypeEnum getLocationType () {
		return pilot.getLocationType();
	}

	public EveLocation getLocation () {
		return pilot.getLocation();
	}

	public String getURLForAvatar () {
		return pilot.getURLForAvatar();
	}

	public boolean checkPilotDownload(){
		if(null==pilot)return false;
		return true;
	}
	/**
	 * Update the values at the database record.
	 */
	public Credential store () {
		setDirty(true);
		return this;
	}
}

// - UNUSED CODE ............................................................................................
