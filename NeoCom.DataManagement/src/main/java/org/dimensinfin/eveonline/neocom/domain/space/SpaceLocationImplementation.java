package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public class SpaceLocationImplementation extends NeoComNode implements SpaceLocation {
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
	private Long structureId;
	private GetUniverseStructuresStructureIdOk structure;

	private SpaceLocationImplementation() {super();}

	public Integer getRegionId() {
		return this.regionId;
	}

	public GetUniverseRegionsRegionIdOk getRegion() {
		return this.region;
	}

	public String getRegionName() {return this.region.getName();}

	public Integer getConstellationId() {
		return this.constellationId;
	}

	public GetUniverseConstellationsConstellationIdOk getConstellation() {
		return this.constellation;
	}

	public String getConstellationName() {return this.constellation.getName();}

	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	public GetUniverseSystemsSystemIdOk getSolarSystem() {
		return this.solarSystem;
	}

	public String getSolarSystemName() {return this.solarSystem.getName();}

	public Integer getStationId() {
		return this.stationId;
	}

	public GetUniverseStationsStationIdOk getStation() {
		return this.station;
	}

	public String getStationName() {return this.station.getName();}

	public Long getStructureId() {
		return this.structureId;
	}

	public GetUniverseStructuresStructureIdOk getStructure() {
		return this.structure;
	}
	public String getStructureName() {return this.structure.getName();}

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
		private SpaceLocationImplementation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceLocationImplementation();
		}

		public SpaceLocationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public SpaceLocationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellation = constellation;
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public SpaceLocationImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.solarSystem = solarSystem;
			this.onConstruction.solarSystemId = solarSystem.getSystemId();
			return this;
		}

		public SpaceLocationImplementation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.station = station;
			this.onConstruction.stationId = station.getStationId();
			return this;
		}
		public SpaceLocationImplementation.Builder withStructure( final Long structureId,
		                                                          final GetUniverseStructuresStructureIdOk structure ) {
			Objects.requireNonNull( structureId );
			Objects.requireNonNull( structure );
			this.onConstruction.structureId = structureId;
			this.onConstruction.structure = structure;
			return this;
		}

		public SpaceLocationImplementation.Builder withSecurity( final Double security ) {
			Objects.requireNonNull( security );
			this.onConstruction.security = security;
			return this;
		}

		public SpaceLocationImplementation.Builder withCorporation( final Integer corporationId,
		                                                            final GetCorporationsCorporationIdOk corporation ) {
			Objects.requireNonNull( corporationId );
			Objects.requireNonNull( corporation );
			this.onConstruction.corporationId = corporationId;
			this.onConstruction.corporation = corporation;
			return this;
		}

		public SpaceLocationImplementation build() {
			Objects.requireNonNull( this.onConstruction.region );
//			Objects.requireNonNull( this.onConstruction.constellation );
//			Objects.requireNonNull( this.onConstruction.solarSystem );
			return this.onConstruction;
		}
	}
}
