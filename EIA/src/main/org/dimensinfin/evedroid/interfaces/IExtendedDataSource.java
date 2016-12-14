//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.interfaces;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.DataSourceManager;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IExtendedDataSource extends IDataSource {
	// - M E T H O D - S E C T I O N ..........................................................................
	public RootNode collaborate2Model();

	@Deprecated
	public void connect(DataSourceManager dataSourceManager);

	public void createContentHierarchy();

	@Deprecated
	public ArrayList<AbstractAndroidPart> getBodyParts();

	public DataSourceLocator getDataSourceLocator();

	//	public RootNode getHeaderModel();

	@Deprecated
	public ArrayList<AbstractAndroidPart> getHeaderParts();
}

// - UNUSED CODE ............................................................................................
