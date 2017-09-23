//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.interfaces;

import java.util.List;

import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.eveonline.neocom.model.Login;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComModelStore extends IModelStore {

	// - M E T H O D - S E C T I O N ..........................................................................
	public Login activateLoginIdentifier(final String loginTarget);

	public NeoComCharacter activatePilot(long characterID);

	public List<NeoComCharacter> getActiveCharacters();

	public List<NeoComApiKey> getApiKeys();

	public NeoComCharacter getCurrentPilot();

	public String getLoginIdentifier();

	//	public NeoComCharacter searchCharacter(long _locator);

	//	public void setCurrentPilot(NeoComCharacter pilot);
}

// - UNUSED CODE ............................................................................................
