package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationCatalogService {
	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	private static Logger logger = LoggerFactory.getLogger(LocationCatalogService.class);
	private static Map<Long, EsiLocation> locationCache = new HashMap<Long, EsiLocation>();
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystem;
	private ISDEDatabaseAdapter sdeDatabaseAdapter;
	private LocationRepository locationRepository;
	private Map<String, Integer> locationTypeCounters = new HashMap<>();
	private boolean dirtyCache = false;
	private LocationCacheAccessType lastLocationAccess = LocationCacheAccessType.NOT_FOUND;

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
		final String cacheFileName = this.configurationProvider.getResourceString("P.cache.directory.path") +
				                             this.configurationProvider.getResourceString("P.cache.locationscache.filename");
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
		if (this.dirtyCache) {
			final String cacheFileName = this.configurationProvider.getResourceString("P.cache.directory.path") +
					                             this.configurationProvider.getResourceString("P.cache.locationscache.filename");
			logger.info("-- [LocationCatalogService.writeLocationsDataCache]> Opening cache file: {}", cacheFileName);
			try (final BufferedOutputStream buffer = new BufferedOutputStream(this.fileSystem.openResource4Output(cacheFileName));
			     final ObjectOutput output = new ObjectOutputStream(buffer)) {
				synchronized (locationCache) {
					output.writeObject(locationCache);
					dirtyCache = false;
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
	}

	public void verifySDERepository() {
		locationTypeCounters = this.locationRepository.getCounters();
	}

	public EsiLocation searchLocation4Id( final long locationId ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey(locationId)) return this.searchOnMemoryCache(locationId);
		final int access = locationsCacheStatistics.accountAccess(false);
		EsiLocation hit = this.searchOnRepository(locationId);
		final int hits = locationsCacheStatistics.getHits();
		if (null == hit) {
			hit = this.buildUpLocation(locationId);
			this.lastLocationAccess = LocationCacheAccessType.GENERATED;
			this.storeOnCacheLocation(hit);
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-{}/{} ] Location {} generated from SDE data.",
			            hits, access, locationId);
			return hit;
		}
		logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-{}/{} ] Location {} found at database.",
		            hits, access, locationId);
//		final EsiLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
		// Add the hit to the cache but only when it is not UNKNOWN.
//		if (hit.getClassType() != LocationClass.UNKNOWN) locationCache.put(locationId, hit);
		// Account for a miss on the cache.
		return hit;
	}

	public LocationCacheAccessType lastSearchLocationAccessType() {
		return this.lastLocationAccess;
	}

	private EsiLocation searchOnMemoryCache( final long locationId ) {
		int access = locationsCacheStatistics.accountAccess(true);
		this.lastLocationAccess = LocationCacheAccessType.MEMORY_ACCESS;
		int hits = locationsCacheStatistics.getHits();
		logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-{}/{} ] Location {}  found at cache.",
		            hits, access, locationId);
		return locationCache.get(locationId);
	}

	private EsiLocation searchOnRepository( final long locationId ) {
		try {
			final EsiLocation hit = this.locationRepository.findById(locationId);
			if (null != hit) {
				locationCache.put(locationId, hit);
				this.lastLocationAccess = LocationCacheAccessType.DATABASE_ACCESS;
				this.dirtyCache = true;
			}
			return hit;
		} catch (final SQLException sqle) {
			logger.info("-- [GlobalDataManager.searchLocation4Id]> Location {} not found on storage.", locationId);
			return null;
		}
	}

	public Map<String, Integer> getLocationTypeCounters() {
		return this.locationTypeCounters;
	}

	private void registerOnScheduler() {
		// TODO - register this on the future scheduler
	}

	private EsiLocation buildUpLocation( final long locationId ) {
		if (locationId < 20000000) { // Can be a Region
			return this.storeOnCacheLocation(this.locationRepository.searchRegionById(locationId));
		}
		if (locationId < 30000000) { // Can be a constellation
			return this.storeOnCacheLocation(this.locationRepository.searchConstellationById(locationId));
		}
		if (locationId < 40000000) { // Can be a system
			return this.storeOnCacheLocation(this.locationRepository.searchSystemById(locationId));
		}
		if (locationId < 61000000) { // Can be a game station
//			return this.locationRepository.searchStationById(locationId);
		}
		return new EsiLocation.Builder().build();
	}

	private EsiLocation storeOnCacheLocation( final EsiLocation entry ) {
		if (null != entry) {
			locationCache.put(entry.getId(), entry);
			this.dirtyCache = true;
		}
		return entry;

//		try {
//			this.locationRepository.persist(location);
//		locationCache.put(location.getId(), location);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

	public boolean getMemoryStatus() {
		return this.dirtyCache;
	}

	public enum LocationCacheAccessType {
		NOT_FOUND, GENERATED, DATABASE_ACCESS, MEMORY_ACCESS
	}

	// - B U I L D E R
	public static class Builder {
		private LocationCatalogService onConstruction;

		public Builder() {
			this.onConstruction = new LocationCatalogService();
		}

		public Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withFileSystem( final IFileSystem fileSystem ) {
			this.onConstruction.fileSystem = fileSystem;
			return this;
		}

		public Builder withSDEDatabaseAdapter( final ISDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public Builder withLocationRepository( final LocationRepository locationRepository ) {
			Objects.requireNonNull(locationRepository);
			this.onConstruction.locationRepository = locationRepository;
			return this;
		}

		public LocationCatalogService build() {
			Objects.requireNonNull(this.onConstruction.configurationProvider);
			Objects.requireNonNull(this.onConstruction.fileSystem);
			Objects.requireNonNull(this.onConstruction.sdeDatabaseAdapter);
			Objects.requireNonNull(this.onConstruction.locationRepository);
			this.onConstruction.verifySDERepository(); // Check that the LocationsCache table exists and verify the contents
			this.onConstruction.readLocationsDataCache(); // Load the cache from the storage.
			this.onConstruction.registerOnScheduler(); // register on scheduler to update storage every some minutes
			return this.onConstruction;
		}
	}
}