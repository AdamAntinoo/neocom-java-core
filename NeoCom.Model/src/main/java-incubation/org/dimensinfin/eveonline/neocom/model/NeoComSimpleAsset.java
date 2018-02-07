//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

// - IMPORT SECTION .........................................................................................
import com.beimin.eveapi.model.shared.Asset;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import jdk.nashorn.internal.objects.Global;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Assets")
public class NeoComSimpleAsset extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7777046172304070923L;
	private static Logger			logger						= LoggerFactory.getLogger(NeoComSimpleAsset.class);

	public static NeoComSimpleAsset createFrom(final Asset eveAsset) {
		final NeoComSimpleAsset newAsset = new NeoComSimpleAsset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		Long locid = eveAsset.getLocationID();
		if (null == locid) {
			locid = (long) -2;
		}
		newAsset.setLocationID(locid);
		newAsset.setQuantity(eveAsset.getQuantity());
		newAsset.setFlag(eveAsset.getFlag());
		newAsset.setSingleton(eveAsset.getSingleton());
		// Get access to the Item and update the copied fields.
		newAsset.itemCache = GlobalDataManager.searchItem4Id(newAsset.getTypeID());
		//		if (null != item) {
		//			try {
		//				newAsset.setName(item.getName());
		//				newAsset.setCategory(item.getCategory());
		//				newAsset.setGroupName(item.getGroupName());
		//				newAsset.setTech(item.getTech());
		//				if (item.isBlueprint()) {
		//					newAsset.setBlueprintType(eveAsset.getRawQuantity());
		//				}
		//			} catch (RuntimeException rtex) {
		//			}
		//		}
		//		// Add the asset value to the database.
		//		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is a generated identifier to allow having duplicated asset numbers when processing updates. This is
	 * the primary key identifier and it is generated by an incremental sequence.
	 */
	@DatabaseField(generatedId = true)
	private long							id					= -2;
	@DatabaseField(index = true)
	private long							assetID;
	@DatabaseField(index = true)
	private long							locationID	= -1;
	@DatabaseField(index = true)
	private int								typeID;
	@DatabaseField
	private int								quantity		= 0;
	@DatabaseField
	private int								flag;
	@DatabaseField
	private boolean						singleton		= false;

	// - C A C H E D   F I E L D S
	@JsonIgnore
	private transient EveItem	itemCache		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private NeoComSimpleAsset() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getAssetID() {
		return assetID;
	}

	public void setAssetID(final long assetID) {
		this.assetID = assetID;
	}

	public int getTypeID() {
		return typeID;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}
	/**
	 * New optimization will leave this filed for lazy evaluation. So check if this is empty before getting any
	 * access and if so download from the Item Cache.
	 *
	 * @return
	 */
	public EveItem getItem() {
		if (null == itemCache) {
			itemCache = GlobalDataManager.searchItem4Id(typeID);
		}
		return itemCache;
	}
	public double getPrice() {
		return this.getItem().getPrice();
	}

	public long getLocationID() {
		return locationID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(final int flag) {
		this.flag = flag;
	}

	public void setLocationID(final long locationID) {
		this.locationID = locationID;
	}

	public void setSingleton(final boolean singleton) {
		this.singleton = singleton;
	}

	public int getCategoryId() {
		return itemCache.getCategoryId();
	}

	public int getGroupId() {
		return itemCache.getGroupId();
	}

	public String getCategoryName() {
		return itemCache.getCategoryName();
	}

	public String getGroupName() {
		return itemCache.getGroupName();
	}

	public String getName() {
		return itemCache.getName();
	}

}

// - UNUSED CODE ............................................................................................