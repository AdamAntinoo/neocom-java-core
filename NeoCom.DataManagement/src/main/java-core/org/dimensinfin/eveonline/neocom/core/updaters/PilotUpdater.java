package org.dimensinfin.eveonline.neocom.core.updaters;


import org.dimensinfin.eveonline.neocom.domain.Pilot;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.joda.time.DateTime;

import java.util.List;
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
			if (this.getModel().getCredential().isValid()) { // Credential data and race information
				// Count the number of assets.
				final List<GetCharactersCharacterIdAssets200Ok> assetList = esiDataAdapter.getCharactersCharacterIdAssets(
						this.getModel().getCredential().getAccountId(),
						this.getModel().getCredential().getRefreshToken(),
						this.getModel().getCredential().getDataSource());
				this.getModel().getCredential().setAssetsCount(assetList.size());

				// Get the wallet balance.
				final Double walletBalance = esiDataAdapter.getCharactersCharacterIdWallet(
						this.getModel().getCredential().getAccountId(),
						this.getModel().getCredential().getRefreshToken(),
						this.getModel().getCredential().getDataSource());
				if (walletBalance > 0.0) this.getModel().getCredential().setWalletBalance(walletBalance);

				// Get the race name. This needs access to the pilot.
				final GetUniverseRaces200Ok raceData = esiDataAdapter.searchSDERace(this.getModel().getRaceId());
				if (null != raceData){
					this.getModel().getCredential().setRaceName(raceData.getName());
					this.getModel().setRaceData(raceData);
				}
			}

			// Get the corporation data if the corporation is defined.
			final Integer corpIdentifier = this.getModel().getCorporationId();
			if (null != corpIdentifier) { // Corporation data.
				logger.info("-- [GlobalDataManager.requestAllianceV1]> ESI Compatible. Download corporation information.");
				final GetCorporationsCorporationIdOk corporationData = esiDataAdapter.getCorporationsCorporationId(corpIdentifier);
				if (null != corporationData)
					this.getModel().setCorporationData(corporationData);
				final GetCorporationsCorporationIdIconsOk corporationIcons = esiDataAdapter.getCorporationsCorporationIdIcons(corpIdentifier);
				if (null != corporationIcons)
					this.getModel().setCorporationIconUrl(corporationIcons.getPx256x256());
			}

			final Integer allianceIdentifier = this.getModel().getAllianceId();
			if (null != allianceIdentifier) { // Alliance data.
				logger.info("-- [GlobalDataManager.requestAllianceV1]> ESI Compatible. Download corporation information.");
				final GetAlliancesAllianceIdOk allianceData = esiDataAdapter.getAlliancesAllianceId(allianceIdentifier);
				if (null != allianceData)
					this.getModel().setAllianceData(allianceData);
				final GetAlliancesAllianceIdIconsOk allianceIcons = esiDataAdapter.getAlliancesAllianceIdIcons(corpIdentifier);
				if (null != allianceIcons)
					this.getModel().setAllianceIconUrl(allianceIcons.getPx128x128());
			}
			this.getModel().timeStamp();
		}
	}
}
