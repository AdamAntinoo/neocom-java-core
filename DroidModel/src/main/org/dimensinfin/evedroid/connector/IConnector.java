//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.connector;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.evedroid.core.INeoComModelStore;


// - CLASS IMPLEMENTATION ...................................................................................
public interface IConnector {

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkExpiration(final long timestamp, final long window);

	public String getAppFilePath(int fileresourceid);

	public String getAppFilePath(String fileresourceid);

	//	public ICache getCacheConnector();

	public IDatabaseConnector getDBConnector();

	public String getResourceString(int reference);

	public IStorageConnector getStorageConnector();

	public boolean sdcardAvailable();

	public IConnector getSingleton();

	public INeoComModelStore getModelStore();
}

// - UNUSED CODE ............................................................................................
