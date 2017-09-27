//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//					environment limits.
//					Database and model adaptations for storage model independency.
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "ApiKey")
public class ApiKey {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger					= Logger.getLogger("ApiKey");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	private final long		id							= -2;
	@DatabaseField
	private String				login						= "Default";
	@DatabaseField
	private int						keynumber				= -1;
	@DatabaseField
	private String				validationcode	= "";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ApiKey() {
	}

	public ApiKey(final String login) {
		this.login = login;
		try {
			Dao<ApiKey, String> apikeyDao = AppConnector.getDBConnector().getApiKeysDao();
			// Try to create the key. It fails then  it was already created.
			apikeyDao.create(this);
		} catch (final SQLException sqle) {
			ApiKey.logger.info("WR [ApiKey.<init>]>ApiKey exists. Update values.");
			this.setDirty(true);
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getKeynumber() {
		return keynumber;
	}

	public String getLogin() {
		return login;
	}

	public String getValidationcode() {
		return validationcode;
	}

	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<ApiKey, String> apikeyDao = AppConnector.getDBConnector().getApiKeysDao();
				apikeyDao.update(this);
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public ApiKey setKeynumber(final int keynumber) {
		this.keynumber = keynumber;
		this.setDirty(true);
		return this;
	}

	public ApiKey setValidationcode(final String validationcode) {
		this.validationcode = validationcode;
		this.setDirty(true);
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ApiKey [");
		buffer.append(login).append("/");
		buffer.append(keynumber).append("-").append(validationcode);
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
