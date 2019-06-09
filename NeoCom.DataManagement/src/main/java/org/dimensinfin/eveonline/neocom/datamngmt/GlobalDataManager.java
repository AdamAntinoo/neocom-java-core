package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.exception.NeoComError;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.interfaces.IGlobalConnector;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
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
public class GlobalDataManager extends GlobalDataManagerFileSystem implements IGlobalConnector {
	//	protected static Logger logger = LoggerFactory.getLogger(GlobalDataManager.class);

	protected static GlobalDataManager singleton;

	public static GlobalDataManager getSingleton() {
		Objects.requireNonNull(singleton);
		return singleton;
	}

	// - M U L T I T H R E A D I N G   S E C T I O N
	/** Background executor to use for long downloading jobs. */
	private static final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
	//	private static final ExecutorService marketDataExecutor = Executors.newFixedThreadPool(2);
	@Deprecated
	private static final ExecutorService uiDataExecutor = Executors.newSingleThreadExecutor();

	public static ExecutorService getBackgroundExecutor() {
		return backgroundExecutor;
	}

	// - N E T W O R K   S T A T U S
	public static boolean getNetworkStatus() {
		return true;
	}

	// - C A C H E   S T O R A G E   S E C T I O N
	private static final Hashtable<Integer, EveItem> itemCache = new Hashtable<Integer, EveItem>();
	private static Hashtable<Long, EveLocation> locationCache = new Hashtable<Long, EveLocation>();
	private static final Hashtable<Integer, ItemGroup> itemGroupCache = new Hashtable<Integer, ItemGroup>();
	private static final Hashtable<Integer, ItemCategory> itemCategoryCache = new Hashtable<Integer, ItemCategory>();


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
		logger.info("-- [GlobalDataManager.readLocationsDataCache]> Opening cache file: {}", cacheFileName);
		try {
			final BufferedInputStream buffer = new BufferedInputStream(
					GlobalDataManager.openResource4Input(cacheFileName)
			);
			final ObjectInputStream input = new ObjectInputStream(buffer);
			try {
				synchronized (locationCache) {
					locationCache = (Hashtable<Long, EveLocation>) input.readObject();
					logger.info("-- [GlobalDataManager.readLocationsDataCache]> Restored cache Locations: " + locationCache.size()
							            + " entries.");
				}
			} finally {
				input.close();
				buffer.close();
			}
		} catch (final ClassNotFoundException ex) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> ClassNotFoundException. {}", ex.getMessage()); //$NON-NLS-1$
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> FileNotFoundException. {}", fnfe.getMessage()); //$NON-NLS-1$
		} catch (final IOException ioe) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> IOException. {}", ioe.getMessage()); //$NON-NLS-1$
		} catch (final IllegalArgumentException iae) {
			logger.warn("W> [GlobalDataManager.readLocationsDataCache]> IllegalArgumentException. {}", iae.getMessage()); //$NON-NLS-1$
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
		//		File modelStoreFile = new File(cacheFileName);
		try {
			final BufferedOutputStream buffer = new BufferedOutputStream(
					GlobalDataManager.openResource4Output(cacheFileName)
			);
			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				synchronized (locationCache) {
					output.writeObject(locationCache);
					logger.info(
							"-- [GlobalDataManager.writeLocationsDatacache]> Wrote Locations cache: " + locationCache.size() + " entries.");
				}
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


	public void shutdownExecutors() {
		//		try {
		//			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown updaterExecutor");
		//			downloadExecutor.shutdown();
		//			downloadExecutor.awaitTermination(1, TimeUnit.MINUTES);
		//		} catch (final InterruptedException iee) {
		//			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		//		} finally {
		//			if (!downloadExecutor.isTerminated()) {
		//				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		//			}
		//			downloadExecutor.shutdownNow();
		//			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		//		}
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown backgroundExecutor");
			backgroundExecutor.shutdown();
			backgroundExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!backgroundExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			backgroundExecutor.shutdownNow();
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

	//	public static Future<?> submitJob2Download( final Runnable task ) {
	//		return downloadExecutor.submit(task);
	//	}
	//
	//	public static Future<?> submitJob2Generic( final Runnable task ) {
	//		return downloadExecutor.submit(task);
	//	}

	public Future<?> submitJob( final Runnable task ) {
		return backgroundExecutor.submit(task);
	}

	public Future<?> submitCall( final Callable<List<NeoComError>> task ) {
		return backgroundExecutor.submit(task);
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
			final EveItem hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchItem4Id(typeId);
			// Add the hit to the cache.
			itemCache.put(typeId, hit);
			return hit;
		}
	}

	public EveLocation searchLocation4Id( final long locationId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (locationCache.containsKey(locationId)) {
			// Account for a hit on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
			return locationCache.get(locationId);
		} else {
			final EveLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
			// Add the hit to the cache but only when it is not UNKNOWN.
			if (hit.getTypeId() != ELocationType.UNKNOWN) locationCache.put(locationId, hit);
			// Account for a miss on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {} found at database.",
					locationId);
			return hit;
		}
	}

	public static EveLocation searchLocationBySystem( final String name ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchLocationBySystem(name);
	}

//	public static ItemGroup searchItemGroup4Id( final int targetGroupId ) {
//		if (itemGroupCache.containsKey(targetGroupId)) return itemGroupCache.get(targetGroupId);
//		else {
//			final ItemGroup hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchItemGroup4Id(targetGroupId);
//			// Add the hit to the cache.
//			itemGroupCache.put(targetGroupId, hit);
//			return hit;
//		}
//	}

	public static ItemCategory searchItemCategory4Id( final int targetCategoryId ) {
		if (itemCategoryCache.containsKey(targetCategoryId)) return itemCategoryCache.get(targetCategoryId);
		else {
			final ItemCategory hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchItemCategory4Id(targetCategoryId);
			// Add the hit to the cache.
			itemCategoryCache.put(targetCategoryId, hit);
			return hit;
		}
	}

	public int searchStationType( final long typeId ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchStationType(typeId);
	}

	public int searchModule4Blueprint( final int bpitemID ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchModule4Blueprint(bpitemID);
	}

	public int searchBlueprint4Module( final int moduleId ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchBlueprint4Module(moduleId);
	}

	public static String searchTech4Blueprint( final int blueprintID ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchTech4Blueprint(blueprintID);
	}

	public static int searchRawPlanetaryOutput( final int typeID ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchRawPlanetaryOutput(typeID);
	}

	public static List<Schematics> searchSchematics4Output( final int targetId ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchSchematics4Output(targetId);
	}

	public static List<Resource> searchListOfMaterials4Blueprint( final int bpid ) {
		return GlobalDataManager.getSingleton().getSDEDBHelper().searchListOfMaterials(bpid);
	}

	// - C O N S T R U C T O R

	protected GlobalDataManager() { }

	private GlobalDataManager( final IConfigurationProvider configurationProvider
			, final IFileSystem fileSystemAdapter
			, final ESIGlobalAdapter esiAdapter ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
		this.esiAdapter = esiAdapter;
	}

	// - B U I L D E R
	public static class Builder {
		protected GlobalDataManager onConstruction;

		public Builder( final IConfigurationProvider configurationProvider
				, final IFileSystem fileSystemAdapter
				, final ESIGlobalAdapter esiAdapter ) {
			Objects.requireNonNull(configurationProvider);
			Objects.requireNonNull(fileSystemAdapter);
			Objects.requireNonNull(esiAdapter);
			this.onConstruction = new GlobalDataManager(configurationProvider, fileSystemAdapter, esiAdapter);
		}

		public Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public GlobalDataManager build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			singleton = this.onConstruction;
			return this.onConstruction;
		}
	}
}
