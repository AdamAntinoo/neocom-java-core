//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.interfaces.IAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends AbstractNeoComNode implements IAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger			= Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
	private IAsset					delegate		= null;
	private NeoComAsset						reference		= null;
	private long						pilotID			= 0;
	private final Separator	highModules	= new Separator("HIGH");
	private final Separator	medModules	= new Separator("MED");
	private final Separator	lowModules	= new Separator("LOW");
	private final Separator	rigs				= new Separator("RIGS");
	private final Separator	drones			= new Separator("DRONES");
	private final Separator	cargo				= new Separator("CARGO HOLD");

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship(long pilot) {
		pilotID = pilot;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration of the ship is different form the one of an asset. I will generate some groups to store
	 * under them the different modules fitted and the cargo contents. <br>
	 * The ship can access the database to get its contents
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		ArrayList<NeoComAsset> contents = AppConnector.getDBConnector().searchAssetContainedAt(pilotID, this.getAssetID());
		// Classify the contents
		for (NeoComAsset node : contents) {
			int flag = node.getFlag();
			if ((flag > 10) && (flag < 19)) {
				highModules.addChild(node);
			} else if ((flag > 18) && (flag < 27)) {
				medModules.addChild(node);
			} else if ((flag > 26) && (flag < 35)) {
				lowModules.addChild(node);
			} else if ((flag > 91) && (flag < 100)) {
				rigs.addChild(node);
			} else {
				// Check for drones
				if (node.getCategory().equalsIgnoreCase("Drones"))
					drones.addChild(node);
				else {
					// Contents on ships go to the cargohold.
					cargo.addChild(node);
				}
			}
		}
		result.add(highModules);
		result.add(medModules);
		result.add(lowModules);
		result.add(rigs);
		result.add(drones);
		result.add(cargo);
		return result;
	}

	//	/**
	//	 * Even this object inherits from the asset structure, it is a new instance of the object and we should copy
	//	 * the data from the original reference to this instance instead using delegates that will not work when
	//	 * accessing directly to fields.
	//	 * 
	//	 * @return this same instance updated with the reference data.
	//	 */
	//	public Asset copyAssetFields() {
	//		this.id = reference.id;
	//		this.assetID = reference.assetID;
	//		this.locationID = reference.locationID;
	//		this.typeID = reference.typeID;
	//		this.quantity = reference.quantity;
	//		this.flag = reference.flag;
	//		this.singleton = reference.singleton;
	//		this.parentAssetID = reference.parentAssetID;
	//
	//		//- D E R I V E D   F I E L D S
	//		this.ownerID = reference.ownerID;
	//		this.name = reference.name;
	//		this.category = reference.category;
	//		this.groupName = reference.groupName;
	//		this.tech = reference.tech;
	//		this.blueprintFlag = reference.blueprintFlag;
	//		this.userLabel = reference.userLabel;
	//		this.shipFlag = reference.shipFlag;
	//		this.containerFlag = reference.containerFlag;
	//
	//		return this;
	//	}

	public Ship copyFrom(final IAsset asset) {
		// Install the original asset in this instance as the delegate.
		delegate = asset;
		return this;
	}

	public long getAssetID() {
		return delegate.getAssetID();
	}

	public long getLocationID() {
		return delegate.getLocationID();
	}

	public String getOrderingName() {
		return delegate.getOrderingName();
	}

	public NeoComAsset getParentContainer() {
		return delegate.getParentContainer();
	}

	public long getParentContainerId() {
		return delegate.getParentContainerId();
	}

	public boolean hasParent() {
		return delegate.hasParent();
	}

	public boolean isContainer() {
		return delegate.isContainer();
	}

	public boolean isPackaged() {
		return delegate.isPackaged();
	}

	public boolean isShip() {
		return delegate.isShip();
	}
}

// - UNUSED CODE ............................................................................................
