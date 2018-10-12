//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.market;

// - IMPORT SECTION .........................................................................................

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import org.dimensinfin.eveonline.neocom.model.EveLocation;

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
	//--- G E T T E R S   &   S E T T E R S
	public EveLocation getLocation() {
		return this.location;
	}

	public MarketDataEntry setLocation( final EveLocation location ) {
		this.location = location;
		return this;
	}

	public int getQty() {
		return this.qty;
	}

	public MarketDataEntry setQty( final int qty ) {
		this.qty = qty;
		return this;
	}

	public double getPrice() {
		return this.price;
	}

	public MarketDataEntry setPrice( final double price ) {
		this.price = price;
		return this;
	}

	//--- N O N   E X P O R T A B L E   F I E L D S
	@JsonIgnore
	public String getConstellation() {
		return location.getConstellation();
	}

	@JsonIgnore
	public String getRegion() {
		return location.getRegion();
	}

	@JsonIgnore
	public String getSecurity() {
		return location.getSecurity();
	}

	public void addQty( final int addqty ) {
		qty += addqty;
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
