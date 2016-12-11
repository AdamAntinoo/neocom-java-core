//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.service;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EDataBlock;
import org.dimensinfin.evedroid.model.NeoComCharacter;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class CharacterUpdaterService extends IntentService {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CharacterUpdaterService() {
		super("CharacterUpdaterService");
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * We can process two types of events. <code>CHARACTERDATA</code> where we update frequent character
	 * information and <code>ASSETDATA</code> that is the call to down the heavy load of the list of character
	 * assets.<br>
	 * The new blueprints will be added with the assets of a first approach but maybe they will be set on their
	 * own type.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.i("CharacterUpdaterService", ">> CharacterUpdaterService.onHandleIntent");
		Long localizer = (Long) intent.getSerializableExtra(AppWideConstants.extras.EXTRA_CHARACTER_LOCALIZER);
		// Be sure we have access to the network. Otherwise intercept the exceptions.
		if (EVEDroidApp.checkNetworkAccess()) {
			NeoComCharacter pilot = EVEDroidApp.getAppStore().searchCharacter(localizer);
			if (null != pilot) {
				// Pilot signaled for update. Locate the next data set to update because its cache has expired.
				EDataBlock datacode = pilot.needsUpdate();
				try {
					Log.i("CharacterUpdaterService", ".. CharacterUpdaterService.onHandleIntent - EDataBlock to process: "
							+ pilot.getName() + " - " + datacode);
					switch (datacode) {
						case CHARACTERDATA:
							pilot.updateCharacterInfo();
							EVEDroidApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
							EVEDroidApp.topCounter--;
							break;
						case ASSETDATA:
						case BLUEPRINTDATA:
							pilot.downloadAssets();
							pilot.downloadBlueprints();
							EVEDroidApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
							EVEDroidApp.topCounter--;
							break;
						case INDUSTRYJOBS:
							pilot.updateIndustryJobs();
							EVEDroidApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
							EVEDroidApp.topCounter--;
							break;
						case MARKETORDERS:
							pilot.updateMarketOrders();
							EVEDroidApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
							EVEDroidApp.topCounter--;
							break;

						default:
							break;
					}
					// Clean the top counter if completed.
					if (EVEDroidApp.topCounter < 0) {
						EVEDroidApp.topCounter = 0;
					}
				} catch (RuntimeException rtex) {
				}
			}
			// Relaunch more jobs if completed.
			EVEDroidApp.runTimer();
		}
		Log.i("CharacterUpdaterService", "<< CharacterUpdaterService.onHandleIntent");
	}
}

// - UNUSED CODE ............................................................................................
