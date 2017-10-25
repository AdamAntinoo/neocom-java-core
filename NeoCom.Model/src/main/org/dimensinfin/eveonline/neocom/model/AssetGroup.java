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
import java.util.Vector;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetGroup extends AbstractViewableNode implements IContentManager {
	/** Use the type to associate it with an icon. */
	public enum EGroupType {
		DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= 8066964529677353362L;

	// - F I E L D - S E C T I O N ............................................................................
	public final Vector<NeoComAsset>	contents					= new Vector<NeoComAsset>();
	public EGroupType									type							= EGroupType.DEFAULT;
	public String											title							= "-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetGroup() {
		super();
		this.setDownloaded(true);
		this.setExpanded(true);
		jsonClass = "AssetGroup";
	}

	public AssetGroup(final String newtitle) {
		this();
		title = newtitle;
	}

	public AssetGroup(final String newtitle, final EGroupType newtype) {
		this();
		title = newtitle;
		type = newtype;
	}

	@Override
	public int add(final NeoComAsset asset) {
		contents.add(asset);
		return contents.size();
	}

	@Deprecated
	@Override
	public void addChild(final IGEFNode child) {
		// TODO Auto-generated method stub
		super.addChild(child);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int addContent(final NeoComAsset asset) {
		contents.add(asset);
		return contents.size();
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		if (this.isVisible()) if (this.isExpanded()) {
			results.addAll(this.getContents());
		}
		return results;
	}

	@Override
	public Vector<NeoComAsset> getContents() {
		return contents;
	}

	@Override
	public int getContentSize() {
		return contents.size();
	}

	public String getTitle() {
		return title;
	}

	public EGroupType getType() {
		return type;
	}

	public void setTitle(final String newtitle) {
		title = newtitle;
	}

	public AssetGroup setType(final EGroupType newtype) {
		type = newtype;
		return this;
	}

	public int size() {
		return contents.size();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AssetGroup [");
		buffer.append(title).append(" type: ").append(type.name());
		buffer.append(" [").append(this.getContentSize()).append("]");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
