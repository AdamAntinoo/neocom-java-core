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

import org.dimensinfin.core.model.AbstractComplexNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComNode {
	// - M E T H O D - S E C T I O N ..........................................................................
	public abstract ArrayList<AbstractComplexNode> collaborate2Model(final String variant);
}

// - UNUSED CODE ............................................................................................
