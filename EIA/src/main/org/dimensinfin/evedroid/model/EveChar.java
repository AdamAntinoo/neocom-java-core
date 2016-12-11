//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.logging.Logger;

import org.dimensinfin.evedroid.interfaces.INeoComNode;

import com.beimin.eveapi.connectors.CachingConnector;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveChar extends Pilot implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= -955059830168434115L;
	private static Logger											logger						= Logger.getLogger("EveChar");
	private static transient CachingConnector	apiCacheConnector	= null;

	// - F I E L D - S E C T I O N ............................................................................
	//	private transient Instant									assetsCacheTime			= null;
	//	private transient Instant									blueprintsCacheTime	= null;

	// - D E P E N D A N T   P R O P E R T I E S
	//	private long															totalAssets					= -1;
	//	private CharacterSheetResponse						characterSheet			= null;
	//	private transient AssetsManager						assetsManager				= null;
	//	private transient SkillInTrainingResponse	skillInTraining			= null;
	//	private transient ArrayList<ApiIndustryJob>	industryJobs				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public EveChar(final Integer key, final String validation, final long characterID) {
	//		super(key, validation, characterID);
	//	}
	protected EveChar() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	//	/**
	//	 * Returns the number of invention jobs that can be launched simultaneously. This will depend on the skills
	//	 * <code>Laboratory Operation</code> and <code>Advanced Laboratory Operation</code>.
	//	 * 
	//	 * @return
	//	 */
	//	public int calculateInventionQueues() {
	//		int queues = 1;
	//		final Set<ApiSkill> skills = characterSheet.getSkills();
	//		for (final ApiSkill apiSkill : skills) {
	//			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.LaboratoryOperation) {
	//				queues += apiSkill.getLevel();
	//			}
	//			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedLaboratoryOperation) {
	//				queues += apiSkill.getLevel();
	//			}
	//		}
	//		return queues;
	//	}

	//	/**
	//	 * Returns the number of manufacture jobs that can be launched simultaneously. This will depend on the
	//	 * skills <code>Mass Production</code> and <code>Advanced Mass Production</code>.
	//	 * 
	//	 * @return
	//	 */
	//	public int calculateManufactureQueues() {
	//		int queues = 1;
	//		final Set<ApiSkill> skills = characterSheet.getSkills();
	//		for (final ApiSkill apiSkill : skills) {
	//			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.MassProduction) {
	//				queues += apiSkill.getLevel();
	//			}
	//			if (apiSkill.getTypeID() == ModelWideConstants.eveglobal.skillcodes.AdvancedMassProduction) {
	//				queues += apiSkill.getLevel();
	//			}
	//		}
	//		return queues;
	//	}

	//	/**
	//	 * For the EveChar the contents provided to the model are empty when the variant is related to the pilot
	//	 * list. Maybe in other calls the return would be another list of contents.
	//	 */
	//	@Override
	//	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
	//		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
	//		return results;
	//	}

	//	public AssetsManager getAssetsManager() {
	//		if (null == assetsManager) {
	//			assetsManager = new AssetsManager(this);
	//		}
	//		// Make sure the Manager is already connected to the Pilot.
	//		assetsManager.setPilot(this);
	//		return assetsManager;
	//	}

	//	public ApiAuthorization getAuthorization() {
	//		return new ApiAuthorization(keyID, characterID, verificationCode);
	//	}

	//	public int getSkillLevel(final int skillID) {
	//		// Corporation api will have all skills maxed.
	//		if (isCorporation()) return 5;
	//		final Set<ApiSkill> skills = characterSheet.getSkills();
	//		for (final ApiSkill apiSkill : skills)
	//			if (apiSkill.getTypeID() == skillID) return apiSkill.getLevel();
	//		return 0;
	//	}

	//	/**
	//	 * At the Character creation we only have the key values to locate it into the CCP databases. During this
	//	 * execution we have to download many different info from many CCP API calls so it will take some time.<br>
	//	 * After this update we will have access to all the direct properties of a character. Other multiple value
	//	 * properties like assets or derived lists will be updated when needed by using other update calls.
	//	 */
	//	@Override
	//	public synchronized void updateCharacterInfo() {
	//		// TODO Verify that the data is stale before attempting to read it again.
	//		try {
	//			if (AppConnector.checkExpiration(lastCCPAccessTime, ModelWideConstants.HOURS1)) {
	//				downloadEveCharacterInfo();
	//				downloadCharacterSheet();
	//				// Get access to the character sheet data.
	//				final CharacterSheetParser parser = CharacterSheetParser.getInstance();
	//				final CharacterSheetResponse response = parser.getResponse(getAuthorization());
	//				if (null != response) {
	//					characterSheet = response;
	//				}
	//				lastCCPAccessTime = new Instant(response.getCachedUntil());
	//				setDirty(true);
	//			}
	//		} catch (final RuntimeException rtex) {
	//			rtex.printStackTrace();
	//		} catch (final ApiException apie) {
	//			apie.printStackTrace();
	//		}
	//	}

	//	private synchronized void downloadCharacterSheet() {
	//		logger.info(">> EveChar.downloadCharacterSheet");
	//		final String eveCharacterInfoCall = CHAR_CHARACTERSHEET + "?keyID=" + keyID + "&vCode=" + verificationCode
	//				+ "&characterID=" + characterID;
	//		final Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
	//		if (null == characterDoc) {
	//			setName("Corporation");
	//		} else {
	//			NodeList resultNodes = characterDoc.getElementsByTagName("result");
	//			Element result = (Element) resultNodes.item(0);
	//			if (null != result) {
	//				resultNodes = characterDoc.getElementsByTagName("name");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setName(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("balance");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setBalance(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("race");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setRace(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("cloneName");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setCloneName(text);
	//				}
	//				//			resultNodes = characterDoc.getElementsByTagName("cloneSkillPoints");
	//				//			result = (Element) resultNodes.item(0);
	//				//			if (null != result) {
	//				//				final String text = result.getTextContent();
	//				//				setCloneSkillPoints(text);
	//				//			}
	//				logger.info(".. Updated a new character <" + getName() + ">");
	//			}
	//		}
	//		logger.info("<< EveChar.downloadCharacterSheet");
	//	}

	//	@SuppressWarnings("rawtypes")
	//	private void downloadCharAssetsList() {
	//	}

	//	private synchronized void downloadEveCharacterInfo() {
	//		logger.info(">> EveChar.downloadEveCharacterInfo");
	//		final String eveCharacterInfoCall = EVE_CHARACTERINFO + "?keyID=" + keyID + "&vCode=" + verificationCode
	//				+ "&characterID=" + characterID;
	//		final Element characterDoc = AppConnector.getStorageConnector().accessDOMDocument(eveCharacterInfoCall);
	//		if (null == characterDoc) {
	//			setName("Corporation");
	//		} else {
	//			NodeList resultNodes = characterDoc.getElementsByTagName("result");
	//			Element result = (Element) resultNodes.item(0);
	//			if (null != result) {
	//				resultNodes = characterDoc.getElementsByTagName("characterName");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					logger.info(".. Setting name <" + text + ">");
	//					setName(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("accountBalance");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setBalance(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("shipName");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setShipName(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("shipTypeName");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setShipTypeName(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("corporation");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setCorporationName(text);
	//				}
	//				resultNodes = characterDoc.getElementsByTagName("lastKnownLocation");
	//				result = (Element) resultNodes.item(0);
	//				if (null != result) {
	//					final String text = result.getTextContent();
	//					setLastKnownLocation(text);
	//				}
	//				logger.info(".. Updated a new character <" + getName() + ">");
	//			}
	//		}
	//		logger.info("<< EveChar.downloadEveCharacterInfo");
	//	}

	//	private void downloadSkillTraining() {
	//		final SkillInTrainingParser parser = SkillInTrainingParser.getInstance();
	//		//		final ApiAuthorization auth = new ApiAuthorization(keyID, characterID, verificationCode);
	//		final SkillInTrainingResponse response;
	//		try {
	//			skillInTraining = parser.getResponse(getAuthorization());
	//		} catch (final ApiException e) {
	//			e.printStackTrace();
	//		}
	//	}

}

// - UNUSED CODE ............................................................................................
