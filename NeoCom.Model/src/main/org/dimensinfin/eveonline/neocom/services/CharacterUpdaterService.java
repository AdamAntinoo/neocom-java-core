//	PROJECT:      Neocom.Microservices (NEOC-MS)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	SpringBoot-MS-Java 1.8.
//	DESCRIPTION:	This is the integration project for all the web server pieces. This is the launcher for
//								the SpringBoot+MicroServices+Angular unified web application.
package org.dimensinfin.eveonline.neocom.services;

import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.enums.EDataBlock;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public class CharacterUpdaterService implements Runnable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("CharacterUpdaterService");

	// - F I E L D - S E C T I O N ............................................................................
	private long					_locator	= -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CharacterUpdaterService(final long locator) {
		_locator = locator;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void run() {
		CharacterUpdaterService.logger.info(">> [CharacterUpdaterService.onHandleIntent]");
		//		Long localizer = (Long) intent.getSerializableExtra(AppWideConstants.EExtras.EXTRA_CHARACTER_LOCALIZER.name());
		//		// Be sure we have access to the network. Otherwise intercept the exceptions.
		//		if (NeoComApp.checkNetworkAccess()) {
		NeoComCharacter pilot = AppConnector.getModelStore().activatePilot(_locator);
		if (null != pilot) {
			// Pilot signaled for update. Locate the next data set to update because its cache has expired.
			EDataBlock datacode = pilot.needsUpdate();
			try {
				CharacterUpdaterService.logger.info(
						"-- [CharacterUpdaterService.onHandleIntent] EDataBlock to process: " + pilot.getName() + " - " + datacode);
				switch (datacode) {
					case CHARACTERDATA:
						pilot.updateCharacterInfo();
						AppConnector.getCacheConnector().clearPendingRequest(_locator);
						AppConnector.getCacheConnector().decrementTopCounter();
						break;
					case ASSETDATA:
						// New data model decouples the character from the data managers. But requires to know if Pilot or Corporation.
						if (pilot.isCorporation()) {
							pilot.getAssetsManager().downloadCorporationAssets();
						} else {
							pilot.getAssetsManager().downloadPilotAssets();
						}
						//							pilot.downloadAssets();
						//							pilot.downloadBlueprints();
						AppConnector.getCacheConnector().clearPendingRequest(_locator);
						AppConnector.getCacheConnector().decrementTopCounter();
						break;
					//													case BLUEPRINTDATA:
					////							pilot.downloadAssets();
					//							pilot.downloadBlueprints();
					//							NeoComApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
					//							NeoComApp.topCounter--;
					//							break;
					//						case INDUSTRYJOBS:
					//							pilot.downloadIndustryJobs();
					//							NeoComApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
					//							NeoComApp.topCounter--;
					//							break;
					//						case MARKETORDERS:
					//							pilot.downloadMarketOrders();
					//							NeoComApp.getTheCacheConnector().clearPendingRequest(Long.valueOf(localizer).toString());
					//							NeoComApp.topCounter--;
					//							break;

					default:
						break;
				}
			} catch (RuntimeException rtex) {
			}
		}
		// Relaunch more jobs if completed.
		//			NeoComApp.runTimer();
		//		}
		CharacterUpdaterService.logger.info("<< CharacterUpdaterService.onHandleIntent");
	}

}

// - UNUSED CODE ............................................................................................
