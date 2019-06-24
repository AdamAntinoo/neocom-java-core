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
import org.dimensinfin.core.model.Container;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetGroup extends Container implements IAssetContainer {
	/** Use the type to associate it with an icon. */
	public enum EGroupType {
		DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 8066964529677353362L;

	// - F I E L D - S E C T I O N ............................................................................
	private double totalValue = 0.0;
	private double totalVolume = 0.0;

	//	public final Vector<NeoComAsset>	_contents					= new Vector<NeoComAsset>();
	//	private boolean										_expanded					= false;
	//	private boolean										_renderIfEmpty		= true;
	//	private final boolean							_downloaded				= false;
	public EGroupType groupType = EGroupType.DEFAULT;

	//	public String											title							= "-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetGroup() {
		super();
		//		this.setDownloaded(true);
		this.expand();
		jsonClass = "AssetGroup";
	}

	public AssetGroup( final String newtitle ) {
		super(newtitle);
		this.expand();
		jsonClass = "AssetGroup";
	}

	public AssetGroup( final String newtitle, final EGroupType newtype ) {
		super(newtitle);
		this.expand();
		jsonClass = "AssetGroup";
		groupType = newtype;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	@Override
	//	public int addContent(final ICollaboration asset) {
	//		super.addContent(asset);
	//		return this.getContentSize();
	//	}

	public int addAsset( final NeoComAsset asset ) {
		super.addContent(asset);
		try {
			totalValue += asset.getQuantity() * asset.getItem().getHighestBuyerPrice().getPrice();
		} catch (ExecutionException ee) {
			totalValue += asset.getQuantity() * asset.getItem().getPrice();
		} catch (InterruptedException ie) {
			totalValue += asset.getQuantity() * asset.getItem().getPrice();
		}
		totalVolume += asset.getQuantity() * asset.getItem().getVolume();
		return this.getContentSize();
	}

	public List<NeoComAsset> getAssets() {
		Vector<NeoComAsset> result = new Vector<NeoComAsset>();
		for (ICollaboration node : super.getContents()) {
			result.add((NeoComAsset) node);
		}
		return result;
	}
	@Override
	public double getTotalValue() {
		return this.totalValue;
	}

	@Override
	public double getTotalVolume() {
		return this.totalVolume;
	}

	public EGroupType getGroupType() {
		return groupType;
	}

	//	public void setName(final String newtitle) {
	//		title = newtitle;
	//	}

	public AssetGroup setGroupType( final EGroupType newtype ) {
		groupType = newtype;
		return this;
	}

	//	public int size() {
	//		return _contents.size();
	//	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AssetGroup [");
		buffer.append(this.getTitle()).append(" type: ").append(groupType.name());
		buffer.append(" [").append(this.getContentSize()).append("]");
		buffer.append("]");
		return buffer.toString();
	}
	//	public boolean collapse() {
	//		_expanded = false;
	//		return _expanded;
	//	}
	//
	//	public boolean expand() {
	//		_expanded = true;
	//		return _expanded;
	//	}
	//
	//	//	public boolean isDownloaded() {
	//	//		return _downloaded;
	//	//	}
	//
	//	public boolean isEmpty() {
	//		return (_contents.size() < 1) ? true : false;
	//	}
	//
	//	@Deprecated
	//	public boolean isExpandable() {
	//		return true;
	//	}
	//
	//	public boolean isExpanded() {
	//		return _expanded;
	//	}
	//
	//	public boolean isRenderWhenEmpty() {
	//		return _renderIfEmpty;
	//	}
	//
	//	//	public IDownloadable setDownloaded(final boolean downloadedstate) {
	//	//		_downloaded = downloadedstate;
	//	//		return this;
	//	//	}
	//
	//	public IExpandable setRenderWhenEmpty(final boolean renderWhenEmpty) {
	//		_renderIfEmpty = renderWhenEmpty;
	//		return this;
	//	}
}

// - UNUSED CODE ............................................................................................
