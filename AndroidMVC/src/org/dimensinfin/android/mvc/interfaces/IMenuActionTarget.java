//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.interfaces;

//- IMPORT SECTION .........................................................................................
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IMenuActionTarget {
	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean onContextItemSelected(final MenuItem item);

	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo);
}
