//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.industry;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class InventoryFlag {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("InventoryFlag");

	// - F I E L D - S E C T I O N ............................................................................
	private int flagId = -1;
	private String flagName = null;
	private String flagText = null;
	private int orderId = 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public InventoryFlag() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getFlagID() {
		return flagId;
	}

	public String getFlagName() {
		return flagName;
	}

	public String getFlagText() {
		return flagText;
	}

	public int getOrderID() {
		return orderId;
	}

	public InventoryFlag setFlagID( final int flagId ) {
		this.flagId = flagId;
		return this;
	}

	public InventoryFlag setFlagName( final String flagName ) {
		this.flagName = flagName;
		return this;
	}

	public InventoryFlag setFlagText( final String flagText ) {
		this.flagText = flagText;
		return this;
	}

	public InventoryFlag setOrderID( final int orderIDd) {
		this.orderId = orderId;
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("InventoryFlag [ ")
				.append("flagId:").append(flagId).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
