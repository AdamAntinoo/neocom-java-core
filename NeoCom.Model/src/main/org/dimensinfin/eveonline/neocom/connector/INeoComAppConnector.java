//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.connector;

import java.io.File;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComAppConnector /* extends IGenericAppConnector */ {
	public File accessAppStorage(final String resourceString);

	public String getAppFilePath(final int fileresourceid);

	public String getAppFilePath(final String fileresourceName);

	//	public ICacheConnector getCacheConnector();

	//	public ICCPDatabaseConnector getCCPDBConnector();

	//	public IDatabaseConnector getDBConnector();

	//	public INeoComModelStore getModelStore();

	public IStorageConnector getStorageConnector();

	public void startTimer();
}

// - UNUSED CODE ............................................................................................
