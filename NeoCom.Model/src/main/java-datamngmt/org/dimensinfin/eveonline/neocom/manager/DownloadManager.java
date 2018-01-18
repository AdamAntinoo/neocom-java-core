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

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.parser.corporation.AssetListParser;
import com.beimin.eveapi.parser.pilot.BlueprintsParser;
import com.beimin.eveapi.parser.pilot.LocationsParser;
import com.beimin.eveapi.parser.pilot.PilotAssetListParser;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.beimin.eveapi.response.shared.BlueprintsResponse;
import com.beimin.eveapi.response.shared.LocationsResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.connector.INeoComModelDatabase;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.TimeStamp;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public class DownloadManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3794753126425122302L;
	private static Logger logger = LoggerFactory.getLogger("DownloadManager");

	// - F I E L D - S E C T I O N ............................................................................
	private String jsonClass = "DownloadManager";
	private transient NeoComCharacter _pilot = null;
	private transient Dao<NeoComAsset, String> assetDao = null;
	private Vector<NeoComAsset> unlocatedAssets = null;
	/** Time stamp for the time when character data is cached. */
	public TimeStamp _characterCacheTime = null;
	/** Time stamp for the time when the asset data downloaded is cached. */
	public TimeStamp _assetsCacheTime = null;
	/** Time stamp for the time when the asset data downloaded is cached. */
	public TimeStamp _blueprintsCacheTime = null;
	/** The complete list of blueprints maybe is not used */
	private final Vector<NeoComBlueprint> blueprintCache = new Vector<NeoComBlueprint>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DownloadManager (final NeoComCharacter pilot) {
		_pilot = pilot;
		jsonClass = "DownloadManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@JsonIgnore
	public NeoComCharacter getPilot () {
		return _pilot;
	}
	public void setPilot (final NeoComCharacter newPilot) {
		_pilot = newPilot;
	}

	public String getJsonClass () {
		return jsonClass;
	}
	public void updateCharacterDataTimeStamp (final Date cachedUntil) {
		// Update the caching time to the time set by the eveapi.
		final String reference = getPilot().getCharacterID() + ".CHARACTERDATA";
		if ( null == _characterCacheTime ) {
			_characterCacheTime = new TimeStamp(reference, new Instant(cachedUntil));
		} else {
			_characterCacheTime.updateTimeStamp(new Instant(cachedUntil));
		}
	}

	/**
	 * The new downloader uses the eveapi library to process the xml source code received. This simplifies the
	 * code but adds the need to control the download format. There are two sets of records, one with the
	 * hierarchical dependencies between the assets and the other a flat list of all the assets with no
	 * dependencies but with a mix of Location and Asset codes on the locationId field.<br>
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the
	 * final list on the database will have the correct parentship hierarchy set up.<br>
	 * <br>
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
	 * <br>
	 * There are two flavour for the asset download process. One for Pilots and other for Corporation assets.
	 */
	public void downloadPilotAssets () {
		DownloadManager.logger.info(">> [AssetsManager.downloadPilotAssets]");
		try {
			// Clear any previous record with owner -1 from database.
			INeoComModelDatabase dbConn = ModelAppConnector.getSingleton().getDBConnector();
			synchronized (dbConn) {
				dbConn.clearInvalidRecords(this.getPilot().getCharacterID());
			}
			// Parse the CCP data to a list of assets
			PilotAssetListParser parser = new PilotAssetListParser();
			AssetListResponse response = parser.getResponse(this.getPilot().getAuthorization());
			if ( null != response ) {
				unlocatedAssets = new Vector<NeoComAsset>();
				List<Asset> assets = response.getAll();
				// Assets may be parent of other assets so process them recursively if the hierarchical mode is selected.
				for (final Asset eveAsset : assets) {
					try {
						this.processAsset(eveAsset, null);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
				// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
				for (NeoComAsset asset : unlocatedAssets) {
					this.validateLocation(asset);
				}
				// Assign the assets to the pilot.
				synchronized (dbConn) {
					dbConn.replaceAssets(this.getPilot().getCharacterID());
				}
				// Update the caching time to the time set by the eveapi.
				String reference = this.getPilot().getCharacterID() + ".ASSETDATA";
				if ( null ==_assetsCacheTime ) {
					_assetsCacheTime = new TimeStamp(reference, new Instant(response.getCachedUntil()));
				} else {
					_assetsCacheTime.updateTimeStamp(new Instant(response.getCachedUntil()));
				}
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadPilotAssets]");
	}

	/**
	 * Download the blueprint list for this character from CCP using the new API over the eveapi library and
	 * then processes the response. It creates our model Blueprints that before being stored at the database are
	 * grouped into stacks to reduce the number of registers to manage on other Industry operations.<br>
	 * Current grouping is by IF-LOCATION-CONTAINER.
	 */
	public synchronized void downloadPilotBlueprints () {
		try {
			Chrono chrono = new Chrono();
			// Clear any previous records with owner -1 from database.
			ModelAppConnector.getSingleton().getDBConnector().clearInvalidRecords(this.getPilot().getCharacterID());
			// Download and parse the blueprints using the eveapi.
			ArrayList<NeoComBlueprint> bplist = new ArrayList<NeoComBlueprint>();
			BlueprintsParser parser = new BlueprintsParser();
			BlueprintsResponse response = parser.getResponse(this.getPilot().getAuthorization());
			if ( null != response ) {
				Set<Blueprint> blueprints = response.getAll();
				for (Blueprint bp : blueprints) {
					try {
						bplist.add(this.convert2Blueprint(bp));
					} catch (final RuntimeException rtex) {
						// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
						DownloadManager.logger.info("W> The Blueprint " + bp.getItemID() + " has no matching asset.");
						DownloadManager.logger.info("W> " + bp.toString());
					}
				}
			}
			// Pack the blueprints and store them on the database.
			storeBlueprints(bplist);
			ModelAppConnector.getSingleton().getDBConnector().replaceBlueprints(this.getPilot().getCharacterID());
			// Update the caching time to the time set by the eveapi.
			String reference = this.getPilot().getCharacterID() + ".BLUEPRINTDATA";
			if ( null == _blueprintsCacheTime ) {
				_blueprintsCacheTime = new TimeStamp(reference, new Instant(response.getCachedUntil()));
			} else {
				_blueprintsCacheTime.updateTimeStamp(new Instant(response.getCachedUntil()));
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		final Duration lapse = ModelAppConnector.getSingleton().timeLapse();
		DownloadManager.logger.info("~~ Time lapse for [UPDATEBLUEPRINTS] - " + lapse);
	}

	/**
	 * The processing of the assets will be performed with a SAX parser instead of the general use of a DOM
	 * parser. This requires then that the cache verification and other cache tasks be performed locally to
	 * avoid downloading the same information multiple times.<br>
	 * Cache expiration is of 6 hours but we will set it up to 3.<br>
	 * After verification we have to update the list, we then fire the events to signal asset list modification
	 * to any dependent data structures or UI objects that may be showing this information.<br>
	 * This update mechanism may require reading the last known state of the assets list from the sdcard file
	 * storage. This information is not stored automatically with the character information to speed up the
	 * initialization process and is loading only when needed and this data should be accessed. This is an
	 * special case because the assets downloaded are being written to a special set of records in the User
	 * database. Then, after the download terminates the database is updated to move those assets to the right
	 * character. It is supposed that this is performed in the background and that while we are doing this the
	 * uses has access to an older set of assets. New implementation. With the use of the eveapi library there
	 * is no need to use the URL to locate and download the assets. We use the eveapi locator and parser to get
	 * the data structures used to generate and store the assets into the local database. We first clear any
	 * database records not associated to any owner, the add records for a generic owner and finally change the
	 * owner to this character.
	 */
	public void downloadCorporationAssets () {
		DownloadManager.logger.info(">> [AssetsManager.downloadCorporationAssets]");
		try {
			// Clear any previous record with owner -1 from database.
			ModelAppConnector.getSingleton().getDBConnector().clearInvalidRecords(this.getPilot().getCharacterID());
			// Download and parse the assets. Check the api key to detect corporations and use the other parser.
			//			AssetListResponse response = null;
			//			if (getName().equalsIgnoreCase("Corporation")) {
			//				AssetListParser parser = com.beimin.eveapi.corporation.assetlist.AssetListParser.getInstance();
			//				response = parser.getResponse(getAuthorization());
			//				if (null != response) {
			//					final HashSet<EveAsset> assets = new HashSet<EveAsset>(response.getAll());
			//					assetsCacheTime = new Instant(response.getCachedUntil());
			//					// Assets may be parent of other assets so process them recursively.
			//					for (final EveAsset eveAsset : assets) {
			//						processAsset(eveAsset, null);
			//					}
			//				}
			//			} else {
			AssetListParser parser = new AssetListParser();
			AssetListResponse response = parser.getResponse(this.getPilot().getAuthorization());
			if ( null != response ) {
				List<Asset> assets = response.getAll();
				//				this.getPilot().updateAssetsAccesscacheTime(response.getCachedUntil());
				// Assets may be parent of other assets so process them recursively.
				for (final Asset eveAsset : assets) {
					this.processAsset(eveAsset, null);
				}
			}
			//			}
			ModelAppConnector.getSingleton().getDBConnector().replaceAssets(this.getPilot().getCharacterID());

			// Update the caching time to the time set by the eveapi.
			String reference = this.getPilot().getCharacterID() + ".ASSETDATA";
			if ( null == _assetsCacheTime) {
				_assetsCacheTime = new TimeStamp(reference, new Instant(response.getCachedUntil()));
			} else {
				_assetsCacheTime.updateTimeStamp(new Instant(response.getCachedUntil()));
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadCorporationAssets");
	}

	private String downloadAssetEveName (final long assetID) {
		// Wait up to one second to avoid request rejections from CCP.
		try {
			Thread.sleep(500); // 500 milliseconds is half second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		final Vector<Long> ids = new Vector<Long>();
		ids.add(assetID);
		try {
			final LocationsParser parser = new LocationsParser();
			final LocationsResponse response = parser.getResponse(this.getPilot().getAuthorization(), ids);
			if ( null != response ) {
				Set<Location> userNames = response.getAll();
				if ( userNames.size() > 0 ) return userNames.iterator().next().getItemName();
			}
		} catch (final ApiException e) {
			DownloadManager.logger.info("W- EveChar.downloadAssetEveName - asset has no user name defined: " + assetID);
		}
		return null;
	}

	/**
	 * Processes an asset and all their children. This method converts from a API record to a database asset
	 * record.<br>
	 * For flat assets it will detect the Location and if matched to an unknown location store the asset for
	 * second pass processing.
	 */
	private void processAsset (final Asset eveAsset, final NeoComAsset parent) {
		final NeoComAsset myasset = this.convert2Asset(eveAsset);
		if ( null != parent ) {
			//			myasset.setParent(parent);
			myasset.setParentContainer(parent);
			// Set the location to the parent's location is not set.
			if ( myasset.getLocationID() == -1 ) {
				myasset.setLocationID(parent.getLocationID());
			}
		}
		// Only search names for containers and ships.
		if ( myasset.isShip() ) {
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		if ( myasset.isContainer() ) {
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		try {
			//			final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
			this.accessDaos();
			final HashSet<Asset> children = new HashSet<Asset>(eveAsset.getAssets());
			if ( children.size() > 0 ) {
				myasset.setContainer(true);
			}
			if ( myasset.getCategory().equalsIgnoreCase("Ship") ) {
				myasset.setShip(true);
			}
			myasset.setOwnerID(this.getPilot().getCharacterID() * -1);
			assetDao.create(myasset);

			// Check the asset location. The location can be a known game station, a known user structure, another asset
			// or an unknown player structure. Check which one is this location.
			EveLocation targetLoc = ModelAppConnector.getSingleton().getCCPDBConnector()
			                                         .searchLocationbyID(myasset.getLocationID());
			if ( targetLoc.getTypeID() == ELocationType.UNKNOWN ) {
				// Add this asset to the list of items to be reprocessed.
				unlocatedAssets.add(myasset);
			}
			// Process all the children and convert them to assets.
			if ( children.size() > 0 ) {
				for (final Asset childAsset : children) {
					this.processAsset(childAsset, myasset);
				}
			}
			DownloadManager.logger.info("-- Wrote asset to database id [" + myasset.getAssetID() + "]");
		} catch (final SQLException sqle) {
			DownloadManager.logger.error("E> [AssetsManager.processAsset]Unable to create the new asset ["
					+ myasset.getAssetID() + "]. " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}

	/**
	 * Checks if the Location can be found on the two lists of Locations, the CCP game list or the player
	 * compiled list. If the Location can't be found on any of those lists then it can be another asset
	 * (Container, Ship, etc) or another player/corporation structure resource that is not listed on the asset
	 * list.
	 */
	private ELocationType validateLocation (final NeoComAsset asset) {
		long targetLocationid = asset.getLocationID();
		EveLocation targetLoc = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(targetLocationid);
		if ( targetLoc.getTypeID() == ELocationType.UNKNOWN ) {
			// Need to check if asset or unreachable location.
			NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(targetLocationid);
			if ( null == target )
				return ELocationType.UNKNOWN;
			else {
				// Change the asset parentship and update the asset location with the location of the parent.
				asset.setParentId(targetLocationid);
				//// search for the location of the parent.
				//ELocationType parentLocationType = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(target.getLocationID()).getTypeID();
				//if(parentLocationType!=ELocationType.UNKNOWN)
				asset.setLocationID(target.getLocationID());
				asset.setDirty(true);
			}
			return ELocationType.UNKNOWN;
		} else
			return targetLoc.getTypeID();
	}

	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information. <br>
	 * This method checks the location to detect if under the new flat model the location is an asset and then
	 * we should convert it into a parent or the location is a real location. Initially this is done checking
	 * the location id value if under 1000000000000.
	 *
	 * @param eveAsset the original assest as downloaded from CCP api
	 */
	private NeoComAsset convert2Asset (final Asset eveAsset) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		Long locid = eveAsset.getLocationID();
		if ( null == locid ) {
			locid = (long) -2;
		}
		newAsset.setLocationID(locid);

		newAsset.setQuantity(eveAsset.getQuantity());
		newAsset.setFlag(eveAsset.getFlag());
		newAsset.setSingleton(eveAsset.getSingleton());

		// Get access to the Item and update the copied fields.
		final EveItem item = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(newAsset.getTypeID());
		if ( null != item ) {
			try {
				newAsset.setName(item.getName());
				newAsset.setCategory(item.getCategory());
				newAsset.setGroupName(item.getGroupName());
				newAsset.setTech(item.getTech());
				if ( item.isBlueprint() ) {
					newAsset.setBlueprintType(eveAsset.getRawQuantity());
				}
			} catch (RuntimeException rtex) {
			}
		}
		// Add the asset value to the database.
		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	private synchronized double calculateAssetValue (final NeoComAsset asset) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if ( null != asset ) {
			EveItem item = asset.getItem();
			if ( null != item ) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if ( null != category ) if ( !category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint) ) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = asset.getItem().getHighestBuyerPrice().getPrice();
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	private void accessDaos () {
		if ( null == assetDao ) {
			try {
				assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
				if ( null == assetDao ) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
			} catch (SQLException sqle) {
				// Interrupt processing and signal a runtime exception.
				throw new RuntimeException(sqle.getMessage());
			}
		}
	}

	/**
	 * Gets the list of blueprints from the API processor and packs them into stacks aggregated by some keys.
	 * This will simplify the quantity of data exported to presentation layers.<br>
	 * Aggregation is performed by TYPEID-LOCATION-CONTAINER-RUNS
	 *
	 * @param bplist list of newly created Blueprints from the CCP API download
	 */
	public void storeBlueprints (final ArrayList<NeoComBlueprint> bplist) {
		HashMap<String, NeoComBlueprint> bpStacks = new HashMap<String, NeoComBlueprint>();
		for (NeoComBlueprint blueprint : bplist) {
			this.checkBPCStacking(bpStacks, blueprint);
		}

		// Extract stacks and store them into the caches.
		blueprintCache.addAll(bpStacks.values());
		// Update the database information.
		for (NeoComBlueprint blueprint : blueprintCache) {
			try {
				Dao<NeoComBlueprint, String> blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDAO();
				// Be sure the owner is reset to undefined when stored at the database.
				blueprint.resetOwner();
				// Set new calculated values to reduce the time for blueprint part rendering.
				// REFACTOR This has to be rewrite to allow this calculation on download time.
				//				IJobProcess process = JobManager.generateJobProcess(getPilot(), blueprint, EJobClasses.MANUFACTURE);
				//				blueprint.setManufactureIndex(process.getProfitIndex());
				//				blueprint.setJobProductionCost(process.getJobCost());
				//				blueprint.setManufacturableCount(process.getManufacturableCount());
				blueprintDao.create(blueprint);
				DownloadManager.logger.info("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
			} catch (final SQLException sqle) {
				DownloadManager.logger.error("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. "
						+ sqle.getMessage());
				sqle.printStackTrace();
			} catch (final RuntimeException rtex) {
				DownloadManager.logger.error("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. "
						+ rtex.getMessage());
				rtex.printStackTrace();
			}
		}
	}

	protected NeoComBlueprint convert2Blueprint (final Blueprint eveBlue) {
		// Create the asset from the API asset.
		final NeoComBlueprint newBlueprint = new NeoComBlueprint(eveBlue.getItemID());
		newBlueprint.setTypeID(eveBlue.getTypeID());
		newBlueprint.setTypeName(eveBlue.getTypeName());
		newBlueprint.setLocationID(eveBlue.getLocationID());
		newBlueprint.setFlag(eveBlue.getFlagID());
		newBlueprint.setQuantity(eveBlue.getQuantity());
		newBlueprint.setTimeEfficiency(eveBlue.getTimeEfficiency());
		newBlueprint.setMaterialEfficiency(eveBlue.getMaterialEfficiency());
		newBlueprint.setRuns(eveBlue.getRuns());
		newBlueprint.setPackaged((eveBlue.getQuantity() == -1) ? true : false);

		// Detect if BPO or BPC and set the flag.
		if ( eveBlue.getRuns() == -1 ) {
			newBlueprint.setBpo(true);
		}
		return newBlueprint;
	}

	/**
	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
	 * same container so the locationID, the parentContainer and the typeID should match to perform the
	 * aggregation.<br>
	 * Aggregation key: ID-LOCATION-CONTAINER
	 *
	 * @param targetContainer the stack storage that contains the list of registered blueprints
	 * @param bp              the blueprint part to be added to the hierarchy
	 */
	private void checkBPCStacking (final HashMap<String, NeoComBlueprint> targetContainer, final NeoComBlueprint bp) {
		// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.ASSETID
		String id = bp.getStackID();
		NeoComBlueprint hit = targetContainer.get(id);
		if ( null == hit ) {
			// Miss. The blueprint is not registered.
			DownloadManager.logger.info("-- AssetsManager.checkBPCStacking >Stacked blueprint. " + bp.toString());
			bp.registerReference(bp.getAssetID());
			targetContainer.put(id, bp);
		} else {
			//Hit. Increment the counter for this stack. And store the id
			hit.setQuantity(hit.getQuantity() + bp.getQuantity());
			hit.registerReference(bp.getAssetID());
		}
	}
}

// - UNUSED CODE ............................................................................................
