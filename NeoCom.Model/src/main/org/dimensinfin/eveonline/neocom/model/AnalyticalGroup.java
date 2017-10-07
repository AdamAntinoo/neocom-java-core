//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download and parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.eveonline.neocom.interfaces.IWeigthedNode;

/**
 * The requirements of model objects to be nodes has to review the implementation of simple nodes that only
 * have a literal or a title and more complex nodes that will have some analytical information about the
 * elements contained inside. Any node will have children and some states so this abstract class will add
 * support to that states and to the availability of analytical data.br> Additionally it will export the
 * <code>IWeigthedNode</code> interface that allows the nodes to be ordered by any application specific
 * numeric weight data.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AnalyticalGroup extends AbstractViewableNode implements IWeigthedNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long		serialVersionUID					= 8917539237775595255L;
	public static final String	EVENT_EXPANDCOLLAPSENODE	= "AnalyticalGroup.EVENT_EXPANDCOLLAPSENODE";

	// - F I E L D - S E C T I O N ............................................................................
	protected String						title											= "G1";
	protected int								weight										= 10;
	private boolean							expanded									= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AnalyticalGroup() {
		super();
	}

	public AnalyticalGroup(final String title) {
		super();
		this.title = title;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Collapses a node from any state if had. If will collapse the node without checking the current state.
	 * 
	 * @return the final extend state.
	 */
	@Override
	public boolean collapse() {
		boolean oldValue = expanded;
		expanded = false;
		this.firePropertyChange(AnalyticalGroup.EVENT_EXPANDCOLLAPSENODE, new Boolean(oldValue), new Boolean(expanded));
		return expanded;
	}

	/**
	 * Only expands nodes that have children. This action also has to keep synchronized the recording of actions
	 * that are used to reproduce the state of a hierarchy.
	 * 
	 * @return the final extend state.
	 */
	@Override
	public boolean expand() {
		boolean oldValue = expanded;
		if (this.getChildren().size() > 0) {
			expanded = true;
		} else {
			expanded = false;
		}
		this.firePropertyChange(AnalyticalGroup.EVENT_EXPANDCOLLAPSENODE, new Boolean(oldValue), new Boolean(expanded));
		return expanded;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public void setWeight(final int weight) {
		this.weight = weight;
	}
}
// - UNUSED CODE ............................................................................................
