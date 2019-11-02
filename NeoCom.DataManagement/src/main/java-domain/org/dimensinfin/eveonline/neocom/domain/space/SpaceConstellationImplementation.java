package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationContainer;

public class SpaceConstellationImplementation extends LocationContainer {

	private SpaceConstellationImplementation() {super();}

	// - B U I L D E R
	public static class Builder {
		private SpaceConstellationImplementation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceConstellationImplementation();
		}

		public SpaceConstellationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.setRegion( region );
			return this;
		}

		public SpaceConstellationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.setConstellation( constellation);
			return this;
		}

		public SpaceSystem build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			Objects.requireNonNull( this.onConstruction.getConstellation() );
			return this.onConstruction;
		}
	}
}