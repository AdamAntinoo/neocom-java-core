//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.core.ComparatorFactory;
import org.dimensinfin.eveonline.neocom.enums.EComparatorField;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.enums.ERequestClass;
import org.dimensinfin.eveonline.neocom.enums.ERequestState;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.services.MarketDataService;
import org.dimensinfin.eveonline.neocom.services.PendingRequestEntry;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Common platform implementation of the Cache Connector service. This code can be used on all platforms and
 * covers almost all the required model data cache functionality.
 * 
 * @author Adam Antinoo
 */
public abstract class CoreCacheConnector implements ICacheConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger																	logger							= Logger.getLogger("CoreCacheConnector");

	// - F I E L D - S E C T I O N ............................................................................
	protected PriorityBlockingQueue<PendingRequestEntry>	_pendingRequests		= new PriorityBlockingQueue<PendingRequestEntry>(
			100, ComparatorFactory.createComparator(EComparatorField.REQUEST_PRIORITY));
	protected final Hashtable<Integer, MarketDataSet>			buyMarketDataCache	= new Hashtable<Integer, MarketDataSet>();
	protected final Hashtable<Integer, MarketDataSet>			sellMarketDataCache	= new Hashtable<Integer, MarketDataSet>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CoreCacheConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Add a new request to download and update the database of locations. This is an special request that it is
	 * initialized at startup and probably in some other conditions like when the information is stale and needs
	 * to be updated.
	 */
	@Override
	public void addCharacterUpdateRequest(final long localizer) {
		CoreCacheConnector.logger.info(">> [CoreCacheConnector.addCharacterUpdateRequest]");
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.setClass(ERequestClass.CHARACTERUPDATE);
		request.setPriority(20);
		if (this.addNoDuplicate(request)) {
			this.incrementTopCounter();
		}
		CoreCacheConnector.logger.info("<< [CoreCacheConnector.addCharacterUpdateRequest]");
	}

	/**
	 * Queues a new request to download Market Data for an Item. We only register the ID of the item because the
	 * side will not be used. On the download phase we will download both sides. Using the ID as key will avoid
	 * requesting the same item multiple times. <br>
	 * 
	 * @param localizer
	 *          identifier of the item related to the data to download.
	 */
	@Override
	public void addMarketDataRequest(final long localizer) {
		CoreCacheConnector.logger.info(">> [CoreCacheConnector.addMarketDataRequest]>Localizer: " + localizer);
		final EveItem item = NeoComAppConnector.getCCPDBConnector().searchItembyID(Long.valueOf(localizer).intValue());
		CoreCacheConnector.logger
				.info("-- [AndroidCacheConnector.addMarketDataRequest] Posting market update for: " + item.getName());
		// Detect priority from the Category of the item. Download data from Asteroids and Minerals first.
		final String category = item.getCategory();
		final String group = item.getGroupName();
		int priority = 30;
		if (category.equalsIgnoreCase("Material")) {
			priority = 5;
		}
		if (category.equalsIgnoreCase("Asteroid")) {
			priority = 6;
		}
		if (category.equalsIgnoreCase("Planetary Commodities")) {
			priority = 7;
		}
		if (category.equalsIgnoreCase("Planetary Resources")) {
			priority = 7;
		}
		if (group.equalsIgnoreCase("Datacores")) {
			priority = 8;
		}
		if (category.equalsIgnoreCase("Module")) {
			priority = 10;
		}
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.setPriority(priority);
		if (this.addNoDuplicate(request)) {
			this.incrementMarketCounter();
		}
	}

	@Override
	@Deprecated
	public synchronized void clearPendingRequest(final long localizer) {
		this.clearPendingRequest(Long.valueOf(localizer).toString());
	}

	@Override
	@Deprecated
	public synchronized void clearPendingRequest(final String localizer) {
		for (final PendingRequestEntry entry : _pendingRequests) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(localizer)) {
				entry.state = ERequestState.COMPLETED;
				// Update the right counter depending on the priority range.
				if (entry.getPriority() < 20) {
					this.decrementMarketCounter();
				} else {
					this.decrementTopCounter();
				}
			}
		}
	}

	@Override
	public abstract int decrementMarketCounter();

	@Override
	public abstract int decrementTopCounter();

	@Override
	public PriorityBlockingQueue<PendingRequestEntry> getPendingRequests() {
		//		if (null == _pendingRequests) {
		//			_pendingRequests = new Vector<PendingRequestEntry>();
		//		}
		//		// Clean up all completed requests.
		//		final Vector<PendingRequestEntry> openRequests = new Vector<PendingRequestEntry>(_pendingRequests.size());
		//		for (final PendingRequestEntry entry : _pendingRequests)
		//			if (entry.state != ERequestState.COMPLETED) {
		//				openRequests.add(entry);
		//			}
		//		Collections.sort(openRequests, ComparatorFactory.createComparator(EComparatorField.REQUEST_PRIORITY));
		//	_pendingRequests = openRequests;
		return _pendingRequests;
	}

	@Override
	public abstract int incrementMarketCounter();

	@Override
	public abstract int incrementTopCounter();

	/**
	 * Search for this market data on the cache. <br>
	 * The cache used for the search depends on the side parameter received on the call. All default prices are
	 * references to the cost of the price to be spent to buy the item.<br>
	 * If not found on the memory cache then try to load from the serialized version stored on disk. This is an
	 * special implementation for SpringBoot applications that may run on a server so the cache disk location is
	 * implemented in a different way that on Android, indeed, because we can access the market data online we
	 * are not going to cache the data but get a fresh copy if not found on the cache.<br>
	 * If the data is not located on the case call the market data downloader and processor to get a new copy
	 * and store it on the cache.
	 * 
	 * @param itemID
	 *          item id code of the item assigned to this market request.
	 * @param side
	 *          differentiates if we like to BUY or SELL the item.
	 * @return the cached data or an empty locator ready to receive downloaded data.
	 */
	//	@Cacheable("MarketData")
	@Override
	public MarketDataSet searchMarketData(final int itemID, final EMarketSide side) {
		CoreCacheConnector.logger
				.info(">> [SpringDatabaseConnector.searchMarketData]> itemid: " + itemID + " side: " + side.toString());
		// for Market Data: " + itemID + " - " + side);
		// Search on the cache. By default load the SELLER as If I am buying the item.

		// Cache interception performed by EHCache. If we reach this point that means we have not cached the data.
		Hashtable<Integer, MarketDataSet> cache = sellMarketDataCache;
		if (side == EMarketSide.BUYER) {
			cache = buyMarketDataCache;
		}
		//		MarketDataSet entry = null;
		MarketDataSet entry = cache.get(itemID);
		if (null == entry) {
			// Download and process the market data by posting a Market Update Request.
			boolean doInmediately = false;
			if (doInmediately) {
				// Download and process the market data right now.
				Vector<MarketDataSet> entries = MarketDataService.marketDataServiceEntryPoint(itemID);
				for (MarketDataSet data : entries) {
					if (data.getSide() == EMarketSide.BUYER) {
						buyMarketDataCache.put(itemID, entry);
						if (side == data.getSide()) {
							entry = data;
						}
					}
					if (data.getSide() == EMarketSide.SELLER) {
						sellMarketDataCache.put(itemID, entry);
						if (side == data.getSide()) {
							entry = data;
						}
					}
				}
			} else {
				// Post the download of the market data to the background service.
				this.addMarketDataRequest(itemID);
			}
		}
		CoreCacheConnector.logger.info("<< [SpringDatabaseConnector.searchMarketData]");
		return entry;
	}

	/**
	 * Adds a new entry if not already found on the pending list. This feature is now a little more tricky
	 * because the queue implementation hides the contens and there is no way to check this without reading the
	 * list completely.
	 * 
	 * @param request
	 */
	private synchronized boolean addNoDuplicate(final PendingRequestEntry request) {
		// Check for duplicates before adding the new element.
		final String requestid = request.getIdentifier();
		synchronized (_pendingRequests) {
			for (PendingRequestEntry pendingRequestEntry : _pendingRequests) {
				PendingRequestEntry entry = pendingRequestEntry;
				if (entry.getIdentifier().equalsIgnoreCase(requestid)) return false;
			}
			_pendingRequests.add(request);
		}
		return true;
	}
}

// - UNUSED CODE ............................................................................................
