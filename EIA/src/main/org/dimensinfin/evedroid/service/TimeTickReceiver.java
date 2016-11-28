//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.service;

import java.io.BufferedReader;
//- IMPORT SECTION .........................................................................................
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EDataBlock;
import org.dimensinfin.evedroid.core.ERequestClass;
import org.dimensinfin.evedroid.core.ERequestState;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Outpost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import net.nikr.eve.jeveasset.data.Citadel;

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
	private static Logger		logger					= Logger.getLogger("TimeTickReceiver");
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
			Collections.sort(requests,
					EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_REQUEST_PRIORITY));

			// Process request by priority. Additions to queue are limited.
			limit = 0;
			for (PendingRequestEntry entry : requests) {
				if (entry.state == ERequestState.PENDING) {
					// Filter only MARKETDATA requests.
					if (entry.reqClass == ERequestClass.MARKETDATA) if (limit <= LAUNCH_LIMIT) {
						launchMarketUpdate(entry);
					}
					// Filter the rest of the character data to be updated
					if (entry.reqClass == ERequestClass.CHARACTERUPDATE) {
						launchCharacterDataUpdate(entry);
					}
					if (entry.reqClass == ERequestClass.CITADELUPDATE) {
						citadelLocationUpdate();
					}
					if (entry.reqClass == ERequestClass.OUTPOSTUPDATE) {
						outpostLocationUpdate();
					}
				}
			}
		}

		// STEP 02. Check characters for pending structures to update.
		ArrayList<EveChar> characters = EVEDroidApp.getAppStore().getActiveCharacters();
		for (EveChar eveChar : characters) {
			EDataBlock updateCode = eveChar.needsUpdate();
			if (updateCode != EDataBlock.READY) {
				Log.i("EVEI Service",
						".. TimeTickReceiver.onReceive.EDataBlock to update: " + eveChar.getName() + " - " + updateCode);
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

	private void citadelLocationUpdate() {
		logger.info(">> [TimeTicketReceiver.citadelLocationUpdate]> Citadels updating");
		String destination = "https://stop.hammerti.me.uk/api/citadel/all";
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
			}

			public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };
		// Install the all-trusting trust manager
		SSLContext sc;
		InputStream in = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(final String hostname, final SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			ObjectMapper mapper = new ObjectMapper(); //create once, reuse
			URL url = new URL(destination);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip");
			long contentLength = con.getContentLength();
			String contentEncoding = con.getContentEncoding();
			InputStream inputStream = con.getInputStream();
			if ("gzip".equals(contentEncoding)) {
				in = new GZIPInputStream(inputStream);
			} else {
				in = inputStream;
			}
			Map<Long, Citadel> results = mapper.readValue(in, new TypeReference<Map<Long, Citadel>>() {
			});
			if (results != null) { //Updated OK
				for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
					// Convert each Citadel to a new Location and update the database if needed.
					EveLocation loc = new EveLocation(entry.getKey(), entry.getValue());
					//					citadelSettings.put(entry.getKey(), entry.getValue());
					//					saveCitadel(entry.getKey(), entry.getValue());
				}
			}

			//			final Reader reader = new InputStreamReader(con.getInputStream());
			//			final BufferedReader br = new BufferedReader(reader);
			//			String line = "";
			//			while ((line = br.readLine()) != null) {
			//				System.out.println(line);
			//			}
			//			br.close();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					//No problem...
				}
			}
		}

		//		 CitadelSettings	citadelSettings	= new CitadelSettings();
		//		if (citadelSettings.getNextUpdate().after(new Date()) && true && true) { //Check if we can update now
		//			//				if (updateTask != null) {
		//			//					updateTask.addError(DialoguesUpdate.get().citadel(), "Not allowed yet.\r\n(Fix: Just wait a bit)");
		//			//				}
		//			logger.info("	Citadels failed to update (NOT ALLOWED YET)");
		//			return;
		//		}
		// Update citadel
		//		InputStream in = null;
		//		try { //Update from API
		//			ObjectMapper mapper = new ObjectMapper(); //create once, reuse
		//			URL url = new URL("https://stop.hammerti.me.uk/api/citadel/all");
		//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
		//			con.setRequestProperty("Accept-Encoding", "gzip");
		//
		//			long contentLength = con.getContentLength();
		//			String contentEncoding = con.getContentEncoding();
		//			InputStream inputStream = con.getInputStream();
		//			if ("gzip".equals(contentEncoding)) {
		//				in = new GZIPInputStream(inputStream);
		//			} else {
		//				in = inputStream;
		//			}
		//			Map<Long, Citadel> results = mapper.readValue(in, new TypeReference<Map<Long, Citadel>>() {
		//			});
		//			if (results != null) { //Updated OK
		//				for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
		//					// Convert each Citadel to a new Location and update the database if needed.
		//					EveLocation loc = new EveLocation(entry.getKey(), entry.getValue());
		//					//					citadelSettings.put(entry.getKey(), entry.getValue());
		//					//					saveCitadel(entry.getKey(), entry.getValue());
		//				}
		//			}
		//			//			citadelSettings.setNextUpdate();
		//			//				saveCitadel(citadelSettings);
		//			logger.info("	Updated citadels for jEveAssets");
		//		} catch (IOException ex) {
		//			//				if (updateTask != null) {
		//			//					updateTask.addError(DialoguesUpdate.get().citadel(), ex.getMessage());
		//			//				}
		//			//				logger.("	Citadels failed to update", ex);
		//		} finally {
		//			if (in != null) {
		//				try {
		//					in.close();
		//				} catch (IOException ex) {
		//					//No problem...
		//				}
		//			}
		//		}
	}

	private void launchCharacterDataUpdate(final PendingRequestEntry entry) {
		Log.i("EVEI Service",
				".. TimeTickReceiver.launchCharacterDataUpdate Character Update Request Class [" + entry.reqClass + "]");
		Intent serialService = new Intent(_context, CharacterUpdaterService.class);
		Number content = entry.getContent();
		serialService.putExtra(AppWideConstants.extras.EXTRA_CHARACTER_LOCALIZER, content.longValue());
		if (null != _context) {
			_context.startService(serialService);
		}
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

	private void outpostLocationUpdate() {
		// Check if the outpotst already loaded.
		//	if ((null == outpostsCache) || (outpostsCache.size() < 1)) {
		// Making a request to url and getting response
		String jsonStr = readJsonData();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			// Getting JSON Array node
			JSONArray outposts = jsonObj.getJSONArray("items");
			// Looping through all outposts
			int counter = 1;
			for (int i = 0; i < outposts.length(); i++) {
				Outpost o = new Outpost();
				JSONObject post = outposts.getJSONObject(i);
				int id = post.getInt("facilityID");
				o.setFacilityID(id);
				JSONObject intermediate = post.getJSONObject("solarSystem");
				o.setSolarSystem(intermediate.getLong("id"));
				o.setName(post.getString("name"));
				intermediate = post.getJSONObject("region");
				o.setRegion(intermediate.getLong("id"));
				intermediate = post.getJSONObject("owner");
				o.setOwner(intermediate.getLong("id"));
				intermediate = post.getJSONObject("type");
				o.setType(intermediate.getLong("id"));

				// Create the part with the Outpost
				EveLocation loc = new EveLocation(o);
				Log.i("DataSource", ".. Part counter " + counter++);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//		}
		// Search for the item
		//		Outpost hit = outpostsCache.get(Long.valueOf(locationID).intValue());
		//		EveLocation location = new EveLocation(locationID);
		//		if (null != hit) {
		//			EveLocation systemLocation = searchLocationbyID(hit.getSolarSystem());
		//			location.setStation(hit.getName());
		//			location.setSystemID(hit.getSolarSystem());
		//			location.setSystem(systemLocation.getSystem());
		//			location.setConstellationID(systemLocation.getConstellationID());
		//			location.setConstellation(systemLocation.getConstellation());
		//			location.setRegionID(systemLocation.getRegionID());
		//			location.setRegion(systemLocation.getRegion());
		//			location.setSecurity(systemLocation.getSecurity());
		//		}
		//		return location;
	}

	private String readJsonData() {
		StringBuffer data = new StringBuffer();
		try {
			String str = "";
			InputStream is = AppConnector.getStorageConnector().accessInternalStorage("outposts.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is != null) {
				while ((str = reader.readLine()) != null) {
					data.append(str);
				}
			}
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data.toString();
	}
}

// - UNUSED CODE ............................................................................................
