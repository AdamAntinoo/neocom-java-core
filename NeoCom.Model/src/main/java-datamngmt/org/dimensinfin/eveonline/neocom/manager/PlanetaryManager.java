//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.model.SpaceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryManager extends AbstractManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3794750126425122302L;
	private static Logger logger = LoggerFactory.getLogger("PlanetaryManager");

	// - F I E L D - S E C T I O N ............................................................................
	public long totalAssetsCount = 0;
	public double totalAssetsValue = 0.0;
	public double totalAssetsVolume = 0.0;
	/** This field should be serialized to get the correct icon at the Angular UI. */
	public final String iconName = "planets.png";
	// TODO This variable is not get. Only the result of the call is stored here but no check for content is optimized.
	// TODO Cache this data in local because the number of colonies does not change frecuently but first check for
	// current response times
	private final List<Colony> colonies = new ArrayList<>();

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	/** Shared field to allow to share the list of assets to process between methods during the processing. */
	@JsonIgnore
	private transient Hashtable<Long, NeoComAsset> assetMap = new Hashtable<Long, NeoComAsset>();

	//	private SpaceContainer container = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryManager (final Credential credential) {
		super(credential);
		jsonClass = "PlanetaryManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Reads from the database all the Planetary assets and classifies them into their contained Locations.
	 * Planetary assets can be located at Locations or inside Containers or at Ships. Now that the parentship
	 * hierarchies are back we should not process the old way but be able to access Ships or Containers on
	 * demand to reconstruct the Resource hierarchy.
	 */
	public void accessAllAssets () {
		// Initialize the model
		regions.clear();
		locations.clear();
		containers.clear();
		try {
			// Read all the assets for this character if not done already.
			ArrayList<NeoComAsset> planetaryAssetList = ModelAppConnector.getSingleton().getDBConnector()
			                                                             .accessAllPlanetaryAssets(getCredentialIdentifier());
			totalAssetsCount = planetaryAssetList.size();
			// Process the Resources and search for the parent assets to classify them into the Locations.
			for (NeoComAsset resource : planetaryAssetList) {
				this.processPlanetaryResource(resource);
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			PlanetaryManager.logger
					.error("RTEX> [PlanetaryManager.accessAllAssets]> There is a problem processing the Planetary Resources.");
		} finally {
			PlanetaryManager.logger.info(">< [AssetsManager.accessAllAssets]> Assets processed: " + totalAssetsCount);
		}
	}

	@Deprecated
	private long getCharacterId () {
		return credential.getAccountId();
	}

	public synchronized List<Colony> accessAllColonies () {
		logger.info(">< [ColonyManager.accessAllColonies]");
		// Call the Global manager to get the list of Colonies that belong to this user.
		synchronized (colonies) {
			colonies.clear();
			// TODO Remove the access to the database until I have solved duplication problems.
			colonies.addAll(GlobalDataManager.downloadColonies4Credential(credential));
		}
		return colonies;
	}

	public long getAssetTotalCount () {
		this.initialize();
		return totalAssetsCount;
	}

	/**
	 * Get the list of Planetary Resources that are at the indicated location. If the location is not found or
	 * the contents are not initializes then return an empty list.
	 */
	public Vector<Resource> getLocationContents (final String locationid) {
		long locidnumber = Long.valueOf(locationid);
		ExtendedLocation hit = locations.get(locidnumber);
		if ( null != hit ) {
			List<NeoComAsset> contents = hit.downloadContents();
			Vector<NeoComAsset> intermediate = new Vector<NeoComAsset>(contents.size());
			for (NeoComAsset node : contents) {
				// Check for containers to get also its contents.
				if ( node instanceof IAssetContainer ) {
					intermediate.addAll(((IAssetContainer) node).getAssets());
				}
				intermediate.add(node);
			}
			// Convert the nodes to Resources.
			Vector<Resource> results = new Vector<Resource>(intermediate.size());
			for (NeoComAsset node : intermediate) {
				results.add(new Resource(node.getTypeId(), node.getQuantity()));
			}
			return results;
		}
		return new Vector<Resource>();
	}

	public String getOrderingName () {
		return "Planetary Manager";
	}

	public long getTotalAssets () {
		return totalAssetsCount;
	}

	public double getTotalAssetsValue () {
		return totalAssetsValue;
	}

	@Override
	public AbstractManager initialize () {
		if ( !initialized ) {
			this.accessAllAssets();
			initialized = true;
		}
		return this;
	}

	/**
	 * Does additional checks besides the initialization flag status. For planetary we have to get sure we have
	 * already downloaded and processed the Resources.
	 */
	//	@Override
	//	public boolean isInitialized() {
	//		if ((regions.size() < 1) && (locations.size() < 1))
	//			return false;
	//		else
	//			return super.isInitialized();
	//	}

	//	/**
	//	 * Returns the list of different Regions found on the list of locations.
	//	 * 
	//	 * @return
	//	 */
	//	public Hashtable<Long, Region> getRegions() {
	//		if (!this.isInitialized()) {
	//			this.initialize();
	//		}
	//		return regions;
	//	}

	/**
	 * Add the resource to the list of contents of the location identified by the identifier stored at the
	 * <code>Asset</code> received as the parameter. There is a list of already detected
	 * ExtendedLocations that are the model objects that get used on the MVC. If the Location is not found on the
	 * cache locate another one and convert it to a ExtendedLocation container.
	 *
	 * @param asset the resource asset to to stored at the Location.
	 */
	protected void add2Location (final NeoComAsset asset) {
		long locid = asset.getLocationId();
		ExtendedLocation target = locations.get(locid);
		if ( null == target ) {
			EveLocation intermediary = GlobalDataManager.searchLocation4Id(locid);
			// Create another new Extended Location as a copy if this one to disconnect it from the unique cache copy.
			ExtendedLocation newloc = new ExtendedLocation(credential.getAccountId(), intermediary);
			newloc.setContentManager(new PlanetaryAssetsContentManager(newloc));
			newloc.setDownloaded(true);
			//			// Calculate the value and the volume and add them to the container aggregation fields.
			//			newloc.addVolumeUsed(asset.getQuantity()*asset.getItem().getVolume());
			//			newloc.addValue(asset.getQuantity()*asset.getItem().getHighestBuyerPrice().getPrice());
			totalAssetsValue += asset.getQuantity() * asset.getItem().getHighestBuyerPrice().getPrice();
			totalAssetsVolume += asset.getQuantity() * asset.getItem().getVolume();
			locations.put(new Long(locid), newloc);
			this.add2Region(newloc);
			newloc.addContent(asset);
		} else {
			target.addContent(asset);
		}
	}

	//	private boolean isContainer (final long identifier) {
	//		// Search for this asset on the database. If it is a ship or a container, add it to the list of assets.
	//		NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(identifier);
	//		if ( null == target )
	//			return false;
	//		else {
	//			// It it is a ship then add the bay as a container. Otherwise add the container and process it as a new asset.
	//			if ( target.isContainer() ) return true;
	//			if ( target.isShip() ) return true;
	//		}
	//		return false;
	//	}

	//	/**
	//	 * Search for this container reference on this Location's children until found. Then aggregates the asset to
	//	 * that container calculating stacking if this is possible. There can be containers inside container like
	//	 * the case where a container is on the hold of a ship. That special case will not be implemented on this
	//	 * first approach and all the container will be located at the Location's hangar floor.<br>
	//	 * Containers also do not have its market value added to the location's aggregation.
	//	 * 
	//	 * @param apart
	//	 */
	//	private void add2Container(final NeoComAsset asset) {
	//		PlanetaryManager.logger.info(">> LocationAssetsPart.add2Container");
	//		// Locate the container if already added to the location.
	//		NeoComAsset cont = asset.getParentContainer();
	//		// TODO Check what is the cause of a parent container null and solve it
	//		if (null != cont) {
	//			long pcid = cont.getDAOID();
	//			NeoComAsset target = containers.get(pcid);
	//			if (null == target) {
	//				// Add the container to the list of containers.
	//				PlanetaryManager.logger
	//						.info("-- [AssetsByLocationDataSource.add2Container]> Created new container: " + cont.getDAOID());
	//				containers.put(new Long(pcid), cont);
	//				// Add the container to the list of locations or to another container if not child
	//				//			if (asset.hasParent()) {
	//				//				add2Container(cont);
	//				//			} else {
	//				//				add2Location(cont);
	//				//			}
	//			} else {
	//				// Add the asset to the children list of the target container
	//				target.addChild(asset);
	//			}
	//		} else {
	//			// Investigate why the container is null. And maybe we should search for it because it is not our asset.
	//			long id = asset.getParentContainerId();
	//			NeoComAsset parentAssetCache = ModelAppConnector.getSingleton().getDBConnector()
	//					.searchAssetByID(asset.getParentContainerId());
	//		}
	//		// This is an Unknown location that should be a Custom Office
	//	}
	//	/**
	//	 * This adds the Asset to the target Container. If the Container is not already on the list of Containers
	//	 * then it is addeed to that list no other assets get added to the same Container. The same with the
	//	 * Location of the container. The Container is then added to its own Location.
	//	 * 
	//	 * @param asset
	//	 * @param target
	//	 */
	//	private void add2Container(final NeoComAsset asset, final NeoComAsset target) {
	//		long id = asset.getLocationID();
	//		NeoComAsset subtarget = containers.get(id);
	//		if (null == subtarget) {
	//			target.addChild(asset);
	//			containers.put(target.getAssetID(), target);
	//			this.add2Location(target);
	//		}
	//		subtarget.addChild(asset);
	//	}
	//
	//	private void add2Location(final NeoComAsset asset) {
	//		long locid = asset.getLocationID();
	//		EveLocation target = locations.get(locid);
	//		if (null == target) {
	//			target = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locid);
	//			locations.put(new Long(locid), target);
	//			this.add2Region(target);
	//		}
	//		target.addChild(asset);
	//	}
	//
	//	private void add2Region(final EveLocation target) {
	//		long regionid = target.getRegionID();
	//		Region region = regions.get(regionid);
	//		if (null == region) {
	//			region = new Region(target.getRegion());
	//			regions.put(new Long(regionid), region);
	//		}
	//		region.addLocation(target);
	//	}

	//	/**
	//	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	//	 * because with the flat listing there is only relationship through the location id. <br>
	//	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	//	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	//	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	//	 * because the auto model generation will already include those items. Only Locations or Regions behave
	//	 * differently.
	//	 */
	//	private void processElement (final NeoComAsset asset) {
	//		PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]>>>>> Asset: " + asset);
	//		PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]>>>>> Pending assrts count: " + assetMap.size());
	//		try {
	//			// Remove the element from the map.
	//			assetMap.remove(asset.getAssetID());
	//			// Add the asset to the verification count.
	//			totalAssetsCount++;
	//			// Add the asset value to the owner balance.
	//			totalAssetsValue += asset.getIskValue();
	//			// Transform the asset if on specific categories like Ship or Container
	//			if ( asset.isShip() ) {
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected ship");
	//				// Check if the ship is packaged. If packaged leave it as a simple asset.
	//				if ( !asset.isPackaged() ) {
	//					// Transform the asset to a ship.
	//					Ship ship = new Ship(getCredentialIdentifier()).copyFrom(asset);
	//					//					ships.put(ship.getAssetID(), ship);
	//					// The ship is a container so add it and forget about this asset.
	//					if ( ship.hasParent() ) {
	//						this.processElement(ship.getParentContainer());
	//					} //else {
	//					this.add2Location(ship);
	//					// Remove all the assets contained because they will be added in the call to collaborate2Model
	//					// REFACTOR set the default variant as a constant even that information if defined at other project
	//					List<ICollaboration> removableList = ship.collaborate2Model("DEFAULT");
	//					// The list returned is not the real list of assets contained but the list of Separators
	//					for (ICollaboration node : removableList) {
	//						this.removeNode(node);
	//					}
	//				} else {
	//					this.add2Location(asset);
	//				}
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
	//						+ asset.getAssetID());
	//				return;
	//			}
	//			if ( asset.isContainer() ) {
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected container");
	//				// Check if the asset is packaged. If so leave as asset
	//				if ( !asset.isPackaged() ) {
	//					// Transform the asset to a ship.
	//					container = new SpaceContainer().copyFrom(asset);
	//					containers.put(container.getAssetID(), container);
	//					// The container is a container so add it and forget about this asset.
	//					if ( container.hasParent() ) {
	//						this.processElement(container.getParentContainer());
	//					} // else {
	//					PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Container added to Location");
	//					this.add2Location(container);
	//					//					// Remove all the assets contained because they will be added in the call to collaborate2Model
	//					//					// REFACTOR set the default variant as a constant even that information if defined at other project
	//					//					ArrayList<AbstractComplexNode> removableList = container
	//					//							.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
	//					//					// The list returned is not the real list of assets contained but the list of Separators
	//					//					for (AbstractComplexNode node : removableList) {
	//					//						this.removeNode(node);
	//					//					}
	//				} else {
	//					PlanetaryManager.logger
	//							.info("DD [PlanetaryManager.processElement]> Container is packaged. Add it as an Asset.");
	//					this.add2Location(asset);
	//				}
	//				//				// Remove all the assets contained because they will be added in the call to collaborate2Model
	//				//				ArrayList<AbstractComplexNode> removable = asset.collaborate2Model("REPLACE");
	//				//				for (AbstractComplexNode node : removable) {
	//				//					assetMap.remove(((Container) node).getAssetID());
	//				//				}
	//				//	}
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
	//						+ asset.getAssetID());
	//				return;
	//			}
	//			// Process the asset parent if this is the case because we should add first parent to the hierarchy
	//			if ( asset.hasParent() ) {
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected parent");
	//				NeoComAsset parent = asset.getParentContainer();
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Parent: " + parent);
	//				if ( null == parent ) {
	//					this.add2Location(asset);
	//				} else {
	//					this.processElement(parent);
	//					// Add the current element to the parent already processed.
	//					// REFACTOR The container for the recursion is stored on a field. This is not a good practice.
	//					if ( null != container ) {
	//						PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Added asset: " + asset.getAssetID()
	//								+ " to Container: " + container.getAssetID());
	//						container.addAsset(asset);
	//					}
	//				}
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
	//						+ asset.getAssetID());
	//				return;
	//			}
	//			// Check if the location identifier matches an asset. This item can be contained inside some other.
	//			PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Checking Location: " + asset.getLocationID());
	//			if ( this.isContainer(asset.getLocationID()) ) {
	//				NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(asset.getLocationID());
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Asset connection found: " + target);
	//				asset.setParentContainer(target);
	//				this.processElement(asset);
	//			} else {
	//				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Adding asset to Location");
	//				this.add2Location(asset);
	//				//	}
	//			}
	//		} catch (Exception ex) {
	//			ex.printStackTrace();
	//		}
	//	}

	/**
	 * Process a resource and connect it to all the hierarchy elements required. If the assets has a parent then
	 * process first that parent and then connect the resource to the already ready structure.
	 */
	private NeoComAsset processPlanetaryResource (final NeoComAsset resource) {
		if ( resource.hasParent() ) {
			NeoComAsset processed = this.processPlanetaryResource(resource.getParentContainer());
			if ( processed instanceof IAssetContainer ) {
				((IAssetContainer) processed).addAsset(resource);
			} else {
				this.add2Location(resource);
			}
		} else {
			if ( resource.isContainer() ) {
				// Search for this container id on the list of Containers. Each assets has ita own Container instance.
				NeoComAsset hit = containers.get(resource.getAssetId());
				if ( null == hit ) {
					SpaceContainer cont = new SpaceContainer().copyFrom(resource);
					// BUG. Mark the ships as already downloaded or we can go to the database for their contents.
					cont.setDownloaded(true);
					// Add to the Location only if not already registered.
					this.add2Location(cont);
					// Add the container to the list ot avoid processing it again.
					containers.put(cont.getAssetId(), cont);
					return cont;
				} else
					return hit;
			}
			if ( resource.isShip() ) {
				// Search for this container id on the list of Containers. Each assets has ita own Container instance.
				NeoComAsset hit = containers.get(resource.getAssetId());
				if ( null == hit ) {
					Ship cont = new Ship().copyFrom(resource);
					// BUG. Mark the ships as already downloaded or we can go to the database for their contents.
					cont.setDownloaded(true);
					// Add to the Location only if not already registered.
					this.add2Location(cont);
					// Add the container to the list ot avoid processing it again.
					containers.put(cont.getAssetId(), cont);
					return cont;
				} else
					return hit;
			}
			this.add2Location(resource);
		}
		return resource;
	}
	//	/**
	//	 * Remove the nodes collaborated and their own collaborations recursively from the list of assets to
	//	 * process.
	//	 */
	//	private void removeNode (final ICollaboration node) {
	//		// Check that the class of the item is an Asset. Anyway check for its collaboration.
	//		if ( node instanceof ICollaboration ) {
	//			// Try to remove the asset if found
	//			if ( node instanceof NeoComAsset ) {
	//				assetMap.remove(((NeoComAsset) node).getAssetID());
	//			}
	//			// Remove also the nodes collaborated by it.
	//			for (ICollaboration child : node.collaborate2Model("DEFAULT")) {
	//				this.removeNode(child);
	//			}
	//		}
	//	}

}

// - UNUSED CODE ............................................................................................
