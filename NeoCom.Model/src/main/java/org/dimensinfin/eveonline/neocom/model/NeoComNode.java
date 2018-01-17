//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;
import org.dimensinfin.core.model.AbstractPropertyChanger;

import java.util.ArrayList;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComNode extends AbstractPropertyChanger implements ICollaboration, IJsonAngular {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 6506043294337948561L;

	// - F I E L D - S E C T I O N ............................................................................
	protected String					jsonClass					= "NeoComNode";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComNode() {
		jsonClass = "NeoComNode";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public List<ICollaboration> collaborate2Model(final String variant) {
		return new ArrayList<>();
	}

	public String getJsonClass() {
		return jsonClass;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComNode [");
		buffer.append(" ]");
		return buffer.toString();
	}
}
