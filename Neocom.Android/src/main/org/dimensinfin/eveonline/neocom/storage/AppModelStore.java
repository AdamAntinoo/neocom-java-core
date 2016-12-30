//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.storage;

//- IMPORT SECTION .........................................................................................
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.core.INeoComModelStore;
import org.dimensinfin.eveonline.neocom.datasource.DataSourceManager;
import org.dimensinfin.eveonline.neocom.datasource.IDataSourceConnector;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import com.beimin.eveapi.exception.ApiException;

import android.app.Activity;
import android.view.Menu;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class has as main responsibility the access to the data input by the user and the store of all Eve
 * data downloaded from the CCP servers. The data to be manipulated has some levels, starting on the list of
 * credentials to access the CCP api and ending on processed and generated data from the other levels.<br>
 * The quantity of information makes a priority to keep it updated to the minor cost of processing. After
 * storing the data in serialized form or developing some cache variations I have choose the store into a
 * local database as the fastest way to have ready data while the lengthy download processes update most of
 * that information.<br>
 * <br>
 * The class is a singleton with two main structures, the list of api keys and the list of user defined
 * fittings. Both sets have to contain unique identifiers so any addition of a duplicated one will only
 * replace the older one. The persistence mechanics are delegated to a new PersistenceManager that will have
 * access to a SQLite database to keep track of this data structures, so all input/output should be handled by
 * that Manager.<br>
 * <br>
 * There should be some type of notification mechanism to report the UI about changes on the data contents
 * performed with background tasks. This is integrated on the GEF model hierarchy but has to be reviewed
 * inside Android special UI/non UI threads structure.
 * 
 * @author Adam Antinoo
 */
public class AppModelStore extends AbstractModelStore implements INeoComModelStore {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long			serialVersionUID	= 8777607802616543118L;
	private static Logger					logger						= Logger.getLogger("AppModelStore");
	private static AppModelStore	singleton					= null;

	/**
	 * Returns the single global instance of the Store to be used as an instance. In case the instance does not
	 * exists then we initiate the initialization process trying to create the most recent instance we have
	 * recorded or recreate it from scratch from the api_list file.
	 * 
	 * @return the single global instance
	 */
	public static AppModelStore getSingleton() {
		if (null == AppModelStore.singleton) {
			// Initiate the recovery.
			// Try to read from persistence file.
			AppModelStore.singleton = new AppModelStore(new UserModelPersistenceHandler());
			AppModelStore.singleton.restore();
			if (!AppModelStore.singleton.isRestored()) {
				AppModelStore.readApiKeys();
			}
		}
		return AppModelStore.singleton;
	}

	/**
	 * Forces the initialization of the Model store from the api list file. Instead reading the data from the
	 * store file it will process the api list and reload all the character information from scratch.
	 */
	public static void initialize() {
		// Create a new from scratch. Read the api key list.
		AppModelStore.singleton = new AppModelStore(new UserModelPersistenceHandler());
		// Load any data from storage and then update the information from CCP.
		AppModelStore.getSingleton().restore();
		AppModelStore.readApiKeys();

		// Make sure we get the characters on a thread out of the main one.
		AppModelStore.getSingleton().getCharacters();
	}

	private static void readApiKeys() {
		AppModelStore.logger.info(">> [AppModelStore.readApiKeys]");
		try {
			// Read the contents of the character information.
			final File characterFile = AppConnector.getStorageConnector()
					.accessAppStorage(AppConnector.getResourceString(R.string.apikeysfilename));
			InputStream is = new BufferedInputStream(new FileInputStream(characterFile));
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = br.readLine();
			while (null != line) {
				try {
					String[] parts = line.split(":");
					String key = parts[0];
					String validationcode = parts[1];
					int keynumber = Integer.parseInt(key);
					AppModelStore.logger.info("-- Inserting API key " + keynumber);
					NeoComApiKey api = NeoComApiKey.build(keynumber, validationcode);
					AppModelStore.getSingleton().addApiKey(api);
				} catch (NumberFormatException nfex) {
				} catch (ArrayIndexOutOfBoundsException aioofe) {
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				line = br.readLine();
			}
			if (null != br) {
				br.close();
			}
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppModelStore.logger.info("<< [AppModelStore.readApiKeys]");
	}

	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the application menu to make it accessible to any level. */
	private transient Menu									_appMenu	= null;
	/** Reference to the current active Activity. Sometimes this is needed to access application resources. */
	private transient Activity							_activity	= null;
	private transient NeoComCharacter				_pilot		= null;
	/** Check to verify if the recovery process is successful. */
	private boolean													recovered	= false;

	/** List of registered DataSources. This data is not stored on switch or termination. */
	private transient DataSourceManager			dsManager	= null;
	private HashMap<Integer, NeoComApiKey>	apiKeys		= new HashMap<Integer, NeoComApiKey>();
	/** List of fittings by name. This is the source for the Fittings DataSource. */
	private HashMap<String, Fitting>				fittings	= new HashMap<String, Fitting>();
	//	private transient HashMap<Long, EveChar>	charCache					= null;
	//	private final long											lastCCPAccessTime	= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private AppModelStore(final IPersistentHandler storageHandler) {
		super();
		// On creation we can set the proper model persistence handler.
		try {
			this.setPersistentStorage(storageHandler);
			this.setAutomaticUpdate(true);
		} catch (final Exception ex) {
			// TODO This is a quite serious error because invalidates any storage of the model data
			ex.printStackTrace();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Stores a reference to the current activity if this is required for some actions inside the DataSources.
	 * 
	 * @param currentActivity
	 */
	public void activateActivity(final Activity currentActivity) {
		_activity = currentActivity;
	}

	/**
	 * Searches for the pilot on the character list active after the API key processing and copies it to the
	 * store slot.
	 * 
	 * @param characterid
	 *          id of the character to activate and select for work with.
	 */
	public void activatePilot(final long characterid) {
		_pilot = this.searchCharacter(characterid);
		if (null == _pilot)
			throw new RuntimeException("RT AppModelStore.activatePilot - Pilot not located. Problem of initialization.");
		// Link the pilot to the store for dirty processing.
		_pilot.setParent(this);
	}

	/**
	 * Before adding the key check if already exists. If so then only update its contents.
	 * 
	 * @param newKey
	 */
	public void addApiKey(final NeoComApiKey newKey) {
		if (null != newKey) {
			newKey.setParent(this);
			apiKeys.put(newKey.getKey(), newKey);
			this.setDirty(true);
		}
	}

	/**
	 * Adds a new fitting to the list of stored ones and persists the data structures.
	 * 
	 * @param fit
	 * @param label
	 */
	public void addFitting(final Fitting fit, final String label) {
		fittings.put(label, fit);
		this.setDirty(true);
	}

	@Deprecated
	public boolean checkStorage() {
		return AppConnector.sdcardAvailable();
	}

	@Override
	@Deprecated
	public void clean() {
		apiKeys = new HashMap<Integer, NeoComApiKey>();
		fittings = new HashMap<String, Fitting>();
	}

	/**
	 * Return the list of pilots that are currently active as selected by the user. The user may deactivate some
	 * pilots to reduce the view of data or pilots that are no longer active.
	 * 
	 * @return
	 */
	public ArrayList<NeoComCharacter> getActiveCharacters() {
		// Iterate the list of pilots and accumulate the active ones.
		final ArrayList<NeoComCharacter> activePilots = new ArrayList<NeoComCharacter>();
		for (final NeoComCharacter pilot : this.getCharacters().values())
			if (pilot.isActive()) {
				activePilots.add(pilot);
			}
		return activePilots;
	}

	public Activity getActivity() {
		return _activity;
	}

	public HashMap<Integer, NeoComApiKey> getApiKeys() {
		return apiKeys;
	}

	@Deprecated
	public Menu getAppMenu() {
		return _appMenu;
	}

	public IDataSourceConnector getDataSourceConector() {
		if (null == dsManager) {
			dsManager = new DataSourceManager();
		}
		return dsManager;
	}

	public HashMap<String, Fitting> getFittings() {
		return fittings;
	}

	public NeoComCharacter getPilot() {
		if (null == _pilot)
			throw new RuntimeException("RT CharacterStore - Pilot access cannot be completed. Pilot is NULL");
		return _pilot;
	}

	/**
	 * Checks if we have data or we have to restore from disk, usually only on the initialization.
	 * 
	 * @return true is we have to read the data because the containers are empty.
	 */
	public boolean needsRestore() {
		if (apiKeys.size() < 1) return true;
		return false;
	}

	/**
	 * Restores the stored state from the disk file. If there any problem the method returns true.
	 * 
	 * @return <code>true</code> if there is a problem and the restore was not executed.
	 */
	@Override
	public boolean restore() {
		AppConnector.startChrono();
		recovered = super.restore();
		if (recovered) {
			AppModelStore.logger.info("~~ Time lapse for APPSTORE[RESTORE] - " + AppConnector.timeLapse());
			return recovered;
		} else {
			// The handler was not able to retrieve the file. Possibly because there was no file.
			this.setDirty(true);
			return false;
		}
	}

	//	/**
	//	 * Checks if there is a need to retrieve again from the servers the user data.
	//	 * 
	//	 * @return
	//	 */
	//	public boolean needsUpdate() {
	//		return AppConnector.checkExpiration(lastCCPAccessTime, ModelWideConstants.HOURS3);
	//	}

	//	public String printReport() {
	//		final StringBuffer buffer = new StringBuffer("[UserModelStore (");
	//		buffer.append("keys: ").append(apiKeys).append(" ");
	//		buffer.append("]");
	//		return buffer.toString();
	//	}

	//	/**
	//	 * Refreshes the api keys and all other depending structures from the api list file. Keeps as much of the
	//	 * current information as possible while updating the list of keys.
	//	 */
	//	public void refresh() {
	//		// TODO Auto-generated method stub
	//	}

	/**
	 * Save data to disk only when we have detected it has changed.
	 * 
	 */
	@Override
	public boolean save() {
		if (this.isDirty()) {
			// Clean the dirty flag.
			this.setDirty(false);
			recovered = true;
			AppConnector.startChrono();
			final boolean state = super.save();
			AppModelStore.logger.info("~~ Time lapse for APPSTORE[SAVE] - " + AppConnector.timeLapse());
			return state;
		} else
			return false;
	}

	/**
	 * Search for the specified character id in the list of api keys.
	 * 
	 * @param characterID
	 * @return
	 */
	public NeoComCharacter searchCharacter(final long characterID) {
		return this.getCharacters().get(characterID);
	}

	public Fitting searchFitting(final String fittingLabel) {
		return fittings.get(fittingLabel);
	}

	/**
	 * Stored the keys on the store fields from the persistence store. This code should reconnect pointers to
	 * fields not stored and marked as transient.
	 * 
	 * @param newkeys
	 */
	public void setApiKeys(final HashMap<Integer, NeoComApiKey> newkeys) {
		apiKeys = newkeys;
		// we have to reparent the new data because this is not stored on the serialization.
		// Also reinitialize transient fields that are not saved
		final Iterator<NeoComApiKey> eit = apiKeys.values().iterator();
		while (eit.hasNext()) {
			final NeoComApiKey apiKey = eit.next();
			apiKey.setParent(this);
		}
	}

	public void setAppMenu(final Menu appMenu) {
		_appMenu = appMenu;
	}

	public void setFittings(final HashMap<String, Fitting> readObject) {
		fittings = readObject;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("AppModelStore [");
		buffer.append(" apiKeys(").append(apiKeys.size()).append("): ").append(apiKeys);
		buffer.append(" fittings(").append(fittings.size()).append("): ").append(fittings.size());
		buffer.append(" ]");
		return buffer.toString();
	}

	/**
	 * Get a complete list of the characters available indexed by the character id.
	 * 
	 * @return
	 */
	private HashMap<Long, NeoComCharacter> getCharacters() {
		HashMap<Long, NeoComCharacter> charCache = new HashMap<Long, NeoComCharacter>();
		for (final NeoComApiKey key : apiKeys.values()) {
			try {
				for (final NeoComCharacter eveChar : key.getApiCharacters()) {
					charCache.put(eveChar.getCharacterID(), eveChar);
				}
			} catch (ApiException apiex) {
				apiex.printStackTrace();
			}
		}
		return charCache;
	}

	private boolean isRestored() {
		return recovered;
	}
}

// - UNUSED CODE ............................................................................................
