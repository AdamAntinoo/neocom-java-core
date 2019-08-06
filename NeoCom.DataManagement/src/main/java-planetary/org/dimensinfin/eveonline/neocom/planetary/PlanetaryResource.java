package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PlanetaryResource extends Resource {
	public PlanetaryResourceTierType tier = PlanetaryResourceTierType.RAW;

	// - C O N S T R U C T O R S
	public PlanetaryResource( final int typeId ) {
		super(typeId);
	}

	public PlanetaryResource( final int typeId, final int qty ) {
		super(typeId, qty);
	}

	public PlanetaryResourceTierType getTier() {
		return PlanetaryResourceTierType.searchTierType4Group(this.getItem().getGroupName());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				       .append("tier", tier)
//				       .append("typeId", typeId)
				       .append(super.toString())
				       .toString();
	}
}
