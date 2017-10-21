//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.core;

// - CLASS IMPLEMENTATION ...................................................................................
public class AccessStatistics {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int	accesses	= 0;
	private int	hits			= 0;
	private int	misses		= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int accountAccess(final boolean isHit) {
		if (isHit) {
			hits++;
		} else {
			misses++;
		}
		accesses++;
		return accesses;
	}

	public int getAccesses() {
		return accesses;
	}

	public int getHits() {
		return hits;
	}

	public int getMisses() {
		return misses;
	}
}

// - UNUSED CODE ............................................................................................
