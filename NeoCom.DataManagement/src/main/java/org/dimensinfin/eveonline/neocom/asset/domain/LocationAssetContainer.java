package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public class LocationAssetContainer extends ExpandableContainer<NeoAsset> implements IAssetContainer {
	private AssetContainerType type = AssetContainerType.SPACE;
	private SpaceLocation spaceLocation;
//	private NeoAsset containerFace;

	protected LocationAssetContainer() {super();}

	public AssetContainerType getType() {
		return this.type;
	}

	public SpaceLocation getSpaceLocation() {
		return this.spaceLocation;
	}

//	public NeoAsset getContainerFace() {
//		return this.containerFace;
//	}

	// - B U I L D E R
	public static class Builder {
		private LocationAssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new LocationAssetContainer();
		}

		public LocationAssetContainer build() {
			return this.onConstruction;
		}

//		public LocationAssetContainer.Builder withFace( final NeoAsset asset ) {
//			Objects.requireNonNull( asset );
//			this.onConstruction.containerFace = asset;
//			if (asset.isShip()) this.onConstruction.type = AssetContainerType.SHIP;
//			if (asset.isContainer()) this.onConstruction.type = AssetContainerType.CONTAINER;
//			return this;
//		}

		public LocationAssetContainer.Builder withSpaceLocation( final SpaceLocation spaceLocation ) {
			Objects.requireNonNull( spaceLocation );
			this.onConstruction.spaceLocation = spaceLocation;
			this.onConstruction.type = AssetContainerType.SPACE;
			return this;
		}
	}
}
