package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class StationImplementation extends AssetContainer {

	private StationImplementation() {super();}

	// - B U I L D E R
	public static class Builder {
		private StationImplementation onConstruction;

		public Builder() {
			this.onConstruction = new StationImplementation();
		}

		public StationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.setRegion( region);
			return this;
		}

		public Station build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			return this.onConstruction;
		}
	}
}