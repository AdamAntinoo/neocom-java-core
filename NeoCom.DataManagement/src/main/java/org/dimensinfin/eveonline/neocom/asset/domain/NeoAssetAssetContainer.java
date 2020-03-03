package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;

public class NeoAssetAssetContainer extends ExpandableContainer<NeoAsset> implements IAssetContainer {
	private AssetContainerType type = AssetContainerType.CONTAINER;
	//	private SpaceLocation spaceLocation;
	private NeoAsset containerFace;

	protected NeoAssetAssetContainer() {super();}

	public AssetContainerType getType() {
		return this.type;
	}

//	public SpaceLocation getSpaceLocation() {
//		return this.spaceLocation;
//	}

	public NeoAsset getContainerFace() {
		return this.containerFace;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoAssetAssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new NeoAssetAssetContainer();
		}

		public NeoAssetAssetContainer build() {
			return this.onConstruction;
		}

		public NeoAssetAssetContainer.Builder withFace( final NeoAsset asset ) {
			Objects.requireNonNull( asset );
			this.onConstruction.containerFace = asset;
			if (asset.isShip()) this.onConstruction.type = AssetContainerType.SHIP;
			if (asset.isContainer()) this.onConstruction.type = AssetContainerType.CONTAINER;
			return this;
		}

//		public NeoAssetAssetContainer.Builder withSpaceLocation( final SpaceLocation spaceLocation ) {
//			Objects.requireNonNull( spaceLocation );
//			this.onConstruction.spaceLocation = spaceLocation;
//			this.onConstruction.type = AssetContainerType.CONTAINER;
//			return this;
//		}
	}
}
