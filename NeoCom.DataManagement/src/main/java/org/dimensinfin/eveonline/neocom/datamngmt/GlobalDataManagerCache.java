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
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerCache extends SDEExternalDataManager{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerCache");

	// --- C A C H E   S T O R A G E   S E C T I O N
	private static final Hashtable<ECacheTimes, Long> ESICacheTimes = new Hashtable();
	private static final long DEFAULT_CACHE_TIME = 600 * 1000;

	public enum ECacheTimes {
		CHARACTER_PUBLIC, CHARACTER_CLONES
		, PLANETARY_INTERACTION_PLANETS, PLANETARY_INTERACTION_STRUCTURES
		, ASSETS_ASSETS, CORPORATION_CUSTOM_OFFICES, UNIVERSE_SCHEMATICS, MARKET_PRICES
		, INDUSTRY_JOBS
	}

	static {
		ESICacheTimes.put(ECacheTimes.CHARACTER_PUBLIC, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.CHARACTER_CLONES, TimeUnit.SECONDS.toMillis(200));
		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_PLANETS, TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_STRUCTURES, TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.ASSETS_ASSETS, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.MARKET_PRICES, TimeUnit.SECONDS.toMillis(3600));
		ESICacheTimes.put(ECacheTimes.INDUSTRY_JOBS, TimeUnit.SECONDS.toMillis(300));
	}

	public static long getCacheTime4Type( final ECacheTimes selector ) {
		final Long hit = ESICacheTimes.get(selector);
		if (null == hit) return DEFAULT_CACHE_TIME;
		else return hit.longValue();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
