//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid;

// - IMPORT SECTION .........................................................................................
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.connector.IConnector;
import org.dimensinfin.evedroid.connector.IDatabaseConnector;
import org.dimensinfin.evedroid.connector.IStorageConnector;
import org.dimensinfin.evedroid.core.AndroidCacheConnector;
import org.dimensinfin.evedroid.core.AndroidDatabaseConnector;
import org.dimensinfin.evedroid.core.AndroidStorageConnector;
import org.dimensinfin.evedroid.core.INeoComModelStore;
import org.dimensinfin.evedroid.interfaces.ICache;
import org.dimensinfin.evedroid.service.TimeTickReceiver;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class EVEDroidApp extends Application implements IConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger									= Logger.getLogger("EVEDroidApp");
	private static DecimalFormat			pendingCounter					= new DecimalFormat("0.0##");
	private static EVEDroidApp				singleton								= null;
	private static boolean						firstTimeInitialization	= false;
	private static BroadcastReceiver	timeTickReceiver				= null;
	public static int									topCounter							= 0;
	public static int									marketCounter						= 0;

	private static AppModelStore			appModelStore						= null;

	public static boolean checkNetworkAccess() {
		final ConnectivityManager cm = (ConnectivityManager) singleton.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ((netInfo != null) && netInfo.isConnectedOrConnecting()) return true;
		return false;
	}

	// public static EveDroidAppContext getAppContext() {
	// singleton = getSingletonApp();
	// if (null == singleton.appContext) // TODO This detects the app
	// reinitialization ??
	// singleton.appContext = new EveDroidAppContext();
	// return singleton.appContext;
	// }

	/**
	 * Return the file that points to the application folder on the external (SDCARD) storage.
	 */
	public static File getAppDirectory() {
		return new File(Environment.getExternalStorageDirectory(), AppConnector.getResourceString(R.string.appfoldername));
	}

	/**
	 * Gets a reference to the main app global data store. If the store is not defined it will start the
	 * procedure to retrieve its contents, first from the persistence storage on the sdcard and if this fails
	 * then it will start the procedure to create a new set of data from the available keys.
	 * 
	 * @return
	 */
	public static AppModelStore getAppStore() {
		//		if (null == appModelStore) {
		//			appModelStore = new AppModelStore(new UserModelPersistenceHandler());
		//			appModelStore.restore();
		//		}
		return AppModelStore.getSingleton();
	}

	public static boolean getBooleanPreference(final String preferenceName, final boolean defaultValue) {
		// Read the flag values from the preferences.
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppStore().getActivity());
		boolean pref = sharedPrefs.getBoolean(preferenceName, defaultValue);
		return pref;
	}

	public static EVEDroidApp getSingletonApp() {
		if (null == singleton) {
			new EVEDroidApp();
		}
		return singleton;
	}

	public static ICache getTheCacheConnector() {
		return getSingletonApp().getCacheConnector();
	}

	public static boolean isFirstTimeInit() {
		return firstTimeInitialization;
	}

	// public static boolean isFullReloadActive() {
	// return fullReload;
	// }

	public static Element parseDOMDocument(final InputStream stream) throws IOException, ParserConfigurationException {
		logger.info(">> EVEDroidApp.downloadDOMDocument");
		Element elementSingleton = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
				.getDocumentElement();
		if (null == stream) return elementSingleton;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(stream);
			elementSingleton = doc.getDocumentElement();
		} catch (final MalformedURLException me) {
			me.printStackTrace();
		} catch (final SAXException se) {
			se.printStackTrace();
		} catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		return elementSingleton;
	}

	// public static void setAppTheme(final int themeCode) {
	// switch (themeCode) {
	// case 1:
	// appTheme = new RubiconRedTheme();
	// break;
	// case 2:
	// appTheme = new CryosBlueTheme();
	// break;
	// default:
	// appTheme = new RubiconRedTheme();
	// }
	//
	// }

	public static void runTimer() {
		timeTickReceiver.onReceive(EVEDroidApp.getSingletonApp().getApplicationContext(), null);
	}

	public static void setFirstInitalization(final boolean state) {
		firstTimeInitialization = state;
	}

	public static void updateProgressSpinner() {
		if ((marketCounter > 0) || (topCounter > 0)) {
			double divider = 10.0;
			if (topCounter > 10) {
				divider = 100.0;
			}
			getSingletonApp().startProgress(new Double(marketCounter + new Double(topCounter / divider)));
		} else {
			getSingletonApp().stopProgress();
		}
	}

	// - F I E L D - S E C T I O N
	// ............................................................................
	private AndroidStorageConnector		storage			= null;
	private AndroidDatabaseConnector	dbconnector	= null;
	private AndroidCacheConnector			cache				= null;

	private Typeface									daysFace		= null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public EVEDroidApp() {
		logger.info(">> EVEDroidApp.<init>");
		// Setup the referencing structures that will serve as proxy and global
		// references.
		// If singleton already defined this is not a first time initialization.
		if (null == singleton) {
			Log.i("EVEDroidApp", ".. First Time Initialization.");
			singleton = this;
			AppConnector.setConnector(EVEDroidApp.getSingletonApp());
			// // appContext = new EveDroidAppContext();
			firstTimeInitialization = true;
		} else {
			Log.i("EVEDroidApp", ".. User reload requested.");
			firstTimeInitialization = false;
		}
		logger.info("<< EVEDroidApp.<init>");
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * Checks that the current parameter timestamp is still on the frame of the window.
	 * 
	 * @param timestamp
	 *          the current and last timestamp of the object.
	 * @param window
	 *          time span window in milliseconds.
	 */
	public boolean checkExpiration(final long timestamp, final long window) {
		// logger.info("-- Checking expiration for " + timestamp + ". Window " +
		// window);
		if (0 == timestamp) return true;
		final long now = GregorianCalendar.getInstance().getTimeInMillis();
		final long endWindow = timestamp + window;
		if (now < endWindow)
			return false;
		else
			return true;
	}

	public void closeDB() {
		getDBConnector().closeDatabases();
	}

	/**
	 * Return the path to a file resource from the stand point of the application. We have to add the folder
	 * path where the user wishes to store the user application data. This directory is initially hardcoded but
	 * later may be changed on the configuration settings.
	 * 
	 * @param fileresourceid
	 * @return
	 */
	public String getAppFilePath(final int fileresourceid) {
		final String sdcarddir = getResourceString(R.string.appfoldername) + getResourceString(R.string.app_versionsuffix);
		final String file = getResourceString(fileresourceid);
		return sdcarddir + "/" + file;
	}

	public String getAppFilePath(final String fileresourceName) {
		final String sdcarddir = getResourceString(R.string.appfoldername) + getResourceString(R.string.app_versionsuffix);
		final String file = fileresourceName;
		return sdcarddir + "/" + file;
	}

	public ICache getCacheConnector() {
		if (null == cache) {
			cache = new AndroidCacheConnector(this);
		}
		return cache;
	}

	public IDatabaseConnector getDBConnector() {
		if (null == dbconnector) {
			dbconnector = new AndroidDatabaseConnector(this);
		}
		return dbconnector;
	}

	@Override
	public INeoComModelStore getModelStore() {
		return AppModelStore.getSingleton();
	}

	public String getResourceString(final int reference) {
		logger.fine("R>" + "Accessing resource: " + reference);
		return EVEDroidApp.getSingletonApp().getResources().getString(reference);
	}

	public IConnector getSingleton() {
		return singleton;
	}

	public IStorageConnector getStorageConnector() {
		if (null == storage) {
			storage = new AndroidStorageConnector(this);
		}
		return storage;
	}

	public File getUserDataStorage() {
		return new File(Environment.getExternalStorageDirectory(),
				AppConnector.getResourceString(R.string.userdatamodelfilename));
	}

	public void init() {
		// Close databases
		getDBConnector().closeDatabases();
		singleton = this;
		storage = null;
		dbconnector = null;
		cache = null;
		appModelStore = null;
		// this.appContext = null;
		// firstTimeInitialization = true;
	}

	@Override
	public void onCreate() {
		logger.info(">> EVEDroidApp.onCreate");
		super.onCreate();
		daysFace = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Days.otf");
		logger.info("<< EVEDroidApp.onCreate");
	}

	@Override
	public void onTerminate() {
		logger.info(">> EVEDroidApp.onTerminate");
		unregisterReceiver(timeTickReceiver);
		super.onTerminate();
		logger.info("<< EVEDroidApp.onTerminate");
	}

	public void openDatabases() {
		getDBConnector().openCCPDataBase();
		getDBConnector().openAppDataBase();
	}

	/**
	 * This method checks if the application has access to the external disk (SDCARD) and if that access
	 * included the writing operations.<br>
	 * This method should be called before any expected access to the filesystem by the minor number of classes
	 * because it is a method strictly related to the Android OS. The execution may change the state of some
	 * external variables but taking on account that this state may change dynamically I would prefer to call
	 * repeatedly the method than storing the initial call results.
	 * 
	 * @return if the FS is writable. This also implies that the SDCARD is available.
	 */
	public boolean sdcardAvailable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		final String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}

	/**
	 * Start the background broadcast receiver to intercept the minute tick and process the data structures
	 * checking elements that should be updated because they are obsolete. New updates will be launched as
	 * separate asynch tasks.
	 */
	public void startTimer() {
		if (null == timeTickReceiver) {
			timeTickReceiver = new TimeTickReceiver(this);
			registerReceiver(timeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		}
	}

	/**
	 * Gets the menu context and updates the background progress indicator with the counters.
	 * 
	 * @param countIndicator
	 */
	private void startProgress(final double countIndicator) {
		// Activate menu icon of progress.
		final Menu menu = EVEDroidApp.getAppStore().getAppMenu();
		if (null != menu) {
			final MenuItem updatingItem = menu.findItem(R.id.action_launchUpdate);
			final LayoutInflater mInflater = (LayoutInflater) EVEDroidApp.getAppStore().getActivity()
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View actionView = mInflater.inflate(R.layout.actionbar_indeterminateprogress, null);
			if (null != updatingItem) {
				updatingItem.setVisible(true);
				updatingItem.setActionView(actionView);
			}
			final TextView counter = (TextView) actionView.findViewById(R.id.progressCounter);
			if (null != counter) {
				counter.setVisibility(View.VISIBLE);
				// REFACTOR This should be configured on the XML and not on the
				// code. If the progress is set on a library
				// then it should be styled.
				counter.setTypeface(daysFace);
				counter.setText(pendingCounter.format(countIndicator));
			}
			actionView.invalidate();
		}
	}

	/**
	 * Hides the background progress indicator.
	 */
	private void stopProgress() {
		// Clear the update progress.
		final Menu menu = EVEDroidApp.getAppStore().getAppMenu();
		if (null != menu) {
			final MenuItem updatingItem = menu.findItem(R.id.action_launchUpdate);
			final LayoutInflater mInflater = (LayoutInflater) EVEDroidApp.getAppStore().getActivity()
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View actionView = mInflater.inflate(R.layout.actionbar_indeterminateprogress, null);
			if (null != updatingItem) {
				updatingItem.setActionView(null);
				updatingItem.setVisible(false);
			}
			final TextView counter = (TextView) actionView.findViewById(R.id.progressCounter);
			if (null != counter) {
				counter.setVisibility(View.GONE);
			}
			actionView.invalidate();
		}
	}
}
// - UNUSED CODE
// ............................................................................................
