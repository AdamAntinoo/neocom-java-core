package org.dimensinfin.eveonline.neocom.support.adapters;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;

public class SupportLocationCatalogService extends LocationCatalogService {
	// - B U I L D E R
	public static class Builder {
		private SupportLocationCatalogService onConstruction;

		public Builder() {
			this.onConstruction = new SupportLocationCatalogService();
		}

		public SupportLocationCatalogService build() {
			return this.onConstruction;
		}
	}
}