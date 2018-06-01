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

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "fittingrequests")
public class FittingRequest extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("FittingRequest");

	public enum EFittingRequestState {
		OUTSTANDING
	}

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(index = true, generatedId = true)
	public long id = -2;
	@DatabaseField
	public int corporationId = -1;
	@DatabaseField
	public int targetFitting = -4;
	@DatabaseField
	public int copies = 1;
	@DatabaseField
	public EFittingRequestState state = EFittingRequestState.OUTSTANDING;
	@DatabaseField
	public long registrationDate = Instant.now().getMillis();
	@DatabaseField
	public double estimatedCost = 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingRequest() {
		super();
		jsonClass = "FittingRequest";
//		registrationDate= Instant.now().getMillis();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Update the values at the database record.
	 */
	public FittingRequest store() {
		try {
			final Dao<FittingRequest, String> fittingRequestDao = accessGlobal().getNeocomDBHelper().getFittingRequestDao();
			fittingRequestDao.createOrUpdate(this);
			logger.info("-- [FittingRequest.store]> FittingRequest data updated successfully.");
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public FittingRequest setCorporationId( final int corporationId ) {
		this.corporationId = corporationId;
		return this;
	}

	public FittingRequest setTargetFitting( final int targetFitting ) {
		this.targetFitting = targetFitting;
		// TODO Need to calculate the estimated cost for the fitting.
		return this;
	}

	public FittingRequest setCopies( final int copies ) {
		this.copies = copies;
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("FittingRequest [")
				.append("field:").append(id).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
