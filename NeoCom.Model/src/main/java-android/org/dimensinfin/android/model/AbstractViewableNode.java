//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.android.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.IViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractViewableNode extends AbstractComplexNode implements IViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -1735276692612402194L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractViewableNode() {
		this.jsonClass = "AbstractViewableNode";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public abstract ArrayList<AbstractComplexNode> collaborate2Model(final String variant);

	/**
	 * Any instance of the subclasses for this class should be able to transform the contents, being them
	 * Regions, or Locations or whatever to a number of elements. That way we can generalize the Part
	 * collaboration process.
	 * 
	 * @return
	 */
	//	public abstract int getContentCount();
	public boolean isEmpty() {
		return true;
	}

	public boolean isExpandable() {
		return false;
	}

	protected ArrayList<AbstractComplexNode> concatenateChildren(final ArrayList<AbstractComplexNode> target,
			final List<IGEFNode> children) {
		for (IGEFNode node : children)
			if (node instanceof AbstractComplexNode) target.add((AbstractComplexNode) node);
		return target;
	}
}

// - UNUSED CODE ............................................................................................
