//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download and parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;

import java.util.List;
import java.util.Vector;

//- CLASS IMPLEMENTATION ...................................................................................
public class AssetCategory extends NeoComExpandableNode implements IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 1250693939026390391L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean _expanded = false;
	private boolean _renderIfEmpty = true;
	private ItemCategory _categoryDelegate = null;
	//	private String _title = "-TITLE-";
	private final Vector<NeoComAsset> _contents = new Vector();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetCategory () {
		super();jsonClass = "AssetCategory";
	}

	public AssetCategory (int categoryid) {
		this();
		_categoryDelegate = GlobalDataManager.searchItemCategory4Id(categoryid);
	}
	//	public AssetCategory (String title) {
	//		this();
	//		_title = title;
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public List<ICollaboration> collaborate2Model (final String variant) {
		final Vector<ICollaboration> results = new Vector<ICollaboration>();
		for (int i = 0; i < _contents.size(); i++) {
			results.add(_contents.get(i));
		}
		return results;
	}

	@Override
	public boolean isEmpty () {
		if(_contents.size()<1)return true;
		return false;
	}

	public String getIconLinkName () {
		return _categoryDelegate.getIconLinkName();
	}

	public int getCategoryId () {
		return _categoryDelegate.getCategoryId();
	}

	public String getCategoryName () {
		return _categoryDelegate.getCategoryName();
	}

	public int getContentSize () {
		return _contents.size();
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("AssetCategory [");
		buffer.append(getCategoryName()).append(" ");
		buffer.append("nro cat: ").append(getContentSize()).append(" ");
		buffer.append(" ]");
		return buffer.toString();
	}

	public void addAsset (final NeoComAsset newasset) {
		_contents.add(newasset);
	}
}
