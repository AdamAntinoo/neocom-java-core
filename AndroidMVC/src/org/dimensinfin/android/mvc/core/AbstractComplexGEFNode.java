//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.core;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class will serve the purpose to isolate data models implementations from the GEF model because on the
 * Android models we do not relay on the children to connect the different nodes and contents. We use a more
 * complex structure where the Android nodes can have multiple fields that collaborate to the hierarchy model.
 * <br>
 * The main purpose is to block the direct use of the children (even keeping them functional and operative for
 * some of the model convergence to the GEF design) and add more methods to control and use this new and
 * complex data model dependencies.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractComplexGEFNode extends AbstractGEFNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1423083997074853847L;
	private static Logger			logger						= Logger.getLogger("org.dimensinfin.android.core");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractComplexGEFNode() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method is being hidden from usage on Android implementations to use a better and complex design.
	 */
	@Override
	@Deprecated
	public void addChild(final IGEFNode child) {
		super.addChild(child);
	}

	public abstract ArrayList<AbstractAndroidNode> collaborate2Model(final String variant);

	/**
	 * This method is being hidden from usage on Android implementations to use a better and complex design.
	 */
	@Override
	@Deprecated
	public Vector<IGEFNode> getChildren() {
		return super.getChildren();
	}
}

// - UNUSED CODE ............................................................................................
