//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.activity;

// - IMPORT SECTION .........................................................................................
import android.app.Fragment;

// - CLASS IMPLEMENTATION ...................................................................................
public class TitledFragment extends Fragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String	_title		= "<TITLE>";
	private String	_subtitle	= "";

	// - U I    F I E L D S

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getSubtitle() {
		return _subtitle;
	}

	public String getTitle() {
		return _title;
	}

	public void setSubtitle(final String subtitle) {
		_subtitle = subtitle;
	}

	public void setTitle(final String title) {
		_title = title;
	}
}

// - UNUSED CODE ............................................................................................
