package org.dimensinfin.eveonline.neocom.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.service.scheduler.HourlyCronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;

public class AssetProcessorIT {
	private JobScheduler jobScheduler;

	@BeforeEach
	void setUpEnvironment() {
		this.jobScheduler = new JobScheduler.Builder()
				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() ).build();
	}

	void registerJobOnScheduler() {

	}

	@Test
	public void downloadAssets() {
	}
	//	private AssetProcessorIT() {}
//
//	// - B U I L D E R
//	public static class Builder {
//		private AssetProcessorIT onConstruction;
//
//		public Builder() {
//			this.onConstruction = new AssetProcessorIT();
//		}
//
//		public AssetProcessorIT build() {
//			return this.onConstruction;
//		}
//	}
}