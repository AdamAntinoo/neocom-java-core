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
import org.dimensinfin.evedroid.interfaces.IAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends NeoComAsset implements IAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger			= Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
	//	private final IAsset			delegate		= null;
	private final NeoComAsset	reference		= null;
	private long							pilotID			= 0;
	private final Separator		highModules	= new Separator("HIGH");
	private final Separator		medModules	= new Separator("MED");
	private final Separator		lowModules	= new Separator("LOW");
	private final Separator		rigs				= new Separator("RIGS");
	private final Separator		drones			= new Separator("DRONES");
	private final Separator		cargo				= new Separator("CARGO HOLD");

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship(final long pilot) {
		pilotID = pilot;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration of the ship is different form the one of an asset. I will generate some groups to store
	 * under them the different modules fitted and the cargo contents. <br>
	 * The ship should access the database to get its contents.
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
				if (node.getCategory().equalsIgnoreCase("Drones")) {
					drones.addChild(node);
				} else {
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

	/**
	 * Even this object inherits from the asset structure, it is a new instance of the object and we should copy
	 * the data from the original reference to this instance instead using delegates that will not work when
	 * accessing directly to fields.
	 * 
	 * @return this same instance updated with the reference data.
	 */
	public Ship copyFrom(final NeoComAsset asset) {
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
	//
	//	@Override
	//	public long getAssetID() {
	//		return delegate.getAssetID();
	//	}
	//
	//	@Override
	//	public double getIskvalue() {
	//		return delegate.getIskvalue();
	//	}
	//
	//	@Override
	//	public long getLocationID() {
	//		return delegate.getLocationID();
	//	}
	//
	//	@Override
	//	public String getOrderingName() {
	//		return delegate.getOrderingName();
	//	}
	//
	//	@Override
	//	public NeoComAsset getParentContainer() {
	//		return delegate.getParentContainer();
	//	}
	//
	//	@Override
	//	public long getParentContainerId() {
	//		return delegate.getParentContainerId();
	//	}
	//
	//	@Override
	//	public boolean hasParent() {
	//		return delegate.hasParent();
	//	}
	//
	//	@Override
	//	public boolean isContainer() {
	//		return delegate.isContainer();
	//	}
	//
	//	@Override
	//	public boolean isPackaged() {
	//		return delegate.isPackaged();
	//	}
	//
	//	@Override
	//	public boolean isShip() {
	//		return delegate.isShip();
	//	}
}

// - UNUSED CODE ............................................................................................
