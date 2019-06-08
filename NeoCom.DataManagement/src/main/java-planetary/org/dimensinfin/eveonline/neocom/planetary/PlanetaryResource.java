//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "PlanetaryResource")
public class PlanetaryResource extends Resource {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true)
	private int id = -1;
	public String tier = "OTHER";
	//	@DatabaseField
	//	private int typeId;
	//	@DatabaseField
	//	private double				quantity;
	//	@DatabaseField
	//	private String				name		= "<NAME>";
	//	@DatabaseField(foreign = true)
	//	private ResourceList	ownerList;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Builds a new resource of quantity 1.
	 */
	public PlanetaryResource( final int typeId ) {
		super(typeId);
		jsonClass = "PlanetaryResource";
		// Set the tier type depending on the type id lookup list.
		if (getItem().getCategoryName() == "Planetary Resources") tier = "RAW";
		else {
			if (getItem().getGroupName() == "Basic Commodities") tier = "TIER1";
			if (getItem().getGroupName() == "Refined Commodities") tier = "TIER2";
			if (getItem().getGroupName() == "Specialized Commodities") tier = "TIER3";
			if (getItem().getGroupName() == "Advanced Commodities") tier = "TIER4";
		}
	}

	public PlanetaryResource( final int typeId, final int newQty ) {
		this(typeId);
		this.baseQty = newQty;
	}

	public PlanetaryResource( final int typeId, final int newQty, final int stackSize ) {
		this(typeId, newQty);
		this.stackSize = stackSize;
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	public String getTier() {
		evaluateTier();
		return this.tier;
	}

	public PlanetaryResource setTier( final String tier ) {
		this.tier = tier;
		return this;
	}

	protected void evaluateTier() {
		// Set the tier type depending on the type id lookup list.
		if (getItem().getCategoryName().equalsIgnoreCase("Planetary Resources")) tier = "RAW";
		else {
			if (getItem().getGroupName().equalsIgnoreCase("Basic Commodities")) tier = "TIER1";
			if (getItem().getGroupName().equalsIgnoreCase("Refined Commodities")) tier = "TIER2";
			if (getItem().getGroupName().equalsIgnoreCase("Specialized Commodities")) tier = "TIER3";
			if (getItem().getGroupName().equalsIgnoreCase("Advanced Commodities")) tier = "TIER4";
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				       .append("id", id)
				       .append("tier", tier)
				       .append("typeId", typeId)
				       .append("baseQty", baseQty)
				       .toString();
	}
}
