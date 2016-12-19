//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.interfaces;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.core.model.AbstractComplexNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IPart extends PropertyChangeListener {
	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList<IPart> collaborate2View();

	public Vector<IPart> getChildren();

	public AbstractComplexNode getModel();

	public IPart getParentPart();

	public IPartFactory getPartFactory();

	public int getRenderMode();

	public RootPart getRoot();

	public boolean isActive();

	public boolean isDownloaded();

	public boolean isExpanded();

	public boolean isRenderWhenEmpty();

	public boolean isVisible();

	public void refreshChildren();

	public Vector<IPart> runPolicies(Vector<IPart> targets);

	public IPart setFactory(final IPartFactory partFactory);

	public void setModel(final AbstractComplexNode model);

	public void setParent(final IPart parent);

	public IPart setRenderMode(final int renderMode);
}

// - UNUSED CODE ............................................................................................
