//	PROJECT:        NeoCom.model
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API15.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.
package org.dimensinfin.evedroid.datasource;

//- IMPORT SECTION .........................................................................................
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Allows to set multiple identifiers to generate a single an unique String
 * identifier to locate a single DataSource. The current identifiers are the
 * Character ID and the DataSource type and along the variant to use for this
 * type. For example: for character id 9856634 and the type Assets we can set
 * the variant AssetsByLocation.
 * 
 * @author Adam Antinoo
 */
public class DataSourceLocator implements Serializable {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = -9208786809602219002L;
	private static Logger logger = Logger.getLogger("org.dimensinfin.neocom.common.datasource");
	private static final Object SEPARATOR = '/';

	// - F I E L D - S E C T I O N
	// ............................................................................
	private final ArrayList<String> identifiers = new ArrayList<String>();

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public DataSourceLocator() {
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public DataSourceLocator addIdentifier(final int identifier) {
		identifiers.add(Integer.valueOf(identifier).toString());
		return this;
	}

	public DataSourceLocator addIdentifier(final long identifier) {
		identifiers.add(Long.valueOf(identifier).toString());
		return this;
	}

	public DataSourceLocator addIdentifier(final String identifier) {
		identifiers.add(identifier);
		return this;
	}

	/**
	 * Get the concatenation of the identifiers to generate a single unique
	 * identifier.
	 * 
	 * @return identifier concatenation
	 */
	public String getIdentity() {
		StringBuffer buffer = new StringBuffer();
		for (String id : identifiers) {
			buffer.append(id).append(SEPARATOR);
		}
		return buffer.toString();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("DataSourceLocator[");
		buffer.append(getIdentity()).append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE
// ............................................................................................
