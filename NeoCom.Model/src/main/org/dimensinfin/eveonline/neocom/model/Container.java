//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;

// - CLASS IMPLEMENTATION ...................................................................................
public class Container extends NeoComAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger					logger						= Logger.getLogger("org.dimensinfin.evedroid.model");
	private static final long			serialVersionUID	= 2813029093080549286L;

	// - F I E L D - S E C T I O N ............................................................................
	public ArrayList<NeoComAsset>	contents					= new ArrayList<NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Container() {
	}
	//	public Container(final long pilot) {
	//		pilotID = pilot;
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration of the container is different form the one of an asset. It will aggregate to the output
	 * the list of the contents. <br>
	 * The Container can access the database to get its contents.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		contents = AppConnector.getDBConnector().searchAssetContainedAt(this.getOwnerID(), this.getAssetID());
		this.clean();
		// Classify the contents
		for (NeoComAsset node : contents) {
			result.add(node);
		}
		return result;
	}

	/**
	 * Even this object inherits from the asset structure, it is a new instance of the object and we should copy
	 * the data from the original reference to this instance instead using delegates that will not work when
	 * accessing directly to fields.
	 * 
	 * @return this same instance updated with the reference data.
	 */
	public Container copyFrom(final NeoComAsset asset) {
		// REFACTOR Get access to the unique asset identifier.
		this.setAssetID(asset.getAssetID());
		this.setLocationID(asset.getLocationID());
		this.setTypeID(asset.getTypeID());
		this.setQuantity(asset.getQuantity());
		//	this.flag = reference.flag;
		this.setSingleton(asset.isPackaged());
		// REFACTOR Get access to the unique asset identifier.
		//		this.parentAssetID = reference.parentAssetID;

		//- D E R I V E D   F I E L D S
		this.setOwnerID(asset.getOwnerID());
		this.setName(asset.getName());
		this.setCategory(asset.getCategory());
		this.setGroupName(asset.getGroupName());
		this.setTech(asset.getTech());
		//		this.blueprintFlag = reference.blueprintFlag;
		this.setUserLabel(asset.getUserLabel());
		this.setShip(asset.isShip());
		this.setContainer(asset.isContainer());
		return this;
	}

	public ArrayList<NeoComAsset> getContents() {
		return contents;
	}
}

// - UNUSED CODE ............................................................................................
