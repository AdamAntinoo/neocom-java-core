//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.market;

// - IMPORT SECTION .........................................................................................

import org.dimensinfin.eveonline.neocom.model.EveLocation;

import java.io.Serializable;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This is a simple class to store the data information for a module in a single market hub, like the better
 * price or the aggregated quantity on the top orders. Information may be of two flavors, BUY order or SELL
 * orders. EveDroid only uses the BUY orders for their calculations but this will improve usability on other
 * apps. These data will be made persistent to the app database so at any time we can have obsolete but valid
 * price information.
 * @author Adam Antinoo
 */
public class MarketDataEntry implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 7263135920147527466L;
	//	private static Logger			logger						= Logger.getLogger("MarketData");

	// - F I E L D - S E C T I O N ............................................................................
	private EveLocation location = null;
	private int qty = 0;
	private double price = 999999999999.99;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketDataEntry() {
	}

	public MarketDataEntry( final EveLocation entryLocation ) {
		location = entryLocation;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addQty( final int addqty ) {
		qty += addqty;
	}

	public String getConstellation() {
		return location.getConstellation();
	}

	public EveLocation getLocation() {
		return location;
	}

	public double getPrice() {
		return price;
	}

	public int getQty() {
		return qty;
	}

	public String getRegion() {
		return location.getRegion();
	}

	public String getSecurity() {
		return location.getSecurity();
	}

	public int getSystemId() {
		return this.location.getSystemId();
	}

	public String getSystem() {
		return this.location.getSystem();
	}

	public void setLocation( final EveLocation location ) {
		this.location = location;
	}

	public void setPrice( final double price ) {
		this.price = price;
	}

	public void setQty( final int qty ) {
		this.qty = qty;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MarketDataEntry [");
		buffer.append("qty: ").append(qty).append(" ");
		buffer.append(price).append(" ISK").append(" ");
		buffer.append("location: ").append(location).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
