//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.interfaces;

import android.content.Context;
import android.content.Intent;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComDirector extends IDirector {

	public abstract Context getActivity();

	public abstract int getIconReferenceActive();

	public abstract int getIconReferenceInactive();

	public abstract String getName();

	public abstract void startActivity(Intent intent);
}

// - UNUSED CODE ............................................................................................
