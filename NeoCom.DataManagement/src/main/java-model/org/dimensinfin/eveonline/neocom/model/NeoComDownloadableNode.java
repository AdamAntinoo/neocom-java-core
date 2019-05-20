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

import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.core.interfaces.IExpandable;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComDownloadableNode extends NeoComExpandableNode implements IDownloadable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -3742179733663283434L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean _downloading = false;
	private boolean _downloaded = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComDownloadableNode () {
		super();
		jsonClass = "NeoComDownloadableNode";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	public IDownloadable setDownloading (final boolean downloading) {
		this._downloading = downloading;
		return this;
	}

	public IDownloadable setDownloaded (final boolean downloaded) {
		this._downloaded = downloaded;
		return this;
	}

	public boolean isDownloading () {
		return _downloading;
	}

	public boolean isDownloaded () {
		return _downloaded;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("NeoComExpandableNode [");
		buffer.append(" ]");
		return buffer.toString();
	}
}
