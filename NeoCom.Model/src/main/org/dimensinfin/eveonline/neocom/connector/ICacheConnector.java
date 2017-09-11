//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.Vector;

import org.dimensinfin.eveonline.neocom.services.PendingRequestEntry;

// - CLASS IMPLEMENTATION ...................................................................................
public interface ICacheConnector {
	// - M E T H O D - S E C T I O N ..........................................................................
	public void addCharacterUpdateRequest(long characterID);

	public void addMarketDataRequest(long requestLocalizer);

	public void clearPendingRequest(final long localizer);

	public void clearPendingRequest(final String localizer);

	public int decrementMarketCounter();

	public int decrementTopCounter();

	public Vector<PendingRequestEntry> getPendingRequests();

	public int incrementMarketCounter();

	public int incrementTopCounter();

}

// - UNUSED CODE ............................................................................................
