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

import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.connector.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.connector.IModelGenerator;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractGenerator implements IModelGenerator {
	// - S T A T I C - S E C T I O N ..........................................................................
	protected static Logger								logger					= Logger.getLogger("AbstractGenerator");

	// - F I E L D - S E C T I O N ............................................................................
	protected DataSourceLocator						_locator				= null;
	private boolean												_cacheable			= true;
	private final HashMap<String, Object>	_parameters			= new HashMap<String, Object>();
	/** The initial node where to store the model. Model elements are children of this root. */
	protected RootNode										_dataModelRoot	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractGenerator(final DataSourceLocator locator) {
		_locator = locator;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public AbstractGenerator addParameter(final String name, final int value) {
		_parameters.put(name, Integer.valueOf(value));
		return this;
	}

	public AbstractGenerator addParameter(final String name, final long value) {
		_parameters.put(name, Long.valueOf(value));
		return this;
	}

	public AbstractGenerator addParameter(final String name, final String value) {
		_parameters.put(name, value);
		return this;
	}

	public DataSourceLocator getDataSourceLocator() {
		return _locator;
	}

	public void setCacheable(final boolean cacheState) {
		_cacheable = cacheState;
	}

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
