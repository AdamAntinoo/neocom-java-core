package org.dimensinfin.eveonline.neocom.core.updaters;

import org.dimensinfin.eveonline.neocom.planetary.ColonyPack;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class ColonyPackUpdater extends NeoComUpdater<ColonyPack> {
	private static final long COLONY_CACHE_TIME = TimeUnit.HOURS.toMillis(1);

	public ColonyPackUpdater( final ColonyPack model ) {
		super(model);
	}

	// - N E O C O M U P D A T E R
	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus(COLONY_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return this.getModel().getJsonClass().toUpperCase() + ":" + this.getModel().getPlanetId();
	}

	@Override
	public void onRun() {
		if (null != esiDataAdapter) {
			// Get the total volume and the total value
			this.getModel().setTotalVolume(this.getModel().getResourcesVolume());
			this.getModel().setTotalValue(this.getModel().getResourcesMarketValue());
			this.getModel().timeStamp(); // Mark the model as updated.
		}
	}
}
