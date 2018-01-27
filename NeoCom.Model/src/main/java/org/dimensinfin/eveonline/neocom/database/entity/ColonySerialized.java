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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringUtils;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.planetary.ColonyCoreStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "ColonyStorage")
public class ColonySerialized {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ColonySerialized.class);

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * Unique identifier. Different characters can have colonies on the same planet so only the Planet identifier is
	 * not enough. The key is the character id-planet id.
	 */
	@DatabaseField(id = true)
	public long pinIdentifier = -1;
	@DatabaseField(index = true)
	public String planetIdentifier = "-";
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private ColonyCoreStructure colonySerialization = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private ColonySerialized () {
		super();
	}

	public ColonySerialized (final long identifier) {
		this();
		pinIdentifier = identifier;
		try {
			Dao<ColonySerialized, String> colonyStorageDao = GlobalDataManager.getHelper().getColonySerializedDao();
			// Try to create the key. It fails then  it was already created.
			colonyStorageDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [ColonyStorage.<constructor>]> ColonyStorage for planet {} exists. Update values.", pinIdentifier);
			store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getPinIdentifier () {
		return pinIdentifier;
	}

	public String getPlanetIdentifier () {
		return planetIdentifier;
	}

	public ColonyCoreStructure getColonySerialization () {
		return colonySerialization;
	}

	public ColonySerialized setPlanetIdentifier (final String planetIdentifier) {
		this.planetIdentifier = planetIdentifier;
		return this;
	}

	public ColonySerialized setColonySerialization (final ColonyCoreStructure colonySerialization) {
		this.colonySerialization = colonySerialization;
		return this;
	}

	public ColonySerialized store () {
		if ( (StringUtils.isNotEmpty(planetIdentifier)) && (null!=colonySerialization) ) {
			try {
				Dao<ColonySerialized, String> colonyStorageDao = GlobalDataManager.getHelper().getColonySerializedDao();
				// Try to create the key. It fails then  it was already created.
				colonyStorageDao.createOrUpdate(this);
			} catch (final SQLException sqle) {
				logger.info("WR [ColonyStorage.store]> {}", sqle.getMessage());
			}
		}
		return this;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ColonyStorage [");
		buffer.append("identifier: ").append(planetIdentifier);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
