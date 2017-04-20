//	PROJECT:        NeoCom.Databases (NEOC.D)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.8 Library.
//	DESCRIPTION:		SQLite database access library. Isolates Neocom database access from any
//					environment limits.
package org.dimensinfin.eveonline.neocom.databases;

// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

// - CLASS IMPLEMENTATION ...................................................................................
public  class NeocomAndroidOpenHelper extends OrmLiteSqliteOpenHelper{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("org.dimensinfin.eveonline.neocom.databases");

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public NeocomAndroidOpenHelper() {
//	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
