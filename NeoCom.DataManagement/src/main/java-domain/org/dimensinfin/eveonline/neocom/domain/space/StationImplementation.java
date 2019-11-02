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

		public Builder() {
			this.onConstruction = new StationImplementation();
		}

		public StationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.setRegion( region);
			return this;
		}
		public StationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.setConstellation( constellation);
			return this;
		}
		public StationImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.setSolarSystem( solarSystem);
			return this;
		}
		public StationImplementation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.setStation( station);
			return this;
		}

		public Station build() {
			Objects.requireNonNull( this.onConstruction.getRegion() );
			Objects.requireNonNull( this.onConstruction.getConstellation() );
			Objects.requireNonNull( this.onConstruction.getSolarSystem() );
			Objects.requireNonNull( this.onConstruction.getStation() );
			return this.onConstruction;
		}
	}
}