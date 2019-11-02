package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public class SpaceLocation extends NeoComNode implements SpaceSystem, Station, Structure {
	private static final long serialVersionUID = -9028958348146320642L;

	private Integer regionId;
	private GetUniverseRegionsRegionIdOk region;
	private Integer constellationId;
	private GetUniverseConstellationsConstellationIdOk constellation;
	private Integer solarSystemId;
	private GetUniverseSystemsSystemIdOk solarSystem;
	private Integer stationId;
	private GetUniverseStationsStationIdOk station;
	private Double security;
	private Integer corporationId;
	private GetCorporationsCorporationIdOk corporation;

	private SpaceLocation() {super();}

	@Override
	public Integer getRegionId() {
		return this.regionId;
	}

	@Override
	public GetUniverseRegionsRegionIdOk getRegion() {
		return this.region;
	}

	@Override
	public String getRegionName() {return this.region.getName();}

	public void setRegion( final GetUniverseRegionsRegionIdOk region ) {
		this.region = region;
		this.regionId = this.region.getRegionId();
	}

	@Override
	public Integer getConstellationId() {
		return this.constellationId;
	}

	@Override
	public GetUniverseConstellationsConstellationIdOk getConstellation() {
		return this.constellation;
	}

	@Override
	public String getConstellationName() {return this.constellation.getName();}

	public void setConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
		this.constellation = constellation;
		this.constellationId = this.constellation.getConstellationId();
	}

	@Override
	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	@Override
	public GetUniverseSystemsSystemIdOk getSolarSystem() {
		return this.solarSystem;
	}

	@Override
	public String getSolarSystemName() {return this.solarSystem.getName();}

	public void setSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
		this.solarSystem = solarSystem;
		this.solarSystemId = this.solarSystem.getSystemId();
	}

	@Override
	public Integer getStationId() {
		return this.stationId;
	}

	@Override
	public GetUniverseStationsStationIdOk getStation() {
		return this.station;
	}

	@Override
	public String getStationName() {return this.station.getName();}

	public void setStation( final GetUniverseStationsStationIdOk station ) {
		this.station = station;
		this.stationId = this.station.getStationId();
	}

	// - V I R T U A L
	public Long getLocationId() {
		if (null != this.station) return this.stationId.longValue();
		if (null != this.solarSystem) return this.solarSystemId.longValue();
		if (null != this.constellation) return this.constellationId.longValue();
		if (null != this.region) return this.regionId.longValue();
		throw new NeoComRuntimeException( "The SpaceLocation is invalid. There is not any of the minimum information." );
	}

	// - B U I L D E R
	public static class Builder {
		private SpaceLocation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceLocation();
		}

		public SpaceLocation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public SpaceLocation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellation = constellation;
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public SpaceLocation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.solarSystem = solarSystem;
			this.onConstruction.solarSystemId = solarSystem.getSystemId();
			return this;
		}

		public SpaceLocation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.station = station;
			this.onConstruction.stationId = station.getStationId();
			return this;
		}

		public SpaceLocation.Builder withSecurity( final Double security ) {
			Objects.requireNonNull( security );
			this.onConstruction.security = security;
			return this;
		}

		public SpaceLocation.Builder withCorporation( final GetCorporationsCorporationIdOk corporation ) {
			Objects.requireNonNull( corporation );
			this.onConstruction.corporation = corporation;
			return this;
		}

		public SpaceLocation.Builder withCorporationId( final Integer corporationId ) {
			Objects.requireNonNull( corporationId );
			this.onConstruction.corporationId = corporationId;
			return this;
		}

		public SpaceLocation build() {
			Objects.requireNonNull( this.onConstruction.region );
			Objects.requireNonNull( this.onConstruction.constellation );
			Objects.requireNonNull( this.onConstruction.solarSystem );
			return this.onConstruction;
		}
	}
}