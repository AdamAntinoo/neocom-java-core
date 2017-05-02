//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class Schematics {
	public enum ESchematicDirection {
		INPUT, OUTPUT
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger		= Logger.getLogger("Schematics");

	// - F I E L D - S E C T I O N ............................................................................
	private int									typeId		= -1;
	private int									qty				= 0;
	private ESchematicDirection	direction	= ESchematicDirection.INPUT;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public Schematics() {
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Add the schematics data for one of the schematics components. Components have an id and a required
	 * queantity and can be of Input type or of Output type.
	 * 
	 * @param typeId
	 *          the item type id
	 * @param quantity
	 *          the quantity required or produced
	 * @param input
	 *          input direction of true
	 */
	public Schematics addData(final int typeId, final int quantity, final boolean input) {
		this.typeId = typeId;
		qty = quantity;
		if (!input) {
			direction = ESchematicDirection.OUTPUT;
		}
		return this;
	}

	public ESchematicDirection getDirection() {
		return direction;
	}

	public int getQty() {
		return qty;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setQty(final int qty) {
		this.qty = qty;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Schematics [");
		buffer.append(direction.name()).append(" #").append(this.getTypeId()).append("x").append(this.getQty());
		//		buffer.append("#").append(this.getTypeId()).append(" ");
		//		buffer.append("qty: ").append(this.getQty()).append(" ");
		//		buffer.append("direction: ").append(direction.name()).append(" ");
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

}

// - UNUSED CODE ............................................................................................
