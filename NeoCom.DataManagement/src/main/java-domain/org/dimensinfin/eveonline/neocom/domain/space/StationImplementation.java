package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class StationImplementation /*extends AssetContainer*/ implements Station {
	private static final long serialVersionUID = -646550210810227292L;
	private SpaceLocationImplementation spaceLocation;

	private StationImplementation() {super();}

	// - D E L E G A T E S
	@Override
	public Long getLocationId() {return spaceLocation.getLocationId();}

	@Override
	public Integer getRegionId() {return spaceLocation.getRegionId();}

	@Override
	public GetUniverseRegionsRegionIdOk getRegion() {return spaceLocation.getRegion();}

	@Override
	public String getRegionName() {return spaceLocation.getRegionName();}

	@Override
	public Integer getConstellationId() {return spaceLocation.getConstellationId();}

	@Override
	public GetUniverseConstellationsConstellationIdOk getConstellation() {return spaceLocation.getConstellation();}

	@Override
	public String getConstellationName() {return spaceLocation.getConstellationName();}

	@Override
	public Integer getSolarSystemId() {return spaceLocation.getSolarSystemId();}

	@Override
	public GetUniverseSystemsSystemIdOk getSolarSystem() {return spaceLocation.getSolarSystem();}

	@Override
	public String getSolarSystemName() {return spaceLocation.getSolarSystemName();}

	@Override
	public Integer getStationId() {return spaceLocation.getStationId();}

	@Override
	public GetUniverseStationsStationIdOk getStation() {return spaceLocation.getStation();}

	@Override
	public String getStationName() {return spaceLocation.getStationName();}

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
			this.onConstruction.spaceLocation = new SpaceLocationImplementation.Builder()
					.withRegion( this.region )
					.withConstellation( this.constellation )
					.withSolarSystem( this.solarSystem )
					.withStation( this.station )
					.build();
			return this.onConstruction;
		}
	}
}