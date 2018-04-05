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
package org.dimensinfin.eveonline.neocom.datahub.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Hashtable;
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
	private static final int cpuCount = Runtime.getRuntime().availableProcessors();

	// - F I E L D - S E C T I O N ............................................................................
	private HashMap<Integer, MarketDataSet> buyMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
	private HashMap<Integer, MarketDataSet> sellMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
	private HashMap<Integer, Instant> expirationTimeMarketData = new HashMap<Integer, Instant>(1000);
	protected final MarketDataJobDownloadManager downloadManager = new MarketDataJobDownloadManager(cpuCount);

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Does the service initialization and start the service. This mean to read back from storage the latest saved cache and then
	 * start the background services to download more Market data.
	 *
	 * @return
	 */
	public MarketDataServer start() {
		logger.info(">> [MarketDataServer.start]");
		readMarketDataCacheFromStorage();
		downloadManager.clear();
		logger.info("<< [MarketDataServer.start]");
		return this;
	}

	public MarketDataServer clear() {
		logger.info(">> [MarketDataServer.clear]");
		buyMarketDataCache.clear();
		sellMarketDataCache.clear();
		downloadManager.clear();
		logger.info("<< [MarketDataServer.clear]");
		return this;
	}

	public synchronized void readMarketDataCacheFromStorage() {
		File modelStoreFile = new File(getCacheStoreName());
		try {
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(modelStoreFile));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			try {
				//				this.getStore().setApiKeys((HashMap<Integer, NeoComApiKey>) input.readObject());
				buyMarketDataCache = (HashMap<Integer, MarketDataSet>) input.readObject();
				logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Restored cache BUY: " + buyMarketDataCache.size()
						+ " entries.");
				sellMarketDataCache = (HashMap<Integer, MarketDataSet>) input.readObject();
				logger.info("-- [MarketDataServer.readMarketDataCacheFromStorage]> Restored cache SELL: " + sellMarketDataCache.size()
						+ " entries.");
			} finally {
				input.close();
				buffer.close();
			}
		} catch (final ClassNotFoundException ex) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> ClassNotFoundException."); //$NON-NLS-1$
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> FileNotFoundException."); //$NON-NLS-1$
		} catch (final IOException ex) {
			logger.warn("W> [MarketDataServer.readMarketDataCacheFromStorage]> IOException."); //$NON-NLS-1$
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		}
	}

	public synchronized void writeMarketDataCacheToStorage() {
		File modelStoreFile = new File(getCacheStoreName());
		try {
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(modelStoreFile));
			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(buyMarketDataCache);
				logger.info(
						"-- [MarketDataServer.writeCacheToStorage]> Wrote cache BUY: " + buyMarketDataCache.size() + " entries.");
				output.writeObject(sellMarketDataCache);
				logger.info(
						"-- [MarketDataServer.writeCacheToStorage]> Wrote cache SELL: " + sellMarketDataCache.size() + " entries.");
			} finally {
				output.flush();
				output.close();
				buffer.close();
			}
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [MarketDataServer.writeCacheToStorage]> FileNotFoundException."); //$NON-NLS-1$
		} catch (final IOException ex) {
			logger.warn("W> [MarketDataServer.writeCacheToStorage]> IOException."); //$NON-NLS-1$
		}
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
	 *
	 * @param itemID item id code of the item assigned to this market request.
	 * @param side   differentiates if we like to BUY or SELL the item.
	 * @return the cached data or an empty locator ready to receive downloaded data.
	 */
	public MarketDataSet searchMarketData( final int itemID, final EMarketSide side ) {
		logger.info(">> [MarketDataServer.searchMarketData]> ItemId: {}/{}.", itemID, side.name());
		try {
			// Search on the cache. By default load the SELLER as If I am buying the item.
			HashMap<Integer, MarketDataSet> cache = sellMarketDataCache;
			if (side == EMarketSide.BUYER) {
				cache = buyMarketDataCache;
			}
			MarketDataSet entry = cache.get(itemID);
			if (null == entry) {
				// The data is not on the cache and neither on the latest disk copy read at initialization. Post a request.
				entry = new MarketDataSet(itemID, side);
				// But store the reference on the cache because this is going to be the single market data instance for this type.
				sellMarketDataCache.put(itemID, entry.setSide(EMarketSide.SELLER));
				buyMarketDataCache.put(itemID, entry.setSide(EMarketSide.SELLER));
				downloadManager.addMarketDataRequest(itemID);
			} else {
				logger.info("-- [MarketDataServer.searchMarketData]> Cache hit on memory.");
				// Check again the expiration time. If expired request a refresh.
				Instant expirationTime = expirationTimeMarketData.get(itemID);
				if (null == expirationTime) expirationTime = Instant.now().minus(TimeUnit.MINUTES.toMillis(1));
				if (expirationTime.isBefore(Instant.now())) downloadManager.addMarketDataRequest(itemID);
			}
			return entry;
		} finally {
			logger.info("<< [MarketDataServer.searchMarketData]");
		}
	}

	public void activateMarketDataCache4Id( final int typeId ) {
		expirationTimeMarketData.put(typeId, Instant.now().plus(TimeUnit.HOURS.toMillis(1)));
	}

	/**
	 * Read the configured cache location for the Market data save/restore serialization file.
	 *
	 * @return
	 */
	protected String getCacheStoreName() {
		return GlobalDataManager.getResourceString("R.cache.directorypath")
				+ GlobalDataManager.getResourceString("R.cache.marketdata.cachename");
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
		public int marketJobCounter = 0;
		private static ExecutorService marketDataDownloadExecutor = null;
		public static final Hashtable<String, Future<?>> runningJobs = new Hashtable();

		// - F I E L D - S E C T I O N ............................................................................
		private final int threadSize;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public MarketDataJobDownloadManager( final int threadSize ) {
			this.threadSize = threadSize;
			marketDataDownloadExecutor = Executors.newFixedThreadPool(threadSize);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public void clear() {
			runningJobs.clear();
		}

		/**
		 * Submits a request for a new Market data update. This should create a new job and update the job counters.
		 *
		 * @param typeId the job to update some information.
		 */
		public synchronized void addMarketDataRequest( final int typeId ) {
			// Launch the updater job only if the market data updater process is allowed.
			if (!GlobalDataManager.getDefaultSharedPreferences().getBoolean(PreferenceKeys.prefkey_BlockMarket.name())) {
				final String identifier = generateMarketDataJobReference(typeId);
				logger.info(">> [MarketDataJobDownloadManager.addMarketDataRequest]");
				try {
					// Search for the job to detect duplications
					final Future<?> hit = runningJobs.get(identifier);
					if (null == hit) {
						// New job. Launch it and store the reference.
						runningJobs.put(identifier, launchDownloadJob(typeId));
					} else {
						// Check for job completed.
						if (hit.isDone()) {
							// The job with this same reference has completed. We can launch a new one.
							runningJobs.put(identifier, launchDownloadJob(typeId));
						}
					}
				} catch (RuntimeException neoe) {
					neoe.printStackTrace();
				}
			}
		}

		protected Future<?> launchDownloadJob( final int typeId ) {
			try {
				logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob]> Launching job {}", typeId);
				marketJobCounter++;
				final Future<?> future = marketDataDownloadExecutor.submit(() -> {
					final int localizer = typeId;
					logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing type {}", localizer);
					// Download the market data and store the new data into the cache.
					final EveItem item = GlobalDataManager.searchItem4Id(localizer);
					try {
						logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing SELLER");
						List<TrackEntry> marketEntries = new ArrayList();
						// Check which data provider should be used for the data.
						// Preference is: eve-market-data/eve-central/esi-marketdata
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true))
								marketEntries = parseMarketDataEMD(item.getName(), EMarketSide.SELLER);
						}
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", true))
								marketEntries = parseMarketDataEC(localizer, EMarketSide.SELLER);
						}
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", true))
								marketEntries = parseMarketDataESI(localizer, EMarketSide.SELLER);
						}

						List<MarketDataEntry> hubData = extractMarketData(marketEntries);
						// Update the structures related to the newly downloaded data.
//					MarketDataSet reference = new MarketDataSet(localizer, EMarketSide.SELLER);
						MarketDataSet reference = GlobalDataManager.searchMarketData(localizer, EMarketSide.SELLER);
						reference.setData(hubData);
						logger.info("-- [MarketDataJobDownloadManager.launchDownloadJob.submit]> Storing data entries {}", hubData.size());
					} catch (RuntimeException rtex) {
						rtex.printStackTrace();
					}
					// Do the same for the other side.
					try {
						logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing BUYER");
						List<TrackEntry> marketEntries = new ArrayList();
						// Check which data provider should be used for the data.
						// Preference is: eve-market-data/eve-central/esi-marketdata
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true))
								marketEntries = parseMarketDataEMD(item.getName(), EMarketSide.BUYER);
						}
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", true))
								marketEntries = parseMarketDataEC(localizer, EMarketSide.BUYER);
						}
						if (marketEntries.size() < 1) {
							if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", true))
								marketEntries = parseMarketDataESI(localizer, EMarketSide.BUYER);
						}

						List<MarketDataEntry> hubData = extractMarketData(marketEntries);
						// Update the structures related to the newly downloaded data.
						MarketDataSet reference = new MarketDataSet(localizer, EMarketSide.BUYER);
						reference.setData(hubData);
						logger.info("-- [MarketDataJobDownloadManager.launchDownloadJob.submit]> Storing data entries {}", hubData.size());
					} catch (RuntimeException rtex) {
						rtex.printStackTrace();
					}
					// If there was no exceptions now the market data is stored on the cache.
					// But still we need to setup the cache time.
					GlobalDataManager.activateMarketDataCache4Id(localizer);
					// Decrement the counter.
					marketJobCounter--;
					logger.info("<< [MarketDataJobDownloadManager.launchDownloadJob.submit]> Completing job {}", typeId);
				});
				return future;
			} finally {
				logger.info("<< [MarketDataJobDownloadManager.launchDownloadJob]");
			}
		}

		public int countRunningJobs() {
			// Count the running or pending jobs to update the ActionBar counter.
			int counter = 0;
			for (Future<?> future : runningJobs.values()) {
				if (!future.isDone()) counter++;
			}
			marketJobCounter = counter;
			return counter;
		}

		/**
		 * Converts the raw TrackEntry structures into aggregated data by location and system. This has a new
		 * implementation that will use real location data for the system to better classify and store the market
		 * data information. It will also remove the current limit on the selected market hubs and will aggregate
		 * all the systems found into the highsec and other sec categories.
		 *
		 * @param entries
		 * @return
		 */
		protected List<MarketDataEntry> extractMarketData( final List<TrackEntry> entries ) {
			final HashMap<String, MarketDataEntry> stations = new HashMap<String, MarketDataEntry>();
			final List<String> stationList = getMarketHubs();
			final Iterator<TrackEntry> meit = entries.iterator();
			while (meit.hasNext()) {
				final TrackEntry entry = meit.next();
				// Filtering for only preferred market hubs.
				if (filterStations(entry, stationList)) {
					// Start searching for more records to sum all entries with the same or a close price to get
					// a better understanding of the market depth. That information is not to relevant so make a
					// best try.
					int stationQty = entry.getQty();
					final String stationName = entry.getStationName();
					final double stationPrice = entry.getPrice();
					while (meit.hasNext()) {
						final TrackEntry searchEntry = meit.next();
						// Check that station and prices are the same or price is inside margin.
						if (searchEntry.getStationName().equals(stationName)) {
							if ((stationPrice >= (searchEntry.getPrice() * 0.99))
									&& (stationPrice <= (searchEntry.getPrice() * 1.01))) {
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

		protected List<String> getMarketHubs() {
			final List<String> stationList = new ArrayList<>();
			//		stationList.add("0.8 Tash-Murkon - Tash-Murkon Prime");
			stationList.add("1.0 Domain - Amarr");
			stationList.add("1.0 Domain - Sarum Prime");
			//		stationList.add("0.8 Devoid - Hati");
			//		stationList.add("0.6 Devoid - Esescama");
			//		stationList.add("0.8 Heimatar - Odatrik");
			//		stationList.add("0.9 Heimatar - Rens");
			//		stationList.add("0.8 Heimatar - Frarn");
			//		stationList.add("0.9 Heimatar - Lustrevik");
			//		stationList.add("0.9 Heimatar - Eystur");
			//		stationList.add("0.5 Metropolis - Hek");
			//		stationList.add("0.5 Sinq Laison - Deltole");
			//		stationList.add("0.5 Sinq Laison - Aufay");
			//		stationList.add("0.9 Sinq Laison - Dodixie");
			//		stationList.add("0.9 Essence - Renyn");
			stationList.add("0.7 Kador - Romi");
			stationList.add("0.8 The Citadel - Kaaputenen");
			stationList.add("1.0 The Forge - Urlen");
			stationList.add("1.0 The Forge - Perimeter");
			stationList.add("0.9 The Forge - Jita");
			return stationList;
		}

		protected boolean filterStations( final TrackEntry entry, final List<String> stationList ) {
//			final Iterator<String> slit = stationList.iterator();
//			while (slit.hasNext()) {
//				final String stationNameMatch = slit.next();
//				final String station = entry.getStationName();
//				if (station.contains(stationNameMatch)) return true;
//			}
			final String station = entry.getStationName();
			for (String stationNameMatch : stationList) {
				if (station.contains(stationNameMatch)) return true;
			}
			return false;
		}

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

		protected List<TrackEntry> parseMarketDataEMD( final String itemName, final EMarketSide opType ) {
			logger.info(">> [MarketDataJobDownloadManager.parseMarketDataEMD]");
			Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
			try {
				XMLReader reader;
				reader = XMLReaderFactory.createXMLReader("org.htmlparser.sax.XMLReader");

				// Create out specific parser for this type of content.
				EVEMarketDataParser content = new EVEMarketDataParser();
				reader.setContentHandler(content);
				reader.setErrorHandler(content);
				String URLDestination = null;
				if (opType == EMarketSide.SELLER) {
					URLDestination = this.getModuleLink(itemName, "SELL");
				}
				if (opType == EMarketSide.BUYER) {
					URLDestination = this.getModuleLink(itemName, "BUY");
				}
				if (null != URLDestination) {
					reader.parse(URLDestination);
					marketEntries = content.getEntries();
				}
			} catch (SAXException saxe) {
				logger.error("E [MarketDataJobDownloadManager.parseMarketDataEMD]> Parsing exception while downloading market data for module [" + itemName + "]. " + saxe.getMessage());
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				logger.error("E [MarketDataJobDownloadManager.parseMarketDataEMD]> Error parsing the market information. " + ioe.getMessage());
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
				if (opType == EMarketSide.SELLER) {
					target = sell;
				} else {
					target = buy;
				}
				double price = 0.0;
				if (opType == EMarketSide.SELLER) {
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
			} catch (JSONException e) {
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

		/**
		 * Get the eve-marketdata link for a requested module and market side.
		 *
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