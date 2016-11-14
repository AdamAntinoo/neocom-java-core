//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.core;

//- IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;

// - INTERFACE IMPLEMENTATION ...............................................................................
public interface IActivityMessageBus extends PropertyChangeListener {
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(int panelVariantReference);

	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(int panelVariantReference);
}

// - UNUSED CODE ............................................................................................
