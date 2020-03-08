package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

public class JobRecord {
	private JobRecord() {}

	// - B U I L D E R
	public static class Builder {
		private JobRecord onConstruction;

		public Builder() {
			this.onConstruction = new JobRecord();
		}

		public JobRecord build() {
			return this.onConstruction;
		}
	}
}