//	PROJECT:      NeoCom.Databases (NEOC.D)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	SQLite database access library. Isolates Neocom database access from any
//								environment limits.
//								Database and model adaptations for storage model independency.
package org.dimensinfin.eveonline.neocom.generator;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
public class PilotRoasterModelGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger									logger					= Logger.getLogger("PilotRoasterModelGenerator");

	// - F I E L D - S E C T I O N ............................................................................
	private DataSourceLocator							_locator				= null;
	private boolean												_cacheable			= true;
	private final HashMap<String, Object>	_parameters			= new HashMap<String, Object>();
	/** The initial node where to store the model. Model elements are children of this root. */
	protected RootNode										_dataModelRoot	= null;

	private String												login						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotRoasterModelGenerator(final DataSourceLocator locator, final String login) {
		_locator = locator;
		this.login = login;
	}

	public PilotRoasterModelGenerator addParameter(final String name, final int value) {
		_parameters.put(name, Integer.valueOf(value));
		return this;
	}

	public PilotRoasterModelGenerator addParameter(final String name, final long value) {
		_parameters.put(name, Long.valueOf(value));
		return this;
	}

	public PilotRoasterModelGenerator addParameter(final String name, final String value) {
		_parameters.put(name, value);
		return this;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This adapter should generate the model for all the EVE characters associated to a set of api keys. The
	 * set is selected on the value of the login identifier that should already have stores that identifier
	 * along the api key on the Neocom database for retrieval. So from the unique login we get access to the set
	 * of keys and from there to the set of characters.
	 */
	public RootNode collaborate2Model() {
		PilotRoasterModelGenerator.logger.info(">> [PilotRoasterModelAdapter.collaborate2Model]");
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
					PilotRoasterModelGenerator.logger.info(
							"-- [PilotRoasterModelAdapter.collaborate2Model]> Adding " + pilot.getName() + " to the _dataModelRoot");
				}
			} catch (ApiException apiex) {
				apiex.printStackTrace();
			}
		}

		PilotRoasterModelGenerator.logger.info("<< [PilotListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}

	public DataSourceLocator getDataSourceLocator() {
		return _locator;
	}

	public void setCacheable(final boolean cacheState) {
		_cacheable = cacheState;
	}

	//[01]
	public void setDataModel(final RootNode root) {
		_dataModelRoot = root;
	}

	protected int getParameterInteger(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof Integer) return ((Integer) param).intValue();
		return 0;
	}

	protected long getParameterLong(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof Long) return ((Long) param).longValue();
		return 0;
	}

	protected String getParameterString(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof String) return (String) param;
		return "";
	}
}

// - UNUSED CODE ............................................................................................
