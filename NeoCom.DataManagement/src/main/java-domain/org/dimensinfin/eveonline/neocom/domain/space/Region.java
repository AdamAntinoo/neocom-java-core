package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class Region implements SpaceRegion {
	private Integer regionId;
	private GetUniverseRegionsRegionIdOk region;
	private List<AssetContainer> contents = new ArrayList<>();
//	private AssetAggregator aggregator = new AssetAggregator();

	private Region() {}

	// - S P A C E R E G I O N
	@Override
	public Integer getRegionId() {
		return this.regionId;
	}

	@Override
	public GetUniverseRegionsRegionIdOk getRegion() {
		return this.region;
	}

	@Override
	public String getRegionName() {
		return this.region.getName();
	}

	// -  C O N T E N T
	public int addContent( final AssetContainer item ) {
		this.contents.add( item );
//		this.aggregator.aggregate(item);
		return this.contents.size();
	}

	public int getContentCount() {
		return this.contents.size();
	}

	public boolean isEmpty() {
		return this.contents.size() > 0;
	}

	// - B U I L D E R
	public static class Builder {
		private Region onConstruction;

		public Builder() {
			this.onConstruction = new Region();
		}

		public Region.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public Region build() {
			Objects.requireNonNull( this.onConstruction.region );
			return this.onConstruction;
		}
	}
}