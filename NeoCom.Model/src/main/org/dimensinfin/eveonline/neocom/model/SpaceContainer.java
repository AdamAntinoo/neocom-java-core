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
import java.util.Vector;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;

// - CLASS IMPLEMENTATION ...................................................................................
public class SpaceContainer extends NeoComAsset implements IAssetContainer {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger					logger						= Logger.getLogger("org.dimensinfin.evedroid.model");
	private static final long		serialVersionUID	= 2813029093080549286L;

	// - F I E L D - S E C T I O N ............................................................................
	public Vector<NeoComAsset>	_contents					= new Vector<NeoComAsset>();

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
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		this.clean();
		// Classify the contents
		for (NeoComAsset node : _contents) {
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
		return _contents;
	}

	private void downloadContainerData() {
		_contents = (Vector<NeoComAsset>) ModelAppConnector.getSingleton().getDBConnector()
				.searchAssetContainedAt(this.getOwnerID(), this.getAssetID());
		this.setDownloaded(true);
	}
}

// - UNUSED CODE ............................................................................................
