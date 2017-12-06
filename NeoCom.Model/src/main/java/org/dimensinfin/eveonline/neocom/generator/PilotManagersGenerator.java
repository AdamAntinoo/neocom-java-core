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

import org.dimensinfin.core.datasource.AbstractGenerator;
import org.dimensinfin.core.datasource.DataSourceLocator;
import org.dimensinfin.core.interfaces.IModelGenerator;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotManagersGenerator extends AbstractGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private final String		login			= "Default";
	private NeoComCharacter	character	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotManagersGenerator(final DataSourceLocator locator, final String variant, final NeoComCharacter pilotCorp) {
		super(locator, variant);
		character = pilotCorp;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration for a Pilot or Corporation on this Activity is the list of Managers that can be applied
	 * to the Character. So we can move the decission to the place where it better suits that is the Character
	 * that is already selected. This is performed internally in a recursive way by calling the model
	 * collaboration depending on the variant.
	 */
	public RootNode collaborate2Model() {
		AbstractGenerator.logger.info(">> [PilotDirectorsGenerator.collaborate2Model]");
		// Initialize the Adapter data structures.
		this.setDataModel(new RootNode());

		// Add to the data model list the Directors that can be used on this character.
		_dataModelRoot.addChildren(character.collaborate2Model(this.getVariant()));
		AbstractGenerator.logger.info("<< [PilotDirectorsGenerator.collaborate2Model]");
		return _dataModelRoot;
	}
}

// - UNUSED CODE ............................................................................................
