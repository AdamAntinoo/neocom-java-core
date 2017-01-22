//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.poc.connector;

import org.dimensinfin.eveonline.neocom.core.INeoComModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IPOCConnector {

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addCharacterUpdateRequest(long characterID);

	public boolean checkExpiration(final long timestamp, final long window);

	public String getAppFilePath(int fileresourceid);

	public String getAppFilePath(String fileresourceid);

	public IPOCDatabaseConnector getDBConnector();

	public INeoComModelStore getModelStore();

	public String getResourceString(int reference);

	public IPOCConnector getSingleton();

//	public IStorageConnector getStorageConnector();

	public boolean sdcardAvailable();
}

// - UNUSED CODE ............................................................................................
