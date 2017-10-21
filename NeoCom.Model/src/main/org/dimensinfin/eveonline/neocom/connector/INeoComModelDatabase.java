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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.industry.Job;
import org.dimensinfin.eveonline.neocom.market.NeoComMarketOrder;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Login;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Property;
import org.dimensinfin.eveonline.neocom.model.TimeStamp;
import org.dimensinfin.eveonline.neocom.planetary.PlanetaryResource;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

import com.j256.ormlite.dao.Dao;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComModelDatabase extends IDeprecatedDatabaseConnector {
	public ArrayList<NeoComAsset> accessAllPlanetaryAssets(long characterID);

	public void clearInvalidRecords(long pilotid);

	public Dao<ApiKey, String> getApiKeysDao() throws SQLException;

	public Dao<NeoComAsset, String> getAssetDAO() throws SQLException;

	public Dao<NeoComBlueprint, String> getBlueprintDAO() throws SQLException;

	public Dao<Job, String> getJobDAO() throws SQLException;

	public Dao<EveLocation, String> getLocationDAO() throws SQLException;

	public Dao<NeoComMarketOrder, String> getMarketOrderDAO() throws SQLException;

	public Dao<PlanetaryResource, String> getPlanetaryResourceDao() throws SQLException;

	public Dao<Property, String> getPropertyDAO() throws SQLException;

	public Dao<TimeStamp, String> getTimeStampDAO() throws SQLException;

	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException;

	public List<NeoComAsset> queryAllAssetLocations(long identifier);

	public Hashtable<String, Login> queryAllLogins();

	@Override
	public List<NeoComAsset> queryLocationContents(long id);

	public void replaceAssets(long characterID);

	@Override
	public List<NeoComAsset> searchAllBlueprintAssets(long characterID);

	public ArrayList<NeoComAsset> searchAsset4Category(final long characterID, final String categoryName);

	public List<NeoComAsset> searchAsset4Type(long characterID, int typeID);

	public NeoComAsset searchAssetByID(long parentAssetID);

	public List<NeoComAsset> searchAssetContainedAt(long pilotID, long assetID);

	public int searchRawPlanetaryOutput(int itemID);

	public Vector<Schematics> searchSchematics4Output(int targetId);
}

// - UNUSED CODE ............................................................................................
