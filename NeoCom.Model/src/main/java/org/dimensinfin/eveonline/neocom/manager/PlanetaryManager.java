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

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChonoOptions;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.model.SpaceContainer;
import org.dimensinfin.eveonline.neocom.planetary.Colony;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryManager extends AbstractManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3794750126425122302L;
	//	private static Logger														logger						= Logger.getLogger("PlanetaryManager");

	// - F I E L D - S E C T I O N ............................................................................
	public long totalAssets = 0;
	public double totalAssetsValue = 0.0;
	public final String iconName = "planets.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	/** Shared field to allow to share the list of assets to process between methods during the processing. */
	@JsonIgnore
	private transient Hashtable<Long, NeoComAsset> assetMap = new Hashtable<Long, NeoComAsset>();

	private SpaceContainer container = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryManager (final Credential credential) {
		super(credential);
		jsonClass = "PlanetaryManager";
	}

	@Deprecated
	public PlanetaryManager (final NeoComCharacter pilot) {
		super(pilot);
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
																																	 .accessAllPlanetaryAssets(this.getPilot().getCharacterID());
			totalAssets = planetaryAssetList.size();
			// Process the Resources and search for the parent assets to classify them into the Locations.
			for (NeoComAsset resource : planetaryAssetList) {
				// Check if the Resource has a parent.
				//				if (resource.hasParent()) {
				//					this.processPlanetaryResource(resource.getParentContainer());
				//				} else {
				this.processPlanetaryResource(resource);
				//				}
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			PlanetaryManager.logger
					.severe("RTEX> [PlanetaryManager.accessAllAssets]> There is a problem processing the Planetary Resources.");
		} finally {
			PlanetaryManager.logger.info(">< [AssetsManager.accessAllAssets]> Assets processed: " + totalAssets);
		}
	}

	private long getCharacterId () {
		return _credential.getAccountId();
	}

	public interface ColonyApi {
		@GET("characters/{character_id}/planets/")
		Call<List<GetCharactersCharacterIdPlanets200Ok>> getCharactersCharacterIdPlanets (
				@Path("character_id") Integer characterId, @Query("datasource") String datasource, @Query("token") String token, @Query("user_agent") String userAgent, @Header("X-User-Agent") String xUserAgent
		);

		@GET("characters/{character_id}/planets/{planet_id}/")
		Call<GetCharactersCharacterIdPlanetsPlanetIdOk> getCharactersCharacterIdPlanetsPlanetId (
				@Path("character_id") Integer characterId, @Path("planet_id") Integer planetId, @Query("datasource") String datasource, @Query("token") String token, @Query("user_agent") String userAgent, @Header("X-User-Agent") String xUserAgent
		);
	}

	/**
	 * Get all the Planetary Colonies for a selected character. After getting the list of colonies and wrapping
	 * all that data into MVC compatible classes it will launch background jobs to retrieve each colony detailed
	 * data.
	 */
	public List<Colony> accessAllColonies () {
		logger.info(">> [ColonyManager.accessAllColonies]");
		final Chrono accessFullTime = new Chrono();

		try {
			// Create a request to the ESI api downloader to get the list of Planets to the current Character.
			final long charId = getCharacterId();
			final ColonyApi colonyApiRetrofit = new Builder()
					.baseUrl("https://login.eveonline.com")
					.addConverterFactory(JacksonConverterFactory.create())
					.build()
					.create(ColonyApi.class);
			final Response<List<GetCharactersCharacterIdPlanets200Ok>> r = colonyApiRetrofit
					.getCharactersCharacterIdPlanets((int) charId, null, null, null, null)
					.execute();
			if ( !r.isSuccessful() ) {
				return new Vector();
			}

			// Transform the received OK instance into a NeoCom compatible model instance.
			ModelMapper modelMapper = new ModelMapper();
			List<Colony> colonies = new Vector();
			for(GetCharactersCharacterIdPlanets200Ok colonyOK :r.body()) {
				Colony col = modelMapper.map(r.body(), Colony.class);
				colonies.add(col);
			}

			// For each of the received planets, get their structures and do the same transformations.
			Stream.of(colonies).forEach(c -> {
				try {
					final Response<GetCharactersCharacterIdPlanetsPlanetIdOk> rp =
							this.planetaryApi.getCharactersCharacterIdPlanetsPlanetId((int) charId, (int) c.getPlanetId(), this.datasource, null, null, null)
															 .execute();
					if ( rp.isSuccessful() ) {
						//>>> Modified by Adam Antinoo
						ESIMapper.map(rp.body(), c);
					}
				} catch (IOException e) {
					LOG.debug(e.getLocalizedMessage(), e);
					LOG.error(e.getLocalizedMessage());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}


		return colonies;


		final PlanetaryColoniesRequest request = _network.PlanetaryColoniesRequest();
		final PlanetaryColoniesResponse colonies = _network.execute(request);
		logger.debug(">> [ColonyManager.accessAllColonies]> [Elapsed Time]- PlanetaryColoniesRequest ", chrono.printElapsed(ChonoOptions.SHOWMILLIS));
		// Converts data to MVC
		List<Colony> presentColonies = new Vector();
		for (PlanetaryColony col : colonies.getColonies()) {
			final Colony newcol = ColonyFactory.from(col);
			presentColonies.add(newcol);
			logger.debug(">> [ColonyManager.accessAllColonies]> newcol: ", newcol.toString());
			logger.debug(">> [ColonyManager.accessAllColonies]> Submitting another job to Executor");
			newcol.setDownloading(true);
			ApplicationCloudAdapter.getSingleton().getDownloadExecutor().submit(
					new OneParameterTask<Colony>(newcol) {
						public void run () {
							logger.info(">> [ColonyManager.accessAllColonies.OneParameterTask.run]");
							Chrono chrono = new Chrono();

							final PlanetaryPinsRequest pinrequest = _network.PlanetaryPinsRequest(getTarget().getPlanetID());
							final PlanetaryPinsResponse pins = _network.execute(pinrequest);
							logger.debug(">> [ColonyManager.accessAllColonies.OneParameterTask.run]> [Elapsed Time]- PlanetaryPinsRequest "
									, chrono.printElapsed(ChonoOptions.SHOWMILLIS));
							for (PlanetaryPin pin : pins.getPins()) {
								final ColonyStructure structure = ColonyStructureFactory.from(pin);
								getTarget().addStructure(structure);
							}
							// Mark the download complete.
							getTarget().setDownloading(false);
							getTarget().setDownloaded(true);
							// Wait a delay of 1 second to allow to watch the counter.
							try {
								Thread.sleep(TimeUnit.SECONDS.toMillis(4));
							} catch (InterruptedException ex) {
							}
							// Send message to the model to update the rendering.
							logger.debug(">> [ColonyManager.accessAllColonies]> Target: ", getTarget().toString());
							logger.debug(">> [ColonyManager.accessAllColonies.OneParameterTask.run]> Going to fire the STRUCTURE message");
							getTarget().fireStructureChange("EVENTSTRUCTURE_DOWNLOADDATA", getTarget(), null);

							logger.info("<< [ColonyManager.accessAllColonies.OneParameterTask.run]> Time Elapsed: " + chrono.printElapsed(Chrono.ChonoOptions.SHOWMILLIS));
						}
					}
			);
		}
		logger.debug("-- [ColonyManager.accessAllColonies]> [Elapsed Time]- Colony Model Generation", chrono.printElapsed(ChonoOptions.SHOWMILLIS));
		logger.info("<< [ColonyManager.accessAllColonies]> Colonies on list: ", presentColonies.size());
		return presentColonies;
	}

	public long getAssetTotalCount () {
		//		if (!this.isInitialized()) {
		this.initialize();
		//		}
		return totalAssets;
		//		if (null == planetaryAssetList)
		//			return 0;
		//		else
		//			return planetaryAssetList.size();
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
				results.add(new Resource(node.getTypeID(), node.getQuantity()));
			}
			return results;
		}
		return new Vector<Resource>();
	}

	public String getOrderingName () {
		return "Planetary Manager";
	}

	public long getTotalAssets () {
		return totalAssets;
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
	protected void add2Location (final NeoComAsset asset) {
		long locid = asset.getLocationID();
		ExtendedLocation target = locations.get(locid);
		if ( null == target ) {
			EveLocation intermediary = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locid);
			// Create another new Extended Location as a copy if this one to disconnect it from the unique cache copy.
			ExtendedLocation newloc = new ExtendedLocation(this.getPilot(), intermediary);
			newloc.setContentManager(new PlanetaryAssetsContentManager(newloc));
			newloc.setDownloaded(true);
			locations.put(new Long(locid), newloc);
			this.add2Region(newloc);
			newloc.addContent(asset);
		} else {
			target.addContent(asset);
		}
	}

	private boolean isContainer (final long identifier) {
		// Search for this asset on the database. If it is a ship or a container, add it to the list of assets.
		NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(identifier);
		if ( null == target )
			return false;
		else {
			// It it is a ship then add the bay as a container. Otherwise add the container and process it as a new asset.
			if ( target.isContainer() ) return true;
			if ( target.isShip() ) return true;
		}
		return false;
	}

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

	/**
	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	 * because with the flat listing there is only relationship through the location id. <br>
	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	 * because the auto model generation will already include those items. Only Locations or Regions behave
	 * differently.
	 */
	private void processElement (final NeoComAsset asset) {
		PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]>>>>> Asset: " + asset);
		PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]>>>>> Pending assrts count: " + assetMap.size());
		try {
			// Remove the element from the map.
			assetMap.remove(asset.getAssetID());
			// Add the asset to the verification count.
			totalAssets++;
			// Add the asset value to the owner balance.
			totalAssetsValue += asset.getIskValue();
			// Transform the asset if on specific categories like Ship or Container
			if ( asset.isShip() ) {
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected ship");
				// Check if the ship is packaged. If packaged leave it as a simple asset.
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					Ship ship = new Ship(this.getPilot().getCharacterID()).copyFrom(asset);
					//					ships.put(ship.getAssetID(), ship);
					// The ship is a container so add it and forget about this asset.
					if ( ship.hasParent() ) {
						this.processElement(ship.getParentContainer());
					} //else {
					this.add2Location(ship);
					// Remove all the assets contained because they will be added in the call to collaborate2Model
					// REFACTOR set the default variant as a constant even that information if defined at other project
					List<ICollaboration> removableList = ship.collaborate2Model("DEFAULT");
					// The list returned is not the real list of assets contained but the list of Separators
					for (ICollaboration node : removableList) {
						this.removeNode(node);
					}
				} else {
					this.add2Location(asset);
				}
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
						+ asset.getAssetID());
				return;
			}
			if ( asset.isContainer() ) {
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected container");
				// Check if the asset is packaged. If so leave as asset
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					container = new SpaceContainer().copyFrom(asset);
					containers.put(container.getAssetID(), container);
					// The container is a container so add it and forget about this asset.
					if ( container.hasParent() ) {
						this.processElement(container.getParentContainer());
					} // else {
					PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Container added to Location");
					this.add2Location(container);
					//					// Remove all the assets contained because they will be added in the call to collaborate2Model
					//					// REFACTOR set the default variant as a constant even that information if defined at other project
					//					ArrayList<AbstractComplexNode> removableList = container
					//							.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
					//					// The list returned is not the real list of assets contained but the list of Separators
					//					for (AbstractComplexNode node : removableList) {
					//						this.removeNode(node);
					//					}
				} else {
					PlanetaryManager.logger
							.info("DD [PlanetaryManager.processElement]> Container is packaged. Add it as an Asset.");
					this.add2Location(asset);
				}
				//				// Remove all the assets contained because they will be added in the call to collaborate2Model
				//				ArrayList<AbstractComplexNode> removable = asset.collaborate2Model("REPLACE");
				//				for (AbstractComplexNode node : removable) {
				//					assetMap.remove(((Container) node).getAssetID());
				//				}
				//	}
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
						+ asset.getAssetID());
				return;
			}
			// Process the asset parent if this is the case because we should add first parent to the hierarchy
			if ( asset.hasParent() ) {
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Detected parent");
				NeoComAsset parent = asset.getParentContainer();
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Parent: " + parent);
				if ( null == parent ) {
					this.add2Location(asset);
				} else {
					this.processElement(parent);
					// Add the current element to the parent already processed.
					// REFACTOR The container for the recursion is stored on a field. This is not a good practice.
					if ( null != container ) {
						PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Added asset: " + asset.getAssetID()
								+ " to Container: " + container.getAssetID());
						container.addAsset(asset);
					}
				}
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]<<<<< Completed processing: "
						+ asset.getAssetID());
				return;
			}
			// Check if the location identifier matches an asset. This item can be contained inside some other.
			PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Checking Location: " + asset.getLocationID());
			if ( this.isContainer(asset.getLocationID()) ) {
				NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(asset.getLocationID());
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Asset connection found: " + target);
				asset.setParentContainer(target);
				this.processElement(asset);
			} else {
				PlanetaryManager.logger.info("DD [PlanetaryManager.processElement]> Adding asset to Location");
				this.add2Location(asset);
				//	}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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
				NeoComAsset hit = containers.get(resource.getAssetID());
				if ( null == hit ) {
					SpaceContainer cont = new SpaceContainer().copyFrom(resource);
					// Add to the Location only if not already registered.
					this.add2Location(cont);
					// Add the container to the list ot avoid processing it again.
					containers.put(cont.getAssetID(), cont);
					return cont;
				} else
					return hit;
			}
			if ( resource.isShip() ) {
				// Search for this container id on the list of Containers. Each assets has ita own Container instance.
				NeoComAsset hit = containers.get(resource.getAssetID());
				if ( null == hit ) {
					Ship cont = new Ship().copyFrom(resource);
					// Add to the Location only if not already registered.
					this.add2Location(cont);
					// Add the container to the list ot avoid processing it again.
					containers.put(cont.getAssetID(), cont);
					return cont;
				} else
					return hit;
			}
			this.add2Location(resource);
		}
		return resource;
	}

	//	private void add2Container(final NeoComAsset asset) {
	//		long id = asset.getLocationID();
	//		NeoComAsset subtarget = containers.get(id);
	//		if (null == subtarget) {
	//			if (target instanceof IAssetContainer) {
	//				((IAssetContainer) target).addContent(asset);
	//			}
	//			containers.put(target.getAssetID(), target);
	//			//			this.add2Location(target);
	//		} else {
	//			subtarget.addChild(asset);
	//		}
	//	}

	//
	//			
	//			
	//
	//			
	//			
	//			
	//			if (null == parent) return null;
	//		// This is the recursive part to get the complete chain.
	////		if (parent.hasParent()) {
	//			NeoComAsset target = this.processPlanetaryResource(parent.getParentContainer());
	//			// Add asset to the chain.
	//			if (target instanceof IAssetContainer) {
	//				((IAssetContainer) target).addContent(parent);
	//			}
	//			return target;
	////		} else {
	//			// Get the asset (a Container or a Ship) and add it to the chain.
	//			//			NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(parent.getAssetID());
	//			//			if (null != parent) {
	//			return parent;
	//			//			} else
	//			//				return null;
	//		}
	//	}
	//
	//	private void process() {
	//		if (null != parentRef) {
	//		}
	//	}else
	//
	//	{
	//		this.add2Location(resource);
	//	}
	//}
	//
	//	}

	/**
	 * Remove the nodes collaborated and their own collaborations recursively from the list of assets to
	 * process.
	 */
	private void removeNode (final ICollaboration node) {
		// Check that the class of the item is an Asset. Anyway check for its collaboration.
		if ( node instanceof ICollaboration ) {
			// Try to remove the asset if found
			if ( node instanceof NeoComAsset ) {
				assetMap.remove(((NeoComAsset) node).getAssetID());
			}
			// Remove also the nodes collaborated by it.
			for (ICollaboration child : node.collaborate2Model("DEFAULT")) {
				this.removeNode(child);
			}
		}
	}

}

// - UNUSED CODE ............................................................................................
