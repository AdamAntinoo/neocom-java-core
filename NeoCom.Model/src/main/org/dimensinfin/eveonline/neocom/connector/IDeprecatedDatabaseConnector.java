//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.industry.Job;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.planetary.ResourceList;

import com.j256.ormlite.dao.Dao;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public interface IDeprecatedDatabaseConnector {

	//	public ArrayList<NeoComAsset> accessAllPlanetaryAssets(long characterID);

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkInvention(int typeID);

	public boolean checkManufacturable(int typeid);

	//	public void clearInvalidRecords(long pilotid);

	public void closeDatabases();

	//	public Dao<ApiKey, String> getApiKeysDao() throws SQLException;

	public List<ApiKey> getApiList4Login(String login);

	//	public Dao<NeoComAsset, String> getAssetDAO() throws SQLException;
	//
	//	public Dao<NeoComBlueprint, String> getBlueprintDAO() throws SQLException;
	//
	//	public Dao<Job, String> getJobDAO() throws SQLException;
	//
	//	public Dao<EveLocation, String> getLocationDAO() throws SQLException;
	//
	//	public Dao<NeoComMarketOrder, String> getMarketOrderDAO() throws SQLException;
	//
	//	public Dao<PlanetaryResource, String> getPlanetaryResourceDao() throws SQLException;
	//
	//	public Dao<Property, String> getPropertyDAO() throws SQLException;

	public Dao<ResourceList, String> getResourceListDao() throws SQLException;

	//	public Dao<TimeStamp, String> getTimeStampDAO() throws SQLException;
	//
	//	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException;

	public void loadSeedData();

	public boolean openAppDataBase();

	public List<NeoComAsset> queryAllAssetContainers(long characterID);

	//	public List<NeoComAsset> queryAllAssetLocations(long identifier);

	//	public Hashtable<String, Login> queryAllLogins();

	public List<NeoComAsset> queryContainerContents(long id);

	public List<NeoComAsset> queryLocationContents(long id);

	public List<NeoComAsset> queryLocationPlanetaryContents(long id);

	//	public ArrayList<Resource> refineOre(int itemID);

	//	public void replaceAssets(long characterID);

	public void replaceBlueprints(long characterID);

	public void replaceJobs(long characterID);

	public List<NeoComAsset> searchAllBlueprintAssets(long characterID);

	//	public ArrayList<NeoComAsset> searchAsset4Category(final long characterID, final String categoryName);
	//
	//	public List<NeoComAsset> searchAsset4Type(long characterID, int typeID);
	//
	//	public NeoComAsset searchAssetByID(long parentAssetID);

	//	public List<NeoComAsset> searchAssetContainedAt(long pilotID, long assetID);

	//	public int searchBlueprint4Module(final int moduleID);

	public Vector<Integer> searchInputResources(int target);

	public List<Integer> searchInventionableBlueprints(String resourceIDs);

	public int searchInventionProduct(int typeID);

	public List<Job> searchJob4Class(long characterID, String string);

	public int searchJobExecutionTime(final int typeID, final int activityID);

	public ArrayList<Resource> searchListOfDatacores(final int itemID);

	public ArrayList<Resource> searchListOfMaterials(int itemID);

	public ArrayList<Resource> searchListOfReaction(int itemID);

	//	public int searchRawPlanetaryOutput(int itemID);

	public int searchReactionOutputMultiplier(int itemID);

	//	public Vector<Schematics> searchSchematics4Output(int targetId);

	//	public String searchTech4Blueprint(int blueprintID);

	//	public int totalLocationContentCount(long id);
}

// - UNUSED CODE ............................................................................................
