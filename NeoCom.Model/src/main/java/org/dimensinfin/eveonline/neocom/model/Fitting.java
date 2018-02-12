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
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.SDEExternalDataManager;
import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

// - CLASS IMPLEMENTATION ...................................................................................
public class Fitting {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("Fitting");

	// - F I E L D - S E C T I O N ............................................................................
	private int fittingId = -1;
	private String name = null;
	private String description = null;
	private int shipTypeId = -1;
	private List<FittingItem> items = new ArrayList<FittingItem>();
	private transient EveItem shipItem = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Fitting() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public int getFittingId() {
		return fittingId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

//	protected int getShipTypeId() {
//		return shipTypeId;
//	}

	public List<FittingItem> getItems() {
		return items;
	}

	public Fitting setFittingId( final int fittingId ) {
		this.fittingId = fittingId;
		return this;
	}

	public Fitting setName( final String name ) {
		this.name = name;
		return this;
	}

	public Fitting setDescription( final String description ) {
		this.description = description;
		return this;
	}

	public Fitting setShipTypeId( final int shipTypeId ) {
		this.shipTypeId = shipTypeId;
		// Update the transient item details from this type identifier.
		shipItem= GlobalDataManager.searchItem4Id(shipTypeId);
		return this;
	}

	/**
	 * During the transformation this method will be called with the original list of items that are encoded in location and in
	 * type. During the assignment we should process that list and expand them to a full list of enumerated ship locations and
	 * full eve items type.
	 * @param items origianl OK item list.
	 */
	protected void setItems( final List<CharacterscharacterIdfittingsItems> items ) {
		// Process the list of items and transform them into ship locations and game items.
		this.items.clear();
		for(CharacterscharacterIdfittingsItems item: items){
			final FittingItem newitem = new FittingItem(item.getTypeId())
					.setFlag(item.getFlag())
					.setQuantity(item.getQuantity());
			this.items.add(newitem);
		}
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public class FittingItem extends NeoComNode{
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private int typeId = -1;
		private int flag = 0;
		private int quantity = 0;
		private transient EveItem itemDetails = null;
//		private EShipSlotLocation slotLocation=EShipSlotLocation.CARGOHOLD;
		private SDEExternalDataManager.InventoryFlag detailedFlag = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public FittingItem( final Integer typeId ) {
			this.typeId=typeId;
			itemDetails=GlobalDataManager.searchItem4Id(typeId);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		// --- G E T T E R S   &   S E T T E R S
		public int getTypeId() {
			return typeId;
		}

		public int getQuantity() {
			return quantity;
		}

		public EveItem getItemDetails() {
			return itemDetails;
		}

		public SDEExternalDataManager.InventoryFlag getDetailedFlag() {
			return detailedFlag;
		}

		public FittingItem setTypeId( final int typeId ) {
			this.typeId = typeId;
			return this;
		}

		public FittingItem setFlag( final int flag ) {
			this.flag = flag;
			// Transform the numeric flag to a categorized value.
			detailedFlag = SDEExternalDataManager.searchFlag4Id(flag);
			return this;
		}

		public FittingItem setQuantity( final int quantity ) {
			this.quantity = quantity;
			return this;
		}
	}
//	public enum EShipSlotLocation{
//		CARGOHOLD, HIGHSLOT, MEDIUMSLOT, LOWSLOT, RIGSLOT, SYSTEMSLOT
//	}
}
// - UNUSED CODE ............................................................................................
