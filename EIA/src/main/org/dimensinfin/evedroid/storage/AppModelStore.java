//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.storage;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.datasource.DataSourceManager;
import org.dimensinfin.evedroid.datasource.IDataSourceConnector;
import org.dimensinfin.evedroid.model.NeoComApiKey;
import org.dimensinfin.evedroid.model.EveChar;

import android.app.Activity;
import android.util.Log;
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
 * The class is a singleton with two main structures, the list of api keys and the list of eve characters
 * identified by that keys. Both sets have to contain unique identifiers so any addition of a duplicated one
 * will only replace the older one. The persistence mechanics are delegated to a new PersistenceManager that
 * will have access to a SQLite database to keep track of this data structures, so all input/output should be
 * handled by that Manager.<br>
 * <br>
 * There should be some type of notification mechanism to report the UI about changes on the data contents
 * performed with background tasks. This is integrated on the GEF model hierarchy but has to be reviewed
 * inside Android special UI/non UI threads structure.
 * 
 * @author Adam Antinoo
 */
public class AppModelStore extends AbstractModelStore {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= 8777607802616543118L;
	private static Logger							logger						= Logger.getLogger("AppModelStore");

	// - F I E L D - S E C T I O N ............................................................................
	private Menu											_appMenu					= null;
	private HashMap<Integer, NeoComApiKey>	apiKeys						= new HashMap<Integer, NeoComApiKey>();
	private HashMap<Long, EveChar>		charCache					= null;
	private final long								lastCCPAccessTime	= 0;
	private transient EveChar					_pilot						= null;
	private transient Activity				_activity					= null;
	private DataSourceManager					dsManager					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AppModelStore(final IPersistentHandler storageHandler) {
		super();
		// On creation we can set the proper model persistence handler.
		try {
			setPersistentStorage(storageHandler);
			setAutomaticUpdate(true);
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
		this._activity = currentActivity;
		//		// REFACTOR Remove this after we have completed the transformation
		//		EVEDroidApp.getAppContext().setCurrentActivity(currentActivity);
	}

	/**
	 * Searches for the pilot on the character list active after the API key processing and copies it to the
	 * store slot. If there is an AssetsManager it is connected to the character, otherwise a new AssetsManager
	 * is created empty.
	 * 
	 * @param characterid
	 */
	public void activatePilot(final long characterid) {
		//		if (null == _userdata)
		//			throw new RuntimeException(
		//					"RT CharacterStore.activatePilot - The UserData is not defined. Problem of initialization.");
		this._pilot = searchCharacter(characterid);
		if (null == this._pilot)
			throw new RuntimeException("RT AppModelStore.activatePilot - Pilot not located. Problem of initialization.");
		// Link the pilot to the store for dirty processing.
		this._pilot.setParent(this);
	}

	/**
	 * Before adding the key check if already exists. If so then only update its contents.
	 * 
	 * @param newKey
	 */
	public void addApiKey(final NeoComApiKey newKey) {
		if (null != newKey) {
			//			HashMap<Integer, APIKey> oldState = (HashMap<Integer, APIKey>) apiKeys.clone();
			newKey.setParent(this);
			// First update the key to avoid adding invalid keys.
			newKey.update();
			this.apiKeys.put(newKey.getKeyID(), newKey);
			//		fireStructureChange(BundlesAndMessages.events.EVENTSTR_APIKEY, oldState, apiKeys);
			setDirty(true);
		}
	}

	public boolean checkStorage() {
		return AppConnector.sdcardAvailable();
	}

	@Override
	public void clean() {
		this.apiKeys = new HashMap<Integer, NeoComApiKey>();
		//		characters = new HashMap<Long, EveChar>();
	}

	/**
	 * Return the list of pilots that are currently active as selected by the user. The user may deactivate some
	 * pilots to reduce the view of data or pilots that are no longer active.
	 * 
	 * @return
	 */
	public ArrayList<EveChar> getActiveCharacters() {
		// Iterate the list of pilots and accumulate the active ones.
		final ArrayList<EveChar> activePilots = new ArrayList<EveChar>();
		for (final EveChar pilot : getCharacters().values())
			if (pilot.isActive()) {
				activePilots.add(pilot);
			}
		return activePilots;
	}

	public Activity getActivity() {
		return this._activity;
	}

	public HashMap<Integer, NeoComApiKey> getApiKeys() {
		return this.apiKeys;
	}

	public Menu getAppMenu() {
		return this._appMenu;
	}

	public HashMap<Long, EveChar> getCharacters() {
		if (null == this.charCache) {
			this.charCache = new HashMap<Long, EveChar>();
			for (final NeoComApiKey key : this.apiKeys.values()) {
				final Collection<EveChar> chars = key.getCharacters().values();
				for (final EveChar eveChar : chars) {
					this.charCache.put(eveChar.getCharacterID(), eveChar);
				}
			}
		}
		return this.charCache;
	}

	public EveChar getPilot() {
		if (null == this._pilot)
			throw new RuntimeException("RT CharacterStore - Pilot access cannot be completed. Pilot is NULL");
		return this._pilot;
	}

	/**
	 * Checks if we have data or we have to restore from disk, usually only on the initialization.
	 * 
	 * @return true is we have to read the data because the containers are empty.
	 */
	public boolean needsRestore() {
		if (this.apiKeys.size() < 1) return true;
		return false;
	}

	/**
	 * Checks if there is a need to retrieve again from the servers the user data.
	 * 
	 * @return
	 */
	public boolean needsUpdate() {
		return AppConnector.checkExpiration(this.lastCCPAccessTime, ModelWideConstants.HOURS3);
	}

	public String printReport() {
		final StringBuffer buffer = new StringBuffer("[UserModelStore (");
		buffer.append("keys: ").append(this.apiKeys).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * Restores the stored state from the disk file. If there any problem the method returns true.
	 * 
	 * @return <code>true</code> if there is a problem and the restore was not executed.
	 */
	@Override
	public boolean restore() {
		AppConnector.startChrono();
		final boolean state = super.restore();
		// REFACTOR - Optimize the outpost by forcing a read at this point.
		AppConnector.getDBConnector().searchLocationbyID(61000890);
		if (state) {
			Log.i("AppModelStore", "~~ Time lapse for APPSTORE[RESTORE] - " + AppConnector.timeLapse());
			return state;
		} else {
			// The handler was not able to retrieve the file. Possibly because there was no file.
			setDirty(true);
			return false;
		}
	}

	/**
	 * Save data to disk only when we have detected it has changed.
	 * 
	 */
	@Override
	public boolean save() {
		if (isDirty()) {
			// Clean the dirty flag.
			setDirty(false);
			AppConnector.startChrono();
			final boolean state = super.save();
			Log.i("AppModelStore", "~~ Time lapse for APPSTORE[SAVE] - " + AppConnector.timeLapse());
			return state;
		} else
			return false;
	}

	/**
	 * Search for the specified character id in the list of api keys and on the characters defined or available
	 * for each of that keys. To speed up that we create a precached list of characters on the first call with
	 * all the current defined characters. That list will be cleared when the background actions modify the list
	 * of keys or update them.
	 * 
	 * @param characterID
	 * @return
	 */
	public EveChar searchCharacter(final long characterID) {
		return getCharacters().get(characterID);
	}

	public void setApiKeys(final HashMap<Integer, NeoComApiKey> newkeys) {
		this.apiKeys = newkeys;
		// we have to reparent the new data because this is not stored on the serialization.
		// Also reinitialize transient fields that are not saved
		final Iterator<NeoComApiKey> eit = this.apiKeys.values().iterator();
		while (eit.hasNext()) {
			final NeoComApiKey apiKey = eit.next();
			apiKey.setParent(this);
			//			apiKey.initialize();
		}
	}

	public void setAppMenu(final Menu appMenu) {
		this._appMenu = appMenu;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("[AppModelStore");
		buffer.append(" apiKeys(").append(this.apiKeys.size()).append("): ").append(this.apiKeys);
		//		buffer.append(" characters: ").append(characters.size());
		buffer.append(" ]");
		return buffer.toString();
	}

	public IDataSourceConnector getDataSourceConector() {
		if (null == dsManager) {
			dsManager = new DataSourceManager();
		}
		return dsManager;
	}
}

// - UNUSED CODE ............................................................................................
