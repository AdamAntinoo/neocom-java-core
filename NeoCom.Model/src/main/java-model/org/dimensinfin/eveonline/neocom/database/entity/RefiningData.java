//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.database.entity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "refiningdata")
public class RefiningData extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("RefiningData");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	private int locationIdentifier = -3;
	@DatabaseField
	private float refiningRatio = 30.0f;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RefiningData() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public RefiningData store() {
		// Update the extraction time.
		try {
			Dao<RefiningData, String> refiningDataDao = accessGlobal().getNeocomDBHelper().getRefiningDataDao();
			// Store should only update already created records. Tables with generated id should use create() for creation.
			refiningDataDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [RefiningData.<constructor>]> Exception Creating or Updating values. {}"
					, sqle.getMessage());
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public int getLocationIdentifier() {
		return locationIdentifier;
	}

	public float getRefiningRatio() {
		return refiningRatio;
	}

	public RefiningData setLocationIdentifier( final int locationIdentifier ) {
		this.locationIdentifier = locationIdentifier;
		return this;
	}

	public RefiningData setRefiningRatio( final float refiningRatio ) {
		this.refiningRatio = refiningRatio;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("RefiningData [ ");
		buffer.append("locationIdentifier: ").append(this.locationIdentifier).append(" ");
		buffer.append("refiningRatio: ").append(this.refiningRatio).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
