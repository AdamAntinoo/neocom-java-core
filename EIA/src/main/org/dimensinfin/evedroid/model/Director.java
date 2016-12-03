//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class will adapt an Activity to a node to relay some field data to the Part.
 * @author Adam Antinoo
 */
public class Director extends AbstractNeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("Director");
	// - F I E L D - S E C T I O N ............................................................................
	private INeoComDirector targetActivity;


	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Director(INeoComDirector target) {
		targetActivity=target;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(String variant) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return targetActivity.getName();
	}

}

// - UNUSED CODE ............................................................................................
