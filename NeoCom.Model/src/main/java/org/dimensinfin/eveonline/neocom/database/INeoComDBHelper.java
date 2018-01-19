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
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.dimensinfin.eveonline.neocom.model.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.planetary.Colony;

import java.sql.SQLException;

/**
 * This interface defines the methods that should be implemented at the final Helper implementation so all
 * platforms share a compatible api. During development this api will include the contents of the current
 * <code>INeoComModelDatabase</code>.
 *
 * @author Adam Antinoo
 */
public interface INeoComDBHelper {
	public Dao<DatabaseVersion, String> getVersionDao () throws SQLException;
	public Dao<ApiKey, String> getApiKeysDao () throws SQLException;

	public Dao<Credential, String> getCredentialDao () throws SQLException;

	public Dao<Colony, String> getColonyDao () throws SQLException;
}
