package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;

public class AndTheSelectedDateBeing {
	private MiningExtractionsWorld miningExtractionsWorld;

	@Autowired
	public AndTheSelectedDateBeing( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@And("the selected date being {string}")
	public void theTodayDateBeing( final String todayDate ) {
		this.miningExtractionsWorld.setTodayDate(new LocalDate(todayDate));
	}
}

