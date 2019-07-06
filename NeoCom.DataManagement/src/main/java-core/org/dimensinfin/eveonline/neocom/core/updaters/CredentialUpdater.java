package org.dimensinfin.eveonline.neocom.core.updaters;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

import org.joda.time.DateTime;

public class CredentialUpdater extends NeoComUpdater<Credential> {
	private static final long CREDENTIAL_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);

	public CredentialUpdater( final Credential model ) {
		super(model);
	}

	// - N E O C O M U P D A T E R
	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus(CREDENTIAL_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return this.getModel().getJsonClass().toUpperCase() + ":" + this.getModel().getAccountId();
	}

	/**
	 * Execute the specific tasks that need to be completed to update the Credential additional information.
	 * There are two tasks: one to update the number of assets and another to get the wallet amount.
	 */
	@Override
	public void onRun() {
		if (null != esiDataAdapter) {
			if (this.getModel().isValid()) {
				// Count the number of assets.
				final List<GetCharactersCharacterIdAssets200Ok> assetList = esiDataAdapter.getCharactersCharacterIdAssets(this.getModel().getAccountId()
						, this.getModel().getRefreshToken(), this.getModel().getDataSource());
				this.getModel().setAssetsCount(assetList.size());

				// Get the wallet balance.
				final Double walletBalance = esiDataAdapter.getCharactersCharacterIdWallet(this.getModel().getAccountId()
						, this.getModel().getRefreshToken(), this.getModel().getDataSource());
				if (walletBalance > 0.0) this.getModel().setWalletBalance(walletBalance);

				// Get the race name. This needs access to the pilot.
				this.getModel().setRaceName("-UNDEFINED-");
				// TODO Access the pilot data and update the race name
			}
			this.getModel().timeStamp(); // Mark the model as updated.
		}
	}
}
