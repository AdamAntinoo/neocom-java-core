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

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerCache extends GlobalDataManagerConfiguration {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerCache");

	//--- C A C H E   S T O R A G E   S E C T I O N
	public static final Hashtable<ECacheTimes, Long> ESICacheTimes = new Hashtable();
	public static final long DEFAULT_CACHE_TIME = TimeUnit.SECONDS.toMillis(600);

	public enum ECacheTimes {
		SERVERSTATUS,
		CHARACTER_PUBLIC, CHARACTER_CLONES, CHARACTER_BLUEPRINTS, CHARACTER_SKILLQUEUE, CHARACTER_SKILLS,
		CHARACTER_COLONIES, PLANETARY_INTERACTION_STRUCTURES,
		ASSETS_ASSETS, CORPORATION_CUSTOM_OFFICES, UNIVERSE_SCHEMATICS,
		MARKET_PRICES,
		INDUSTRY_JOBS, INDUSTRY_MARKET_ORDERS, INDUSTRY_MINING,
		WALLET, CORPORATION_WALLET
	}

	static {
		ESICacheTimes.put(ECacheTimes.SERVERSTATUS, TimeUnit.SECONDS.toMillis(30));
		ESICacheTimes.put(ECacheTimes.CHARACTER_PUBLIC, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.CHARACTER_CLONES, TimeUnit.SECONDS.toMillis(200));
		ESICacheTimes.put(ECacheTimes.CHARACTER_BLUEPRINTS, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.CHARACTER_SKILLQUEUE, TimeUnit.SECONDS.toMillis(120));
		ESICacheTimes.put(ECacheTimes.CHARACTER_SKILLS, TimeUnit.SECONDS.toMillis(120));
		ESICacheTimes.put(ECacheTimes.CHARACTER_COLONIES, TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.ASSETS_ASSETS, TimeUnit.SECONDS.toMillis(3600 * 1));// TODO - Changing some values during debugging.
		ESICacheTimes.put(ECacheTimes.MARKET_PRICES, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.INDUSTRY_JOBS, TimeUnit.SECONDS.toMillis(300));
		ESICacheTimes.put(ECacheTimes.INDUSTRY_MARKET_ORDERS, TimeUnit.SECONDS.toMillis(1200));
		ESICacheTimes.put(ECacheTimes.INDUSTRY_MINING, TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.WALLET, TimeUnit.SECONDS.toMillis(120));
		ESICacheTimes.put(ECacheTimes.CORPORATION_WALLET, TimeUnit.SECONDS.toMillis(300));
	}

	public static long getCacheTime4Type( final ECacheTimes selector ) {
		final Long hit = ESICacheTimes.get(selector);
		if ( null == hit ) return DEFAULT_CACHE_TIME;
		else return hit.longValue();
	}

	//--- M A R K E T   D A T A   S E R V I C E   S E C T I O N
//	private static MarketDataServer marketDataService = null;
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(1000);

//	public void connectMarketDataManager( final MarketDataServer manager ) {
//		logger.info(">> [GlobalDataManagerCache.connectMarketDataManager]");
////		marketDataService = manager;
//		// At this point we should have been initialized.
//		// The next section should be executed out of the main thread to be compatible con Android.
//		GlobalDataManager.getSingleton().submitJob(() -> {
//			// Initialize and process the list of market process form the ESI full market data.
//			final List<GetMarketsPrices200Ok> marketPrices = this.esiAdapter.getMarketsPrices(GlobalDataManager.TRANQUILITY_DATASOURCE);
//			logger.info(">> [GlobalDataManagerCache.connectMarketDataManager]> Process all market prices: {} items", marketPrices.size());
//			for (GetMarketsPrices200Ok price : marketPrices) {
//				marketDefaultPrices.put(price.getTypeId(), price);
//			}
//		});
//		logger.info("<< [GlobalDataManagerCache.connectMarketDataManager]");
//	}

//	public static MarketDataServer currentMarketDataManager() {
//		return marketDataService;
//	}

	/**
	 * Returns the default and average prices found on the ESI market price list for the specified item identifier.
	 * @param typeId
	 * @return
	 */
	public GetMarketsPrices200Ok searchMarketPrice( final int typeId ) {
		final GetMarketsPrices200Ok hit = marketDefaultPrices.get(typeId);
		// TODO - If there is no data for the item on the ESI data source we should go to the best buyer price at Jita. We can do
		// it with another future that evaluates when the price is consumed.
		if ( null == hit ) {
			final GetMarketsPrices200Ok newprice = new GetMarketsPrices200Ok().typeId(typeId);
			newprice.setAdjustedPrice(-1.0);
			newprice.setAveragePrice(-1.0);
			return newprice;
		} else return hit;
	}

//	public Future<MarketDataSet> searchMarketData( final int itemId, final EMarketSide side ) {
//		if ( null != marketDataService ) return marketDataService.searchMarketData(itemId, side);
//		else throw new RuntimeException("No MarketDataManager service connected.");
//	}
	//	public static void activateMarketDataCache4Id( final int typeId ) {
	//		if (null != marketDataService) marketDataService.activateMarketDataCache4Id(typeId);
	//		else throw new RuntimeException("No MarketDataManager service connected.");
	//	}
}
// - UNUSED CODE ............................................................................................
//[01]
