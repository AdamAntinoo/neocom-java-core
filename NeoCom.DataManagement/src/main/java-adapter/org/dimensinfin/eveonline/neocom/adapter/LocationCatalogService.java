package org.dimensinfin.eveonline.neocom.adapter;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.core.AccessStatistics;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceConstellationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegionImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.StationImplementation;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

@NeoComAdapter
public class LocationCatalogService {
	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	private static Logger logger = LoggerFactory.getLogger( LocationCatalogService.class );
	private static Map<Long, SpaceLocation> locationCache = new HashMap<>();
	private Map<String, Integer> locationTypeCounters = new HashMap<>();
	private boolean dirtyCache = false;
	private LocationCacheAccessType lastLocationAccess = LocationCacheAccessType.NOT_FOUND;

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	private ESIUniverseDataProvider esiUniverseDataProvider;
	//	protected ISDEDatabaseAdapter sdeDatabaseAdapter;
	protected LocationRepository locationRepository;

	private LocationCatalogService() { }

	// - C A C H E   M A N A G E M E N T
	public void stopService() {
		this.writeLocationsDataCache();
		this.cleanLocationsCache();
	}

	private void startService() {
		// TODO - This is not required until the citadel structures get stored on the SDE database.
//		this.verifySDERepository(); // Check that the LocationsCache table exists and verify the contents
		this.readLocationsDataCache(); // Load the cache from the storage.
		this.registerOnScheduler(); // Register on scheduler to update storage every some minutes
	}

	// - S T O R A G E
	public void cleanLocationsCache() {
		locationCache.clear();
	}

	public void readLocationsDataCache() {
		logger.info( ">> [LocationCatalogService.readLocationsDataCache]" );
		final String directoryPath = this.configurationProvider.getResourceString( "P.cache.directory.path" );
		final String fileName = this.configurationProvider.getResourceString( "P.cache.locationscache.filename" );
		final String cacheFileName = directoryPath + fileName;
		logger.info( "-- [LocationCatalogService.readLocationsDataCache]> Opening cache file: {}", cacheFileName );
		try (final BufferedInputStream buffer = new BufferedInputStream(
				this.fileSystemAdapter.openResource4Input( cacheFileName ) );
		     final ObjectInputStream input = new ObjectInputStream( buffer )
		) {
			synchronized (locationCache) {
				locationCache = (Map<Long, SpaceLocation>) input.readObject();
				logger.info( "-- [LocationCatalogService.readLocationsDataCache]> Restored cache Locations: {} entries.",
						locationCache.size() );
			}
		} catch (final ClassNotFoundException ex) {
			logger.warn( "W> [LocationCatalogService.readLocationsDataCache]> ClassNotFoundException. {}",
					ex.getMessage() );
		} catch (final FileNotFoundException fnfe) {
			logger.warn( "W> [LocationCatalogService.readLocationsDataCache]> FileNotFoundException. {}",
					fnfe.getMessage() );
		} catch (final IOException ioe) {
			logger.warn( "W> [LocationCatalogService.readLocationsDataCache]> IOException. {}", ioe.getMessage() );
		} catch (final IllegalArgumentException iae) {
			logger.warn( "W> [LocationCatalogService.readLocationsDataCache]> IllegalArgumentException. {}",
					iae.getMessage() );
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		} finally {
			logger.info( "<< [LocationCatalogService.readLocationsDataCache]" );
		}
	}

	public void writeLocationsDataCache() {
		logger.info( ">> [LocationCatalogService.writeLocationsDataCache]" );
		if (this.dirtyCache) {
			final String cacheFileName = this.configurationProvider.getResourceString( "P.cache.directory.path" ) +
					this.configurationProvider.getResourceString( "P.cache.locationscache.filename" );
			logger.info( "-- [LocationCatalogService.writeLocationsDataCache]> Opening cache file: {}", cacheFileName );
			try (final BufferedOutputStream buffer = new BufferedOutputStream(
					this.fileSystemAdapter.openResource4Output( cacheFileName ) );
			     final ObjectOutput output = new ObjectOutputStream( buffer )
			) {
				synchronized (locationCache) {
					output.writeObject( locationCache );
					dirtyCache = false;
					logger.info( "-- [LocationCatalogService.writeLocationsDataCache]> Wrote Locations cache: {} entries.",
							locationCache.size() );
				}
			} catch (final FileNotFoundException fnfe) {
				logger.warn( "W> [LocationCatalogService.writeLocationsDataCache]> FileNotFoundException." );
			} catch (final IOException ex) {
				logger.warn( "W> [LocationCatalogService.writeLocationsDataCache]> IOException." );
			} finally {
				logger.info( "<< [LocationCatalogService.writeLocationsDataCache]" );
			}
		}
	}

//	public void verifySDERepository() {
//		this.locationTypeCounters = this.locationRepository.getCounters();
//	}

	// - S E A R C H   L O C A T I O N   A P I
//	public SpaceLocation searchSpaceLocation4Id( final Long locationIdentifier ) {
//		return new EsiLocation2SpaceKLocationConverter.Builder()
//				.withESIUniverseDataProvider( this.esiUniverseDataProvider )
//				.build().convert( this.buildUpLocation( locationIdentifier ) );
//	}

//	public StationLocation searchStationLocation4Id( final Integer stationIdentifier ) {
//		if (stationIdentifier < 61000000) { // Can be a game station
//			return this.locationRepository.searchStationById( stationIdentifier );
//		}
//		return null;
//	}

	public SpaceLocation searchLocation4Id( final Long locationId ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey( locationId )) return this.searchOnMemoryCache( locationId );
		final int access = locationsCacheStatistics.accountAccess( false );
//		SpaceLocation hit = this.searchOnRepository( locationId );
		final int hits = locationsCacheStatistics.getHits();
//		if (null == hit) {
		SpaceLocation hit = this.buildUpLocation( locationId );
		this.lastLocationAccess = LocationCacheAccessType.GENERATED;
		this.storeOnCacheLocation( hit );
		logger.info( ">< [LocationCatalogService.searchLocation4Id]> [HIT-{}/{} ] Location {} generated from ESI data.",
				hits, access, locationId );
		return hit;
//		}
//		logger.info( ">< [LocationCatalogService.searchLocation4Id]> [HIT-{}/{} ] Location {} found at database.",
//				hits, access, locationId );
//		return hit;
	}

//	public LocationCacheAccessType lastSearchLocationAccessType() {
//		return this.lastLocationAccess;
//	}

	private SpaceLocation searchOnMemoryCache( final Long locationId ) {
		int access = locationsCacheStatistics.accountAccess( true );
		this.lastLocationAccess = LocationCacheAccessType.MEMORY_ACCESS;
		int hits = locationsCacheStatistics.getHits();
		logger.info( ">< [LocationCatalogService.searchOnMemoryCache]> [HIT-{}/{} ] Location {}  found at cache.",
				hits, access, locationId );
		return locationCache.get( locationId );
	}
// TODO - Needs reimplementation for corporation structures
//	private SpaceLocation searchOnRepository( final Long locationId ) {
//		try {
//			final EsiLocation hit = this.locationRepository.findById( locationId );
//			if (null != hit) {
//				locationCache.put( locationId, hit );
//				this.lastLocationAccess = LocationCacheAccessType.DATABASE_ACCESS;
//				this.dirtyCache = true;
//			}
//			return hit;
//		} catch (final SQLException sqle) {
//			logger.info( "-- [GlobalDataManager.searchLocation4Id]> Location {} not found on storage.", locationId );
//			return null;
//		}
//	}

	public Map<String, Integer> getLocationTypeCounters() {
		return this.locationTypeCounters;
	}

	private void registerOnScheduler() {
		// TODO - register this on the future scheduler
	}

	private SpaceLocation buildUpLocation( final Long locationId ) {
		if (locationId < 20000000) { // Can be a Region
			return this.storeOnCacheLocation(
					(SpaceLocation) new SpaceRegionImplementation.Builder()
							.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) )
							.build() );
		}
		if (locationId < 30000000) { // Can be a Constellation
			return this.storeOnCacheLocation(
					(SpaceLocation) new SpaceConstellationImplementation.Builder()
							.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) )
							.withConstellation(
									this.esiUniverseDataProvider.getUniverseConstellationById( locationId.intValue() ) )
							.build() );
		}
		if (locationId < 40000000) { // Can be a system
			return this.storeOnCacheLocation(
					(SpaceLocation) new SpaceSystemImplementation.Builder()
							.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) )
							.withConstellation(
									this.esiUniverseDataProvider.getUniverseConstellationById( locationId.intValue() ) )
							.withSolarSystem( this.esiUniverseDataProvider.getUniverseSystemById( locationId.intValue() ) )
							.build() );
		}
		if (locationId < 61000000) { // Can be a game station
			return this.storeOnCacheLocation(
					(SpaceLocation) new StationImplementation.Builder()
							.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) )
							.withConstellation(
									this.esiUniverseDataProvider.getUniverseConstellationById( locationId.intValue() ) )
							.withSolarSystem( this.esiUniverseDataProvider.getUniverseSystemById( locationId.intValue() ) )
							.withStation( this.esiUniverseDataProvider.getUniverseStationById( locationId.intValue() ) )
							.build() );
		}
		return null;
	}

	private SpaceLocation storeOnCacheLocation( final SpaceLocation entry ) {
		if (null != entry) {
			locationCache.put( entry.getLocationId(), entry );
			this.dirtyCache = true;
		}
		return entry;
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

		public Builder( final LocationCatalogService preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new LocationCatalogService();
		}

		public Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withFileSystemAdapter( final IFileSystem fileSystem ) {
			this.onConstruction.fileSystemAdapter = fileSystem;
			return this;
		}

		public Builder withESIUniverseDataProvider( final ESIUniverseDataProvider esiUniverseDataProvider ) {
			Objects.requireNonNull( esiUniverseDataProvider );
			this.onConstruction.esiUniverseDataProvider = esiUniverseDataProvider;
			return this;
		}

		public Builder withLocationRepository( final LocationRepository locationRepository ) {
			Objects.requireNonNull( locationRepository );
			this.onConstruction.locationRepository = locationRepository;
			return this;
		}

		public LocationCatalogService build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
//			Objects.requireNonNull( this.onConstruction.sdeDatabaseAdapter );
			Objects.requireNonNull( this.onConstruction.locationRepository );
			this.onConstruction.startService();
			return this.onConstruction;
		}
	}
}