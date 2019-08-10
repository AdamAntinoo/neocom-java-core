package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

public class LocationCatalogService {
		public EveLocation searchLocation4Id( final long locationId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (locationCache.containsKey(locationId)) {
			// Account for a hit on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
			return locationCache.get(locationId);
		} else {
			final EveLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
			// Add the hit to the cache but only when it is not UNKNOWN.
			if (hit.getTypeId() != ELocationType.UNKNOWN) locationCache.put(locationId, hit);
			// Account for a miss on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {} found at database.",
					locationId);
			return hit;
		}
	}
	// - B U I L D E R
	public static class Builder {
		private LocationCatalogService onConstruction;

		public Builder() {
			this.onConstruction = new LocationCatalogService();
		}

		public LocationCatalogService build() {
			return this.onConstruction;
		}
	}
}