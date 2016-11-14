//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.activity;

// - IMPORT SECTION .........................................................................................
import android.app.DialogFragment;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IDialogListener {
	// - M E T H O D - S E C T I O N ..........................................................................
	public void onDialogNegativeClick(DialogFragment dialog);

	public void onDialogPositiveClick(DialogFragment dialog);
}
