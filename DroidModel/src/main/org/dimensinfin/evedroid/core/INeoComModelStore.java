//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.evedroid.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComModelStore extends IModelStore {

	// - M E T H O D - S E C T I O N ..........................................................................
	public NeoComCharacter getPilot();
}

// - UNUSED CODE ............................................................................................
