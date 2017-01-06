//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
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
