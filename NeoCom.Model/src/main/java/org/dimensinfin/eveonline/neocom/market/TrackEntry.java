//	PROJECT:        EveMarket
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.eveonline.neocom.market;

//- IMPORT SECTION .........................................................................................
import java.text.NumberFormat;
import java.text.ParseException;

//- CLASS IMPLEMENTATION ...................................................................................
public class TrackEntry {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String	stationName	= "";
	private int			qty					= 0;
	private double	price				= 0.0;
	private double	security		= 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public double getPrice() {
		return price;
	}

	public int getQty() {
		return qty;
	}

	public String getStationName() {
		return stationName;
	}

	public void setPrice(final String tagContent) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US)
					.parse(tagContent.trim().replace('\n', ' ').replaceAll(" +", " "));
			price = number.doubleValue();
		} catch (NumberFormatException nfe) {
			price = 10.0;
		} catch (ParseException pe) {
			price = 10.0;
		}
	}

	public void setQty(final String tagContent) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US).parse(tagContent);
			qty = number.intValue();
		} catch (NumberFormatException nfe) {
			qty = 0;
		} catch (ParseException pe) {
			qty = 0;
		}
	}

	public void setSecurity(final String tagContent) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US).parse(tagContent);
			security = number.doubleValue();
		} catch (NumberFormatException nfe) {
			security = 1.0;
		} catch (ParseException pe) {
			security = 1.0;
		}
	}

	public void setStationName(final String tagContent) {
		stationName = tagContent.trim().replace('\n', ' ').replaceAll(" +", " ");
	}

	@Override
	public String toString() {
		return "stationName=" + stationName + " qty=" + qty + " price=" + price;
	}
}
