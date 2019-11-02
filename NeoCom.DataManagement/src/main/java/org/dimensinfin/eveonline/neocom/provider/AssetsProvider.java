package org.dimensinfin.eveonline.neocom.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.adapters.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.space.Region;
import org.dimensinfin.eveonline.neocom.utility.AssetContainer;

public class AssetsProvider implements Serializable {
	private static final long serialVersionUID = -4896485833695914012L;
	private static final Logger logger = LoggerFactory.getLogger( AssetsProvider.class );

	//	private Map<Integer, NeoAsset> structuresCache = new HashMap<>();
	private Map<Long, AssetContainer> spaceLocationsCache = new HashMap<>();
//	private Map<Integer, FacetedNodeContainer<SpaceLocation>> systemsCache = new HashMap<>();
//	private Map<Integer, FacetedLocationContainer<Region>> regionsCache = new HashMap<>();

	// - C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private LocationCatalogService locationCatalogService;

	private LocalTime assetsReadTime;
	private int assetCounter = 0;

	private AssetsProvider() {}

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/**
	 * Use a map to allow the removal of more nodes during the processing.
	 */
	private transient HashMap<Long, NeoAsset> assetMap = new HashMap<>();
	private List<NeoAsset> unlocatedAssets = new ArrayList<>();

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
			logger.info( "<< [AssetsProvider.classifyAssetsByLocation]> Classification complete: {} assets",
					this.assetCounter );
		}
	}

	public List<Region> getRegionList() {
		final Map<Integer, Region> regions = new HashMap<>();
		for (AssetContainer spaceLocation : this.spaceLocationsCache.values()) {
			Region hit = regions.get( spaceLocation.getRegionId() );
			if (null == hit) {
				hit = new Region.Builder().withRegion( spaceLocation.getRegion() ).build();
				regions.put( spaceLocation.getRegionId(), hit );
			}
			hit.addContent( spaceLocation );
		}
		return new ArrayList<>( regions.values() );
	}

	private boolean verifyTimeStamp() {
		if (null == this.assetsReadTime) return false;
		// TODO - verify that the time stamp has elapsed to get a new list of assets updated.
		return false;
	}

	private void clear() {
		this.assetCounter = 0;
		this.assetMap.clear();
		int assetCounter = 0;
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
		switch (asset.getLocationId().getType()) {
			case SPACE:
			case STATION:
				this.assetMap.remove( asset.getAssetId() ); // Remove the asset from the pending for processing asset list.
				this.add2SpaceLocation( asset );
				break;
//			case STATION:
//				this.assetMap.remove( asset.getAssetId() ); // Remove the asset from the pending for processing asset list.
//				this.add2StationLocation( asset );
//				break;
		}
	}

//	private void add2StationLocation( final NeoAsset asset ) {
//		final Integer stationIdentifier = asset.getLocationId().getStationIdentifier();
//		FacetedAssetContainer<StationLocation> hit = this.spaceLocationsCache.get( stationIdentifier );
//		if (null == hit) {
//			hit = new FacetedAssetContainer.Builder<StationLocation>()
//					.withFacet( this.locationCatalogService.searchStationLocation4Id( stationIdentifier ) )
//					.build();
//			this.spaceLocationsCache.put( stationIdentifier, hit );
//		}
//		this.add2SpaceLocation( hit );
//		hit.addContent( asset );
//	}

//	private void add2SpaceLocation( final FacetedAssetContainer<StationLocation> station ) {
//		final Integer spaceIdentifier = station.getFacet().getSystemId();
//		FacetedNodeContainer<SpaceLocation> hit = this.systemsCache.get( spaceIdentifier );
//		if (null == hit) {
//			hit = new FacetedNodeContainer.Builder<SpaceLocation>()
//					.withFacet( this.locationCatalogService.searchSpaceLocation4Id( spaceIdentifier ) )
//					.build();
//			this.systemsCache.put( spaceIdentifier, hit );
//		}
//		this.add2Region( hit );
//		hit.addContent( station );
//	}

	private void add2SpaceLocation( final NeoAsset asset ) {
		final Long spaceIdentifier = asset.getLocationId().getSpaceIdentifier().longValue();
		AssetContainer hit = this.spaceLocationsCache.get( spaceIdentifier );
		if (null == hit) {
//			switch (asset.getLocationId().getType()) {
//				case SPACE:
			hit = new AssetContainer.Builder()
					.withSpaceLocation( this.locationCatalogService.searchLocation4Id( spaceIdentifier ) )
					.build();
//			}
//			hit = new FacetedNodeContainer.Builder<SpaceLocation>()
//					.withFacet( this.locationCatalogService.searchSpaceLocation4Id( spaceIdentifier ) )
//					.build();
			this.spaceLocationsCache.put( spaceIdentifier, hit );
		}
//		this.add2Region( hit );
		hit.addContent( asset );
	}

//	private void add2Region( final FacetedAssetContainer<SpaceLocation> location ) {
//		final Integer regionIdentifier = location.getFacet().getRegionId();
//		FacetedLocationContainer<Region> hit = this.regionsCache.get( regionIdentifier );
//		if (null == hit) {
//			hit = new FacetedLocationContainer.Builder<Region>()
//					.withFacet( SpaceKLocation2RegionDuplicator.clone( location.getFacet() ) )
//					.build();
//			this.regionsCache.put( regionIdentifier, hit );
//		}
//		hit.addContent( location.getFacet() );
//	}

	// - B U I L D E R
	public static class Builder {
		private AssetsProvider onConstruction;

		public Builder() {
			this.onConstruction = new AssetsProvider();
		}

		public AssetsProvider.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public AssetsProvider.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.onConstruction.assetRepository = assetRepository;
			return this;
		}

		public AssetsProvider.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public AssetsProvider build() {
			Objects.requireNonNull( this.onConstruction.credential );
			return this.onConstruction;
		}
	}
}