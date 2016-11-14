//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.core;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class RootPart extends AbstractEditPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -8085543451527813221L;
	private static Logger logger = Logger.getLogger("SeparatorPart");
	private IPartFactory _factory=null;

	// - F I E L D - S E C T I O N ............................................................................
	// private AbstractPilotBasedActivity activity = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RootPart(final RootNode node,IPartFactory factory) {
		super(node);
	//	setModel(node);
		_factory=factory;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	protected List<IGEFNode> collaborate2Model() {
		Vector<IGEFNode> c = getModel().getChildren();
		return c;
	}
/**
 * This method is the one that will run over the part and its children to detect what elements (Parts) go to the result
 * list and from it to the ListView to be presented on the display.
 * @return
 */
	@SuppressWarnings("deprecation")
	// REFACTOR Some lines were commented out to compile the unit. Pending work.
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		ArrayList<AbstractAndroidPart> childrenParts = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> v = getChildren();
		for (AbstractPropertyChanger node : v) {
			childrenParts.add((AbstractAndroidPart) node);
		}
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>(childrenParts.size());
		for (AbstractAndroidPart child : childrenParts)
			if (child instanceof AbstractAndroidPart) {
				AbstractAndroidPart node = child;
//				result.add(child);
				result.addAll(node.collaborate2View());
//				for (AbstractPropertyChanger candidate : node.collaborate2View()) {
//					result.add((AbstractAndroidPart) candidate);
//				}
			}
		return result;
	}
/**
 * Create the Part for the model object received. We have then to have access to the Factory from the root element
 * and all the other parts should have a reference to the root to be able to do the same.
 */
	protected IEditPart createChild(final Object model) {
		IPartFactory factory = getRoot().getFactory();
		IEditPart part = factory.createPart( (IGEFNode) model);
		part.setParent(this);
		return part;
	}
protected IPartFactory getFactory() {
	return _factory;
}

public RootPart getRoot() {
	return  this;
}
/**
 * A RooPart has no visuals, but other Parts may have.
 */
	protected void removeChildVisual(final IEditPart child) {
	}


	// public String get_title() {
	// return getCastedModel().getTitle();
	// }
	//
	// public Separator getCastedModel() {
	// return (Separator) getModel();
	// }
	//
	// @Override
	// public long getModelID() {
	// return GregorianCalendar.getInstance().getTimeInMillis();
	// }
	//
	// @Override
	// protected AbstractHolder selectHolder() {
	// // Get the proper holder from the render mode.
	// return new SeparatorHolder(this, getActivity());
	// }

}

// - UNUSED CODE
// ............................................................................................
