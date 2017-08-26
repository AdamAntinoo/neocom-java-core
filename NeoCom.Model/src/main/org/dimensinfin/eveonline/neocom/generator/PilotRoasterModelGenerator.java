//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//								environment limits.
//								Database and model adaptations for storage model independency.
package org.dimensinfin.eveonline.neocom.generator;

import java.util.List;

import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.connector.IModelGenerator;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import com.beimin.eveapi.exception.ApiException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotRoasterModelGenerator extends AbstractGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String login = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotRoasterModelGenerator(final DataSourceLocator locator, final String login) {
		super(locator);
		this.login = login;
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
		// Access the database to get the list of keys. From that point on we can retrieve the characters easily.
		List<ApiKey> apilist = null;
		try {
			Dao<ApiKey, String> keyDao = AppConnector.getDBConnector().getApiKeysDao();
			QueryBuilder<ApiKey, String> queryBuilder = keyDao.queryBuilder();
			Where<ApiKey, String> where = queryBuilder.where();
			where.eq("login", login);
			PreparedQuery<ApiKey> preparedQuery = queryBuilder.prepare();
			apilist = keyDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		// Initialize the Adapter data structures.
		this.setDataModel(new RootNode());
		// For each key get the list of characters and instantiate them to the resulting list.
		for (ApiKey apiKey : apilist) {
			try {
				NeoComApiKey key = NeoComApiKey.build(apiKey.getKeynumber(), apiKey.getValidationcode());
				// Scan for the characters declared into this key.
				for (NeoComCharacter pilot : key.getApiCharacters()) {
					_dataModelRoot.addChild(pilot);
					AbstractGenerator.logger.info(
							"-- [PilotRoasterModelAdapter.collaborate2Model]> Adding " + pilot.getName() + " to the _dataModelRoot");
				}
			} catch (ApiException apiex) {
				apiex.printStackTrace();
			}
		}

		AbstractGenerator.logger.info("<< [PilotListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}
}

// - UNUSED CODE ............................................................................................
