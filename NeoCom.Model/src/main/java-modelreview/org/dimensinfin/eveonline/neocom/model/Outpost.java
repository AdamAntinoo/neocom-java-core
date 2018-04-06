//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download and parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Outposts")
public class Outpost extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7718648590261849585L;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(index = true)
	private int								facilityID				= -1;
	@DatabaseField
	private String						name							= "";
	@DatabaseField(index = true)
	private long							solarSystem				= -1;
	@DatabaseField
	private long							region						= -1;
	@DatabaseField
	private long							owner							= -1;
	@DatabaseField
	private long							type							= -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getFacilityID() {
		return facilityID;
	}

	public String getName() {
		return name;
	}

	public long getOwner() {
		return owner;
	}

	public long getRegion() {
		return region;
	}

	public long getSolarSystem() {
		return solarSystem;
	}

	public long getType() {
		return type;
	}

	public void setFacilityID(final int facilityID) {
		this.facilityID = facilityID;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOwner(final long owner) {
		this.owner = owner;
	}

	public void setRegion(final long region) {
		this.region = region;
	}

	public void setSolarSystem(final long solarSystem) {
		this.solarSystem = solarSystem;
	}

	public void setType(final long type) {
		this.type = type;
	}

}

// - UNUSED CODE ............................................................................................
