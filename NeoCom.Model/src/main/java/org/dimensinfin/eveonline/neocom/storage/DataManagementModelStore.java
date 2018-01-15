//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API22.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. This is the Android application but shares
//                  libraries and code with other application designed for alternate platforms.
//                  The model management is shown using a generic Model View Controller that allows make the
//                  rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.storage;

import com.tlabs.android.evanova.adapter.ApplicationCloudAdapter;

import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChonoOptions;
import org.dimensinfin.core.util.OneParameterTask;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.database.NeoComDatabase;
import org.dimensinfin.eveonline.neocom.esiswagger.api.ClonesApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOkHomeLocation.LocationTypeEnum;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Login;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
public class DataManagementModelStore extends AbstractModelStore /*implements INeoComModelStore*/ {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 8777607802616543118L;

	private static Logger logger = LoggerFactory.getLogger(DataManagementModelStore.class);
	private static DataManagementModelStore singleton = null;

	/**
	 * Returns the single global instance of the Store to be used as an instance. In case the instance does not
	 * exists then we initiate the initialization process trying to create the most recent instance we have
	 * recorded or recreate it from scratch from the stored credentials.
	 *
	 * @return the single global sinleton.
	 */
	public static DataManagementModelStore getSingleton () {
		//		NeoComModelStore.logger.info(">> [NeoComModelStore.getSingleton]");
		if ( null == DataManagementModelStore.singleton ) {
			// Initiate the recovery.
			DataManagementModelStore.initialize();
		}
		//		NeoComModelStore.logger.info("<< [NeoComModelStore.getSingleton]");
		return DataManagementModelStore.singleton;
	}

	/**
	 * <p>Forces the initialization of the Model store from the api list file. Instead reading the data from the store
	 * file it will process the api list and reload all the character information from scratch.</p> <p>With the latest
	 * implementation the persistence of the Model is not required since we can replicate its stae from scratch with the
	 * parameters recovered at the Activities. We still connect the persistence Handler but should not be used.</p>
	 */
	public static void initialize () {
		logger.info(">> [AppModelStore.initialize]");
		// Create a new from scratch. Tag it with the persistence handler so we can read/write its state.
		DataManagementModelStore.singleton = new DataManagementModelStore(new NoOpPersistenceHandler());
		// Load any data from storage and then update the information from CCP.
		//		AppModelStore.getSingleton().restore();
		//	AppModelStore.readApiKeys();

		// Make sure we get the characters on a thread out of the main one.
		//	AppModelStore.getSingleton().getCharacters();
		// Set back the current pilot whose id is stored on the _pilotIdentifier
		//		if ( NeoComModelStore.getSingleton()._pilotIdentifier > 0 ) {
		//			NeoComModelStore.getSingleton().activatePilot(NeoComModelStore.getSingleton()._pilotIdentifier);
		//		}
		logger.info("<< [AppModelStore.initialize]");
	}

	public static Credential getActiveCredential () {
		return getSingleton().getActiveCredentialMethod();
	}
	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the application menu to make it accessible to any level. */
	//	private transient Menu _appMenu = null;
	/** Reference to the current active Activity. Sometimes this is needed to access application resources. */
	//	private transient Activity _activity = null;
	/** This is the unique list for all registered distinct credentials at the database. */
	//	private Hashtable<String, Login> _loginList = null;
	private final List<Credential> _credentialList = new ArrayList<>();
	/**
	 * Reference to the Active Credential that points to the Active Character. If null we should go back to the Login
	 * Activity to select a new Credential.
	 */
	private Credential _activeCredential = null;
	/**
	 * Reference to the current active Character. If this field is null we have to go back to the Login Activity to select
	 * another one.
	 */
	//	private NeoComCharacter _activeCharacter = null;

	/** This is the credential identifier assigned to this session and that relates to an specific credential. */
	//	private Login _loginIdentifier = null;
	/** Reference to the current active Character, be it a Pilot or a Corporation. */
	//	private transient NeoComCharacter _pilot = null;

	/** Check to verify if the recovery process is successful. Persistence is now not being used. */
	//	private boolean recovered = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected DataManagementModelStore (final IPersistentHandler storageHandler) {
		super();
		// On creation we can set the proper model persistence handler.
		try {
			this.setPersistentStorage(storageHandler);
			this.setAutomaticUpdate(false);
		} catch (final Exception ex) {
			// TODO This is a quite serious error because invalidates any storage of the model data
			ex.printStackTrace();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Returns the current list of active credentials. At this stage credentials are generated from a mix of
	 * api keys and esi tokens. After march 2018 only ESI tokens will be available.
	 * If the list is empty we go to the database to fetch any valid key or identifier and merge all them
	 * into a single table indexed by the Character identifier. If there are more that one key or credential for
	 * the same character they are merged taking on account the mask value for keys or the scope list for
	 * credentials.
	 * The mask is not available as a single number but as a list of information. I will not implement that
	 * functionality.
	 * In addition to generate the list of Credentials, with each of them I will download some of the Character
	 * data during the creation of the list and connect that data to the parent Credential.
	 */
	public List<Credential> accessCredentialList () {
		if ( _credentialList.size() < 1 ) {
			try {
				// Read and process the list of ApiKeys and Credentials to get a single Character list.
				final Hashtable<String, Login> logins = NeoComDatabase.accessAllLogins();
				final List<Credential> credentials = NeoComDatabase.accessAllCredentials();
				_credentialList.clear();
				// Process the list to unify the results.
				for (Credential current : credentials) {
					_credentialList.add(current);
					// Scan the keys to search for matches.
					final long cid = current.getAccountId();
					for (Login login : logins.values()) {
						for (NeoComCharacter character : login.getCharacters()) {
							if ( character.getCharacterID() == cid ) {
								current.setKeyCode(character.getAuthorization().getKeyID())
											 .setValidationCode(character.getAuthorization().getVCode())
											 .store();
								// Post a backend request to create som Character information to connect to the credential.
								ApplicationCloudAdapter.submit2downloadExecutor(
										new OneParameterTask<Credential>(current) {
											@Override
											public void run () {
												logger.info(">> [DataManagementModelStore.accessCredentialList]");
												final Chrono downloadTotalTime = new Chrono();
												try {
													// Create a request to the ESI api downloader to get Character clones and locations.
													final long charId = getTarget().getAccountId();
													final ClonesApi clonesRetrofit = new Builder()
															.baseUrl("https://esi.tech.ccp.is/latest/")
															.addConverterFactory(JacksonConverterFactory.create())
															.build()
															.create(ClonesApi.class);
													final Response<GetCharactersCharacterIdClonesOk> r = clonesRetrofit
															.getCharactersCharacterIdClones((int) charId, null, null, null, null)
															.execute();
													if ( r.isSuccessful() ) {
														// Create a minimum Character profile and fill it up with the available information.
														final CorePilot pilot = new CorePilot()
																.setIdentifier(getTarget().getAccountId())
														.setLocationId(r.body().getHomeLocation().getLocationId())
														.setLocationType(r.body().getHomeLocation().getLocationType());
														// Add this data to the Credential.
														getTarget().setCharacterCoreData(pilot);
													}
												} catch (IOException e) {
													e.printStackTrace();
												} finally {
													logger.info("<< [DataManagementModelStore.accessCredentialList]> [TIMING] Full elapsed: ", downloadTotalTime.printElapsed(ChonoOptions.SHOWMILLIS));
												}
											}
										});
							}
						}
					}
				}
			} catch (RuntimeException rtex) {
				// There is some kind of exception during this key initialization routine. Post to the ModelStore a
				// exception documentation so the display can show that information (maybe on the header).
				rtex.printStackTrace();
			}
		}
		return _credentialList;
	}

	public static class CorePilot {
		private long identifier=-1;
		private long locationId = -1;
		private LocationTypeEnum locationType;
		private EveLocation location=null;

		public long getIdentifier () {
			return identifier;
		}

		public CorePilot setIdentifier (final long identifier) {
			this.identifier = identifier;
			return this;
		}

		public long getLocationId () {
			return locationId;
		}

		public CorePilot setLocationId (final long locationId) {
			this.locationId = locationId;
			// Search this location identifier at the Location service.
			final EveLocation location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locationId);
			return this;
		}

		public LocationTypeEnum getLocationType () {
			return locationType;
		}

		public CorePilot setLocationType (final LocationTypeEnum locationType) {
			this.locationType = locationType;
			return this;
		}

		public EveLocation getLocation () {
			return location;
		}
		public String getURLForAvatar(){
			return "http://image.eveonline.com/character/" + identifier + "_256.jpg";
		}
	}

	/**
	 * Activated the selected credential as the active Credential. This will also point to the current Character but
	 * allows for lazy evaluation of most of the Character data to be obtained when the character is later used on the
	 * interfaces.
	 *
	 * @param identifier unique account number of character identifier.
	 * @return the new credential made active. Raises a RuntimeException if the Credential is not found.
	 */
	public Credential activateCredential (final long identifier) {
		// Check if the list of credentials is already loaded. If not get it from the database.
		if ( _credentialList.size() < 1 ) accessCredentialList();
		if ( null != _activeCredential )
			if ( _activeCredential.getAccountId() == identifier ) return _activeCredential;
		// Search for the credential on the list.
		for (Credential target : _credentialList) {
			if ( target.getAccountId() == identifier ) {
				_activeCredential = target;
				return _activeCredential;
			}
		}
		// If we reach this point this means that we have not found the credential. This is an exception.
		throw new RuntimeException("RT [NeoComModelStore]> Credential with id " + identifier + " not found.");
	}

	/**
	 * Returns the current active Credential that matched the active character. Raises a runtime exception if the
	 * character is null.
	 */
	private Credential getActiveCredentialMethod () {
		if ( null == _activeCredential )
			throw new RuntimeException("RT> Accessing an invalid Credential. Select a new character from the list of Credentials.");
		return _activeCredential;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("DataManagementModelStore [");
		buffer.append("Credentials [").append(_credentialList.size()).append("]: ").append(_credentialList.toString());
		buffer.append(" ]");
		return buffer.toString();
	}

	/**
	 * This methods should do a complete cleanup of the data structures to force a new reload of the data from its
	 * sources. Also check that the delegated instances are on place. In the future it will also need to close databases
	 * to reset its status.
	 */
	public void cleanModel () {
		//		_loginList = null;
		//		_loginIdentifier = null;
		//		_pilot = null;
		singleton = null;
		_credentialList.clear();
	}
}

// - UNUSED CODE ............................................................................................
