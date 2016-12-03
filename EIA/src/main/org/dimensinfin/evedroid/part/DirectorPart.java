//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.model.Director;
import org.dimensinfin.evedroid.model.Separator;

// - CLASS IMPLEMENTATION ...................................................................................
public class DirectorPart extends AbstractAndroidPart{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 2354400290130765711L;
	private static Logger logger = Logger.getLogger("DirectorPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorPart(final Director node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public long getModelID() {
		return getCastedModel().getName().hash();
	}

	private Director getCastedModel() {
		return (Director) getModel();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// TODO Auto-generated method stub
		return null;
	}

}

// - UNUSED CODE ............................................................................................
