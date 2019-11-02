package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
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
			this.onConstruction.setRegion( region);
			return this;
		}

		public SpaceSystem build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			return this.onConstruction;
		}
	}
}