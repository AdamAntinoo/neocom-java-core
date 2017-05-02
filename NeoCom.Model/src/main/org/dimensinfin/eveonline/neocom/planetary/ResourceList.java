//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download and parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.logging.Logger;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "resourcelist")
public class ResourceList {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("ResourceList");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	public int						id			= -1;
	@DatabaseField
	public String					name		= "<NAME>";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ResourceList() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}

// - UNUSED CODE ............................................................................................
