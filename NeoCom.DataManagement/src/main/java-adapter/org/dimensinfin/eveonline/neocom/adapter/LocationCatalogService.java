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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.annotation.NeoComAdapter;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.core.AccessStatistics;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceConstellationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegionImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.StationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.StructureImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import retrofit2.Response;

@NeoComAdapter
public class LocationCatalogService {
	public enum LocationCacheAccessType {
		NOT_FOUND, GENERATED, DATABASE_ACCESS, MEMORY_ACCESS
	}

	private static final AccessStatistics locationsCacheStatistics = new AccessStatistics();
	private static Logger logger = LoggerFactory.getLogger( LocationCatalogService.class );
	private static Map<Long, SpaceLocation> locationCache = new HashMap<>();
	private Map<String, Integer> locationTypeCounters = new HashMap<>();
	private boolean dirtyCache = false;
	private LocationCacheAccessType lastLocationAccess = LocationCacheAccessType.NOT_FOUND;

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected ESIUniverseDataProvider esiUniverseDataProvider;
	protected LocationRepository locationRepository;
	protected NeoComRetrofitFactory retrofitFactory;

	protected LocationCatalogService() { }

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
	public Optional<SpaceLocation> searchLocation4Id( final Long locationId ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey( locationId ))
			return Optional.ofNullable( this.searchOnMemoryCache( locationId ) );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		SpaceLocation hit = this.buildUpLocation( locationId );
		if (null != hit) {
			this.lastLocationAccess = LocationCacheAccessType.GENERATED;
			this.storeOnCacheLocation( hit );
			logger.info( ">< [LocationCatalogService.searchLocation4Id]> [HIT-{}/{} ] Location {} generated from ESI data.",
					hits, access, locationId );
			return Optional.of( hit );
		} else return Optional.empty();
	}

	public Optional<SpaceLocation> searchStructure4Id( final Long locationId, final Credential credential ) {
		this.lastLocationAccess = LocationCacheAccessType.NOT_FOUND;
		if (locationCache.containsKey( locationId ))
			return Optional.ofNullable( this.searchOnMemoryCache( locationId ) );
		final int access = locationsCacheStatistics.accountAccess( false );
		final int hits = locationsCacheStatistics.getHits();
		SpaceLocation hit = this.buildUpStructure( locationId, credential );
		if (null != hit) {
			this.lastLocationAccess = LocationCacheAccessType.GENERATED;
			this.storeOnCacheLocation( hit );
			logger.info( ">< [LocationCatalogService.searchStructure4Id]> [HIT-{}/{} ] Location {} generated from Public " +
							"Structure Data.",
					hits, access, locationId );
			return Optional.of( hit );
		} else return Optional.empty();
	}

	public Map<String, Integer> getLocationTypeCounters() {
		return this.locationTypeCounters;
	}

	public SpaceLocation storeOnCacheLocation( final SpaceLocation entry ) {
		if (null != entry) {
			locationCache.put( entry.getLocationId(), entry );
			this.dirtyCache = true;
		}
		return entry;
	}

	public boolean getMemoryStatus() {
		return this.dirtyCache;
	}

	private SpaceLocation searchOnMemoryCache( final Long locationId ) {
		int access = locationsCacheStatistics.accountAccess( true );
		this.lastLocationAccess = LocationCacheAccessType.MEMORY_ACCESS;
		int hits = locationsCacheStatistics.getHits();
		logger.info( ">< [LocationCatalogService.searchOnMemoryCache]> [HIT-{}/{} ] Location {}  found at cache.",
				hits, access, locationId );
		return locationCache.get( locationId );
	}

	private void registerOnScheduler() {
		// TODO - register this on the future scheduler
	}

	private SpaceLocation buildUpLocation( final Long locationId ) {
		if (locationId < 20000000) { // Can be a Region
			return this.storeOnCacheLocation(
					new SpaceRegionImplementation.Builder()
							.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( locationId.intValue() ) )
							.build() );
		}
		if (locationId < 30000000) { // Can be a Constellation
			final GetUniverseConstellationsConstellationIdOk constellation = this.esiUniverseDataProvider
					.getUniverseConstellationById( locationId.intValue() );
			Objects.requireNonNull( constellation );
			final GetUniverseRegionsRegionIdOk region = this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() );
			Objects.requireNonNull( region );
			return this.storeOnCacheLocation(
					new SpaceConstellationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.build() );
		}
		if (locationId < 40000000) { // Can be a system
			final GetUniverseSystemsSystemIdOk solarSystem = this.esiUniverseDataProvider
					.searchSolarSystem4Id( locationId.intValue() );
			Objects.requireNonNull( solarSystem );
			final GetUniverseConstellationsConstellationIdOk constellation = this.esiUniverseDataProvider
					.getUniverseConstellationById( solarSystem.getConstellationId() );
			Objects.requireNonNull( constellation );
			final GetUniverseRegionsRegionIdOk region = this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() );
			Objects.requireNonNull( region );
			return this.storeOnCacheLocation(
					new SpaceSystemImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.withSolarSystem( solarSystem )
							.build() );
		}
		if (locationId < 61000000) { // Can be a game station
			final GetUniverseStationsStationIdOk station = this.esiUniverseDataProvider
					.getUniverseStationById( locationId.intValue() );
			Objects.requireNonNull( station );
			final GetUniverseSystemsSystemIdOk solarSystem = this.esiUniverseDataProvider
					.searchSolarSystem4Id( station.getSystemId() );
			Objects.requireNonNull( solarSystem );
			final GetUniverseConstellationsConstellationIdOk constellation = this.esiUniverseDataProvider
					.getUniverseConstellationById( solarSystem.getConstellationId() );
			Objects.requireNonNull( constellation );
			final GetUniverseRegionsRegionIdOk region = this.esiUniverseDataProvider
					.getUniverseRegionById( constellation.getRegionId() );
			Objects.requireNonNull( region );
			return this.storeOnCacheLocation(
					new StationImplementation.Builder()
							.withRegion( region )
							.withConstellation( constellation )
							.withSolarSystem( solarSystem )
							.withStation( station )
							.build() );
		}
		return null;
	}

	private SpaceLocation buildUpStructure( final Long locationId, final Credential credential ) {
		final GetUniverseStructuresStructureIdOk structure = this.searchStructureById( locationId, credential );
		Objects.requireNonNull( structure );
		final GetUniverseSystemsSystemIdOk solarSystem = this.esiUniverseDataProvider
				.searchSolarSystem4Id( structure.getSolarSystemId() );
		Objects.requireNonNull( solarSystem );
		final GetUniverseConstellationsConstellationIdOk constellation = this.esiUniverseDataProvider
				.getUniverseConstellationById( solarSystem.getConstellationId() );
		Objects.requireNonNull( constellation );
		final GetUniverseRegionsRegionIdOk region = this.esiUniverseDataProvider
				.getUniverseRegionById( constellation.getRegionId() );
		Objects.requireNonNull( region );
		return this.storeOnCacheLocation(
				new StructureImplementation.Builder()
						.withRegion( region )
						.withConstellation( constellation )
						.withSolarSystem( solarSystem )
						.withStructure( locationId, structure )
						.build() );
	}

	public GetUniverseStructuresStructureIdOk searchStructureById( final Long structureId, final Credential credential ) {
		final String refreshToken = credential.getRefreshToken();
		final int identifier = credential.getAccountId();
		try {
			NeoComRetrofitHTTP.setRefeshToken( refreshToken );
			String datasource = ESIDataProvider.DEFAULT_ESI_SERVER;
			final Response<GetUniverseStructuresStructureIdOk> universeResponse = this.retrofitFactory
					.accessESIAuthRetrofit()
					.create( UniverseApi.class )
					.getUniverseStructuresStructureId( structureId, datasource, null, credential.getAccessToken() )
					.execute();
			if (universeResponse.isSuccessful()) {
				return universeResponse.body();
			} else return null;
		} catch (final IOException ioe) {
			NeoComLogger.error( "[IOException]> locating public structure: ", ioe );
			ioe.printStackTrace();
			return null;
		} catch (final RuntimeException rte) {
			NeoComLogger.error( "[RuntimeException]> locating public structure: ", rte );
			rte.printStackTrace();
			return null;
		}
//		return null;
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
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
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

		public Builder withRetrofitFactory( final NeoComRetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}

		public LocationCatalogService build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.esiUniverseDataProvider );
			Objects.requireNonNull( this.onConstruction.locationRepository );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			this.onConstruction.startService();
			return this.onConstruction;
		}
	}
}