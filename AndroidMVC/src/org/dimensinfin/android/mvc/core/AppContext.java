//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;

import android.app.Activity;
import android.app.Fragment;
import android.view.Menu;

// - CLASS IMPLEMENTATION ...................................................................................
public class AppContext implements Serializable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 2308283849532193647L;
	//	private static Logger			logger						= Logger.getLogger("AppContext");

	// - F I E L D - S E C T I O N ............................................................................
	private Activity					_activity					= null;
	private Fragment					_fragment					= null;
	private int								_state						= 0;
	private Menu							_appMenu					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public Activity getActivity() {
		return _activity;
	}

	public Menu getAppMenu() {
		return _appMenu;
	}

	public void setAppMenu(final Menu appMenu) {
		_appMenu = appMenu;
	}

	public void setCurrentActivity(final Activity act) {
		_activity = act;
		_state = 1;
	}

	public void setCurrentFragment(final Fragment frag) {
		_fragment = frag;
		_activity = _fragment.getActivity();
		_state = 2;
	}

	public void setStopFlag() {
		_state = 3;
	}
}

// - UNUSED CODE ............................................................................................
