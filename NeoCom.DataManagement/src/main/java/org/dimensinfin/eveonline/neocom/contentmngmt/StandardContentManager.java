//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.contentmngmt;

import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This is another class of Content Manager. Instead of managing the contend on demand we should be sure that
 * all the contents are accessible before the methods are called. There is no downloaded state and contents
 * are always accessible.
 * 
 * @author Adam Antinoo
 */
public class StandardContentManager extends AbstractContentManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -4981239043616210887L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								contentCount			= 0;
	private int								containerCount		= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public StandardContentManager (final ExtendedLocation newparent) {
		super(newparent);
		jsonClass = "PlanetaryAssetsContentManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * We add the contents but we only count when it is a Planetary Resource.
	 */
	@Override
	public int add(final NeoComAsset child) {
		if (null != child) {
			super.add(child);
			if (child instanceof IAssetContainer) {
				containerCount++;
			} else {
				contentCount++;
			}
		}
		return contentCount;
	}

	public int getContainerCount() {
		return containerCount;
	}
}

// - UNUSED CODE ............................................................................................
