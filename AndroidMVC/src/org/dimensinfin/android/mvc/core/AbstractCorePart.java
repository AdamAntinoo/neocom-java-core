//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.core;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;

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

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * 
	 * @return <code>long</code> identifier with the model number.
	 */
	public abstract long getModelID();

	abstract public void invalidate();

	abstract public void needsRedraw();

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

	private IPartFactory getPartFactory() {
		return _factory;
	}
}

// - UNUSED CODE
// ............................................................................................
