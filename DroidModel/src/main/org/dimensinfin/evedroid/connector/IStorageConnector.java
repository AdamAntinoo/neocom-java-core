//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.connector;

//- IMPORT SECTION .........................................................................................
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.market.MarketDataSet;
import org.dimensinfin.evemarket.model.TrackEntry;
import org.w3c.dom.Element;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IStorageConnector {

	// - M E T H O D - S E C T I O N ..........................................................................
	public File accessAppStorage(String resourceString);

	public Element accessDOMDocument(String url);

	public InputStream accessInternalStorage(String resourceString) throws IOException;

	public InputStream accessNetworkResource(String url) throws IOException;

	public boolean checkStorageResource(File base, String resourceString);

	public File getCacheStorage();

	public Vector<TrackEntry> parseMarketDataEMD(String itemName, EMarketSide opType);
	public Vector<TrackEntry> parseMarketDataEC(int itemid, EMarketSide opType);

	public MarketDataSet readDiskMarketData(int itemID, EMarketSide side);

	public void writeDiskMarketData(final MarketDataSet reference, final int itemID, EMarketSide side);
}

// - UNUSED CODE ............................................................................................
