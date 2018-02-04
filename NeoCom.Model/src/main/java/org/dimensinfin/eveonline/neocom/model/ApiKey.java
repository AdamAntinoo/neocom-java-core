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
package org.dimensinfin.eveonline.neocom.model;

import com.beimin.eveapi.model.account.ApiKeyInfo;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class will wrap the XML api Key definition. Instead using the old ApiKey and the new NeoComApiKey I will
 * create an expansion of the old just to survive until the final eliminatin of the xml api.
 * This instance initially contains the core data to be stored at the database. During the initialization it will be
 * expanded with the xml api data.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "ApiKey")
public class ApiKey {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ApiKey");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	private final long id = -2;
	@DatabaseField
	private String login = "Default";
	@DatabaseField
	private int keynumber = -1;
	@DatabaseField
	private String validationcode = "";
	@DatabaseField
	private boolean active = true;

	private transient ApiAuthorization authorization = null;
	private transient ApiKeyInfo delegatedApiKey = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ApiKey () {
	}

	public ApiKey (final String login) {
		this.login = login;
		try {
			Dao<ApiKey, String> apikeyDao = GlobalDataManager.getNeocomDBHelper().getApiKeysDao();
			// Try to create the key. It fails then  it was already created.
			apikeyDao.create(this);
		} catch (final SQLException sqle) {
			ApiKey.logger.info("WR [ApiKey.<constructor>]> ApiKey exists. Update values.");
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getKeynumber () {
		return keynumber;
	}

	public String getLogin () {
		return login;
	}

	public String getValidationcode () {
		return validationcode;
	}

	public boolean isActive () {
		return active;
	}

	public ApiKey setActive (final boolean activeState) {
		active = activeState;
		return this;
	}
	public ApiKey setKeynumber (final int keynumber) {
		this.keynumber = keynumber;
		return this;
	}

	public ApiKey setValidationcode (final String validationcode) {
		this.validationcode = validationcode;
		return this;
	}

	public ApiKey setAuthorization (final ApiAuthorization authorization) {
		this.authorization = authorization;
		return this;
	}

	public ApiKey setDelegated (final ApiKeyInfo delegatedApiKey) {
		this.delegatedApiKey = delegatedApiKey;
		return this;
	}

	public Collection<Character> getEveCharacters () {
		final Collection<Character> charList = delegatedApiKey.getEveCharacters();
		if(null==charList)return new ArrayList<>();
		else return charList;
	}

	public ApiKey store () {
		try {
			Dao<ApiKey, String> apikeyDao = GlobalDataManager.getNeocomDBHelper().getApiKeysDao();
			apikeyDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}
	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ApiKey [");
		buffer.append(login).append("/");
		buffer.append(keynumber).append("-").append(validationcode);
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
