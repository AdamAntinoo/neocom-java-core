//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.core.model;


// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.core.model.AbstractGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractComplexNode extends AbstractGEFNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long		serialVersionUID					= -7147051299063576257L;
	public static final String	EVENT_EXPANDCOLLAPSENODE	= "AbstractAndroidNode.EVENT_EXPANDCOLLAPSENODE";
	public static final String	EVENT_DOWNLOADNODE				= "AbstractAndroidNode.EVENT_DOWNLOADNODE";

	// - F I E L D - S E C T I O N ............................................................................
	/** Stores the expand/collapse visual state to be used on Android UI. */
	private boolean							expanded									= false;
	/**
	 * If the node has its children calculated (by default) then this shows a <code>true</code>. If the
	 * hierarchy may be calculated on a deferred instant like the moment the user expands or selects the node
	 * then stores the downloaded state of the children data.
	 */
	private boolean							downloaded								= false;
	/** If true then the node is ever on the contents list. If false then it will depend on the hierarchy. */
	private boolean							renderWhenEmpty						= true;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
//	public ArrayList<AbstractAndroidNode> collaborate2Model(final String variant) {
//		final ArrayList<AbstractAndroidNode> results = new ArrayList<AbstractAndroidNode>();
//		if (renderWhenEmpty()) results.add(this);
//		return results;
//	}
//public abstract ArrayList<AbstractComplexNode> collaborate2Model(final String variant);
	/**
	 * Collapses a node from any state if had. If will collapse the node without checking the current state.
	 * 
	 * @return the final extend state.
	 */
	public boolean collapse() {
		boolean oldValue = expanded;
		expanded = false;
		// REFACTOR This gets fires to every listener and there are duplicates
		//		firePropertyChange(EVENT_EXPANDCOLLAPSENODE, new Boolean(oldValue), new Boolean(expanded));
		return expanded;
	}

	/**
	 * Only expands nodes that have children. This action also has to keep synchronized the recording of actions
	 * that are used to reproduce the state of a hierarchy.<br>
	 * With the new model there is no need to check if an element has children to filter the expansion. This is
	 * something that is used and calculated when the model element collaborates to the viewable model. Remove
	 * that check because now children hierarchies are not guaranteed.
	 * 
	 * @return the final extend state.
	 */
	public boolean expand() {
		boolean oldValue = expanded;
		expanded = true;
		// REFACTOR This gets fires to every listener and there are duplicates
		//		firePropertyChange(EVENT_EXPANDCOLLAPSENODE, new Boolean(oldValue), new Boolean(expanded));
		return expanded;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Checks of the groups should be rendered depending of some configuration values. By default all groups are
	 * rendered only when they have contents. If they are empty they may not be rendered. Only they can be
	 * forced to be rendered if the right flag value is set to true. <br>
	 * With the new design the use of the children to control the emptiness of a node is no longer valid until I
	 * implement the children list as a copy of the model representation. So the abstract nodes are empty by
	 * definition under the new design.
	 * 
	 * @return
	 */
	public boolean renderWhenEmpty() {
		if (this.renderWhenEmpty)
				if (getChildren().size() > 0) return true;
				else return false;
		else return this.renderWhenEmpty;
	}

	public void setDownloaded(final boolean downloaded) {
		this.downloaded = downloaded;
	}

	public boolean setExpanded(final boolean newState) {
		expanded = newState;
		return expanded;
	}

	public void setRenderWhenEmpty(final boolean renderWhenEmpty) {
		this.renderWhenEmpty = renderWhenEmpty;
	}

	/**
	 * Tries to invert the expansion state of the node. Only expands nodes that have children and is not
	 * operative on the other nodes.<br>
	 * Since the addition of the view cache every change on the part content or model should trigger a change on
	 * the view.
	 */
	public void toggleExpanded() {
		if (!expanded)
			expand();
		else
			collapse();
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("AbstractAndroidNode [");
		buffer.append("expand/download/renderEmpty: ").append(expanded).append("/").append(downloaded).append("/")
				.append(renderWhenEmpty);
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
