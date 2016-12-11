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

import java.sql.SQLException;
//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.joda.time.Instant;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.pilot.SkillQueueItem;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.parser.corporation.AssetListParser;
import com.beimin.eveapi.response.pilot.CharacterSheetResponse;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.j256.ormlite.dao.Dao;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComPilot extends NeoComCharacter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger											logger					= Logger.getLogger("NeoComPilot");

	// - F I E L D - S E C T I O N ............................................................................
	protected CharacterSheetResponse					characterSheet	= null;

	// - T R A N S I E N T   D A T A
	private transient Set<SkillQueueItem>			skills					= null;
	private transient SkillInTrainingResponse	skillInTraining	= null;
	private transient ArrayList<Job>					jobList					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComPilot() {
	}

	/**
	 * Returns the number of invention jobs that can be launched simultaneously. This will depend on the skills
	 * <code>Laboratory Operation</code> and <code>Advanced Laboratory Operation</code>.
	 * 
	 * @return
	 */
	public int calculateInventionQueues() {
		int queues = 1;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills) {
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
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills) {
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.MassProduction) {
				queues += apiSkill.getLevel();
			}
			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedMassProduction) {
				queues += apiSkill.getLevel();
			}
		}
		return queues;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the elements collaborated by this object. For a Character it depends on the implementation being a
	 * Pilot or a Corporation. For a Pilot the result depends on the variant received as the parameter
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		return results;
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
	public synchronized void downloadAssets() {
		logger.info(">> EveChar.updateAssets");
		try {
			// Clear any previous record with owner -1 from database.
			AppConnector.getDBConnector().clearInvalidRecords();
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
			AssetListResponse response = parser.getResponse(apikey.getAuthorization());
			if (null != response) {
				List<NeoComAsset> assets = response.getAll();
				assetsCacheTime = new Instant(response.getCachedUntil());
				// Assets may be parent of other assets so process them recursively.
				for (final NeoComAsset eveAsset : assets) {
					processAsset(eveAsset, null);
				}
			}
			//			}
			AppConnector.getDBConnector().replaceAssets(getCharacterID());

			// Update the caching time to the time set by the eveapi.
			assetsCacheTime = new Instant(response.getCachedUntil());
		} catch (

		final ApiException apie) {
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

	public int getSkillLevel(final int skillID) {
		// Corporation api will have all skills maxed.
		//		if (isCorporation()) return 5;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills)
			if (apiSkill.getTypeID() == skillID) return apiSkill.getLevel();
		return 0;
	}

	public void setCharacterSheet(CharacterSheetResponse sheet) {
		characterSheet = sheet;
	}

	public void setSkillInTraining(SkillInTrainingResponse training) {
		skillInTraining = training;
	}

	public void setSkillQueue(Set<SkillQueueItem> skilllist) {
		skills = skilllist;
	}

	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information.
	 * 
	 * @param eveAsset
	 * @return
	 */
	private NeoComAsset convert2Asset(final NeoComAsset eveAsset) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset();
		newAsset.setAssetID(eveAsset.getItemID());
		newAsset.setTypeID(eveAsset.getTypeID());
		// Children locations have a null on this field. Set it to their parents
		final Long assetloc = eveAsset.getLocationID();
		// DEBUG Add a conditional breakpoint for an specific asset.
		//		Long test = new Long(1021037093228L);
		if (eveAsset.getTypeID() == 8625) {
			@SuppressWarnings("unused")
			int i = 1; // stop point
		}
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

	/**
	 * Processes an asset and all their children. This method converts from a API record to a database asset
	 * record.
	 * 
	 * @param eveAsset
	 */
	private void processAsset(NeoComAsset eveAsset, final NeoComAsset parent) {
		final NeoComAsset myasset = convert2Asset(eveAsset);
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
			final Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
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

}

// - UNUSED CODE ............................................................................................
