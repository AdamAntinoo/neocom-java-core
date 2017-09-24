//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.market;

import java.util.ArrayList;
import java.util.Date;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.INamed;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "MarketOrders")
public class NeoComMarketOrder extends AbstractComplexNode implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long			serialVersionUID	= -167389011690399402L;

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is a generated identifier to allow having duplicated asset numbers when processing updates. This is
	 * the primary key identifier and it is generated by an incremental sequence.
	 */
	@DatabaseField(id = true)
	private long									orderID;
	@DatabaseField
	private long									ownerID						= -1;
	@DatabaseField
	private long									stationID					= -1;
	@DatabaseField
	private int										volEntered;
	@DatabaseField
	private int										volRemaining;
	@DatabaseField
	private int										minVolume;
	@DatabaseField
	private int										orderState;
	@DatabaseField
	private int										typeID						= -1;
	@DatabaseField
	private int										range;
	@DatabaseField
	private int										accountKey;
	@DatabaseField
	private int										duration;
	@DatabaseField
	private double								escrow;
	@DatabaseField
	private double								price;
	@DatabaseField
	private boolean								bid;
	@DatabaseField
	private Date									issuedDate;

	/** Derived fields that store cached data. */
	private transient EveItem			orderItem					= null;
	private transient EveLocation	orderLocation			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComMarketOrder() {
	}

	public NeoComMarketOrder(final long newOrderID) {
		orderID = newOrderID;
	}

	/**
	 * Generate the model elements that want to be represented at the UI.
	 * 
	 * @return
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		result.add(this);
		return result;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getAccountKey() {
		return accountKey;
	}

	public boolean getBid() {
		return bid;
	}

	public int getDuration() {
		return duration;
	}

	public double getEscrow() {
		return escrow;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public EveItem getItem() {
		if (null == orderItem) {
			orderItem = AppConnector.getCCPDBConnector().searchItembyID(typeID);
		}
		return orderItem;
	}

	public int getItemTypeID() {
		return typeID;
	}

	public int getMinVolume() {
		return minVolume;
	}

	public long getOrderID() {
		return orderID;
	}

	public String getOrderingName() {
		return this.getItem().getName();
	}

	public EveItem getOrderItem() {
		return orderItem;
	}

	public EveLocation getOrderLocation() {
		if (null == orderLocation) {
			orderLocation = AppConnector.getCCPDBConnector().searchLocationbyID(stationID);
		}
		return orderLocation;
	}

	public long getOrderLocationID() {
		return stationID;
	}

	public int getOrderState() {
		return orderState;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return volEntered;
	}

	public int getRange() {
		return range;
	}

	public long getStationID() {
		return stationID;
	}

	public int getVolEntered() {
		return volEntered;
	}

	public int getVolRemaining() {
		return volRemaining;
	}

	public void setAccountKey(final int accountKey) {
		this.accountKey = accountKey;
	}

	public void setBid(final int newbid) {
		if (newbid == 1) {
			bid = true;
		} else {
			bid = false;
		}
	}

	public void setDuration(final int duration) {
		this.duration = duration;
	}

	public void setEscrow(final double escrow) {
		this.escrow = escrow;
	}

	public void setIssuedDate(final Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public void setMinVolume(final int minVolume) {
		this.minVolume = minVolume;
	}

	public void setOrderLocationID(final long locationID) {
		stationID = locationID;
	}

	public void setOrderState(final int orderState) {
		this.orderState = orderState;
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
	}

	public void setPrice(final double price) {
		this.price = price;
	}

	public void setRange(final int range) {
		this.range = range;
	}

	public void setStationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	public void setVolEntered(final int volEntered) {
		this.volEntered = volEntered;
	}

	public void setVolRemaining(final int volRemaining) {
		this.volRemaining = volRemaining;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("MarketOrder [");
		buffer.append(orderID).append(" ");
		//		buffer.append("[").append(blueprintID).append("]");
		buffer.append("#").append(typeID).append(" ");
		buffer.append(this.getOrderingName()).append(" - ");
		//		if (activityID == 1) buffer.append("MANUFACTURE").append(" ");
		//		if (activityID == 8) buffer.append("INVENTION").append(" ");
		buffer.append("Volumes:").append(volEntered).append("/").append(volRemaining).append(" ");
		//		buffer.append("Module [").append(moduleName).append("] ");
		buffer.append("Escrow:").append(escrow).append(" ");
		buffer.append("Price:").append(price).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
