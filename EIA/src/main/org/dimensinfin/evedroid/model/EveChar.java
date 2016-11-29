//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EDataBlock;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.interfaces.INeoComNode;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.character.blueprints.BlueprintListParser;
import com.beimin.eveapi.character.industryjobs.NewIndustryJobsHistoryParser;
import com.beimin.eveapi.character.industryjobs.NewIndustryJobsParser;
import com.beimin.eveapi.character.locations.LocationsParser;
import com.beimin.eveapi.character.marketorders.MarketOrdersParser;
import com.beimin.eveapi.character.sheet.ApiSkill;
import com.beimin.eveapi.character.sheet.CharacterSheetParser;
import com.beimin.eveapi.character.sheet.CharacterSheetResponse;
import com.beimin.eveapi.character.skill.intraining.SkillInTrainingParser;
import com.beimin.eveapi.character.skill.intraining.SkillInTrainingResponse;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.core.ApiAuthorization;
import com.beimin.eveapi.corporation.assetlist.AssetListParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.assetlist.AssetListResponse;
import com.beimin.eveapi.shared.assetlist.EveAsset;
import com.beimin.eveapi.shared.blueprints.BlueprintListResponse;
import com.beimin.eveapi.shared.blueprints.EveBlueprint;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.industryjobs.ApiNewIndustryJob;
import com.beimin.eveapi.shared.industryjobs.NewIndustryJobsHistoryResponse;
import com.beimin.eveapi.shared.industryjobs.NewIndustryJobsResponse;
import com.beimin.eveapi.shared.locations.ApiLocation;
import com.beimin.eveapi.shared.locations.LocationsResponse;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.marketorders.MarketOrdersResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveChar extends EveCharCore implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long										serialVersionUID		= -955059830168434115L;
	private static Logger												logger							= Logger.getLogger("EveChar");
	private static transient CachingConnector		apiCacheConnector		= null;

	// - F I E L D - S E C T I O N ............................................................................
	private transient Instant										assetsCacheTime			= null;
	private transient Instant										blueprintsCacheTime	= null;
	private transient Instant										jobsCacheTime				= null;
	private transient Instant										marketCacheTime			= null;

	// - D E P E N D A N T   P R O P E R T I E S
	private long																totalAssets					= -1;
	private CharacterSheetResponse							characterSheet			= null;
	private transient AssetsManager							assetsManager				= null;
	private transient SkillInTrainingResponse		skillInTraining			= null;
	private transient ArrayList<ApiIndustryJob>	industryJobs				= null;
	private transient ArrayList<Job>						jobList							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveChar(final Integer key, final String validation, final long characterID) {
		super(key, validation, characterID);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method is to process a request from the UI to get the model for the Market Orders. The market orders
	 * are stored at the database and there are two sets, the orders that are downloaded from CCP and the orders
	 * scheduled by the user through the UI. If this access does not find orders it posts a refresh to download
	 * that information from CCP servers. There is no timing check to access the information.
	 * 
	 * @return the market order data hierarchy with the analytical groups and the orders.
	 */
	public ArrayList<MarketOrderAnalyticalGroup> accessMarketOrders() {
		final ArrayList<MarketOrder> orders = searchMarketOrders();
		// Create the analytical groups.
		final MarketOrderAnalyticalGroup scheduledBuyGroup = new MarketOrderAnalyticalGroup(10, "SCHEDULED BUYS");
		final MarketOrderAnalyticalGroup buyGroup = new MarketOrderAnalyticalGroup(30, "BUYS");
		final MarketOrderAnalyticalGroup sellGroup = new MarketOrderAnalyticalGroup(40, "SELLS");
		final MarketOrderAnalyticalGroup finishedGroup = new MarketOrderAnalyticalGroup(50, "FINISHED");

		for (final MarketOrder order : orders) {
			// Add the order to the scheduled aggregator that will also pack similar items into a single item.
			if (order.getOrderState() == ModelWideConstants.orderstates.SCHEDULED) {
				scheduledBuyGroup.addChild(order);
				continue;
			}
			if (order.getOrderState() == ModelWideConstants.orderstates.EXPIRED) {
				finishedGroup.addChild(order);
				continue;
			}
			// Detect buys and sells.				
			final boolean bid = order.getBid();
			if (bid) {
				buyGroup.addChild(order);
			} else {
				sellGroup.addChild(order);
			}
		}
		// Compose the output.
		final ArrayList<MarketOrderAnalyticalGroup> result = new ArrayList<MarketOrderAnalyticalGroup>();
		result.add(scheduledBuyGroup);
		result.add(buyGroup);
		result.add(sellGroup);
		result.add(finishedGroup);
		return result;
	}

	public MarketOrderAnalyticalGroup accessModules4Sell() {
		final ScheduledSellsAnalyticalGroup scheduledSellGroup = new ScheduledSellsAnalyticalGroup(20, "SCHEDULED SELLS");
		final ArrayList<Asset> modules = getAssetsManager().searchT2Modules();
		final HashMap<String, Resource> mods = new HashMap<String, Resource>();
		for (final Asset mc : modules) {
			// Check if the item is already on the list.
			final boolean hit = mods.containsKey(mc.getItemName());
			// Only add to sell list the stacks with more than 10 elements.
			if (mc.getQuantity() > 10) if (!hit) {
				// TODO Instead defining a resoure I should create a new fake order.
				final Resource mod4sell = new Resource(mc.getTypeID(), mc.getQuantity());
				mods.put(mc.getItemName(), mod4sell);
				scheduledSellGroup.addChild(mod4sell);
			} else {
				final Resource mod4sell = mods.get(mc.getItemName());
				mod4sell.setQuantity(mod4sell.getQuantity() + mc.getQuantity());
			}
		}
		return scheduledSellGroup;
	}

	/**
	 * Returns the number of invention jobs that can be launched simultaneously. This will depend on the skills
	 * <code>Laboratory Operation</code> and <code>Advanced Laboratory Operation</code>.
	 * 
	 * @return
	 */
	public int calculateInventionQueues() {
		int queues = 1;
		final Set<ApiSkill> skills = characterSheet.getSkills();
		for (final ApiSkill apiSkill : skills) {
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.LaboratoryOperation) {
				queues += apiSkill.getLevel();
			}
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedLaboratoryOperation) {
				queues += apiSkill.getLevel();
			}
		}
		return queues;
	}

	/**
	 * Returns the number of manufacture jobs that can be launched simultaneously. This will depend on the
	 * skills <code>Mass Production</code> and <code>Advanced Mass Production</code>.
	 * 
	 * @return
	 */
	public int calculateManufactureQueues() {
		int queues = 1;
		final Set<ApiSkill> skills = characterSheet.getSkills();
		for (final ApiSkill apiSkill : skills) {
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.MassProduction) {
				queues += apiSkill.getLevel();
			}
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedMassProduction) {
				queues += apiSkill.getLevel();
			}
		}
		return queues;
	}

	@Override
	public void clean() {
		assetsManager = null;
		lastCCPAccessTime = null;
		assetsCacheTime = null;
		blueprintsCacheTime = null;
		jobsCacheTime = null;
		super.clean();
	}

	public void cleanJobs() {
		jobList = null;
		jobsCacheTime = new Instant();
	}

	/**
	 * Does nothing because the list of orders is not cached on any structure and is read from database every
	 * time we access that list.
	 */
	public void cleanOrders() {
		marketCacheTime = null;
	}

	/**
	 * For the EveChar the contents provided to the model are empty when the variant is related to the pilot
	 * list. Maybe in other calls the return would be another list of contents.
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		//		if (renderWhenEmpty()) results.add(this);
		return results;
	}

	public void forceRefresh() {
		clean();
		assetsManager = new AssetsManager(this);
		EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(characterID);
	}

	public long getAssetCount() {
		if (totalAssets == -1) {
			try {
				final Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
				totalAssets = assetDao
						.countOf(assetDao.queryBuilder().setCountOf(true).where().eq("ownerID", getCharacterID()).prepare());
			} catch (final SQLException sqle) {
				Log.w("EVEI", "W> Proglem calculating the number of assets for " + getName());
			}
		}
		return totalAssets;
	}

	public AssetsManager getAssetsManager() {
		if (null == assetsManager) {
			assetsManager = new AssetsManager(this);
		}
		// Make sure the Manager is already connected to the Pilot.
		assetsManager.setPilot(this);
		return assetsManager;
	}

	public ApiAuthorization getAuthorization() {
		return new ApiAuthorization(keyID, characterID, verificationCode);
	}

	/**
	 * Returns a non null default location so any Industry action has a location to be used as reference. Any
	 * location is valid.
	 * 
	 * @return
	 */
	public EveLocation getDefaultLocation() {
		return getAssetsManager().getLocations().get(1);
	}

	public ArrayList<Job> getIndustryJobs() {
		if (null == jobList) {
			jobList = searchIndustryJobs();
		}
		return jobList;
	}

	public ArrayList<MarketOrder> getMarketOrders() {
		return searchMarketOrders();
	}

	public int getSkillLevel(final int skillID) {
		// Corporation api will have all skills maxed.
		if (isCorporation()) return 5;
		final Set<ApiSkill> skills = characterSheet.getSkills();
		for (final ApiSkill apiSkill : skills)
			if (apiSkill.getTypeID() == skillID) return apiSkill.getLevel();
		return 0;
	}

	public boolean isCorporation() {
		if (getName().equalsIgnoreCase("Corporation"))
			return true;
		else
			return false;
	}

	/**
	 * Check each of the request cache time until founds one that has expired. If no one found then the
	 * character does not need any update
	 * 
	 * @return
	 */
	public EDataBlock needsUpdate() {
		if (AppConnector.checkExpiration(lastCCPAccessTime, ModelWideConstants.HOURS1)) return EDataBlock.CHARACTERDATA;
		if (AppConnector.checkExpiration(marketCacheTime, ModelWideConstants.NOW)) return EDataBlock.MARKETORDERS;
		if (AppConnector.checkExpiration(jobsCacheTime, ModelWideConstants.NOW)) return EDataBlock.INDUSTRYJOBS;
		if (AppConnector.checkExpiration(assetsCacheTime, ModelWideConstants.NOW)) return EDataBlock.ASSETDATA;
		if (AppConnector.checkExpiration(blueprintsCacheTime, ModelWideConstants.NOW)) return EDataBlock.BLUEPRINTDATA;
		return EDataBlock.READY;
	}

	public ArrayList<MarketOrder> searchMarketOrders() {
		//	Select assets of type blueprint and that are of T2.
		List<MarketOrder> orderList = new ArrayList<MarketOrder>();
		try {
			AppConnector.startChrono();
			final Dao<MarketOrder, String> marketOrderDao = AppConnector.getDBConnector().getMarketOrderDAO();
			final QueryBuilder<MarketOrder, String> qb = marketOrderDao.queryBuilder();
			qb.where().eq("ownerID", getCharacterID());
			orderList = marketOrderDao.query(qb.prepare());
			final Duration lapse = AppConnector.timeLapse();
			logger.info("-- Time lapse for [SELECT MARKETORDERS] " + lapse);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<MarketOrder>) orderList;
	}

	/**
	 * Connects the AssetsManager to one that maybe has been restored from persistence storage.
	 * 
	 * @param manager
	 */
	public void setAssetsManager(final AssetsManager manager) {
		assetsManager = manager;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveChar [");
		buffer.append(super.toString()).append(" ");
		buffer.append("assets:").append(totalAssets).append(" ");
		buffer.append("]");
		return buffer.toString();
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
	@SuppressWarnings("rawtypes")
	public synchronized void updateAssets() {
		logger.info(">> EveChar.updateAssets");
		try {
			// Clear any previous record with owner -1 from database.
			AppConnector.getDBConnector().clearInvalidRecords();
			// Download and parse the assets. Check the api key to detect corporations and use the other parser.
			AssetListResponse response = null;
			if (getName().equalsIgnoreCase("Corporation")) {
				AssetListParser parser = com.beimin.eveapi.corporation.assetlist.AssetListParser.getInstance();
				response = parser.getResponse(getAuthorization());
				if (null != response) {
					final HashSet<EveAsset> assets = new HashSet<EveAsset>(response.getAll());
					assetsCacheTime = new Instant(response.getCachedUntil());
					// Assets may be parent of other assets so process them recursively.
					for (final EveAsset eveAsset : assets) {
						processAsset(eveAsset, null);
					}
				}
			} else {
				com.beimin.eveapi.character.assetlist.AssetListParser parser = com.beimin.eveapi.character.assetlist.AssetListParser
						.getInstance();
				response = parser.getResponse(getAuthorization());
				if (null != response) {
					final HashSet<EveAsset> assets = new HashSet<EveAsset>(response.getAll());
					assetsCacheTime = new Instant(response.getCachedUntil());
					// Assets may be parent of other assets so process them recursively.
					for (final EveAsset eveAsset : assets) {
						processAsset(eveAsset, null);
					}
				}
			}
			AppConnector.getDBConnector().replaceAssets(getCharacterID());

			// Update the caching time to the time set by the eveapi.
			assetsCacheTime = new Instant(response.getCachedUntil());
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		// Clean all user structures invalid after the reload of the assets.
		assetsManager = null;
		totalAssets = -1;
		//		clearTimers();
		JobManager.clearCache();
		setDirty(true);
		fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_EVECHARACTER_ASSETS, null, null);
		logger.info("<< EveChar.updateAssets");
	}

	/**
	 * Download the blueprint list for this character from CCP using the new API over the eveapi library and
	 * then processes the response. It creates our model Blueprints that before being stored at the database are
	 * grouped into stacks to reduce the number of registers to manage on other Industry operations.<br>
	 * Current grouping is by IF-LOCATION-CONTAINER.
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void updateBlueprints() {
		try {
			AppConnector.startChrono();
			// Clear any previous records with owner -1 from database.
			AppConnector.getDBConnector().clearInvalidRecords();
			// Download and parse the blueprints using the eveapi.
			// Set the default connector for blueprints to a cache connector.
			if (null == apiCacheConnector) {
				apiCacheConnector = new CachingConnector();
			}
			EveApi.setConnector(apiCacheConnector);
			BlueprintListResponse response = null;
			ArrayList<Blueprint> bplist = new ArrayList<Blueprint>();
			if (getName().equalsIgnoreCase("Corporation")) {
				com.beimin.eveapi.corporation.blueprints.BlueprintListParser parser = com.beimin.eveapi.corporation.blueprints.BlueprintListParser
						.getInstance();
				response = parser.getResponse(getAuthorization());
				if (null != response) {
					//					final ArrayList<Blueprint> bplist = new ArrayList<Blueprint>();
					final HashSet<EveBlueprint> blueprints = new HashSet<EveBlueprint>(response.getAll());
					for (final EveBlueprint bp : blueprints) {
						try {
							bplist.add(convert2Blueprint(bp));
						} catch (final RuntimeException rtex) {
							// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
							Log.w("EveChar", "W> The Blueprint " + bp.getItemID() + " has no matching asset.");
							Log.w("EveChar", "W> " + bp.toString());
						}
					}
				}
			} else {
				BlueprintListParser parser = BlueprintListParser.getInstance();
				response = parser.getResponse(getAuthorization());
				if (null != response) {
					//					final ArrayList<Blueprint> bplist = new ArrayList<Blueprint>();
					final HashSet<EveBlueprint> blueprints = new HashSet<EveBlueprint>(response.getAll());
					for (final EveBlueprint bp : blueprints) {
						try {
							bplist.add(convert2Blueprint(bp));
						} catch (final RuntimeException rtex) {
							// Intercept any exception for blueprints that do not match the asset. Remove them from the listing
							Log.w("EveChar", "W> The Blueprint " + bp.getItemID() + " has no matching asset.");
							Log.w("EveChar", "W> " + bp.toString());
						}
					}
				}
			}
			// Pack the blueprints and store them on the database.
			getAssetsManager().storeBlueprints(bplist);
			AppConnector.getDBConnector().replaceBlueprints(getCharacterID());
			// Update the caching time to the time set by the eveapi.
			blueprintsCacheTime = new Instant(response.getCachedUntil());
			// Update the dirty state to signal modification of store structures.
			setDirty(true);
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		final Duration lapse = AppConnector.timeLapse();
		Log.i("EveChar", "~~ Time lapse for [UPDATEBLUEPRINTS] - " + lapse);
	}

	/**
	 * At the Character creation we only have the key values to locate it into the CCP databases. During this
	 * execution we have to download many different info from many CCP API calls so it will take some time.<br>
	 * After this update we will have access to all the direct properties of a character. Other multiple value
	 * properties like assets or derived lists will be updated when needed by using other update calls.
	 */
	@Override
	public synchronized void updateCharacterInfo() {
		// TODO Verify that the data is stale before attempting to read it again.
		try {
			if (AppConnector.checkExpiration(lastCCPAccessTime, ModelWideConstants.HOURS1)) {
				downloadEveCharacterInfo();
				downloadCharacterSheet();
				// Get access to the character sheet data.
				final CharacterSheetParser parser = CharacterSheetParser.getInstance();
				final CharacterSheetResponse response = parser.getResponse(getAuthorization());
				if (null != response) {
					characterSheet = response;
				}
				lastCCPAccessTime = new Instant(response.getCachedUntil());
				setDirty(true);
			}
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
	}

	/**
	 * The industry jobs are obsolete so an update was triggered. Go to the CCP servers and get a fresh set of
	 * Industry Jobs data. After the processing write the records down to the database and then remove the CCP
	 * records.<br>
	 * After the processing check for User Jobs converted to running jobs and remove them from the app list
	 * because the user has already launched them on the real EVE client.
	 */
	public void updateIndustryJobs() {
		Log.i("EveChar", ">> EveChar.updateIndustryJobs");
		try {
			// Clear any previous record with owner -1 from database.
			//		AppConnector.getDBConnector().clearInvalidRecords();
			// Download and parse the industry jobs history.
			final NewIndustryJobsHistoryParser parserhist = NewIndustryJobsHistoryParser.getInstance();
			final NewIndustryJobsHistoryResponse responsehist = parserhist.getResponse(getAuthorization());
			if (null != responsehist) {
				final HashSet<ApiNewIndustryJob> jobs = new HashSet<ApiNewIndustryJob>(responsehist.getAll());
				jobsCacheTime = new Instant(responsehist.getCachedUntil());
				for (final ApiNewIndustryJob evejob : jobs) {
					final Job myjob = convert2Job(evejob);
					// Set the owner my there is not job cleanup.
					//					myjob.setOwnerID(getCharacterID());
					try {
						final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
						jobDao.createOrUpdate(myjob);
						logger.finest("-- Wrote job to database id [" + myjob.getJobID() + "]");
					} catch (final SQLException sqle) {
						logger.severe("E> Unable to create the new Job [" + myjob.getJobID() + "]. " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
			}

			// Download and parse the industry jobs.
			final NewIndustryJobsParser parser = NewIndustryJobsParser.getInstance();
			final NewIndustryJobsResponse response = parser.getResponse(getAuthorization());
			if (null != response) {
				final HashSet<ApiNewIndustryJob> jobs = new HashSet<ApiNewIndustryJob>(response.getAll());
				jobsCacheTime = new Instant(response.getCachedUntil());
				for (final ApiNewIndustryJob evejob : jobs) {
					final Job myjob = convert2Job(evejob);
					// Set the owner my there is not job cleanup.
					//					myjob.setOwnerID(getCharacterID());
					try {
						final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
						jobDao.createOrUpdate(myjob);
						logger.finest("-- Wrote job to database id [" + myjob.getJobID() + "]");
					} catch (final SQLException sqle) {
						logger.severe("E> Unable to create the new Job [" + myjob.getJobID() + "]. " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
			}
			//		AppConnector.getDBConnector().replaceJobs(getCharacterID());
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		setDirty(true);
		Log.i("EveChar", "<< EveChar.updateIndustryJobs");
	}

	public void updateMarketOrders() {
		Log.i("EveChar", ">> EveChar.updateMarketOrders");
		try {
			// Download and parse the market orders.
			final MarketOrdersParser parser = MarketOrdersParser.getInstance();
			final MarketOrdersResponse response = parser.getResponse(getAuthorization());
			if (null != response) {
				final HashSet<ApiMarketOrder> orders = new HashSet<ApiMarketOrder>(response.getAll());
				for (final ApiMarketOrder eveorder : orders) {
					final MarketOrder myorder = convert2Order(eveorder);
					try {
						final Dao<MarketOrder, String> marketOrderDao = AppConnector.getDBConnector().getMarketOrderDAO();
						marketOrderDao.createOrUpdate(myorder);
						logger.finest(
								"-- EveChar.updateMarketOrders.Wrote MarketOrder to database id [" + myorder.getOrderID() + "]");
					} catch (final SQLException sqle) {
						logger.severe("E> Unable to create the new Job [" + myorder.getOrderID() + "]. " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
				marketCacheTime = new Instant(response.getCachedUntil());
			}
		} catch (final ApiException apie) {
			apie.printStackTrace();
		}
		setDirty(true);
		Log.i("EveChar", "<< EveChar.updateMarketOrders");
	}

	private double calculateAssetValue(final Asset asset) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if (null != category) {
					if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
						// Add the value and volume of the stack to the global result.
						long quantity = asset.getQuantity();
						double price = asset.getItem().getHighestBuyerPrice().getPrice();
						assetValueISK = price * quantity;
					}
				}
			}
		}
		return assetValueISK;
	}

	/**
	 * Checks if the database is empty of a set of records so it may require a forced request to update their
	 * data from CCP databases. If no records then it will reset the cache timers.
	 * 
	 * @param size
	 * @param string
	 */
	private void checkRefresh(final int size, final String section) {
		if (size < 1) {
			if (section.equalsIgnoreCase("JOBS")) {
				cleanJobs();
				EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(getCharacterID());
			}
			if (section.equalsIgnoreCase("MARKETORDERS")) {
				EVEDroidApp.getTheCacheConnector().addCharacterUpdateRequest(getCharacterID());
			}
		}
	}

	private void clearTimers() {
		lastCCPAccessTime = null;
		assetsCacheTime = null;
		blueprintsCacheTime = null;
		jobsCacheTime = null;
		marketCacheTime = null;
		//		skillsCacheTime = null;
	}

	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information.
	 * 
	 * @param eveAsset
	 * @return
	 */
	private Asset convert2Asset(final EveAsset<EveAsset> eveAsset) {
		// Create the asset from the API asset.
		final Asset newAsset = new Asset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		// Children locations have a null on this field. Set it to their parents
		final Long assetloc = eveAsset.getLocationID();
		if (null != assetloc) {
			newAsset.setLocationID(eveAsset.getLocationID());
		}
		newAsset.setQuantity(eveAsset.getQuantity());
		newAsset.setFlag(eveAsset.getFlag());
		newAsset.setSingleton(eveAsset.getSingleton());

		// Get access to the Item and update the copied fields.
		final EveItem item = AppConnector.getDBConnector().searchItembyID(newAsset.getTypeID());
		if (null != item) {
			try {
				newAsset.setName(item.getName());
				newAsset.setCategory(item.getCategory());
				newAsset.setGroupName(item.getGroupName());
				newAsset.setTech(item.getTech());
				if (item.isBlueprint()) {
					newAsset.setBlueprintType(eveAsset.getRawQuantity());
				}
			} catch (RuntimeException rtex) {
			}
		}
		// Add the asset value to the database.
		newAsset.setIskvalue(calculateAssetValue(newAsset));
		return newAsset;
	}

	private Blueprint convert2Blueprint(final EveBlueprint eveBlue) {
		// Create the asset from the API asset.
		final Blueprint newBlueprint = new Blueprint(eveBlue.getItemID());
		newBlueprint.setTypeID(eveBlue.getTypeID());
		newBlueprint.setTypeName(eveBlue.getTypeName());
		newBlueprint.setLocationID(eveBlue.getLocationID());
		newBlueprint.setFlag(eveBlue.getFlag());
		newBlueprint.setQuantity(eveBlue.getQuantity());
		newBlueprint.setTimeEfficiency(eveBlue.getTimeEfficiency());
		newBlueprint.setMaterialEfficiency(eveBlue.getMaterialEfficiency());
		newBlueprint.setRuns(eveBlue.getRuns());
		newBlueprint.setPackaged((eveBlue.getQuantity() == -1) ? true : false);

		// Detect if BPO or BPC and set the flag.
		if (eveBlue.getRuns() == -1) {
			newBlueprint.setBpo(true);
		}
		return newBlueprint;
	}

	private Job convert2Job(final ApiNewIndustryJob evejob) {
		// Create the asset from the API asset.
		final Job newJob = new Job(evejob.getJobID());
		try {
			newJob.setOwnerID(evejob.getInstallerID());
			newJob.setFacilityID(evejob.getFacilityID());
			newJob.setStationID(evejob.getStationID());
			newJob.setActivityID(evejob.getActivityID());
			newJob.setBlueprintID(evejob.getBlueprintID());
			newJob.setBlueprintTypeID(evejob.getBlueprintTypeID());
			newJob.setBlueprintLocationID(evejob.getBlueprintLocationID());
			newJob.setRuns(evejob.getRuns());
			newJob.setCost(evejob.getCost());
			newJob.setLicensedRuns(evejob.getLicensedRuns());
			newJob.setProductTypeID(evejob.getProductTypeID());
			newJob.setStatus(evejob.getStatus());
			newJob.setTimeInSeconds(evejob.getTimeInSeconds());
			newJob.setStartDate(evejob.getStartDate());
			newJob.setEndDate(evejob.getEndDate());
			newJob.setCompletedDate(evejob.getCompletedDate());
			newJob.setCompletedCharacterID(evejob.getCompletedCharacterID());
			newJob.setSuccessfulRuns(evejob.getSuccessfulRuns());
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return newJob;
	}

	private MarketOrder convert2Order(final ApiMarketOrder eveorder) {
		// Create the asset from the API asset.
		final MarketOrder newMarketOrder = new MarketOrder(eveorder.getOrderID());
		try {
			newMarketOrder.setOwnerID(eveorder.getCharID());
			newMarketOrder.setStationID(eveorder.getStationID());
			newMarketOrder.setVolEntered(eveorder.getVolEntered());
			newMarketOrder.setVolRemaining(eveorder.getVolRemaining());
			newMarketOrder.setMinVolume(eveorder.getMinVolume());
			newMarketOrder.setOrderState(eveorder.getOrderState());
			newMarketOrder.setTypeID(eveorder.getTypeID());
			newMarketOrder.setRange(eveorder.getRange());
			newMarketOrder.setAccountKey(eveorder.getAccountKey());
			newMarketOrder.setDuration(eveorder.getDuration());
			newMarketOrder.setEscrow(eveorder.getEscrow());
			newMarketOrder.setPrice(eveorder.getPrice());
			newMarketOrder.setBid(eveorder.getBid());
			newMarketOrder.setIssuedDate(eveorder.getIssued());
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return newMarketOrder;
	}

	private String downloadAssetEveName(final long assetID) {
		// Wait up to one second to avoid request rejections from CCP.
		try {
			Thread.sleep(1000); //1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		final Vector<Long> ids = new Vector<Long>();
		ids.add(assetID);
		try {
			final LocationsParser parser = LocationsParser.getInstance();
			final LocationsResponse response = parser.getResponse(getAuthorization(), ids);
			if (null != response) {
				final HashSet<ApiLocation> userNames = new HashSet<ApiLocation>(response.getAll());
				if (userNames.size() > 0) return userNames.iterator().next().getItemName();
			}
		} catch (final ApiException e) {
			Log.w("NEOCOM", "W- EveChar.downloadAssetEveName - asset has no user name defined: " + assetID);
			//			e.printStackTrace();
		}
		return null;
	}

	private synchronized void downloadCharacterSheet() {
		logger.info(">> EveChar.downloadCharacterSheet");
		final String eveCharacterInfoCall = CHAR_CHARACTERSHEET + "?keyID=" + keyID + "&vCode=" + verificationCode
				+ "&characterID=" + characterID;
		final Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
		if (null == characterDoc) {
			setName("Corporation");
		} else {
			NodeList resultNodes = characterDoc.getElementsByTagName("result");
			Element result = (Element) resultNodes.item(0);
			if (null != result) {
				resultNodes = characterDoc.getElementsByTagName("name");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setName(text);
				}
				resultNodes = characterDoc.getElementsByTagName("balance");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setBalance(text);
				}
				resultNodes = characterDoc.getElementsByTagName("race");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setRace(text);
				}
				resultNodes = characterDoc.getElementsByTagName("cloneName");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setCloneName(text);
				}
				//			resultNodes = characterDoc.getElementsByTagName("cloneSkillPoints");
				//			result = (Element) resultNodes.item(0);
				//			if (null != result) {
				//				final String text = result.getTextContent();
				//				setCloneSkillPoints(text);
				//			}
				logger.info(".. Updated a new character <" + getName() + ">");
			}
		}
		logger.info("<< EveChar.downloadCharacterSheet");
	}

	@SuppressWarnings("rawtypes")
	private void downloadCharAssetsList() {
	}

	private synchronized void downloadEveCharacterInfo() {
		logger.info(">> EveChar.downloadEveCharacterInfo");
		final String eveCharacterInfoCall = EVE_CHARACTERINFO + "?keyID=" + keyID + "&vCode=" + verificationCode
				+ "&characterID=" + characterID;
		final Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
		if (null == characterDoc) {
			setName("Corporation");
		} else {
			NodeList resultNodes = characterDoc.getElementsByTagName("result");
			Element result = (Element) resultNodes.item(0);
			if (null != result) {
				resultNodes = characterDoc.getElementsByTagName("characterName");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					logger.info(".. Setting name <" + text + ">");
					setName(text);
				}
				resultNodes = characterDoc.getElementsByTagName("accountBalance");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setBalance(text);
				}
				resultNodes = characterDoc.getElementsByTagName("shipName");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setShipName(text);
				}
				resultNodes = characterDoc.getElementsByTagName("shipTypeName");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setShipTypeName(text);
				}
				resultNodes = characterDoc.getElementsByTagName("corporation");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setCorporationName(text);
				}
				resultNodes = characterDoc.getElementsByTagName("lastKnownLocation");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					setLastKnownLocation(text);
				}
				logger.info(".. Updated a new character <" + getName() + ">");
			}
		}
		logger.info("<< EveChar.downloadEveCharacterInfo");
	}

	private void downloadSkillTraining() {
		final SkillInTrainingParser parser = SkillInTrainingParser.getInstance();
		//		final ApiAuthorization auth = new ApiAuthorization(keyID, characterID, verificationCode);
		final SkillInTrainingResponse response;
		try {
			skillInTraining = parser.getResponse(getAuthorization());
		} catch (final ApiException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Asset> filterAssets4Name(final String moduleName) {
		///	Optimize the update of the assets to just process the ones with the -1 owner.
		List<Asset> accountList = new ArrayList<Asset>();
		try {
			final Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			final QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			final Where<Asset, String> where = queryBuilder.where();
			where.eq("name", moduleName);
			//			where.and();
			//			where.gt("count", new Integer(9));
			final PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			accountList = assetDao.query(preparedQuery);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Asset>) accountList;
	}

	private Instant getAssetsCacheTime() {
		if (null == assetsCacheTime) {
			assetsCacheTime = new Instant(0);
		}
		return assetsCacheTime;
	}

	/**
	 * Processes an asset and all their children. This method converts from a API record to a database asset
	 * record.
	 * 
	 * @param eveAsset
	 */
	private void processAsset(final EveAsset<EveAsset> eveAsset, final Asset parent) {
		final Asset myasset = convert2Asset(eveAsset);
		if (null != parent) {
			myasset.setParent(parent);
			myasset.setParentContainer(parent);
			// Set the location to the parent's location is not set.
			if (myasset.getLocationID() == -1) {
				myasset.setLocationID(parent.getLocationID());
			}
		}
		// Only search names for containers and ships.
		if (myasset.isShip()) {
			myasset.setUserLabel(downloadAssetEveName(myasset.getAssetID()));
		}
		if (myasset.isContainer()) {
			myasset.setUserLabel(downloadAssetEveName(myasset.getAssetID()));
		}
		try {
			final Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			final HashSet<EveAsset> children = new HashSet<EveAsset>(eveAsset.getAssets());
			if (children.size() > 0) {
				myasset.setContainer(true);
			}
			if (myasset.getCategory().equalsIgnoreCase("Ship")) {
				myasset.setShip(true);
			}
			assetDao.create(myasset);

			// Process all the children and convert them to assets.
			if (children.size() > 0) {
				for (final EveAsset childAsset : children) {
					processAsset(childAsset, myasset);
				}
			}
			logger.finest("-- Wrote asset to database id [" + myasset.getAssetID() + "]");
		} catch (final SQLException sqle) {
			logger.severe("E> Unable to create the new asset [" + myasset.getAssetID() + "]. " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}

	private ArrayList<Job> searchIndustryJobs() {
		logger.info(">> EveChar.searchIndustryJobs");
		//	Select assets of type blueprint and that are of T2.
		List<Job> jobList = new ArrayList<Job>();
		try {
			AppConnector.startChrono();
			final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
			final QueryBuilder<Job, String> qb = jobDao.queryBuilder();
			qb.where().eq("ownerID", getCharacterID());
			qb.orderBy("endDate", false);
			jobList = jobDao.query(qb.prepare());
			checkRefresh(jobList.size(), "JOBS");
			final Duration lapse = AppConnector.timeLapse();
			logger.info("-- Time lapse for [SELECT JOBS] " + lapse);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Job>) jobList;
	}
}

// - UNUSED CODE ............................................................................................
