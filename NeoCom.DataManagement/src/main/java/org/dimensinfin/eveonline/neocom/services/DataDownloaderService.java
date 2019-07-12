package org.dimensinfin.eveonline.neocom.services;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.domain.IEsiItemDownloadCallback;
import org.dimensinfin.eveonline.neocom.domain.IPilotDataDownloadCallback;
import org.dimensinfin.eveonline.neocom.domain.PilotDataSections;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataDownloaderService {
	private static final Logger logger = LoggerFactory.getLogger(DataDownloaderService.class);
	private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();

	public enum EsiItemSections {
		ESIITEM_DATA, ESIITEM_PRICE;
	}

	private ESIDataAdapter esiDataAdapter;

	private DataDownloaderService( final ESIDataAdapter esiDataAdapter ) {
		this.esiDataAdapter = esiDataAdapter;
	}

	public void accessEveItem( final IEsiItemDownloadCallback callbackDestination, final EsiItemSections section ) {
		logger.info("-- [DataDownloaderService.accessEveItem]> Posting request: {}", section.name());
		//		final GetUniverseTypesTypeIdOk item = this.esiDataAdapter.getUniverseTypeById(callbackDestination.getTypeId());
		//		if (null == item) {
		downloadExecutor.submit(() -> {
			logger.info("-- [DataDownloaderService.accessEveItem]> Downloading item data information...");
			final GetUniverseTypesTypeIdOk itemData = this.esiDataAdapter.getUniverseTypeById(
					callbackDestination.getTypeId());
			// Callback the pilot instance with the data.
			logger.info("-- [DataDownloaderService.accessEveItem]> Completed item data download. Sending data to callback.");
			if (null != itemData) callbackDestination.signalCompletion(section, itemData);
			else
				logger.info("-- [DataDownloaderService.accessEveItem]> Failed public data download. Null contents.");
		});
		//		} else callbackDestination.signalCompletion(section, item);
	}

	public void accessItemPrice( final IEsiItemDownloadCallback callbackDestination, final EsiItemSections section ) {
		logger.info("-- [DataDownloaderService.accessEveItem]> Posting request: {}", section.name());
		final double price = this.esiDataAdapter.searchSDEMarketPrice(callbackDestination.getTypeId());
		callbackDestination.signalCompletion(section, new Double(price));
	}


	// - B U I L D E R
	public static class Builder {
		private DataDownloaderService onConstruction;

		public Builder( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction = new DataDownloaderService(esiDataAdapter);
		}

		public Builder withEsiAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public DataDownloaderService build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			return this.onConstruction;
		}
	}

	// - P I L O T
	public void accessPilotPublicData( final IPilotDataDownloadCallback callbackDestination, final PilotDataSections section ) {
		logger.info("-- [DataDownloaderService.accessPilotPublicData]> Posting request: {}", section.name());
		downloadExecutor.submit(() -> {
			logger.info("-- [DataDownloaderService.accessPilotPublicData]> Downloading public data information...");
			final GetCharactersCharacterIdOk publicData = this.esiDataAdapter.getCharactersCharacterId(
					callbackDestination.getCredential().getAccountId());
			// Callback the pilot instance with the data.
			logger.info("-- [DataDownloaderService.accessPilotPublicData]> Completed public data download. Sending data to callback.");
			if (null != publicData) callbackDestination.signalCompletion(section, publicData);
			else
				logger.info("-- [DataDownloaderService.accessPilotPublicData]> Failed public data download. Null contents.");
		});
	}

	/**
	 * Search for the race information on the ESI cache. If the race data is not found then we should start a background download for all that
	 * data and when completed update the information on the callback instance.
	 *
	 * @param callbackDestination the instance to be notified when the data is found or the background download completes.
	 * @param section             the data that was requested. This is important to the callback to know where to put the data received.
	 */
	public void accessPilotRace( final IPilotDataDownloadCallback callbackDestination, final PilotDataSections section ) {
		logger.info("-- [DataDownloaderService.accessPilotRace]> Searching data: {}", section.name());
		downloadExecutor.submit(() -> {
			final GetUniverseRaces200Ok race = this.esiDataAdapter.searchSDERace(callbackDestination.getRaceId());
			if (null != race) callbackDestination.signalCompletion(section, race);
			else
				logger.info("-- [DataDownloaderService.accessPilotRace]> Failed race data assign. Null contents.");
		});
	}
}
