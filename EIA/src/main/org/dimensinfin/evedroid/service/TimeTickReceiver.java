//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.service;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EDataBlock;
import org.dimensinfin.evedroid.core.ERequestClass;
import org.dimensinfin.evedroid.core.ERequestState;
import org.dimensinfin.evedroid.model.EveChar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class is a one minute timer activated at startup. It will search for data obsolete and fire the
 * services required to update that data by downloading new CCP api calls.<br>
 * There are two sets of services, one for character data and the other for market data that have quite
 * different mechanics. All data updates the background counters that inform the user that some background
 * services are active and the number of elements on their queues.
 * 
 * @author Adam Antinoo
 */
public class TimeTickReceiver extends BroadcastReceiver {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static boolean	BLOCKED_STATUS	= false;
	private static int			LAUNCH_LIMIT		= 30;

	// - F I E L D - S E C T I O N ............................................................................
	private Context					_context				= null;
	private int							limit						= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TimeTickReceiver(final Context context) {
		_context = context;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Receive an intent on every minute tick.<br>
	 * We should scan the list of pending data for queued requests and after termination we have to check for
	 * character structures that need update depending on their different valid periods.<br>
	 * Pending requests are processed by priority and the number of requests queued to the service is limited so
	 * more requests will be queued on the next tick if they are more than the queue limit.<br>
	 * The user data is refreshed on step two and the time limit is one hour.<br>
	 * The asset information is also updated on step 2 but with a duration of 8 hours. <br>
	 * Every minute the process checks for pending market data downloads.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.i("EVEI Service", ">> TimeTickReceiver.onReceive");
		// Run only if the network is active.
		if (!EVEDroidApp.checkNetworkAccess()) return;
		// Or if the service is disables by configuration.
		if (blockedDownload()) return;

		// STEP 01. Pending Market Data Requests
		// Get requests pending from the queue service.
		Vector<PendingRequestEntry> requests = EVEDroidApp.getTheCacheConnector().getPendingRequests();
		synchronized (requests) {
			// Get the pending requests and order them by the priority.
			Collections
					.sort(requests, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_REQUEST_PRIORITY));

			// Process request by priority. Additions to queue are limited.
			limit = 0;
			for (PendingRequestEntry entry : requests) {
				if (entry.state == ERequestState.PENDING) {
					// Filter only MARKETDATA requests.
					if (entry.reqClass == ERequestClass.MARKETDATA) if (limit <= LAUNCH_LIMIT) launchMarketUpdate(entry);
					// Filter the rest of the character data to be updated
					if (entry.reqClass == ERequestClass.CHARACTERUPDATE) launchCharacterDataUpdate(entry);
				}
			}
		}

		// STEP 02. Check characters for pending structures to update.
		ArrayList<EveChar> characters = EVEDroidApp.getAppStore().getActiveCharacters();
		for (EveChar eveChar : characters) {
			EDataBlock updateCode = eveChar.needsUpdate();
			if (updateCode != EDataBlock.READY) {
				Log.i("EVEI Service", ".. TimeTickReceiver.onReceive.EDataBlock to update: " + eveChar.getName() + " - "
						+ updateCode);
				EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(eveChar.getCharacterID());
			}
		}
		Activity activity = EVEDroidApp.getAppStore().getActivity();
		if (null != activity) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					EVEDroidApp.updateProgressSpinner();
				}
			});
		}
		Log.i("EVEI Service", "<< TimeTickReceiver.onReceive [" + requests.size() + " - " + EVEDroidApp.marketCounter + "/"
				+ EVEDroidApp.topCounter + "]");
	}

	private boolean blockedDownload() {
		// Read the flag values from the preferences.
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(_context);
		boolean blockDownload = sharedPrefs.getBoolean(AppWideConstants.preference.PREF_BLOCKDOWNLOAD, false);
		return blockDownload;
	}

	private void launchCharacterDataUpdate(final PendingRequestEntry entry) {
		Log.i("EVEI Service", ".. TimeTickReceiver.launchCharacterDataUpdate Character Update Request Class ["
				+ entry.reqClass + "]");
		Intent serialService = new Intent(_context, CharacterUpdaterService.class);
		Number content = entry.getContent();
		serialService.putExtra(AppWideConstants.extras.EXTRA_CHARACTER_LOCALIZER, content.longValue());
		if (null != _context) _context.startService(serialService);
		entry.state = ERequestState.ON_PROGRESS;
		// Increment the counter.
		EVEDroidApp.topCounter++;
	}

	private void launchMarketUpdate(final PendingRequestEntry entry) {
		Log.i("EVEI Service", "-- TimeTickReceiver.launchService Market Update Request Class [" + entry.reqClass + "]");
		if (null != _context) {
			Intent serialService = new Intent(_context, MarketUpdaterService.class);
			Number content = entry.getContent();
			Log.i("EVEI Service", ".. TimeTickReceiver.launchMarketUpdate Posting update. Item ID [" + content + "]");
			serialService.putExtra(AppWideConstants.extras.EXTRA_MARKETDATA_LOCALIZER, content.intValue());
			_context.startService(serialService);
			entry.state = ERequestState.ON_PROGRESS;
			// Increment the counter.
			EVEDroidApp.marketCounter++;
			limit++;
		}
	}
}

// - UNUSED CODE ............................................................................................
