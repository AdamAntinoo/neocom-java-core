package org.dimensinfin.eveonline.neocom.updater;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

public class CredentialUpdater extends NeoComUpdater<Credential> {
	private static final long CREDENTIAL_CACHE_TIME = TimeUnit.MINUTES.toMillis( 15 );

	public CredentialUpdater( final Credential model ) {
		super( model );
	}

	// - N E O C O M U P D A T E R
	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus( CREDENTIAL_CACHE_TIME ).isBefore( DateTime.now() ))
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
				final List<GetCharactersCharacterIdAssets200Ok> assetList = esiDataAdapter.getCharactersCharacterIdAssets(
						this.getModel().getAccountId(),
						this.getModel().getRefreshToken(),
						this.getModel().getDataSource() );
				this.getModel().setAssetsCount( assetList.size() );

				// Estimate the mining resources value.
				// TODO - Do this but form a directed wuery to the asset repository.
//				final Double miningResourcesValue = Stream.of( assetList )
//						.filter( asset -> this.isMiningResource( asset ) )
//						.map( asset -> new NeoAsset.Builder().fromEsiAsset( asset ) )
//						.mapToDouble( asset -> asset.getPrice() * asset.getQuantity() )
//						.sum();
//				if (miningResourcesValue > 0.0) this.getModel().setMiningResourcesEstimatedValue( miningResourcesValue );

				// Get the wallet balance.
				final Double walletBalance = esiDataAdapter.getCharactersCharacterIdWallet( this.getModel().getAccountId()
						, this.getModel().getRefreshToken(), this.getModel().getDataSource() );
				if (walletBalance > 0.0) this.getModel().setWalletBalance( walletBalance );

				// Get the race name. This needs access to the pilot.
				final GetCharactersCharacterIdOk pilotPublicData = esiDataAdapter
						.getCharactersCharacterId( this.getModel().getAccountId() );
				final GetUniverseRaces200Ok raceData = esiDataAdapter.searchSDERace( pilotPublicData.getRaceId() );
				if (null != raceData) this.getModel().setRaceName( raceData.getName() );
			}
			this.getModel().timeStamp(); // Mark the model as updated.
		}
	}

	private boolean isMiningResource( final GetCharactersCharacterIdAssets200Ok asset2Test ) {
//		if (asset2Test.getCategoryName().equalsIgnoreCase( "Asteroid" )) return true;
//		if ((asset2Test.getCategoryName().equalsIgnoreCase( "Material" )) &&
//				(asset2Test.getGroupName().equalsIgnoreCase( "Mineral" ))) return true;
		return false;
	}
}
