//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.core;

import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class RootPart extends AbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -8085543451527813221L;
	private static Logger			logger						= Logger.getLogger("SeparatorPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RootPart(final RootNode node, final IPartFactory factory) {
		super(node, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getModelID() {
		return this.getModel().getClass().hashCode();
	}

	@Override
	public RootPart getRoot() {
		return this;
	}

	public void needsRedraw() {
	}

	/**
	 * Returns the same input because the core Root has no policies.
	 */
	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		return targets;
	}
}

// - UNUSED CODE ............................................................................................
