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

import org.dimensinfin.eveonline.neocom.core.AccessStatistics;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

import java.sql.SQLException;
import java.util.List;

/**
 * This interface defines the methods that should be implemented at the database adapter for the Eve Online
 * SDE resources database. This database is downloaded from CCP and contains all the game data that should be used
 * to construct Items or any other detail information. Some of this information is also now replicated
 * on the Universe ESI api but database access is still required for many activities.
 *
 * @author Adam Antinoo
 */
public interface ISDEDBHelper {
	public static AccessStatistics locationsCacheStatistics = new AccessStatistics();

	public ISDEDBHelper setDatabaseSchema (final String newschema);

	public ISDEDBHelper setDatabasePath (final String newpath);

	public ISDEDBHelper setDatabaseName (final String instanceName);

	public ISDEDBHelper build () throws SQLException;

	public String getConnectionDescriptor ();

	public boolean databaseIsValid ();

	public EveItem searchItem4Id (final int typeId);

	public EveLocation searchLocation4Id (final long locationId);

	public EveLocation searchLocationBySystem (final String name);

	public ItemGroup searchItemGroup4Id (final int targetGroupId);

	public ItemCategory searchItemCategory4Id (final int targetCategoryId);

	public int searchStationType (final long stationId);

	public int searchModule4Blueprint (final int bpitemID);

	public String searchTech4Blueprint (final int blueprintID);

	public int searchRawPlanetaryOutput (final int typeID);

	public List<Schematics> searchSchematics4Output (final int targetId);
}
