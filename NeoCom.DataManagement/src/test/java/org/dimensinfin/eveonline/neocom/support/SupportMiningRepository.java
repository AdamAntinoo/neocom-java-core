package org.dimensinfin.eveonline.neocom.support;

public class SupportMiningRepository {
	private SupportMiningRepository() {}

	// - B U I L D E R
	public static class Builder {
		private SupportMiningRepository onConstruction;

		public Builder() {
			this.onConstruction = new SupportMiningRepository();
		}

		public SupportMiningRepository build() {
			return this.onConstruction;
		}
	}
}
