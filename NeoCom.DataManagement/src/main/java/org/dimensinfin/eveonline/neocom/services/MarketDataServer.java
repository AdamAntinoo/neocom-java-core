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
package org.dimensinfin.eveonline.neocom.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.joda.time.Instant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.enums.PreferenceKeys;
import org.dimensinfin.eveonline.neocom.market.EVEMarketDataParser;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.market.TrackEntry;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketDataServer {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("MarketDataServer");
	public static int cpuCount = 1;

	static {
		if ( GlobalDataManager.getResourceString("R.runtime.platform", "Android").equalsIgnoreCase("Android") )
			cpuCount = 2;
		else
			cpuCount = Runtime.getRuntime().availableProcessors();
	}

	private static final ExecutorService marketUpdaterExecutor = Executors.newFixedThreadPool(cpuCount);
	public static final List<String> stationList = new ArrayList<>();

	// - F I E L D - S E C T I O N ............................................................................
	private HashMap<Integer, MarketDataSet> buyMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
	private HashMap<Integer, MarketDataSet> sellMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
	private HashMap<Integer, Instant> expirationTimeMarketData = new HashMap<Integer, Instant>(1000);
	private final MarketDataJobDownloadManager downloadManager = new MarketDataJobDownloadManager(cpuCount);
	private List<Future<MarketDataSet>> runningJobsList = new Vector<>(1000);

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Does the service initialization and start the service. This mean to read back from storage the latest saved cache and then
	 * start the background services to download more Market data.
	 * @return
	 */
	public MarketDataServer start() {
		logger.info(">> [MarketDataServer.start]");
		try {
			readMarketDataCacheFromStorage();
			// Read the configured list of preferential marked data hubs from the assets store.
			final String stationsFileName = GlobalDataManager.getResourceString("R.cache.marketdata.markethubs.configuration.path");
			BufferedReader reader = new BufferedReader(new InputStreamReader(GlobalDataManager.openAsset4Input(stationsFileName)));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					stationList.add(line);
				}
			} catch ( IOException ioe ) {
				// If there are exceptions add some key stations.
				stationList.add("1.0 Domain - Amarr");
				stationList.add("0.9 The Forge - Jita");
			} finally {
				reader.close();
			}
		} catch ( FileNotFoundException fnfe ) {
			// If there are exceptions add some key stations.
			stationList.add("1.0 Domain - Amarr");
			stationList.add("0.9 The Forge - Jita");
		} catch ( IOException ioe ) {
			// If there are exceptions add some key stations.
			stationList.add("1.0 Domain - Amarr");
			stationList.add("0.9 The Forge - Jita");
		}
		logger.info("<< [MarketDataServer.start]");
		return this;
	}

	/**
	 * Clone the list of stations to be consumed by a filter.
	 * @return a clone of the marter list read from configuration.
	 */
	public static List<String> getStationList() {
		final List<String> result = new ArrayList<>();
		for ( String station : stationList ) {
			result.add(station);
		}
		return result;
	}

	public MarketDataServer clear() {
		logger.info(">> [MarketDataServer.clear]");
		synchronized (buyMarketDataCache) {
			buyMarketDataCache.clear();
		}
		synchronized (sellMarketDataCache) {
			sellMarketDataCache.clear();
		}
		//		downloadManager.clear();
		logger.info("<< [MarketDataServer.clear]");
		return this;
	}

	public synchronized void readMarketDataCacheFromStorage() {
		logger.info(">> [MarketDataServer.readMarketDataCacheFromStorage]");
		final String cacheFileName = GlobalDataManager.getResourceString("R.cache.directorypath")
				+ GlobalDataManager.getResourceString("R.cache.marketdata.cachename");
		logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Opening cache file: {}", cacheFileName);
		try {
			// Open the file on the Application storage area.
			final BufferedInputStream buffer = new BufferedInputStream(GlobalDataManager.openResource4Input(cacheFileName));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Opening cache file: {}", cacheFileName);
			try {
				synchronized (buyMarketDataCache) {
					buyMarketDataCache = (HashMap<Integer, MarketDataSet>) input.readObject();
					logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Restored cache BUY: " + buyMarketDataCache.size()
							+ " entries.");
				}
				synchronized (sellMarketDataCache) {
					sellMarketDataCache = (HashMap<Integer, MarketDataSet>) input.readObject();
					logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Restored cache SELL: " + sellMarketDataCache.size()
							+ " entries.");
				}
				synchronized (expirationTimeMarketData) {
					expirationTimeMarketData = (HashMap<Integer, Instant>) input.readObject();
					logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Restored expiration times: " + expirationTimeMarketData.size()
							+ " entries.");
				}
			} finally {
				input.close();
				buffer.close();
			}
		} catch ( final ClassNotFoundException ex ) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> ClassNotFoundException.");
		} catch ( final FileNotFoundException fnfe ) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> FileNotFoundException. {}"
					, cacheFileName);
		} catch ( final IOException ex ) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> IOException.");
		} catch ( final RuntimeException rex ) {
			rex.printStackTrace();
		}
	}

	public synchronized void writeMarketDataCacheToStorage() {
		final String cacheFileName = GlobalDataManager.getResourceString("R.cache.directorypath")
				+ GlobalDataManager.getResourceString("R.cache.marketdata.cachename");
		try {
			final BufferedOutputStream buffer = new BufferedOutputStream(
					GlobalDataManager.openResource4Output(cacheFileName)
			);
			final ObjectOutput output = new ObjectOutputStream(buffer);
			// Block the object to write before handling them.
			try {
				synchronized (buyMarketDataCache) {
					output.writeObject(buyMarketDataCache);
					logger.info(
							"-- [MarketDataServer.writeCacheToStorage]> Wrote cache BUY: " + buyMarketDataCache.size() + " entries.");
				}
				synchronized (sellMarketDataCache) {
					output.writeObject(sellMarketDataCache);
					logger.info(
							"-- [MarketDataServer.writeCacheToStorage]> Wrote cache SELL: " + sellMarketDataCache.size() + " entries.");
				}
				synchronized (expirationTimeMarketData) {
					output.writeObject(expirationTimeMarketData);
					logger.info(
							"-- [MarketDataServer.writeCacheToStorage]> Wrote expiration times: " + expirationTimeMarketData.size() + " entries.");
				}
			} finally {
				output.flush();
				output.close();
				buffer.close();
			}
		} catch ( final FileNotFoundException fnfe ) {
			logger.warn("W> [MarketDataServer.writeCacheToStorage]> FileNotFoundException. {}", cacheFileName);
		} catch ( final IOException ex ) {
			logger.warn("W> [MarketDataServer.writeCacheToStorage]> IOException."); //$NON-NLS-1$
		}
	}

	public synchronized int reportMarketDataJobs() {
		// Count the jobs.
		int pending = 0;
		int done = 0;
		synchronized (runningJobsList) {
			for ( Future<MarketDataSet> fut : runningJobsList ) {
				if ( fut.isDone() ) done++;
				else pending++;
			}
		}
		logger.info(">< [MarketDataServer.reportMarketDataJobs]> Pending: {}.", pending);
		logger.info(">< [MarketDataServer.reportMarketDataJobs]> Done   : {}.", done);
		logger.info(">< [MarketDataServer.reportMarketDataJobs]> TOTAL  : {}.", pending + done);
		return pending;
	}

	/**
	 * Search for this market data on the cache.
	 * The cache used for the search depends on the side parameter received on the call. All default prices are references to
	 * the cost of the price to be spent to buy the item.
	 * Cached items no longer are read from disk on demand. The disk cache is read at initialization and then on the new data is
	 * just stored on memory and write back down to disk sometimes. This way the cache is simplified to a single file and all the
	 * io related to disk is minimized.
	 * If the data is not located on the case call the market data downloader and processor to get a new copy
	 * and store it on the cache.
	 * @param itemId item id code of the item assigned to this market request.
	 * @param side   differentiates if we like to BUY or SELL the item.
	 * @return the cached data or an empty locator ready to receive downloaded data.
	 */
	public Future<MarketDataSet> searchMarketData( final int itemId, final EMarketSide side ) {
		logger.info(">< [MarketDataServer.searchMarketData]> ItemId: {}/{}.", itemId, side.name());
		// Filter out invalid localizers.
		if ( itemId < 1 ) {
			logger.info("-- [MarketDataServer.searchMarketData]> Market Data download replaced because item id is not valid [{}]."
					, itemId);
			final Future<MarketDataSet> fut = marketUpdaterExecutor.submit(() -> {
				return new MarketDataSet(34, side);
			});
			// Register the Future request onto the list to count them down.
			synchronized (runningJobsList) {
				runningJobsList.add(fut);
			}
			return fut;
		}
		// Check if the user preferences allows to go to the market downloader.
		if ( GlobalDataManager.getDefaultSharedPreferences().getBooleanPreference(PreferenceKeys.prefkey_BlockMarket.name(), true) ) {
			logger.info("-- [MarketDataServer.searchMarketData]> Market Data download cancelled because preferences 'BlockMarket'.");
			final Future<MarketDataSet> fut = marketUpdaterExecutor.submit(() -> {
				return new MarketDataSet(itemId, side);
			});
			// Register the Future request onto the list to count them down.
			synchronized (runningJobsList) {
				runningJobsList.add(fut);
			}
			return fut;
		} else {
			final Future<MarketDataSet> fut = marketUpdaterExecutor.submit(() -> {
				// Search on the cache. By default load the SELLER as If I am buying the item.
				HashMap<Integer, MarketDataSet> cache = sellMarketDataCache;
				if ( side == EMarketSide.BUYER ) {
					cache = buyMarketDataCache;
				}
				MarketDataSet entry = cache.get(itemId);
				if ( null == entry ) {
					// The data is not on the cache and neither on the latest disk copy read at initialization.
					// Do a new market data download process.
					try {
						// Report the number of jobs pending.
						reportMarketDataJobs();
						final MarketDataSet data = downloadManager.doMarketDataRequest(itemId, side);
						if ( null != data ) {
							// Save the data on the cache and update the expiration time.
							data.setSide(side);
							synchronized (cache) {
								cache.put(itemId, data);
								expirationTimeMarketData.put(itemId, Instant.now().plus(TimeUnit.HOURS.toMillis(48)));
								entry = data;
							}
						} else {
							// Return some basic data but post a new request.
							entry = new MarketDataSet(itemId, side);
							// Post again another Future to refresh the cache value.
							searchMarketData(itemId, side);
						}
					} catch ( RuntimeException rtex ) {
						rtex.printStackTrace();
						entry = new MarketDataSet(itemId, side);
					}
				} else {
					logger.info("-- [MarketDataServer.searchMarketData]> Cache hit on memory.");
					// Check again the expiration time. If expired clear cache and request a refresh.
					Instant expirationTime = expirationTimeMarketData.get(itemId);
					if ( null == expirationTime ) expirationTime = Instant.now().minus(TimeUnit.MINUTES.toMillis(1));
					if ( expirationTime.isBefore(Instant.now()) ) {
						// Clear the cache entry.
						synchronized (cache) {
							cache.remove(itemId);
						}
						// Post again another Future to refresh the cache value.
						searchMarketData(itemId, side);
					}
				}
				return entry;
			});      // Register the Future request onto the list to count them down.
			synchronized (runningJobsList) {
				runningJobsList.add(fut);
			}
			return fut;
		}
	}

	/**
	 * The main responsibility of this class is to have a unique list of update jobs. If every minute we check for
	 * data to update and that data is already scheduled but not completed we can found a second and third requests
	 * that will also have to be executes.
	 * So we need something between the launcher of updated and the executor that removed already registered
	 * updates and do not request them again.
	 * Using an specific executor for this task will isolate the run effect from other tasks but anyway it
	 * requires some way for the job to notify its state so it can clear the request after completed or remove it
	 * if the process fails or gets interrupted.
	 * With the use of utures we can track pending jobs and be sure the update mechanics are followed as
	 * requested.
	 */
	//- CLASS IMPLEMENTATION ...................................................................................
	public static class MarketDataJobDownloadManager {
		// - S T A T I C - S E C T I O N ..........................................................................
		//		public int marketJobCounter = 0;
		//		private static ExecutorService marketDataDownloadExecutor = null;
		//		public static final Hashtable<String, Future<MarketDataSet>> runningJobs = new Hashtable();

		// - F I E L D - S E C T I O N ............................................................................
		private final int threadSize;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public MarketDataJobDownloadManager( final int threadSize ) {
			this.threadSize = threadSize;
			//			marketDataDownloadExecutor = Executors.newFixedThreadPool(threadSize);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		//		public void clear() {
		//			runningJobs.clear();
		//		}

		//		/**
		//		 * Submits a request for a new Market data update. This should create a new job and update the job counters.
		//		 *
		//		 * @param typeId the job to update some information.
		//		 */
		//		public synchronized MarketDataSet addMarketDataRequest( final int typeId ) {
		//			// TODO - This code should be trimmed out once the new model is working. There is no more job launching.
		//			// Launch the updater job only if the market data updater process is allowed.
		////			if (!GlobalDataManager.getDefaultSharedPreferences().getBoolean(PreferenceKeys.prefkey_BlockMarket.name())) {
		//				final String identifier = generateMarketDataJobReference(typeId);
		//				logger.info(">> [MarketDataJobDownloadManager.addMarketDataRequest]");
		//				try {
		//					// Search for the job to detect duplications
		//					final Future<MarketDataSet> hit = runningJobs.get(identifier);
		//					if (null == hit) {
		//						// New job. Launch it and store the reference.
		//						runningJobs.put(identifier, launchDownloadJob(typeId));
		//					} else {
		//						// Check for job completed.
		//						if (hit.isDone()) {
		//							// The job with this same reference has completed. We can launch a new one.
		//							runningJobs.put(identifier, launchDownloadJob(typeId));
		//						}
		//					}
		//					return hit;
		//				} catch (RuntimeException neoe) {
		//					neoe.printStackTrace();
		//				}
		////			}
		//			return null;
		//		}
		public MarketDataSet doMarketDataRequest( final int typeId, final EMarketSide side ) {
			final int localizer = typeId;
			logger.info(">> [MarketDataJobDownloadManager.doMarketDataRequest]> Processing type {}", localizer);
			// Download the market data and return it to store the new data into the cache.
			final EveItem item = new GlobalDataManager().searchItem4Id(localizer);
			MarketDataSet reference = new MarketDataSet(localizer, side);
			//			try {
			logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing {}", side.name());
			List<TrackEntry> marketEntries = new ArrayList();
			// Check which data provider should be used for the data.
			// Preference is: eve-market-data/eve-central/esi-marketdata
			try {
				if ( marketEntries.size() < 1 ) {
					if ( GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true) )
						marketEntries = parseMarketDataEMD(item.getName(), side);
				}
				if ( marketEntries.size() < 1 ) {
					if ( GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", false) )
						marketEntries = parseMarketDataEC(localizer, side);
				}
				if ( marketEntries.size() < 1 ) {
					if ( GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", false) )
						marketEntries = parseMarketDataESI(localizer, side);
				}
				List<MarketDataEntry> hubData = extractMarketData(marketEntries);
				logger.info("-- [MarketDataJobDownloadManager.doMarketDataRequest]> Storing data entries {}", hubData.size());
				reference.setData(hubData);
			} catch ( SAXException saxe ) {
				logger.error("E [MarketDataJobDownloadManager.parseMarketDataEMD]> Parsing exception while downloading market data for module [" + item.getName() + "]. " + saxe.getMessage());
				reference = null;
			} catch ( IOException ioe ) {
				logger.error("E [MarketDataJobDownloadManager.parseMarketDataEMD]> Error parsing the market information. " + ioe.getMessage());
				reference = null;
			}
			return reference;
		}

		//		protected MarketDataSet launchDownloadJob( final int typeId ) {
		//			try {
		//				logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob]> Launching job {}", typeId);
		//				marketJobCounter++;
		//				final Future<?> future = marketDataDownloadExecutor.submit(() -> {
		//					final int localizer = typeId;
		//					logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing type {}", localizer);
		//					// Download the market data and store the new data into the cache.
		//					final EveItem item = new GlobalDataManager().searchItem4Id(localizer);
		//					try {
		//						logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing SELLER");
		//						List<TrackEntry> marketEntries = new ArrayList();
		//						// Check which data provider should be used for the data.
		//						// Preference is: eve-market-data/eve-central/esi-marketdata
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true))
		//								marketEntries = parseMarketDataEMD(item.getName(), EMarketSide.SELLER);
		//						}
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", true))
		//								marketEntries = parseMarketDataEC(localizer, EMarketSide.SELLER);
		//						}
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", true))
		//								marketEntries = parseMarketDataESI(localizer, EMarketSide.SELLER);
		//						}
		//
		//						List<MarketDataEntry> hubData = extractMarketData(marketEntries);
		//						// Update the structures related to the newly downloaded data.
		////					MarketDataSet reference = new MarketDataSet(localizer, EMarketSide.SELLER);
		//						MarketDataSet reference = new GlobalDataManager().searchMarketData(localizer, EMarketSide.SELLER);
		//						reference.setData(hubData);
		//						logger.info("-- [MarketDataJobDownloadManager.launchDownloadJob.submit]> Storing data entries {}", hubData.size());
		//					} catch (RuntimeException rtex) {
		//						rtex.printStackTrace();
		//					}
		//					// Do the same for the other side.
		//					try {
		//						logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing BUYER");
		//						List<TrackEntry> marketEntries = new ArrayList();
		//						// Check which data provider should be used for the data.
		//						// Preference is: eve-market-data/eve-central/esi-marketdata
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true))
		//								marketEntries = parseMarketDataEMD(item.getName(), EMarketSide.BUYER);
		//						}
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", true))
		//								marketEntries = parseMarketDataEC(localizer, EMarketSide.BUYER);
		//						}
		//						if (marketEntries.size() < 1) {
		//							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", true))
		//								marketEntries = parseMarketDataESI(localizer, EMarketSide.BUYER);
		//						}
		//
		//						List<MarketDataEntry> hubData = extractMarketData(marketEntries);
		//						// Update the structures related to the newly downloaded data.
		//						MarketDataSet reference = new MarketDataSet(localizer, EMarketSide.BUYER);
		//						reference.setData(hubData);
		//						logger.info("-- [MarketDataJobDownloadManager.launchDownloadJob.submit]> Storing data entries {}", hubData.size());
		//					} catch (RuntimeException rtex) {
		//						rtex.printStackTrace();
		//					}
		//					// If there was no exceptions now the market data is stored on the cache.
		//					// But still we need to setup the cache time.
		//					GlobalDataManager.activateMarketDataCache4Id(localizer);
		//					// Decrement the counter.
		//					marketJobCounter--;
		//					logger.info("<< [MarketDataJobDownloadManager.launchDownloadJob.submit]> Completing job {}", typeId);
		//				});
		//				return (Future<MarketDataSet>) future;
		//			} finally {
		//				logger.info("<< [MarketDataJobDownloadManager.launchDownloadJob]");
		//			}
		//		}

		//		public int countRunningJobs() {
		//			// Count the running or pending jobs to update the ActionBar counter.
		//			int counter = 0;
		//			for (Future<?> future : runningJobs.values()) {
		//				if (!future.isDone()) counter++;
		//			}
		//			marketJobCounter = counter;
		//			return counter;
		//		}

		/**
		 * Converts the raw TrackEntry structures into aggregated data by location and system. This has a new
		 * implementation that will use real location data for the system to better classify and store the market
		 * data information. It will also remove the current limit on the selected market hubs and will aggregate
		 * all the systems found into the highsec and other sec categories.
		 * @param entries
		 * @return
		 */
		protected List<MarketDataEntry> extractMarketData( final List<TrackEntry> entries ) {
			final HashMap<String, MarketDataEntry> stations = new HashMap<String, MarketDataEntry>();
			final List<String> stationList = MarketDataServer.getStationList();
			final Iterator<TrackEntry> meit = entries.iterator();
			while (meit.hasNext()) {
				final TrackEntry entry = meit.next();
				// Filtering for only preferred market hubs.
				if ( filterStations(entry, stationList) ) {
					// Start searching for more records to sum all entries with the same or a close price to get
					// a better understanding of the market depth. That information is not to relevant so make a
					// best try.
					int stationQty = entry.getQty();
					final String stationName = entry.getStationName();
					final double stationPrice = entry.getPrice();
					while (meit.hasNext()) {
						final TrackEntry searchEntry = meit.next();
						// Check that station and prices are the same or price is inside margin.
						if ( searchEntry.getStationName().equals(stationName) ) {
							if ( (stationPrice >= (searchEntry.getPrice() * 0.99))
									&& (stationPrice <= (searchEntry.getPrice() * 1.01)) ) {
								stationQty += searchEntry.getQty();
							} else {
								break;
							}
						} else {
							break;
						}
					}
					// Convert to standard location.
					final EveLocation entryLocation = this.generateLocation(stationName);
					final MarketDataEntry data = new MarketDataEntry(entryLocation);
					data.setQty(stationQty);
					data.setPrice(stationPrice);
					stations.put(stationName, data);
					stationList.remove(entry.getStationName());
				}
			}
			return new ArrayList<>(stations.values());
		}

		//		protected List<String> getMarketHubs() {
		//			final List<String> stationList = new ArrayList<>();
		//			//		stationList.add("0.8 Tash-Murkon - Tash-Murkon Prime");
		//			stationList.add("1.0 Domain - Amarr");
		//			stationList.add("1.0 Domain - Sarum Prime");
		//			stationList.add("0.8 Devoid - Hati");
		//			stationList.add("0.6 Devoid - Esescama");
		//			stationList.add("0.8 Heimatar - Odatrik");
		//			stationList.add("0.9 Heimatar - Rens");
		//			stationList.add("0.8 Heimatar - Frarn");
		//			stationList.add("0.9 Heimatar - Lustrevik");
		//			stationList.add("0.9 Heimatar - Eystur");
		//			stationList.add("0.5 Metropolis - Hek");
		//			stationList.add("0.5 Sinq Laison - Deltole");
		//			stationList.add("0.5 Sinq Laison - Aufay");
		//			stationList.add("0.9 Sinq Laison - Dodixie");
		//			stationList.add("0.9 Essence - Renyn");
		//			stationList.add("0.7 Kador - Romi");
		//			stationList.add("0.8 The Citadel - Kaaputenen");
		//			stationList.add("1.0 The Forge - Urlen");
		//			stationList.add("1.0 The Forge - Perimeter");
		//			stationList.add("0.9 The Forge - Jita");
		//			return stationList;
		//		}
		//
		protected boolean filterStations( final TrackEntry entry, final List<String> stationList ) {
			//			final Iterator<String> slit = stationList.iterator();
			//			while (slit.hasNext()) {
			//				final String stationNameMatch = slit.next();
			//				final String station = entry.getStationName();
			//				if (station.contains(stationNameMatch)) return true;
			//			}
			final String station = entry.getStationName();
			for ( String stationNameMatch : stationList ) {
				if ( station.contains(stationNameMatch) ) return true;
			}
			return false;
		}

		/**
		 * Creates a EveLocation instance from the data retrieved from the eve-marketdata records. We can only go to the system
		 * because the source data does not include the station.
		 * @param hubName
		 * @return
		 */
		protected EveLocation generateLocation( String hubName ) {
			// Extract system name from the station information.
			final int pos = hubName.indexOf(" ");
			final String hubSecurity = hubName.substring(0, pos);
			// Divide the name into region-system
			hubName = hubName.substring(pos + 1, hubName.length());
			final String[] parts = hubName.split(" - ");
			final String hubSystem = parts[1].trim();
			final String hubRegion = parts[0].trim();

			// Search for the system on the list of locations.
			return GlobalDataManager.searchLocationBySystem(hubSystem);
		}

		protected String generateMarketDataJobReference( final int typeId ) {
			return new StringBuffer()
					.append("MDJ:")
					.append(typeId)
					.append(":")
					//					.append(Instant.now().toString())
					.toString();
		}

		protected List<TrackEntry> parseMarketDataESI( final int typeId, final EMarketSide opType ) {
			logger.info(">> [MarketDataJobDownloadManager.parseMarketDataESI]");
			try {
				List<TrackEntry> marketEntries = new ArrayList<>();
				return marketEntries;
			} finally {
				logger.info("<< [MarketDataJobDownloadManager.parseMarketDataESI]");
			}
		}

		protected List<TrackEntry> parseMarketDataEMD( final String itemName, final EMarketSide opType ) throws IOException, SAXException {
			logger.info(">> [MarketDataJobDownloadManager.parseMarketDataEMD]");
			Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
			XMLReader reader;
			reader = XMLReaderFactory.createXMLReader("org.htmlparser.sax.XMLReader");

			// Create out specific parser for this type of content.
			EVEMarketDataParser content = new EVEMarketDataParser();
			reader.setContentHandler(content);
			reader.setErrorHandler(content);
			String URLDestination = null;
			if ( opType == EMarketSide.SELLER ) {
				URLDestination = this.getModuleLink(itemName, "SELL");
			}
			if ( opType == EMarketSide.BUYER ) {
				URLDestination = this.getModuleLink(itemName, "BUY");
			}
			if ( null != URLDestination ) {
				reader.parse(URLDestination);
				marketEntries = content.getEntries();
			}
			logger.info("<< [MarketDataJobDownloadManager.parseMarketDataEMD]> MarketEntries [" + marketEntries.size() + "]");
			return marketEntries;
		}

		/**
		 * New version that downloads the information from eve-central in json format.
		 */
		public List<TrackEntry> parseMarketDataEC( final int itemid, final EMarketSide opType ) {
			logger.info(">> [MarketDataJobDownloadManager.parseMarketDataEC]");
			Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
			// Making a request to url and getting response
			String jsonStr = this.readJsonData(itemid);
			try {
				JSONArray jsonObj = new JSONArray(jsonStr);
				JSONObject part1 = jsonObj.getJSONObject(0);
				// Get the three blocks.
				JSONObject buy = part1.getJSONObject("buy");
				JSONObject all = part1.getJSONObject("all");
				JSONObject sell = part1.getJSONObject("sell");
				JSONObject target = null;
				if ( opType == EMarketSide.SELLER ) {
					target = sell;
				} else {
					target = buy;
				}
				double price = 0.0;
				if ( opType == EMarketSide.SELLER ) {
					price = target.getDouble("min");
				} else {
					price = target.getDouble("max");
				}
				long volume = target.getLong("volume");
				TrackEntry entry = new TrackEntry();
				entry.setPrice(Double.valueOf(price).toString());
				entry.setQty(Long.valueOf(volume).toString());
				entry.setStationName("0.9 The Forge - Jita");
				marketEntries.add(entry);
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
			logger.info("<< [MarketDataJobDownloadManager.parseMarketDataEC]> MarketEntries [" + marketEntries.size() + "]");
			return marketEntries;
		}

		protected String readJsonData( final int typeid ) {
			StringBuffer data = new StringBuffer();
			try {
				String str = "";
				URL url = new URL("http://api.eve-central.com/api/marketstat/json?typeid=" + typeid + "&regionlimit=10000002");
				URLConnection urlConnection = url.openConnection();
				InputStream is = new BufferedInputStream(urlConnection.getInputStream());
				// InputStream is = NeoComAppConnector.getSingleton().getStorageConnector().accessNetworkResource(
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				if ( is != null ) {
					while ((str = reader.readLine()) != null) {
						data.append(str);
					}
				}
				is.close();
			} catch ( Exception ex ) {
				ex.printStackTrace();
			}
			return data.toString();
		}

		/**
		 * Get the eve-marketdata link for a requested module and market side.
		 * @param moduleName The module name to be used on the link.
		 * @param opType     if the set is from sell or buy orders.
		 * @return the URL to access the HTML page with the data.
		 */
		protected String getModuleLink( final String moduleName, final String opType ) {
			// Adjust the module name to a URL suitable name.
			String name = moduleName.replace(" ", "+");
			return "http://eve-marketdata.com/price_check.php?type=" + opType.toLowerCase() + "&region_id=-1&type_name_header="
					+ name;
		}
	}
}
