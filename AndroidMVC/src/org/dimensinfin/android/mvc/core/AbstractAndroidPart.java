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

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
// REFACTOR Change the class dependency to simplify once the code is stable.
/**
 * Core code for any Android Part. Will have enough code to deal with the model transformation, the model/view
 * access and the notifications and events mechanisms.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractAndroidPart extends AbstractCorePart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7467855028114565679L;
	private static Logger			logger						= Logger.getLogger("AbstractAndroidPart");

	// - F I E L D - S E C T I O N ............................................................................
	//	private IGEFNode					_model						= null;
	//	protected boolean					expanded					= false;
	protected boolean					downloaded				= false;
	protected int							renderMode				= 1000;
	protected Activity				_activity					= null;
	protected Fragment				_fragment					= null;
	private View							_view							= null;
	//	protected AbstractRender	_render						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractAndroidPart() {
		super();
	}

	public AbstractAndroidPart(final AbstractComplexNode model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Collapses a node from any state if had.
	 * 
	 * @return the final expend state.
	 */
	//	public boolean collapse() {
	//		expanded = false;
	//		return expanded;
	//	}
	public void addChild(final AbstractAndroidPart child) {
		children.add(child);
	}

	//	public void addChild(final IGEFNode child) {
	//		children.add((AbstractPropertyChanger) child);
	//	}
	public void addChild(final IEditPart child) {
		children.add((AbstractPropertyChanger) child);
	}

	public void clean() {
		children.clear();
	}

	public ArrayList<AbstractAndroidPart> collaborate2View() {
		return getPartChildren();
	}

	/**
	 * Only expand nodes that have children. This action also has to keep synchronized the recording of actions
	 * that are used to reproduce the state of a hierarchy.
	 * 
	 * @return the final expend state.
	 */
	//	public boolean expand() {
	//		if (getChildren().size() > 0)
	//			expanded = true;
	//		else
	//			expanded = false;
	//		return expanded;
	//	}
	/**
	 * On the new design the activity is present but not the Fragment. Check that.
	 * 
	 * @return
	 */
	public Activity getActivity() {
		if (null == _fragment)
			return _activity;
		else
			return getFragment().getActivity();
	}
	//	public Activity getActivity() {
	//		if (null != _activity)
	//			return _activity;
	//		else
	//			throw new RuntimeException("Activity object not available on access on a Part.");
	//	}

	//	public AbstractComplexNode getAndroidModel() {
	//		return (AbstractComplexNode) getModel();
	//	}

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
	//	public AbstractAndroidNode getCastedModel() {
	//		return (AbstractAndroidNode) getModel();
	//	}

	@Deprecated
	public AbstractHolder getHolder(final Fragment fragment) {
		_fragment = fragment;
		_activity = fragment.getActivity();
		return selectHolder();
	}

	//	public IGEFNode getModel() {
	//		return _model;
	//	}

	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * 
	 * @return <code>long</code> identifier with the model number.
	 */
	@Override
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
		Vector<AbstractPropertyChanger> ch = getChildren();
		for (Object node : ch) {
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
	//	public AbstractRender getRenderer(final Fragment fragment) {
	//		//REFACTOR This has to be updated to AbstractPager once the PagerFragment is completely deprecated.
	//		_fragment = fragment;
	//		// Save the render at the Part. This guarantees that this action is always performed.
	//		_render = (AbstractRender) selectHolder();
	//		//		_activity = fragment.getActivity();
	//		return _render;
	//	}

	public View getView() {
		return _view;
	}

	@Override
	public void invalidate() {
		if (null != _view) {
			_view.invalidate();
			needsRedraw();
		}
	}

	public boolean isExpanded() {
		// Check the model type.
		if (getModel() instanceof AbstractComplexNode) {
			return ((AbstractComplexNode) getModel()).isExpanded();
		} else
			return true;
	}

	@Override
	public void needsRedraw() {
		_view = null;
	}

	public void setDownloaded() {
		downloaded = true;
	}

	//	public boolean isExpanded() {
	//		return getModel().is;
	//	}

	public AbstractPropertyChanger setRenderMode(final int renderMode) {
		this.renderMode = renderMode;
		needsRedraw();
		return this;
	}

	//	/**
	//	 * Sets the expand to the value shown on the parameter. This may not toggle the real expand state. On nodes
	//	 * with no children has no impact because the node will fall back again to the collapsed state.
	//	 * 
	//	 * @param newExpanded
	//	 */
	//	public void setExpanded(final boolean newExpanded) {
	//		if (newExpanded)
	//			expand();
	//		else
	//			collapse();
	//	}

	//	public void setModel(final IGEFNode model) {
	//		_model = model;
	//		needsRedraw();
	//	}

	public void setView(final View convertView) {
		_view = convertView;
	}

	public void toggleExpanded() {
		if (getModel() instanceof AbstractComplexNode) {
			((AbstractComplexNode) getModel()).toggleExpanded();
		}
	}

	//	/**
	//	 * Tries to invert the expansion state of the node. Only expands nodes that have children and is not
	//	 * operative on the other nodes.<br>
	//	 * Since the addition of the view cache every change on the part content or model should trigger a change on
	//	 * the view. This can be achieved by removing the view from the cache.
	//	 */
	//	public void toggleExpanded() {
	//		if (!expanded)
	//			expand();
	//		else
	//			collapse();
	//		needsRedraw();
	//	}

	/**
	 * Create the Part for the model object received. We have then to have access to the Factory from the root
	 * element and all the other parts should have a reference to the root to be able to do the same.
	 */
	//		protected IEditPart createChild(final Object model) {
	//			IPartFactory factory = getRoot().getFactory();
	//			return factory.createPart( (IGEFNode) model);
	//		}
	@Override
	protected IEditPart createChild(final Object model) {
		IPartFactory factory = getRoot().getFactory();
		IEditPart part = factory.createPart((IGEFNode) model);
		// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
		if (null != part) {
			part.setParent(this);
		}
		return part;
	}

	//	public AbstractAndroidNode getModel() {
	//		return (AbstractAndroidNode) super.getModel();
	//	}
	@Override
	protected void removeChildVisual(final IEditPart child) {
		this.invalidate();
		this._view = null;
	}

	protected abstract AbstractHolder selectHolder();
}

// - UNUSED CODE ............................................................................................
