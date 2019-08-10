//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.interfaces;

import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.industry.InventoryFlag;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Adam Antinoo
 */
public interface IGlobalConnector {
	// --- H E L P E R S
	public INeoComDBHelper getNeocomDBHelper();

	public ISDEDBHelper getSDEDBHelper();

	// --- M A R K E T   D A T A
	public Future<MarketDataSet> searchMarketData( final int itemId, final EMarketSide side );

	public GetMarketsPrices200Ok searchMarketPrice( final int typeId );

	// --- C O L O N Y
	public List<ColonyStructure> downloadStructures4Colony( final int characterid, final int planetid );

	// --- S D E   S E A R C H S
	public int searchStationType( final long typeId );

	public EveItem searchItem4Id( final int typeId );

//	public EveLocation searchLocation4Id( final long locationId );

	public int searchModule4Blueprint( final int bpitemId );

	public InventoryFlag searchFlag4Id( final int identifier );

	// --- C O N F I G U R A T I O N
	public String getResourcePropertyString( final String key );

	public Integer getResourcePropertyInteger( final String key );

//	public String getEveOnlineServerDatasource();

	// --- M U L T Y T H R E A D I N G
//	public Future<?> submitJob2ui( final Runnable task );

	// --- F I L E S Y S T E M
	//	public String accessAssetPath( final String path );
	//
	//	public InputStream openAsset4Input( final String filePath ) throws IOException;
	//
	//	public File accessStorageResourcePath( final String path );
}
// - UNUSED CODE ............................................................................................
