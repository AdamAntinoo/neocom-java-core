package org.dimensinfin.eveonline.neocom.core.updaters;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

import org.joda.time.DateTime;

public class CredentialUpdater extends NeoComUpdater<Credential> {
	private static final long CREDENTIAL_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);
	private ESIDataAdapter esiDataAdapter;

	public CredentialUpdater( final Credential model ) {
		super(model);
	}

	@Override
	public boolean needsRefresh() {
		if (null == this.getModel().getLastUpdateTime()) return true;
		if (this.getModel().getLastUpdateTime().plus(CREDENTIAL_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	// - N E O C O M U P D A T E R
	@Override
	public String getIdentifier() {
		return "CRD:" + this.getModel().getAccountId();
	}

	/**
	 * Execute the specific tasks that need to be completed to update the Credential additional information.
	 * There are two tasks: one to update the number of assets and another to get the wallet amount.
	 *
	 * @return the updater instance with any fields updated.
	 */
	@Override
	public NeoComUpdater run() {
		if ( null != this.esiDataAdapter) {
			// Count the number of assets.
			final List<GetCharactersCharacterIdAssets200Ok> assetList = this.esiDataAdapter.getCharactersCharacterIdAssets(this.getModel().getAccountId()
					, this.getModel().getRefreshToken(), this.getModel().getDataSource());
			this.getModel().setAssetsCount(assetList.size());

			// Get the wallet balance.
			final Double walletBalance = this.esiDataAdapter.getCharactersCharacterIdWallet(this.getModel().getAccountId()
					, this.getModel().getRefreshToken(), this.getModel().getDataSource());
			if (walletBalance > 0.0) this.getModel().setWalletBalance(walletBalance);
		}
		return this;
	}

//	@Override
//	public boolean sendChangeEvent( final String eventName ) {
//		return this.se
//	}
}
