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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.interfaces.INeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Generic Part that should be valid for all platforms. Will implements some features that are isolated from
 * Android or Spring libraries.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractCorePart extends AbstractEditPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long		serialVersionUID	= 6597547532471711856L;
	private static Logger				logger						= Logger.getLogger("org.dimensinfin.eveonline.neocom.isolation");

	// - F I E L D - S E C T I O N ............................................................................
	private IPartFactory				_factory					= null;
	private AbstractDataSource	_dataSource				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractCorePart() {
		super();
	}

	public AbstractCorePart(AbstractGEFNode model) {
		super(model);
	}

	public AbstractCorePart(RootNode model, IPartFactory factory) {
		super(model);
		_factory = factory;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * 
	 * @return <code>long</code> identifier with the model number.
	 */
	public abstract long getModelID();

	//	abstract public void invalidate();

	abstract public void needsRedraw();

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
		logger.info(">> [AbstractEditPart.refreshChildren]");
		int i;
		//		AbstractEditPart editPart;
		Object model;

		// Get the list of children for this Part.
		List selfChildren = getChildren();
		int size = selfChildren.size();
		// This field has the list of Parts pointed by their corresponding model.
		Map modelToEditPart = Collections.EMPTY_MAP;
		if (size > 0) {
			modelToEditPart = new HashMap(size);
			for (i = 0; i < size; i++) {
				AbstractEditPart editPart = (AbstractEditPart) selfChildren.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		// Get the list of model elements that collaborate to the Part model. This is the complex-simple model transformation.
		INeoComNode partModel = (INeoComNode) getModel();
		logger.info("-- [AbstractEditPart.refreshChildren]> partModel: " + partModel);
		ArrayList<AbstractComplexNode> modelObjects = partModel.collaborate2Model(getPartFactory().getVariant());
		logger.info("-- [AbstractEditPart.refreshChildren]> modelObjects: " + modelObjects);

		// Process the list of model children for this Part.
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			AbstractCorePart editPart = (AbstractCorePart) modelToEditPart.get(model);
			if ((i < selfChildren.size()) && (((IEditPart) selfChildren.get(i)).getModel() == model)) {
				// But in any case try to update all the children
				//				editPart = (AbstractEditPart) modelToEditPart.get(model);
				logger.info("-- [AbstractEditPart.refreshChildren]> model matches. Refreshing children.");
				if (editPart != null) editPart.refreshChildren();
				continue;
			}

			// Look to see if the EditPart is already around but in the wrong location
			//			editPart = (AbstractEditPart) modelToEditPart.get(model);

			if (editPart != null) {
				logger.info("-- [AbstractEditPart.refreshChildren]> model found but out of order.");
				reorderChild(editPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and insert one.
				editPart = (AbstractCorePart) createChild(model);
				logger.info("-- [AbstractEditPart.refreshChildren]> New Part: " + editPart);
				// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
				if (null != editPart) {
					addChild(editPart, i);
					editPart.refreshChildren();
				}
			}
		}

		// Remove the remaining EditParts
		size = selfChildren.size();
		if (i < size) {
			List trash = new ArrayList(size - i);
			for (; i < size; i++)
				trash.add(selfChildren.get(i));
			for (i = 0; i < trash.size(); i++) {
				IEditPart ep = (IEditPart) trash.get(i);
				removeChild(ep);
			}
		}
	}

	public AbstractCorePart setDataStore(final AbstractDataSource ds) {
		_dataSource = ds;
		return this;
	}

	public AbstractCorePart setFactory(final IPartFactory partFactory) {
		_factory = partFactory;
		return this;
	}

	public void setModel(final AbstractGEFNode model) {
		super.setModel(model);
		needsRedraw();
	}

	/**
	 * Create the child <code>Part</code> for the given model object. This method is called from
	 * {@link #refreshChildren()}.
	 * <P>
	 * By default, the implementation will delegate to the <code>EditPartViewer</code>'s {@link EditPartFactory}
	 * . Subclasses may override this method instead of using a Factory.
	 * 
	 * @param model
	 *          the Child model object
	 * @return The child EditPart
	 */
	protected IEditPart createChild(final IGEFNode model) {
		return getPartFactory().createPart(model);
	}

	/**
	 * The factory is set on the Root parts. Most of the other parts do not declare it or is not setup. To
	 * detect this problem and correct if if we detect the null we search for the parent until a factory is
	 * found.
	 * 
	 * @return
	 */
	protected IPartFactory getPartFactory() {
		if (null == _factory) {
			// Search at the parent 
			return ((AbstractCorePart) getParentPart()).getPartFactory();
		} else
			return _factory;
	}
}

// - UNUSED CODE
// ............................................................................................
