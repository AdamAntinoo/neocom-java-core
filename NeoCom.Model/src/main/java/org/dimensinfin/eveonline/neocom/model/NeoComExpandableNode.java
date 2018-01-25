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

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.core.interfaces.IExpandable;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComExpandableNode extends NeoComNode implements IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -3742179733511283434L;

	// - F I E L D - S E C T I O N ............................................................................
	protected boolean _expanded = false;
	protected boolean _renderIfEmpty = true;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComExpandableNode () {
		super();
		jsonClass = "NeoComExpandableNode";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@JsonIgnore
	public boolean collapse () {
		_expanded = false;
		return _expanded;
	}

	@JsonIgnore
	public boolean expand () {
		_expanded = true;
		return _expanded;
	}

	public abstract boolean isEmpty ();

	public boolean isExpanded () {
		return _expanded;
	}

	public boolean isRenderWhenEmpty () {
		return _renderIfEmpty;
	}

	public IExpandable setRenderWhenEmpty (final boolean renderWhenEmpty) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("NeoComExpandableNode [");
		buffer.append(" ]");
		return buffer.toString();
	}
}
