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
package org.dimensinfin.eveonline.neocom.database.entity;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "miningextractions")
public class MiningExtraction extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("MiningExtraction");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(index = true, generatedId = true)
	private long id=-1;
	@DatabaseField
	private int typeId = -1;
	@DatabaseField
	private int solarSystemId = -2;
	@DatabaseField
	private long quantity = 0;
	@DatabaseField
	private long extractionDateNumber = 0;
	@DatabaseField(index = true)
	private String extractionIndexDate = "0000/00/00";
	@DatabaseField
	private String extractionDate = "0000/00/00";
	@DatabaseField(index = true)
	private long ownerId = -1;

	private transient EveItem resourceCache=null;
	private transient EveLocation systemCache=null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MiningExtraction() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	public MiningExtraction store() {
		try {
			Dao<MiningExtraction, String> miningExtractionDao = accessGlobal().getNeocomDBHelper().getMiningExtractionDao();
			// Try to create the record. It fails then it was already created.
			miningExtractionDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [MiningExtraction.<constructor>]> Exception Updating values. {}"
			,sqle.getMessage());
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

	public String getSystemName(){
		if(null==this.systemCache)
			this.systemCache=accessGlobal().searchLocation4Id(this.solarSystemId);
		return systemCache.getSystem();
	}
	public String getResourceName(){
		if(null==this.resourceCache)
			this.resourceCache=accessGlobal().searchItem4Id(this.typeId);
		return this.resourceCache.getName();
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

	public MiningExtraction setDate( final LocalDate date ) {
		// Converts the date to Joda and then to millliseconds.
//		final DateTime extraction = new DateTime(date);
		this.extractionDateNumber = Instant.now().getMillis();
		this.extractionDate = Instant.now().toString();
		this.extractionIndexDate = date.toString("YYYY/MM/dd");
//		logger.info("WR [MiningExtraction.<constructor>]> Datestring {}",date.toString("YYYY/MM/dd"));
		return this;
	}

	public MiningExtraction setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
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
