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

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.core.model.RootNode;

// - INTERFACE IMPLEMENTATION ...............................................................................
public interface IDataSource extends PropertyChangeListener {
	// - M E T H O D - S E C T I O N ..........................................................................
	public void addPropertyChangeListener(final PropertyChangeListener newListener);

	public RootNode collaborate2Model();

	public void createContentHierarchy();

	public ArrayList<AbstractAndroidPart> getBodyParts();

	public ArrayList<AbstractAndroidPart> getHeaderParts();

	public int getItemsCount();
}
