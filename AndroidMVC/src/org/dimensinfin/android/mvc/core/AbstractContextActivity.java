//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import android.app.Activity;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This abstract class with focus on making compatible the use of a global structure to record the application
 * location and the parameters used to keep track of the current selection and deep progress. Instead of using
 * the Android save/restore mechanics that is still generating exceptions on some cases and heavy class
 * dependencies, this class will create the connections to allow access to that data if the Activity required
 * it.<br>
 * There is a global AppContext class that has to be extended by every application to add to it any specific
 * functionality that is desired to add by each application that uses this model.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractContextActivity extends Activity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger			= Logger.getLogger("AbstractContextActivity");
	private static AppContext	appContext	= new AppContext();

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	protected void onStart() {
		super.onStart();
		appContext.setCurrentActivity(this);
	}

	@Override
	protected void onStop() {
		appContext.setStopFlag();
		super.onStop();
	}
}

// - UNUSED CODE ............................................................................................
