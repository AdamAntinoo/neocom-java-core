package org.dimensinfin.eveonline.neocom.domain;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.services.DataDownloaderService;
import org.joda.time.LocalDateTime;

public class PilotV3 extends PilotV2  {
	private transient DataDownloaderService downloaderService;
//	private Credential credential;

	// - C O R E   D A T A
	private LocalDateTime publicDataAccess;

	// -  C O N S T R U C T O R S
//	private PilotV3( final Credential credential, final DataDownloaderService dowloaderService ) {
//		this.credential = credential;
//		this.downloaderService = dowloaderService;
//	}

	// - D A T A   A C C E S S O R S
//	@Override
//	public int getCharacterId() {
//		return this.credential.getAccountId();
//	}

	@Override
	public double getAccountBalance() {
		return super.getAccountBalance();
	}

//	@Override
	public long getTotalAssetsNumber() {
		return this.totalAssetsNumber;
	}

//	@Override
//	public int getRaceId() {
//		if (null != this.publicData) return this.publicData.getRaceId();
//		else return 0;
//	}

//	public String getRaceName() {
//		if (null == this.race) return null;
//		else return this.race.getName();
//	}

	/**
	 * A pilot instance is a set of multiple blocks of data, some of then obtained form the CCP data services and other from the local
	 * repository caches. Because the high quantity of data we cannot stop to update all them and then render the contents so we should separate
	 * data access from data representation. This is most important on Android since the UI thread will not allow to make networks calls on the same sequential
	 * line of code, so anyway we should move networks data download to a separate process.
	 *
	 * This method will interface with the data downloading service and make the requests for data synchronization that seem relevant, depending on the instance
	 * contents and business logic relative to timed data validity and priorities. So this method will decide of current data is stale
	 * or if no new requests should be made because current values are dependant on others that are still pending reception.
	 *
	 * Data interchange with the downloading service will be synchronized with events. Requests are made adding a callback message receiver and when
	 * the operation completes then the reception event will forward back the received data. This asynchronous gives flexibility and allows to request
	 * the same data more than one time without blocking other requests. Use of Futures or Observables creates unnecessary threads to controls the flows
	 * that can be removed with event messaging.
	 *
	 * Once data arrives this method will notify the controller about changes to send the UI updated messages necessary to start the rendering process with the new data.
	 */
//	private void synchronizeInstance() {
//		// Start checking what data is missing or stale.
//		// PILOT PUBLIC DATA
//		if (this.checkDataStaleness(this.publicDataAccess, PilotDataSections.PILOT_PUBLICDATA))
//			this.downloaderService.accessPilotPublicData(this, PilotDataSections.PILOT_PUBLICDATA);
//		// PILOT FAMILY CREDENTIALS
//		if (this.checkDataStaleness(this.race, PilotDataSections.PILOT_RACE))
//			this.downloaderService.accessPilotRace(this, PilotDataSections.PILOT_RACE);
//		//		if (this.checkDataStaleness(this.bloodline, PilotDataSections.PILOT_BLOODLINE))
//		//			this.downloaderService.accessPilotPublicData(this.getCharacterId()
//		//					, this
//		//					, PilotDataSections.PILOT_BLOODLINE);
//		//		if (this.checkDataStaleness(this.ancestry, PilotDataSections.PILOT_ANCESTRY))
//		//			this.downloaderService.accessPilotPublicData(this.getCharacterId()
//		//					, this
//		//					, PilotDataSections.PILOT_ANCESTRY);
//		//		if (this.checkDataStaleness(this.corporation, PilotDataSections.PILOT_CORPORATION))
//		//			this.downloaderService.accessPilotPublicData(this.getCharacterId()
//		//					, this
//		//					, PilotDataSections.PILOT_CORPORATION);
//		//		if (this.checkDataStaleness(this.alliance, PilotDataSections.PILOT_ALLIANCE))
//		//			this.downloaderService.accessPilotPublicData(this.getCharacterId()
//		//					, this
//		//					, PilotDataSections.PILOT_ALLIANCE);
//	}

	boolean checkDataStaleness( final Object data, final PilotDataSections section ) {
		return (null == data);
	}

	boolean checkDataStaleness( final LocalDateTime timestamp, final PilotDataSections section ) {
		if (null == timestamp) return true;
		if (timestamp.plusMillis(section.getCacheTime()).isBefore(new LocalDateTime())) return true;
		return false;
	}

	// - I N E O C O M D O W N L O A D C A L L B A C K
//	@Override
//	public Credential getCredential() {
//		return this.credential;
//	}

//	@Override
	public void signalCompletion( final PilotDataSections section, final Object completedData ) {
		switch (section) {
			case PILOT_PUBLICDATA:
				this.setPublicData((GetCharactersCharacterIdOk) completedData);
//				this.sendChangeEvent(new PropertyChangeEvent(this
//						, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
//						, null, completedData));
				break;
//			case PILOT_RACE:
//				this.setRace((GetUniverseRaces200Ok) completedData);
////				this.sendChangeEvent(new PropertyChangeEvent(this
////						, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
////						, null, completedData));
//				break;
		}
	}

//	// - B U I L D E R
//	public static class Builder {
//		private PilotV3 onConstruction;
//
//		public Builder(  final Credential credential,final DataDownloaderService dowloaderService ) {
//			Objects.requireNonNull(credential);
//			Objects.requireNonNull(dowloaderService);
//			this.onConstruction = new PilotV3(credential, dowloaderService);
//		}
//
//		public PilotV3 build() {
//			// Building will require more than constructing the POJO but also starting the data access process.
//			this.onConstruction.synchronizeInstance();
//			return this.onConstruction;
//		}
//	}
}
