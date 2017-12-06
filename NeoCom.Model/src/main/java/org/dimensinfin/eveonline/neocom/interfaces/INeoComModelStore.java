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

import org.dimensinfin.core.interfaces.IModelStore;
import org.dimensinfin.eveonline.neocom.model.Login;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComModelStore extends IModelStore {

	public Set<NeoComCharacter> accessCharacterList();

	public Hashtable<String, Login> accessLoginList();

	public Login activateLoginIdentifier(final String loginTarget);

	public NeoComCharacter activatePilot(long characterID);

	public NeoComCharacter activatePilot(final String characterstring);

	public void cleanModel();

	public NeoComCharacter getActiveCharacter();

	public List<NeoComCharacter> getActiveCharacters();

	public Login getActiveLogin();

	public String getActiveLoginName();

	@Deprecated
	public NeoComCharacter getCurrentPilot();

	@Deprecated
	public String getLoginIdentifier();
}

// - UNUSED CODE ............................................................................................
