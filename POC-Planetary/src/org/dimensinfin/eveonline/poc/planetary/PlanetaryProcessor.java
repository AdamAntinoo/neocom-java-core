//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

//- IMPORT SECTION .........................................................................................
import java.awt.Cursor;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger												= Logger
			.getLogger("org.dimensinfin.eveonline.poc.planetary");
	private static final String	SELECT_RAW_PRODUCTRESULT			= "SELECT ps.schematicName AS productName, ps.cycleTime AS cycleTime, pstm.quantity AS inputQuantity"
			+ " FROM  planetSchematicsTypeMap pstm, planetSchematics ps" + " WHERE pstm.isInput " + " AND   pstm.typeID = ?"
			+ " AND   ps.schematicID = pstm.schematicID";

	private static final String	SELECT_RAW_PRODUCTRESULT_INV	= "SELECT ps.schematicName AS productName, ps.cycleTime AS cycleTime, pstm.quantity AS inputQuantity"
			+ " FROM  planetSchematicsTypeMap pstm" + " WHERE pstm.isInput AND   pstm.typeID = ?"
			+ " LEFT OUTER JOIN planetSchematics ps ON ps.schematicID = pstm.schematicID";

	private final static String	DATABASE_URL									= "jdbc:h2:mem:account";

	// - M E T H O D - S E C T I O N ..........................................................................
	public static void process(Resource inResource) {
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(SELECT_RAW_PRODUCTRESULT,
					new String[] { Integer.valueOf(inResource.getTypeID()).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
					// EveItem newItem = searchItembyID(resourceID);
					// Resource resource = ;
					inventionJob.add(new Resource(resourceID, qty));
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
	}

	// - F I E L D - S E C T I O N ............................................................................
	private SQLiteDatabase				ccpDatabase				= null;

	private Dao<Account, Integer>	accountDao;
	String												databaseUrl				= "jdbc:h2:mem:account";
	// create a connection source to our database
	ConnectionSource							connectionSource	= new JdbcConnectionSource(databaseUrl);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryProcessor() {
	}

}

// - UNUSED CODE ............................................................................................
