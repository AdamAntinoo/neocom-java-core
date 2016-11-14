//  PROJECT:        EveDroid
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.core;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;

import org.dimensinfin.evedroid.service.PendingRequestEntry;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
public interface ICache {

	// - M E T H O D - S E C T I O N ..........................................................................
	//public void addAssetDownloadRequest(final long requestLocalizer);

	public void addCharacterUpdateRequest(long characterID);

	public void addDrawableToCache(String key, Drawable image);

	public void addMarketDataRequest(long requestLocalizer);

	public void clearPendingRequest(final String localizer);

	public Drawable getCacheDrawable(String link, ImageView targetIcon);

	public Vector<PendingRequestEntry> getPendingRequests();

	public String getURLForItem(int typeID);

	public String getURLForStation(int typeID);

}

// - UNUSED CODE ............................................................................................
