//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//								environment limits.
//								Database and model adaptations for storage model independency.
package org.dimensinfin.eveonline.neocom.generator;

import java.util.List;

import org.dimensinfin.core.datasource.AbstractGenerator;
import org.dimensinfin.core.datasource.DataSourceLocator;
import org.dimensinfin.core.interfaces.IModelGenerator;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotRoasterGenerator extends AbstractGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String	_login	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotRoasterGenerator(final DataSourceLocator locator, final String variant, final String login) {
		super(locator, variant);
		_login = login;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This adapter should generate the model for all the EVE characters associated to a set of api keys. The
	 * set is selected on the value of the login identifier that should already have stores that identifier
	 * along the api key on the Neocom database for retrieval. So from the unique login we get access to the set
	 * of keys and from there to the set of characters.
	 */
	public RootNode collaborate2Model() {
		AbstractGenerator.logger.info(">> [PilotRoasterModelAdapter.collaborate2Model]");
		// Get the list for characters associates to the current login. This should be already accessible at the AppModelStore.
		List<NeoComCharacter> characters = ModelAppConnector.getSingleton().getModelStore().getActiveCharacters();
		// Initialize the Adapter data structures.
		this.setDataModel(new RootNode());
		// For each key get the list of characters and instantiate them to the resulting list.
		// For first level items do not use the collaborate2Model function. This is to get the rest of the hierarchy
		for (NeoComCharacter character : characters) {
			_dataModelRoot.addChild(character);
			AbstractGenerator.logger.info("-- [PilotRoasterModelAdapter.collaborate2Model]> Adding '" + character.getName()
					+ "' to the _dataModelRoot");
		}
		AbstractGenerator.logger.info("<< [PilotListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}
}

// - UNUSED CODE ............................................................................................
