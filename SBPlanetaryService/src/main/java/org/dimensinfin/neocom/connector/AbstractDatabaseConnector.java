//	PROJECT:        NeoCom.angularjs
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.

package org.dimensinfin.neocom.connector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;

import com.j256.ormlite.dao.Dao;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractDatabaseConnector implements IDatabaseConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("org.dimensinfin.neocom.connector");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractDatabaseConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkInvention(int typeID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public boolean checkManufacturable(int typeid) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public void clearInvalidRecords() {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public void closeDatabases() {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Dao<Asset, String> getAssetDAO() throws java.sql.SQLException {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Dao<Blueprint, String> getBlueprintDAO() throws java.sql.SQLException {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Dao<Job, String> getJobDAO() throws SQLException {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Dao<MarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Dao<Property, String> getPropertyDAO() throws SQLException {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public boolean openAppDataBase() {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public boolean openCCPDataBase() {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public boolean openDAO() {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int queryBlueprintDependencies(int bpitemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Resource> refineOre(int itemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public void replaceAssets(long characterID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public void replaceBlueprints(long characterID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public void replaceJobs(long characterID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Asset> searchAsset4Type(long characterID, int typeID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public Asset searchAssetByID(long parentAssetID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int searchBlueprint4Module(final int moduleID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Integer> searchInventionableBlueprints(String resourceIDs) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int searchInventionProduct(int typeID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public EveItem searchItembyID(int typeID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Job> searchJob4Class(long characterID, String string) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int searchJobExecutionTime(final int typeID, final int activityID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Resource> searchListOfDatacores(final int itemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public ArrayList<Resource> searchListOfMaterials(int itemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public EveLocation searchLocationbyID(long locationID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public EveLocation searchLocationBySystem(String system) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public MarketDataSet searchMarketData(int typeID, EMarketSide side) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int searchModule4Blueprint(int bpitemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public int searchStationType(long systemID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

	public String searchTech4Blueprint(int blueprintID) {
		throw new RuntimeException("Application connector not defined. Functionality 'checkExpiration' disabled.");
	}

}

// - UNUSED CODE ............................................................................................
