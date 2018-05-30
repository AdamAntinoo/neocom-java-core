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

import org.dimensinfin.eveonline.neocom.enums.EPropertyTypes;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "properties")
public class Property extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 1209487969346789159L;
	public static final int LOCATION_ROLE_PROPERTY = 10;
	public static final int TASK_ACTION_PROPERTY = 20;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(index = true, generatedIdSequence="properties_id_seq")
	private long id = -2;
	@DatabaseField
	private String propertyType = EPropertyTypes.UNDEFINED.name();
	@DatabaseField
	private String stringValue = "";
	@DatabaseField
	private double numericValue = 0.0;
	@DatabaseField
	private long targetId = -6;
	@DatabaseField
	private long ownerId = -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Property() {
	}

	public Property( final EPropertyTypes propertyType ) {
		// Be sure the owner is reset to undefined when stored at the database.
		this.resetOwner();
		this.setPropertyType(propertyType);
		try {
			Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
			// Try to create the pair. It fails then  it was already created.
			propertyDao.create(this);
		} catch (final SQLException sqle) {
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Property store() {
		try {
			Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
			propertyDao.update(this);
		} catch (final SQLException sqle) {
		}
		return this;
	}

	public long getId() {
		return id;
	}

	public double getNumericValue() {
		return numericValue;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public EPropertyTypes getPropertyType() {
		return EPropertyTypes.valueOf(propertyType);
	}

	public String getPropertyValue() {
		return stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public long getTargetId() {
		return targetId;
	}

	public Property setTargetId( final long targetId ) {
		this.targetId = targetId;
		return this;
	}

	public void resetOwner() {
		ownerId = -1;
	}

	public Property setNumericValue( final double numericValue ) {
		this.numericValue = numericValue;
		return this;
	}

	public Property setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
		return this;
	}

	public Property setPropertyType( final EPropertyTypes propertyType ) {
		this.propertyType = propertyType.toString();
		return this;
	}

	public Property setStringValue( final String stringValue ) {
		this.stringValue = stringValue;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Property [");
		buffer.append(stringValue).append(" [").append(numericValue).append("] ");
		buffer.append("Type:").append(propertyType).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
