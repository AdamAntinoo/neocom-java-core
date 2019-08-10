package org.dimensinfin.eveonline.neocom.adapters;

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
	private static Logger logger = LoggerFactory.getLogger(LocationCatalogService.class);
	private static Map<Long, EsiLocation> locationCache = new HashMap<Long, EsiLocation>();
	private static boolean dirtyCache = false;
	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	private ESIDataAdapter esiDataAdapter;
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystem;
	private LocationRepository locationRepository;

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
		if (dirtyCache) {
			final String cacheFileName = this.configurationProvider.getResourceString("R.cache.directorypath") +
					                             this.configurationProvider.getResourceString("R.cache.locationscache.filename");
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

	private void registerOnScheduler() {
		// TODO - register this on the future scheduler
	}

	public EsiLocation searchLocation4Id( final long locationId ) {
		if (locationCache.containsKey(locationId)) return this.searchOnMemoryCache(locationId);
		final int access = locationsCacheStatistics.accountAccess(false);
		EsiLocation hit = this.searchOnRepository(locationId);
		if (null == hit) {
			final int hits = locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-{}/{} ] Location {} found at database.",
			            hits, access, locationId);
			hit = this.buildUpLocation(locationId);
			this.storeOnCacheLocation(hit);
		}
//		final EsiLocation hit = GlobalDataManager.getSingleton().getSDEDBHelper().searchLocation4Id(locationId);
		// Add the hit to the cache but only when it is not UNKNOWN.
//		if (hit.getClassType() != LocationClass.UNKNOWN) locationCache.put(locationId, hit);
		// Account for a miss on the cache.
		return hit;
	}

	private EsiLocation searchOnMemoryCache( final long locationId ) {
		int access = locationsCacheStatistics.accountAccess(true);
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
				dirtyCache = true;
			}
			return hit;
		} catch (final SQLException sqle) {
			logger.info("-- [GlobalDataManager.searchLocation4Id]> Location {} not found on storage.", locationId);
			return null;
		}
	}

	private EsiLocation buildUpLocation( final long locationId ) {
		// TODO Read the location data from the SDE repository.
		return new EsiLocation.Builder().build();
	}

	private void storeOnCacheLocation( final EsiLocation location ) {

	}

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

		public Builder withLocationRepository( final LocationRepository locationRepository ) {
			Objects.requireNonNull(locationRepository);
			this.onConstruction.locationRepository = locationRepository;
			return this;
		}

		public LocationCatalogService build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			Objects.requireNonNull(this.onConstruction.configurationProvider);
			Objects.requireNonNull(this.onConstruction.fileSystem);
			Objects.requireNonNull(this.onConstruction.locationRepository);
			this.onConstruction.readLocationsDataCache(); // Load the cache from the storage.
			this.onConstruction.registerOnScheduler(); // register on scheduler to update storage every some minutes
			return this.onConstruction;
		}
	}
}