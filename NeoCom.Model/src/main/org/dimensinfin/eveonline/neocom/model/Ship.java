//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.AssetGroup.EGroupType;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends NeoComAsset implements IAssetContainer {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger						= Logger.getLogger("Ship");
	private static final long	serialVersionUID	= 1782782104428714849L;

	// - F I E L D - S E C T I O N ............................................................................
	private long							pilotID						= 0;
	private final AssetGroup	highModules				= new AssetGroup("HIGH").setType(EGroupType.SHIPSECTION_HIGH);
	private final AssetGroup	medModules				= new AssetGroup("MED").setType(EGroupType.SHIPSECTION_MED);
	private final AssetGroup	lowModules				= new AssetGroup("LOW").setType(EGroupType.SHIPSECTION_LOW);
	private final AssetGroup	rigs							= new AssetGroup("RIGS").setType(EGroupType.SHIPSECTION_RIGS);
	private final AssetGroup	drones						= new AssetGroup("DRONES").setType(EGroupType.SHIPSECTION_DRONES);
	private final AssetGroup	cargo							= new AssetGroup("CARGO HOLD").setType(EGroupType.SHIPSECTION_CARGO);
	private final AssetGroup	orecargo					= new AssetGroup("ORE HOLD").setType(EGroupType.SHIPSECTION_CARGO);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship() {
		// Ships have contents and are not available upon creation.
		this.setDownloaded(false);
		jsonClass = "Ship";
	}

	/**
	 * Get the Pilot when the ship is created to be able to search for its contents. Check if this value matches
	 * the owner ID.
	 * 
	 * @param pilot
	 */
	public Ship(final long pilot) {
		jsonClass = "Ship";
		pilotID = pilot;
		this.setDownloaded(false);
	}

	/**
	 * By default the contents are added to the Cargo Hold of the Ship.
	 */
	@Override
	public int addContent(final NeoComAsset asset) {
		cargo.addChild(asset);
		return cargo.size();
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
			this.downloadShipData();
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

	public List<NeoComAsset> getCargo() {
		//		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		//		for (IGEFNode node : cargo.getChildren()) {
		//			result.add((NeoComAsset) node);
		//		}
		return cargo.getContents();
	}

	@Override
	public int getContentCount() {
		return highModules.size() + medModules.size() + lowModules.size() + rigs.size() + drones.size() + cargo.size()
				+ orecargo.size();
	}

	@Override
	public List<NeoComAsset> getContents() {
		return cargo.getContents();
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

	private void downloadShipData() {
		ArrayList<NeoComAsset> contents = (ArrayList<NeoComAsset>) ModelAppConnector.getSingleton().getDBConnector()
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
}

// - UNUSED CODE ............................................................................................
