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

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.AssetGroup.EGroupType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends NeoComAsset implements IAssetContainer, IDownloadable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("Ship");
	private static final long serialVersionUID = 1782782104428714849L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean _expanded = false;
	private boolean _renderIfEmpty = true;
	private boolean _downloading = false;
	private boolean _downloaded = false;
	private long _credentialIdentifier = 0;
	private final AssetGroup highModules = new AssetGroup("HIGH").setGroupType(EGroupType.SHIPSECTION_HIGH);
	private final AssetGroup medModules = new AssetGroup("MED").setGroupType(EGroupType.SHIPSECTION_MED);
	private final AssetGroup lowModules = new AssetGroup("LOW").setGroupType(EGroupType.SHIPSECTION_LOW);
	private final AssetGroup rigs = new AssetGroup("RIGS").setGroupType(EGroupType.SHIPSECTION_RIGS);
	private final AssetGroup drones = new AssetGroup("DRONES").setGroupType(EGroupType.SHIPSECTION_DRONES);
	private final AssetGroup cargo = new AssetGroup("CARGO HOLD").setGroupType(EGroupType.SHIPSECTION_CARGO);
	private final AssetGroup orecargo = new AssetGroup("ORE HOLD").setGroupType(EGroupType.SHIPSECTION_CARGO);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	@Deprecated
	public Ship () {
		super();
		// Ships have contents and are not available upon creation.
		this.setDownloaded(false);
		jsonClass = "Ship";
	}

	/**
	 * Get the Pilot when the ship is created to be able to search for its contents. Check if this value matches
	 * the owner ID.
	 */
	public Ship (final long identifier) {
		this();
		_credentialIdentifier = identifier;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	/**
	//	 * By default the contents are added to the Cargo Hold of the Ship.
	//	 */
	//	public int addContent(final NeoComAsset asset) {
	//		cargo.add(asset);
	//		return cargo.size();
	//	}

	public int addAsset (final NeoComAsset asset) {
		cargo.addAsset(asset);
		return cargo.getContentSize();
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
	public List<ICollaboration> collaborate2Model (final String variant) {
		ArrayList<ICollaboration> result = new ArrayList<ICollaboration>();
		if ( !this.isDownloaded() ) {
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
	public Ship copyFrom (final NeoComAsset asset) {
		// REFACTOR Get access to the unique asset identifier.
		this.setAssetId(asset.getAssetId());
		this.setLocationId(asset.getLocationId());
		this.setTypeId(asset.getTypeId());
		this.setQuantity(asset.getQuantity());
		this.setSingleton(asset.isPackaged());

		//- D E R I V E D   F I E L D S
		this.setOwnerID(asset.getOwnerID());
		this.setName(asset.getName());
		this.setCategory(asset.getCategory());
		this.setGroupName(asset.getGroupName());
		this.setTech(asset.getTech());
		this.setUserLabel(asset.getUserLabel());
		this.setShip(asset.isShip());
		this.setContainer(asset.isContainer());
		return this;
	}

	public List<NeoComAsset> getCargo () {
		return cargo.getAssets();
	}

	public int getContentSize () {
		return highModules.getContentSize() + medModules.getContentSize() + lowModules.getContentSize()
				+ rigs.getContentSize() + drones.getContentSize() + cargo.getContentSize() + orecargo.getContentSize();
	}

	//	public List<NeoComAsset> getContents() {
	//		return cargo.getAssets();
	//	}

	public ArrayList<NeoComAsset> getDrones () {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (ICollaboration node : drones.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	/**
	 * Returns the list of modules to be copied to the fitting.
	 */
	public ArrayList<NeoComAsset> getModules () {
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

	public ArrayList<NeoComAsset> getRigs () {
		ArrayList<NeoComAsset> result = new ArrayList<NeoComAsset>();
		for (ICollaboration node : rigs.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}

	public boolean isExpandable () {
		return true;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("Ship [");
		buffer.append("#").append(this.getTypeId()).append(" - ").append(this.getName()).append(" ");
		if ( null != this.getUserLabel() ) {
			buffer.append("[").append(this.getUserLabel()).append("] ");
		}
		buffer.append("itemID:").append(this.getAssetId()).append(" ");
		//		buffer.append("typeID:")..append(" ");
		buffer.append("locationID:").append(this.getLocationId()).append(" ");
		buffer.append("ownerID:").append(this.getOwnerID()).append(" ");
		//	buffer.append("quantity:").append(this.getQuantity()).append(" ");
		buffer.append("]\n");
		return buffer.toString();
		//		return super.toString();
	}

	private void downloadShipData () {
		ArrayList<NeoComAsset> contents = (ArrayList<NeoComAsset>) ModelAppConnector.getSingleton().getDBConnector()
		                                                                            .searchAssetContainedAt
				                                                                            (_credentialIdentifier, this.getAssetId());
		highModules.clean();
		medModules.clean();
		lowModules.clean();
		rigs.clean();
		drones.clean();
		cargo.clean();
		// Classify the contents
		for (NeoComAsset node : contents) {
			// TODO New ESI location also have a location flag that manages this information
			final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag = node.getFlag();
	//		int flag = node.getFlag();
//			if ( (flag > 10)
//					&& (flag < 19) ) {
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT0) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT1) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT2) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT3) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT4) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT5) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT6) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT7) highModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT0) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT1) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT2) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT3) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT4) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT5) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT6) medModules.addAsset(node);
			if(locationFlag== GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT7) medModules.addAsset(node);
//			} else if ( (flag > 18) && (flag < 27) ) {
//				medModules.addAsset(node);
//			} else if ( (flag > 26) && (flag < 35) ) {
//				lowModules.addAsset(node);
//			} else if ( (flag > 91) && (flag < 100) ) {
//				rigs.addAsset(node);
//			} else {
//				// Check for drones
//				if ( node.getCategory().equalsIgnoreCase("Drones") ) {
//					drones.addAsset(node);
//				} else {
//					// Contents on ships go to the cargohold.
//					cargo.addAsset(node);
//				}
//			}
		}
		this.setDownloaded(true);
	}

	public boolean collapse () {
		_expanded = false;
		return _expanded;
	}

	public boolean expand () {
		_expanded = true;
		return _expanded;
	}

	public boolean isEmpty () {
		if ( this.getContentSize() > 0 )
			return true;
		else
			return false;
	}

	public boolean isExpanded () {
		return _expanded;
	}

	public boolean isRenderWhenEmpty () {
		return _renderIfEmpty;
	}

	public IExpandable setRenderWhenEmpty (final boolean renderWhenEmpty) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

	public boolean isDownloaded () {
		return _downloaded;
	}

	public IDownloadable setDownloaded (final boolean downloadedstate) {
		_downloaded = downloadedstate;
		return this;
	}
	public IDownloadable setDownloading (final boolean downloading) {
		this._downloading = downloading;
		return this;
	}


	public boolean isDownloading () {
		return _downloading;
	}


	public List<NeoComAsset> getAssets () {
		return cargo.getAssets();
	}
}

// - UNUSED CODE ............................................................................................
