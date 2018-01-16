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
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore.CorePilot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Credential")
public class Credential extends NeoComNode {
//	public enum ECredentialType {
//		EMPTY, ESI, XML, ESIXML
//	}

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
	public String accessToken="";
	@DatabaseField
	public String tokenType="";
	/** Future expiration Instant time in millisecons */
	@DatabaseField
	public long expires=0;
	@DatabaseField
	private String refreshToken = "-TOKEN-";

	// Additional XML api data.
	@DatabaseField
	private int keycode = -1;
	@DatabaseField
	private String validationcode = "";

	@DatabaseField
	private boolean active = true;

//	private ECredentialType ctype = ECredentialType.EMPTY;
	private transient CorePilot pilot = null;
	private transient NeoComCharacter character = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected Credential () {
		super();
		jsonClass = "Credential";
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
	/**
	 * Update the values at the database record.
	 */
	public Credential store () {
		setDirty(true);
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public long getAccountId () {
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

	public Credential setAccountId (final long accountId) {
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

//	public ECredentialType getCredentialType () {
//		if ( keycode > 0 ) ctype = ECredentialType.XML;
//		if ( !refreshToken.equalsIgnoreCase("-TOKEN-") )
//			if ( ECredentialType.XML == ctype ) ctype = ECredentialType.ESIXML;
//			else ctype = ECredentialType.ESI;
//		return ctype;
//	}

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
	public Credential setCharacterCoreData (final CorePilot pilot) {
		this.pilot = pilot;
		return this;
	}
	public Credential setCharacterXML (final NeoComCharacter character) {
		this.character = character;
		return this;
	}
	public boolean isActive () {
		return active;
	}

	public boolean isXMLCompatible(){
		if( keycode<1)return false;
		if(validationcode.isEmpty())return false;
		return true;
	}
	public boolean isESICompatible(){
		if( accountId<1)return false;
		if(accessToken.isEmpty())return false;
		if(refreshToken.isEmpty())return false;
		return true;
	}
	public void addPlanetaryData (final List<GetCharactersCharacterIdPlanets200Ok> data) {
		if(null!=pilot)pilot.setPlanetaryData(data);
	}
	public boolean checkPilotDownload () {
		if ( null == pilot ) return false;
		return true;
	}

	// --- D E L E G A T E D   M E T H O D S
	public long getLocationId () {
		return pilot.getLocationId();
	}

	public LocationTypeEnum getLocationType () {
		return pilot.getLocationType();
	}

	public EveLocation getLocation () {
		return pilot.getLocation();
	}

//	public String getURLForAvatar () {
//		return pilot.getURLForAvatar();
//	}

	public double getAccountBalance () {
		return character.getAccountBalance();
	}

	public Date getApiKeyExpiration () {
		return character.getApiKeyExpiration();
	}

	public Date getApiKeyPaidUntil () {
		return character.getApiKeyPaidUntil();
	}
	public NeoComCharacter getMasterCharacter(){
		return character;
	}
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
