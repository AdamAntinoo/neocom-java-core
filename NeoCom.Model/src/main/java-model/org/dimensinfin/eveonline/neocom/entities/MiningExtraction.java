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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "MiningExtractions")
public class MiningExtraction extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("MiningExtraction");

	/**
	 * The record id creation used two algorithms. If the date is the current date we add the hour as an identifier. But id the date is not
	 * the current date we should not chage any data on the database since we understand that old data is not being modified. But it can
	 * happen that old data is the first time the it is added to the database. So we set the hout of day to the number 24.
	 * @param date
	 * @param typeId
	 * @param systemId
	 * @param ownerId
	 * @return
	 */
	public static String generateRecordId( final LocalDate date, final int typeId, final long systemId, final long ownerId ) {
		// Check the date.
		final String todayDate = DateTime.now().toString("YYYY/MM/dd");
		final String targetDate = date.toString("YYYY/MM/dd");
		if (todayDate.equalsIgnoreCase(targetDate))
			return new StringBuffer()
					.append(date.toString("YYYY/MM/dd")).append(":")
					.append(DateTime.now().getHourOfDay()).append("-")
					.append(systemId).append("-")
					.append(typeId).append("-")
					.append(ownerId)
					.toString();
		else
			return new StringBuffer()
					.append(date.toString("YYYY/MM/dd")).append(":")
					.append(24).append("-")
					.append(systemId).append("-")
					.append(typeId).append("-")
					.append(ownerId)
					.toString();
	}

	public static String generateRecordId( final String date, final int hour, final int typeId, final long systemId, final long ownerId ) {
		return new StringBuffer()
				.append(date).append(":")
				.append(hour).append("-")
				.append(systemId).append("-")
				.append(typeId).append("-")
				.append(ownerId)
				.toString();
	}

	//	public static String generateExtractionDateName( final LocalDate date ) {
	//		// Check the date.
	//		final String todayDate = DateTime.now().toString("YYYY/MM/dd");
	//		final String targetDate = date.toString("YYYY/MM/dd");
	//		if ( todayDate.equalsIgnoreCase(targetDate) )
	//			return new StringBuffer()
	//					.append(date.toString("YYYY/MM/dd")).append(":")
	//					.append(DateTime.now().getHourOfDay())
	//					.toString();
	//		else
	//			return new StringBuffer()
	//					.append(date.toString("YYYY/MM/dd")).append(":")
	//					.append(24)
	//					.toString();
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	private String id = "YYYY/MM/DD:HH-SYSTEMID-TYPEID-OWNERID";
	@DatabaseField
	private int typeId = -1;
	@DatabaseField
	private int solarSystemId = -2;
	@DatabaseField
	private long quantity = 0;
	@DatabaseField
	private long delta = 0;
	@DatabaseField
	private String extractionDateName = "YYYY/MM/DD";
	@DatabaseField
	private int extractionHour = 24;
	@DatabaseField(index = true)
	private long ownerId = -1;

	private transient EveItem resourceCache = null;
	private transient EveLocation systemCache = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public
	MiningExtraction() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getRecordId() {
		return this.id;
	}

	public MiningExtraction store() {
		// Update the extraction time.
		try {
			Dao<MiningExtraction, String> miningExtractionDao = accessGlobal().getNeocomDBHelper().getMiningExtractionDao();
			// Store should only update already created records. Tables with generated id should use create() for creation.
			miningExtractionDao.update(this);
		} catch (final SQLException sqle) {
			logger.info("WR [MiningExtraction.<constructor>]> Exception Updating values. {}"
					, sqle.getMessage());
		}
		return this;
	}

	public MiningExtraction create( final String recordId ) {
		this.id = recordId;
		try {
			Dao<MiningExtraction, String> miningExtractionDao = accessGlobal().getNeocomDBHelper().getMiningExtractionDao();
			// Tables with generated id should use create() for creation.
			miningExtractionDao.create(this);
		} catch (final SQLException sqle) {
			logger.info("WR [MiningExtraction.<constructor>]> Exception creating new record: {} - {}"
					, this.id, sqle.getMessage());
		}
		return this;
	}

	// ---  G E T T E R S   &   S E T T E R S
	public int getTypeId() {
		return typeId;
	}

	public int getSolarSystemId() {
		return solarSystemId;
	}

	public long getQuantity() {
		return quantity;
	}

	public long getDelta() {
		return delta;
	}

	public String getSystemName() {
		if (null == this.systemCache)
			this.systemCache = accessGlobal().searchLocation4Id(this.solarSystemId);
		return systemCache.getSystem();
	}

	public String getResourceName() {
		if (null == this.resourceCache)
			this.resourceCache = accessGlobal().searchItem4Id(this.typeId);
		return this.resourceCache.getName();
	}

	public String getExtractionDate() {
		return this.extractionDateName;
	}

	public int getExtractionHour() {
		return this.extractionHour;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public MarketDataEntry getLowestSellerPrice() throws ExecutionException, InterruptedException {
		if (null == this.resourceCache)
			this.resourceCache = accessGlobal().searchItem4Id(this.typeId);
		return resourceCache.getLowestSellerPrice();
	}

	public MarketDataEntry getHighestBuyerPrice() throws ExecutionException, InterruptedException {
		if (null == this.resourceCache)
			this.resourceCache = accessGlobal().searchItem4Id(this.typeId);
		return resourceCache.getHighestBuyerPrice();
	}

	public double getVolume() {
		return resourceCache.getVolume();
	}

	public double getPrice() {
		return resourceCache.getPrice();
	}

	public MiningExtraction setTypeId( final int typeId ) {
		this.typeId = typeId;
		return this;
	}

	public MiningExtraction setSolarSystemId( final int solarSystemId ) {
		this.solarSystemId = solarSystemId;
		return this;
	}

	public MiningExtraction setQuantity( final long quantity ) {
		this.quantity = quantity;
		return this;
	}

	public MiningExtraction setDelta( long delta ) {
		this.delta = delta;
		return this;
	}

	public MiningExtraction setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
		return this;
	}

	public MiningExtraction setExtractionDate( final LocalDate extractionDate ) {
		// Update the extractions date string.
		this.extractionDateName = extractionDate.toString("YYYY/MM/dd");
		final String todayDate = DateTime.now().toString("YYYY/MM/dd");
		final String targetDate = extractionDate.toString("YYYY/MM/dd");
		if (todayDate.equalsIgnoreCase(targetDate))
			this.extractionHour = DateTime.now().getHourOfDay();
		else
			this.extractionHour = 24;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MiningExtraction [ ");
		buffer.append("#").append(typeId).append("-").append(getResourceName()).append(" ");
		buffer.append("x").append(quantity).append(" ");
		buffer.append("@").append(solarSystemId).append("-").append(getSystemName()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
