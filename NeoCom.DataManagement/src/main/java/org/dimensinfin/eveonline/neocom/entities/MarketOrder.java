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
package org.dimensinfin.eveonline.neocom.entities;

import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "MarketOrders")
public class MarketOrder extends ANeoComEntity {
	public enum EOrderStates {
		OPEN, CLOSED, EXPIRED, CANCELLED, PENDING, SCHEDULED
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -167389011690399402L;
	private static Logger logger = LoggerFactory.getLogger("MarketOrder");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	private long orderId;
	@DatabaseField(index = true)
	private long ownerId = -1;
	@DatabaseField
	private int typeId = -1;
	@DatabaseField
	private int regionId = -2;
	@DatabaseField
	private long locationId = -3;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private GetCharactersCharacterIdOrders200Ok.RangeEnum range = null;
	@DatabaseField
	private double price;
	@DatabaseField
	private int volumeTotal = 0;
	@DatabaseField
	private int volumeRemain = 0;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private DateTime issued;
	@DatabaseField
	private boolean isBuyOrder = false;
	@DatabaseField
	private int minVolume;
	@DatabaseField
	private double escrow;
	@DatabaseField
	private int duration;
	@DatabaseField
	private boolean isCorporation = false;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private EOrderStates orderState=EOrderStates.OPEN;

	/**
	 * Derived fields that store cached data.
	 */
	private transient EveItem orderItem = null;
	private transient EsiLocation orderLocation = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrder() {
//		jsonClass = "MarketOrder";
	}

	public MarketOrder( final long newOrderId ) {
		this();
		orderId = newOrderId;
		try {
			Dao<MarketOrder, String> orderDao = accessGlobal().getNeocomDBHelper().getMarketOrderDao();
			// Try to create the record. It fails then it was already created.
			orderDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [MarketOrder.<constructor>]> Market Order exists. Update values.");
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public MarketOrder store() {
		try {
			Dao<MarketOrder, String> orderDao = accessGlobal().getNeocomDBHelper().getMarketOrderDao();
			// Try to create the record. It fails then it was already created.
			orderDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [MarketOrder.<constructor>]> Market Order exists. Update values.");
			this.store();
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public long getOrderId() {
		return orderId;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public int getTypeId() {
		return typeId;
	}

	public GetCharactersCharacterIdOrders200Ok.RangeEnum getRange() {
		return range;
	}

	public double getPrice() {
		return price;
	}

	public int getVolumeTotal() {
		return volumeTotal;
	}

	public int getVolumeRemain() {
		return volumeRemain;
	}

	public int getDuration() {
		return duration;
	}

	public double getEscrow() {
		return escrow;
	}

	public DateTime getIssuedDate() {
		return issued;
	}

	public EveItem getItem() {
		if (null == orderItem) {
			try {
				orderItem = accessGlobal().searchItem4Id(typeId);
			} catch (NeoComRuntimeException neoe) {
				orderItem = new EveItem();
			}
		}
		return orderItem;
	}

	public EsiLocation getLocation() {
		if (null == orderLocation) {
			try {
				orderLocation = accessGlobal().searchLocation4Id(locationId);
			} catch (NeoComRuntimeException neoe) {
				orderLocation = new EsiLocation();
			}
		}
		return orderLocation;
	}

	public EOrderStates getOrderState() {
		return orderState;
	}

	public MarketOrder setOrderId( final long orderId ) {
		this.orderId = orderId;
		return this;
	}

	public MarketOrder setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
		return this;
	}

	public MarketOrder setTypeId( final int typeId ) {
		this.typeId = typeId;
		return this;
	}

	public MarketOrder setRegionId( final int regionId ) {
		this.regionId = regionId;
		return this;
	}

	public MarketOrder setLocationId( final long locationId ) {
		this.locationId = locationId;
		return this;
	}

	public MarketOrder setRange( final GetCharactersCharacterIdOrders200Ok.RangeEnum range ) {
		this.range = range;
		return this;
	}

	public MarketOrder setPrice( final double price ) {
		this.price = price;
		return this;
	}

	public MarketOrder setVolumeTotal( final int volumeTotal ) {
		this.volumeTotal = volumeTotal;
		return this;
	}

	public MarketOrder setVolumeRemain( final int volumeRemain ) {
		this.volumeRemain = volumeRemain;
		return this;
	}

	public MarketOrder setBuyOrder( final boolean buyOrder ) {
		isBuyOrder = buyOrder;
		return this;
	}

	public MarketOrder setMinVolume( final int minVolume ) {
		this.minVolume = minVolume;
		return this;
	}

	public MarketOrder setEscrow( final double escrow ) {
		this.escrow = escrow;
		return this;
	}

	public MarketOrder setDuration( final int duration ) {
		this.duration = duration;
		return this;
	}

	public MarketOrder setCorporation( final boolean corporation ) {
		isCorporation = corporation;
		return this;
	}

	public MarketOrder setIssued( final DateTime issued ) {
		this.issued = issued;
		return this;
	}

	public MarketOrder setOrderState( final EOrderStates orderState ) {
		this.orderState = orderState;
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("MarketOrder [");
		buffer.append(orderId).append(" ");
		//		buffer.append("[").append(blueprintId).append("]");
		buffer.append("#").append(typeId).append(" ");
//		buffer.append(this.getOrderingName()).append(" - ");
		//		if (activityId == 1) buffer.append("MANUFACTURE").append(" ");
		//		if (activityId == 8) buffer.append("INVENTION").append(" ");
		buffer.append("Volumes:").append(volumeTotal).append("/").append(volumeRemain).append(" ");
		//		buffer.append("Module [").append(moduleName).append("] ");
		buffer.append("Escrow:").append(escrow).append(" ");
		buffer.append("Price:").append(price).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
