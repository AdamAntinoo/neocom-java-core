//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
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

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.EDataBlock;
import org.dimensinfin.evedroid.enums.EPropertyTypes;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.model.shared.IndustryJob;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.corporation.AccountBalanceParser;
import com.beimin.eveapi.parser.eve.CharacterInfoParser;
import com.beimin.eveapi.parser.pilot.CharacterSheetParser;
import com.beimin.eveapi.parser.pilot.LocationsParser;
import com.beimin.eveapi.parser.pilot.PilotAccountBalanceParser;
import com.beimin.eveapi.parser.pilot.SkillInTrainingParser;
import com.beimin.eveapi.parser.pilot.SkillQueueParser;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.beimin.eveapi.response.pilot.CharacterSheetResponse;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;
import com.beimin.eveapi.response.pilot.SkillQueueResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import com.beimin.eveapi.response.shared.LocationsResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class NeoComCharacter extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComCharacter");

	public static NeoComCharacter build(final Character coreChar, final NeoComApiKey apikey) throws ApiException {
		// The api to use depends on the type of character.
		if (apikey.getType() == KeyType.Character) return NeoComCharacter.createPilot(coreChar, apikey);
		if (apikey.getType() == KeyType.Corporation) return NeoComCharacter.createCorporation(coreChar, apikey);
		return NeoComCharacter.createPilot(coreChar, apikey);
	}

	private static Corporation createCorporation(final Character coreChar, final NeoComApiKey apikey)
			throws ApiException {
		Corporation newcorp = new Corporation();
		newcorp.setApiKey(apikey);
		// Copy the authorization and add to it the characterID
		ApiAuthorization authcopy = new ApiAuthorization(apikey.getKey(), coreChar.getCharacterID(),
				apikey.getValidationCode());
		newcorp.setAuthorization(authcopy);
		newcorp.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		AccountBalanceParser balanceparser = new AccountBalanceParser();
		AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
		if (null != balanceresponse) {
			Set<EveAccountBalance> balance = balanceresponse.getAll();
			if (balance.size() > 0) {
				newcorp.setAccountBalance(balance.iterator().next().getBalance());
			}
		}
		// Character information
		CharacterInfoParser infoparser = new CharacterInfoParser();
		CharacterInfoResponse inforesponse = infoparser.getResponse(authcopy);
		if (null != inforesponse) {
			newcorp.characterInfo = inforesponse;
		}
		return newcorp;
	}

	private static Pilot createPilot(final Character coreChar, final NeoComApiKey apikey) throws ApiException {
		Pilot newchar = new Pilot();
		newchar.setApiKey(apikey);
		// Copy the authorization and add to it the characterID
		ApiAuthorization authcopy = new ApiAuthorization(apikey.getKey(), coreChar.getCharacterID(),
				apikey.getValidationCode());
		newchar.setAuthorization(authcopy);
		newchar.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		PilotAccountBalanceParser balanceparser = new PilotAccountBalanceParser();
		AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
		if (null != balanceresponse) {
			Set<EveAccountBalance> balance = balanceresponse.getAll();
			if (balance.size() > 0) {
				newchar.setAccountBalance(balance.iterator().next().getBalance());
			}
		}
		// Character information
		CharacterInfoParser infoparser = new CharacterInfoParser();
		CharacterInfoResponse inforesponse = infoparser.getResponse(authcopy);
		if (null != inforesponse) {
			newchar.characterInfo = inforesponse;
		}
		// Character sheet information
		CharacterSheetParser sheetparser = new CharacterSheetParser();
		CharacterSheetResponse sheetresponse = sheetparser.getResponse(authcopy);
		if (null != sheetresponse) {
			newchar.setCharacterSheet(sheetresponse);
		}
		// Skill list
		SkillQueueParser skillparser = new SkillQueueParser();
		SkillQueueResponse skillresponse = skillparser.getResponse(authcopy);
		if (null != skillresponse) {
			newchar.setSkillQueue(skillresponse.getAll());
		}
		// Skill in training
		SkillInTrainingParser trainingparser = new SkillInTrainingParser();
		SkillInTrainingResponse trainingresponse = trainingparser.getResponse(authcopy);
		if (null != skillresponse) {
			newchar.setSkillInTraining(trainingresponse);
		}
		// Full list of assets from database.
		//		newchar.accessAllAssets();
		return newchar;
	}

	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the delegated core eveapi Character */
	protected NeoComApiKey										apikey							= null;
	private ApiAuthorization									authorization				= null;
	protected transient Character							delegatedCharacter	= null;
	protected transient CharacterInfoResponse	characterInfo				= null;
	private final boolean											active							= true;
	private double														accountBalance			= 0.0;

	// - T R A N S I E N T   D A T A
	protected transient Instant								lastCCPAccessTime		= null;
	protected transient Instant								assetsCacheTime			= null;
	protected transient AssetsManager					assetsManager				= null;
	protected transient Instant								blueprintsCacheTime	= null;
	protected transient Instant								jobsCacheTime				= null;
	protected transient ArrayList<Job>				jobList							= null;
	protected transient Instant								marketCacheTime			= null;
	private transient ArrayList<Property>			locationRoles				= null;
	private transient HashMap<Long, Property>	actions4Character		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected NeoComCharacter() {
		lastCCPAccessTime = Instant.now();
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
		final ArrayList<NeoComMarketOrder> orders = this.searchMarketOrders();
		// Create the analytical groups.
		final MarketOrderAnalyticalGroup scheduledBuyGroup = new MarketOrderAnalyticalGroup(10, "SCHEDULED BUYS");
		final MarketOrderAnalyticalGroup buyGroup = new MarketOrderAnalyticalGroup(30, "BUYS");
		final MarketOrderAnalyticalGroup sellGroup = new MarketOrderAnalyticalGroup(40, "SELLS");
		final MarketOrderAnalyticalGroup finishedGroup = new MarketOrderAnalyticalGroup(50, "FINISHED");

		for (final NeoComMarketOrder order : orders) {
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
		final ArrayList<NeoComAsset> modules = this.getAssetsManager().searchT2Modules();
		final HashMap<String, Resource> mods = new HashMap<String, Resource>();
		for (final NeoComAsset mc : modules) {
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

	public void addLocationRole(final EveLocation theSelectedLocation, final String locationrole) {
		if (null == locationRoles) {
			this.accessLocationRoles();
		}
		if (locationRoles.size() < 1) {
			this.accessLocationRoles();
		}
		Property hit = new Property(EPropertyTypes.LOCATIONROLE);
		hit.setOwnerID(delegatedCharacter.getCharacterID());
		hit.setNumericValue(theSelectedLocation.getID());
		hit.setStringValue(locationrole);
		locationRoles.add(hit);
	}

	@Override
	public void clean() {
		assetsManager = null;
		//		lastCCPAccessTime = null;
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
	 * Removes the records that define the association of roles to the selected location. This clears all the
	 * roles for a location and if the user only wants to clear one he/she has to activate the others again
	 * since all get removed.
	 * 
	 * @param theSelectedLocation
	 */
	public void clearLocationRoles(final EveLocation theSelectedLocation) {
		if (null == locationRoles) {
			this.accessLocationRoles();
		}
		for (Property role : locationRoles)
			if (role.getNumericValue() == Double.valueOf(theSelectedLocation.getID())) {
				//		if (null != hit) {
				try {
					Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
					propertyDao.delete(role);
					locationRoles = null;
				} catch (final SQLException sqle) {
					sqle.printStackTrace();
				}
			}
	}

	public abstract ArrayList<AbstractComplexNode> collaborate2Model(String variant);

	public abstract void downloadAssets();

	public abstract void downloadBlueprints();

	public abstract void downloadIndustryJobs();

	public abstract void downloadMarketOrders();

	public void forceRefresh() {
		this.clean();
		assetsManager = new AssetsManager(this);
		AppConnector.addCharacterUpdateRequest(this.getCharacterID());
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public HashMap<Long, Property> getActions() {
		if (null == actions4Character) {
			this.accessActionList();
		}
		return actions4Character;
	}

	/**
	 * Delegate the request to the assets manager that will make a sql request to get the assets number.
	 * 
	 * @return
	 */
	public long getAssetCount() {
		return this.getAssetsManager().getAssetTotalCount();
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
		return authorization;
	}

	public long getCharacterID() {
		return delegatedCharacter.getCharacterID();
	}

	/**
	 * Returns a non null default location so any Industry action has a location to be used as reference. Any
	 * location is valid.
	 * 
	 * @return
	 */
	public EveLocation getDefaultLocation() {
		return this.getAssetsManager().getLocations().get(1);
	}

	public ArrayList<Job> getIndustryJobs() {
		if (null == jobList) {
			jobList = this.searchIndustryJobs();
		}
		return jobList;
	}

	/**
	 * Returns the first location that matches the specified role. This is confusing because more that one
	 * location can match that role but this is a first approach.
	 * 
	 * @param matchingRole
	 * @return
	 */
	public EveLocation getLocation4Role(final String matchingRole) {
		if (null == locationRoles) {
			this.accessLocationRoles();
		}
		for (Property role : locationRoles) {
			String value = role.getPropertyType().toString();
			if (role.getPropertyType().toString().equalsIgnoreCase(matchingRole))
				return AppConnector.getDBConnector().searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
			//		Property currentRole = locationRoles.get(locID);
			//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
			//				return AppConnector.getDBConnector().searchLocationbyID(locID);
		}
		return null;
	}

	/**
	 * Return the firat location that matches that role at the specified Region.
	 * 
	 * @param matchingRole
	 * @param Region
	 * @return
	 */
	public EveLocation getLocation4Role(final String matchingRole, final String region) {
		//		EveLocation preferredLocation = null;
		for (Property role : locationRoles)
			if (role.getPropertyType().toString().equalsIgnoreCase(matchingRole)) {
				EveLocation target = AppConnector.getDBConnector()
						.searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
				if (target.getRegion().equalsIgnoreCase(region)) return target;
				//		Property currentRole = locationRoles.get(locID);
				//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
				//				return AppConnector.getDBConnector().searchLocationbyID(locID);
			}
		return null;
	}

	/**
	 * From the list of roles set to location, extract all the roles that match the location ID.
	 * 
	 * @param targetLocationID
	 * @param defaultValue
	 * @return
	 * @return
	 */
	public ArrayList<Property> getLocationRoles(final long targetLocationID, final String defaultValue) {
		ArrayList<Property> roles = new ArrayList<Property>();
		if (null == locationRoles) {
			this.accessLocationRoles();
		}
		for (Property role : locationRoles)
			if (role.getNumericValue() == Double.valueOf(targetLocationID)) {
				roles.add(role);
			}
		//		Property hit = locationRoles.get(targetLocationID);
		//		if (null == hit)
		//			return defaultValue;
		//		else
		//			return hit.getStringValue();
		return roles;
	}

	public ArrayList<NeoComMarketOrder> getMarketOrders() {
		return this.searchMarketOrders();
	}

	public String getName() {
		return delegatedCharacter.getName();
	}

	public ArrayList<NeoComAsset> getShips() {
		return this.searchAsset4Category(this.getCharacterID(), "Ship");
	}

	/**
	 * Return the active state set by the user. The user can hide some characters from the application
	 * processing through this flag.
	 * 
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	public boolean isCorporation() {
		if (this.getName().equalsIgnoreCase("Corporation"))
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

	/**
	 * Stores as a register the user preferred action to perform with this item when there is a shortage while
	 * manufacturing. First get the current action defined for this item on the database and update it or create
	 * a new one of not found.
	 * 
	 * @param typeID
	 * @param taskName
	 */
	public void putAction4Item(final int typeID, final String taskName) {
		if (null == actions4Character) {
			this.accessActionList();
		}
		Property hit = actions4Character.get(typeID);
		if (null == hit) {
			hit = new Property(EPropertyTypes.MANUFACTUREACTION);
			hit.setOwnerID(delegatedCharacter.getCharacterID());
			//			hit.setPropertyType();
			hit.setNumericValue(typeID);
			hit.setStringValue(taskName);
			actions4Character = null;
		}
		hit.setStringValue(taskName);
	}

	public ArrayList<NeoComMarketOrder> searchMarketOrders() {
		//	Select assets of type blueprint and that are of T2.
		List<NeoComMarketOrder> orderList = new ArrayList<NeoComMarketOrder>();
		try {
			AppConnector.startChrono();
			final Dao<NeoComMarketOrder, String> marketOrderDao = AppConnector.getDBConnector().getMarketOrderDAO();
			final QueryBuilder<NeoComMarketOrder, String> qb = marketOrderDao.queryBuilder();
			qb.where().eq("ownerID", this.getCharacterID());
			orderList = marketOrderDao.query(qb.prepare());
			final Duration lapse = AppConnector.timeLapse();
			NeoComCharacter.logger.info("-- Time lapse for [SELECT MARKETORDERS] " + lapse);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComMarketOrder>) orderList;
	}

	public void setAccountBalance(final double accountBalance) {
		this.accountBalance = accountBalance;
	}

	/**
	 * Connects the AssetsManager to one that maybe has been restored from persistence storage.
	 * 
	 * @param manager
	 */
	public void setAssetsManager(final AssetsManager manager) {
		assetsManager = manager;
	}

	public void setAuthorization(final ApiAuthorization authorization) {
		this.authorization = authorization;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComCharacter [");
		buffer.append("assets:").append(this.getAssetCount()).append(" ");
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	public abstract void updateCharacterInfo();

	/**
	 * Updates the list of assets, regions and locations from the database. This code will initialize the
	 * AssetsManager with that information on application load preferably and that lengthy operation will be
	 * done on background. After this call the list of assets by location is accessible with just a call.
	 */
	protected void accessAllAssets() {
		// Do this on the assets manager or create one is reuired.
		this.getAssetsManager().accessAllAssets();
	}

	protected double calculateAssetValue(final NeoComAsset asset) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = asset.getItem().getHighestBuyerPrice().getPrice();
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information.
	 * 
	 * @param eveAsset
	 * @return
	 */
	protected NeoComAsset convert2Asset(final Asset eveAsset) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		// Children locations have a null on this field. Set it to their parents
		final Long assetloc = eveAsset.getLocationID();
		// DEBUG Add a conditional breakpoint for an specific asset.
		//		Long test = new Long(1021037093228L);
		//		if (eveAsset.getTypeID() == 8625) {
		//			@SuppressWarnings("unused")
		//			int i = 1; // stop point
		//		}
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
		newAsset.setIskvalue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	protected NeoComBlueprint convert2Blueprint(final Blueprint eveBlue) {
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
		if (eveBlue.getRuns() == -1) {
			newBlueprint.setBpo(true);
		}
		return newBlueprint;
	}

	protected Job convert2Job(final IndustryJob evejob) {
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
			//			newJob.setSuccessfulRuns(evejob.getSuccessfulRuns());
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return newJob;
	}

	protected NeoComMarketOrder convert2Order(final MarketOrder eveorder) {
		// Create the asset from the API asset.
		final NeoComMarketOrder newMarketOrder = new NeoComMarketOrder(eveorder.getOrderID());
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

	protected String downloadAssetEveName(final long assetID) {
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
			final LocationsResponse response = parser.getResponse(this.getAuthorization(), ids);
			if (null != response) {
				Set<Location> userNames = response.getAll();
				if (userNames.size() > 0) return userNames.iterator().next().getItemName();
			}
		} catch (final ApiException e) {
			NeoComCharacter.logger.info("W- EveChar.downloadAssetEveName - asset has no user name defined: " + assetID);
			//			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Processes an asset and all their children. This method converts from a API record to a database asset
	 * record.
	 * 
	 * @param eveAsset
	 */
	protected void processAsset(final Asset eveAsset, final NeoComAsset parent) {
		final NeoComAsset myasset = this.convert2Asset(eveAsset);
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
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		if (myasset.isContainer()) {
			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
		}
		try {
			final Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			final HashSet<Asset> children = new HashSet<Asset>(eveAsset.getAssets());
			if (children.size() > 0) {
				myasset.setContainer(true);
			}
			if (myasset.getCategory().equalsIgnoreCase("Ship")) {
				myasset.setShip(true);
			}
			assetDao.create(myasset);

			// Process all the children and convert them to assets.
			if (children.size() > 0) {
				for (final Asset childAsset : children) {
					this.processAsset(childAsset, myasset);
				}
			}
			NeoComCharacter.logger.finest("-- Wrote asset to database id [" + myasset.getAssetID() + "]");
		} catch (final SQLException sqle) {
			NeoComCharacter.logger
					.severe("E> Unable to create the new asset [" + myasset.getAssetID() + "]. " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}

	protected ArrayList<NeoComAsset> searchAsset4Category(final long characterID, final String category) {
		//	Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("category", category);
			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;

	}

	protected ArrayList<Job> searchIndustryJobs() {
		NeoComCharacter.logger.info(">> EveChar.searchIndustryJobs");
		//	Select assets of type blueprint and that are of T2.
		List<Job> jobList = new ArrayList<Job>();
		try {
			AppConnector.startChrono();
			final Dao<Job, String> jobDao = AppConnector.getDBConnector().getJobDAO();
			final QueryBuilder<Job, String> qb = jobDao.queryBuilder();
			qb.where().eq("ownerID", this.getCharacterID());
			qb.orderBy("endDate", false);
			jobList = jobDao.query(qb.prepare());
			this.checkRefresh(jobList.size(), "JOBS");
			final Duration lapse = AppConnector.timeLapse();
			NeoComCharacter.logger.info("-- Time lapse for [SELECT JOBS] " + lapse);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Job>) jobList;
	}

	protected void setApiKey(final NeoComApiKey apikey) {
		this.apikey = apikey;
	}

	protected void setDelegatedCharacter(final Character coreChar) {
		delegatedCharacter = coreChar;
	}

	private void accessActionList() {
		List<Property> actionList = new ArrayList<Property>();
		try {
			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
			Where<Property, String> where = queryBuilder.where();
			where.eq("ownerID", delegatedCharacter.getCharacterID());
			where.and();
			where.eq("propertyType", EPropertyTypes.MANUFACTUREACTION.toString());
			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
			actionList = propertyDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		// Process the returned list and store in the character.
		actions4Character = new HashMap<Long, Property>();
		for (Property property : actionList) {
			// The type selected for the action is stored as the property key.
			actions4Character.put(Double.valueOf(property.getNumericValue()).longValue(), property);
		}
	}

	private void accessLocationRoles() {
		//		List<Property> roleList = new ArrayList<Property>();
		try {
			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
			Where<Property, String> where = queryBuilder.where();
			where.eq("ownerID", delegatedCharacter.getCharacterID());
			where.and();
			where.eq("propertyType", EPropertyTypes.LOCATIONROLE.toString());
			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
			locationRoles = new ArrayList(propertyDao.query(preparedQuery));
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
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
				this.cleanJobs();
				AppConnector.addCharacterUpdateRequest(this.getCharacterID());
			}
			if (section.equalsIgnoreCase("MARKETORDERS")) {
				AppConnector.addCharacterUpdateRequest(this.getCharacterID());
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

	private ArrayList<NeoComAsset> filterAssets4Name(final String moduleName) {
		///	Optimize the update of the assets to just process the ones with the -1 owner.
		List<NeoComAsset> accountList = new ArrayList<NeoComAsset>();
		try {
			final Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			final QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			final Where<NeoComAsset, String> where = queryBuilder.where();
			where.eq("name", moduleName);
			//			where.and();
			//			where.gt("count", new Integer(9));
			final PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			accountList = assetDao.query(preparedQuery);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) accountList;
	}

	private Instant getAssetsCacheTime() {
		if (null == assetsCacheTime) {
			assetsCacheTime = new Instant(0);
		}
		return assetsCacheTime;
	}
}

// - UNUSED CODE ............................................................................................
