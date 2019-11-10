package org.dimensinfin.eveonline.neocom.integration.support;

public class IntegrationCredentialSupport {
	private IntegrationCredentialSupport() {}

	// - B U I L D E R
	public static class Builder {
		private IntegrationCredentialSupport onConstruction;

		public Builder() {
			this.onConstruction = new IntegrationCredentialSupport();
		}

		public IntegrationCredentialSupport build() {
			return this.onConstruction;
		}
	}
}
