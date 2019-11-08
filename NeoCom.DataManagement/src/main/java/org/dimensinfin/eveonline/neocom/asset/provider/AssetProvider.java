package org.dimensinfin.eveonline.neocom.asset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.Region;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegion;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class AssetProvider implements Serializable {
	private static final long serialVersionUID = -4896485833695914012L;
	private static final Logger logger = LoggerFactory.getLogger( AssetProvider.class );
	private static final LocationIdentifier UNKNOWN_SPACE_LOCATION_IDENTIFIER = new LocationIdentifier.Builder()
			.withSpaceIdentifier( 0L )
			.build();

	/**
	 * Use a map to allow the removal of more nodes during the processing.
	 */
	private transient HashMap<Long, NeoAsset> assetMap = new HashMap<>();
	private Map<Long, AssetContainer> spaceLocationsCache = new HashMap<>();
	private Map<Long, AssetContainer> containersCache = new HashMap<>();
	private DateTime assetsReadTime;
	private int assetCounter = 0;
	private List<NeoAsset> unlocatedAssets = new ArrayList<>();

	// - C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private LocationCatalogService locationCatalogService;

	private AssetProvider() {}

	public void classifyAssetsByLocation() {
		if (this.verifyTimeStamp()) return;
		this.clear();
		for (NeoAsset asset : this.assetRepository.findAllByOwnerId( this.credential.getAccountId() ))
			this.assetMap.put( asset.getAssetId(), asset );
		// Process the map until all elements are removed.
		Long key = this.assetMap.keySet().iterator().next();
		this.assetCounter++;
		NeoAsset point = this.assetMap.get( key );
		try {
			while (null != point) {
				this.processAsset( point );
				key = this.assetMap.keySet().iterator().next();
				point = this.assetMap.get( key );
			}
		} catch (final NoSuchElementException nsee) {
			logger.info( "<< [AssetProvider.classifyAssetsByLocation]> Classification complete: {} assets",
					this.assetCounter );
		}
		this.timeStamp();
	}

	public List<Region> getRegionList() {
		final Map<Integer, Region> regions = new HashMap<>();
		for (AssetContainer spaceLocation : this.spaceLocationsCache.values()) {
			Region hit = regions.get( ((SpaceRegion) spaceLocation).getRegionId() );
			if (null == hit) {
				hit = new Region.Builder().withRegion( ((SpaceRegion) spaceLocation).getRegion() ).build();
				regions.put( ((SpaceRegion) spaceLocation).getRegionId(), hit );
			}
			hit.addContent( spaceLocation );
		}
		return new ArrayList<>( regions.values() );
	}

//	private void checks() {
//		final Station station = new StationImplementation.Builder().build();
//
//		final SpaceSystem system = new SpaceSystemImplementation.Builder().build();
//
//		final Structure structure = new StructureImplementation.Builder().build();
//	}

	private boolean verifyTimeStamp() {
		if (null == this.assetsReadTime) return false;
		// TODO - verify that the time stamp has elapsed to get a new list of assets updated.
		return false;
	}

	private void timeStamp() {
		this.assetsReadTime = DateTime.now();
	}

	private void clear() {
		this.assetCounter = 0;
		this.assetMap.clear();
	}

	/**
	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	 * because with the flat listing there is only relationship through the location id. <br>
	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	 * because the auto model generation will already include those items. Only Locations or Regions behave
	 * differently.
	 *
	 * NEW IMPLEMENTATION
	 * During the processing of an asset the first task is to check if the asset is contained on another game asset, belongs it to
	 * the current
	 * character or to any other character. If this is the case then we switch and start processing that new asset until we reach
	 * a space location
	 * going up on the containers hierarchy.
	 * So if a node location is of the type container then we recursively switch processing to this new asset.
	 *
	 * NEW IMPLEMENTATION
	 * There are only asset containers on the global list. The minimum classification item is a SpaceLocation that can be a
	 * space location of a game station. All assets should be covered by this classification.
	 */
	private void processAsset( final NeoAsset asset ) {
		logger.info( "--[AssetProvider.processAsset]> Processing asset: {}",
				asset.getAssetId() );
		this.assetMap.remove( asset.getAssetId() ); // Remove the asset from the pending for processing asset list.
		switch (asset.getLocationId().getType()) {
			case SPACE:
			case STATION:
			case STRUCTURE:
				this.add2SpaceLocation( asset );
				break;
			case UNKNOWN: // Add the asset to the UNKNOWN space location.
				logger.info( "--[AssetProvider.processAsset]> Not accessible location coordinates: {}",
						asset.getLocationId().toString() );
				final LocationIdentifier spaceIdentifier = UNKNOWN_SPACE_LOCATION_IDENTIFIER;
				AssetContainer hit = this.spaceLocationsCache.get( spaceIdentifier.getSpaceIdentifier() );
				if (null == hit) {
					hit = new AssetContainer.Builder()
							.withSpaceLocationIdentifier( UNKNOWN_SPACE_LOCATION_IDENTIFIER )
							.build();
					this.spaceLocationsCache.put( spaceIdentifier.getSpaceIdentifier(), hit );
				}
				hit.addContent( asset );
//
//
//				if (asset.hasParentContainer()) { // Read the asset from the database with a new call.
//					final Optional<NeoAsset> parentAsset = this.assetRepository
//							.findAssetById( asset.getParentContainerId() );
//					parentAsset.ifPresent( this::processAsset );
//				}
//				this.add2ContainerLocation( asset ); // The container should be present because the processing of the parent.
				break;
		}
	}

	private void add2SpaceLocation( final NeoAsset asset ) {
		final Long spaceIdentifier = asset.getLocationId().getSpaceIdentifier();
		AssetContainer hit = this.spaceLocationsCache.get( spaceIdentifier );
		if (null == hit) {
			final Optional<SpaceLocation> location = this.searchSpaceLocation( spaceIdentifier );
			if (location.isPresent()) { // The location is a public accessible structure and we can add it to the list
				hit = new AssetContainer.Builder().withSpaceLocation( location.get() ).build();
				this.spaceLocationsCache.put( spaceIdentifier, hit );
				hit.addContent( asset );
			} else { // This should be a container and so we add it to the list of container
				this.add2ContainerLocation( asset );
			}
		}
	}

	private void add2ContainerLocation( final NeoAsset asset ) {
		final Long spaceIdentifier = asset.getLocationId().getSpaceIdentifier();
		AssetContainer hit = this.containersCache.get( spaceIdentifier );
		if (null == hit) { // This is an exception because container should exist.
			logger.info( "--[AssetProvider.processAsset]> Parent container not found: {}",
					asset.getParentContainerId() );
			this.unlocatedAssets.add( asset );
		} else hit.addContent( asset );
	}

	private Optional<SpaceLocation> searchSpaceLocation( final Long spaceIdentifier ) {
		// Search on the universe list of space locations and stations.
		final Optional<SpaceLocation> location = this.locationCatalogService.searchLocation4Id( spaceIdentifier );
		if (location.isPresent()) // The location is a station or space location
			return location;

		// Search on the authenticated list of public structures.
		final Optional<SpaceLocation> structure = this.locationCatalogService.searchStructure4Id( spaceIdentifier,
				this.credential );
		if (structure.isPresent()) // The location is a public accessible structure and we can add it to the list
			return location;
		return Optional.empty();
	}

	// - B U I L D E R
	public static class Builder {
		private AssetProvider onConstruction;

		public Builder() {
			this.onConstruction = new AssetProvider();
		}

		public AssetProvider.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public AssetProvider.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.onConstruction.assetRepository = assetRepository;
			return this;
		}

		public AssetProvider.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public AssetProvider build() {
			Objects.requireNonNull( this.onConstruction.credential );
			return this.onConstruction;
		}
	}
}