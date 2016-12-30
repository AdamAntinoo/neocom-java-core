//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.theme;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EThemeTransparency;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

// - CLASS IMPLEMENTATION ...................................................................................
public class RubiconRedTheme implements ITheme {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static Logger		logger				= Logger.getLogger("RubiconRedTheme");
	private static Typeface	daysFace			= Typeface
			.createFromAsset(NeoComApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");

	// - F I E L D - S E C T I O N
	// ............................................................................
	private Resources				_resourceMgr	= null;

	// private Activity _activity = null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	// public RubiconRedTheme(final Activity activity) {
	// _activity = activity;
	// // _resourceMgr = activity.getResources();
	// }

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public Drawable getThemeBackground() {
		return this.getResourceManager().getDrawable(R.drawable.dimmedbackground60);
	}

	public Drawable getThemeDimmed(final EThemeTransparency level) {
		Drawable draw = this.getResourceManager().getDrawable(R.drawable.blacktraslucent80);
		if (level == EThemeTransparency.LOW) {
			draw = this.getResourceManager().getDrawable(R.drawable.blacktraslucent40);
		}
		return draw;
	}

	public Typeface getThemeTextFont() {
		return RubiconRedTheme.daysFace;
	}

	public Drawable getThemeTransparent(final EThemeTransparency level) {
		Drawable draw = this.getResourceManager().getDrawable(R.drawable.redtraslucent80);
		if (level == EThemeTransparency.VERYLOW) {
			draw = this.getResourceManager().getDrawable(R.drawable.defaultgreentraslucent);
		}
		if (level == EThemeTransparency.LOW) {
			draw = this.getResourceManager().getDrawable(R.drawable.selectedgreentraslucent);
		}
		if (level == EThemeTransparency.MEDIUM) {
			draw = this.getResourceManager().getDrawable(R.drawable.greentraslucent40);
		}
		if (level == EThemeTransparency.HIGH) {
			draw = this.getResourceManager().getDrawable(R.drawable.actiongreentraslucent);
		}
		return draw;
	}

	protected Resources getResourceManager() {
		if (null == _resourceMgr) {
			_resourceMgr = this.getActivity().getResources();
		}
		return _resourceMgr;
	}

	private Activity getActivity() {
		final Activity act = AppModelStore.getSingleton().getActivity();
		if (null == act)
			throw new RuntimeException("Bad initialization and theme usage. App activity lost or not defined.");
		return act;
	}
}

// - UNUSED CODE
// ............................................................................................
