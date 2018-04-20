//  PROJECT:        DroidModel
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.enums.EPropertyTypes;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Properties")
public class Property extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 1209487969346789159L;
	public static final int LOCATION_ROLE_PROPERTY = 10;
	public static final int TASK_ACTION_PROPERTY = 20;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true, index = true)
	private final long id = -2;
	@DatabaseField
	private String propertyType = EPropertyTypes.UNDEFINED.name();
	@DatabaseField
	private String stringValue = "";
	@DatabaseField
	private double numericValue = 0.0;
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
		} catch (NeoComException neoe) {
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
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

	public void resetOwner() {
		ownerId = -1;
	}

	public Property store() {
		try {
			Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
			propertyDao.update(this);
		} catch (final SQLException sqle) {
		} catch (NeoComException neoe) {
		}
		return this;
	}

	public Property setNumericValue( final double numericValue ) {
		this.numericValue = numericValue;
		return this;
//		this.setDirty(true);
	}

	public Property setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
		return this;
//		this.setDirty(true);
	}

	public Property setPropertyType( final EPropertyTypes propertyType ) {
		this.propertyType = propertyType.toString();
		return this;
//		this.setDirty(true);
	}

	public Property setStringValue( final String stringValue ) {
		this.stringValue = stringValue;
		return this;
//		this.setDirty(true);
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
