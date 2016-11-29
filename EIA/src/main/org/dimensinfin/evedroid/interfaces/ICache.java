//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.interfaces;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;

import org.dimensinfin.evedroid.core.ERequestClass;
import org.dimensinfin.evedroid.service.PendingRequestEntry;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
public interface ICache {

	// - M E T H O D - S E C T I O N ..........................................................................
	//public void addAssetDownloadRequest(final long requestLocalizer);

	public void addCharacterUpdateRequest(long characterID);

	public void addDrawableToCache(String key, Drawable image);

	public void addLocationUpdateRequest(ERequestClass locationClass);

	public void addMarketDataRequest(long requestLocalizer);

	public void clearPendingRequest(final String localizer);

	public Drawable getCacheDrawable(String link, ImageView targetIcon);

	public Vector<PendingRequestEntry> getPendingRequests();

	public String getURLForItem(int typeID);

	public String getURLForStation(int typeID);

}

// - UNUSED CODE ............................................................................................
