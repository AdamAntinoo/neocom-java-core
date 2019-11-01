package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class StationLocation extends SpaceKLocation {
	private Integer stationId;
	private GetUniverseStationsStationIdOk station;
	private Double security;
	private Integer corporationId;
	private GetCorporationsCorporationIdOk corporation;

	private StationLocation() {}

	// - B U I L D E R
	public static class Builder {
		private StationLocation onConstruction;

		public Builder() {
			this.onConstruction = new StationLocation();
		}

		public StationLocation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public StationLocation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellation = constellation;
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public StationLocation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk system ) {
			Objects.requireNonNull( system );
			this.onConstruction.solarSystem = system;
			this.onConstruction.systemId = system.getSystemId();
			return this;
		}

		public StationLocation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.station = station;
			this.onConstruction.stationId = station.getStationId();
			return this;
		}

		public StationLocation.Builder withSecurity( final Double security ) {
			Objects.requireNonNull( security );
			this.onConstruction.security = security;
			return this;
		}

		public StationLocation.Builder withCorporation( final GetCorporationsCorporationIdOk corporation ) {
			Objects.requireNonNull( corporation );
			this.onConstruction.corporation = corporation;
			return this;
		}

		public StationLocation.Builder withCorporationId( final Integer corporationId ) {
			Objects.requireNonNull( corporationId );
			this.onConstruction.corporationId = corporationId;
			return this;
		}

		public StationLocation build() {
			Objects.requireNonNull( this.onConstruction.region );
			Objects.requireNonNull( this.onConstruction.constellation );
			Objects.requireNonNull( this.onConstruction.solarSystem );
			return this.onConstruction;
		}
	}
}