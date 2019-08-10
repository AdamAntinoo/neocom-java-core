package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationCatalogService {
	private static Logger logger = LoggerFactory.getLogger(LocationCatalogService.class);
	private static Map<Long, EsiLocation> locationCache = new HashMap<Long, EsiLocation>();
	private ESIDataAdapter esiDataAdapter;
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystem;

	// - C A C H E   M A N A G E M E N T
	public void stopService() {
		this.writeLocationsDataCache();
		this.cleanLocationsCache();
	}

	public void cleanLocationsCache() {
		locationCache.clear();
	}

	public void readLocationsDataCache() {
		logger.info(">> [LocationCatalogService.readLocationsDataCache]");
		final String cacheFileName = this.configurationProvider.getResourceString("R.cache.directorypath") +
				                             this.configurationProvider.getResourceString("R.cache.locationscache.filename");
		logger.info("-- [LocationCatalogService.readLocationsDataCache]> Opening cache file: {}", cacheFileName);
		try (final BufferedInputStream buffer = new BufferedInputStream(this.fileSystem.openResource4Input(cacheFileName));
		     final ObjectInputStream input = new ObjectInputStream(buffer)) {
//			try {
			synchronized (locationCache) {
				locationCache = (HashMap<Long, EsiLocation>) input.readObject();
				logger.info("-- [LocationCatalogService.readLocationsDataCache]> Restored cache Locations: {} entries.",
				            locationCache.size());
			}
//			} finally {
//			}
		} catch (final ClassNotFoundException ex) {
			logger.warn("W> [LocationCatalogService.readLocationsDataCache]> ClassNotFoundException. {}",
			            ex.getMessage());
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [LocationCatalogService.readLocationsDataCache]> FileNotFoundException. {}",
			            fnfe.getMessage());
		} catch (final IOException ioe) {
			logger.warn("W> [LocationCatalogService.readLocationsDataCache]> IOException. {}", ioe.getMessage());
		} catch (final IllegalArgumentException iae) {
			logger.warn("W> [LocationCatalogService.readLocationsDataCache]> IllegalArgumentException. {}",
			            iae.getMessage());
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		} finally {
//			input.close();
//			buffer.close();
			logger.info("<< [LocationCatalogService.readLocationsDataCache]");
		}
	}

	public void writeLocationsDataCache() {
		logger.info(">> [LocationCatalogService.writeLocationsDataCache]");
		final String cacheFileName = this.configurationProvider.getResourceString("R.cache.directorypath") +
				                             this.configurationProvider.getResourceString("R.cache.locationscache.filename");
		logger.info("-- [LocationCatalogService.writeLocationsDataCache]> Opening cache file: {}", cacheFileName);
		try (final BufferedOutputStream buffer = new BufferedOutputStream(this.fileSystem.openResource4Output(cacheFileName));
		     final ObjectOutput output = new ObjectOutputStream(buffer)) {
			synchronized (locationCache) {
				output.writeObject(locationCache);
				logger.info("-- [LocationCatalogService.writeLocationsDataCache]> Wrote Locations cache: {} entries.",
				            locationCache.size());
			}
		} catch (final FileNotFoundException fnfe) {
			logger.warn("W> [LocationCatalogService.writeLocationsDataCache]> FileNotFoundException.");
		} catch (final IOException ex) {
			logger.warn("W> [LocationCatalogService.writeLocationsDataCache]> IOException.");
		} finally {
			logger.info("<< [LocationCatalogService.writeLocationsDataCache]");
		}
	}

	private void registerOnScheduler() {
		// TODO - register this on the future scheduler
	}

	public EsiLocation searchLocation4Id( final long locationId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (locationCache.containsKey(locationId)) {
			// Account for a hit on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
			return locationCache.get(locationId);
		} else {
			final EsiLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
			// Add the hit to the cache but only when it is not UNKNOWN.
			if (hit.getClassType() != LocationClass.UNKNOWN) locationCache.put(locationId, hit);
			// Account for a miss on the cache.
			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {} found at database.",
			            locationId);
			return hit;
		}
	}



	
	//		public EsiLocation searchLocation4Id( final long locationId ) {
//		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
//		if (locationCache.containsKey(locationId)) {
//			// Account for a hit on the cache.
//			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
//			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
//			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
//			return locationCache.get(locationId);
//		} else {
//			final EsiLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
//			// Add the hit to the cache but only when it is not UNKNOWN.
//			if (hit.getTypeId() != LocationClass.UNKNOWN) locationCache.put(locationId, hit);
//			// Account for a miss on the cache.
//			int access = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
//			int hits = GlobalDataManager.getSingleton().getSDEDBHelper().locationsCacheStatistics.getHits();
//			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {} found at database.",
//					locationId);
//			return hit;
//		}
//	}
// - B U I L D E R
	public static class Builder {
		private LocationCatalogService onConstruction;

		public Builder() {
			this.onConstruction = new LocationCatalogService();
		}

		public Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withFileSystem( final IFileSystem fileSystem ) {
			this.onConstruction.fileSystem = fileSystem;
			return this;
		}

		public LocationCatalogService build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			Objects.requireNonNull(this.onConstruction.configurationProvider);
			Objects.requireNonNull(this.onConstruction.fileSystem);
			this.onConstruction.readLocationsDataCache(); // Load the cache from the storage.
			this.onConstruction.registerOnScheduler(); // register on scheduler to update storage every some minutes
			return this.onConstruction;
		}
	}
}