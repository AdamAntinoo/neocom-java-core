package org.dimensinfin.eveonline.neocom.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public class AssetContainer {
	public enum AssetContainerType {
		UNDEFINED, SPACE, CONTAINER, SHIP, UNKNOWN;
	}

	private AssetContainerType type = AssetContainerType.UNDEFINED;
	private LocationIdentifier spaceLocationIdentifier;
	private SpaceLocation spaceLocation;
	private NeoAsset parentContainer;
	private List<NeoAsset> contents = new ArrayList<>();

	protected AssetContainer() {}

	public int addContent( final NeoAsset item ) {
		this.contents.add( item );
		return this.contents.size();
	}

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

		public AssetContainer.Builder withSpaceLocationIdentifier( final LocationIdentifier spaceLocationIdentifier ) {
			Objects.requireNonNull( spaceLocationIdentifier );
			this.onConstruction.spaceLocationIdentifier = spaceLocationIdentifier;
			this.onConstruction.type = AssetContainerType.UNKNOWN;
			return this;
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

		public AssetContainer build() {
//			Objects.requireNonNull( this.onConstruction.spaceLocation );
			return this.onConstruction;
		}
	}
}
