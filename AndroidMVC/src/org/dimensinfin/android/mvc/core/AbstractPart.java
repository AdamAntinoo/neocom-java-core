//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.core;

//- IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.RootNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractPart extends AbstractPropertyChanger implements IPart {
	public enum EPARTEVENT {
		ADD_CHILD, REMOVE_CHILD
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7601587036153405892L;
	public static Logger			logger						= Logger.getLogger("AbstractPart");
	static {
		// Register the event into the global event register
		CEventPart.register(EPARTEVENT.ADD_CHILD.hashCode(), EPARTEVENT.ADD_CHILD.name());
		CEventPart.register(EPARTEVENT.REMOVE_CHILD.hashCode(), EPARTEVENT.REMOVE_CHILD.name());
	}
	// - F I E L D - S E C T I O N ............................................................................
	private Vector<IPart>				children		= new Vector<IPart>();
	private AbstractComplexNode	model;
	private IPart								parent;
	/** Stores the user activation state. Usually becomes true when the users is interacting with the part. */
	private boolean							active			= true;
	private IPartFactory				_factory		= null;
	private AbstractDataSource	_dataSource	= null;
	protected int								renderMode	= 1000;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractPart() {
	}

	public AbstractPart(final AbstractComplexNode model) {
		this.model = model;
	}

	public AbstractPart(final RootNode model, final IPartFactory factory) {
		super(model);
		_factory = factory;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addChild(final IPart child) {
		children.add(child);
	}

	/**
	 * Adds a child <code>EditPart</code> to this EditPart. This method is called from
	 * {@link #refreshChildren()}. The following events occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #children} List, and its parent is set to <code>this</code>
	 * <LI><code>EditPartListeners</code> are notified that the child has been added.
	 * </OL>
	 * <P>
	 * 
	 * @param child
	 *          The <code>EditPart</code> to add
	 * @param index
	 *          The index
	 * @see #removeChild(IEditPart)
	 * @see #reorderChild(IEditPart,int)
	 */
	public void addChild(final IPart child, int index) {
		//		Assert.isNotNull(child);
		if (index == -1) index = this.getChildren().size();
		if (children == null) children = new Vector<IPart>(2);

		children.add(index, child);
		child.setParent(this);
		this.fireChildAdded(child, index);
	}

	public void clean() {
		children.clear();
	}

	/**
	 * By default until all parts are reviewed this method calls the original and now deprecated
	 * <code>getPartChildren</code> to it gets collected the part that conform the model. On the new design this
	 * comes from the model while in the old comes from the design structures but both are compatible.
	 */
	public ArrayList<IPart> collaborate2View() {
		ArrayList<IPart> result = new ArrayList<IPart>();
		// If the node is expanded then give the children the opportunity to also be added.
		if (this.isExpanded()) {
			// ---This is the section that is different for any Part. This should be done calling the list of policies.
			Vector<IPart> ch = this.runPolicies(this.getChildren());
			// --- End of policies
			for (IPart part : ch) {
				if (part.isRenderWhenEmpty()) result.add(part);
				result.addAll(part.collaborate2View());
			}
		}
		return result;
	}

	public Vector<IPart> getChildren() {
		if (children == null) return new Vector<IPart>(2);
		return children;
	}

	public AbstractComplexNode getModel() {
		return model;
	}

	public IPart getParentPart() {
		return parent;
	}

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grandchildren of this one. If the node is collapsed then the only
	 * result will be the node itself. <br>
	 * This method is being deprecated and replaced with the <code>collaborate2View</code>. The first change is
	 * to add myself only if not empty and the
	 * 
	 * @return list of parts that are accessible for this node.
	 */
	@Deprecated
	public ArrayList<IPart> getPartChildren() {
		return this.collaborate2View();
	}

	/**
	 * The factory is set on the Root parts. Most of the other parts do not declare it or is not setup. To
	 * detect this problem and correct if if we detect the null we search for the parent until a factory is
	 * found.
	 * 
	 * @return
	 */
	public IPartFactory getPartFactory() {
		if (null == _factory)
			// Search for the factory at the parent. 
			return this.getParentPart().getPartFactory();
		else
			return _factory;
	}

	public int getRenderMode() {
		return renderMode;
	}

	public RootPart getRoot() {
		if (this.getParentPart() == null) return null;
		return this.getParentPart().getRoot();
	}

	public boolean isActive() {
		return active;
	}

	public boolean isDownloaded() {
		return model.isDownloaded();
	}

	public boolean isExpanded() {
		return model.isExpanded();
	}

	public boolean isRenderWhenEmpty() {
		return model.isRenderWhenEmpty();
	}

	public void propertyChange(final PropertyChangeEvent evt) {
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
		AbstractPart.logger.info(">> [AbstractPart.refreshChildren]");
		//		int i=0;
		//		Object model;

		// Get the list of children for this Part.
		Vector<IPart> selfChildren = this.getChildren();
		int size = selfChildren.size();
		// This variable has the list of Parts pointed by their corresponding model.
		HashMap<AbstractComplexNode, IPart> modelToEditPart = new HashMap<AbstractComplexNode, IPart>(size + 1);
		if (size > 0) //			modelToEditPart = new HashMap<AbstractComplexNode, IPart>(size);
			for (int i = 0; i < size; i++) {
			IPart editPart = selfChildren.get(i);
			modelToEditPart.put(editPart.getModel(), editPart);
			}

		// Get the list of model elements that collaborate to the Part model. This is the complex-simple model transformation.
		INeoComNode partModel = (INeoComNode) this.getModel();
		AbstractPart.logger.info("-- [AbstractEditPart.refreshChildren]> partModel: " + partModel);
		ArrayList<AbstractComplexNode> modelObjects = partModel.collaborate2Model(this.getPartFactory().getVariant());
		AbstractPart.logger.info("-- [AbstractEditPart.refreshChildren]> modelObjects: " + modelObjects);

		// Process the list of model children for this Part.
		int i = 0;
		for (i = 0; i < modelObjects.size(); i++) {
			AbstractComplexNode nodemodel = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			IPart editPart = modelToEditPart.get(nodemodel);
			if ((i < selfChildren.size()) && (selfChildren.get(i).getModel() == nodemodel)) {
				// But in any case try to update all the children
				AbstractPart.logger.info("-- [AbstractEditPart.refreshChildren]> model matches. Refreshing children.");
				if (editPart != null) editPart.refreshChildren();
				continue;
			}

			// Look to see if the EditPart is already around but in the wrong location
			//			editPart = (AbstractEditPart) modelToEditPart.get(model);

			if (editPart != null) {
				AbstractPart.logger.info("-- [AbstractEditPart.refreshChildren]> model found but out of order.");
				this.reorderChild(editPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and insert one.
				editPart = this.createChild(nodemodel);
				AbstractPart.logger.info("-- [AbstractEditPart.refreshChildren]> New Part: " + editPart);
				// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
				if (null != editPart) {
					this.addChild(editPart, i);
					editPart.refreshChildren();
				}
			}
		}

		// Remove the remaining EditParts
		size = selfChildren.size();
		if (i < size) {
			Vector<IPart> trash = new Vector<IPart>(size - i);
			for (; i < size; i++)
				trash.add(selfChildren.get(i));
			for (i = 0; i < trash.size(); i++) {
				IPart ep = trash.get(i);
				this.removeChild(ep);
			}
		}
	}

	public abstract Vector<IPart> runPolicies(Vector<IPart> targets);

	public void setActive(final boolean active) {
		this.active = active;
	}

	public IPart setDataStore(final AbstractDataSource ds) {
		_dataSource = ds;
		return this;
	}

	public IPart setFactory(final IPartFactory partFactory) {
		_factory = partFactory;
		return this;
	}

	/**
	 * Set the primary model object that this EditPart represents. This method is used by an
	 * <code>EditPartFactory</code> when creating an EditPart.
	 */
	public void setModel(final AbstractComplexNode model) {
		this.model = model;
	}

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 */
	public void setParent(final IPart parent) {
		this.parent = parent;
	}

	public IPart setRenderMode(final int renderMode) {
		this.renderMode = renderMode;
		//		this.needsRedraw();
		return this;
	}

	/**
	 * Describes this EditPart for developmental debugging purposes.
	 * 
	 * @return a description
	 */
	@Override
	public String toString() {
		String c = this.getClass().getName();
		c = c.substring(c.lastIndexOf('.') + 1);
		return c + "( " + this.getModel() + " )";
	}

	/**
	 * Create the Part for the model object received. We have then to have access to the Factory from the root
	 * element and all the other parts should have a reference to the root to be able to do the same.
	 */
	protected IPart createChild(final AbstractComplexNode model) {
		IPartFactory factory = this.getRoot().getPartFactory();
		IPart part = factory.createPart(model);
		// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
		if (null != part) part.setParent(this);
		return part;
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
	protected void removeChild(final IPart child) {
		//		Assert.isNotNull(child);
		int index = this.getChildren().indexOf(child);
		if (index < 0) return;
		this.fireRemovingChild(child, index);
		child.setParent(null);
		this.getChildren().remove(child);
	}

	/**
	 * Moves a child <code>EditPart</code> into a lower index than it currently occupies. This method is called
	 * from {@link #refreshChildren()}.
	 * 
	 * @param editpart
	 *          the child being reordered
	 * @param index
	 *          new index for the child
	 */
	protected void reorderChild(final IPart editpart, final int index) {
		children.remove(editpart);
		children.add(index, editpart);
	}

	private void fireChildAdded(final IPart child, final int index) {
		this.fireStructureChange(CEventPart.getName4Event(EPARTEVENT.ADD_CHILD.hashCode()), child, index);
	}

	private void fireRemovingChild(final IPart child, final int index) {
		this.fireStructureChange(CEventPart.getName4Event(EPARTEVENT.REMOVE_CHILD.hashCode()), child, index);
	}
}

// - UNUSED CODE ............................................................................................
