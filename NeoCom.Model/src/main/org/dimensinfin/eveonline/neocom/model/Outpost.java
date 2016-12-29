//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.model;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Outposts")
public class Outpost extends AbstractComplexNode {
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
