//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.core;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class NeoComAbstractPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComAbstractPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComAbstractPart() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public long getModelID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractHolder selectHolder() {
		// TODO Auto-generated method stub
		return null;
	}

}

// - UNUSED CODE ............................................................................................
