package org.dimensinfin.eveonline.neocom.converter;

import org.dimensinfin.eveonline.neocom.domain.Region;
import org.dimensinfin.eveonline.neocom.domain.SpaceKLocation;

public class SpaceKLocation2RegionDuplicator {
	public static Region clone ( final SpaceKLocation location ){
		return new Region.Builder()
				.withRegion( location.getRegion() )
				.build();
	}
}