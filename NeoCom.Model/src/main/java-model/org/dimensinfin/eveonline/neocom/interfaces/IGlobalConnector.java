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

import java.util.List;

import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;

/**
 * @author Adam Antinoo
 */
public interface IGlobalConnector {
	public INeoComDBHelper getNeocomDBHelper();

	public ISDEDBHelper getSDEDBHelper();

	public MarketDataSet searchMarketData( final int itemId, final EMarketSide side );

	public GetMarketsPrices200Ok searchMarketPrice( final int typeId );

	public int searchStationType( final long typeId );

	public EveItem searchItem4Id( final int typeId );

	public EveLocation searchLocation4Id( final long locationId );

	public List<ColonyStructure> downloadStructures4Colony( final int characterid, final int planetid );

	public int searchModule4Blueprint( final int bpitemID );
}
// - UNUSED CODE ............................................................................................
