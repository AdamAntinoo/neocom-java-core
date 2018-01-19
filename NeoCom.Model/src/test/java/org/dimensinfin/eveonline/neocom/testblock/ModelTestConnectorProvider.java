//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionalities than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.testblock;

import org.dimensinfin.eveonline.neocom.connector.ICCPDatabaseConnector;
import org.dimensinfin.eveonline.neocom.connector.ICacheConnector;
import org.dimensinfin.eveonline.neocom.connector.IModelAppConnector;
import org.dimensinfin.eveonline.neocom.connector.INeoComModelDatabase;
import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.NeoComDBHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created by Adam on 18/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ModelTestConnectorProvider implements IModelAppConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ModelTestConnectorProvider.class);

	// - F I E L D - S E C T I O N ............................................................................
	private NeoComDBHelper helper;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModelTestConnectorProvider () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	@Override
	public ICacheConnector getCacheConnector () {
		return null;
	}

	@Override
	public ICCPDatabaseConnector getCCPDBConnector () {
		return null;
	}

	@Override
	public INeoComModelDatabase getDBConnector () {
		return null;
	}

	@Override
	public INeoComDBHelper getNewDBConnector () throws SQLException {
		if ( null == helper ) {
			helper = new NeoComDBHelper()
					.setDatabaseHost("jdbc:mysql://localhost:3306")
					.setDatabaseName("neocom")
					.setDatabaseUser("NEOCOMTEST")
					.setDatabasePassword("01.Alpha")
					.setDatabaseVersion(1)
					.build();
		}
		return helper;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ModelTestConnectorProvider [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
