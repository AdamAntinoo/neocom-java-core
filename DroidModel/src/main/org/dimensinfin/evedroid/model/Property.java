//  PROJECT:        DroidModel
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.model;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;
import java.sql.SQLException;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.enums.EPropertyTypes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Properties")
public class Property implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static final int	LOCATION_ROLE_PROPERTY	= 10;
	public static final int	TASK_ACTION_PROPERTY		= 20;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(generatedId = true, index = true)
	private long						id											= -2;
	@DatabaseField
	private String					propertyType						= EPropertyTypes.UNDEFINED.toString();
	@DatabaseField
	private String					stringValue							= "";
	@DatabaseField
	private double					numericValue						= 0.0;
	@DatabaseField
	private long						ownerID									= -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Property() {
	}

	public Property(EPropertyTypes propertyType) {
		try {
			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
			// Try to create the pair. It fails then  it was already created.
			propertyDao.create(this);
			// Be sure the owner is reset to undefined when stored at the database.
			resetOwner();
			setPropertyType(propertyType);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			setDirty(true);
		}
	}

	//	public Property(final int identifier) {
	//		id = identifier;
	//		try {
	//			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
	//			propertyDao.create(this);
	//			// Be sure the owner is reset to undefined when stored at the database.
	//			resetOwner();
	//		} catch (final SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//	}
	//	public Property(final EPropertyTypes type, final String label) {
	//		//		id = identifier;
	//		propertyType = type.toString();
	//		stringValue = label;
	//		try {
	//			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
	//			// Try to create the pair. It fails then  it was already created.
	//			propertyDao.createOrUpdate(this);
	//			// Be sure the owner is reset to undefined when stored at the database.
	//			resetOwner();
	//			setDirty(true);
	//			//		logger.finest("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
	//		} catch (final SQLException sqle) {
	//			//		logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
	//			sqle.printStackTrace();
	//			setDirty(true);
	//		}
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getId() {
		return id;
	}

	public double getNumericValue() {
		return numericValue;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public EPropertyTypes getPropertyType() {
		return EPropertyTypes.decode(propertyType);
	}

	public String getStringValue() {
		return stringValue;
	}

	public void resetOwner() {
		ownerID = -1;
	}

	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
				propertyDao.update(this);
				//		logger.finest("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
			} catch (final SQLException sqle) {
				//		logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}

	public void setNumericValue(final double numericValue) {
		this.numericValue = numericValue;
		setDirty(true);
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
		setDirty(true);
	}

	public void setPropertyType(final EPropertyTypes propertyType) {
		this.propertyType = propertyType.toString();
		setDirty(true);
	}

	public void setStringValue(final String stringValue) {
		this.stringValue = stringValue;
		setDirty(true);
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
