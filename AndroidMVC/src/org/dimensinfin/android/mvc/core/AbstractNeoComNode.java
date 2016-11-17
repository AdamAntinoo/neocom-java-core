//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.core;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.core.INeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractNeoComNode extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -1735276692612402194L;
	private static Logger			logger						= Logger.getLogger("org.dimensinfin.android.mvc.core");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractNeoComNode() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public abstract ArrayList<AbstractComplexNode> collaborate2Model(final String variant);
}

// - UNUSED CODE ............................................................................................
