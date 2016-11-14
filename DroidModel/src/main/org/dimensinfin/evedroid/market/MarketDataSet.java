//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.market;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.model.EveLocation;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class stores the module and the market information and any other data for manufacturing calculations
 * and module classification. This is the equivalent to the Market card on the excel file and should be able
 * to calculate and generate any manufacturing information usable on the User Interface.<br>
 * 
 * Because on some cases the information may not be available and on Android the update is delegated to a
 * background task the class should be able to cope with NO DATA environments and generate dummy data that
 * will indicate the UI of such state.
 * 
 * @author Adam Antinoo
 */
public class MarketDataSet implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= -2976488566617309014L;
	private static Logger											logger						= Logger.getLogger("MarketDataSet");
	private static final int									HIGH_SECURITY			= 1;
	private static final int									LOW_SECURITY			= 2;
	private static final int									NULL_SECURITY			= 3;

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * The data structures of the price and orders pending on the different markets for this single module.
	 */
	private transient Vector<MarketDataEntry>	dataOnMarketHub		= null;
	private MarketDataEntry										bestmarkethigh		= null;
	private MarketDataEntry										bestmarketlow			= null;
	private MarketDataEntry										bestmarketnull		= null;
	private Instant														timestamp					= new Instant(0);
	private int																id								= -2;
	private EMarketSide												side							= EMarketSide.SELLER;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * When creating the new data we have to access the base information that is the item and then copy the
	 * price from the base price of the item to the price of the card. This will allow use the card without
	 * problems while the background processes update the information from the network provider.
	 * 
	 * @param id
	 *          item id to store and to get info for.
	 */
	public MarketDataSet(final int id, final EMarketSide side) {
		this.id = id;
		this.side = side;
		double baseprice = AppConnector.getDBConnector().searchItembyID(id).getBaseprice();
		bestmarkethigh = bestmarketlow = bestmarketnull = new MarketDataEntry(new EveLocation());
		bestmarkethigh.setPrice(baseprice);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public MarketDataEntry getBestMarket() {
		return bestmarkethigh;
	}

	public Instant getTS() {
		return timestamp;
	}

	public void markUpdate() {
		timestamp = new Instant();
	}

	public void setData(final Vector<MarketDataEntry> hubData) {
		dataOnMarketHub = hubData;
		updateBestMarket();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MarketDataSet [");
		buffer.append(side).append(" ").append(bestmarkethigh.toString());
		buffer.append("]");
		return buffer.toString();
	}

	private int getSecurityCategory(final String security) {
		try {
			double sec = Double.parseDouble(security);
			if (sec >= 0.5) return HIGH_SECURITY;
			if (sec >= 0.0) return LOW_SECURITY;
		} catch (RuntimeException rtex) {
			return NULL_SECURITY;
		}
		return NULL_SECURITY;
	}

	/**
	 * Iterates over the market information to get the market data with the better price depending on the market
	 * direction.<br>
	 * If the list is empty there is no best market information so we can set the default item price
	 * information.
	 */
	private synchronized void updateBestMarket() {
		if (side == EMarketSide.SELLER) {
			if ((null == dataOnMarketHub) || (dataOnMarketHub.size() < 1)) {
				double baseprice = AppConnector.getDBConnector().searchItembyID(id).getBaseprice();
				bestmarkethigh = bestmarketlow = bestmarketnull = new MarketDataEntry(new EveLocation());
				bestmarkethigh.setPrice(baseprice);
				logger.info("-- MarketDataSet.updateBestMarket - using default price: " + baseprice); //$NON-NLS-1$
			} else {
				// Scan the list of entries to store the best one for each of the categories.
				bestmarkethigh = null;
				for (MarketDataEntry mde : dataOnMarketHub) {
					logger.info("-- MarketDataSet.updateBestMarket - processing MDE: " + mde); //$NON-NLS-1$
					int sec = getSecurityCategory(mde.getSecurity());
					if (sec == HIGH_SECURITY) {
						if (null == bestmarkethigh)
							bestmarkethigh = mde;
						else if (bestmarkethigh.getPrice() > mde.getPrice()) {
							bestmarkethigh = mde;
							logger.info("-- MarketDataSet.updateBestMarket - setting a better SELLER: " + mde); //$NON-NLS-1$
						}
					}
				}
				// Check for empty process. For example on blueprints.
				if (null == bestmarkethigh) bestmarkethigh = new MarketDataEntry(new EveLocation());
			}
		}
		if (side == EMarketSide.BUYER) {
			if ((null == dataOnMarketHub) || (dataOnMarketHub.size() < 1)) {
				double baseprice = AppConnector.getDBConnector().searchItembyID(id).getBaseprice();
				bestmarkethigh = bestmarketlow = bestmarketnull = new MarketDataEntry(new EveLocation());
				bestmarkethigh.setPrice(baseprice);
				logger.info("-- MarketDataSet.updateBestMarket - using default price: " + baseprice); //$NON-NLS-1$
			} else {
				// Scan the list of entries to store the best one for each of the categories.
				bestmarkethigh = null;
				for (MarketDataEntry mde : dataOnMarketHub) {
					int sec = getSecurityCategory(mde.getSecurity());
					if (sec == HIGH_SECURITY) {
						if (null == bestmarkethigh)
							bestmarkethigh = mde;
						else if (bestmarkethigh.getPrice() < mde.getPrice()) {
							bestmarkethigh = mde;
							logger.info("-- MarketDataSet.updateBestMarket - setting a better BUYER: " + mde); //$NON-NLS-1$
						}
					}
				}
				// Check for empty process. For example on blueprints.
				if (null == bestmarkethigh) bestmarkethigh = new MarketDataEntry(new EveLocation());
			}
		}
	}
}

// - UNUSED CODE ............................................................................................
