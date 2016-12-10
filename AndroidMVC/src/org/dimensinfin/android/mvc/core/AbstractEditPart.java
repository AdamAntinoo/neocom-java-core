/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.dimensinfin.android.mvc.core;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.core.INeoComNode;

/**
 * The baseline implementation for the {@link IEditPart} interface.
 * <P>
 * Since this is the default implementation of an interface, this document deals with proper sub-classing of
 * this implementation. This class is not the API. For documentation on proper usage of the public API, see
 * the documentation for the interface itself: {@link IEditPart}.
 * <P>
 * This class assumes no visual representation. Subclasses {@link AbstractGraphicalEditPart} and
 * {@link AbstractTreeEditPart} add support for {@link org.eclipse.draw2d.IFigure Figures} and
 * {@link org.eclipse.swt.widgets.TreeItem TreeItems} respectively.
 * <P>
 * AbstractEditPart provides support for children. All AbstractEditPart's can potentially be containers for
 * other EditParts.
 */
public abstract class AbstractEditPart extends AbstractPropertyChanger implements IEditPart {
	private static final long									serialVersionUID	= 4934501535877351327L;
	public static Logger											logger						= Logger.getLogger("AbstractEditPart");
	/**
	 * This flag is set during {@link #activate()}, and reset on {@link #deactivate()}
	 */
	protected static final int								FLAG_ACTIVE				= 1;
	/**
	 * This flag indicates that the EditPart has focus.
	 */
	protected static final int								FLAG_FOCUS				= 2;

	/**
	 * The left-most bit that is reserved by this class for setting flags. Subclasses may define additional
	 * flags starting at <code>(MAX_FLAG << 1)</code>.
	 */
	protected static final int								MAX_FLAG					= FLAG_FOCUS;

	private IGEFNode													model;
	private int																flags;
	private IEditPart													parent;
	private int																selected;

	/**
	 * The List of children EditParts
	 */
	protected Vector<AbstractPropertyChanger>	children					= new Vector<AbstractPropertyChanger>();

	public AbstractEditPart() {
	}

	public AbstractEditPart(IGEFNode model) {
		this.model = model;
	}

	public AbstractEditPart(RootNode node) {
		this.model = node;
	}

	public void addChild(final AbstractPropertyChanger child) {
		children.add(child);
	}

	/**
	 * Adds a child <code>EditPart</code> to this EditPart. This method is called from
	 * {@link #refreshChildren()}. The following events occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #children} List, and its parent is set to <code>this</code>
	 * <LI>{@link #addChildVisual(IEditPart, int)} is called to add the child's visual
	 * <LI>{@link IEditPart#addNotify()} is called on the child.
	 * <LI><code>activate()</code> is called if this part is active
	 * <LI><code>EditPartListeners</code> are notified that the child has been added.
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #addChildVisual(IEditPart, int)}.
	 * 
	 * @param child
	 *          The <code>EditPart</code> to add
	 * @param index
	 *          The index
	 * @see #addChildVisual(IEditPart, int)
	 * @see #removeChild(IEditPart)
	 * @see #reorderChild(IEditPart,int)
	 */
	public void addChild(final AbstractPropertyChanger child, int index) {
		//		Assert.isNotNull(child);
		if (index == -1) index = getChildren().size();
		if (children == null) children = new Vector<AbstractPropertyChanger>(2);

		children.add(index, child);
		((IEditPart) child).setParent(this);
		//		addChildVisual(child, index);
		//		child.addNotify();

		//		if (isActive()) child.activate();
		//		fireChildAdded(child, index);
	}

	/**
	 * @return
	 * @see org.dimensinfin.android.mvc.core.IEditPart.gef.EditPart#getChildren()
	 */
	public Vector<AbstractPropertyChanger> getChildren() {
		if (children == null) return (Vector<AbstractPropertyChanger>) Collections.EMPTY_LIST;
		return children;
	}

	/**
	 * @see org.dimensinfin.android.mvc.core.IEditPart.gef.EditPart#getModel()
	 */
	public IGEFNode getModel() {
		return model;
	}

	/**
	 * @see org.dimensinfin.android.mvc.core.IEditPart.gef.EditPart#getParent()
	 */
	public IEditPart getParentPart() {
		return parent;
	}

	/**
	 * @see org.dimensinfin.android.mvc.core.IEditPart.gef.EditPart#getRoot()
	 */
	public RootPart getRoot() {
		if (getParentPart() == null) return null;
		return getParentPart().getRoot();
	}

	/**
	 * @return <code>true</code> if this EditPart is active.
	 */
	public boolean isActive() {
		return getFlag(FLAG_ACTIVE);
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * Updates the set of children EditParts so that it is in sync with the model children. This method is
	 * called from {@link #refresh()}, and may also be called in response to notification from the model. This
	 * method requires linear time to complete. Clients should call this method as few times as possible.
	 * Consider also calling {@link #removeChild(IEditPart)} and {@link #addChild(IEditPart, int)} which run in
	 * constant time.
	 * <P>
	 * The update is performed by comparing the existing EditParts with the set of model children returned from
	 * {@link #getModelChildren()}. EditParts whose models no longer exist are {@link #removeChild(IEditPart)
	 * removed}. New models have their EditParts {@link #createChild(Object) created}.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 * 
	 * @see #getModelChildren()
	 */
	public void refreshChildren() {
		logger.info(">> AbstractEditPart.refreshChildren");
		int i;
		AbstractEditPart editPart;
		Object model;

		List children = getChildren();
		int size = children.size();
		Map modelToEditPart = Collections.EMPTY_MAP;
		if (size > 0) {
			modelToEditPart = new HashMap(size);
			for (i = 0; i < size; i++) {
				editPart = (AbstractEditPart) children.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		// Get the list of model elements that collaborate to the Part model. This is the complex-simple model transformation.
		INeoComNode partModel = (INeoComNode) getModel();
		ArrayList<AbstractComplexNode> modelObjects = partModel.collaborate2Model("DEFAULT");
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if ((i < children.size()) && (((IEditPart) children.get(i)).getModel() == model)) continue;

			// Look to see if the EditPart is already around but in the wrong location
			editPart = (AbstractEditPart) modelToEditPart.get(model);

			if (editPart != null)
				reorderChild(editPart, i);
			else {
				// An EditPart for this model doesn't exist yet. Create and
				// insert one.
				editPart = (AbstractEditPart) createChild(model);
				// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
				if (null != editPart) {
					editPart.refreshChildren();
					addChild(editPart, i);
				}
			}
		}

		// Remove the remaining EditParts
		size = children.size();
		if (i < size) {
			List trash = new ArrayList(size - i);
			for (; i < size; i++)
				trash.add(children.get(i));
			for (i = 0; i < trash.size(); i++) {
				IEditPart ep = (IEditPart) trash.get(i);
				removeChild(ep);
			}
		}
	}

	/**
	 * Set the primary model object that this EditPart represents. This method is used by an
	 * <code>EditPartFactory</code> when creating an EditPart.
	 * 
	 * @see IEditPart#setModel(Object)
	 */
	public void setModel(final Object model) {
		this.model = (IGEFNode) model;
	}

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 * 
	 * @see IEditPart#setParent(IEditPart)
	 */
	public void setParent(final IEditPart parent) {
		this.parent = parent;
	}

	/**
	 * Describes this EditPart for developmental debugging purposes.
	 * 
	 * @return a description
	 */
	@Override
	public String toString() {
		String c = getClass().getName();
		c = c.substring(c.lastIndexOf('.') + 1);
		return c + "( " + getModel() + " )";//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * Returns a <code>List</code> containing the children model objects. If this EditPart's model is a
	 * container, this method should be overridden to returns its children. This is what causes children
	 * EditParts to be created.
	 * <P>
	 * Callers must not modify the returned List. Must not return <code>null</code>.
	 * 
	 * @return the List of children
	 */
	protected List<IGEFNode> collaborate2Model() {
		return Collections.EMPTY_LIST;
	}

	//	/**
	//	 * Creates the initial EditPolicies and/or reserves slots for dynamic ones. Should be implemented to install
	//	 * the inital EditPolicies based on the model's initial state. <code>null</code> can be used to reserve a
	//	 * "slot", should there be some desire to guarantee the ordering of EditPolcies.
	//	 * 
	//	 * @see IEditPart#installEditPolicy(Object, EditPolicy)
	//	 */
	//	protected abstract void createEditPolicies();
	//
	//	/**
	//	 * Subclasses should extend this method to handle Requests. For now, the default implementation does not
	//	 * handle any requests.
	//	 * 
	//	 * @see EditPart#performRequest(Request)
	//	 */
	//	//	public void performRequest(Request req) {
	//	//	}
	//
	//	/**
	//	 * This method will log a message to GEF's trace/debug system if the corresponding flag for EditParts is set
	//	 * to true.
	//	 * 
	//	 * @param message
	//	 *          a debug message
	//	 * @deprecated in 3.1
	//	 */
	//	@Deprecated
	//	protected final void debug(final String message) {
	//	}
	//
	//	/**
	//	 * This method will log the message to GEF's trace/debug system if the corrseponding flag for FEEDBACK is
	//	 * set to true.
	//	 * 
	//	 * @param message
	//	 *          Message to be passed
	//	 * @deprecated in 3.1
	//	 */
	//	@Deprecated
	//	protected final void debugFeedback(final String message) {
	//	}

	/**
	 * Create the child <code>EditPart</code> for the given model object. This method is called from
	 * {@link #refreshChildren()}.
	 * <P>
	 * By default, the implementation will delegate to the <code>EditPartViewer</code>'s {@link EditPartFactory}
	 * . Subclasses may override this method instead of using a Factory.
	 * 
	 * @param model
	 *          the Child model object
	 * @return The child EditPart
	 */
	// REFACTOR maybe this method should be reimplemented to allow the dynamic creation of childs from inside the part
	//	protected IEditPart createChild(final Object model) {
	//		return getEditPartFactory().createPart(this, model);
	//	}
	protected abstract IEditPart createChild(final Object model);
	//	public abstract RootPart getRoot() ;

	/**
	 * Returns the boolean value of the given flag. Specifically, returns <code>true</code> if the bitwise AND
	 * of the specified flag and the internal flags field is non-zero.
	 * 
	 * @param flag
	 *          Bitmask indicating which flag to return
	 * @return the requested flag's value
	 * @see #setFlag(int,boolean)
	 */
	protected final boolean getFlag(final int flag) {
		return (flags & flag) != 0;
	}

	/**
	 * Removes a child <code>EditPart</code>. This method is called from {@link #refreshChildren()}. The
	 * following events occur in the order listed:
	 * <OL>
	 * <LI><code>EditPartListeners</code> are notified that the child is being removed
	 * <LI><code>deactivate()</code> is called if the child is active
	 * <LI>{@link IEditPart#removeNotify()} is called on the child.
	 * <LI>{@link #removeChildVisual(IEditPart)} is called to remove the child's visual object.
	 * <LI>The child's parent is set to <code>null</code>
	 * </OL>
	 * <P>
	 * Subclasses should implement {@link #removeChildVisual(IEditPart)}.
	 * 
	 * @param child
	 *          EditPart being removed
	 * @see #addChild(IEditPart,int)
	 */
	protected void removeChild(final IEditPart child) {
		//		Assert.isNotNull(child);
		int index = getChildren().indexOf(child);
		if (index < 0) return;
		//		fireRemovingChild(child, index);
		//		if (isActive()) child.deactivate();
		//		child.removeNotify();
		removeChildVisual(child);
		child.setParent(null);
		getChildren().remove(child);
	}

	/**
	 * Removes the childs visual from this EditPart's visual. Subclasses should implement this method to support
	 * the visual type they introduce, such as Figures or TreeItems.
	 * 
	 * @param child
	 *          the child EditPart
	 */
	protected abstract void removeChildVisual(IEditPart child);

	/**
	 * Moves a child <code>EditPart</code> into a lower index than it currently occupies. This method is called
	 * from {@link #refreshChildren()}.
	 * 
	 * @param editpart
	 *          the child being reordered
	 * @param index
	 *          new index for the child
	 */
	protected void reorderChild(final IEditPart editpart, final int index) {
		removeChildVisual(editpart);
		List children = getChildren();
		children.remove(editpart);
		children.add(index, editpart);
		//		addChildVisual(editpart, index);
	}

	/**
	 * Sets the value of the specified flag. Flag values are decalared as static constants. Subclasses may
	 * define additional constants above {@link #MAX_FLAG}.
	 * 
	 * @param flag
	 *          Flag being set
	 * @param value
	 *          Value of the flag to be set
	 * @see #getFlag(int)
	 */
	protected final void setFlag(final int flag, final boolean value) {
		if (value)
			flags |= flag;
		else
			flags &= ~flag;
	}
}
