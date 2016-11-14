//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractAndroidPart extends AbstractGEFNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7467855028114565679L;
	private static Logger			logger						= Logger.getLogger("AbstractAndroidPart");

	// - F I E L D - S E C T I O N ............................................................................
	private IGEFNode					_model						= null;
	protected boolean					expanded					= false;
	protected boolean					downloaded				= false;
	protected int							renderMode				= 1000;
	protected Activity				_activity					= null;
	protected Fragment				_fragment					= null;
	private View							_view							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractAndroidPart(final IGEFNode model) {
		super();
		_model = model;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Collapses a node from any state if had.
	 * 
	 * @return the final expend state.
	 */
	public boolean collapse() {
		expanded = false;
		return expanded;
	}

	/**
	 * Only expand nodes that have children. This action also has to keep synchronized the recording of actions
	 * that are used to reproduce the state of a hierarchy.
	 * 
	 * @return the final expend state.
	 */
	public boolean expand() {
		if (getChildren().size() > 0)
			expanded = true;
		else
			expanded = false;
		return expanded;
	}

	public Activity getActivity() {
		if (null != _activity)
			return _activity;
		else
			throw new RuntimeException("Activity object not available on access on a Part.");
	}

	public Fragment getFragment() {
		if (null != _fragment)
			return _fragment;
		else
			throw new RuntimeException("Fragment object not available on access on a Part.");
	}

	public AbstractHolder getHolder(final Activity activity) {
		_activity = activity;
		return selectHolder();
	}

	@Deprecated
	public AbstractHolder getHolder(final Fragment fragment) {
		_fragment = fragment;
		_activity = fragment.getActivity();
		return selectHolder();
	}

	public IGEFNode getModel() {
		return _model;
	}

	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * 
	 * @return <code>long</code> identifier with the model number.
	 */
	public abstract long getModelID();

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grandchildren of this one. If the node is collapsed then the only
	 * result will be the node itself.
	 * 
	 * @return list of parts that are accesible for this node.
	 */
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<IGEFNode> ch = getChildren();
		for (IGEFNode node : ch) {
			// Convert the node to a part.
			AbstractAndroidPart part = (AbstractAndroidPart) node;
			result.add(part);
			// Check if the node is expanded. Then add its children.
			if (part.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
				result.addAll(grand);
			}
		}
		return result;
	}

	public int getRenderMode() {
		return renderMode;
	}

	public View getView() {
		return _view;
	}

	public void invalidate() {
		if (null != _view) {
			_view.invalidate();
			needsRedraw();
		}
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setDownloaded() {
		downloaded = true;
	}

	/**
	 * Sets the expand to the value shown on the parameter. This may not toggle the real expand state. On nodes
	 * with no children has no impact because the node will fall back again to the collapsed state.
	 * 
	 * @param newExpanded
	 */
	public void setExpanded(final boolean newExpanded) {
		if (newExpanded)
			expand();
		else
			collapse();
	}

	public void setModel(final IGEFNode model) {
		_model = model;
		needsRedraw();
	}

	public IGEFNode setRenderMode(final int renderMode) {
		this.renderMode = renderMode;
		needsRedraw();
		return this;
	}

	public void setView(final View convertView) {
		_view = convertView;
	}

	/**
	 * Tries to invert the expansion state of the node. Only expands nodes that have children and is not
	 * operative on the other nodes.<br>
	 * Since the addition of the view cache every change on the part content or model should trigger a change on
	 * the view. This can be achieved by removing the view from the cache.
	 */
	public void toggleExpanded() {
		if (!expanded)
			expand();
		else
			collapse();
		needsRedraw();
	}

	protected abstract AbstractHolder selectHolder();

	private void needsRedraw() {
		_view = null;
	}
}

// - UNUSED CODE ............................................................................................
