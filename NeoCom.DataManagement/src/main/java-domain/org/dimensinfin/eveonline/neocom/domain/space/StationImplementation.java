package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class StationImplementation extends AssetContainer {

	private StationImplementation() {super();}

	// - B U I L D E R
	public static class Builder {
		private StationImplementation onConstruction;
		private GetUniverseRegionsRegionIdOk region;
		private GetUniverseConstellationsConstellationIdOk constellation;
		private GetUniverseSystemsSystemIdOk solarSystem;
		private GetUniverseStationsStationIdOk station;

		public Builder() {
			this.onConstruction = new StationImplementation();
		}

		public StationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.region = region;
			return this;
		}

		public StationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.constellation = constellation;
			return this;
		}

		public StationImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.solarSystem = solarSystem;
			return this;
		}

		public StationImplementation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.station = station;
			return this;
		}

		public Station build() {
			Objects.requireNonNull( this.region );
			Objects.requireNonNull( this.constellation );
			Objects.requireNonNull( this.solarSystem );
			Objects.requireNonNull( this.station );
			this.onConstruction.spaceLocation = new SpaceLocation.Builder()
					.withRegion( this.region )
					.withConstellation( this.constellation )
					.withSolarSystem( this.solarSystem )
					.withStation( this.station )
					.build();
			return this.onConstruction;
		}
	}
}