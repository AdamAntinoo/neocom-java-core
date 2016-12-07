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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.core.INeoComNode;
import org.dimensinfin.evedroid.enums.EPropertyTypes;
import org.dimensinfin.evedroid.industry.Resource;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

/**
 * This class encapsulates the eveapi Character into a NeoCom data structure that will allow me to use the
 * original character as a delegate to use eveapi code for all CCP interactions. <br>
 * Fields not declared on the core Character will be added through more delegates or coded into this class
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public abstract class EveCharCore extends AbstractNeoComNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID		= 7187291497544861371L;
	protected static Logger										logger							= Logger.getLogger("EveChar");
	protected static String										CHAR_CHARACTERSHEET	= "https://api.eveonline.com/char/CharacterSheet.xml.aspx";
	protected static String										EVE_CHARACTERINFO		= "https://api.eveonline.com/eve/CharacterInfo.xml.aspx";

	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the delegated core eveapi Character */
	private Character													coreCharacter				= null;
	private CharacterInfoResponse							characterInfo				= null;
	//	/**
	//	 * Flag that shows if this character is active or not for user interaction. Can be changed from UI.
	//	 */
	//	private final boolean											active							= true;
	//
	//	// - C C P   A U T H E N T I C A T I O N
	//	protected transient Instant								lastCCPAccessTime		= new Instant(0);
	//	protected int															keyID								= 0;
	//	protected String													verificationCode		= null;
	//	protected long														characterID					= 0;

	// - P R O P E R T I E S
	//	private String														name								= "<undefined>";
	private double														accountBalance			= 0.0;
	private String														shipName						= "<undefined>";
	private String														shipTypeName				= "<undefined>";
	private String														lastKnownLocation		= "Space";
	//	private String														corporationName			= "<undefined>";
	private String														race								= "<NO RACE>";
	private String														cloneName						= "Clone Grade Alpha";
	//	private final long												corporationID				= 0;

	// - D E P E N D A N T   P R O P E R T I E S
	private boolean														hasBlueprints				= false;

	// - U S E R   K E Y E D   D A T A
	private transient ArrayList<Property>			locationRoles				= null;
	private transient HashMap<Long, Property>	actions4Character		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveCharCore(final Integer key, final String validation, final long characterID) {
		// Initialize the char with the locator API parameters
		keyID = key;
		verificationCode = validation;
		this.characterID = characterID;
		coreCharacter.setCharacterID(characterID);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addLocationRole(final EveLocation theSelectedLocation, final String locationrole) {
		if (null == locationRoles) accessLocationRoles();
		if (locationRoles.size() < 1) accessLocationRoles();
		Property hit = new Property(EPropertyTypes.LOCATIONROLE);
		hit.setOwnerID(getCharacterID());
		hit.setNumericValue(theSelectedLocation.getID());
		hit.setStringValue(locationrole);
		locationRoles.add(hit);
	}

	/**
	 * Removes the records that define the association of roles to the selected location. This clears all the
	 * roles for a location and if the user only wants to clear one he/she has to activate the others again
	 * since all get removed.
	 * 
	 * @param theSelectedLocation
	 */
	public void clearLocationRoles(final EveLocation theSelectedLocation) {
		if (null == locationRoles) accessLocationRoles();
		for (Property role : locationRoles) {
			if (role.getNumericValue() == Double.valueOf(theSelectedLocation.getID())) {
				//		Property hit = locationRoles.get(theSelectedLocation.getID());
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
	}

	public HashMap<Long, Property> getActions() {
		if (null == actions4Character) accessActionList();
		return actions4Character;
	}

	public double getBalance() {
		return accountBalance;
	}

	/**
	 * Return the list of Blueprints Originals on the asset list of the character.<br>
	 * Requires the existence of the list of blueprints and the reload should not be called because this
	 * information clearly depends on the update of the character's assets and may be calculated after the asset
	 * upload.
	 * 
	 * @return
	 */
	public ArrayList<Asset> getBPO() {
		ArrayList<Asset> bps = searchBPOAssets(getCharacterID());
		if (bps.size() > 0) hasBlueprints = true;
		return bps;
	}

	@Override
	public long getCharacterID() {
		return characterID;
	}

	public String getCloneName() {
		return cloneName;
	}

	@Override
	public String getCorporationName() {
		return corporationName;
	}

	public int getKeyID() {
		return keyID;
	}

	public Instant getLastCCPAccess() {
		return lastCCPAccessTime;
	}

	public String getLastKnownLocation() {
		return lastKnownLocation;
	}

	/**
	 * Returns the first location that matches the specified role. This is confusing because more that one
	 * location can match that role but this is a first approach.
	 * 
	 * @param matchingRole
	 * @return
	 */
	public EveLocation getLocation4Role(final String matchingRole) {
		if (null == locationRoles) accessLocationRoles();
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
	public EveLocation getLocation4Role(final String matchingRole, String region) {
		//		EveLocation preferredLocation = null;
		for (Property role : locationRoles) {
			if (role.getPropertyType().toString().equalsIgnoreCase(matchingRole)) {
				EveLocation target = AppConnector.getDBConnector()
						.searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
				if (target.getRegion().equalsIgnoreCase(region)) return target;
				//		Property currentRole = locationRoles.get(locID);
				//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
				//				return AppConnector.getDBConnector().searchLocationbyID(locID);
			}
		}
		return null;
	}

	//	/**
	//	 * Gets the location or locations with that function from the preferences list. There can be more than one
	//	 * location with the same function but no more that one on a single region. We have to implement region
	//	 * filtering to get the right one or return any other alternative if not defined that function on that
	//	 * region.
	//	 * 
	//	 * @param string
	//	 * @return
	//	 */
	//	public EveLocation getLocationRole(final String function, final String region) {
	//		EveLocation preferredLocation = null;
	//		for (long locID : locationRoles.keySet()) {
	//			Property currentRole = locationRoles.get(locID);
	//			if (function.equalsIgnoreCase(currentRole.getStringValue())) {
	//				if (null == preferredLocation) {
	//					preferredLocation = AppConnector.getDBConnector().searchLocationbyID(locID);
	//					if (preferredLocation.getRegion().equalsIgnoreCase(region)) return preferredLocation;
	//				} else {
	//					EveLocation alternateLocation = AppConnector.getDBConnector().searchLocationbyID(locID);
	//					if (alternateLocation.getRegion().equalsIgnoreCase(region)) return alternateLocation;
	//				}
	//			}
	//		}
	//		return preferredLocation;
	//	}

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
		if (null == locationRoles) accessLocationRoles();
		for (Property role : locationRoles) {
			if (role.getNumericValue() == Double.valueOf(targetLocationID)) roles.add(role);
			//		Property hit = locationRoles.get(targetLocationID);
			//		if (null == hit)
			//			return defaultValue;
			//		else
			//			return hit.getStringValue();
		}
		return roles;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getRace() {
		return race;
	}

	public String getShipName() {
		return shipName;
	}

	//	public SkillInTrainingResponse getSkills() {
	//		return skillInTraining;
	//	}

	//	public ArrayList<Asset> getT2Modules() {
	//		logger.info(">> EveChar.getT2Modules");
	//		//	Select assets of type blueprint and that are of T2.
	//		List<Asset> assetList = new ArrayList<Asset>();
	//		try {
	//			AppConnector.startChrono();
	//			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
	//			Dao<EveItem, String> itemDao = AppConnector.getDBConnector().getItemDAO();
	//
	//			QueryBuilder<EveItem, String> itemQuery = itemDao.queryBuilder();
	//			itemQuery.where().eq("tech", "Tech II");
	//			QueryBuilder<Asset, String> assetQuery = assetDao.queryBuilder();
	//			assetQuery.where().eq("ownerID", getCharacterID()).and().eq("category", "Module");
	//			assetList = assetQuery.join(itemQuery).query();
	//			Duration lapse = AppConnector.timeLapse();
	//			logger.info("-- Time lapse for [SELECT CATEGORY=MODULE AND TECH=TECH II AND OWNERID=" + getCharacterID() + "] "
	//					+ lapse);
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		}
	//		logger.info("<< EveChar.getT2Modules");
	//		return (ArrayList<Asset>) assetList;
	//	}

	public ArrayList<Asset> getShips() {
		return searchAsset4Category(getCharacterID(), "Ship");
	}

	public String getShipTypeName() {
		return shipTypeName;
	}

	public String getURLForAvatar() {
		return "http://image.eveonline.com/character/" + characterID + "_256.jpg";
	}

	public boolean hasBPOs() {
		return hasBlueprints;
	}

	public boolean isActive() {
		return active;
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
		if (null == actions4Character) accessActionList();
		Property hit = actions4Character.get(typeID);
		if (null == hit) {
			hit = new Property(EPropertyTypes.MANUFACTUREACTION);
			hit.setOwnerID(getCharacterID());
			//			hit.setPropertyType();
			hit.setNumericValue(typeID);
			hit.setStringValue(taskName);
			actions4Character = null;
		}
		hit.setStringValue(taskName);
	}

	public void setBalance(final double newBalance) {
		accountBalance = newBalance;
	}

	public void setBalance(final String newBalance) {
		// Convert the balance to a number
		setBalance(Double.valueOf(newBalance).doubleValue());
	}

	public void setCloneName(final String cloneName) {
		this.cloneName = cloneName;
	}

	@Override
	public void setCorporationName(final String corporationName) {
		this.corporationName = corporationName;
	}

	//	public void setCloneSkillPoints(final int cloneSkillPoints) {
	//		this.cloneSkillPoints = cloneSkillPoints;
	//	}

	@Override
	public void setDirty(final boolean dirtyState) {
		// If dirty then write down the assets to a file. But only when the assets have been changed.
		super.setDirty(dirtyState);
	}

	//	public void setCloneSkillPoints(final String points) {
	//		// Convert the parameter to a number
	//		setCloneSkillPoints(Integer.valueOf(points).intValue());
	//	}
	public void setLastKnownLocation(final String lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	public void setRace(final String race) {
		this.race = race;
	}

	public void setShipName(final String shipName) {
		this.shipName = shipName;
	}

	public void setShipTypeName(final String shipTypeName) {
		this.shipTypeName = shipTypeName;
	}

	@Override
	public String toString() {
		final int assetsNro = 0;
		//		if (null != assets) assetsNro = assets.size();
		final StringBuffer buffer = new StringBuffer("EveChar [");
		buffer.append("id: ").append(characterID).append(" ");
		buffer.append("active: ").append(active).append(" ");
		buffer.append("name: ").append(name).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * At the Character creation we only have the key values to locate it into the CCP databases. During this
	 * execution we have to download many different info from many CCP API calls so it will take some time.<br>
	 * After this update we will have access to all the direct properties of a character. Other multiple value
	 * properties like assets or derived lists will be updated when needed by using other update calls.
	 */
	public synchronized void updateCharacterInfo() {
		// TODO Verify that the data is stale before attempting to read it again.
		if (AppConnector.checkExpiration(lastCCPAccessTime, ModelWideConstants.HOURS1)) {
			downloadEveCharacterInfo();
			downloadCharacterSheet();
			//			downloadSkillTraining();
			setDirty(true);
		}
	}

	private void accessActionList() {
		List<Property> actionList = new ArrayList<Property>();
		try {
			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
			Where<Property, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
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
			where.eq("ownerID", characterID);
			where.and();
			where.eq("propertyType", EPropertyTypes.LOCATIONROLE.toString());
			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
			locationRoles = new ArrayList(propertyDao.query(preparedQuery));
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	private synchronized void downloadCharacterSheet() {
		logger.info(">> EveChar.downloadCharacterSheet");
		final String eveCharacterInfoCall = CHAR_CHARACTERSHEET + "?keyID=" + keyID + "&vCode=" + verificationCode
				+ "&characterID=" + characterID;
		Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
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
		logger.info("<< EveChar.downloadCharacterSheet");
	}

	private synchronized void downloadEveCharacterInfo() {
		logger.info(">> EveChar.downloadEveCharacterInfo");
		final String eveCharacterInfoCall = EVE_CHARACTERINFO + "?keyID=" + keyID + "&vCode=" + verificationCode
				+ "&characterID=" + characterID;
		Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
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
		logger.info("<< EveChar.downloadEveCharacterInfo");
	}

	private String extractRootModuleName(final String inname) {
		String partial = inname.replaceAll("Blueprint(\\s*\\S*)", "");
		partial = partial.trim();
		return partial;
	}

	private ArrayList<Asset> filterAssets4Name(final String moduleName) {
		///	Optimize the update of the assets to just process the ones with the -1 owner.
		List<Asset> accountList = new ArrayList<Asset>();
		try {
			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("name", moduleName);
			//			where.and();
			//			where.gt("count", new Integer(9));
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			accountList = assetDao.query(preparedQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Asset>) accountList;
	}

	//		private void downloadSkillTraining() {
	//			final SkillInTrainingParser parser = SkillInTrainingParser.getInstance();
	//			final ApiAuthorization auth = new ApiAuthorization(keyID, characterID, verificationCode);
	//			final SkillInTrainingResponse response;
	//			try {
	//				skillInTraining = parser.getResponse(auth);
	//			} catch (final ApiException e) {
	//				e.printStackTrace();
	//			}
	//		}

	/**
	 * Searches for an specified resource at a location.
	 * 
	 * @param itemID
	 * @param storageLocation
	 * @return
	 */
	private long getAvailableAtLocation(final int itemID, final EveLocation location) {
		final ArrayList<Asset> availableAssets = getAvailableResources(location);
		for (Asset asset : availableAssets) {
			if (itemID == asset.getTypeID()) return asset.getQuantity();
		}
		return 0;
	}

	private ArrayList<Asset> getAvailableResources(final EveLocation targetLocation) {
		///	Optimize the update of the assets to just process the ones with the -1 owner.
		List<Asset> resources = new ArrayList<Asset>();
		try {
			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("locationID", new Long(targetLocation.getID()).toString());
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			resources = assetDao.query(preparedQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Asset>) resources;
	}

	private int getJobSlotsProperty() {
		// TODO We have to implement this operation.
		return 9;
	}

	private Resource search4Resource(final Resource resource, final ArrayList<Asset> availableAssets) {
		final Resource target = new Resource(resource.getItem().getItemID(), 0);
		final Iterator<Asset> ait = availableAssets.iterator();
		while (ait.hasNext()) {
			final Asset asset = ait.next();
			//			int targetType = target.getTypeID();
			final int resourceType = resource.getTypeID();
			final int assetType = asset.getTypeID();
			if (asset.getTypeID() == resource.getTypeID()) {
				target.add(asset.getQuantity());
			}
		}
		return target;
	}

	private ArrayList<Asset> searchAsset4Category(final long characterID, final String category) {
		//	Select assets for the owner and with an specific type id.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("category", category);
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Asset>) assetList;

	}

	private ArrayList<Asset> searchBPCT2(final long characterID) {
		logger.info(">> Entering EveChar.searchBPCT2");
		//	Select assets of type blueprint and that are of T2.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			AppConnector.startChrono();
			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", getCharacterID());
			where.and();
			where.eq("category", "Blueprint");
			where.and();
			where.like("name", "%II Blueprint%");
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
			Duration lapse = AppConnector.timeLapse();
			logger.info("-- Time lapse for [SELECT CATEGORY=BLUEPRINT NAME LIKE II Blueprint OWNERID = " + getCharacterID()
					+ "] " + lapse);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Asset>) assetList;
	}

	private ArrayList<Asset> searchBPOAssets(final long characterID) {
		logger.info(">> Entering EveChar.searchBPCT2");
		//	Select assets of type blueprint and that are of T2.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			AppConnector.startChrono();
			Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", getCharacterID());
			where.and();
			where.eq("category", "Blueprint");
			where.and();
			where.like("name", "%BPO%");
			PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
			Duration lapse = AppConnector.timeLapse();
			logger.info(
					"-- Time lapse for [SELECT CATEGORY=BLUEPRINT NAME LIKE %BPO% OWNERID = " + getCharacterID() + "] " + lapse);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Asset>) assetList;
	}

	private int sumStacks(final ArrayList<Asset> stacks) {
		final Iterator<Asset> sit = stacks.iterator();
		int total = 0;
		while (sit.hasNext()) {
			final Asset asset = sit.next();
			total += asset.getQuantity();
		}
		return total;
	}
}

// - UNUSED CODE ............................................................................................
