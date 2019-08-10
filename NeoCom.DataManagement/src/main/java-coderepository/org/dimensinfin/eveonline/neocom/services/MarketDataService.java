//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.neocom.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.EVEMarketDataParser;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.market.TrackEntry;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.xml.sax.SAXException;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class interfaces the downloading eve online market data services and serves as the integration layer
 * to the different platforms. Based on the Android Service pattern it will implement a core class that will
 * be usable on any environment.
 * 
 * @author Adam Antinoo
 */
public class MarketDataService implements Runnable {
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
	public static Vector<MarketDataSet> marketDataServiceEntryPoint(final int localizer) {
		MarketDataService.logger.info(">> [MarketDataService.marketDataServiceEntryPoint]");
		// Create the result structure to be processed by the caller.
		Vector<MarketDataSet> results = new Vector<MarketDataSet>(2);
		//		final Integer localizer = (Integer) intent.getSerializableExtra(AppWideConstants.extras.EXTRA_MARKETDATA_LOCALIZER);
		// Be sure we have access to the network. Otherwise intercept the exceptions.
		//		if (NeoComApp.checkNetworkAccess()) {
		final EveItem item = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(localizer);
		//			if(market==EVEMARKETDATA)
		Vector<TrackEntry> marketEntries = MarketDataService.parseMarketDataEMD(item.getName(), EMarketSide.SELLER);
		//		if (marketEntries.size() < 1) {
		//			marketEntries = AppConnector.getStorageConnector().parseMarketDataEC(item.getTypeId(), EMarketSide.SELLER);
		//		}
		Vector<MarketDataEntry> hubData = MarketDataService.extractMarketData(marketEntries);
		// Update the structures related to the newly downloaded data.
		MarketDataSet reference = new MarketDataSet(localizer, EMarketSide.SELLER);
		reference.setData(hubData);
		if (marketEntries.size() > 1) {
			results.add(reference);
		}

		// Do the same for the other side.
		marketEntries = MarketDataService.parseMarketDataEMD(item.getName(), EMarketSide.BUYER);
		//		if (marketEntries.size() < 1) {
		//			marketEntries = AppConnector.getStorageConnector().parseMarketDataEC(item.getTypeId(), EMarketSide.BUYER);
		//		}
		hubData = MarketDataService.extractMarketData(marketEntries);
		reference = new MarketDataSet(localizer, EMarketSide.BUYER);
		reference.setData(hubData);
		if (marketEntries.size() > 1) {
			results.add(reference);
		}
		// Create a new method to access the cache for requests and change the state
		//			NeoComApp.getTheCacheConnector().clearPendingRequest(localizer.toString());
		//		}
		MarketDataService.logger.info("<< [MarketDataService.marketDataServiceEntryPoint]");
		return results;
	}
	//	private static String readJsonData(final int typeid) {
	//		StringBuffer data = new StringBuffer();
	//		try {
	//			String str = "";
	//			URL url = new URL("http://api.eve-central.com/api/marketstat/json?typeid=" + typeid + "&regionlimit=10000002");
	//			URLConnection urlConnection = url.openConnection();
	//			InputStream is = new BufferedInputStream(urlConnection.getInputStream());
	//			// InputStream is = AppConnector.getStorageConnector().accessNetworkResource(
	//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	//			if (is != null) {
	//				while ((str = reader.readLine()) != null) {
	//					data.append(str);
	//				}
	//			}
	//			is.close();
	//		} catch (Exception ex) {
	//			ex.printStackTrace();
	//		}
	//		return data.toString();
	//	}
	///**
	//	 * New version that downloads the information from eve-central in json format.
	//	 */
	//	private static Vector<TrackEntry> parseMarketDataEC(final int itemid, final EMarketSide opType) {
	//		logger.info(">> AndroidStorageConnector.parseMarketData");
	//		Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
	//		// try {
	//		// Making a request to url and getting response
	//		String jsonStr = readJsonData(itemid);
	//		try {
	//			JSONArray jsonObj = new JSONArray(jsonStr);
	//			JSONObject part1 = jsonObj.getJSONObject(0);
	//			// Get the three blocks.
	//			JSONObject buy = part1.getJSONObject("buy");
	//			JSONObject all = part1.getJSONObject("all");
	//			JSONObject sell = part1.getJSONObject("sell");
	//			JSONObject target = null;
	//			if (opType == EMarketSide.SELLER) {
	//				target = sell;
	//			} else {
	//				target = buy;
	//			}
	//			double price = 0.0;
	//			if (opType == EMarketSide.SELLER) {
	//				price = target.getDouble("min");
	//			} else {
	//				price = target.getDouble("max");
	//			}
	//			long volume = target.getLong("volume");
	//			TrackEntry entry = new TrackEntry();
	//			entry.setPrice(Double.valueOf(price).toString());
	//			entry.setQty(Long.valueOf(volume).toString());
	//			entry.setStationName("0.9 The Forge - Jita");
	//			marketEntries.add(entry);
	//		} catch (JSONException e) {
	//			e.printStackTrace();
	//		}
	//		// } catch (SAXException saxe) {
	//		// logger.severe("E> Parsing exception while downloading market data for module [" + itemName + "]. "
	//		// + saxe.getMessage());
	//		// } catch (IOException ioe) {
	//		// // TODO Auto-generated catch block
	//		// ioe.printStackTrace();
	//		// logger.severe("E> Error parsing the market information. " + ioe.getMessage());
	//		// }
	//		logger.info("<< AndroidStorageConnector.parseMarketData. marketEntries [" + marketEntries.size() + "]");
	//		return marketEntries;
	//	}

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
		final Vector<String> stationList = MarketDataService.getMarketHubs();
		final Iterator<TrackEntry> meit = entries.iterator();
		while (meit.hasNext()) {
			final TrackEntry entry = meit.next();
			// Filtering for only preferred market hubs.
			if (MarketDataService.filterStations(entry, stationList)) {
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
				final EsiLocation entryLocation = MarketDataService.generateLocation(stationName);
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

	private static EsiLocation generateLocation(String hubName) {
		// Extract system name from the station information.
		final int pos = hubName.indexOf(" ");
		final String hubSecurity = hubName.substring(0, pos);
		// Divide the name into region-system
		hubName = hubName.substring(pos + 1, hubName.length());
		final String[] parts = hubName.split(" - ");
		final String hubSystem = parts[1].trim();
		final String hubRegion = parts[0].trim();

		// Search for the system on the list of locations.
		return ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationBySystem(hubSystem);
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

	/**
	 * Get the eve-marketdata link for a requested module and market side.
	 * 
	 * @param moduleName
	 *          The module name to be used on the link.
	 * @param opType
	 *          if the set is from sell or buy orders.
	 * @return the URL to access the HTML page with the data.
	 */
	private static String getModuleLink(final String moduleName, final String opType) {
		// Adjust the module name to a URL suitable name.
		String name = moduleName.replace(" ", "+");
		return "http://eve-marketdata.com/price_check.php?type=" + opType.toLowerCase() + "&region_id=-1&type_name_header="
				+ name;
	}

	private static Vector<TrackEntry> parseMarketDataEMD(final String itemName, final EMarketSide opType) {
		MarketDataService.logger.info(">> AndroidStorageConnector.parseMarketData");
		Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
		try {
			org.xml.sax.XMLReader reader;
			reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader("org.htmlparser.sax.XMLReader");

			// Create out specific parser for this type of content.
			EVEMarketDataParser content = new EVEMarketDataParser();
			reader.setContentHandler(content);
			reader.setErrorHandler(content);
			String URLDestination = null;
			if (opType == EMarketSide.SELLER) {
				URLDestination = MarketDataService.getModuleLink(itemName, "SELL");
			}
			if (opType == EMarketSide.BUYER) {
				URLDestination = MarketDataService.getModuleLink(itemName, "BUY");
			}
			if (null != URLDestination) {
				reader.parse(URLDestination);
				marketEntries = content.getEntries();
			}
		} catch (SAXException saxe) {
			MarketDataService.logger.severe(
					"E> Parsing exception while downloading market data for module [" + itemName + "]. " + saxe.getMessage());
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
			MarketDataService.logger.severe("E> Error parsing the market information. " + ioe.getMessage());
		}
		MarketDataService.logger
				.info("<< AndroidStorageConnector.parseMarketData. marketEntries [" + marketEntries.size() + "]");
		return marketEntries;
	}

	// - F I E L D - S E C T I O N ............................................................................
	private int _locator = -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketDataService(final int locator) {
		_locator = locator;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void run() {
		MarketDataService.marketDataServiceEntryPoint(_locator);
	}
}

// - UNUSED CODE ............................................................................................
