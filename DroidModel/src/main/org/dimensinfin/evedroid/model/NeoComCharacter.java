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
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.core.INeoComNode;
import org.dimensinfin.evedroid.enums.EPropertyTypes;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.pilot.SkillQueueItem;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.corporation.AccountBalanceParser;
import com.beimin.eveapi.parser.pilot.CharacterSheetParser;
import com.beimin.eveapi.parser.pilot.SkillInTrainingParser;
import com.beimin.eveapi.parser.pilot.SkillQueueParser;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.beimin.eveapi.response.pilot.CharacterSheetResponse;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;
import com.beimin.eveapi.response.pilot.SkillQueueResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComCharacter extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComCharacter");

	public static NeoComCharacter build(Character coreChar, NeoComApiKey apikey) throws ApiException {
		// The api to use depends on the type of character.
		if (apikey.getType() == KeyType.Character) return createPilot(coreChar, apikey);
		if (apikey.getType() == KeyType.Corporation) return createCorporation(coreChar, apikey);
		return createPilot(coreChar, apikey);
	}

	private static NeoComPilot createPilot(Character coreChar, NeoComApiKey apikey) throws ApiException {
		NeoComPilot newchar = new NeoComPilot();
		newchar.setApiKey(apikey);
		newchar.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		AccountBalanceParser balanceparser = new AccountBalanceParser();
		AccountBalanceResponse balanceresponse = balanceparser.getResponse(apikey.getAuthorization());
		if (null != balanceresponse) {
			Set<EveAccountBalance> balance = balanceresponse.getAll();
			if (balance.size() > 0) newchar.setAccountBalance(balance.iterator().next().getBalance());
		}
		// Character sheet information
		CharacterSheetParser sheetparser = new CharacterSheetParser();
				CharacterSheetResponse sheetresponse=sheetparser.getResponse(apikey.getAuthorization());
				if (null != sheetresponse) {
					newchar.setCharacterSheet( sheetresponse);
				}
		// Skill list
		SkillQueueParser skillparser = new SkillQueueParser();
		SkillQueueResponse skillresponse = skillparser.getResponse(apikey.getAuthorization());
		if (null != skillresponse) {
			newchar.setSkillQueue( skillresponse.getAll());
		}
		// Skill in training
		 SkillInTrainingParser trainingparser = new SkillInTrainingParser();
		 SkillInTrainingResponse trainingresponse = trainingparser.getResponse(apikey.getAuthorization());
		if (null != skillresponse) {
			newchar.setSkillInTraining( trainingresponse);
		}
		return newchar;
	}

	private static NeoComCorporation createCorporation(Character coreChar, NeoComApiKey apikey) throws ApiException{
		NeoComCorporation newcorp = new NeoComCorporation();
		newcorp.setDelegatedCharacter(coreChar);
		// Go to the API and get more information for this character.
		// Balance information
		AccountBalanceParser balanceparser = new AccountBalanceParser();
		AccountBalanceResponse balanceresponse = balanceparser.getResponse(apikey.getAuthorization());
		if (null != balanceresponse) {
			Set<EveAccountBalance> balance = balanceresponse.getAll();
			if (balance.size() > 0) newcorp.setAccountBalance(balance.iterator().next().getBalance());
		}
		return newcorp;
	}

	// - F I E L D - S E C T I O N ............................................................................
	/** Reference to the delegated core eveapi Character */
	private NeoComApiKey					apikey							= null;
	private Character							delegatedCharacter	= null;
	private CharacterInfoResponse	characterInfo				= null;
	private double								accountBalance			= 0.0;
	// - U S E R   K E Y E D   D A T A
	private transient ArrayList<Property>			locationRoles				= null;
	private transient HashMap<Long, Property>	actions4Character		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComCharacter() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(String variant) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return delegatedCharacter.getName();
	}

	public long getCharacterID() {
		return delegatedCharacter.getCharacterID();
	}

	protected void setApiKey(NeoComApiKey apikey) {
		this.apikey = apikey;
	}

	public double getAccountBalance() {
		return accountBalance;
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
	public HashMap<Long, Property> getActions() {
		if (null == actions4Character) accessActionList();
		return actions4Character;
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
			hit.setOwnerID(delegatedCharacter.getCharacterID());
			//			hit.setPropertyType();
			hit.setNumericValue(typeID);
			hit.setStringValue(taskName);
			actions4Character = null;
		}
		hit.setStringValue(taskName);
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

	public void setAccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
	}
	public void addLocationRole(final EveLocation theSelectedLocation, final String locationrole) {
		if (null == locationRoles) accessLocationRoles();
		if (locationRoles.size() < 1) accessLocationRoles();
		Property hit = new Property(EPropertyTypes.LOCATIONROLE);
		hit.setOwnerID(delegatedCharacter.getCharacterID());
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

	protected void setDelegatedCharacter(Character coreChar) {
		delegatedCharacter = coreChar;
	}

}

// - UNUSED CODE ............................................................................................
