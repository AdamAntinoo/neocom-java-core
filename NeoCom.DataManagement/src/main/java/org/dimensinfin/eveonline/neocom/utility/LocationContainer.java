package org.dimensinfin.eveonline.neocom.utility;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class LocationContainer implements Station, SpaceSystem {
	private SpaceLocation location;
	private List<SpaceLocation> contents = new ArrayList<>();

	protected LocationContainer() {}

	public int addContent( final SpaceLocation item ) {
		this.contents.add( item );
		return this.contents.size();
	}

	// - D E L E G A T E S
	@Override
	public Integer getRegionId() {return this.location.getRegionId();}

	@Override
	public GetUniverseRegionsRegionIdOk getRegion() {return this.location.getRegion();}

	@Override
	public String getRegionName() {return this.location.getRegionName();}

	public void setRegion( final GetUniverseRegionsRegionIdOk region ) {
		this.location.setRegion( region );
	}

	@Override
	public Integer getConstellationId() {
		return this.location.getConstellationId();
	}

	public void setConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
		this.location.setConstellation( constellation );
	}

	@Override
	public GetUniverseConstellationsConstellationIdOk getConstellation() {
		return this.location.getConstellation();
	}

	@Override
	public String getConstellationName() {return this.location.getConstellationName();}

	@Override
	public Integer getSolarSystemId() {return location.getSolarSystemId();}

	@Override
	public GetUniverseSystemsSystemIdOk getSolarSystem() {return location.getSolarSystem();}

	@Override
	public String getSolarSystemName() {return location.getSolarSystemName();}


	// - B U I L D E R
//	public static class Builder {
//		private AssetContainer onConstruction;
//
//		public Builder() {
//			this.onConstruction = new AssetContainer();
//		}
//
//		public AssetContainer.Builder withLocation( final SpaceLocation location ) {
//			Objects.requireNonNull( location );
//			this.onConstruction.location = location;
//			return this;
//		}
//
//		public AssetContainer build() {
//			Objects.requireNonNull( this.onConstruction.location );
//			return this.onConstruction;
//		}
//	}
}