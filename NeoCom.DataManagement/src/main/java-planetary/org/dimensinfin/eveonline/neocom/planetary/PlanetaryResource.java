package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PlanetaryResource extends Resource {
	public String tier = "OTHER";

	// - C O N S T R U C T O R S
	public PlanetaryResource( final int typeId ) {
		super(typeId);
	}

	public PlanetaryResource( final int typeId, final int qty ) {
		super(typeId, qty);
	}

	public String getTier() {
		this.evaluateTier();
		return this.tier;
	}

	public PlanetaryResource setTier( final String tier ) {
		this.tier = tier;
		return this;
	}

	protected void evaluateTier() {
		// Set the tier type depending on the type id lookup list.
		if (this.getItem().getCategoryName().equalsIgnoreCase("Planetary Resources")) tier = "RAW";
		if (this.getItem().getCategoryName().equalsIgnoreCase("Planetary Commodities")) tier = "RAW";
		if (this.getItem().getGroupName().equalsIgnoreCase("Basic Commodities - Tier 1")) tier = "TIER1";
		if (this.getItem().getGroupName().equalsIgnoreCase("Refined Commodities - Tier 2")) tier = "TIER2";
		if (this.getItem().getGroupName().equalsIgnoreCase("Specialized Commodities- Tier 3")) tier = "TIER3";
		if (this.getItem().getGroupName().equalsIgnoreCase("Advanced Commodities - Tier 4")) tier = "TIER4";
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				       //				       .append("id", id)
				       .append("tier", tier)
				       .append("typeId", typeId)
				       //				       .append("baseQty", baseQty)
				       .append(super.toString())
				       .toString();
	}
}
