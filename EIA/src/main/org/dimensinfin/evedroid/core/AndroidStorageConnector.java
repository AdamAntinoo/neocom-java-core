//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
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
import java.util.Vector;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.config.DevelopmentStorageConnector;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.connector.IStorageConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.market.MarketDataSet;
import org.dimensinfin.evemarket.model.TrackEntry;
import org.dimensinfin.evemarket.parser.EVEMarketDataParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Environment;

// - CLASS IMPLEMENTATION ...................................................................................
public class AndroidStorageConnector implements IStorageConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("AndroidStorageConnector");

	// - F I E L D - S E C T I O N ............................................................................
	private Context				_context	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AndroidStorageConnector(final Context app) {
		_context = app;
	}

	public File accessAppStorage(final String resourceString) {
		if (null != resourceString)
			return new File(Environment.getExternalStorageDirectory(), AppConnector.getResourceString(R.string.appfoldername)
					+ AppConnector.getResourceString(R.string.app_versionsuffix) + "/" + resourceString);
		else
			return new File(Environment.getExternalStorageDirectory(), AppConnector.getResourceString(R.string.appfoldername)
					+ AppConnector.getResourceString(R.string.app_versionsuffix));
	}

	/**
	 * Depending on the value of the <code>force</code> field the cache check if to check the access time of the
	 * entry or not. Then tries to locate it on the cache structures and checks the access time. If the force
	 * parameter is true or the last access time recorded has expired, the data ir retrieved again from the
	 * source by using and object that understands the information being accessed. For that we have to record
	 * the data inside the cache with the corresponding <code>Interpreter</code>. <br>
	 * This particular call returns the root of the XML DOM document without processing.
	 * 
	 * @param link
	 * @param force
	 */
	public Element accessDOMDocument(final String url) {
		logger.info(">> AndroidStorageConnector.accessDOMDocument");
		AppConnector.startChrono();
		Element element = null;
		try {
			element = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			logger.info("E> Cannot generate simple element");
			return element;
		}
		try {
			InputStream netResource = AppConnector.getStorageConnector().accessNetworkResource(url);
			element = EVEDroidApp.parseDOMDocument(netResource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("<< AndroidStorageConnector.accessDOMDocument [" + AppConnector.timeLapse() + "]");
		return element;
	}

	public InputStream accessInternalStorage(final String resourceName) throws IOException {
		return EVEDroidApp.getSingletonApp().getApplicationContext().getAssets().open(resourceName);
	}

	public InputStream accessNetworkResource(final String link) throws IOException {
		logger.info(">> AndroidStorageConnector.accessNetworkResource");
		logger.info("-- AccessNetworkResource MISS!. Reading DOM Document [" + link + "]");
		BufferedInputStream is = null;
		if (AppWideConstants.DEVELOPMENT) {
			// Try to locate the resource on the recorded list. If not go to network.
			String recordFileName = DevelopmentStorageConnector.recordedXMLResponses.get(link);
			if (null != recordFileName) {
				// ANDROID This line cannot be changed because it is the way we get an asset on Android.
				is = new BufferedInputStream(
						EVEDroidApp.getSingletonApp().getApplicationContext().getAssets().open(recordFileName));
				logger.info("-- Using test file downloader.");
			}
		}
		if (null == is) {
			URL url = new URL(link);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			is = new BufferedInputStream(urlConnection.getInputStream());
			logger.info("-- Using network downloader.");
		}
		logger.info("<< AndroidStorageConnector.accessNetworkResource");
		return is;
	}

	public boolean checkStorageResource(final File base, final String fileName) {
		File toCheck = new File(base, fileName);
		return toCheck.exists();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The cache storage depend on the global state of the DEVELOPING flag. For developing the cache will be on
	 * the application storage while for production may be on the Android standard place.
	 */
	public File getCacheStorage() {
		if (AppWideConstants.DEVELOPMENT)
			return accessAppStorage(AppConnector.getResourceString(R.string.cachefoldername));
		else
			return new File(EVEDroidApp.getSingletonApp().getApplicationContext().getCacheDir(),
					AppConnector.getResourceString(R.string.cachefoldername));
	}

	private String readJsonData(final int typeid) {
		StringBuffer data = new StringBuffer();
		try {
			String str = "";
			URL url = new URL("http://api.eve-central.com/api/marketstat/json?typeid=" + typeid + "&regionlimit=10000002");
			URLConnection urlConnection = url.openConnection();
			InputStream is = new BufferedInputStream(urlConnection.getInputStream());
			// InputStream is = AppConnector.getStorageConnector().accessNetworkResource(
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
	 * New version that downloads the information from eve-central in json format.
	 */
	public Vector<TrackEntry> parseMarketDataEC(final int itemid, final EMarketSide opType) {
		logger.info(">> AndroidStorageConnector.parseMarketData");
		Vector<TrackEntry> marketEntries = new Vector<TrackEntry>();
		// try {
		// Making a request to url and getting response
		String jsonStr = readJsonData(itemid);
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
		// } catch (SAXException saxe) {
		// logger.severe("E> Parsing exception while downloading market data for module [" + itemName + "]. "
		// + saxe.getMessage());
		// } catch (IOException ioe) {
		// // TODO Auto-generated catch block
		// ioe.printStackTrace();
		// logger.severe("E> Error parsing the market information. " + ioe.getMessage());
		// }
		logger.info("<< AndroidStorageConnector.parseMarketData. marketEntries [" + marketEntries.size() + "]");
		return marketEntries;
	}

	public Vector<TrackEntry> parseMarketDataEMD(final String itemName, final EMarketSide opType) {
		logger.info(">> AndroidStorageConnector.parseMarketData");
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
				URLDestination = getModuleLink(itemName, "SELL");
			}
			if (opType == EMarketSide.BUYER) {
				URLDestination = getModuleLink(itemName, "BUY");
			}
			if (null != URLDestination) {
				reader.parse(URLDestination);
				marketEntries = content.getEntries();
			}
		} catch (SAXException saxe) {
			logger.severe(
					"E> Parsing exception while downloading market data for module [" + itemName + "]. " + saxe.getMessage());
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
			logger.severe("E> Error parsing the market information. " + ioe.getMessage());
		}
		logger.info("<< AndroidStorageConnector.parseMarketData. marketEntries [" + marketEntries.size() + "]");
		return marketEntries;
	}

	public MarketDataSet readDiskMarketData(final int itemID, final EMarketSide side) {
		try {
			String filePath = AppConnector.getResourceString(R.string.marketdatacachefoldername) + "/"
					+ createMarketDataFileName(itemID, side);
			File dataFile = new File(AppConnector.getStorageConnector().getCacheStorage(), filePath);
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(dataFile));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			MarketDataSet data = null;
			try {
				data = (MarketDataSet) input.readObject();
				logger.info("-- MarketUpdaterService.readDiskData [done]"); //$NON-NLS-1$
				return data;
			} finally {
				input.close();
				buffer.close();
			}
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			logger.info("-- MarketUpdaterService.readDiskData. [Unexpected exception]"); //$NON-NLS-1$
			logger.info("-- MarketUpdaterService.readDiskData." + cnfe.getMessage()); //$NON-NLS-1$
		} catch (final FileNotFoundException fnfe) {
			logger.info("-- MarketUpdaterService.readDiskData. [Cache not found]"); //$NON-NLS-1$
		} catch (final IOException ex) {
			logger.info("-- MarketUpdaterService.readDiskData. [Cache not found]"); //$NON-NLS-1$
		}
		return null;
	}

	public void writeDiskMarketData(final MarketDataSet reference, final int itemID, final EMarketSide side) {
		logger.info(">> UpdaterService.writeDiskData"); //$NON-NLS-1$
		try {
			String filePath = AppConnector.getResourceString(R.string.marketdatacachefoldername) + "/"
					+ createMarketDataFileName(itemID, side);
			File dataFile = new File(AppConnector.getStorageConnector().getCacheStorage(), filePath);
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(dataFile));
			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				// Update the time stamp before storing the object.
				reference.markUpdate();
				output.writeObject(reference);
				output.flush();
				logger.info("<< UpdaterService.writeDiskData [true]"); //$NON-NLS-1$
				return;
			} finally {
				output.flush();
				output.close();
				buffer.close();
			}
		} catch (final FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		logger.info("<< UpdaterService.writeDiskData [false]"); //$NON-NLS-1$
	}

	private String createMarketDataFileName(final int itemID, final EMarketSide side) {
		return new Integer(itemID).toString() + "_" + side.toString().toUpperCase() + ".s";
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
	private String getModuleLink(final String moduleName, final String opType) {
		// Adjust the module name to a URL suitable name.
		String name = moduleName.replace(" ", "+");
		return "http://eve-marketdata.com/price_check.php?type=" + opType.toLowerCase() + "&region_id=-1&type_name_header="
				+ name;
	}
}

// - UNUSED CODE ............................................................................................
