package org.dimensinfin.eveonline.neocom.asset.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.container.FacetedExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegion;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegionImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.domain.space.Structure;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class AssetProvider /*implements Serializable*/ {
	//	private static final long serialVersionUID = -4896485833695914012L;
	private static final LocationIdentifier UNKNOWN_SPACE_LOCATION_IDENTIFIER = new LocationIdentifier.Builder()
			.withSpaceIdentifier( 0L )
			.build();

	/**
	 * Use a map to allow the removal of more nodes during the processing.
	 */
	private transient HashMap<Long, NeoAsset> assetMap = new HashMap<>();
	private Map<Long, AssetContainer> spaceLocationsCache = new HashMap<>();
	private Map<Long, AssetContainer> containersCache = new HashMap<>();
	//	private DateTime assetsReadTime;
	private int assetCounter = 0;
	private List<NeoAsset> unlocatedAssets = new ArrayList<>();

	// - C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private LocationCatalogService locationCatalogService;

	private AssetProvider() {}

	public List<FacetedExpandableContainer> getRegionList() {
		final Map<Integer, FacetedExpandableContainer<SpaceRegion, SpaceSystem>> regions = new HashMap<>();
		try {
			for (AssetContainer spaceLocation : this.spaceLocationsCache.values()) {
				final Integer regionId = ((SpaceRegion) spaceLocation.getSpaceLocation()).getRegionId();
				FacetedExpandableContainer hit = regions.get( regionId );
				if (null == hit) {
					final SpaceRegion region = new SpaceRegionImplementation.Builder()
							.withRegion( ((SpaceRegion) spaceLocation.getSpaceLocation()).getRegion() ).build();
					hit = new FacetedExpandableContainer.Builder<SpaceRegion, SpaceSystem>()
							.withFacet( region ).build();
					regions.put( regionId, hit );
				}
				// TODO - Transform the generic and MVC unsupported AssetContainer to the right station type.
				final SpaceLocation location = spaceLocation.getSpaceLocation();
				if (location instanceof SpaceSystem) {
					final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<SpaceSystem, NeoAsset>()
							.withFacet( (SpaceSystem) location ).build();
					for (NeoAsset item : spaceLocation.getContents()) {
						container.addContent( item );
					}
					hit.addContent( container );
				} else {
					if (location instanceof Structure) {
						final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<Structure, NeoAsset>()
								.withFacet( (Structure) location ).build();
						for (NeoAsset item : spaceLocation.getContents()) {
							container.addContent( item );
						}
						hit.addContent( container );
					}
					if (location instanceof Station) {
						final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<Station, NeoAsset>()
								.withFacet( (Station) location ).build();
						for (NeoAsset item : spaceLocation.getContents()) {
							container.addContent( item );
						}
						hit.addContent( container );
					}
				}
			}
		} catch (final RuntimeException rte) {
			rte.printStackTrace();
		}
		return new ArrayList<>( regions.values() );
	}

	/**
	 * Access the asset repository and searches for all the owner id assets. Then stores them into the asset map by the location id. During the
	 * processing ite counts the number of assets found.
	 * @return
	 */
	public int classifyAssetsByLocation() {
		this.clear();
		final List<NeoAsset> sourceAssetList = this.assetRepository.findAllByOwnerId( this.credential.getAccountId() );
		this.assetCounter = sourceAssetList.size();
		if (sourceAssetList.isEmpty()) return this.assetCounter;
		for (NeoAsset asset : sourceAssetList)
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
			NeoComLogger.info( "Classification complete: {} assets", this.assetCounter + "" );
		} catch (final RuntimeException rte) {
			rte.printStackTrace();
		}
		return this.assetCounter;
	}

	private void add2ContainerLocation( final NeoAsset asset ) {
		final Long spaceIdentifier = asset.getLocationId().getSpaceIdentifier();
		AssetContainer hit = this.containersCache.get( spaceIdentifier );
		if (null == hit) { // This is an exception because container should exist.
			NeoComLogger.info( "--[AssetProvider.processAsset]> Parent container not found: {}",
					asset.getParentContainerId() + "" );
			this.unlocatedAssets.add( asset );
		} else hit.addContent( asset );
	}

	private void add2SpaceLocation( final NeoAsset asset ) {
		final Long spaceIdentifier = asset.getLocationId().getSpaceIdentifier();
		AssetContainer hit = this.spaceLocationsCache.get( spaceIdentifier );
		if (null == hit) {
			final SpaceLocation location = this.searchSpaceLocation( spaceIdentifier );
			if (null != location) { // The location is a public accessible structure and we can add it to the list
				hit = new AssetContainer.Builder().withSpaceLocation( location ).build();
				this.spaceLocationsCache.put( spaceIdentifier, hit );
				hit.addContent( asset );
			} else { // This should be a container and so we add it to the list of container
				this.add2ContainerLocation( asset );
			}
		}
	}

	private void checkIfContainer( final NeoAsset asset ) {
		if (asset.isShip())
			if (!this.containersCache.containsKey( asset.getAssetId() ))
				this.containersCache.put( asset.getAssetId(),
						new AssetContainer.Builder()
								.withAsset( asset ).build() );
		if (asset.isContainer())
			if (!this.containersCache.containsKey( asset.getAssetId() ))
				this.containersCache.put( asset.getAssetId(),
						new AssetContainer.Builder()
								.withAsset( asset ).build() );
	}

	private void clear() {
		this.assetCounter = 0;
		this.assetMap.clear();
//		this.assetsReadTime = null;
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
		NeoComLogger.info( "Processing asset: {}", asset.getAssetId() + "" );
		this.assetMap.remove( asset.getAssetId() ); // Remove the asset from the pending for processing asset list.
		this.checkIfContainer( asset );
		switch (asset.getLocationId().getType()) {
			case SPACE:
			case STATION:
			case STRUCTURE:
				this.add2SpaceLocation( asset );
				break;
			case SHIP:
			case CONTAINER:
				if (asset.hasParentContainer()) {
					final NeoAsset hit = asset.getParentContainer();
					if (null != hit) this.processAsset( hit );
					else
						this.unlocatedAssets.add( asset );
//						throw new NeoComRuntimeException( "The asset reconstruction failed because the expected " +
//								"parent container is not instantiated. Reference: " +
//								asset.getParentContainerId() );
				}
				this.add2ContainerLocation( asset );
				break;
			case UNKNOWN: // Add the asset to the UNKNOWN space location.
				NeoComLogger.info( "Not accessible location coordinates: {}",
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
				break;
		}
	}

	private SpaceLocation searchSpaceLocation( final Long spaceIdentifier ) {
		// Search on the universe list of space locations and stations.
		final SpaceLocation location = this.locationCatalogService.searchLocation4Id( spaceIdentifier );
		if (null != location) // The location is a station or space location
			return location;

		// Search on the authenticated list of public structures.
		final SpaceLocation structure = this.locationCatalogService.searchStructure4Id( spaceIdentifier,
				this.credential );
		if (null != structure) // The location is a public accessible structure and we can add it to the list
			return location;
		return null;
	}

//	private void timeStamp() {
//		this.assetsReadTime = DateTime.now();
//	}
//
//	private boolean verifyTimeStamp() {
//		if (null == this.assetsReadTime) return false;
//		// TODO - verify that the time stamp has elapsed to get a new list of assets updated.
//		return false;
//	}

	// - B U I L D E R
	public static class Builder {
		private AssetProvider onConstruction;

		public Builder() {
			this.onConstruction = new AssetProvider();
		}

		public AssetProvider build() {
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.assetRepository );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			return this.onConstruction;
		}

		public AssetProvider.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.onConstruction.assetRepository = assetRepository;
			return this;
		}

		public AssetProvider.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public AssetProvider.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}
	}
}
