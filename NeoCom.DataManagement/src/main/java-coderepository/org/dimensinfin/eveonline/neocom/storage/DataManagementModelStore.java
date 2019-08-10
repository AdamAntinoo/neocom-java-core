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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.model.AbstractModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;

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
 * <p>
 * WARNING.
 * Due to the singleton inheritance and the delayed singleton creation we should avoid the use of the
 * <code>getSingleton()</code> method because it can return the wrong singleton depending on the class being
 * called. We should use ever the <code>singleton</code> instance and then we should be sure it is
 * properly initialized.
 *
 * @author Adam Antinoo
 */
public class DataManagementModelStore extends AbstractModelStore /*implements INeoComModelStore*/ {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 8777607802616543118L;
	private static Logger logger = LoggerFactory.getLogger(DataManagementModelStore.class);
	protected static DataManagementModelStore singleton = null;

	/**
	 * Returns the single global instance of the Store to be used as an instance. In case the instance does not
	 * exists then we initiate the initialization process trying to create the most recent instance we have
	 * recorded or recreate it from scratch from the stored credentials.
	 *
	 * @return the single global sinleton.
	 */
	public static DataManagementModelStore getSingleton() {
		//		NeoComModelStore.logger.info(">> [DataManagementModelStore.getSingleton]");
		if (null == DataManagementModelStore.singleton) {
			// Initiate the recovery.
			DataManagementModelStore.initialize();
		}
		//		NeoComModelStore.logger.info("<< [DataManagementModelStore.getSingleton]");
		return DataManagementModelStore.singleton;
	}

	/**
	 * <p>Forces the initialization of the Model store from the api list file. Instead reading the data from the store
	 * file it will process the api list and reload all the character information from scratch.</p> <p>With the latest
	 * implementation the persistence of the Model is not required since we can replicate its stae from scratch with the
	 * parameters recovered at the Activities. We still connect the persistence Handler but should not be used.</p>
	 */
	private static void initialize() {
//		logger.info(">> [DataManagementModelStore.initialize]");
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
//		logger.info("<< [DataManagementModelStore.initialize]");
	}

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
	public static List<Credential> accessCredentialList() {
		if (null == singleton) DataManagementModelStore.initialize();
		return singleton.accessCredentialListImpl();
	}

	public static Credential activateCredential( final int identifier ) {
		if (null == singleton) DataManagementModelStore.initialize();
		return singleton.activateCredentialImpl(identifier);
	}
	public static Credential searchCredential4Id( final int identifier ) {
		if (null == singleton) DataManagementModelStore.initialize();
		return singleton.activateCredentialImpl(identifier);
	}
	public static Credential getActiveCredential() {
		if (null == singleton) DataManagementModelStore.initialize();
		return singleton.getActiveCredentialImpl();
	}

	/**
	 * Search this identifier on the list of credentials and returns the findings.
	 */
	@Deprecated
	public static Credential getCredential4Id( final int identifier ) {
		if (null == singleton) DataManagementModelStore.initialize();
		return singleton.getCredential4IdImpl(identifier);
	}

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is the unique list for all registered distinct credentials at the database.
	 */
	private final List<Credential> _credentialList = new Vector<>();
	/**
	 * Reference to the Active Credential that points to the Active Character. If null we should go back to the Login
	 * Activity to select a new Credential.
	 */
	private Credential _activeCredential = null;
//[01]

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected DataManagementModelStore( final IPersistentHandler storageHandler ) {
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
	 * Go always to the database to get a fresh credential list. NOw that there are no XML api access we can do it directly with
	 * no more processing or delays.
	 *
	 * @return
	 */
	private List<Credential> accessCredentialListImpl() {
		try {
			// Get the list of Credentials.
			final List<Credential> credentials = GlobalDataManager.accessAllCredentials();
			_credentialList.clear();
			for (Credential currentCredential : credentials) {
				_credentialList.add(currentCredential);
//					final PilotV1 pilot = GlobalDataManager.getPilotV2(currentCredential.getAccountId());
			}
		} catch (RuntimeException rtex) {
			// There is some kind of exception during this key initialization routine. Post to the ModelStore a
			// exception documentation so the display can show that information (maybe on the header).
			rtex.printStackTrace();
		}
		return _credentialList;
	}

	/**
	 * Returns the current active Credential that matched the active character. Raises a runtime exception if the
	 * character is null.
	 */
	private Credential getActiveCredentialImpl() {
		if (null == _activeCredential)
			throw new RuntimeException("RT> Accessing an invalid Credential. Select a new character from the list of Credentials.");
		return _activeCredential;
	}

	/**
	 * Search this identifier on the list of credentials and returns the findings. We should not return null values
	 * because this can generate exception because this method is consumed without checks.
	 * Other possible solution is to check again for the Credential list from the database.
	 */
	@Deprecated
	private Credential getCredential4IdImpl( final long identifier ) {
		for (Credential cred : _credentialList) {
			if (cred.getAccountId() == identifier) return cred;
		}
		// TODO Get again the credential list from the persistence database to check that this is not a new login or that
		// the login list has been lost by reinitialization.
		throw new NeocomRuntimeException("RTE [DataManagementModelStore.getCredential4IdImpl]> Credential not found on " +
				"the credential list. This is not an expected exception");
	}
//[02]

	/**
	 * Activated the selected credential as the active Credential. This will also point to the current Character but
	 * allows for lazy evaluation of most of the Character data to be obtained when the character is later used on the
	 * interfaces.
	 *
	 * @param identifier unique account number of character identifier.
	 * @return the new credential made active. Raises a RuntimeException if the Credential is not found.
	 */
	private Credential activateCredentialImpl( final long identifier ) {
		// Check if the list of credentials is already loaded. If not get it from the database.
		if (_credentialList.size() < 1) accessCredentialList();
		if (null != _activeCredential)
			if (_activeCredential.getAccountId() == identifier) return _activeCredential;
		// Search for the credential on the list.
		for (Credential target : _credentialList) {
			if (target.getAccountId() == identifier) {
				_activeCredential = target;
				return _activeCredential;
			}
		}
		// If we reach this point this means that we have not found the credential. This is an exception.
		throw new RuntimeException("RT [NeoComModelStore]> Credential with id " + identifier + " not found.");
	}

	@Override
	public String toString() {
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
	public void cleanModel() {
		_credentialList.clear();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
//	/** This is the list of available characters that are connected to the available Credentials */
//	private final List<NeoComCharacter> _characters = new vector();
/**
 * Reference to the application menu to make it accessible to any level.  Reference to the current active Activity. Sometimes this is needed to access application resources.
 * Reference to the current active Character. If this field is null we have to go back to the Login Activity to select
 * another one.
 * This is the credential identifier assigned to this session and that relates to an specific credential.  Reference to the current active Character, be it a Pilot or a Corporation.  Check to verify if the recovery process is successful. Persistence is now not being used.
 * Reference to the current active Activity. Sometimes this is needed to access application resources.
 * Reference to the current active Character. If this field is null we have to go back to the Login Activity to select
 * another one.
 * This is the credential identifier assigned to this session and that relates to an specific credential.  Reference to the current active Character, be it a Pilot or a Corporation.  Check to verify if the recovery process is successful. Persistence is now not being used.
 * Reference to the current active Activity. Sometimes this is needed to access application resources.
 * Reference to the current active Character. If this field is null we have to go back to the Login Activity to select
 * another one.
 * This is the credential identifier assigned to this session and that relates to an specific credential.  Reference to the current active Character, be it a Pilot or a Corporation.  Check to verify if the recovery process is successful. Persistence is now not being used.
 */
//	private transient Menu _appMenu = null;
/** Reference to the current active Activity. Sometimes this is needed to access application resources. */
//	private transient Activity _activity = null;
//	private Hashtable<String, Login> _loginList = null;
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

//[02]
//	/**
//	 * Returns the current list of active credentials. At this stage credentials are generated from a mix of
//	 * api keys and esi tokens. After march 2018 only ESI tokens will be available.
//	 * If the list is empty we go to the database to fetch any valid key or identifier and merge all them
//	 * into a single table indexed by the Character identifier. If there are more that one key or credential for
//	 * the same character they are merged taking on account the mask value for keys or the scope list for
//	 * credentials.
//	 * The mask is not available as a single number but as a list of information. I will not implement that
//	 * functionality.
//	 * In addition to generate the list of Credentials, with each of them I will download some of the Character
//	 * data during the creation of the list and connect that data to the parent Credential.
//	 */
//	@Deprecated
//	public List<Credential> coalesceCredentialList() {
//		logger.info(">> [DataManagementModelStore.coalesceCredentialList]");
//		try {
//			if (_credentialList.size() < 1) {
//				try {
//					// Read and process the list of ApiKeys and Credentials to get a single Character list.
//					final List<ApiKey> keyList = GlobalDataManager.accessAllApiKeys();
//					final List<Credential> credentials = GlobalDataManager.accessAllCredentials();
//					_credentialList.clear();
//					// Process the list to unify the results.
//					for (Credential currentCredential : credentials) {
//						_credentialList.add(currentCredential);
//						// Scan the keys to search for matches.
//						final int cid = currentCredential.getAccountId();
//						for (ApiKey apikey : keyList) {
//							// Access the XML api to get the contents for this key so we can match the characters.
//							//			final ApiKey apikeyInfo = GlobalDataManager.extendApiKey(apikey);
//							for (Character character : apikey.getEveCharacters())
//								if (character.getCharacterID() == cid) {
//									currentCredential.setKeyCode(apikey.getKeynumber())
//											.setValidationCode(apikey.getValidationcode())
//											.store();
//									final PilotV1 pilot = GlobalDataManager.getPilotV2(cid);
//								}
//						}
//					}
//				} catch (RuntimeException rtex) {
//					// There is some kind of exception during this key initialization routine. Post to the ModelStore a
//					// exception documentation so the display can show that information (maybe on the header).
//					rtex.printStackTrace();
//				}
//			}
//			return _credentialList;
//		} finally {
//			logger.info(">> [DataManagementModelStore.coalesceCredentialList]> Credentials found {}.", _credentialList.size());
//		}
//	}
//	private File createCacheFile( final String fileName ) {
//		File esiCacheFile = new File(fileName);
//		return esiCacheFile;
//	}

//	private void testCharacterESIRequest (final Credential cred) {
//		logger.info(">> [DataManagementModelStore.accessCredentialList]");
//		final Chrono downloadTotalTime = new Chrono();
//		try {
//			// Create a request to the ESI api downloader to get Character clones and locations.
//			// Initialization of data that are required.
//			final long charId = cred.getAccountId();
//			final String refresh = cred.getRefreshToken();
//			final String datasource = "tranquility";
//			final String CLIENT_ID = "396e0b6cbed2488284ed4ae426133c90";
//			final String SECRET_KEY = "gf8X16xbdaO6soJWCdYHPFfftczZyvfo63z6WUjO";
//			final String CALLBACK = "eveauth-neotest://authentication";
//			final String agent = "org.dimensinfin.eveonline.neocom; Dimensinfin Industries";
//			final ESIStore store = ESIStore.DEFAULT;
//			final List<String> scopes = new ArrayList<>(2);
//			scopes.add("publicData");
//			scopes.add("esi-clones.read_clones.v1");
//			final String cacheFilename = "./NeoComESIcache.store";
//			final File cache = createCacheFile(cacheFilename);
//			final long cacheSize = 1000000;
//			final long timeout = 10000;
//
//
//			// Initialization of instances required later.
//			final NeoComOAuth20 neocomAuth20 = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, agent, store, scopes);
//			final Retrofit neocomRetrofit = NeoComRetrofitHTTP.build(refresh, neocomAuth20, agent, cache, cacheSize, timeout);
//
//			// Access the current clone location and the list of jump clones
//			final ClonesApi clonesApiRetrofit = neocomRetrofit.create(ClonesApi.class);
//			final Response<GetCharactersCharacterIdClonesOk> characterCloneResponse = clonesApiRetrofit.getCharactersCharacterIdClones(Long.valueOf(charId).intValue(), datasource, null, null, null).execute();
//			if ( characterCloneResponse.isSuccessful() ) {
//				// Create a minimum Character profile and fill it up with the available information.
//				final CorePilot pilot = new CorePilot(characterCloneResponse.body())
//						.setIdentifier(cred.getAccountId())
//						.setLocationId(characterCloneResponse.body().getHomeLocation().getLocationId())
//						.setLocationType(characterCloneResponse.body().getHomeLocation().getLocationType());
//				// Add this data to the Credential.
//				cred.setCharacterCoreData(pilot);
//			}
//
//			final PlanetaryInteractionApi colonyApiRetrofit = neocomRetrofit.create(PlanetaryInteractionApi.class);
//			final Response<List<GetCharactersCharacterIdPlanets200Ok>> colonyApiResponse = colonyApiRetrofit.getCharactersCharacterIdPlanets(Long.valueOf(charId).intValue(), datasource, null, null, null).execute();
//			if ( colonyApiResponse.isSuccessful() ) {
//				// Add Colony information to the Pilot.
//				cred.addPlanetaryData(colonyApiResponse.body());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			logger.info("<< [DataManagementModelStore.accessCredentialList]> [TIMING] Full elapsed: ", downloadTotalTime.printElapsed(ChonoOptions.SHOWMILLIS));
//		}
//	}

//	public static class CorePilot {
//		private final GetCharactersCharacterIdClonesOk delegate;
//		private long identifier = -1;
//		private long locationId = -1;
//		private LocationTypeEnum locationType;
//		private EsiLocation location = null;
//		private List<GetCharactersCharacterIdPlanets200Ok> planetaryData;
//
//		public CorePilot (final GetCharactersCharacterIdClonesOk delegate) {
//			this.delegate = delegate;
//		}
//
//		public long getIdentifier () {
//			return identifier;
//		}
//
//		public CorePilot setIdentifier (final long identifier) {
//			this.identifier = identifier;
//			return this;
//		}
//
//		public long getLocationId () {
//			return locationId;
//		}
//
//		public CorePilot setLocationId (final long locationId) {
//			this.locationId = locationId;
//			// Search this location identifier at the Location service.
//			location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locationId);
//			return this;
//		}
//
//		public LocationTypeEnum getLocationType () {
//			return locationType;
//		}
//
//		public CorePilot setLocationType (final LocationTypeEnum locationType) {
//			this.locationType = locationType;
//			return this;
//		}
//
//		public EsiLocation getLocation () {
//			return location;
//		}
//
//		public String getURLForAvatar () {
//			return "http://image.eveonline.com/character/" + identifier + "_256.jpg";
//		}
//
//		public CorePilot setPlanetaryData (final List<GetCharactersCharacterIdPlanets200Ok> data) {
//			this.planetaryData = data;
//			return this;
//		}
//	}
