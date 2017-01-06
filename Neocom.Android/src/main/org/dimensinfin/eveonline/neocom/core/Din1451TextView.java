//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.core;

// - IMPORT SECTION .........................................................................................
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Din1451TextView extends TextView {
	// - S T A T I C - S E C T I O N ..........................................................................
	protected static Typeface	typefaceName	= null;

	// - F I E L D - S E C T I O N ............................................................................

	public Din1451TextView(final Context context) {
		super(context);
		init(null);
	}

	public Din1451TextView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Din1451TextView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	private void init(final AttributeSet attrs) {
		if (!isInEditMode()) {
			typefaceName = Typeface.createFromAsset(getContext().getAssets(), "fonts/alte-din-1451-mittelschrift.regular.ttf");
			setTypeface(typefaceName);
		}
	}
}

// - UNUSED CODE ............................................................................................
