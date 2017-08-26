//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.generator;

import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.connector.IModelGenerator;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotDirectorsGenerator extends AbstractGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String login = "Default";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotDirectorsGenerator(final DataSourceLocator locator, final String login) {
		super(locator);
		this.login = login;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public RootNode collaborate2Model() {
		AbstractGenerator.logger.info(">> [PilotDirectorsGenerator.collaborate2Model]");
		// Initialize the Adapter data structures.
		this.setDataModel(new RootNode());

		// Add to the data model list the Directors that can be used on this character.
		_dataModelRoot.addChild(new AssetsManager(AppConnector.getModelStore().getCurrentPilot()));
		AbstractGenerator.logger.info("<< [PilotDirectorsGenerator.collaborate2Model]");
		return _dataModelRoot;
	}
}

// - UNUSED CODE ............................................................................................
