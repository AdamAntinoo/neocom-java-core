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

import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

import org.joda.time.Instant;

import java.sql.SQLException;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "TimeStamps")
public class TimeStamp extends ANeoComEntity{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("TimeStamp");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	private String reference = "-REF-";
	@DatabaseField(index = true)
	private long credentialId = -1;
	@DatabaseField
	private long timeStamp = -1;
	@DatabaseField
	private String dateTimeUserReference = "-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private TimeStamp () {
	}

	public TimeStamp (final String reference, final Instant instant) {
		this.reference = reference;
		timeStamp = instant.getMillis();
		dateTimeUserReference = instant.toString();
		try {
			Dao<TimeStamp, String> timeStampDao = accessGlobal().getNeocomDBHelper().getTimeStampDao();
			// Try to create the pair. It fails then  it was already created.
			timeStampDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			TimeStamp.logger.info("WR [TimeStamp.<constructor>]> Timestamp exists. Update values.");
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getReference () {
		return reference;
	}

	public long getTimeStamp () {
		return timeStamp;
	}

	public String getDateTimeUserReference () {
		return dateTimeUserReference;
	}

	public long getCredentialId () {
		return credentialId;
	}

	private TimeStamp setReference (final String reference) {
		this.reference = reference;
		return this;
	}

	public TimeStamp setCredentialId (final long credentialId) {
		this.credentialId = credentialId;
		return this;
	}

	public TimeStamp setTimeStamp (final long timeStamp) {
		this.timeStamp = timeStamp;
		dateTimeUserReference = new Instant(timeStamp).toString();
		return this;
	}

	public TimeStamp setTimeStamp (final Instant moment) {
		this.timeStamp = moment.getMillis();
		dateTimeUserReference = moment.toString();
		return this;
	}

	public TimeStamp store () {
		try {
			Dao<TimeStamp, String> timeStampDao = accessGlobal().getNeocomDBHelper().getTimeStampDao();
			if ( -1 == credentialId )
				TimeStamp.logger.info("W [TimeStamp.store]> CredentialId has not been setup. Possible invalid TS.");
			timeStampDao.createOrUpdate(this);
			TimeStamp.logger.info("-- [TimeStamp.store]> Timestamp data updated successfully.");
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("TimeStamp [");
		buffer.append(reference).append("-").append(dateTimeUserReference);
		buffer.append("]");
		return buffer.toString();
	}

	public void updateTimeStamp (final Instant instant) {
		timeStamp = instant.getMillis();
		dateTimeUserReference = instant.toString();
		this.store();
	}
}

// - UNUSED CODE ............................................................................................
