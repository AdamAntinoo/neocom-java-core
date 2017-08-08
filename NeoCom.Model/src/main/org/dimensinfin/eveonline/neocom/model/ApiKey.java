//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//					environment limits.
//					Database and model adaptations for storage model independency.
package org.dimensinfin.eveonline.neocom.model;

import java.util.logging.Logger;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "apikey")
public class ApiKey {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger					= Logger.getLogger("ApiKey.java");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField
	private String				login						= "Default";
	@DatabaseField(id = true)
	private int						keynumber				= -1;
	@DatabaseField
	private String				validationcode	= "";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ApiKey() {
	}

	public ApiKey(final String login) {
		this.login = login;
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

	public void setKeynumber(final int keynumber) {
		this.keynumber = keynumber;
	}

	public void setValidationcode(final String validationcode) {
		this.validationcode = validationcode;
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
