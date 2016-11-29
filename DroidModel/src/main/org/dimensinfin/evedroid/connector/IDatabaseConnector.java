//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.connector;

import java.sql.SQLException;
import java.util.ArrayList;

import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.market.MarketDataSet;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Job;
import org.dimensinfin.evedroid.model.MarketOrder;
import org.dimensinfin.evedroid.model.Property;

import com.j256.ormlite.dao.Dao;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public interface IDatabaseConnector {

	public boolean checkInvention(int typeID);

	public boolean checkManufacturable(int typeid);

	// - M E T H O D - S E C T I O N ..........................................................................
	public void clearInvalidRecords();

	public void closeDatabases();

	public Dao<Asset, String> getAssetDAO() throws java.sql.SQLException;

	public Dao<Blueprint, String> getBlueprintDAO() throws java.sql.SQLException;

	public Dao<Job, String> getJobDAO() throws SQLException;

	public Dao<MarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException;

	public Dao<Property, String> getPropertyDAO() throws SQLException;

	public boolean openAppDataBase();

	public boolean openCCPDataBase();

	public boolean openDAO();

	public int queryBlueprintDependencies(int bpitemID);

	public ArrayList<Resource> refineOre(int itemID);

	public void replaceAssets(long characterID);

	public void replaceBlueprints(long characterID);

	public void replaceJobs(long characterID);

	public ArrayList<Asset> searchAsset4Type(long characterID, int typeID);

	public Asset searchAssetByID(long parentAssetID);

	public int searchBlueprint4Module(final int moduleID);

	public ArrayList<Integer> searchInventionableBlueprints(String resourceIDs);

	public int searchInventionProduct(int typeID);

	public EveItem searchItembyID(int typeID);

	public ArrayList<Job> searchJob4Class(long characterID, String string);

	public int searchJobExecutionTime(final int typeID, final int activityID);

	public ArrayList<Resource> searchListOfDatacores(final int itemID);

	public ArrayList<Resource> searchListOfMaterials(int itemID);

	public EveLocation searchLocationbyID(long locationID);

	public EveLocation searchLocationBySystem(String system);

	public MarketDataSet searchMarketData(int typeID, EMarketSide side);

	public int searchModule4Blueprint(int bpitemID);

	public int searchStationType(long systemID);

	public String searchTech4Blueprint(int blueprintID);

	public ArrayList<Resource> searchListOfReaction(int itemID);

	public int searchReactionOutputMultiplier(int itemID);

	public Dao<EveLocation, String> getLocationDAO()throws java.sql.SQLException;
}

// - UNUSED CODE ............................................................................................
