//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.evemarket.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evemarket.model.TrackEntry;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class interfaces the downloading eve online market data services and serves as the integration layer
 * to the different platforms. Based on the Android Service pattern it will implement a core class that will
 * be usable on any environment.
 * 
 * @author Adam Antinoo
 */
public class MarketDataService {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("MarketDataService");

	/**
	 * This should represent the service entry point. It will be called by the common implementation. It should
	 * receive the localized key for the eve item and be called after the network availability has been checked
	 * to avoid calling the methods and falling back to other market providers. <br>
	 * The implementation depends on some eve core functions to access the CCP item database so this should be
	 * added to a library that has common access to all that functions (market and CCP database) at the same
	 * time.
	 */
	public static Vector<MarketDataSet> marketDataServiceEntryPoint(int localizer) {
		logger.info(">> [MarketDataService.marketDataServiceEntryPoint]");
		// Create the result strucnture to be processed by the caller.
		Vector<MarketDataSet> results = new Vector<MarketDataSet>(2);
		//		final Integer localizer = (Integer) intent.getSerializableExtra(AppWideConstants.extras.EXTRA_MARKETDATA_LOCALIZER);
		// Be sure we have access to the network. Otherwise intercept the exceptions.
		//		if (NeoComApp.checkNetworkAccess()) {
		final EveItem item = AppConnector.getDBConnector().searchItembyID(localizer);
		//			if(market==EVEMARKETDATA)
		Vector<TrackEntry> marketEntries = AppConnector.getStorageConnector().parseMarketDataEMD(item.getName(),
				EMarketSide.SELLER);
		if (marketEntries.size() < 1) {
			marketEntries = AppConnector.getStorageConnector().parseMarketDataEC(item.getTypeID(), EMarketSide.SELLER);
		}
		Vector<MarketDataEntry> hubData = extractMarketData(marketEntries);
		// Update the structures related to the newly downloaded data.
		MarketDataSet reference = AppConnector.getDBConnector().searchMarketData(localizer, EMarketSide.SELLER);
		reference.setData(hubData);
		if (marketEntries.size() > 1) {
			results.add(reference);
		}

		// Do the same for the other side.
		marketEntries = AppConnector.getStorageConnector().parseMarketDataEMD(item.getName(), EMarketSide.BUYER);
		if (marketEntries.size() < 1) {
			marketEntries = AppConnector.getStorageConnector().parseMarketDataEC(item.getTypeID(), EMarketSide.BUYER);
		}
		hubData = extractMarketData(marketEntries);
		reference = AppConnector.getDBConnector().searchMarketData(localizer, EMarketSide.BUYER);
		reference.setData(hubData);
		if (marketEntries.size() > 1) {
			results.add(reference);
		}
		// Create a new method to access the cache for requests and change the state
		//			NeoComApp.getTheCacheConnector().clearPendingRequest(localizer.toString());
		//		}
		logger.info("<< [MarketDataService.marketDataServiceEntryPoint]");
		return results;
	}

	/**
	 * Converts the raw TrakEntry structures into aggregated data by location and system. This has a new
	 * implementation that will use real location data for the system to better classify and store the market
	 * data information. It will also remove the current limit on the selected market hubs and will aggregate
	 * all the systems found into the highsec and other sec categories.
	 * 
	 * @param item
	 * @param entries
	 * @return
	 * @return
	 */
	private static Vector<MarketDataEntry> extractMarketData(final Vector<TrackEntry> entries) {
		final HashMap<String, MarketDataEntry> stations = new HashMap<String, MarketDataEntry>();
		final Vector<String> stationList = getMarketHubs();
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
				final EveLocation entryLocation = generateLocation(stationName);
				final MarketDataEntry data = new MarketDataEntry(entryLocation);
				data.setQty(stationQty);
				data.setPrice(stationPrice);
				stations.put(stationName, data);
				stationList.remove(entry.getStationName());
			}
		}
		return new Vector<MarketDataEntry>(stations.values());
	}

	private static boolean filterStations(final TrackEntry entry, final Vector<String> stationList) {
		final Iterator<String> slit = stationList.iterator();
		while (slit.hasNext()) {
			final String stationNameMatch = slit.next();
			final String station = entry.getStationName();
			if (station.contains(stationNameMatch)) return true;
		}
		return false;
	}

	private static EveLocation generateLocation(String hubName) {
		// Extract system name from the station information.
		final int pos = hubName.indexOf(" ");
		final String hubSecurity = hubName.substring(0, pos);
		// Divide the name into region-system
		hubName = hubName.substring(pos + 1, hubName.length());
		final String[] parts = hubName.split(" - ");
		final String hubSystem = parts[1].trim();
		final String hubRegion = parts[0].trim();

		// Search for the system on the list of locations.
		return AppConnector.getDBConnector().searchLocationBySystem(hubSystem);
	}

	private static Vector<String> getMarketHubs() {
		final Vector<String> stationList = new Vector<String>();
		//		stationList.add("0.8 Tash-Murkon - Tash-Murkon Prime");
		stationList.add("1.0 Domain - Amarr");
		//		stationList.add("1.0 Domain - Sarum Prime");
		//		stationList.add("0.8 Devoid - Hati");
		//		stationList.add("0.6 Devoid - Esescama");
		//		stationList.add("0.8 Heimatar - Odatrik");
		stationList.add("0.9 Heimatar - Rens");
		//		stationList.add("0.8 Heimatar - Frarn");
		//		stationList.add("0.9 Heimatar - Lustrevik");
		//		stationList.add("0.9 Heimatar - Eystur");
		stationList.add("0.5 Metropolis - Hek");
		//		stationList.add("0.5 Sinq Laison - Deltole");
		//		stationList.add("0.5 Sinq Laison - Aufay");
		//		stationList.add("0.9 Sinq Laison - Dodixie");
		//		stationList.add("0.9 Essence - Renyn");
		//		stationList.add("0.7 Kador - Romi");
		//		stationList.add("0.8 The Citadel - Kaaputenen");
		//		stationList.add("1.0 The Forge - Urlen");
		stationList.add("1.0 The Forge - Perimeter");
		stationList.add("0.9 The Forge - Jita");
		return stationList;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public MarketDataService() {
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................

}

// - UNUSED CODE ............................................................................................
