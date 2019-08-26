package org.dimensinfin.eveonline.neocom.test.support;

import org.joda.time.LocalDate;

public class SharedWorld {
	private LocalDate todayDate;

	public LocalDate getTodayDate() {
		return this.todayDate;
	}

	public SharedWorld setTodayDate( final LocalDate todayDate ) {
		this.todayDate = todayDate;
		return this;
	}
}