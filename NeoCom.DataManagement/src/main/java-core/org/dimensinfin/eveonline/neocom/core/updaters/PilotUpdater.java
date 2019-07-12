package org.dimensinfin.eveonline.neocom.core.updaters;


import org.dimensinfin.eveonline.neocom.domain.Pilot;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class PilotUpdater extends NeoComUpdater<Pilot> {
	private static final long PILOT_CACHE_TIME = TimeUnit.DAYS.toMillis(1);

	public PilotUpdater( final Pilot model ) {
		super(model);
	}

	// - N E O C O M U P D A T E R
	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus(PILOT_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return this.getModel().getJsonClass().toUpperCase() + ":" + this.getModel().getPilotIdentifier();
	}

	@Override
	public void onRun() {
		if (null != esiDataAdapter) {
			// Get the corporation data if the corporation is defined.
			final Integer corpIdentifier = this.getModel().getCorporationId();
			if ( null != corpIdentifier) {
				logger.info("-- [GlobalDataManager.requestAllianceV1]> ESI Compatible. Download corporation information.");
				final GetCorporationsCorporationIdOk corporationData = esiDataAdapter.getCorporationsCorporationId(corpIdentifier);
				if ( null != corporationData)
					this.getModel().setCorporationData(corporationData);
				final GetCorporationsCorporationIdIconsOk corporationIcons = esiDataAdapter.getCorporationsCorporationIdIcons(corpIdentifier);
				if ( null != corporationIcons)
					this.getModel().setCorporationIconUrl(corporationIcons.getPx256x256());
			}

			final  Integer allianceIdentifier = this.getModel().getAllianceId();
			if ( null != allianceIdentifier) {
				logger.info("-- [GlobalDataManager.requestAllianceV1]> ESI Compatible. Download corporation information.");
				final GetAlliancesAllianceIdOk allianceData = esiDataAdapter.getAlliancesAllianceId(allianceIdentifier);
				if ( null != allianceData)
					this.getModel().setAllianceData(allianceData);
				final GetCorporationsCorporationIdIconsOk allianceIcons = esiDataAdapter.getCorporationsCorporationIdIcons(corpIdentifier);
				if ( null != allianceIcons)
					this.getModel().setAllianceIconUrl(allianceIcons.getPx256x256());
			}
			this.getModel().timeStamp();
		}
	}
}
