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

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.eveonline.neocom.entities.*;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class ExtendedLocation extends EveLocation implements IExpandable, IDownloadable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -4484922266027865406L;
	private static Logger logger = Logger.getLogger("ExtendedLocation");

	// - F I E L D - S E C T I O N ............................................................................
	private EveLocation delegate = null;
	private long _characterIdentifier = -1;
	/** Set the default content manager to one that is managed manually through method calls. */
	private IContentManager contentManager = null;
	private boolean _expanded = false;
	private boolean _renderIfEmpty = true;
	private boolean _downloading = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ExtendedLocation () {
		super();
		this.setRenderWhenEmpty(false);
		jsonClass = "ExtendedLocation";
	}

	@Deprecated
	public ExtendedLocation (final Credential credential, final EveLocation delegate) {
		this(delegate);
		_characterIdentifier = credential.getAccountId();
	}

	public ExtendedLocation (final long characterId, final EveLocation delegate) {
		this(delegate);
		_characterIdentifier = characterId;
	}

	private ExtendedLocation (final EveLocation delegate) {
		this();
		//		this.setDownloaded(false);
		this.setRenderWhenEmpty(false);
		this.delegate = delegate;
		// Copy important identifiers from delegate.
		id = delegate.id;
		stationId = delegate.getStationId();
		constellationId = Long.valueOf(delegate.getConstellationId()).intValue();
		regionId = Long.valueOf(delegate.getRegionId()).intValue();
		jsonClass = "ExtendedLocation";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int addContent (final org.dimensinfin.eveonline.neocom.entities.NeoComAsset asset) {
		if ( null != contentManager )
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
	public List<ICollaboration> collaborate2Model (final String variant) {
		return contentManager.collaborate2Model(variant);
	}

	@JsonIgnore
	public List<org.dimensinfin.eveonline.neocom.entities.NeoComAsset> downloadContents () {
		if ( null != contentManager )
			return contentManager.getContents();
		else
			return new ArrayList<org.dimensinfin.eveonline.neocom.entities.NeoComAsset>();
	}

	@Override
	public String getConstellation () {
		return delegate.getConstellation();
	}

	public int getContentSize () {
		if ( null != contentManager )
			return contentManager.getContentSize();
		else
			return 0;
	}

	@Override
	public String getFullLocation () {
		return delegate.getFullLocation();
	}

	public long getLocationId () {
		return delegate.getId();
	}

	@Override
	public String getName () {
		return delegate.getName();
	}

	public long getCredentialIdentifier () {
		return _characterIdentifier;
	}

	@Override
	public String getRegion () {
		return delegate.getRegion();
	}

	@Override
	public String getSecurity () {
		return delegate.getSecurity();
	}

	@Override
	public double getSecurityValue () {
		return delegate.getSecurityValue();
	}

	@Override
	public String getStation () {
		return delegate.getStation();
	}

	@Override
	public String getSystem () {
		return delegate.getSystem();
	}

	@Override
	public ELocationType getTypeId () {
		return delegate.getTypeId();
	}

	@Override
	public String getUrlLocationIcon () {
		return delegate.getUrlLocationIcon();
	}

	public boolean isDownloaded () {
		if ( contentManager instanceof IDownloadable )
			return ((IDownloadable) delegate).isDownloaded();
		else
			return true;
	}

	public boolean isEmpty () {
		if ( null != contentManager )
			return contentManager.isEmpty();
		else
			return true;
	}

	public boolean collapse () {
		_expanded = false;
		return _expanded;
	}

	public boolean expand () {
		_expanded = true;
		return _expanded;
	}

	public boolean isExpanded () {
		return _expanded;
	}

	public IExpandable setRenderWhenEmpty (final boolean renderWhenEmpty) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}
	public boolean isRenderWhenEmpty () {
		if ( _renderIfEmpty )
			return true;
		else {
			if ( this.isEmpty() )
				return false;
			else
				return true;
		}
	}
	@Override
	public EveLocation setConstellation (final String constellation) {
		delegate.setConstellation(constellation);
		return this;
	}

//	@Override
	public EveLocation setConstellationId (final long constellationID) {
		delegate.setConstellationId(constellationID);
		return this;
	}

	public void setContentManager (final IContentManager manager) {
		contentManager = manager;
	}
	public IContentManager getContentManager () {
		return contentManager;
	}

	public IDownloadable setDownloaded (final boolean downloadedstate) {
		if ( null == delegate )
			return this;
		else if ( contentManager instanceof IDownloadable ) {
			((IDownloadable) delegate).setDownloaded(downloadedstate);
			return this;
		} else
			return this;
	}
	public IDownloadable setDownloading (final boolean downloading) {
		this._downloading = downloading;
		return this;
	}
	public boolean isDownloading () {
		return _downloading;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ExtendedLocation [");
		buffer.append(delegate.toString());
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
