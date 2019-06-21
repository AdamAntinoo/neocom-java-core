package org.dimensinfin.eveonline.neocom.database;

import java.sql.SQLException;

public interface ISDEDatabaseAdapter extends ISDEDBHelper {
//	SQLiteDatabase getSDEDatabase();
	RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;
}
