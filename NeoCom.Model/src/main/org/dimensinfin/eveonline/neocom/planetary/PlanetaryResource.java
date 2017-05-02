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
@DatabaseTable(tableName = "planetaryresource")
public class PlanetaryResource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("PlanetaryResource");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	private int						id			= -1;
	@DatabaseField
	private int						typeid;
	@DatabaseField
	private double				quantity;
	@DatabaseField
	private String				name		= "<NAME>";
	@DatabaseField(foreign = true)
	private ResourceList	ownerList;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryResource() {
	}

	public PlanetaryResource(final int newtype, final double newquantity) {
		typeid = newtype;
		quantity = newquantity;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ResourceList getOwnerListid() {
		return ownerList;
	}

	public double getQuantity() {
		return quantity;
	}

	public int getTypeid() {
		return typeid;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String newname) {
		name = newname;
	}

	public void setOwnerList(final ResourceList list) {
		ownerList = list;
	}

	public void setQuantity(final double newq) {
		quantity = newq;
	}

	public void setTypeid(final int typeid) {
		this.typeid = typeid;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("PlanetaryResource [");
		buffer.append("#").append(id).append(" ");
		buffer.append("name:").append(name);
		buffer.append(" [").append(quantity).append("]");
		buffer.append("]");
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
