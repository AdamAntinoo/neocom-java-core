package org.dimensinfin.eveonline.neocom.support;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;

public class SupportStoreCacheManager extends StoreCacheManager {
	private RetrofitUniverseConnector noAuthRetrofitConnector;

	private SupportStoreCacheManager() {}

	// - B U I L D E R
	public static class Builder {
		private SupportStoreCacheManager onConstruction;

		public Builder() {
			this.onConstruction = new SupportStoreCacheManager();
		}

		public SupportStoreCacheManager.Builder withNoAuthRetrofitConnector( final RetrofitUniverseConnector noAuthRetrofitConnector ) {
			Objects.requireNonNull( noAuthRetrofitConnector );
			this.onConstruction.noAuthRetrofitConnector = noAuthRetrofitConnector;
			return this;
		}
		public SupportStoreCacheManager build() {
			return this.onConstruction;
		}
	}
}