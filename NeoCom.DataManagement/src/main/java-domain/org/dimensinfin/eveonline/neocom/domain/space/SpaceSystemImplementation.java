package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class SpaceSystemImplementation extends AssetContainer {

	private SpaceSystemImplementation() {super();}

	// - B U I L D E R
	public static class Builder {
		private SpaceSystemImplementation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceSystemImplementation();
		}

		public SpaceSystemImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.setRegion( region );
			return this;
		}

		public SpaceSystemImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.setConstellation( constellation );
			return this;
		}

		public SpaceSystemImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.setSolarSystem( solarSystem );
			return this;
		}

		public SpaceSystem build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			Objects.requireNonNull( this.onConstruction.getConstellation() );
			Objects.requireNonNull( this.onConstruction.getSolarSystem() );
			return this.onConstruction;
		}
	}
}