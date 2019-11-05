package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

public class Schedule {
	public Schedule() {}

	// - B U I L D E R
	public static class Builder {
		private Schedule onConstruction;

		public Builder() {
			this.onConstruction = new Schedule();
		}

		public Schedule build() {
			return this.onConstruction;
		}
	}
}