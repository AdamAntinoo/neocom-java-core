//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt.manager;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.connectors.LoggingConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.core.NeoComConnector;
import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.TimeStamp;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.manager.AbstractManager;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.manager.PlanetaryManager;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.network.NetworkManager;
import org.dimensinfin.eveonline.neocom.planetary.ColonyCoreStructure;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;
import org.joda.time.Instant;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This static class centralizes all the functionalities to access data. It will provide a consistent api to the rest
 * of the application and will hide the internals of how that data is obtained, managed and stored.
 * All thet now are direct database access or cache access or even Model list accesses will be hidden under an api
 * that will decide at any point from where to get the information and if there are more jobs to do to keep
 * that information available and up to date.
 * <p>
 * The initial release will start transferring the ModelFactory functionality.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManager {
	// - P U B L I C - S E C T I O N ..........................................................................
	public enum EDataUpdateJobs {
		READY, CHARACTER_CORE, CHARACTER_FULL, ASSETDATA, BLUEPRINTDATA, INDUSTRYJOBS, MARKETORDERS, COLONYDATA, SKILL_DATA
	}

	// - PR I V A T E - S E C T I O N .........................................................................
	private enum EModelVariants {
		PILOTV1, APIKEY
	}

	private enum EManagerCodes {
		PLANETARY_MANAGER, ASSETS_MANAGER
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(GlobalDataManager.class);
	private static final String SERVER_DATASOURCE = "tranquility";
	private static final long CHARACTER_PLANETS_DURATION = TimeUnit.SECONDS.toMillis(600);

	/** Initialize the beimin Eve Api connector to remove SSL certification. From this point on we can use the beimin
	 * XML api to access CCP data. */
	static {
		EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
		// Remove the secure XML access and configure the ApiConnector.
		ApiConnector.setSecureXmlProcessing(false);
	}

	/**
	 * GDM singleton to store all data, caches and references. The use of a singleton will allow to drop all data
	 * on a single operation and restart all data caches.
	 */
	//	public static final GlobalDataManager GDM = new GlobalDataManager();

	// - S T A T I C - F I E L D S - S E C T I O N ............................................................
	// --- S D E   F I E L D S
	private static final Hashtable<Integer, EveItem> itemCache = new Hashtable<Integer, EveItem>();
	private static final Hashtable<Integer, EveLocation> locationCache = new Hashtable<Integer, EveLocation>();

	// --- M A N A G E R - S T O R E   F I E L D S
	private static ManagerOptimizedCache managerCache = new ManagerOptimizedCache();
	/** Instance for the mapping of OK instances to the MVC compatible classes. */
	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
		           .setFieldMatchingEnabled(true)
		           .setMethodAccessLevel(Configuration.AccessLevel.PRIVATE);
	}

	/** Jackson mapper to use for object json serialization. */
	private static final ObjectMapper objectMapper = new ObjectMapper();
	/** Background executor to use for long downloading jobs. */
	private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();

	// --- D A T A B A S E   F I E L D S
	/** Reference to the NeoCom persistece database Dao provider. This filed should be injected on startup. */
	private static INeoComDBHelper helper = null;

	// - S T A T I C - M E T H O D S - S E C T I O N ..........................................................
	// --- S D E   I N T E R F A C E
	public static EveItem searchItemById (final int typeId) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if ( itemCache.containsKey(typeId) ) return itemCache.get(typeId);
		else return ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(typeId);
	}

	public static EveLocation searchLocationById (final int locationId) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if ( locationCache.containsKey(locationId) ) return locationCache.get(locationId);
		else return ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locationId);
	}
	// --- C A C H E   I N T E R F A C E

	// --- M O D E L - F A C T O R Y   I N T E R F A C E
	//	public static List<Colony> accessColonies4Credential (final int characterid) {
	//		// Get the Credential that matched the received identifier.
	//		Credential credential = DataManagementModelStore.getCredential4Id(characterid);
	//		if ( null != credential ) {
	//			final PlanetaryManager manager = GlobalDataManager.getPlanetaryManager(credential);
	//			final List<Colony> colonies = manager.accessAllColonies();
	//			return colonies;
	//		} else {
	//			// Possible that because the application has been previously removed from memory that data is not reloaded.
	//			// Call the reloading mechanism and have a second opportunity.
	//			DataManagementModelStore.accessCredentialList();
	//			credential = DataManagementModelStore.getCredential4Id(characterid);
	//			if ( null == credential ) return new ArrayList<>();
	//			else return GlobalDataManager.accessColonies4Credential(characterid);
	//		}
	//	}
	public static String constructJobReference (final EDataUpdateJobs type, final long identifier) {
		return new StringBuffer(type.name())
				.append("/")
				.append(identifier).toString();
	}

	public static String constructPlanetStorageIdentifier (final int characterIdentifier, final int planetIdentifier) {
		return new StringBuffer("CS:")
				.append(Integer.valueOf(characterIdentifier).toString())
				.append(":")
				.append(Integer.valueOf(planetIdentifier).toString()).toString();
	}


	// --- M A N A G E R - S T O R E   I N T E R F A C E
	public static AssetsManager getAssetsManager (final Credential credential) {
		return GlobalDataManager.getAssetsManager(credential, false);
	}

	public static AssetsManager getAssetsManager (final Credential credential, final boolean forceNew) {
		// Check if this request is already available on the cache.
		final AssetsManager hit = (AssetsManager) managerCache.access(EManagerCodes.ASSETS_MANAGER, credential.getAccountId());
		if ( (null == hit) || (forceNew) ) {
			final AssetsManager manager = new AssetsManager(DataManagementModelStore.getCredential4Id(credential.getAccountId()));
			managerCache.store(EManagerCodes.ASSETS_MANAGER, manager, credential.getAccountId());
			return manager;
		} else return hit;
	}

	public static AssetsManager dropAssetsManager (final long identifier) {
		return (AssetsManager) managerCache.delete(EManagerCodes.ASSETS_MANAGER, identifier);
	}

	public static PlanetaryManager getPlanetaryManager (final Credential credential) {
		return getPlanetaryManager(credential, false);
	}

	public static PlanetaryManager getPlanetaryManager (final Credential credential, final boolean forceNew) {
		// Check if this request is already available on the cache.
		final PlanetaryManager hit = (PlanetaryManager) managerCache.access(EManagerCodes.PLANETARY_MANAGER, credential.getAccountId());
		if ( (null == hit) || (forceNew) ) {
			// TODO This line depends on the architecture of the data loading when it should not.
			final PlanetaryManager manager = new PlanetaryManager(credential);
			managerCache.store(EManagerCodes.PLANETARY_MANAGER, manager, credential.getAccountId());
			return manager;
		} else return hit;
	}

	public static PlanetaryManager dropPlanetaryManager (final long identifier) {
		return (PlanetaryManager) managerCache.delete(EManagerCodes.PLANETARY_MANAGER, identifier);
	}

	// --- D A T A B A S E   A C C E S S   I N T E R F A C E

	public static INeoComDBHelper getHelper () {
		// TODO During the time the old and new implementations share the code make the implementer the one at the Connector.
		if ( null == helper ) try {
			helper = ModelAppConnector.getSingleton().getNewDBConnector();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( null == helper )
			throw new RuntimeException("[NeoComDatabase]> NeoCom database helper not defined. No access to platform library to get database results.");
		return helper;
	}

	public static void setHelper (final INeoComDBHelper newImplementer) {
		if ( null != newImplementer ) helper = newImplementer;
	}

	/**
	 * Reads the list of Colonies for the identified Credential from the persistence database. Inthe case there are no
	 * records the method checks the TimeStamp to verify that the Downloader is working on this demand and if
	 * the timer has elapsed or there is no TS, forces a first download directly throught the Network.
	 *
	 * @param credential
	 * @return
	 */
	public static List<Colony> accessColonies4Credential (final Credential credential) {
		logger.info(">> [GlobalDataManager.accessColonies4Credential]> Credential: {}", credential.getAccountName());
		List<Colony> colonyList = new ArrayList<>();
		try {
			// SELECT * FROM COLONY WHERE OWNERID = <identifier>
			Dao<Colony, String> colonyDao = getHelper().getColonyDao();
			QueryBuilder<Colony, String> queryBuilder = colonyDao.queryBuilder();
			Where<Colony, String> where = queryBuilder.where();
			where.eq("ownerID", credential.getAccountId());
			PreparedQuery<Colony> preparedQuery = queryBuilder.prepare();
			colonyList = colonyDao.query(preparedQuery);

			// Check the number of registers. If they are zero then we have to perform additional checks.
			if ( colonyList.size() < 1 ) {
				// Check if there is a valid TS.
				final String reference = constructJobReference(EDataUpdateJobs.COLONYDATA, credential.getAccountId());
				final TimeStamp ts = GlobalDataManager.getHelper().getTimeStampDao().queryForId(reference);
				if ( null == ts ) {
					// No time stamp so force a request for this data now.
					return GlobalDataManager.downloadColonies4Credential(credential);
				} else {
					// Check time stamp if elapsed.
					if ( ts.getTimeStamp() < Instant.now().getMillis() )
						return GlobalDataManager.downloadColonies4Credential(credential);
				}
			}

			// Add pending downloaded information.
			for (Colony col : colonyList) {
				col.setStructures(downloadStructures4Colony(credential.getAccountId(), col.getPlanetId()));
				final List<ColonyCoreStructure> struc = accessColonyStructures4Planet(credential.getAccountId(), col.getPlanetId());
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessColonies4Credential]> Exception reading Colonies. " + sqle.getMessage());
		} finally {
			logger.info("<< [GlobalDataManager.accessColonies4Credential]");
		}
		return colonyList;
	}

	public static List<ColonyCoreStructure> accessColonyStructures4Planet (final int identifier, final int planet) {
		logger.info(">> [GlobalDataManager.accessColonyStructures4Planet]");
		List<ColonyCoreStructure> structureList = new ArrayList<>();
		try {
			try {
				// Compose the unique key reference.
				final String ref = constructPlanetStorageIdentifier(identifier, planet);
				logger.info(">> [GlobalDataManager.accessColonyStructures4Planet]> Structure reference: {}", ref);
				// SELECT * FROM ColonyStorage WHERE planetIdentifier = <identifier>
				final ColonyStorage structureData = GlobalDataManager.getHelper().getColonyStorageDao().queryForId(ref);
				if ( null != structureData ) {
					// Reconstruct the structure from the serialized data.
					final ColonyCoreStructure structure = objectMapper.readValue(structureData.getColonySerialization(), ColonyCoreStructure.class);
					structureList.add(structure);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessColonyStructures4Planet]> Exception reading Colonies. " + sqle.getMessage());
		} finally {
			logger.info("<< [GlobalDataManager.accessColonyStructures4Planet]");
		}
		return structureList;
	}

	// --- N E T W O R K    D O W N L O A D   I N T E R F A C E
	public static List<Colony> downloadColonies4Credential (final Credential credential) {
		// Optimize the access to the Colony data.
		//		if(colonies.size()<1) {
		final Chrono accessFullTime = new Chrono();
		List<Colony> colonies = new ArrayList<>();
		// Create a request to the ESI api downloader to get the list of Planets of the current Character.
		final int identifier = credential.getAccountId();
		final List<GetCharactersCharacterIdPlanets200Ok> colonyInstances = NetworkManager.getCharactersCharacterIdPlanets(identifier, credential.getRefreshToken(), SERVER_DATASOURCE);
		// Transform the received OK instance into a NeoCom compatible model instance.
		for (GetCharactersCharacterIdPlanets200Ok colonyOK : colonyInstances) {
			Colony col = modelMapper.map(colonyOK, Colony.class);
			// Block to add additional data not downloaded on this call.
			// To set more information about this particular planet we should call the Universe database.
			final GetUniversePlanetsPlanetIdOk planetData = NetworkManager.getUniversePlanetsPlanetId(col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
			if ( null != planetData ) col.setPlanetData(planetData);

			// During this first phase download all the rest of the information.
			// Get to the Network and download the data from the ESI api.
			final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = NetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
			if ( null != colonyStructures ) {
				// Add the original data to the colony if we need some more information later.
				col.setStructuresData(colonyStructures);
				List<ColonyCoreStructure> results = new ArrayList<>();

				// Process the structures converting the pin to the Colony structures compatible with MVC.
				final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
				for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
					ColonyCoreStructure newstruct = modelMapper.map(structureOK, ColonyCoreStructure.class);
					// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
					try {
						final String serialized = objectMapper.writeValueAsString(newstruct);
						final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), col.getPlanetId());
						final ColonyStorage storage = new ColonyStorage(storageIdentifier)
								.setColonySerialization(serialized)
								.store();
					} catch (JsonProcessingException jpe) {
						jpe.printStackTrace();
					}
					// missing code
					results.add(newstruct);
				}
				col.setStructures(results);
			}
			col.store();
			colonies.add(col);
		}
		return colonies;
	}

	public static List<ColonyCoreStructure> downloadStructures4Colony (final int characterid, final int planetid) {
		logger.info(">> [GlobalDataManager.accessStructures4Colony]");
		List<ColonyCoreStructure> results = new ArrayList<>();
		// Get the Credential that matched the received identifier.
		Credential credential = DataManagementModelStore.getCredential4Id(characterid);
		if ( null != credential ) {
			// Get to the Network and download the data from the ESI api.
			final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = NetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), planetid, credential.getRefreshToken(), SERVER_DATASOURCE);
			if ( null != colonyStructures ) {
				// Process the structures converting the pin to the Colony structures compatible with MVC.
				final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
				for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
					ColonyCoreStructure newstruct = modelMapper.map(structureOK, ColonyCoreStructure.class);
					// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
					try {
						final String serialized = objectMapper.writeValueAsString(newstruct);
						final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), planetid);
						final ColonyStorage storage = new ColonyStorage(storageIdentifier)
								.setColonySerialization(serialized)
								.store();
					} catch (JsonProcessingException jpe) {
						jpe.printStackTrace();
					}
					results.add(newstruct);
				}
			}
		} else {
			// Possible that because the application has been previously removed from memory that data is not reloaded.
			// Call the reloading mechanism and have a second opportunity.
			DataManagementModelStore.accessCredentialList();
			credential = DataManagementModelStore.getCredential4Id(characterid);
			if ( null == credential ) return new ArrayList<>();
			else return GlobalDataManager.downloadStructures4Colony(characterid, planetid);
		}
		return results;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public GlobalDataManager () {
	//		super();
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("GlobalDataManager [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	// - CLASS IMPLEMENTATION .................................................................................
	public static class ManagerOptimizedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, AbstractManager> _managerCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size () {
			return _managerCacheStore.size();
		}

		public String constructManagerIdentifier (final String type, final long identifier) {
			return new StringBuffer(type).append("/").append(identifier).toString();
		}

		public AbstractManager access (final EManagerCodes variant, long longIdentifier) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			final AbstractManager hit = _managerCacheStore.get(locator);
			return hit;
		}

		public boolean store (final EManagerCodes variant, final AbstractManager instance, final long longIdentifier) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			_managerCacheStore.put(locator, instance);
			return true;
		}

		public AbstractManager delete (final EManagerCodes variant, final long longIdentifier) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			final AbstractManager hit = _managerCacheStore.get(locator);
			_managerCacheStore.remove(locator);
			return hit;
		}
	}
	// ........................................................................................................
}
// - UNUSED CODE ............................................................................................
//[01]
