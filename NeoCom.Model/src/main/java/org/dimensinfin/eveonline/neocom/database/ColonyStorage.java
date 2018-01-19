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
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringUtils;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.DatabaseVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created by Adam on 19/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "ColonyStorage")
public class ColonyStorage {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ColonyStorage.class);

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	public int planetIdentifier = -1;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private String colonySerialization = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ColonyStorage () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getPlanetIdentifier () {
		return planetIdentifier;
	}

	public String getColonySerialization () {
		return colonySerialization;
	}

	public ColonyStorage setPlanetIdentifier (final int planetIdentifier) {
		this.planetIdentifier = planetIdentifier;
		return this;
	}

	public ColonyStorage setColonySerialization (final String colonySerialization) {
		this.colonySerialization = colonySerialization;
		return this;
	}

	public void store () {
		if ( (planetIdentifier > 0) || (StringUtils.isNotEmpty(colonySerialization)) ) {
			try {
				Dao<ColonyStorage, String> colonyStorageDao = ModelAppConnector.getSingleton().getNewDBConnector().getColonyStorageDao();
				// Try to create the key. It fails then  it was already created.
				colonyStorageDao.create(this);
			} catch (final SQLException sqle) {
				logger.info("WR [DatabaseVersion.<init>]>DatabaseVersion exists. Update values.");
//				this.setDirty(true);
			}
		}
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ColonyStorage [");
		buffer.append("identifier: ").append(planetIdentifier);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
