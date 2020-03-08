package org.dimensinfin.eveonline.neocom.service.scheduler.converter;

public class JobToJobRecordConverter {
	private JobToJobRecordConverter() {}

	// - B U I L D E R
	public static class Builder {
		private JobToJobRecordConverter onConstruction;

		public Builder() {
			this.onConstruction = new JobToJobRecordConverter();
		}

		public JobToJobRecordConverter build() {
			return this.onConstruction;
		}
	}
}