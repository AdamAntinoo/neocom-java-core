//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//					environment limits.
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.NeoComAppConnector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Version")
public class DatabaseVersion {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger				= Logger.getLogger("DatabaseVersion");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	public int						versionNumber	= -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DatabaseVersion() {
	}

	public DatabaseVersion(final int newVersion) {
		versionNumber = newVersion;
		try {
			Dao<DatabaseVersion, String> versionDao = NeoComAppConnector.getSingleton().getDBConnector().getVersionDao();
			// Try to create the key. It fails then  it was already created.
			versionDao.create(this);
		} catch (final SQLException sqle) {
			DatabaseVersion.logger.info("WR [DatabaseVersion.<init>]>DatabaseVersion exists. Update values.");
			this.setDirty(true);
		}
	}

	public DatabaseVersion(final String newVersion) {
		this(Integer.valueOf(newVersion).intValue());
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getVersionNumber() {
		return versionNumber;
	}

	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<DatabaseVersion, String> versionDao = NeoComAppConnector.getSingleton().getDBConnector().getVersionDao();
				versionDao.update(this);
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public void setVersionNumber(final int versionNumber) {
		this.versionNumber = versionNumber;
	}
}

// - UNUSED CODE ............................................................................................
