//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.theme;

import org.dimensinfin.eveonline.neocom.core.EThemeTransparency;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

// - CLASS IMPLEMENTATION ...................................................................................
public interface ITheme {
	// - M E T H O D - S E C T I O N ..........................................................................
	public Drawable getThemeBackground();

	public Drawable getThemeDimmed(final EThemeTransparency level);

	public Typeface getThemeTextFont();

	public Drawable getThemeTransparent(EThemeTransparency level);
}

// - UNUSED CODE ............................................................................................
