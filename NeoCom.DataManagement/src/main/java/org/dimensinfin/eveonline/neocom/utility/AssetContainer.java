package org.dimensinfin.eveonline.neocom.utility;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;

public class AssetContainer {
	private List<NeoAsset> contents = new ArrayList<>();

	public int addContent( final NeoAsset item ) {
		this.contents.add( item );
		return this.contents.size();
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
	}
}