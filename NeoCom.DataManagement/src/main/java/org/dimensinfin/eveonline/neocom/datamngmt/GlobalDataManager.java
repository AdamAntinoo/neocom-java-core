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
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.Instant;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.dimensinfin.eveonline.neocom.conf.GlobalPreferencesManager;
import org.dimensinfin.eveonline.neocom.conf.IGlobalPreferencesManager;
import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IGlobalConnector;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

/**
 * This static class centralizes all the functionality to access data. It will provide a consistent api to the rest
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
public class GlobalDataManager extends GlobalDataManagerNetwork implements IGlobalConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManager");

	//public getSingleton(){
//	return new GlobalDataManager();
//}

	// --- E X C E P T I O N   L O G G I N G   S E C T I O N
	private static final List<ExceptionRecord> exceptionsIntercepted = new ArrayList();

	public static void interceptException( final Exception exceptionIntercepted ) {
		exceptionsIntercepted.add(new ExceptionRecord(exceptionIntercepted));
	}

	// --- E V E A P I   X M L   S E C T I O N
//	/** Initialize the beimin Eve Api connector to remove SSL certification. From this point on we can use the beimin
//	 * XML api to access CCP data. */
//	static {
//		EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
//		// Remove the secure XML access and configure the ApiConnector.
//		ApiConnector.setSecureXmlProcessing(false);
//	}
//
//	/**
//	 * GDM singleton to store all data, caches and references. The use of a singleton will allow to drop all data
//	 * on a single operation and restart all data caches.
//	 */

	// --- C A C H E   S T O R A G E   S E C T I O N
	private static final Hashtable<Integer, EveItem> itemCache = new Hashtable<Integer, EveItem>();
	private static Hashtable<Long, EveLocation> locationCache = new Hashtable<Long, EveLocation>();
	private static final Hashtable<Integer, ItemGroup> itemGroupCache = new Hashtable<Integer, ItemGroup>();
	private static final Hashtable<Integer, ItemCategory> itemCategoryCache = new Hashtable<Integer, ItemCategory>();
	private static MarketDataServer marketDataService = null;
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(1000);

	public static void setMarketDataManager( final MarketDataServer manager ) {
		logger.info(">> [GlobalDataManager.setMarketDataManager]");
		marketDataService = manager;
		// At this point we should have been initialized.
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = ESINetworkManager.getMarketsPrices(SERVER_DATASOURCE);
		logger.info(">> [GlobalDataManager.setMarketDataManager]> Process all market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
		logger.info("<< [GlobalDataManager.setMarketDataManager]");
	}

	public MarketDataSet searchMarketData( final int itemId, final EMarketSide side ) {
		if (null != marketDataService) return marketDataService.searchMarketData(itemId, side);
		else throw new RuntimeException("No MarketDataManager service connected.");
	}

	public static void activateMarketDataCache4Id( final int typeId ) {
		if (null != marketDataService) marketDataService.activateMarketDataCache4Id(typeId);
		else throw new RuntimeException("No MarketDataManager service connected.");
	}

	/**
	 * Returns the default and average prices found on the ESI market price list for the specified item identifier.
	 *
	 * @param typeId
	 * @return
	 */
	public GetMarketsPrices200Ok searchMarketPrice( final int typeId ) {
		final GetMarketsPrices200Ok hit = marketDefaultPrices.get(typeId);
		if (null == hit) {
			final GetMarketsPrices200Ok newprice = new GetMarketsPrices200Ok().typeId(typeId);
			newprice.setAdjustedPrice(-1.0);
			newprice.setAveragePrice(-1.0);
			return newprice;
		} else return hit;
	}

	public static void cleanEveItemCache() {
		itemCache.clear();
	}

	public static void cleanLocationsCache() {
		locationCache.clear();
	}

	public static void readLocationsDataCache() {
		logger.info(">> [GlobalDataManager.readLocationsDataCache]");
		final String cacheFileName = GlobalDataManager.getResourceString("R.cache.directorypath")
				+ GlobalDataManager.getResourceString("R.cache.locationscache.filename");
		logger.info("-- [GlobalDataManager.readLocationsDataCache]> Openning cache file: {}", cacheFileName);
		File modelStoreFile = new File(cacheFileName);
		try {
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(modelStoreFile));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			try {
				//				this.getStore().setApiKeys((HashMap<Integer, NeoComApiKey>) input.readObject());
				locationCache = (Hashtable<Long, EveLocation>) input.readObject();
				logger.info("-- [GlobalDataManager.readLocationsDataCache]> Restored cache Locations: " + locationCache.size()
						+ " entries.");
			} finally {
				input.close();
				buffer.close();
			}
		} catch (final ClassNotFoundException ex) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> ClassNotFoundException."); //$NON-NLS-1$
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> FileNotFoundException."); //$NON-NLS-1$
		} catch (final IOException ex) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> IOException."); //$NON-NLS-1$
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		} finally {
			logger.info("<< [GlobalDataManager.readLocationsDataCache]");
		}
	}

	public static void writeLocationsDatacache() {
		logger.info(">> [GlobalDataManager.writeLocationsDatacache]");
		final String cacheFileName = GlobalDataManager.getResourceString("R.cache.directorypath")
				+ GlobalDataManager.getResourceString("R.cache.locationscache.filename");
		logger.info("-- [GlobalDataManager.writeLocationsDatacache]> Openning cache file: {}", cacheFileName);
		File modelStoreFile = new File(cacheFileName);
		try {
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(modelStoreFile));
			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(locationCache);
				logger.info(
						"-- [GlobalDataManager.writeLocationsDatacache]> Wrote Locations cache: " + locationCache.size() + " entries.");
			} finally {
				output.flush();
				output.close();
				buffer.close();
			}
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [GlobalDataManager.writeLocationsDatacache]> FileNotFoundException."); //$NON-NLS-1$
		} catch (final IOException ex) {
			logger.warn("W> [GlobalDataManager.writeLocationsDatacache]> IOException."); //$NON-NLS-1$
		} finally {
			logger.info("<< [GlobalDataManager.writeLocationsDatacache]");
		}
	}

	// --- M U L T I T H R E A D I N G   S E C T I O N
	/**
	 * Background executor to use for long downloading jobs.
	 */
	private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
	private static final ExecutorService marketDataExecutor = Executors.newFixedThreadPool(2);
	private static final ExecutorService uiDataExecutor = Executors.newSingleThreadExecutor();

	public void shutdownExecutors() {
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown downloadExecutor");
			downloadExecutor.shutdown();
			downloadExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!downloadExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			downloadExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown marketDataExecutor");
			marketDataExecutor.shutdown();
			marketDataExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!marketDataExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			marketDataExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown uiDataExecutor");
			uiDataExecutor.shutdown();
			uiDataExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!uiDataExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			uiDataExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
	}

	public static void suspendThread( final long millis ) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
		}
	}

	public static Future<?> submitJob2Download( final Runnable task ) {
		return downloadExecutor.submit(task);
	}

	public static Future<?> submitJob2Generic( final Runnable task ) {
		return downloadExecutor.submit(task);
	}

	// --- D A T A B A S E   A R E A
	// --- S D E   D A T A B A S E   S E C T I O N
	/**
	 * Reference to the SDE database managers to access the Eve Online downloaded database.
	 */
	private static ISDEDBHelper neocomSDEHelper = null;

	public ISDEDBHelper getSDEDBHelper() {
		if (null == neocomSDEHelper)
			throw new RuntimeException("[NeoComDatabase]> SDE Eve database neocomSDEHelper not defined. No access to platform library to get SDE data.");
		return neocomSDEHelper;
	}

	public static void connectSDEDBConnector( final ISDEDBHelper newhelper ) {
		if (null != newhelper) neocomSDEHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> SDE Eve database neocomSDEHelper not defined. No access to platform library to get SDE data.");
	}

	public EveItem searchItem4Id( final int typeId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (itemCache.containsKey(typeId)) return itemCache.get(typeId);
		else {
			final EveItem hit = new GlobalDataManager().getSDEDBHelper().searchItem4Id(typeId);
			// Add the hit to the cache.
			itemCache.put(typeId, hit);
			return hit;
		}
	}

	public EveLocation searchLocation4Id( final long locationId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (locationCache.containsKey(locationId)) {
			// Account for a hit on the cache.
			int access = new GlobalDataManager().getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
			int hits = new GlobalDataManager().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
			return locationCache.get(locationId);
		} else {
			final EveLocation hit = new GlobalDataManager().getSDEDBHelper().searchLocation4Id(locationId);
			// Add the hit to the cache but only when it is not UNKNOWN.
			if (hit.getTypeID() != ELocationType.UNKNOWN) locationCache.put(locationId, hit);
			// Account for a miss on the cache.
			int access = new GlobalDataManager().getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
			int hits = new GlobalDataManager().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {} found at database.",
					locationId);
			return hit;
		}
	}

	public static EveLocation searchLocationBySystem( final String name ) {
		return new GlobalDataManager().getSDEDBHelper().searchLocationBySystem(name);
	}

	public static ItemGroup searchItemGroup4Id( final int targetGroupId ) {
		if (itemGroupCache.containsKey(targetGroupId)) return itemGroupCache.get(targetGroupId);
		else {
			final ItemGroup hit = new GlobalDataManager().getSDEDBHelper().searchItemGroup4Id(targetGroupId);
			// Add the hit to the cache.
			itemGroupCache.put(targetGroupId, hit);
			return hit;
		}
	}

	public static ItemCategory searchItemCategory4Id( final int targetCategoryId ) {
		if (itemCategoryCache.containsKey(targetCategoryId)) return itemCategoryCache.get(targetCategoryId);
		else {
			final ItemCategory hit = new GlobalDataManager().getSDEDBHelper().searchItemCategory4Id(targetCategoryId);
			// Add the hit to the cache.
			itemCategoryCache.put(targetCategoryId, hit);
			return hit;
		}
	}

	public int searchStationType( final long typeId ) {
		return new GlobalDataManager().getSDEDBHelper().searchStationType(typeId);
	}

	public int searchModule4Blueprint( final int bpitemID ) {
		return new GlobalDataManager().getSDEDBHelper().searchModule4Blueprint(bpitemID);
	}

	public static String searchTech4Blueprint( final int blueprintID ) {
		return new GlobalDataManager().getSDEDBHelper().searchTech4Blueprint(blueprintID);
	}

	public static int searchRawPlanetaryOutput( final int typeID ) {
		return new GlobalDataManager().getSDEDBHelper().searchRawPlanetaryOutput(typeID);
	}

	public static List<Schematics> searchSchematics4Output( final int targetId ) {
		return new GlobalDataManager().getSDEDBHelper().searchSchematics4Output(targetId);
	}

	// --- N E O C O M   P R I V A T E   D A T A B A S E   S E C T I O N
	/**
	 * Reference to the NeoCom persistece database Dao provider. This filed should be injected on startup.
	 */
	private static INeoComDBHelper neocomDBHelper = null;

	public INeoComDBHelper getNeocomDBHelper() {
		if (null == neocomDBHelper)
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
		return neocomDBHelper;
	}

	public static void connectNeoComDBConnector( final INeoComDBHelper newhelper ) {
		if (null != newhelper) neocomDBHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
	}

	/**
	 * Reads all the list of credentials stored at the Database and returns them. Activation depends on the
	 * interpretation used by the application.
	 */
	public static List<Credential> accessAllCredentials() {
		List<Credential> credentialList = new ArrayList<>();
		try {
//			final Dao<Credential, String> credentialDao = GlobalDataManager.getNeocomDBHelper().getCredentialDao();
//			final PreparedQuery<Credential> preparedQuery = credentialDao.queryBuilder().prepare();
//			credentialList = credentialDao.query(preparedQuery);
			credentialList = new GlobalDataManager().getNeocomDBHelper().getCredentialDao().queryForAll();
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;
	}


	// --- S E R I A L I Z A T I O N   I N T E R F A C E
//	public static String serializeCredentialList( final List<Credential> credentials ) {
//		// Use my own serialization control to return the data to generate exactly what I want.
//		String contentsSerialized = "[jsonClass: \"Exception\"," +
//				"message: \"Unprocessed data. Possible JsonProcessingException exception.\"]";
//		try {
//			contentsSerialized = jsonMapper.writeValueAsString(credentials);
//		} catch (JsonProcessingException jpe) {
//			jpe.printStackTrace();
//		}
//		return contentsSerialized;
//	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("GlobalDataManager [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	// - CLASS IMPLEMENTATION .................................................................................
//	public static class ManagerOptimizedCache {
//
//		// - F I E L D - S E C T I O N ............................................................................
//		private Hashtable<String, AbstractManager> _managerCacheStore = new Hashtable();
//
//		// - M E T H O D - S E C T I O N ..........................................................................
//		public int size() {
//			return _managerCacheStore.size();
//		}
//
//		public String constructManagerIdentifier( final String type, final long identifier ) {
//			return new StringBuffer(type).append("/").append(identifier).toString();
//		}
//
//		public AbstractManager access( final EManagerCodes variant, long longIdentifier ) {
//			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
//			final AbstractManager hit = _managerCacheStore.get(locator);
//			return hit;
//		}
//
//		public boolean store( final EManagerCodes variant, final AbstractManager instance, final long longIdentifier ) {
//			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
//			_managerCacheStore.put(locator, instance);
//			return true;
//		}
//
//		public AbstractManager delete( final EManagerCodes variant, final long longIdentifier ) {
//			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
//			final AbstractManager hit = _managerCacheStore.get(locator);
//			_managerCacheStore.remove(locator);
//			return hit;
//		}
//	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ModelTimedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, ICollaboration> _instanceCacheStore = new Hashtable();
		private Hashtable<String, Instant> _timeCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size() {
			return _instanceCacheStore.size();
		}

		public ICollaboration access( final EModelVariants variant, long longIdentifier ) {
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				if (null != hit) {
					final Instant expitationTime = _timeCacheStore.get(locator);
					if (expitationTime.isBefore(Instant.now())) return null;
					else return hit;
				}
			}
			return null;
		}

		public ICollaboration delete( final EModelVariants variant, long longIdentifier ) {
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				_instanceCacheStore.put(locator, null);
				return hit;
			}
			return null;
		}

		public boolean store( final EModelVariants variant, final ICollaboration instance, final Instant expirationTime, final long longIdentifier ) {
			// Store command for PILOTV1 instances.
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				_instanceCacheStore.put(locator, instance);
				_timeCacheStore.put(locator, expirationTime);
				return true;
			}
//			// Store command for APIKEY instances.
//			if (variant == EModelVariants.APIKEY) {
//				final String locator = EModelVariants.APIKEY.name() + "/" + Long.valueOf(longIdentifier).toString();
//				_instanceCacheStore.put(locator, instance);
//				_timeCacheStore.put(locator, expirationTime);
//				return true;
//			}
			return false;
		}
	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
//	public static class ShipSerializer extends JsonSerializer<Ship> {
//		// - F I E L D - S E C T I O N ............................................................................
//
//		// - M E T H O D - S E C T I O N ..........................................................................
//		@Override
//		public void serialize( final Ship value, final JsonGenerator jgen, final SerializerProvider provider )
//				throws IOException, JsonProcessingException {
//			jgen.writeStartObject();
//			jgen.writeStringField("jsonClass", value.getJsonClass());
//			jgen.writeNumberField("assetId", value.getAssetId());
//			jgen.writeNumberField("typeId", value.getTypeId());
//			jgen.writeNumberField("ownerId", value.getOwnerID());
//			jgen.writeStringField("name", value.getItemName());
//			jgen.writeStringField("category", value.getCategory());
//			jgen.writeStringField("groupName", value.getGroupName());
//			jgen.writeStringField("tech", value.getTech());
//			jgen.writeStringField("userLabel", value.getUserLabel());
//			jgen.writeNumberField("price", value.getItem().getPrice());
//			jgen.writeNumberField("highesBuyerPrice", value.getItem().getHighestBuyerPrice().getPrice());
//			jgen.writeNumberField("lowerSellerPrice", value.getItem().getLowestSellerPrice().getPrice());
//			jgen.writeObjectField("item", value.getItem());
//			jgen.writeEndObject();
//		}
//	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ExceptionRecord {
		// - F I E L D - S E C T I O N ............................................................................
		private long timeStamp = 0;
		private Exception exceptionRegistered = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ExceptionRecord( final Exception newexception ) {
			this.exceptionRegistered = newexception;
			this.timeStamp = Instant.now().getMillis();
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public void setTimeStamp( final long timeStamp ) {
			this.timeStamp = timeStamp;
		}

		public void setTimeStamp( final Instant timeStamp ) {
			this.timeStamp = timeStamp.getMillis();
		}
	}
	// ........................................................................................................

//	// - CLASS IMPLEMENTATION ...................................................................................
//	public static class SessionContext {
//		// - S T A T I C - S E C T I O N ..........................................................................
//
//		// - F I E L D - S E C T I O N ............................................................................
//		private Credential credential = null;
//
//		// - C O N S T R U C T O R - S E C T I O N ................................................................
//		public SessionContext() {
//		}
//
//		// - M E T H O D - S E C T I O N ..........................................................................
//
//		public Credential getCredential() {
//			return credential;
//		}
//
//		public void setCredential( final Credential credential ) {
//			this.credential = credential;
//		}
//
//		// --- D E L E G A T E D   M E T H O D S
//		@Override
//		public String toString() {
//			return new StringBuffer("SessionContext [ ")
//					.append("Credential:").append(credential.getAccountName()).append(" ")
//					.append("]")
////				.append("->").append(super.toString())
//					.toString();
//		}
//	}
//	// ........................................................................................................
}
// - UNUSED CODE ............................................................................................
//[01]
