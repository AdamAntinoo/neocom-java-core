package org.dimensinfin.eveonline.neocom.domain;

import java.util.concurrent.TimeUnit;

public enum PilotDataSections {
	  PILOT_PUBLICDATA(TimeUnit.SECONDS.toMillis(3600))
	, PILOT_CORPORATION(TimeUnit.DAYS.toMillis(1))
	, PILOT_ALLIANCE(TimeUnit.DAYS.toMillis(1))
	, PILOT_RACE(TimeUnit.DAYS.toMillis(1))
	, PILOT_BLOODLINE(TimeUnit.DAYS.toMillis(1))
	, PILOT_ANCESTRY(TimeUnit.DAYS.toMillis(1))
	;
	private final long cacheTime;

	PilotDataSections( final long cacheTime ) {
		this.cacheTime = cacheTime;
	}

	public int getCacheTime() {
		return Long.valueOf(this.cacheTime).intValue();
	}
}
