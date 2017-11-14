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
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;
import org.dimensinfin.eveonline.neocom.interfaces.IExpandable;
import org.dimensinfin.eveonline.neocom.manager.DefaultAssetsContentManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

// - CLASS IMPLEMENTATION ...................................................................................
public class ExtendedLocation extends EveLocation implements IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -4484922266027865406L;
	private static Logger			logger						= Logger.getLogger("ExtendedLocation.java");

	// - F I E L D - S E C T I O N ............................................................................
	private EveLocation				delegate					= null;
	private NeoComCharacter		pilot							= null;
	private IContentManager		contentManager		= new DefaultAssetsContentManager(this);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ExtendedLocation() {
		super();
		this.setDownloaded(false);
		this.setRenderWhenEmpty(false);
		jsonClass = "ExtendedLocation";
	}

	public ExtendedLocation(final NeoComCharacter character, final EveLocation delegate) {
		this(delegate);
		pilot = character;
	}

	private ExtendedLocation(final EveLocation delegate) {
		this();
		this.setDownloaded(false);
		this.setRenderWhenEmpty(false);
		this.delegate = delegate;
		// Copy important identifiers from delegate.
		id = delegate.getRealId();
		stationID = delegate.getStationID();
		constellationID = delegate.getConstellationID();
		regionID = delegate.getRegionID();
		jsonClass = "ExtendedLocation";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int addContent(final NeoComAsset asset) {
		if (null != contentManager)
			return contentManager.add(asset);
		else
			return 0;
	}

	/**
	 * Locations collaborate to the model by adding all their contents if already downloaded. If not downloaded
	 * but are being expanded then we should first download all their contents and process them into the model
	 * before generating a new collaboration hierarchy.<br>
	 * During the resolution of the contents we check the download state to download the items if not already
	 * done.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return (ArrayList<AbstractComplexNode>) contentManager.collaborate2Model(variant);
	}

	@JsonIgnore
	public List<NeoComAsset> downloadContents() {
		if (null != contentManager)
			return contentManager.getContents();
		else
			return new ArrayList<NeoComAsset>();
	}

	@Override
	public String getConstellation() {
		return delegate.getConstellation();
	}

	public int getContentSize() {
		if (null != contentManager)
			return contentManager.getContentSize();
		else
			return 0;
	}

	@Override
	public String getFullLocation() {
		return delegate.getFullLocation();
	}

	@Override
	public long getID() {
		return delegate.getID();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	public long getPilotId() {
		return pilot.getCharacterID();
	}

	@Override
	public String getRegion() {
		return delegate.getRegion();
	}

	@Override
	public String getSecurity() {
		return delegate.getSecurity();
	}

	@Override
	public double getSecurityValue() {
		return delegate.getSecurityValue();
	}

	@Override
	public String getStation() {
		return delegate.getStation();
	}

	@Override
	public String getSystem() {
		return delegate.getSystem();
	}

	@Override
	public ELocationType getTypeID() {
		return delegate.getTypeID();
	}

	@Override
	public String getUrlLocationIcon() {
		return delegate.getUrlLocationIcon();
	}

	@Override
	public boolean isDownloaded() {
		return delegate.isDownloaded();
	}

	@Override
	public boolean isEmpty() {
		if (null != contentManager)
			return contentManager.isEmpty();
		else
			return true;
	}

	@Override
	public boolean isExpandable() {
		return true;
	}

	@Override
	public boolean isExpanded() {
		return delegate.isExpanded();
	}

	@Override
	public boolean isRenderWhenEmpty() {
		if (renderWhenEmpty)
			return true;
		else {
			if (this.isEmpty())
				return false;
			else
				return true;
		}
	}

	@Override
	public boolean isVisible() {
		return delegate.isVisible();
	}

	@Override
	public void setConstellation(final String constellation) {
		delegate.setConstellation(constellation);
	}

	@Override
	public void setConstellationID(final long constellationID) {
		delegate.setConstellationID(constellationID);
	}

	public void setContentManager(final IContentManager manager) {
		contentManager = manager;
	}

	@Override
	public AbstractComplexNode setDownloaded(final boolean downloadedstate) {
		if (null == delegate)
			return this;
		else
			return delegate.setDownloaded(downloadedstate);
	}

	@Override
	public AbstractComplexNode setExpanded(final boolean newState) {
		return delegate.setExpanded(newState);
	}

	@Override
	public boolean toggleExpanded() {
		return delegate.toggleExpanded();
	}

	@Override
	public boolean toggleVisible() {
		return delegate.toggleVisible();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ExtendedLocation [");
		buffer.append(delegate.toString());
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................