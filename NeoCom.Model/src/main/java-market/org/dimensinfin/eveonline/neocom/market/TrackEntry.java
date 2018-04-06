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
package org.dimensinfin.eveonline.neocom.market;

import java.text.NumberFormat;
import java.text.ParseException;

//- CLASS IMPLEMENTATION ...................................................................................
public class TrackEntry {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String stationName = "";
	private int qty = 0;
	private double price = 0.0;
	private double security = 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T E R S   &   S E T T E R S
	public double getPrice() {
		return price;
	}

	public int getQty() {
		return qty;
	}

	public String getStationName() {
		return stationName;
	}

	public TrackEntry setPrice( final String tagContent ) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US)
					.parse(tagContent.trim().replace('\n', ' ').replaceAll(" +", " "));
			price = number.doubleValue();
		} catch (NumberFormatException nfe) {
			price = 10.0;
		} catch (ParseException pe) {
			price = 10.0;
		}
		return this;
	}

	public TrackEntry setQty( final String tagContent ) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US).parse(tagContent);
			qty = number.intValue();
		} catch (NumberFormatException nfe) {
			qty = 0;
		} catch (ParseException pe) {
			qty = 0;
		}
		return this;
	}

	public TrackEntry setSecurity( final String tagContent ) {
		try {
			Number number = NumberFormat.getNumberInstance(java.util.Locale.US).parse(tagContent);
			security = number.doubleValue();
		} catch (NumberFormatException nfe) {
			security = 1.0;
		} catch (ParseException pe) {
			security = 1.0;
		}
		return this;
	}

	public TrackEntry setStationName( final String tagContent ) {
		stationName = tagContent.trim().replace('\n', ' ').replaceAll(" +", " ");
		return this;
}

	@Override
	public String toString() {
		return new StringBuffer("TrackEntry [")
		.append("stationName=")
		.append(stationName).append(" ")
		.append("qty=")
				.append(qty ).append(" ")
				.append("price=")
				.append( price).append(" ")
				.append("}")
				.toString();
	}
}
