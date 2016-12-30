//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.config;

// - IMPORT SECTION .........................................................................................
import java.util.HashMap;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class DevelopmentStorageConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger									logger								= Logger.getLogger("DevelopmentStorageConnector");
	/**
	 * The list of resources to use in the tests. The cache will search for that resource instead of going to
	 * the net for the resource.
	 */
	public static HashMap<String, String>	recordedXMLResponses	= new HashMap<String, String>();

	static {
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/account/AccountStatus.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy",
						"accountAccountStatus_900001.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/account/Characters.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy",
						"accountCharacters_900001.xml");

		recordedXMLResponses
				.put(
						"https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444",
						"eveCharacterInfo_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444",
						"charCharacterSheet_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94005555",
						"eveCharacterInfo_94005555.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94005555",
						"charCharacterSheet_94005555.xml");
		//		recordedXMLResponses	= new HashMap<String, String>();
	}

	//	static {
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=93813310",
	//						"eveCharacterInfo_93813310.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=93813310",
	//						"charCharacterSheet_93813310.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=94003310",
	//						"eveCharacterInfo_94003310.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=94003310",
	//						"charCharacterSheet_94003310.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/AssetList.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=93813310",
	//						"charAssetList_93813310.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/AssetList.xml.aspx?keyID=924767&vCode=2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P&characterID=94003310",
	//						"charAssetList_94003310.xml");
	//		//	recordedXMLResponses = new HashMap<String, String>();
	//	}
	//	static {
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/account/Characters.xml.aspx?keyID=2889577&vCode=Mb6iDKR14m9Xjh9maGTQCGTkpjRHPjOgVUkvK6E9r6fhMtOWtipaqybp0qCzxuuw",
	//						"accountCharacters_2889577.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyID=2889577&vCode=Mb6iDKR14m9Xjh9maGTQCGTkpjRHPjOgVUkvK6E9r6fhMtOWtipaqybp0qCzxuuw&characterID=92223647",
	//						"eveCharacterInfo_92223647.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=2889577&vCode=Mb6iDKR14m9Xjh9maGTQCGTkpjRHPjOgVUkvK6E9r6fhMtOWtipaqybp0qCzxuuw&characterID=92223647",
	//						"charCharacterSheet_92223647.xml");
	//		recordedXMLResponses
	//				.put(
	//						"https://api.eveonline.com/char/AssetList.xml.aspx?keyID=2889577&vCode=Mb6iDKR14m9Xjh9maGTQCGTkpjRHPjOgVUkvK6E9r6fhMtOWtipaqybp0qCzxuuw&characterID=92223647",
	//						"charAssetList_92223647.xml");
	//	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DevelopmentStorageConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
