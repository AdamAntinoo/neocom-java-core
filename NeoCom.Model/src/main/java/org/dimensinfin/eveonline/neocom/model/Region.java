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
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IExpandable;

// - CLASS IMPLEMENTATION ...................................................................................
public class Region extends NeoComNode implements IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long							serialVersionUID	= 3623925848703776069L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean												_expanded					= false;
	private boolean												_renderIfEmpty		= true;
	private String												_title						= "-DEEP SPACE-";
	private final ArrayList<EveLocation>	_locations				= new ArrayList<EveLocation>();

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public Region() {
		super();
		//		this.setDownloaded(true);
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
		this();
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
	public void addLocation(final EveLocation target) {
		if (null != target) {
			_locations.add(target);
		}
	}

	/**
	 * Check visibility and extension before selecting what collaborates.
	 */
	@Override
	public List<ICollaboration> collaborate2Model(final String variant) {
		ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		//		if (this.isVisible()) {
		//			if (this.isExpanded()) {
		results.addAll(this.getLocations());
		//		}
		//		}
		return results;
	}

	public int getLocationCount() {
		return _locations.size();
	}

	public ArrayList<EveLocation> getLocations() {
		return _locations;
	}

	public String getTitle() {
		return _title;
	}

	public boolean isEmpty() {
		return (_locations.size() > 0) ? false : true;
	}

	public boolean isExpandable() {
		return true;
	}

	//	@Override
	//	public Region setDownloaded(final boolean downloadedstate) {
	//		super.setDownloaded(downloadedstate);
	//		return this;
	//	}

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

	public boolean collapse() {
		_expanded = false;
		return _expanded;
	}

	public boolean expand() {
		_expanded = true;
		return _expanded;
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public IExpandable setRenderWhenEmpty(final boolean renderWhenEmpty) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

	public boolean isRenderWhenEmpty() {
		if (_renderIfEmpty)
			return true;
		else {
			if (this.isEmpty())
				return false;
			else
				return true;
		}
	}
}

// - UNUSED CODE ............................................................................................
