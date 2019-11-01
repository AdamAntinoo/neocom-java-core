package org.dimensinfin.eveonline.neocom.utility;

public class FacetedAssetContainer {
	// - B U I L D E R
	public static class Builder {
		private FacetedAssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new FacetedAssetContainer();
		}

		public FacetedAssetContainer build() {
			return this.onConstruction;
		}
	}
}