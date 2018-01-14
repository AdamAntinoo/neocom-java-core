//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.connector;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IConnector {
	public void addCharacterUpdateRequest(long characterID);

	public IConnector getAppSingleton();

	public ICacheConnector getCacheConnector();

	public ICCPDatabaseConnector getCCPDBConnector();

	public IDeprecatedDatabaseConnector getDBConnector();

	//	public boolean checkExpiration(final long timestamp, final long window);

	//	public String getAppFilePath(int fileresourceid);

	//	public String getAppFilePath(String fileresourceid);

	//	public boolean getAssetsFormat();
	//	public Class<?> getFirstActivity();

//	public INeoComModelStore getModelStore();

	public IStorageConnector getStorageConnector();

	//	public NeocomPreferences getDefaultSharedPreferences();

	//	public String getResourceString(int reference);

	//	public IConnector getSingleton();

	//	public boolean sdcardAvailable();
}

// - UNUSED CODE ............................................................................................
