package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationContainer;

public class SpaceRegionImplementation extends LocationContainer {

	private SpaceRegionImplementation() {super();}

	// - B U I L D E R
	public static class Builder {
		private SpaceRegionImplementation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceRegionImplementation();
		}

		public SpaceRegionImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.setRegion( region);
			return this;
		}

		public SpaceRegion build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			return this.onConstruction;
		}
	}
}