//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import java.util.List;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class AbstractContentManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger logger = Logger.getLogger("AbstractContentManager");

	// - F I E L D - S E C T I O N ............................................................................
	protected String									jsonClass	= "AbstractContentManager";
	protected EveLocation							parent		= null;
	protected final List<NeoComAsset>	contents	= new Vector<NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractContentManager() {
		jsonClass = "";
	}

	public AbstractContentManager(final EveLocation newparent) {
		this();
		parent = newparent;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int add(final NeoComAsset child) {
		if (null != child) {
			contents.add(child);
		}
		return contents.size();
	}

	public long getID() {
		return parent.getRealId();
	}
}

// - UNUSED CODE ............................................................................................
