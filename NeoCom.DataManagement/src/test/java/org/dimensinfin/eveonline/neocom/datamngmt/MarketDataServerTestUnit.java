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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.GlobalSBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.NeoComSBDBHelper;
import org.dimensinfin.eveonline.neocom.SDESBDBHelper;
import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.market.TrackEntry;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;
import org.dimensinfin.eveonline.neocom.model.EveItem;

import static org.dimensinfin.eveonline.neocom.datamngmt.MarketDataServer.cpuCount;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class MarketDataServerTestUnit {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger logger = LoggerFactory.getLogger("MarketDataServerTestUnit");
	/**
	 * This is the place where to store the test item during different calls to allow background processing.
	 */
	private FutureEveItem item = null;

	@BeforeClass
	public static void before01OpenAndConnectDatabase() throws SQLException {
		logger.info(">> [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting the Configuration Manager...");
		GlobalDataManager.connectConfigurationManager(new GlobalSBConfigurationProvider("testproperties"));

		// Initialize the Model with the current global instance.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting Global to Model...");
		ANeoComEntity.connectGlobal(new GlobalDataManager());

		// Initializing the ESI api network controller.
		ESINetworkManager.initialize();

		// Connect the SDE database.
		logger.info("-- [NeoComMicroServiceApplication.main]> Connecting SDE database...");
		try {
			GlobalDataManager.connectSDEDBConnector(new SDESBDBHelper()
					.setDatabaseSchema(GlobalDataManager.getResourceString("R.database.sdedatabase.databaseschema"))
					.setDatabasePath(GlobalDataManager.getResourceString("R.database.sdedatabase.databasepath"))
					.setDatabaseName(GlobalDataManager.getResourceString("R.database.sdedatabase.databasename"))
					.build()
			);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		ANeoComEntity.connectSDEHelper(new GlobalDataManager().getSDEDBHelper());

		// Connect the NeoCom database.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Connecting NeoCom private database...");
		try {
			GlobalDataManager.connectNeoComDBConnector(new NeoComSBDBHelper()
					.setDatabaseHost(GlobalDataManager.getResourceString("R.database.neocom.databasehost"
							, "jdbc:mysql://localhost:3306"))
					.setDatabaseName("neocom")
					.setDatabaseUser(GlobalDataManager.getResourceString("R.database.neocom.databaseuser"
							, "NEOCOM"))
					.setDatabasePassword(GlobalDataManager.getResourceString("R.database.neocom.databasepassword"))
					.setDatabaseVersion(GlobalDataManager.getResourceInt("R.database.neocom.databaseversion"))
					.build()
			);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
//		ANeoComEntity.connectNeoComHelper(new GlobalDataManager().getNeocomDBHelper());

		// Load the Locations cache to speed up the Citadel and Outpost search.
		logger.info("-- [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]> Read Locations data cache...");
		GlobalDataManager.readLocationsDataCache();

		// Check the connection descriptor.
		Assert.assertEquals("-> Validating the database is valid..."
				, new GlobalDataManager().getNeocomDBHelper().isDatabaseValid()
				, true);
		// Check the database is open and has a valid connection.
		Assert.assertEquals("-> Validating the database is open..."
				, new GlobalDataManager().getNeocomDBHelper().isOpen()
				, true);

//		// Get a testing credential.
//		final List<Credential> credentials = new GlobalDataManager().getNeocomDBHelper().getCredentialDao().queryForAll();
//		for (Credential c : credentials) {
//			if (c.getAccountId() == 92002067) testCredential = c;
//		}
		logger.info("<< [ESINetworkManagerTestUnit.before01OpenAndConnectDatabase]");
	}

	// - F I E L D - S E C T I O N ............................................................................
	private static FutureMarketDataDownloader marketDataService = null;

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void test01InitiateFutureEveItem() {
		logger.info(">> [MarketDataServerTestUnit.test01InitiateFutureEveItem]");
		// Get and initialize the market server.
		marketDataService = new FutureMarketDataDownloader(4);
		// Instantiate a modified EveItem that uses futures to get market data.
		final int itemId = 34;
		item = new FutureEveItem(itemId);
		logger.info("-- [MarketDataServerTestUnit.test01InitiateFutureEveItem]> Created a new Item that is accessing the market " +
				"data.");
		logger.info("<< [MarketDataServerTestUnit.test01InitiateFutureEveItem]");
//	}
//
//	@Test
//	public void test02AccessMarketData() {
		logger.info(">> [MarketDataServerTestUnit.test02AccessMarketData]");
		// Get access to the market data to get a fresh value for the market data.
		final double price = item.getBuyerPrice();
		logger.info("-- [MarketDataServerTestUnit.test02AccessMarketData]> Price: {}", price);
		logger.info("<< [MarketDataServerTestUnit.test02AccessMarketData]");
	}

	public static class FutureEveItem extends EveItem {
		private transient Future<MarketDataSet> futureBuyerData = null;
		private transient Future<MarketDataSet> futureSellerData = null;
		private ExecutorService executor = Executors.newFixedThreadPool(1);

		public FutureEveItem( final int typeId ) {
			super();
			// Start the lookup for the market data futures.
			setTypeId(typeId);
			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.BUYER);
			futureSellerData = retrieveMarketData(getTypeId(), EMarketSide.SELLER);
		}

		public double getBuyerPrice() {
			logger.info(">> [FutureEveItem.getBuyerPrice]");
			double price = 0.0;
			try {
				final MarketDataSet data = futureBuyerData.get();
				price = data.getBestMarket().getPrice();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				return price;
			}
		}

		private Future<MarketDataSet> retrieveMarketData( final int itemId, final EMarketSide side ) {
			Callable<MarketDataSet> task = () -> {
				return marketDataService.searchMarketData(itemId, side);
			};
			Future<MarketDataSet> future = executor.submit(task);
			return future;
		}
	}

	public static class FutureMarketDataDownloader extends MarketDataServer.MarketDataJobDownloadManager {
		private HashMap<Integer, MarketDataSet> buyMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
		private HashMap<Integer, MarketDataSet> sellMarketDataCache = new HashMap<Integer, MarketDataSet>(1000);
		protected final MarketDataServer.MarketDataJobDownloadManager downloadManager = new MarketDataServer.MarketDataJobDownloadManager(cpuCount);

		public FutureMarketDataDownloader( final int threadSize ) {
			super(threadSize);
		}

		public MarketDataSet searchMarketData( final int localizer, final EMarketSide side ) {
			MarketDataServerTestUnit.logger.info(">> [MarketDataServer.searchMarketData]> ItemId: {}/{}.", localizer, side.name());
			 MarketDataSet set = new MarketDataSet(localizer, side);
			final EveItem item = new GlobalDataManager().searchItem4Id(localizer);
			try {
				try {
					logger.info(">> [MarketDataJobDownloadManager.launchDownloadJob.submit]> Processing SELLER");
					List<TrackEntry> marketEntries = new ArrayList();
					// Check which data provider should be used for the data.
					// Preference is: eve-market-data/eve-central/esi-marketdata
					if (marketEntries.size() < 1) {
						if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEMD", true))
							marketEntries = parseMarketDataEMD(item.getName(), side);
					}
					if (marketEntries.size() < 1) {
						if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateEC", true))
							marketEntries = parseMarketDataEC(localizer, side);
					}
					if (marketEntries.size() < 1) {
						if (GlobalDataManager.getResourceBoolean("R.cache.marketdata.provider.activateESI", true))
							marketEntries = parseMarketDataESI(localizer, side);
					}

					List<MarketDataEntry> hubData = extractMarketData(marketEntries);
					// Update the structures related to the newly downloaded data.
//					MarketDataSet reference = new GlobalDataManager().searchMarketData(localizer, side);
//					reference.setData(hubData);
					set.setData(hubData);
					logger.info("-- [MarketDataJobDownloadManager.launchDownloadJob.submit]> Storing data entries {}", hubData.size());
//					return set;
				} catch (RuntimeException rtex) {
					rtex.printStackTrace();
				}

//				// Search on the cache. By default load the SELLER as If I am buying the item.
//				HashMap<Integer, MarketDataSet> cache = sellMarketDataCache;
//				if (side == EMarketSide.BUYER) {
//					cache = buyMarketDataCache;
//				}
//				MarketDataSet entry = cache.get(itemId);
//				if (null == entry) {
//					return downloadManager.addMarketDataRequest(itemId);
//					// The data is not on the cache and neither on the latest disk copy read at initialization. Post a request.
////				entry = new MarketDataSet(itemId, side);
////				// But store the reference on the cache because this is going to be the single market data instance for this type.
////				sellMarketDataCache.put(itemId, entry.setSide(EMarketSide.SELLER));
////				buyMarketDataCache.put(itemId, entry.setSide(EMarketSide.SELLER));
//				} else {
//					MarketDataServerTestUnit.logger.info("-- [MarketDataServer.searchMarketData]> Cache hit on memory.");
//					// Check again the expiration time. If expired request a refresh.
//					Instant expirationTime = expirationTimeMarketData.get(itemId);
//					if (null == expirationTime) expirationTime = Instant.now().minus(TimeUnit.MINUTES.toMillis(1));
//					if (expirationTime.isBefore(Instant.now())) downloadManager.addMarketDataRequest(itemId);
//				}
//				return entry;
			} finally {
				MarketDataServerTestUnit.logger.info("<< [MarketDataServer.searchMarketData]");
				return set;
			}
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]
