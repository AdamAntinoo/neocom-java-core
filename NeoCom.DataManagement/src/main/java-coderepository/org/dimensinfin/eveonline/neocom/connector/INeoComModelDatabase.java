//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.entities.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Property;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComModelDatabase /* extends IDeprecatedDatabaseConnector */ {
	public ArrayList<NeoComAsset> accessAllPlanetaryAssets (long characterID);

	public void clearInvalidRecords (final long pilotid);

	public void replaceBlueprints (final long characterID);

	//	public Dao<Credential, String> getCredentialDao () throws SQLException;

	//	public Dao<ApiKey, String> getApiKeysDao () throws SQLException;

	public Dao<NeoComAsset, String> getAssetDao () throws SQLException;

	public Dao<NeoComBlueprint, String> getBlueprintDao () throws SQLException;

	//	public Dao<Job, String> getJobDAO() throws SQLException;

	public Dao<EsiLocation, String> getLocationDao () throws SQLException;

	//	public Dao<NeoComMarketOrder, String> getMarketOrderDAO() throws SQLException;

	//	public Dao<PlanetaryResource, String> getPlanetaryResourceDao() throws SQLException;

	public Dao<Property, String> getPropertyDao () throws SQLException;

	public Dao<TimeStamp, String> getTimeStampDao () throws SQLException;

	public Dao<DatabaseVersion, String> getVersionDao () throws SQLException;

	public void loadSeedData ();

	//	public List<NeoComAsset> queryAllAssetLocations(long identifier);

	//	public Hashtable<String, Login> queryAllLogins();

	//	public List<NeoComAsset> queryContainerContents(final long identifier);
	//
	//	public List<NeoComAsset> searchAssetsAtLocation(long id);

	public void replaceAssets (long characterID);

	//	public List<NeoComAsset> searchAllBlueprintAssets(long characterID);

	public ArrayList<NeoComAsset> searchAsset4Category (final long characterID, final String categoryName);

	//	public List<NeoComAsset> searchAsset4Type(long characterID, int typeID);

	public NeoComAsset searchAssetByID (long parentAssetID);

	//	public List<NeoComAsset> searchAssetContainedAt(long pilotID, long assetID);

	public List<NeoComAsset> searchAssetsAtLocation (final long ownerid, final long identifier);

	public int totalLocationContentCount (final long identifier);
}

// - UNUSED CODE ............................................................................................
