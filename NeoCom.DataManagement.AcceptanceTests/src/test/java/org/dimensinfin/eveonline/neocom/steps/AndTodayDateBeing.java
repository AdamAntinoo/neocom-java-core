package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;

public class AndTodayDateBeing {
	private MiningExtractionsWorld miningExtractionsWorld;

	@Autowired
	public AndTodayDateBeing( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
	}

	@And("today date being {string}")
	public void theTodayDateBeing(final  String todayDate ) {
		this.miningExtractionsWorld.setTodayDate(new LocalDate(todayDate));
	}
}

