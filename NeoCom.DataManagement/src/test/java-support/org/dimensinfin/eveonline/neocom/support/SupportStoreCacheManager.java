package org.dimensinfin.eveonline.neocom.support;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapters.StoreCacheManager;

import retrofit2.Retrofit;

public class SupportStoreCacheManager extends StoreCacheManager {
	private Retrofit noAuthRetrofitConnector;

	private SupportStoreCacheManager() {}

	// - B U I L D E R
	public static class Builder {
		private SupportStoreCacheManager onConstruction;

		public Builder() {
			this.onConstruction = new SupportStoreCacheManager();
		}

		public SupportStoreCacheManager.Builder withNoAuthRetrofitConnector( final Retrofit noAuthRetrofitConnector ) {
			Objects.requireNonNull( noAuthRetrofitConnector );
			this.onConstruction.noAuthRetrofitConnector = noAuthRetrofitConnector;
			return this;
		}
		public SupportStoreCacheManager build() {
			return this.onConstruction;
		}
	}
}