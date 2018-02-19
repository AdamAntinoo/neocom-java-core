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

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.ColonySerialized;
import org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.database.entity.TimeStamp;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Property;

/**
 * This interface defines the methods that should be implemented at the final Helper implementation so all
 * platforms share a compatible api. This helper is associated to the NeoCom private database only. That database
 * is used through Dao elements and the api exports the same functionality that are found on Android database
 * helpers.
 *
 * @author Adam Antinoo
 */
public interface INeoComDBHelper {
	public int getDatabaseVersion();

	public int getStoredVersion() throws SQLException;

	public boolean isDatabaseValid();

	public boolean isOpen();

	public void onCreate( final ConnectionSource databaseConnection );

	public void onUpgrade( final ConnectionSource databaseConnection, final int oldVersion, final int newVersion );

	public void loadSeedData();

	public INeoComDBHelper setDatabaseHost( final String hostName );

	public INeoComDBHelper setDatabaseName( final String instanceName );

	public INeoComDBHelper setDatabaseUser( final String user );

	public INeoComDBHelper setDatabasePassword( final String password );

	public INeoComDBHelper setDatabaseVersion( final int newVersion );

	public INeoComDBHelper build() throws SQLException;

	public ConnectionSource getConnectionSource() throws SQLException;

	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException;

	public Dao<TimeStamp, String> getTimeStampDao() throws SQLException;

	@Deprecated
	public Dao<ApiKey, String> getApiKeysDao() throws SQLException;

	public Dao<Credential, String> getCredentialDao() throws SQLException;

	public Dao<Colony, String> getColonyDao() throws SQLException;

	@Deprecated
	public Dao<ColonyStorage, String> getColonyStorageDao() throws SQLException;

	@Deprecated
	public Dao<ColonySerialized, String> getColonySerializedDao() throws SQLException;

	public Dao<NeoComAsset, String> getAssetDao() throws SQLException;

	public Dao<EveLocation, String> getLocationDao() throws SQLException;

	public Dao<Property, String> getPropertyDao() throws SQLException;
}
