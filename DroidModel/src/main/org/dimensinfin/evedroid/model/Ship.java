//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.j256.ormlite.field.DatabaseField;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends Asset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
private Asset reference = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship() {
	}

	public Ship(Asset asset) {
		reference = asset;
	}


	// - M E T H O D - S E C T I O N ..........................................................................
/**
 * Even this object inherits from the asset structure, it is a new instance of the object and we should copy the data from
 * the original reference to this instance instead using delegates that will not work when accessing directly to fields.
 * @return this same instance updated with the reference data.
 */
	public Asset copyAssetFields() {
				this.id=reference.id;
			this.assetID=reference.assetID;
			this.locationID	=reference.locationID;
			this.typeID=reference.typeID;
			this.quantity	=reference.quantity;
			this.flag=reference.flag;
			this.singleton=reference.singleton;
			this.parentAssetID=reference.parentAssetID;

				//- D E R I V E D   F I E L D S
			this.ownerID=reference.ownerID;
			this.name	=reference.name;
			this.category	=reference.category;
			this.groupName=reference.groupName;
			this.tech=reference.tech;
			this.blueprintFlag=reference.blueprintFlag;
			this.userLabel=reference.userLabel;
			this.shipFlag=reference.shipFlag;
			this.containerFlag=reference.containerFlag;

				return this;
	}
}

// - UNUSED CODE ............................................................................................
