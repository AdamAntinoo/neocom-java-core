//  PROJECT:      NeoCom.Model (NEOC.M)
//  AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:    (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT:  Java 1.8 Library.
//  DESCRIPTION:  Java library for the NeoCom project that contains the model classes and all the
//                data management code to maintain the different model structures and functionalities.
//                The module integrates all data conversion and functionalities that can be
//                used on any platform not being dependant on Android development.
//                New functionalities allow the access of Eve Online CCP data with the new
//                developer ESI api and keeps the transformations and the code for the persistence
//                of the downloaded data on an external database.
//                The code isolates from the external database implementation to the extent to keep
//                the code compatible with Android and SpringBoot.
package org.dimensinfin.eveonline.neocom.model;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.corporation.AccountBalanceParser;
import com.beimin.eveapi.parser.eve.CharacterInfoParser;
import com.beimin.eveapi.parser.pilot.PilotAccountBalanceParser;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.manager.DownloadManager;

import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class NeoComCharacter extends NeoComNode implements Comparable<NeoComCharacter> {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3456210619258009170L;
	private static Logger logger = Logger.getLogger("NeoComCharacter");

	public static NeoComCharacter build (final Character coreChar, final NeoComApiKey apikey) throws ApiException {
		// The api to use depends on the type of character.
		if ( apikey.getType() == KeyType.Character ) return NeoComCharacter.createPilot(coreChar, apikey);
		if ( apikey.getType() == KeyType.Corporation ) return NeoComCharacter.createCorporation(coreChar, apikey);
		// By default create a Pilot.
		return NeoComCharacter.createPilot(coreChar, apikey);
	}

	/**
	 * Creates a new Corporation Character from some calls to CCP sources. I use the eveapi to process that
	 * information.
	 */
	private static Corporation createCorporation (final Character coreChar, final NeoComApiKey apikey) throws ApiException {
		final Corporation newcorp = new Corporation();
		newcorp.setApiKey(apikey);
		// Copy the authorization and add to it the characterID
		final ApiAuthorization authcopy = new ApiAuthorization(apikey.getKey(), coreChar.getCharacterID(),
				apikey.getValidationCode());
		newcorp.setAuthorization(authcopy);
		newcorp.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		final AccountBalanceParser balanceparser = new AccountBalanceParser();
		final AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
		if ( null != balanceresponse ) {
			final Set<EveAccountBalance> balance = balanceresponse.getAll();
			if ( balance.size() > 0 ) {
				newcorp.setAccountBalance(balance.iterator().next().getBalance());
			}
		}
		// Character information
		final CharacterInfoParser infoparser = new CharacterInfoParser();
		final CharacterInfoResponse inforesponse = infoparser.getResponse(authcopy);
		if ( null != inforesponse ) {
			newcorp.setInfo(inforesponse);
		}
		// Update the last updated timestamp from the CharacterInfoResponse.
//		newcorp.getDownloadManager().updateCharacterDataTimeStamp(inforesponse.getCachedUntil());

		// Because renderization needs some detailed information only found on Managers we get that copied
		// into fields before terminating the construction of the instance.
		//		newcorp.assetTotalCount = new AssetsManager(newcorp).getTotalAssetsNumber();
		return newcorp;
	}

	private static Pilot createPilot (final Character coreChar, final NeoComApiKey apikey) throws ApiException {
		final Pilot newchar = new Pilot();
		newchar.setApiKey(apikey);
		// Copy the authorization and add to it the characterID
		final ApiAuthorization authcopy = new ApiAuthorization(apikey.getKey(), coreChar.getCharacterID(),
				apikey.getValidationCode());
		newchar.setAuthorization(authcopy);
		// Copy the id to a non volatile field.
		newchar.setID(coreChar.getCharacterID());
		newchar.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		final PilotAccountBalanceParser balanceparser = new PilotAccountBalanceParser();
		final AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
		if ( null != balanceresponse ) {
			final Set<EveAccountBalance> balance = balanceresponse.getAll();
			if ( balance.size() > 0 ) {
				newchar.setAccountBalance(balance.iterator().next().getBalance());
			}
		}
		// Character information
		final CharacterInfoParser infoparser = new CharacterInfoParser();
		final CharacterInfoResponse inforesponse = infoparser.getResponse(authcopy);
		if ( null != inforesponse ) {
			newchar.setInfo(inforesponse);
		}
		//		// Character sheet information
		//		CharacterSheetParser sheetparser = new CharacterSheetParser();
		//		CharacterSheetResponse sheetresponse = sheetparser.getResponse(authcopy);
		//		if (null != sheetresponse) {
		//			newchar.setCharacterSheet(sheetresponse);
		//		}
		//		// Skill list
		//		SkillQueueParser skillparser = new SkillQueueParser();
		//		SkillQueueResponse skillresponse = skillparser.getResponse(authcopy);
		//		if (null != skillresponse) {
		//			newchar.setSkillQueue(skillresponse.getAll());
		//		}
		//		// Skill in training
		//		SkillInTrainingParser trainingparser = new SkillInTrainingParser();
		//		SkillInTrainingResponse trainingresponse = trainingparser.getResponse(authcopy);
		//		if (null != skillresponse) {
		//			newchar.setSkillInTraining(trainingresponse);
		//		}
		// Full list of assets from database.
		//	newchar.accessAllAssets();

		// Update the last updated timestamp from the CharacterInfoResponse.
//		newchar.getDownloadManager().updateCharacterDataTimeStamp(inforesponse.getCachedUntil());

		// Because renderization needs some detailed information only found on Managers we get that copied
		// into fields before terminating the construction of the instance.
		//		newchar.assetTotalCount = new AssetsManager(newchar).getTotalAssetsNumber();
		return newchar;
	}

	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the key that generated this character on the first place. */
	private transient NeoComApiKey apikey = null;
	private ApiAuthorization authorization = null;
	/** Parent reference to the container Login. This is used when reconstructing the data chain. */
	private Login parentLoginRef = null;
	/** Should contain a copy of this data value can can also be found at the delegatedCharacter. */
	private long characterID = -1;
	/** Reference to the original eveapi Character data. */
	private Character delegatedCharacter = null;
	/**
	 * Character detailed information from the CharacterInfoResponse CCP api call. This can apply to Pilots and
	 * Corporations.
	 */
	private CharacterInfoResponse characterInfo = null;
	/**
	 * Character account balance from the AccountBalanceResponse CCP api call. This can apply to Pilots and
	 * Corporations.
	 */
	private double accountBalance = 0.0;

	// - T R A N S I E N T   D A T A

	//	private transient Instant lastCCPAccessTime = null;
	@JsonIgnore
	private transient DownloadManager _downloadManager = null;
//	@JsonIgnore
//	private transient AssetsManager _assetsManager = null;
//	@JsonIgnore
//	private transient PlanetaryManager _planetaryManager = null;
//	@JsonIgnore
//	private transient BlueprintManager _blueprintManager = null;

	// - F I E L D S   T O   R E V I E W
	//	/** Copy of the total number of assets got from one AssetsManager. */
	//	protected long											assetTotalCount			= 0;

	/**
	 * State of this character. The use can deactivate the character so it is removed from the update lists even
	 * the current data is still visible.
	 */
	//	private final boolean active = true;

	//	private ArrayList<Property>					locationRoles				= null;
	//	private HashMap<Long, Property>			actions4Character		= null;
	//	//	private transient Instant						assetsCacheTime			= null;


	//	private transient Instant						blueprintsCacheTime	= null;
	//	protected transient Instant					jobsCacheTime				= null;
	//	private transient ArrayList<Job>		jobList							= null;
	//	protected transient Instant					marketCacheTime			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected NeoComCharacter () {
		super();
		jsonClass = "NeoComCharacter";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getURLForAvatar () {
		return "http://image.eveonline.com/character/" + this.getCharacterID() + "_256.jpg";
	}

	public String getLastKnownLocation () {
		final CharacterInfoResponse info = this.getCharacterInfo();
		if ( null == info ) return "-Unknown-";
		else return info.getLastKnownLocation();
	}

//	public DownloadManager getDownloadManager () {
//		if ( null == _downloadManager ) {
//			_downloadManager = new DownloadManager(this);
//		}
//		// Make sure the Manager is already connected to the Pilot.
//		_downloadManager.setPilot(this);
//		return _downloadManager;
//	}

//	/**
//	 * Check all the cache time stamps for existence and in case the TS exists if the time has already passed.
//	 * TS are stored at the database and updated any time some data is downloaded and updated with the cached
//	 * time reported by CCP.
//	 */
//	public EDataUpdateJobs needsUpdate () {
//		// Check for character data cache.
//		if ( null == getDownloadManager()._characterCacheTime ) {
//			try {
//				// Get access to the database record for this Character cache time stamp.
//				final String reference = this.getCharacterID() + ".CHARACTERDATA";
//				getDownloadManager()._characterCacheTime = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO().queryForId(reference);
//				if ( null == getDownloadManager()._characterCacheTime ) return EDataUpdateJobs.CHARACTERDATA;
//			} catch (final SQLException sqle) {
//				return EDataUpdateJobs.CHARACTERDATA;
//			}
//		}
//		if ( ModelAppConnector.getSingleton().checkExpiration(getDownloadManager()._characterCacheTime.getTimeStamp(), ModelWideConstants.NOW) )
//			return EDataUpdateJobs.CHARACTERDATA;
//		// Check for asset cached time.
//		if ( null == getDownloadManager()._assetsCacheTime ) {
//			try {
//				// Get access to the database record for this Character cache time stamp.
//				final String reference = this.getCharacterID() + ".ASSETDATA";
//				getDownloadManager()._assetsCacheTime = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO().queryForId(reference);
//				if ( null == getDownloadManager()._assetsCacheTime ) return EDataUpdateJobs.ASSETDATA;
//			} catch (final SQLException sqle) {
//				return EDataUpdateJobs.ASSETDATA;
//			}
//		}
//		if ( ModelAppConnector.getSingleton().checkExpiration(getDownloadManager()._assetsCacheTime.getTimeStamp(), ModelWideConstants.NOW) )
//			return EDataUpdateJobs.ASSETDATA;
//		// Check for blueprints cached time.
//		if ( null == getDownloadManager()._blueprintsCacheTime ) {
//			try {
//				// Get access to the database record for this Character cache time stamp.
//				final String reference = this.getCharacterID() + ".BLUEPRINTDATA";
//				getDownloadManager()._blueprintsCacheTime = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO().queryForId(reference);
//				if ( null == getDownloadManager()._blueprintsCacheTime ) return EDataUpdateJobs.BLUEPRINTDATA;
//			} catch (final SQLException sqle) {
//				return EDataUpdateJobs.BLUEPRINTDATA;
//			}
//		}
//		if ( ModelAppConnector.getSingleton().checkExpiration(getDownloadManager()._blueprintsCacheTime.getTimeStamp(), ModelWideConstants.NOW) )
//			return EDataUpdateJobs.BLUEPRINTDATA;
//
//		// If not returned before we have nothing to update
//		return EDataUpdateJobs.READY;
//	}

	// [01]
	public int compareTo (final NeoComCharacter target) {
		if ( this.getCharacterID() > target.getCharacterID() ) return -1;
		if ( this.getCharacterID() == target.getCharacterID() ) return 0;
		return 1;
	}

	public void connectLogin (final Login newref) {
		parentLoginRef = newref;
	}

	public double getAccountBalance () {
		return accountBalance;
	}


	public Date getApiKeyExpiration () {
		return apikey.getDelegatedApiKey().getExpires();
	}

	public Date getApiKeyPaidUntil () {
		return apikey.getPaidUntil();
	}

	//	/**
	//	 * Delegate the request to the assets manager that will make a sql request to get the assets number.
	//	 * 
	//	 * @return
	//	 */
	//	public long getAssetCount() {
	//		return this.getAssetsManager().getTotalAssetsNumber();
	//	}

//	@JsonIgnore
//	public AssetsManager getAssetsManager () {
//		if ( null == _assetsManager ) {
//			_assetsManager = new AssetsManager(this);
//		}
//		// Make sure the Manager is already connected to the Pilot.
//		_assetsManager.setPilot(this);
//		return _assetsManager;
//	}
//
//	public BlueprintManager getBlueprintsManager () {
//		if ( null == _blueprintManager ) {
//			_blueprintManager = new BlueprintManager(this);
//		}
//		// Make sure the Manager is already connected to the Pilot.
//		_blueprintManager.setPilot(this);
//		return _blueprintManager;
//	}

	public ApiAuthorization getAuthorization () {
		return authorization;
	}

	public long getCharacterID () {
		// Delegated maybe null when read from storage. Return the copy
		if ( null == delegatedCharacter )
			return characterID;
		else
			return delegatedCharacter.getCharacterID();
	}

	public CharacterInfoResponse getCharacterInfo () {
		if ( null == characterInfo ) {
			try {
				// Character information
				final CharacterInfoParser infoparser = new CharacterInfoParser();
				CharacterInfoResponse inforesponse = null;
				inforesponse = infoparser.getResponse(this.getAuthorization());
				if ( null != inforesponse ) {
					this.setInfo(inforesponse);
				}
			} catch (final ApiException apie) {
				NeoComCharacter.logger.warning("W- [NeoComCharacter.getCharacterInfo]> Exception: " + apie.getMessage());
			}
		}
		return characterInfo;
	}

//	/**
//	 * Returns a non null default location so any Industry action has a location to be used as reference. Any
//	 * location is valid.
//	 */
//	@JsonIgnore
//	public EveLocation getDefaultLocation () {
//		return this.getAssetsManager().initialize().getLocations().values().iterator().next();
//	}

	public Character getDelegatedCharacter () {
		return delegatedCharacter;
	}


	@JsonIgnore
	public Login getLoginRef () {
		if ( null == parentLoginRef )
			throw new NeocomRuntimeException("Null pointer trying to access a Pilot's Login that is orphan.");
		return parentLoginRef;
	}

	public String getName () {
		return delegatedCharacter.getName();
	}

//	@JsonIgnore
//	public PlanetaryManager getPlanetaryManager () {
//		if ( null == _planetaryManager ) {
//			_planetaryManager = new PlanetaryManager(this);
//		}
//		return _planetaryManager;
//	}

	/**
	 * Return the active state set by the user. The user can hide some characters from the application
	 * processing through this flag.
	 */
	public boolean isActive () {
		return true;
	}

	//	public boolean isCorporation() {
	//		if (this.getName().equalsIgnoreCase("Corporation"))
	//			return true;
	//		else
	//			return false;
	//	}


	//	public ArrayList<NeoComMarketOrder> searchMarketOrders() {
	//		//	Select assets of type blueprint and that are of T2.
	//		List<NeoComMarketOrder> orderList = new ArrayList<NeoComMarketOrder>();
	//		try {
	//			ModelAppConnector.getSingleton().startChrono();
	//			final Dao<NeoComMarketOrder, String> marketOrderDao = ModelAppConnector.getSingleton().getDBConnector()
	//					.getMarketOrderDAO();
	//			final QueryBuilder<NeoComMarketOrder, String> qb = marketOrderDao.queryBuilder();
	//			qb.where().eq("ownerID", this.getCharacterID());
	//			orderList = marketOrderDao.query(qb.prepare());
	//			final Duration lapse = ModelAppConnector.getSingleton().timeLapse();
	//			NeoComCharacter.logger.info("-- Time lapse for [SELECT MARKETORDERS] " + lapse);
	//		} catch (final SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		return (ArrayList<NeoComMarketOrder>) orderList;
	//	}

	public void setAccountBalance (final double accountBalance) {
		this.accountBalance = accountBalance;
	}

	//	/**
	//	 * Connects the AssetsManager to one that maybe has been restored from persistence storage.
	//	 * 
	//	 * @param manager
	//	 */
	//	public void setAssetsManager(final AssetsManager manager) {
	//		_assetsManager = manager;
	//	}

	public void setAuthorization (final ApiAuthorization authorization) {
		this.authorization = authorization;
	}

	public void setID (final long newid) {
		characterID = newid;
	}

	//	public void updateAssetsAccesscacheTime(final Date newCacheTime) {
	//		assetsCacheTime = new Instant(newCacheTime);
	//	}

	public void setInfo (final CharacterInfoResponse inforesponse) {
		characterInfo = inforesponse;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("NeoComCharacter [");
		buffer.append("name:").append(this.getName()).append(" ");
		//	buffer.append("assets#:").append(this.getAssetCount()).append(" ");
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	public abstract void updateCharacterInfo ();

	//	/**
	//	 * Updates the list of assets, regions and locations from the database. This code will initialize the
	//	 * AssetsManager with that information on application load preferably and that lengthy operation will be
	//	 * done on background. After this call the list of assets by location is accessible with just a call.
	//	 */
	//	protected void accessAllAssets() {
	//		// Do this on the assets manager or create one is reuired.
	//		this.getAssetsManager().accessAllAssets();
	//	}

	//	public void updateLastAccess (final Date cachedUntil) {
	//		lastCCPAccessTime = new Instant(cachedUntil);
	//	}

	//	protected double calculateAssetValue(final NeoComAsset asset) {
	//		// Skip blueprints from the value calculations
	//		double assetValueISK = 0.0;
	//		if (null != asset) {
	//			EveItem item = asset.getItem();
	//			if (null != item) {
	//				String category = item.getCategory();
	//				String group = item.getGroupName();
	//				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
	//					// Add the value and volume of the stack to the global result.
	//					long quantity = asset.getQuantity();
	//					double price = asset.getItem().getHighestBuyerPrice().getPrice();
	//					assetValueISK = price * quantity;
	//				}
	//			}
	//		}
	//		return assetValueISK;
	//	}
	//
	//	/**
	//	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information. <br>
	//	 * This method checks the location to detect if under the new flat model the location is an asset and then
	//	 * we should convert it into a parent or the location is a real location. Initially this is done checking
	//	 * the location id value if under 1000000000000.
	//	 * 
	//	 * @param eveAsset
	//	 *          the original assest as downloaded from CCP api
	//	 * @return
	//	 */
	//	protected NeoComAsset convert2Asset(final Asset eveAsset) {
	//		// Create the asset from the API asset.
	//		final NeoComAsset newAsset = new NeoComAsset();
	//		newAsset.setAssetID(eveAsset.getItemID());
	//		newAsset.setTypeID(eveAsset.getTypeID());
	//		//		// Children locations have a null on this field. Set it to their parents
	//		//		final Long assetloc = eveAsset.getLocationID();
	//		//		if (null != assetloc) {
	//		//			newAsset.setLocationID(assetloc.longValue());
	//		//			//	}else {
	//		//			//		newAsset.setLocationID(
	//		//		}
	//		// Under the flat api check if the location is a real location or an asset.
	//		Long locid = eveAsset.getLocationID();
	//		if (locid > 1000000000000L) {
	//			// This is an asset so it represents the parent. We have not the location since the parent may not exist.
	//			newAsset.setLocationID(-1);
	//			newAsset.setParentId(locid);
	//		} else {
	//			// The location is a real location.
	//			newAsset.setLocationID(locid);
	//		}
	//
	//		newAsset.setQuantity(eveAsset.getQuantity());
	//		newAsset.setFlag(eveAsset.getFlag());
	//		newAsset.setSingleton(eveAsset.getSingleton());
	//
	//		// Get access to the Item and update the copied fields.
	//		final EveItem item = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(newAsset.getTypeID());
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
	//
	//	protected NeoComBlueprint convert2Blueprint(final Blueprint eveBlue) {
	//		// Create the asset from the API asset.
	//		final NeoComBlueprint newBlueprint = new NeoComBlueprint(eveBlue.getItemID());
	//		newBlueprint.setTypeID(eveBlue.getTypeID());
	//		newBlueprint.setTypeName(eveBlue.getTypeName());
	//		newBlueprint.setLocationID(eveBlue.getLocationID());
	//		newBlueprint.setFlag(eveBlue.getFlagID());
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

	//	/**
	//	 * Processes an asset and all their children. This method converts from a API record to a database asset
	//	 * record.
	//	 * 
	//	 * @param eveAsset
	//	 */
	//	protected void processAsset(final Asset eveAsset, final NeoComAsset parent) {
	//		final NeoComAsset myasset = this.convert2Asset(eveAsset);
	//		if (null != parent) {
	//			myasset.setParent(parent);
	//			myasset.setParentContainer(parent);
	//			// Set the location to the parent's location is not set.
	//			if (myasset.getLocationID() == -1) {
	//				myasset.setLocationID(parent.getLocationID());
	//			}
	//		}
	//		// Only search names for containers and ships.
	//		if (myasset.isShip()) {
	//			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
	//		}
	//		if (myasset.isContainer()) {
	//			myasset.setUserLabel(this.downloadAssetEveName(myasset.getAssetID()));
	//		}
	//		try {
	//			final Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
	//			final HashSet<Asset> children = new HashSet<Asset>(eveAsset.getAssets());
	//			if (children.size() > 0) {
	//				myasset.setContainer(true);
	//			}
	//			if (myasset.getCategory().equalsIgnoreCase("Ship")) {
	//				myasset.setShip(true);
	//			}
	//			assetDao.create(myasset);
	//
	//			// Process all the children and convert them to assets.
	//			if (children.size() > 0) {
	//				for (final Asset childAsset : children) {
	//					this.processAsset(childAsset, myasset);
	//				}
	//			}
	//			NeoComCharacter.logger.info("-- Wrote asset to database id [" + myasset.getAssetID() + "]");
	//			//			NeoComCharacter.logger.info("-- [NeoComCharacter.processAsset]> asset: " + myasset);
	//		} catch (final SQLException sqle) {
	//			NeoComCharacter.logger
	//					.severe("E> Unable to create the new asset [" + myasset.getAssetID() + "]. " + sqle.getMessage());
	//			sqle.printStackTrace();
	//		}
	//	}

	//	protected Job convert2Job(final IndustryJob evejob) {
	//		// Create the asset from the API asset.
	//		final Job newJob = new Job(evejob.getJobID());
	//		try {
	//			newJob.setOwnerID(evejob.getInstallerID());
	//			newJob.setFacilityID(evejob.getFacilityID());
	//			newJob.setStationID(evejob.getStationID());
	//			newJob.setActivityID(evejob.getActivityID());
	//			newJob.setBlueprintID(evejob.getBlueprintID());
	//			newJob.setBlueprintTypeID(evejob.getBlueprintTypeID());
	//			newJob.setBlueprintLocationID(evejob.getBlueprintLocationID());
	//			newJob.setRuns(evejob.getRuns());
	//			newJob.setCost(evejob.getCost());
	//			newJob.setLicensedRuns(evejob.getLicensedRuns());
	//			newJob.setProductTypeID(evejob.getProductTypeID());
	//			newJob.setStatus(evejob.getStatus());
	//			newJob.setTimeInSeconds(evejob.getTimeInSeconds());
	//			newJob.setStartDate(evejob.getStartDate());
	//			newJob.setEndDate(evejob.getEndDate());
	//			newJob.setCompletedDate(evejob.getCompletedDate());
	//			newJob.setCompletedCharacterID(evejob.getCompletedCharacterID());
	//			//			newJob.setSuccessfulRuns(evejob.getSuccessfulRuns());
	//		} catch (final RuntimeException rtex) {
	//			rtex.printStackTrace();
	//		}
	//		return newJob;
	//	}
	//
	//	protected NeoComMarketOrder convert2Order(final MarketOrder eveorder) {
	//		// Create the asset from the API asset.
	//		final NeoComMarketOrder newMarketOrder = new NeoComMarketOrder(eveorder.getOrderID());
	//		try {
	//			newMarketOrder.setOwnerID(eveorder.getCharID());
	//			newMarketOrder.setStationID(eveorder.getStationID());
	//			newMarketOrder.setVolEntered(eveorder.getVolEntered());
	//			newMarketOrder.setVolRemaining(eveorder.getVolRemaining());
	//			newMarketOrder.setMinVolume(eveorder.getMinVolume());
	//			newMarketOrder.setOrderState(eveorder.getOrderState());
	//			newMarketOrder.setTypeID(eveorder.getTypeID());
	//			newMarketOrder.setRange(eveorder.getRange());
	//			newMarketOrder.setAccountKey(eveorder.getAccountKey());
	//			newMarketOrder.setDuration(eveorder.getDuration());
	//			newMarketOrder.setEscrow(eveorder.getEscrow());
	//			newMarketOrder.setPrice(eveorder.getPrice());
	//			newMarketOrder.setBid(eveorder.getBid());
	//			newMarketOrder.setIssuedDate(eveorder.getIssued());
	//		} catch (final RuntimeException rtex) {
	//			rtex.printStackTrace();
	//		}
	//		return newMarketOrder;
	//	}

	//	protected String downloadAssetEveName(final long assetID) {
	//		// Wait up to one second to avoid request rejections from CCP.
	//		try {
	//			Thread.sleep(500); // 500 milliseconds is half second.
	//		} catch (InterruptedException ex) {
	//			Thread.currentThread().interrupt();
	//		}
	//		final Vector<Long> ids = new Vector<Long>();
	//		ids.add(assetID);
	//		try {
	//			final LocationsParser parser = new LocationsParser();
	//			final LocationsResponse response = parser.getResponse(this.getAuthorization(), ids);
	//			if (null != response) {
	//				Set<Location> userNames = response.getAll();
	//				if (userNames.size() > 0) return userNames.iterator().next().getItemName();
	//			}
	//		} catch (final ApiException e) {
	//			NeoComCharacter.logger.info("W- EveChar.downloadAssetEveName - asset has no user name defined: " + assetID);
	//			//			e.printStackTrace();
	//		}
	//		return null;
	//	}

	//	protected ArrayList<NeoComAsset> searchAsset4Category(final long characterID, final String category) {
	//		//	Select assets for the owner and with an specific type id.
	//		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
	//		try {
	//			Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
	//			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
	//			Where<NeoComAsset, String> where = queryBuilder.where();
	//			where.eq("ownerID", characterID);
	//			where.and();
	//			where.eq("category", category);
	//			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
	//			assetList = assetDao.query(preparedQuery);
	//		} catch (java.sql.SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		return (ArrayList<NeoComAsset>) assetList;
	//
	//	}
	//
	//	protected ArrayList<Job> searchIndustryJobs() {
	//		NeoComCharacter.logger.info(">> EveChar.searchIndustryJobs");
	//		//	Select assets of type blueprint and that are of T2.
	//		List<Job> jobList = new ArrayList<Job>();
	//		try {
	//			ModelAppConnector.getSingleton().startChrono();
	//			final Dao<Job, String> jobDao = ModelAppConnector.getSingleton().getDBConnector().getJobDAO();
	//			final QueryBuilder<Job, String> qb = jobDao.queryBuilder();
	//			qb.where().eq("ownerID", this.getCharacterID());
	//			qb.orderBy("endDate", false);
	//			jobList = jobDao.query(qb.prepare());
	//			this.checkRefresh(jobList.size(), "JOBS");
	//			final Duration lapse = ModelAppConnector.getSingleton().timeLapse();
	//			NeoComCharacter.logger.info("-- Time lapse for [SELECT JOBS] " + lapse);
	//		} catch (final SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		return (ArrayList<Job>) jobList;
	//	}

	protected void setApiKey (final NeoComApiKey apikey) {
		this.apikey = apikey;
	}

	protected void setDelegatedCharacter (final Character coreChar) {
		delegatedCharacter = coreChar;
	}


	//	/**
	//	 * Checks if the database is empty of a set of records so it may require a forced request to update their
	//	 * data from CCP databases. If no records then it will reset the cache timers.
	//	 * 
	//	 * @param size
	//	 * @param string
	//	 */
	//	private void checkRefresh(final int size, final String section) {
	//		if (size < 1) {
	//			if (section.equalsIgnoreCase("JOBS")) {
	//				//				this.cleanJobs();
	//				ModelAppConnector.getSingleton().getCacheConnector().addCharacterUpdateRequest(this.getCharacterID());
	//			}
	//			if (section.equalsIgnoreCase("MARKETORDERS")) {
	//				ModelAppConnector.getSingleton().getCacheConnector().addCharacterUpdateRequest(this.getCharacterID());
	//			}
	//		}
	//	}
	//
	//	private void clearTimers() {
	//		lastCCPAccessTime = null;
	//		assetsCacheTime = null;
	//		blueprintsCacheTime = null;
	//		jobsCacheTime = null;
	//		marketCacheTime = null;
	//		//		skillsCacheTime = null;
	//	}

	//	private ArrayList<NeoComAsset> filterAssets4Name(final String moduleName) {
	//		///	Optimize the update of the assets to just process the ones with the -1 owner.
	//		List<NeoComAsset> accountList = new ArrayList<NeoComAsset>();
	//		try {
	//			final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
	//			final QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
	//			final Where<NeoComAsset, String> where = queryBuilder.where();
	//			where.eq("name", moduleName);
	//			//			where.and();
	//			//			where.gt("count", new Integer(9));
	//			final PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
	//			accountList = assetDao.query(preparedQuery);
	//		} catch (final SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return (ArrayList<NeoComAsset>) accountList;
	//	}

	//	private Instant getAssetsCacheTime() {
	//		if (null == assetsCacheTime) {
	//			assetsCacheTime = new Instant().now();
	//		}
	//		return assetsCacheTime;
	//	}
}

// - UNUSED CODE ............................................................................................
// [01]
//	public void addLocationRole(final EveLocation theSelectedLocation, final String locationrole) {
//		if (null == locationRoles) {
//			this.accessLocationRoles();
//		}
//		if (locationRoles.size() < 1) {
//			this.accessLocationRoles();
//		}
//		Property hit = new Property(EPropertyTypes.LOCATIONROLE);
//		hit.setOwnerID(delegatedCharacter.getCharacterID());
//		hit.setNumericValue(theSelectedLocation.getID());
//		hit.setStringValue(locationrole);
//		locationRoles.add(hit);
//	}
//	/**
//	 * Removes the records that define the association of roles to the selected location. This clears all the
//	 * roles for a location and if the user only wants to clear one he/she has to activate the others again
//	 * since all get removed.
//	 *
//	 * @param theSelectedLocation
//	 */
//	public void clearLocationRoles(final EveLocation theSelectedLocation) {
//		if (null == locationRoles) {
//			this.accessLocationRoles();
//		}
//		for (Property role : locationRoles)
//			if (role.getNumericValue() == Double.valueOf(theSelectedLocation.getID())) {
//				//		if (null != hit) {
//				try {
//					Dao<Property, String> propertyDao = ModelAppConnector.getSingleton().getDBConnector().getPropertyDAO();
//					propertyDao.delete(role);
//					locationRoles = null;
//				} catch (final SQLException sqle) {
//					sqle.printStackTrace();
//				}
//			}
//	}
//	public HashMap<Long, Property> getActions() {
//		if (null == actions4Character) {
//			this.accessActionList();
//		}
//		return actions4Character;
//	}
//	/**
//	 * Returns the first location that matches the specified role. This is confusing because more that one
//	 * location can match that role but this is a first approach.
//	 *
//	 * @param matchingRole
//	 * @return
//	 */
//	@JsonIgnore
//	public EveLocation getLocation4Role(final String matchingRole) {
//		if (null == locationRoles) {
//			this.accessLocationRoles();
//		}
//		for (Property role : locationRoles) {
//			String value = role.getPropertyType().toString();
//			if (role.getPropertyType().toString().equalsIgnoreCase(matchingRole))
//				return ModelAppConnector.getSingleton().getCCPDBConnector()
//						.searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
//			//		Property currentRole = locationRoles.get(locID);
//			//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
//			//				return AppConnector.getDBConnector().searchLocationbyID(locID);
//		}
//		return null;
//	}
//	/**
//	 * Return the first location that matches that role at the specified Region.
//	 *
//	 * @param matchingRole
//	 * @param region
//	 * @return
//	 */
//	@JsonIgnore
//	public EveLocation getLocation4Role(final String matchingRole, final String region) {
//		//		EveLocation preferredLocation = null;
//		for (Property role : locationRoles)
//			if (role.getPropertyType().toString().equalsIgnoreCase(matchingRole)) {
//				EveLocation target = ModelAppConnector.getSingleton().getCCPDBConnector()
//						.searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
//				if (target.getRegion().equalsIgnoreCase(region)) return target;
//				//		Property currentRole = locationRoles.get(locID);
//				//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
//				//				return AppConnector.getDBConnector().searchLocationbyID(locID);
//			}
//		return null;
//	}
//
//	/**
//	 * From the list of roles set to location, extract all the roles that match the location ID.
//	 *
//	 * @param targetLocationID
//	 * @param defaultValue
//	 * @return
//	 * @return
//	 */
//	@JsonIgnore
//	public ArrayList<Property> getLocationRoles(final long targetLocationID, final String defaultValue) {
//		ArrayList<Property> roles = new ArrayList<Property>();
//		if (null == locationRoles) {
//			this.accessLocationRoles();
//		}
//		for (Property role : locationRoles)
//			if (role.getNumericValue() == Double.valueOf(targetLocationID)) {
//				roles.add(role);
//			}
//		//		Property hit = locationRoles.get(targetLocationID);
//		//		if (null == hit)
//		//			return defaultValue;
//		//		else
//		//			return hit.getStringValue();
//		return roles;
//	}
//	/**
//	 * Stores as a register the user preferred action to perform with this item when there is a shortage while
//	 * manufacturing. First get the current action defined for this item on the database and update it or create
//	 * a new one of not found.
//	 *
//	 * @param typeID
//	 * @param taskName
//	 */
//	public void putAction4Item(final int typeID, final String taskName) {
//		if (null == actions4Character) {
//			this.accessActionList();
//		}
//		Property hit = actions4Character.get(typeID);
//		if (null == hit) {
//			hit = new Property(EPropertyTypes.MANUFACTUREACTION);
//			hit.setOwnerID(delegatedCharacter.getCharacterID());
//			//			hit.setPropertyType();
//			hit.setNumericValue(typeID);
//			hit.setStringValue(taskName);
//			actions4Character = null;
//		}
//		hit.setStringValue(taskName);
//	}
//	private void accessActionList() {
//		List<Property> actionList = new ArrayList<Property>();
//		try {
//			Dao<Property, String> propertyDao = ModelAppConnector.getSingleton().getDBConnector().getPropertyDAO();
//			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
//			Where<Property, String> where = queryBuilder.where();
//			where.eq("ownerID", delegatedCharacter.getCharacterID());
//			where.and();
//			where.eq("propertyType", EPropertyTypes.MANUFACTUREACTION.toString());
//			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
//			actionList = propertyDao.query(preparedQuery);
//		} catch (java.sql.SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		// Process the returned list and store in the character.
//		actions4Character = new HashMap<Long, Property>();
//		for (Property property : actionList) {
//			// The type selected for the action is stored as the property key.
//			actions4Character.put(Double.valueOf(property.getNumericValue()).longValue(), property);
//		}
//	}
//
//	private void accessLocationRoles() {
//		//		List<Property> roleList = new ArrayList<Property>();
//		try {
//			Dao<Property, String> propertyDao = ModelAppConnector.getSingleton().getDBConnector().getPropertyDAO();
//			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
//			Where<Property, String> where = queryBuilder.where();
//			where.eq("ownerID", delegatedCharacter.getCharacterID());
//			where.and();
//			where.eq("propertyType", EPropertyTypes.LOCATIONROLE.toString());
//			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
//			locationRoles = new ArrayList<Property>(propertyDao.query(preparedQuery));
//		} catch (java.sql.SQLException sqle) {
//			sqle.printStackTrace();
//		}
//	}

