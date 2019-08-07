package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;

public class AndTheTodayDateBeing {
	private MiningExtractionsWorld miningExtractionsWorld;

	@Autowired
	public AndTheTodayDateBeing( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
		// Connect the item to the adapter
		EveItem.injectEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@And("the today date being {string}")
	public void theTodayDateBeing(final  String todayDate ) {
		this.miningExtractionsWorld.setTodayDate(new DateTime(todayDate));
	}
}
