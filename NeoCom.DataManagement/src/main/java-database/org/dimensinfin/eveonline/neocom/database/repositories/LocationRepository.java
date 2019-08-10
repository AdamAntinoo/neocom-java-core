package org.dimensinfin.eveonline.neocom.database.repositories;

public class LocationRepository {
	// - B U I L D E R
	public static class Builder {
		private LocationRepository onConstruction;

		public Builder() {
			this.onConstruction = new LocationRepository();
		}

		public LocationRepository build() {
			return this.onConstruction;
		}
	}
}