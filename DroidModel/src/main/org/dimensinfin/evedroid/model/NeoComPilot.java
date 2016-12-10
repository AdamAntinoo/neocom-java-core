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

import java.util.ArrayList;
//- IMPORT SECTION .........................................................................................
import java.util.Set;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.manager.AssetsManager;

import com.beimin.eveapi.model.pilot.SkillQueueItem;
import com.beimin.eveapi.response.pilot.CharacterSheetResponse;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;

import android.support.v4.util.LongSparseArray;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComPilot extends NeoComCharacter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComPilot");

	// - F I E L D - S E C T I O N ............................................................................
	protected CharacterSheetResponse characterSheet = null;

	// - T R A N S I E N T   D A T A
	private transient Set<SkillQueueItem> skills=null;
	private transient SkillInTrainingResponse	skillInTraining			= null;
	private transient ArrayList<Job>					jobList							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComPilot() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the elements collaborated by this object. For a Character it depends on the implementation being a Pilot or
	 * a Corporation. For a Pilot the result depends on the variant received as the parameter
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		return results;
	}
	public void setSkillQueue(Set<SkillQueueItem> skilllist) {
		skills=skilllist;
	}

	public void setSkillInTraining(SkillInTrainingResponse training) {
		skillInTraining=training;
	}
	protected  void getAllAssets() {
		try {
			// Initialize the model
			dataModelRoot = new RootNode();
			regions = new LongSparseArray<Region>();
			locations = new LongSparseArray<EveLocation>();
			containers = new LongSparseArray<Asset>();
			// Read all the assets for this character is not done already.
			AppModelStore store = EVEDroidApp.getAppStore();
			// Get the full list of assets for this pilot.
			final AssetsManager manager = store.getPilot().getAssetsManager();
			ArrayList<Asset> assets = manager.getAllAssets();
			// Move the list to a processing map.
			assetMap = new LongSparseArray<Asset>(assets.size());
			for (Asset asset : assets) {
				assetMap.put(asset.getAssetID(), asset);
			}
			// Process the map until all elements are removed.
			try {
				Long key = assetMap.keyAt(0);
				Asset point = assetMap.get(key);
				while (null != point) {
					processElement(point);
					key = assetMap.keyAt(0);
					point = assetMap.get(key);
				}
			} catch (NoSuchElementException nsee) {
				nsee.printStackTrace();
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			logger.severe(
					"RTEX> AssetsByLocationDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
	}

	public void setCharacterSheet(CharacterSheetResponse sheet) {
		characterSheet=sheet;
	}
	public int getSkillLevel(final int skillID) {
		// Corporation api will have all skills maxed.
//		if (isCorporation()) return 5;
		final Set<com.beimin.eveapi.model.pilot.Skill> currentskills = characterSheet.getSkills();
		for (final com.beimin.eveapi.model.pilot.Skill apiSkill : currentskills)
			if (apiSkill.getTypeID() == skillID) return apiSkill.getLevel();
		return 0;
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

}

// - UNUSED CODE ............................................................................................
