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
package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipPre10 extends NeoComAsset implements IAssetContainer, IExpandable {
	//		public enum EGroupType {
	//			DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
	//		}
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger logger = Logger.getLogger("Ship");
	private static final long serialVersionUID = 1782782104428714849L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean _expanded = false;
	private boolean _renderIfEmpty = true;
	private boolean _downloading = false;
	private boolean _downloaded = false;

	private double totalValue = 0.0;
	private double totalVolume = 0.0;

	private final AssetGroup highModules = new AssetGroup("HIGH").setGroupType(AssetGroup.EGroupType.SHIPSECTION_HIGH);
	private final AssetGroup medModules = new AssetGroup("MED").setGroupType(AssetGroup.EGroupType.SHIPSECTION_MED);
	private final AssetGroup lowModules = new AssetGroup("LOW").setGroupType(AssetGroup.EGroupType.SHIPSECTION_LOW);
	private final AssetGroup rigs = new AssetGroup("RIGS").setGroupType(AssetGroup.EGroupType.SHIPSECTION_RIGS);
	private final AssetGroup drones = new AssetGroup("DRONES").setGroupType(AssetGroup.EGroupType.SHIPSECTION_DRONES);
	private final AssetGroup cargo = new AssetGroup("CARGO HOLD").setGroupType(AssetGroup.EGroupType.SHIPSECTION_CARGO);
	private final AssetGroup orecargo = new AssetGroup("ORE HOLD").setGroupType(AssetGroup.EGroupType.SHIPSECTION_CARGO);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	@Deprecated
	public ShipPre10() {
		super();
		// Ships have contents and are not available upon creation.
		//		this.setDownloaded(false);
//		jsonClass = "Ship";
	}

	//	/**
	//	 * Get the Pilot when the ship is created to be able to search for its contents. Check if this value matches
	//	 * the owner ID.
	//	 */
	//	public ShipPre10 (final long identifier) {
	//		this();
	//		_credentialIdentifier = identifier;
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	/**
	//	 * By default the contents are added to the Cargo Hold of the Ship.
	//	 */
	//	public int addContent(final NeoComAsset asset) {
	//		cargo.add(asset);
	//		return cargo.size();
	//	}

	public int addAsset( final NeoComAsset asset ) {
		cargo.addAsset(asset);
		try {
			totalValue += asset.getQuantity() * asset.getItem().getHighestBuyerPrice().getPrice();
		} catch (ExecutionException ee) {
			totalValue += asset.getQuantity() * asset.getItem().getPrice();
		} catch (InterruptedException ie) {
			totalValue += asset.getQuantity() * asset.getItem().getPrice();
		}
		totalVolume += asset.getQuantity() * asset.getItem().getVolume();
		return cargo.getContentSize();
	}

	@Override
	public double getTotalValue() {
		return this.totalValue;
	}

	@Override
	public double getTotalVolume() {
		return this.totalVolume;
	}

	/**
	 * The collaboration of the ship is different form the one of an asset. I will generate some groups to store
	 * under them the different modules fitted and the cargo contents. <br>
	 * The ship should access the database to get its contents. <br>
	 * This should be done once to avoid the multiple calls to the database as an optimization. The clear of the
	 * fields have removed the bug that caused the same ships to be processed multiple times by different DS.
	 * Use the downloaded flag for this purpose.
	 */
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		ArrayList<ICollaboration> result = new ArrayList<ICollaboration>();
		// TODO With the current code the data is downloaded any time we call the collaborate2Model without waiting for
		// the item to be expanded so the effect is that the contents are evaluated during the first model run and not
		// when the node is expanded.
		// In the version 0.10.9 this meand that the contents are read when the location is expanded.
		//		if (!this.isDownloaded()) {
		//			this.downloadShipData();
		//		}
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
	 * @return this same instance updated with the reference data.
	 */
	public ShipPre10 copyFrom( final NeoComAsset asset ) {
		// REFACTOR Get access to the unique asset identifier.
		this.setAssetId(asset.getAssetId());
		this.setLocationId(asset.getLocationId());
		this.setTypeId(asset.getTypeId());
		this.setQuantity(asset.getQuantity());
		this.setSingleton(asset.isPackaged());

		//- D E R I V E D   F I E L D S
		this.setOwnerId(asset.getOwnerId());
		this.setName(asset.getName());
		this.setCategory(asset.getCategoryName());
		this.setGroupName(asset.getGroupName());
		this.setTech(asset.getTech());
		this.setUserLabel(asset.getUserLabel());
		this.setShip(asset.isShip());
		this.setContainer(asset.isContainer());
		return this;
	}

	public List<NeoComAsset> getCargo() {
		return cargo.getAssets();
	}

	public int getContentSize() {
		return highModules.getContentSize() + medModules.getContentSize() + lowModules.getContentSize()
				+ rigs.getContentSize() + drones.getContentSize() + cargo.getContentSize() + orecargo.getContentSize();
	}

	//	public List<NeoComAsset> getContents() {
	//		return cargo.getAssets();
	//	}

	public ArrayList<NeoComAsset> getDrones() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (ICollaboration node : drones.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	/**
	 * Returns the list of modules to be copied to the fitting.
	 */
	public ArrayList<NeoComAsset> getModules() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (ICollaboration node : highModules.getContents()) {
			result.add((NeoComAsset) node);
		}
		for (ICollaboration node : medModules.getContents()) {
			result.add((NeoComAsset) node);
		}
		for (ICollaboration node : lowModules.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	public ArrayList<NeoComAsset> getRigs() {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (ICollaboration node : rigs.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	public boolean isExpandable() {
		return true;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Ship [");
		buffer.append("#").append(this.getTypeId()).append(" - ").append(this.getName()).append(" ");
		if (null != this.getUserLabel()) {
			buffer.append("[").append(this.getUserLabel()).append("] ");
		}
		buffer.append("itemID:").append(this.getAssetId()).append(" ");
		//		buffer.append("typeID:")..append(" ");
		buffer.append("locationID:").append(this.getLocationId()).append(" ");
		buffer.append("ownerID:").append(this.getOwnerId()).append(" ");
		//	buffer.append("quantity:").append(this.getQuantity()).append(" ");
		buffer.append("]\n");
		return buffer.toString();
		//		return super.toString();
	}

	//	private void downloadShipData () {
	//		ArrayList<NeoComAsset> contents = GlobalDataManager.accessAssetsContainedAt(this.getAssetId());
	//		highModules.clean();
	//		medModules.clean();
	//		lowModules.clean();
	//		rigs.clean();
	//		drones.clean();
	//		cargo.clean();
	//		// Classify the contents
	//		for (NeoComAsset node : contents) {
	//			// TODO New ESI location also have a location flag that manages this information
	//			final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag = node.getFlag();
	//	//		int flag = node.getFlag();
	////			if ( (flag > 10)
	////					&& (flag < 19) ) {
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT0) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT1) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT2) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT3) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT4) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT5) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT6) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT7) highModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT0) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT1) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT2) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT3) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT4) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT5) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT6) medModules.addAsset(node);
	//			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT7) medModules.addAsset(node);
	////			} else if ( (flag > 18) && (flag < 27) ) {
	////				medModules.addAsset(node);
	////			} else if ( (flag > 26) && (flag < 35) ) {
	////				lowModules.addAsset(node);
	////			} else if ( (flag > 91) && (flag < 100) ) {
	////				rigs.addAsset(node);
	////			} else {
	////				// Check for drones
	////				if ( node.getCategory().equalsIgnoreCase("Drones") ) {
	////					drones.addAsset(node);
	////				} else {
	////					// Contents on ships go to the cargohold.
	////					cargo.addAsset(node);
	////				}
	////			}
	//		}
	//		this.setDownloaded(true);
	//	}

	public boolean collapse() {
		_expanded = false;
		return _expanded;
	}

	public boolean expand() {
		_expanded = true;
		return _expanded;
	}

	public boolean isEmpty() {
		if (this.getContentSize() > 0)
			return true;
		else
			return false;
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public boolean isRenderWhenEmpty() {
		return _renderIfEmpty;
	}

	public IExpandable setRenderWhenEmpty( final boolean renderWhenEmpty ) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

	//	public boolean isDownloaded () {
	//		return _downloaded;
	//	}
	//
	//	public IDownloadable setDownloaded (final boolean downloadedstate) {
	//		_downloaded = downloadedstate;
	//		return this;
	//	}
	//	public IDownloadable setDownloading (final boolean downloading) {
	//		this._downloading = downloading;
	//		return this;
	//	}
	//
	//
	//	public boolean isDownloading () {
	//		return _downloading;
	//	}


	public List<NeoComAsset> getAssets() {
		return cargo.getAssets();
	}
}

// - UNUSED CODE ............................................................................................
