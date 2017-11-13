//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.model;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "UserLabels")
public class UserLabel implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -2662145568311324498L;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	private long							assetID;
	@DatabaseField
	private String						userLabel					= "<NO NAME>";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public UserLabel() {
	}

	public UserLabel(final long id, final String label) {
		assetID = id;
		userLabel = label;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getAssetID() {
		return assetID;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public void setAssetID(final long assetID) {
		this.assetID = assetID;
	}

	public void setUserLabel(final String userLabel) {
		this.userLabel = userLabel;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("UserLabel [");
		buffer.append(assetID).append(" - ").append(userLabel);
		buffer.append(" ]");
		return buffer.toString();
	}

}

// - UNUSED CODE ............................................................................................
