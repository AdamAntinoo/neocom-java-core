//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.parser.AttributeGetters;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.core.INeoComNode;
import org.dimensinfin.evedroid.enums.EAPIKeyTypes;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// - CLASS IMPLEMENTATION ...................................................................................
public class APIKey extends APIKeyCore implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long								serialVersionUID		= 2600656062640799339L;
	private static Logger										logger							= Logger.getLogger("APIKey");
	private static String										ACCOUNT_CHARACTERS	= "https://api.eveonline.com/account/Characters.xml.aspx";
	private static String										ACCOUNT_STATUS			= "https://api.eveonline.com/account/AccountStatus.xml.aspx";

	// - F I E L D - S E C T I O N ............................................................................
	protected final HashMap<Long, EveChar>	characters					= new HashMap<Long, EveChar>();

	// - P R O P E R T I E S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public APIKey(final int keyID, final String verificationCode) {
		super(keyID, verificationCode);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		//		results.add(this);
		// Add the characters tied to this apikey.
		for (EveChar character : characters.values()) {
			results.add(character);
		}
		return results;
	}

	public HashMap<Long, EveChar> getCharacters() {
		return characters;
	}

	/**
	 * At the APIKey creation we should access CCP to get the list of characters associated with the pair. Those
	 * characters are then instantiated on the data model. Because that instantiation may be a time consuming
	 * process I suggest to prepare this model to allow for background manipulation. <br>
	 * That is because all Android models will relay on AsyncTasks to perform long time operations.
	 */
	public void update() {
		logger.info(">> APIKey.update");
		// Get the account new information added to the key
		String accountAccountStatus = ACCOUNT_STATUS + "?keyID=" + keyID + "&vCode=" + verificationCode;
		Element accountStatusDoc = AppConnector.getStorageConnector().accessDOMDocument(accountAccountStatus);
		if (null == accountStatusDoc) {
			setPaidUntil("2015-12-12 20:00:00");
		} else {
			NodeList resultNodes = accountStatusDoc.getElementsByTagName("result");
			Element result = (Element) resultNodes.item(0);
			if (null != result) {
				resultNodes = accountStatusDoc.getElementsByTagName("paidUntil");
				result = (Element) resultNodes.item(0);
				if (null != result) {
					final String text = result.getTextContent();
					logger.info(".. Setting paidUntil <" + text + ">");
					setPaidUntil(text);
				}
			}
		}
		// Download the references to the Characters referenced by this key
		String accountCharacterCall = ACCOUNT_CHARACTERS + "?keyID=" + keyID + "&vCode=" + verificationCode;
		// With this information go to the API and get a basic information of the Character to initialize it.
		Element acCharacterDoc = AppConnector.getStorageConnector().accessDOMDocument(accountCharacterCall);
		if (null != acCharacterDoc) {
			NodeList nodes = acCharacterDoc.getElementsByTagName("row");
			if (nodes.getLength() > 1) {
				type = EAPIKeyTypes.MultiCharacter;
			}
			for (int i = 0; i < nodes.getLength(); i++) {
				Element itemElement = (Element) nodes.item(i);
				long characterID = AttributeGetters.getLong(itemElement, "characterID");
				// Create the char with the locator values. Just the character. Do not download any data.
				EveChar newChar = new EveChar(keyID, verificationCode, characterID);
				// Add the character to the key instead to an external storage.
				EveChar existed = characters.get(newChar.getCharacterID());
				if (null != existed) {
					existed.setParent(this);
					existed.updateCharacterInfo();
					existed.clean();
					//					existed.updateAssets();
					//					existed.updateBlueprints();
				} else {
					characters.put(newChar.getCharacterID(), newChar);
					newChar.setParent(this);
					newChar.updateCharacterInfo();
					newChar.clean();
				}
				//				if (null != getParent()) ((AppModelStore) getParent()).addCharacter(newChar);
			}
			setDirty(true);
		}
		logger.info("<< APIKey.update");
	}
}

// - UNUSED CODE ............................................................................................
