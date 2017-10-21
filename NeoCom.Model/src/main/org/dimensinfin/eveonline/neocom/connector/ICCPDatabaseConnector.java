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

import java.util.ArrayList;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

//- INTERFACE IMPLEMENTATION ...............................................................................
public interface ICCPDatabaseConnector {
	public boolean openCCPDataBase();

	public ArrayList<Resource> refineOre(int itemID);

	public int searchBlueprint4Module(final int moduleID);

	public EveItem searchItembyID(int typeID);

	public EveLocation searchLocationbyID(long locationID);

	public EveLocation searchLocationBySystem(String system);

	public int searchModule4Blueprint(int bpitemID);

	public int searchStationType(long systemID);

	public String searchTech4Blueprint(int blueprintID);
}
