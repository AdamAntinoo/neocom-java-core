package org.dimensinfin.eveonline.neocom.planetary;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.dimensinfin.eveonline.neocom.industry.Resource;

public class PlanetaryResource extends Resource {
	public org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType tier = org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType.RAW;

	// - C O N S T R U C T O R S
	public PlanetaryResource( final int typeId ) {
		super( typeId );
	}

	public PlanetaryResource( final int typeId, final int qty ) {
		super( typeId, qty );
	}

	public String getName() {
		return this.getName();
	}

	public org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType getTier() {
		return org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType.searchTierType4Group( this.getItem().getGroupName() );
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this )
				.append( "tier", tier )
//				       .append("typeId", typeId)
				.append( super.toString() )
				.toString();
	}
}
