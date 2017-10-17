//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class Region extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long							serialVersionUID	= 3623925848703776069L;

	// - F I E L D - S E C T I O N ............................................................................
	private String												_title						= "-DEEP SPACE-";
	private final ArrayList<EveLocation>	_locations				= new ArrayList<EveLocation>();

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public Region() {
		this.setDownloaded(true);
		jsonClass = "Region";
	}

	/**
	 * If the region id is -1 this means that this is probable coming from an space structure not registered on
	 * CCP data. So we can assume that this is a User Structure in an unknown place of space.
	 * 
	 * @param regionid
	 * @param regionName
	 */
	public Region(final long regionid, final String regionName) {
		// If undefined update the name.
		if (-1 == regionid) {
			this.setTitle("-DEEP SPACE-");
		}
	}

	public Region(final String title) {
		this();
		_title = title;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Deprecated
	@Override
	public void addChild(final IGEFNode child) {
		if (child instanceof EveLocation) {
			_locations.add((EveLocation) child);
		}
	}

	public void addLocation(final EveLocation target) {
		if (null != target) {
			_locations.add(target);
		}
	}

	/**
	 * Check visibility and extension before selecting what collaborates.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		if (this.isVisible()) if (this.isExpanded()) {
			results.addAll(this.getLocations());
		}
		return results;
	}

	@Deprecated
	@Override
	public Vector<IGEFNode> getChildren() {
		Vector<IGEFNode> result = new Vector<IGEFNode>(_locations.size());
		for (EveLocation node : _locations) {
			result.add(node);
		}
		return result;
	}

	public int getContentCount() {
		return _locations.size();
	}

	public ArrayList<EveLocation> getLocations() {
		return _locations;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public boolean isEmpty() {
		return (_locations.size() > 0) ? false : true;
	}

	@Override
	public Region setDownloaded(final boolean downloadedstate) {
		super.setDownloaded(downloadedstate);
		return this;
	}

	public void setTitle(final String title) {
		_title = title;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Region [");
		buffer.append(_title).append(" [").append(_locations.size()).append("]");
		buffer.append(" ]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
