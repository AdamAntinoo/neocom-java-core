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

import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public class SpaceContainer extends NeoComAsset implements IAssetContainer, IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= 2813029093080549286L;

	// - F I E L D - S E C T I O N ............................................................................
	private final Vector<NeoComAsset>	_contents					= new Vector<NeoComAsset>();
	private double										totalVolume				= 0.0;
	private double										totalValue				= 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SpaceContainer() {
		super();
		this.setDownloaded(false);
		jsonClass = "SpaceContainer";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public int addContent(final NeoComAsset asset) {
		_contents.add(asset);
		return _contents.size();
	}

	/**
	 * The collaboration of the container is different form the one of an asset. It will aggregate to the output
	 * the list of the contents. <br>
	 * The Container can access the database to get its contents.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		List<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		// If the contents are already downloaded chain the collaboration calls.
		if (this.isDownloaded()) {
			results.addAll(_contents);
		} else {
			// Download the assets only if the state is expended.
			//	if (this.isExpanded()) {
			results.addAll(this.getContents());
			//		}
		}
		return (ArrayList<AbstractComplexNode>) results;
	}

	/**
	 * Even this object inherits from the asset structure, it is a new instance of the object and we should copy
	 * the data from the original reference to this instance instead using delegates that will not work when
	 * accessing directly to fields.
	 * 
	 * @return this same instance updated with the reference data.
	 */
	public SpaceContainer copyFrom(final NeoComAsset asset) {
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

	@Override
	public List<NeoComAsset> getContents() {
		if (!this.isDownloaded()) {
			// Get the assets from the database.
			_contents.clear();
			_contents.addAll(this.processDownloadedAssets(ModelAppConnector.getSingleton().getDBConnector()
					.searchAssetContainedAt(this.getOwnerID(), this.getAssetID())));
			this.setDownloaded(true);
		}
		return _contents;
	}

	public int getContentsSize() {
		return _contents.size();
	}

	public double getTotalValue() {
		return totalValue;
	}

	public double getTotalVolume() {
		return totalVolume;
	}

	@Override
	public boolean isEmpty() {
		return (_contents.size() < 1) ? true : false;
	}

	@Override
	public boolean isExpandable() {
		return true;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("SpaceContainer [");
		buffer.append(this.getName()).append(" [");
		buffer.append(super.toString());
		buffer.append("]\n");
		return buffer.toString();
	}

	/**
	 * Process the assets being downloaded and convert to their new types. Warning!!. This methods need to know
	 * the id of the current pilot but has no connection to the assets manager or the Character itself so I am
	 * resorting to use the default Character stored at the AppModel.
	 * 
	 * @param input
	 * @return
	 */
	private List<NeoComAsset> processDownloadedAssets(final List<NeoComAsset> input) {
		ArrayList<NeoComAsset> results = new ArrayList<NeoComAsset>();
		for (NeoComAsset asset : input) {
			if (asset.isContainer()) {
				// Check if the asset is packaged. If so leave as asset
				if (!asset.isPackaged()) {
					// Transform the asset to a ship.
					SpaceContainer container = new SpaceContainer().copyFrom(asset);
					results.add(container);
					continue;
				}
			}
			if (asset.isShip()) {
				// Check if the ship is packaged. If packaged leave it as a simple asset.
				if (!asset.isPackaged()) {
					// Transform the asset to a ship.
					Ship ship = new Ship(ModelAppConnector.getSingleton().getModelStore().getActiveCharacter().getCharacterID())
							.copyFrom(asset);
					// Calculate value and volume to register on the aggregation.
					totalValue = +asset.getPrice();
					//			totalVolume=+asset.getItem().getVolume()*asset.getQuantity();
					results.add(ship);
					continue;
				}
			}
			// Calculate value and volume to register on the aggregation.
			totalValue = +asset.getPrice() * asset.getQuantity();
			totalVolume = +asset.getItem().getVolume() * asset.getQuantity();
			results.add(asset);
		}
		return results;
	}

	//	private void downloadContainerData() {
	//		_contents = (Vector<NeoComAsset>) ModelAppConnector.getSingleton().getDBConnector()
	//				.searchAssetContainedAt(this.getOwnerID(), this.getAssetID());
	//		this.setDownloaded(true);
	//	}
}

// - UNUSED CODE ............................................................................................
