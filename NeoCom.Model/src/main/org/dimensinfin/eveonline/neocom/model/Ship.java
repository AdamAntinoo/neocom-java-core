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

import org.dimensinfin.android.model.Separator;
import org.dimensinfin.android.model.Separator.ESeparatorType;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends NeoComAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger						= Logger.getLogger("org.dimensinfin.evedroid.model");
	private static final long	serialVersionUID	= 1782782104428714849L;

	// - F I E L D - S E C T I O N ............................................................................
	private long							pilotID						= 0;
	private final Separator		highModules				= new Separator("HIGH").setType(ESeparatorType.SHIPSECTION_HIGH);
	private final Separator		medModules				= new Separator("MED").setType(ESeparatorType.SHIPSECTION_MED);
	private final Separator		lowModules				= new Separator("LOW").setType(ESeparatorType.SHIPSECTION_LOW);
	private final Separator		rigs							= new Separator("RIGS").setType(ESeparatorType.SHIPSECTION_RIGS);
	private final Separator		drones						= new Separator("DRONES").setType(ESeparatorType.SHIPSECTION_DRONES);
	private final Separator		cargo							= new Separator("CARGO HOLD").setType(ESeparatorType.SHIPSECTION_CARGO);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship() {
		jsonClass = "ShipLocation";
	}

	/**
	 * Get the Pilot when the ship is created to be able to search for its contents. Check if this value matches
	 * the owner ID.
	 * 
	 * @param pilot
	 */
	public Ship(final long pilot) {
		jsonClass = "ShipLocation";
		pilotID = pilot;
		this.setDownloaded(false);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration of the ship is different form the one of an asset. I will generate some groups to store
	 * under them the different modules fitted and the cargo contents. <br>
	 * The ship should access the database to get its contents. <br>
	 * This should be done once to avoid the multiple calls to the database as an optimization. The clear of the
	 * fields have removed the bug that caused the same ships to be processed multiple times by different DS.
	 * Use the downloaded flag for this purpose.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		if (!this.isDownloaded()) {
			ArrayList<NeoComAsset> contents = ModelAppConnector.getSingleton().getDBConnector()
					.searchAssetContainedAt(pilotID, this.getAssetID());
			highModules.clean();
			medModules.clean();
			lowModules.clean();
			rigs.clean();
			drones.clean();
			cargo.clean();
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
			this.setDownloaded(true);
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

	public ArrayList<NeoComAsset> getCargo() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (IGEFNode node : cargo.getChildren()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	public ArrayList<NeoComAsset> getDrones() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (IGEFNode node : drones.getChildren()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	/**
	 * Returns the list of modules to be copied to the fitting.
	 * 
	 * @return
	 */
	public ArrayList<NeoComAsset> getModules() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (IGEFNode node : highModules.getChildren()) {
			result.add((NeoComAsset) node);
		}
		for (IGEFNode node : medModules.getChildren()) {
			result.add((NeoComAsset) node);
		}
		for (IGEFNode node : lowModules.getChildren()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	public ArrayList<NeoComAsset> getRigs() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (IGEFNode node : rigs.getChildren()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Ship [");
		buffer.append("#").append(this.getTypeID()).append(" - ").append(this.getName()).append(" ");
		if (null != this.getUserLabel()) {
			buffer.append("[").append(this.getUserLabel()).append("] ");
		}
		buffer.append("itemID:").append(this.getAssetID()).append(" ");
		//		buffer.append("typeID:")..append(" ");
		buffer.append("locationID:").append(this.getLocationID()).append(" ");
		buffer.append("ownerID:").append(this.getOwnerID()).append(" ");
		//	buffer.append("quantity:").append(this.getQuantity()).append(" ");
		buffer.append("]\n");
		return buffer.toString();
		//		return super.toString();
	}
}

// - UNUSED CODE ............................................................................................
