package org.dimensinfin.eveonline.neocom.support;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapters.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

public class SupportStoreCacheManager extends StoreCacheManager {
	private ESIUniverseDataProvider esiUniverseDataProvider;

	private SupportStoreCacheManager() {}

	// - B U I L D E R
	public static class Builder {
		private SupportStoreCacheManager onConstruction;

		public Builder() {
			this.onConstruction = new SupportStoreCacheManager();
		}

		public SupportStoreCacheManager.Builder withESIUniverseDataProvider( final ESIUniverseDataProvider esiUniverseDataProvider ) {
			Objects.requireNonNull( esiUniverseDataProvider );
			this.onConstruction.esiUniverseDataProvider = esiUniverseDataProvider;
			return this;
		}
		public SupportStoreCacheManager build() {
			return this.onConstruction;
		}
	}
}