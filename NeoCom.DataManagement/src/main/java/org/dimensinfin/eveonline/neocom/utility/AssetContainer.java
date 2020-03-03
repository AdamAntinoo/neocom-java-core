package org.dimensinfin.eveonline.neocom.utility;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public class AssetContainer extends ExpandableContainer<NeoAsset> {
	public enum AssetContainerType {
		UNDEFINED, SPACE, CONTAINER, SHIP, UNKNOWN;
	}

	private AssetContainerType type = AssetContainerType.UNDEFINED;
//	private LocationIdentifier spaceLocationIdentifier;
	private SpaceLocation spaceLocation;
	private NeoAsset parentContainer;

	protected AssetContainer() {super();}

	public AssetContainerType getType() {
		return this.type;
	}

	public SpaceLocation getSpaceLocation() {
		return this.spaceLocation;
	}

	// - B U I L D E R
	public static class Builder {
		private AssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new AssetContainer();
		}

		public AssetContainer build() {
			return this.onConstruction;
		}

		public AssetContainer.Builder withAsset( final NeoAsset asset ) {
			Objects.requireNonNull( asset );
			this.onConstruction.parentContainer = asset;
			if (asset.isShip()) this.onConstruction.type = AssetContainerType.SHIP;
			if (asset.isContainer()) this.onConstruction.type = AssetContainerType.CONTAINER;
			return this;
		}

		public AssetContainer.Builder withSpaceLocation( final SpaceLocation spaceLocation ) {
			Objects.requireNonNull( spaceLocation );
			this.onConstruction.spaceLocation = spaceLocation;
			this.onConstruction.type = AssetContainerType.SPACE;
			return this;
		}

//		public AssetContainer.Builder withSpaceLocationIdentifier( final LocationIdentifier spaceLocationIdentifier ) {
//			Objects.requireNonNull( spaceLocationIdentifier );
//			this.onConstruction.spaceLocationIdentifier = spaceLocationIdentifier;
//			this.onConstruction.type = AssetContainerType.UNKNOWN;
//			return this;
//		}
	}
}
