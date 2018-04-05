//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.storage;

import org.dimensinfin.core.interfaces.IModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adam on 14/01/2018.
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class NoOpPersistenceHandler implements IPersistentHandler{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(NoOpPersistenceHandler.class);

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NoOpPersistenceHandler () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public IModelStore getStore () {
		return null;
	}

	@Override
	public boolean restore () {
		return false;
	}

	@Override
	public boolean save () {
		return false;
	}

	@Override
	public void setStore (final IModelStore newStore) {

	}
//	@Override
//	public String toString () {
//		StringBuffer buffer = new StringBuffer("NoOpPersistenceHandler [");
//		buffer.append("name: ").append(0);
//		buffer.append("]");
//		buffer.append("->").append(super.toString());
//		return buffer.toString();
//	}
}
// - UNUSED CODE ............................................................................................
//[01]
