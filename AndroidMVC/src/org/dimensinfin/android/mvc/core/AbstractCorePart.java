//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.core;

import org.dimensinfin.core.model.AbstractGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Generic Part that should be valid for all platforms. Will implements some features that are isolated from
 * Android or Spring libraries.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractCorePart extends AbstractEditPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 6597547532471711856L;
	//	static Logger				logger						= Logger.getLogger("AbstractCorePart");

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public AbstractCorePart() {
	//		super();
	//	}

	//	public AbstractCorePart(AbstractGEFNode model) {
	//		super(model);
	//	}

	//	public AbstractCorePart(RootNode model, IPartFactory factory) {
	//		super(model);
	//		_factory = factory;
	//	}

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

	public void setModel(final AbstractGEFNode model) {
		super.setModel(model);
		this.needsRedraw();
	}
}

// - UNUSED CODE
// ............................................................................................
