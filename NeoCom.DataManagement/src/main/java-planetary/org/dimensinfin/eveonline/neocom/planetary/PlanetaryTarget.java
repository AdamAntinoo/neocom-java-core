//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryTarget extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 3759275643794264024L;

	// - F I E L D - S E C T I O N ............................................................................
	private EveItem						privateItem				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryTarget(final EveItem item) {
		privateItem = item;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public List<ICollaboration> collaborate2Model(final String variant) {
		return new ArrayList<ICollaboration>();
	}

	public String getName() {
		return privateItem.getName();
	}

	public double getPrice() {
		return privateItem.getPrice();
	}

	public double getVolume() {
		return privateItem.getVolume();
	}

}

// - UNUSED CODE ............................................................................................
