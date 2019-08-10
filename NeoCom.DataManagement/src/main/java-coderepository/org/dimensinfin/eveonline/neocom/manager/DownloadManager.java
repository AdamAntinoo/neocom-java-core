//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.joda.time.Instant;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.services.TimedUpdater;

// - CLASS IMPLEMENTATION ...................................................................................
public class DownloadManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3794753126425122302L;
	private static Logger logger = LoggerFactory.getLogger("DownloadManager");
	private static final DownloadManager singleton = new DownloadManager();
	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setMethodAccessLevel(AccessLevel.PRIVATE);
	}

	private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();

	private static void submit2downloadExecutor( final Runnable task ) {
		downloadExecutor.submit(task);
	}

	public static String constructReference( final GlobalDataManager.EDataUpdateJobs type, final long identifier ) {
		return new StringBuffer(type.name()).append("/").append(identifier).toString();
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - F I E L D - S E C T I O N ............................................................................
	//	private String jsonClass = "DownloadManager";
	private transient Credential credential = null;

	//	private transient NeoComCharacter _pilot = null;
	private transient Dao<NeoComAsset, String> assetDao = null;
	private List<NeoComAsset> unlocatedAssets = null;
	/**
	 * Time stamp for the time when character data is cached.
	 */
	public TimeStamp _characterCacheTime = null;
	/**
	 * Time stamp for the time when the asset data downloaded is cached.
	 */
	public TimeStamp _assetsCacheTime = null;
	/**
	 * Time stamp for the time when the asset data downloaded is cached.
	 */
	public TimeStamp _blueprintsCacheTime = null;
	/**
	 * The complete list of blueprints maybe is not used
	 */
	private final Vector<NeoComBlueprint> blueprintCache = new Vector<NeoComBlueprint>();

	private HashMap<Long, NeoComAsset> containerCache=new HashMap();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................


	private NeoComAsset convert2AssetFromESI( final GetCharactersCharacterIdAssets200Ok asset200Ok ) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset(asset200Ok.getTypeId())
				.setAssetId(asset200Ok.getItemId());
		//	.setTypeId(asset200Ok.getTypeId());
		Long locid = asset200Ok.getLocationId();
		if (null == locid) {
			locid = (long) -2;
		}
		newAsset.setLocationId(locid)
				.setLocationType(asset200Ok.getLocationType())
				.setQuantity(asset200Ok.getQuantity())
				.setFlag(asset200Ok.getLocationFlag())
				.setSingleton(asset200Ok.getIsSingleton());
		// Get access to the Item and update the copied fields.
		final EveItem item = GlobalDataManager.searchItem4Id(newAsset.getTypeId());
		if (null != item) {
			try {
				newAsset.setName(item.getName());
				newAsset.setCategory(item.getCategory());
				newAsset.setGroupName(item.getGroupName());
				newAsset.setTech(item.getTech());
				if (item.isBlueprint()) {
					//			newAsset.setBlueprintType(eveAsset.getRawQuantity());
				}
			} catch (RuntimeException rtex) {
			}
		}
		// Add the asset value to the database.
		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	public boolean downloadColonyList( final Credential credential ) {
		DownloadManager.logger.info(">> [DownloadManager.downloadColonyList]");
		Chrono totalDownloadTime = new Chrono();
		DownloadManager.logger.info("-- [DownloadManager.downloadColonyList]> Download colony list for identifier: {}", credential.getAccountId());
		//		server=ModelAppConnector.getSingleton().getResourceString(R.string.esisourceserver);
		final String server = "tranquility";
		final List<GetCharactersCharacterIdPlanets200Ok> colonyInstances = ESINetworkManager.getCharactersCharacterIdPlanets(credential.getAccountId(), credential.getRefreshToken(), server);

		// Transform the received OK instance into a NeoCom compatible model instance.
		for (GetCharactersCharacterIdPlanets200Ok colonyOK : colonyInstances) {
			Colony col = modelMapper.map(colonyOK, Colony.class);
			DownloadManager.logger.info("-- [DownloadManager.downloadColonyList]> Transformed colony: {}", col.getPlanetId());
			col.store();


			//			// Block to add additional data not downloaded on this call.
			//			// To set mre information about this particular planet we should call the Universe database.
			//			//			ApplicationCloudAdapter.submit2downloadExecutor(() -> {
			//			final GetUniversePlanetsPlanetIdOk planetData = ESINetworkManager.getUniversePlanetsPlanetId(col.getPlanetId(), credential.getRefreshToken(), "tranquility");
			//			if ( null != planetData ) col.setPlanetData(planetData);
			//			//			});
			//			// For each of the received planets, get their structures and do the same transformations.
			//			//			Stream.of(colonies).forEach(c -> {
			//			//			try {
			//			final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), col.getPlanetId(), credential.getRefreshToken(), "tranquility");
			//			if ( null != colonyStructures ) {
			//				// Do not process the structures and the rest of the data directly but just generate the correct delegate methods.
			//				col.setStructuresData(colonyStructures);
			//				// Process the Colony contents indirectly using the mapper again.
			//				//						modelMapper.map(colonyStructures, c);
			//			}
			//			//		});
			//			colonies.add(col);
		}
		DownloadManager.logger.info(">> [DownloadManager.downloadColonyList]> [TIMING] Total download time: {}", totalDownloadTime.printElapsed(ChronoOptions.SHOWMILLIS));
		return true;
	}

	//	@JsonIgnore
	//	public NeoComCharacter getPilot () {
	//		return _pilot;
	//	}
	//
	//	public void setPilot (final NeoComCharacter newPilot) {
	//		_pilot = newPilot;
	//	}

	//	public String getJsonClass () {
	//		return jsonClass;
	//	}

	public void updateTargetDataTimeStamp( final String reference, final Instant timePoint ) {
		try {
			_assetsCacheTime = GlobalDataManager.getNeocomDBHelper().getTimeStampDao().queryForId(reference);
			//		final Instant newExpirationTime = Instant.now().plus(TimeUnit.SECONDS.toMillis(3600));
			if (null == _assetsCacheTime) {
				_assetsCacheTime = new TimeStamp(reference, timePoint)
						.setCredentialId(credential.getAccountId())
						.store();
			} else {
				_assetsCacheTime.setTimeStamp(timePoint)
						.setCredentialId(credential.getAccountId())
						.store();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * The new downloader uses the eveapi library to process the xml source code received. This simplifies the
//	 * code but adds the need to control the download format. There are two sets of records, one with the
//	 * hierarchical dependencies between the assets and the other a flat list of all the assets with no
//	 * dependencies but with a mix of Location and Asset codes on the locationId field.<br>
//	 * The new processing will filter the assets with Unknown locations for a second pass processing so the
//	 * final list on the database will have the correct parentship hierarchy set up.<br>
//	 * <br>
//	 * The assets downloaded are being written to a special set of records in the User database with an special
//	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
//	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
//	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
//	 * <br>
//	 * There are two flavour for the asset download process. One for Pilots and other for Corporation assets.
//	 */
//	public void downloadPilotAssetsXML() {
//		DownloadManager.logger.info(">> [AssetsManager.downloadPilotAssets]");
//		try {
//			// Clear any previous record with owner -1 from database.
//			GlobalDataManager.getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
//			// Parse the CCP data to a list of assets
//			PilotAssetListParser parser = new PilotAssetListParser();
//			AssetListResponse response = parser.getResponse(new ApiAuthorization(credential.getKeyCode(), credential
//					.getAccountId(), credential.getValidationCode()));
//			if (null != response) {
//				unlocatedAssets = new Vector<NeoComAsset>();
//				List<Asset> assets = response.getAll();
//				// Assets may be parent of other assets so process them recursively if the hierarchical mode is selected.
//				for (final Asset eveAsset : assets) {
//					try {
//						this.processAsset(eveAsset, null);
//					} catch (final Exception ex) {
//						ex.printStackTrace();
//					}
//				}
//				// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
//				for (NeoComAsset asset : unlocatedAssets) {
//					this.validateLocation(asset);
//				}
//				// Assign the assets to the pilot.
//				GlobalDataManager.getNeocomDBHelper().replaceAssets(credential.getAccountId());
//
//				// Update the timer for this download at the database.
//				final String currentrequestReference = TimedUpdater.Job.constructReference(GlobalDataManager.EDataUpdateJobs.ASSETDATA
//						, credential.getAccountId());
//				final Instant validUntil = Instant.now()
//						.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.ASSETS_ASSETS));
//				final TimeStamp ts = new TimeStamp(currentrequestReference, validUntil)
//						.setCredentialId(credential.getAccountId())
//						.store();
//			}
//		} catch (final ApiException apie) {
//			apie.printStackTrace();
//		} catch (final Exception ex) {
//			ex.printStackTrace();
//		}
//		DownloadManager.logger.info("<< [AssetsManager.downloadPilotAssets]");
//	}

//	/**
//	 * Download the blueprint list for this character from CCP using the new API over the eveapi library and
//	 * then processes the response. It creates our model Blueprints that before being stored at the database are
//	 * grouped into stacks to reduce the number of registers to manage on other Industry operations.<br>
//	 * Current grouping is by IF-LOCATION-CONTAINER.
//	 */
//	public synchronized void downloadPilotBlueprints() {
//		DownloadManager.logger.info(">> [AssetsManager.downloadPilotBlueprints]");
//		Chrono chrono = new Chrono();
//		try {
//			// Clear any previous records with owner -1 from database.
//			GlobalDataManager.getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
//			// Download and parse the blueprints using the eveapi.
//			ArrayList<NeoComBlueprint> bplist = new ArrayList<NeoComBlueprint>();
//			BlueprintsParser parser = new BlueprintsParser();
//			BlueprintsResponse response = parser.getResponse(new ApiAuthorization(credential.getKeyCode(), credential
//					.getAccountId(), credential.getValidationCode()));
//			if (null != response) {
//				Set<Blueprint> blueprints = response.getAll();
//				for (Blueprint bp : blueprints) {
//					try {
//						bplist.add(this.convert2Blueprint(bp));
//					} catch (final RuntimeException rtex) {
//						// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
//						DownloadManager.logger.info("W> The Blueprint " + bp.getItemID() + " has no matching asset.");
//						DownloadManager.logger.info("W> " + bp.toString());
//					}
//				}
//			}
//			// Pack the blueprints and store them on the database.
//			storeBlueprints(bplist);
//			GlobalDataManager.getNeocomDBHelper().replaceBlueprints(credential.getAccountId());
//
//			// Update the timer for this download at the database.
//			final String currentrequestReference = TimedUpdater.Job.constructReference(GlobalDataManager.EDataUpdateJobs.BLUEPRINTDATA
//					, credential.getAccountId());
//			final Instant validUntil = Instant.now()
//					.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.ASSETS_ASSETS));
//			final TimeStamp ts = new TimeStamp(currentrequestReference, validUntil)
//					.setCredentialId(credential.getAccountId())
//					.store();
//		} catch (final ApiException apie) {
//			apie.printStackTrace();
//		} finally {
//			DownloadManager.logger.info("<< [AssetsManager.downloadPilotBlueprints]> [TIMING] - {}"
//					, chrono.printElapsed(ChronoOptions.SHOWMILLIS));
//		}
//	}

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
	//	public void downloadCorporationAssets () {
	//		DownloadManager.logger.info(">> [AssetsManager.downloadCorporationAssets]");
	//		try {
	//			// Clear any previous record with owner -1 from database.
	//			ModelAppConnector.getSingleton().getDBConnector().clearInvalidRecords(credential.getAccountId());
	//			// Download and parse the assets. Check the api key to detect corporations and use the other parser.
	//			//			AssetListResponse response = null;
	//			//			if (getName().equalsIgnoreCase("Corporation")) {
	//			//				AssetListParser parser = com.beimin.eveapi.corporation.assetlist.AssetListParser.getInstance();
	//			//				response = parser.getResponse(getAuthorization());
	//			//				if (null != response) {
	//			//					final HashSet<EveAsset> assets = new HashSet<EveAsset>(response.getAll());
	//			//					assetsCacheTime = new Instant(response.getCachedUntil());
	//			//					// Assets may be parent of other assets so process them recursively.
	//			//					for (final EveAsset eveAsset : assets) {
	//			//						processAsset(eveAsset, null);
	//			//					}
	//			//				}
	//			//			} else {
	//			AssetListParser parser = new AssetListParser();
	//			AssetListResponse response = parser.getResponse(this.getPilot().getAuthorization());
	//			if ( null != response ) {
	//				List<Asset> assets = response.getAll();
	//				//				this.getPilot().updateAssetsAccesscacheTime(response.getCachedUntil());
	//				// Assets may be parent of other assets so process them recursively.
	//				for (final Asset eveAsset : assets) {
	//					this.processAsset(eveAsset, null);
	//				}
	//			}
	//			//			}
	//			ModelAppConnector.getSingleton().getDBConnector().replaceAssets(credential.getAccountId());
	//
	//			// Update the caching time to the time set by the eveapi.
	//			String reference = credential.getAccountId() + ".ASSETDATA";
	//			if ( null == _assetsCacheTime ) {
	//				_assetsCacheTime = new TimeStamp(reference, new Instant(response.getCachedUntil()));
	//			} else {
	//				_assetsCacheTime.updateTimeStamp(new Instant(response.getCachedUntil()));
	//			}
	//		} catch (final ApiException apie) {
	//			apie.printStackTrace();
	//		}
	//		DownloadManager.logger.info("<< [AssetsManager.downloadCorporationAssets");
	//	}


//	/**
//	 * Processes an asset and all their children. This method converts from a API record to a database asset
//	 * record.<br>
//	 * For flat assets it will detect the Location and if matched to an unknown location store the asset for
//	 * second pass processing.
//	 */
//	private void processAsset( final Asset eveAsset, final NeoComAsset parent ) {
//		final NeoComAsset myasset = this.convert2Asset(eveAsset);
//		if (null != parent) {
//			//			myasset.setParent(parent);
//			myasset.setParentContainer(parent);
//			// Set the location to the parent's location is not set.
//			if (myasset.getLocationId() == -1) {
//				myasset.setLocationId(parent.getLocationId());
//			}
//		}
//		// Only search names for containers and ships.
//		if (myasset.isShip()) {
//			downloadAssetEveName(myasset.getAssetId());
//		}
//		if (myasset.isContainer()) {
//			this.downloadAssetEveName(myasset.getAssetId());
//		}
//		try {
//			//			final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
//			this.accessDaos();
//			final HashSet<Asset> children = new HashSet<Asset>(eveAsset.getAssets());
//			if (children.size() > 0) {
//				myasset.setContainer(true);
//			}
//			if (myasset.getCategory().equalsIgnoreCase("Ship")) {
//				myasset.setShip(true);
//			}
//			myasset.setOwnerId(credential.getAccountId() * -1);
//			assetDao.create(myasset);
//
//			// Check the asset location. The location can be a known game station, a known user structure, another asset
//			// or an unknown player structure. Check which one is this location.
//			EsiLocation targetLoc = GlobalDataManager.searchLocation4Id(myasset.getLocationId());
//			if (targetLoc.getTypeId() == ELocationType.UNKNOWN) {
//				// Add this asset to the list of items to be reprocessed.
//				unlocatedAssets.add(myasset);
//			}
//			// Process all the children and convert them to assets.
//			if (children.size() > 0) {
//				for (final Asset childAsset : children) {
//					this.processAsset(childAsset, myasset);
//				}
//			}
//			DownloadManager.logger.info("-- Wrote asset to database id [" + myasset.getAssetId() + "]");
//		} catch (final SQLException sqle) {
//			DownloadManager.logger.error("E> [AssetsManager.processAsset]Unable to create the new asset ["
//					+ myasset.getAssetId() + "]. " + sqle.getMessage());
//			sqle.printStackTrace();
//		}
//	}


	public NeoComAsset searchAssetByID( final long assetID ) {
		// search for the asset on the cache. Usually searching for containers.
		NeoComAsset hit = containerCache.get(assetID);
		if (null == hit) {
			// Select assets for the owner and with an specific type id.
			List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
			try {
				Dao<NeoComAsset, String> assetDao = GlobalDataManager.getNeocomDBHelper().getAssetDao();
				QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
				Where<NeoComAsset, String> where = queryBuilder.where();
				where.eq("assetID", assetID);
				PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
				assetList = assetDao.query(preparedQuery);
				if (assetList.size() > 0) {
					hit = assetList.get(0);
					containerCache.put(hit.getAssetId(), hit);
				}
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return hit;
	}

//	/**
//	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information. <br>
//	 * This method checks the location to detect if under the new flat model the location is an asset and then
//	 * we should convert it into a parent or the location is a real location. Initially this is done checking
//	 * the location id value if under 1000000000000.
//	 *
//	 * @param eveAsset the original assest as downloaded from CCP api
//	 */
//	private NeoComAsset convert2Asset( final Asset eveAsset ) {
//		// Create the asset from the API asset.
//		final NeoComAsset newAsset = new NeoComAsset();
//		newAsset.setAssetId(eveAsset.getItemID());
//		newAsset.setTypeId(eveAsset.getTypeId());
//		Long locid = eveAsset.getLocationID();
//		if (null == locid) {
//			locid = (long) -2;
//		}
//		newAsset.setLocationId(locid);
//
//		newAsset.setQuantity(eveAsset.getQuantity());
//		//		newAsset.setLocationFlag(eveAsset.getFlag());
//		newAsset.setSingleton(eveAsset.getSingleton());
//
//		// Get access to the Item and update the copied fields.
//		final EveItem item = GlobalDataManager.searchItem4Id(newAsset.getTypeId());
//		if (null != item) {
//			try {
//				newAsset.setName(item.getName());
//				newAsset.setCategory(item.getCategory());
//				newAsset.setGroupName(item.getGroupName());
//				newAsset.setTech(item.getTech());
//				if (item.isBlueprint()) {
//					newAsset.setBlueprintType(eveAsset.getRawQuantity());
//				}
//			} catch (RuntimeException rtex) {
//			}
//		}
//		// Add the asset value to the database.
//		newAsset.setIskValue(this.calculateAssetValue(newAsset));
//		return newAsset;
//	}


	private void accessDaos() {
		if (null == assetDao) {
			try {
				assetDao = GlobalDataManager.getNeocomDBHelper().getAssetDao();
				if (null == assetDao) throw new RuntimeException("AssetsManager - Required dao object is not valid.");
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
	public void storeBlueprints( final ArrayList<NeoComBlueprint> bplist ) {
		HashMap<String, NeoComBlueprint> bpStacks = new HashMap<String, NeoComBlueprint>();
		for (NeoComBlueprint blueprint : bplist) {
			this.checkBPCStacking(bpStacks, blueprint);
		}

		// Extract stacks and store them into the caches.
		blueprintCache.addAll(bpStacks.values());
		// Update the database information.
		for (NeoComBlueprint blueprint : blueprintCache) {
			try {
				Dao<NeoComBlueprint, String> blueprintDao = GlobalDataManager.getNeocomDBHelper().getBlueprintDao();
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

//	protected NeoComBlueprint convert2Blueprint( final Blueprint eveBlue ) {
//		// Create the asset from the API asset.
//		final NeoComBlueprint newBlueprint = new NeoComBlueprint(eveBlue.getItemID());
//		newBlueprint.setTypeId(eveBlue.getTypeId());
//		newBlueprint.setTypeName(eveBlue.getTypeName());
//		newBlueprint.setLocationID(eveBlue.getLocationID());
//		newBlueprint.setLocationFlag(eveBlue.getFlagID());
//		newBlueprint.setQuantity(eveBlue.getQuantity());
//		newBlueprint.setTimeEfficiency(eveBlue.getTimeEfficiency());
//		newBlueprint.setMaterialEfficiency(eveBlue.getMaterialEfficiency());
//		newBlueprint.setRuns(eveBlue.getRuns());
//		newBlueprint.setPackaged((eveBlue.getQuantity() == -1) ? true : false);
//
//		// Detect if BPO or BPC and set the flag.
//		if (eveBlue.getRuns() == -1) {
//			newBlueprint.setBpo(true);
//		}
//		return newBlueprint;
//	}

	/**
	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
	 * same container so the locationID, the parentContainer and the typeId should match to perform the
	 * aggregation.<br>
	 * Aggregation key: ID-LOCATION-CONTAINER
	 *
	 * @param targetContainer the stack storage that contains the list of registered blueprints
	 * @param bp              the blueprint part to be added to the hierarchy
	 */
	private void checkBPCStacking( final HashMap<String, NeoComBlueprint> targetContainer, final NeoComBlueprint bp ) {
		// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.ASSETID
		String id = bp.getStackID();
		NeoComBlueprint hit = targetContainer.get(id);
		if (null == hit) {
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
