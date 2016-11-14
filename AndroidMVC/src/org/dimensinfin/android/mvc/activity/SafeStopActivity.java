//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.activity;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

//- CLASS IMPLEMENTATION ...................................................................................
public class SafeStopActivity extends Activity {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safestop);
		TextView userMessage = (TextView) findViewById(R.id.userStopMessage);
		Intent intent = getIntent();
		String message = intent.getStringExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE);
		userMessage.setText(message);
	}
}
//- UNUSED CODE ............................................................................................
