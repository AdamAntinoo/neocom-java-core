package org.dimensinfin.eveonline.neocom.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class AssetContainer implements Station {
	protected SpaceLocation spaceLocation;
	private List<NeoAsset> contents = new ArrayList<>();

	protected AssetContainer() {}

	public int addContent( final NeoAsset item ) {
		this.contents.add( item );
		return this.contents.size();
	}

	// - D E L E G A T E S
	@Override
	public Integer getRegionId() {return this.spaceLocation.getRegionId();}

	@Override
	public GetUniverseRegionsRegionIdOk getRegion() {return this.spaceLocation.getRegion();}

	@Override
	public String getRegionName() {return this.spaceLocation.getRegionName();}

	public void setRegion( final GetUniverseRegionsRegionIdOk region ) {
		this.spaceLocation.setRegion( region );
	}

	@Override
	public Integer getConstellationId() {
		return this.spaceLocation.getConstellationId();
	}

	@Override
	public GetUniverseConstellationsConstellationIdOk getConstellation() {
		return this.spaceLocation.getConstellation();
	}

	@Override
	public String getConstellationName() {return this.spaceLocation.getConstellationName();}

	public void setConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
		this.spaceLocation.setConstellation( constellation );
	}

	@Override
	public Integer getSolarSystemId() {return this.spaceLocation.getSolarSystemId();}

	@Override
	public GetUniverseSystemsSystemIdOk getSolarSystem() {return this.spaceLocation.getSolarSystem();}

	@Override
	public String getSolarSystemName() {return this.spaceLocation.getSolarSystemName();}

	public void setSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
		this.spaceLocation.setSolarSystem( solarSystem );
	}

	@Override
	public Integer getStationId() {return this.spaceLocation.getSolarSystemId();}

	@Override
	public GetUniverseStationsStationIdOk getStation() {return this.spaceLocation.getStation();}

	@Override
	public String getStationName() {return this.spaceLocation.getStationName();}

	public void setStation( final GetUniverseStationsStationIdOk station ) {
		this.spaceLocation.setStation( station );
	}

	// - B U I L D E R
	public static class Builder {
		private AssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new AssetContainer();
		}

		public AssetContainer.Builder withSpaceLocation( final SpaceLocation spaceLocation ) {
			Objects.requireNonNull( spaceLocation );
			this.onConstruction.spaceLocation = spaceLocation;
			return this;
		}

		public AssetContainer build() {
			Objects.requireNonNull( this.onConstruction.spaceLocation );
			return this.onConstruction;
		}
	}
}