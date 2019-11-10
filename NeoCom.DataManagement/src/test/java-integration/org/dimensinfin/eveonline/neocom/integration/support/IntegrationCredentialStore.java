package org.dimensinfin.eveonline.neocom.integration.support;

public class IntegrationCredentialStore {
	private IntegrationCredentialStore() {}

	// - B U I L D E R
	public static class Builder {
		private IntegrationCredentialStore onConstruction;

		public Builder() {
			this.onConstruction = new IntegrationCredentialStore();
		}

		public IntegrationCredentialStore build() {
			return this.onConstruction;
		}
	}
}
