//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.Job;
import org.dimensinfin.eveonline.neocom.database.entity.MarketOrder;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class DownloadManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("DownloadManager");

	// - F I E L D - S E C T I O N ............................................................................
	private transient Credential credential = null;
	//	private transient Dao<NeoComAsset, String> assetDao = null;
	private List<NeoComAsset> unlocatedAssets = null;
	private transient final List<Long> id4Names = new ArrayList<>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DownloadManager() {
		super();
	}

	public DownloadManager( final Credential credential ) {
		this();
		this.credential = credential;
		// Preload the dao.
//		try {
//			assetDao = new GlobalDataManager().getNeocomDBHelper().getAssetDao();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * This downloader will use the new ESI api to get access to the full list of assets for this character.
	 * Once the list is processed we should create an instance as close as possible to the older XML
	 * instances generated by the XML processing.<br>
	 * That instance will then get stored at the database and then we should make the trick of asset
	 * replacing.<br>
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the
	 * final list on the database will have the correct parentship hierarchy set up.<br>
	 * <br>
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
	 */
	public boolean downloadPilotAssetsESI() {
		DownloadManager.logger.info(">> [AssetsManager.downloadPilotAssetsESI]");
		try {
			// Clear any previous record with owner -1 from database.
			new GlobalDataManager().getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
			// Download the list of assets.
			final List<GetCharactersCharacterIdAssets200Ok> assetOkList = ESINetworkManager.getCharactersCharacterIdAssets(credential.getAccountId(), credential.getRefreshToken(), null);
			if ((null == assetOkList) || (assetOkList.size() < 1)) return false;
			// Create the list for orphaned locations assets. They should be processed later.
			unlocatedAssets = new ArrayList<NeoComAsset>();
			// Assets may be parent of other assets so process them recursively if the hierarchical mode is selected.
			for (final GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
				//--- A S S E T   P R O C E S S I N G
				try {
					// Convert the asset from the OK format to a MVC compatible structure.
					final NeoComAsset myasset = this.convert2AssetFromESI(assetOk);
					if (myasset.getCategoryName().equalsIgnoreCase("Ship")) {
						myasset.setShip(true);
					}
					if (myasset.getCategoryName().equalsIgnoreCase("Blueprint")) {
						myasset.setBlueprintType(assetOk.getQuantity());
					}
					if (myasset.isShip()) {
						downloadAssetEveName(myasset.getAssetId());
					}
					if (myasset.isContainer()) {
						downloadAssetEveName(myasset.getAssetId());
					}
					// Mark the asset owner to the work in progress value.
					myasset.setOwnerId(credential.getAccountId() * -1);
					// With assets separate the update from the creation because they use a generated unique key.
					new GlobalDataManager().getNeocomDBHelper().getAssetDao().create(myasset);
					DownloadManager.logger.info("-- Wrote asset to database id [" + myasset.getAssetId() + "]");

					//--- L O C A T I O N   P R O C E S S I N G
					// Check the asset location. The location can be a known game station, a known user structure, another asset
					// or an unknown player structure. Check which one is this location.
					EveLocation targetLoc = new GlobalDataManager().searchLocation4Id(myasset.getLocationId());
					if (targetLoc.getTypeId() == ELocationType.UNKNOWN) {
						// Add this asset to the list of items to be reprocessed.
						unlocatedAssets.add(myasset);
					}
				} catch (final RuntimeException rtex) {
					DownloadManager.logger.info("RTEX ´[AssetsManager.downloadPilotAssetsESI]> Processing asset: {} - {}"
							, assetOk.getItemId(), rtex.getMessage());
					rtex.printStackTrace();
				}
			}
			//--- O R P H A N   L O C A T I O N   A S S E T S
			// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
			for (NeoComAsset asset : unlocatedAssets) {
				this.validateLocation(asset);
			}
			// Assign the assets to the pilot.
			new GlobalDataManager().getNeocomDBHelper().replaceAssets(credential.getAccountId());
			// Remove from memory the managers that contain now stale data.
			//TODO Removed until this is checked if required.
//			GlobalDataManager.dropAssetsManager(credential.getAccountId());
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadPilotAssetsESI]");
		return true;
	}

	public boolean downloadPilotBlueprintsESI() {
		DownloadManager.logger.info(">> [AssetsManager.downloadPilotBlueprintsESI]");
		try {
			// Clear any previous record with owner -1 from database.
			new GlobalDataManager().getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
			// Download the list of blueprints.
			final List<GetCharactersCharacterIdBlueprints200Ok> blueprintOkList = ESINetworkManager
					.getCharactersCharacterIdBlueprints(
							credential.getAccountId()
							, credential.getRefreshToken()
							, null
					);
			if ((null == blueprintOkList) || (blueprintOkList.size() < 1)) return false;
//			// Create the list for orphaned locations assets. They should be processed later.
			final List<NeoComBlueprint> bplist = new ArrayList<NeoComBlueprint>();
			// Blueprints point to another node qualified as asset. So the location and the rest of the data comes from that already
			// processed asset. While adding that field we expect the blueprint instance to auto connect itself.
			for (final GetCharactersCharacterIdBlueprints200Ok blueprintOk : blueprintOkList) {
				//--- B L U E P R I N T   P R O C E S S I N G
				 NeoComBlueprint newBlueprint =null;
				try {
					// TODO - Check that after the asset association we have the correct location information.
					 newBlueprint = new NeoComBlueprint(blueprintOk.getItemId())
							.setTypeId(blueprintOk.getTypeId())
							.setQuantity(blueprintOk.getQuantity())
							.setTimeEfficiency(blueprintOk.getTimeEfficiency())
							.setMaterialEfficiency(blueprintOk.getMaterialEfficiency())
							.setRuns(blueprintOk.getRuns())
							.setPackaged((blueprintOk.getQuantity() == -1) ? true : false);
					// Detect if BPO or BPC and set the flag.
					if (blueprintOk.getRuns() == -1) {
						newBlueprint.setBpo(true);
					}
				} catch (final RuntimeException rtex) {
					// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
					DownloadManager.logger.info("W> The Blueprint " + newBlueprint.getAssetId() + " has no matching asset.");
					DownloadManager.logger.info("W> " + newBlueprint.toString());
				}
			}
			// Pack the blueprints and store them on the database.
			storeBlueprints(bplist);
			// Assign the blueprints to the pilot.
			new GlobalDataManager().getNeocomDBHelper().replaceBlueprints(credential.getAccountId());
			// Remove from memory the managers that contain now stale data.
			//TODO Removed until this is checked if required.
//			GlobalDataManager.dropAssetsManager(credential.getAccountId());
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadPilotBlueprintsESI]");
		return true;
	}

	public void downloadPilotJobsESI() {
		DownloadManager.logger.info(">> [DownloadManager.downloadPilotJobsESI]");
		try {
			List<Job> jobsList = GlobalDataManager.downloadIndustryJobs4Credential(credential);
		} finally {
			DownloadManager.logger.info("<< [DownloadManager.downloadPilotJobsESI]");
		}
	}

	public void downloadPilotMarketOrdersESI() {
		DownloadManager.logger.info(">> [DownloadManager.downloadPilotMarketOrdersESI]");
		try {
			List<MarketOrder> ordersList = GlobalDataManager.downloadMarketOrders4Credential(credential);
			ordersList = GlobalDataManager.downloadMarketOrdersHistory4Credential(credential);
		} finally {
			DownloadManager.logger.info("<< [DownloadManager.downloadPilotMarketOrdersESI]");
		}
	}

	//--- P R I V A T E   M E T H O D S
	private NeoComAsset convert2AssetFromESI( final GetCharactersCharacterIdAssets200Ok asset200Ok ) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset(asset200Ok.getTypeId())
				.setAssetId(asset200Ok.getItemId());
		// TODO -- Location management is done ourside this transormation. This is duplicated code.
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
		final EveItem item = new GlobalDataManager().searchItem4Id(newAsset.getTypeId());
		if (null != item) {
//			try {
			newAsset.setName(item.getName());
			newAsset.setCategory(item.getCategoryName());
			newAsset.setGroupName(item.getGroupName());
			newAsset.setTech(item.getTech());
//				if (item.isBlueprint()) {
//					//			newAsset.setBlueprintType(eveAsset.getRawQuantity());
//				}
//			} catch (RuntimeException rtex) {
//			}
		}
		// Add the asset value to the database.
		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	private synchronized double calculateAssetValue( final NeoComAsset asset ) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategoryName();
				String group = item.getGroupName();
				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = 0.0;
					try {
						// First try to set the average market price. If it fails search for the market data.
						price = asset.getItem().getPrice();
						if (price < 0)
							price = asset.getItem().getHighestBuyerPrice().getPrice();
					} catch (ExecutionException ee) {
						price = asset.getItem().getPrice();
					} catch (InterruptedException ee) {
						price = asset.getItem().getPrice();
					}
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	/**
	 * Checks if the Location can be found on the two lists of Locations, the CCP game list or the player
	 * compiled list. If the Location can't be found on any of those lists then it can be another asset
	 * (Container, Ship, etc) or another player/corporation structure resource that is not listed on the asset
	 * list.
	 */
	private ELocationType validateLocation( final NeoComAsset asset ) {
		long targetLocationid = asset.getLocationId();
		EveLocation targetLoc = new GlobalDataManager().searchLocation4Id(targetLocationid);
		if (targetLoc.getTypeId() == ELocationType.UNKNOWN) {
			try {
				// Need to check if asset or unreachable location. Search for asset with locationid.
				List<NeoComAsset> targetList = new GlobalDataManager().getNeocomDBHelper().getAssetDao()
						.queryForEq("assetId", Long.valueOf(targetLocationid));
				NeoComAsset target = null;
				if (targetList.size() > 0) target = targetList.get(0);
				if (null == target)
					return ELocationType.UNKNOWN;
				else {
					// Change the asset parentship and update the asset location with the location of the parent.
					asset.setParentId(targetLocationid);

					// Search recursively on the parentship chain until a leaf is found. Then check that location.
					long parentIdentifier = target.getParentContainerId();
					while (parentIdentifier != -1) {
						validateLocation(target);
						targetList = new GlobalDataManager().getNeocomDBHelper().getAssetDao()
								.queryForEq("assetId", Long.valueOf(parentIdentifier));
						if (targetList.size() > 0) target = targetList.get(0);
						parentIdentifier = target.getParentContainerId();
					}
					// Now target contains a parent with parentship -1.
					// Set to this asset the parent location whichever it is.
					asset.setLocationId(target.getLocationId())
							.setLocationType(target.getLocationType())
							.setFlag(target.getFlag());
					asset.store();
					return target.getLocation().getTypeId();
				}
			} catch (SQLException sqle) {
				return ELocationType.UNKNOWN;
			}
		} else
			return targetLoc.getTypeId();
	}

	/**
	 * Aggregates ids for some of the assets until it reached 10 and then posts and update for the whole batch.
	 */
	private void downloadAssetEveName( final long assetId ) {
		id4Names.add(assetId);
		if (id4Names.size() > 9) {
			postUserLabelNameDownload();
			id4Names.clear();
		}
	}

	private void postUserLabelNameDownload() {
		// Launch the download of the names block.
		final List<Long> idList = new ArrayList<>();
		idList.addAll(id4Names);
		GlobalDataManager.submitJob2Download(() -> {
			// Copy yhe list of assets to local to allow parallel use.
			final List<Long> localIdList = new ArrayList<>();
			localIdList.addAll(idList);
			try {
				final List<PostCharactersCharacterIdAssetsNames200Ok> itemNames = ESINetworkManager.postCharactersCharacterIdAssetsNames(credential.getAccountId(), localIdList, credential.getRefreshToken(), null);
				for (final PostCharactersCharacterIdAssetsNames200Ok name : itemNames) {
					final List<NeoComAsset> assetsMatch = new GlobalDataManager().getNeocomDBHelper().getAssetDao().queryForEq("assetId",
							name.getItemId());
					for (NeoComAsset asset : assetsMatch) {
						logger.info("-- [DownloadManager.downloadAssetEveName]> Setting UserLabel name {} for asset {}.", name
								.getName(), name.getItemId());
						asset.setUserLabel(name.getName())
								.store();
					}
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		});
	}
	/**
	 * Gets the list of blueprints from the API processor and packs them into stacks aggregated by some keys.
	 * This will simplify the quantity of data exported to presentation layers.<br>
	 * Aggregation is performed by TYPEID-LOCATION-CONTAINER-RUNS
	 *
	 * @param bplist list of newly created Blueprints from the CCP API download
	 */
	protected void storeBlueprints( final List<NeoComBlueprint> bplist ) {
		logger.info(">> [DownloadManager.storeBlueprints]");
		HashMap<String, NeoComBlueprint> bpStacks = new HashMap<String, NeoComBlueprint>();
		for (NeoComBlueprint blueprint : bplist) {
			this.checkBPCStacking(bpStacks, blueprint);
		}

		// Extract stacks and store them into the caches.
//		blueprintCache.addAll(bpStacks.values());
		// Update the database information.
		for (NeoComBlueprint blueprint : bpStacks.values()) {
			try {
				Dao<NeoComBlueprint, String> blueprintDao = new GlobalDataManager().getNeocomDBHelper().getBlueprintDao();
				// Be sure the owner is reset to undefined when stored at the database.
				blueprint.resetOwner();
				// Set new calculated values to reduce the time for blueprint part rendering.
				// REFACTOR This has to be rewrite to allow this calculation on download time.
				//				IJobProcess process = JobManager.generateJobProcess(getPilot(), blueprint, EJobClasses.MANUFACTURE);
				//				blueprint.setManufactureIndex(process.getProfitIndex());
				//				blueprint.setJobProductionCost(process.getJobCost());
				//				blueprint.setManufacturableCount(process.getManufacturableCount());
				blueprintDao.create(blueprint);
				DownloadManager.logger.info("-- [DownloadManager.storeBlueprints]> Wrote blueprint to database id [" + blueprint
						.getAssetId() + "]");
			} catch (final SQLException sqle) {
				DownloadManager.logger.error("E> [DownloadManager.storeBlueprints]> Unable to create the new blueprint [" + blueprint
						.getAssetId() + "]. "
						+ sqle.getMessage());
				sqle.printStackTrace();
			} catch (final RuntimeException rtex) {
				DownloadManager.logger.error("E> [DownloadManager.storeBlueprints]> Unable to create the new blueprint [" + blueprint
						.getAssetId() + "]. "
						+ rtex.getMessage());
				rtex.printStackTrace();
			}
		}
		logger.info("<< [DownloadManager.storeBlueprints]");
	}
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
		String id = bp.getStackId();
		NeoComBlueprint hit = targetContainer.get(id);
		if (null == hit) {
			// Miss. The blueprint is not registered.
			DownloadManager.logger.info("-- AssetsManager.checkBPCStacking >Stacked blueprint. " + bp.toString());
			bp.registerReference(bp.getAssetId());
			targetContainer.put(id, bp);
		} else {
			//Hit. Increment the counter for this stack. And store the id
			hit.setQuantity(hit.getQuantity() + bp.getQuantity());
			hit.registerReference(bp.getAssetId());
		}
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		if (null != credential) return new StringBuffer("DownloadManager [")
				.append("owner:").append(credential.getAccountId()).append(" ")
				.append("]")
				.append("->").append(super.toString())
				.toString();
		else return new StringBuffer("DownloadManager []").toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
