//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.poc.connector;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IPOCDatabaseConnector {

	public boolean checkInvention(int typeID);

	//	public boolean checkManufacturable(int typeid);
	//
	//	public void clearInvalidRecords();
	//
	//	public void closeDatabases();
	//
	//	public Dao<Asset, String> getAssetDAO() throws java.sql.SQLException;
	//
	//	public Dao<Blueprint, String> getBlueprintDAO() throws java.sql.SQLException;
	//
	//	public Dao<Job, String> getJobDAO() throws SQLException;
	//
	//	public Dao<MarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException;
	//
	//	public Dao<Property, String> getPropertyDAO() throws SQLException;
	//
	//	public boolean openAppDataBase();
	//
	//	public boolean openCCPDataBase();
	//
	//	public boolean openDAO();
	//
	//	public int queryBlueprintDependencies(int bpitemID);
	//
	//	public ArrayList<Resource> refineOre(int itemID);
	//
	//	public void replaceAssets(long characterID);
	//
	//	public void replaceBlueprints(long characterID);
	//
	//	public void replaceJobs(long characterID);
	//
	//	public ArrayList<Asset> searchAsset4Type(long characterID, int typeID);
	//
	//	public Asset searchAssetByID(long parentAssetID);
	//
	//	public int searchBlueprint4Module(final int moduleID);
	//
	//	public ArrayList<Integer> searchInventionableBlueprints(String resourceIDs);
	//
	//	public int searchInventionProduct(int typeID);
	//
	public EveItem searchItembyID(int typeID);

	//	public ArrayList<Job> searchJob4Class(long characterID, String string);
	//
	//	public int searchJobExecutionTime(final int typeID, final int activityID);
	//
	//	public ArrayList<Resource> searchListOfDatacores(final int itemID);
	//
	//	public ArrayList<Resource> searchListOfMaterials(int itemID);
	//
	public EveLocation searchLocationbyID(long locationID);

	//	public EveLocation searchLocationBySystem(String system);
	//
	public MarketDataSet searchMarketData(int typeID, EMarketSide side);

	public int searchModule4Blueprint(int bpitemID);

	//	public int searchStationType(long systemID);

	public String searchTech4Blueprint(int blueprintID);
}

// - UNUSED CODE ............................................................................................
