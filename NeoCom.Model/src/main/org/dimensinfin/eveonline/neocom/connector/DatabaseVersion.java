//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//					environment limits.
package org.dimensinfin.eveonline.neocom.connector;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "version")
public class DatabaseVersion {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	public int versionNumber = -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DatabaseVersion() {
	}

	public DatabaseVersion(int newVersion) {
		versionNumber = newVersion;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}
}

// - UNUSED CODE ............................................................................................
