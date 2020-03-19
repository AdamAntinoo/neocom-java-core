package org.dimensinfin.eveonline.neocom.planetary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.Resource;

public class PlanetaryResource extends Resource {
	// - C O N S T R U C T O R S
	public PlanetaryResource( final int typeId ) {
		super( typeId );
	}

	public PlanetaryResourceTierType getTier() {
		return PlanetaryResourceTierType.searchTierType4Group( this.getItem().getGroupName() );
	}

	// - C O R E
	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "tier", this.getTier() )
				.append( super.toString() )
				.toString();
	}
}
