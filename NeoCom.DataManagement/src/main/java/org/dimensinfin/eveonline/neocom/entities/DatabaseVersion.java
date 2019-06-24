//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//					environment limits.
package org.dimensinfin.eveonline.neocom.entities;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

/**
 * This is a singleton row table. The unique ID is fixed to a predefined value because the database can only can
 * have a version so there is no need to allow the creation of more records.
 */
// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Version")
public class DatabaseVersion extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("DatabaseVersion");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	public String identifier = "CURRENT-VERSION";
	@DatabaseField
	public int versionNumber = -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DatabaseVersion() {
	}

	public DatabaseVersion(final int newVersion) {
		this();
		versionNumber = newVersion;
//		try {
//			Dao<DatabaseVersion, String> versionDao = accessGlobal().getNeocomDBHelper().getVersionDao();
//			// Try to create the key. It fails then  it was already created.
//			versionDao.create(this);
//		} catch ( final SQLException sqle ) {
//			DatabaseVersion.logger.info("WR [DatabaseVersion.<constructor>]> DatabaseVersion exists. Update valueto {}.", versionNumber);
//			this.store();
//		}
	}

	public DatabaseVersion(final String newVersion) {
		this(Integer.valueOf(newVersion).intValue());
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getVersionNumber() {
		return versionNumber;
	}

	public DatabaseVersion setVersionNumber(final int versionNumber) {
		this.versionNumber = versionNumber;
		return this;
	}

//	public DatabaseVersion store() {
//		try {
//			Dao<DatabaseVersion, String> versionDao = accessGlobal().getNeocomDBHelper().getVersionDao();
//			versionDao.update(this);
//		} catch ( final SQLException sqle ) {
//			DatabaseVersion.logger.error("WR [DatabaseVersion.store]> Exceptions saving Version: {}.", sqle.getMessage());
//			sqle.printStackTrace();
//		}
//		return this;
//	}
}

// - UNUSED CODE ............................................................................................
