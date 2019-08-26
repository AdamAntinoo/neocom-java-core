package org.dimensinfin.eveonline.neocom.test.steps.shared;

import org.dimensinfin.eveonline.neocom.test.support.SharedWorld;
import org.joda.time.LocalDate;

import cucumber.api.java.en.And;

public class AndTodayDateBeing {
	private SharedWorld sharedWorld;

	public AndTodayDateBeing( final SharedWorld sharedWorld ) {
		this.sharedWorld = sharedWorld;
	}

	@And("today date being {string}")
	public void theTodayDateBeing( final String todayDate ) {
		if (todayDate.equalsIgnoreCase("<today>")) this.sharedWorld.setTodayDate(LocalDate.now());
		else this.sharedWorld.setTodayDate(new LocalDate(todayDate));
	}
}

