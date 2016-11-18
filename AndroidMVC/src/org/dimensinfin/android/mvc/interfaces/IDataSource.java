//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.interfaces;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;

// - INTERFACE IMPLEMENTATION ...............................................................................
public interface IDataSource extends PropertyChangeListener {
	public void createContentHierarchy();

	public int getItemsCount();

	public ArrayList<AbstractAndroidPart> getPartHierarchy();

	//	public void processArguments(final Bundle arguments);
}
