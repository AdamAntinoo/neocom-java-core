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

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetGroup extends AbstractViewableNode {
	/** Use the type to associate it with an icon. */
	public enum EGroupType {
		DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= 8066964529677353362L;
	//	private static Logger				logger						= Logger.getLogger("GroupAggregation");

	// - F I E L D - S E C T I O N ............................................................................
	public final Vector<NeoComAsset>	_contents					= new Vector<NeoComAsset>();
	public EGroupType									_type							= EGroupType.DEFAULT;
	public String											_title						= "-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetGroup() {
		super();
		jsonClass = "AssetGroup";
	}

	public AssetGroup(final String title) {
		super();
		_title = title;
	}

	public AssetGroup(final String title, final EGroupType type) {
		super();
		_title = title;
		_type = type;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int addContent(final NeoComAsset asset) {
		_contents.add(asset);
		return _contents.size();
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		if (this.isVisible()) if (this.isExpanded()) {
			results.addAll(this.getContents());
		}
		return results;
	}

	public Vector<NeoComAsset> getContents() {
		return _contents;
	}

	public String getTitle() {
		return _title;
	}

	public EGroupType getType() {
		return _type;
	}

	public void setTitle(final String title) {
		_title = title;
	}

	public AssetGroup setType(final EGroupType type) {
		_type = type;
		return this;
	}

	public int size() {
		return _contents.size();
	}
}

// - UNUSED CODE ............................................................................................
