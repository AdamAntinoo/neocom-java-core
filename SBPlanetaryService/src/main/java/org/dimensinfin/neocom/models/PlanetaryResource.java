//	PROJECT:        POC-ASB-Planetary (POC.ASBP)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Angular-SpringBoot.
//	DESCRIPTION:	Proof of Concept. Use of Angular 2.0 and Sprint Boot to create a test service
//					to display and process the Planetary Data of a sample Eve account.
package org.dimensinfin.neocom.models;

import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryResource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("org.dimensinfin.neocom.models");

	// - F I E L D - S E C T I O N ............................................................................
	private int						typeid;
	private double				quantity;
	private String				name		= "<NAME>";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryResource() {
	}

	public PlanetaryResource(int newid, double newquantity) {
		this.typeid = newid;
		this.quantity = newquantity;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getId() {
		return this.typeid;
	}

	public String getName() {
		return this.name;
	}

	public double getQuantity() {
		return this.quantity;
	}

	public void setName(String newname) {
		this.name = newname;
	}

	public void setQuantity(double newq) {
		this.quantity = newq;
	}
}
// - UNUSED CODE ............................................................................................
