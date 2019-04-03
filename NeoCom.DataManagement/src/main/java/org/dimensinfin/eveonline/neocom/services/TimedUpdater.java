//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.services;

import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.PreferenceKeys;
import org.dimensinfin.eveonline.neocom.managers.DownloadManager;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class TimedUpdater {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("TimedUpdater");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public void timeTick() {
		logger.info(">> [TimedUpdater.timeTick]");
		// Check Credentials for pending data to update.
		final List<Credential> credentialList = GlobalDataManager.accessAllCredentials();
		logger.info("-- [TimedUpdater.timeTick]> Accessing credentials. Credentials found: {}"
				, credentialList.size());
		for (Credential cred : credentialList) {
			logger.info("-- [TimedUpdater.timeTick]> Processing Credential: {}-{}"
					, cred.getAccountId(), cred.getAccountName());
			// Set up the complete list depending on the Preferences selected.
			boolean blockDownloads = GlobalDataManager.getDefaultSharedPreferences()
					.getBooleanPreference(PreferenceKeys.prefkey_BlockDownloads.name(), true);
			final ArrayList<GlobalDataManager.EDataUpdateJobs> joblist = new ArrayList<>();
			if (!blockDownloads) {
				// Check Character Update
				boolean blockCharacter = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockCharacterUpdate.name(), true);
				if (!blockCharacter) joblist.add(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE);

				// Check Assets
				boolean blockAssets = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockAssetsUpdate.name(), true);
				if (!blockAssets) {
					joblist.add(GlobalDataManager.EDataUpdateJobs.ASSETDATA);
					joblist.add(GlobalDataManager.EDataUpdateJobs.BLUEPRINTDATA);
				}

				// Check Colony data
				boolean blockColony = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockColonyUpdate.name(), true);
				if (!blockColony) joblist.add(GlobalDataManager.EDataUpdateJobs.COLONYDATA);

				// Check Skills
				boolean blockSkills = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockSkillsUpdate.name(), true);
				if (!blockSkills) joblist.add(GlobalDataManager.EDataUpdateJobs.SKILL_DATA);

				// Check Industry downloads
				boolean blockIndustry = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockIndustryUpdate.name(), true);
				if (!blockIndustry) {
					joblist.add(GlobalDataManager.EDataUpdateJobs.INDUSTRYJOBS);
					joblist.add(GlobalDataManager.EDataUpdateJobs.MININGEXTRACTIONS);
				}

				// Check Market Orders downloads
				boolean blockOrders = GlobalDataManager.getDefaultSharedPreferences()
						.getBooleanPreference(PreferenceKeys.prefkey_BlockMarketOrdersUpdate.name(), true);
				if (!blockOrders) {
					joblist.add(GlobalDataManager.EDataUpdateJobs.MARKETORDERS);
				}
			}

			// Now process all job classes contained on the list . If the TS is found check it. If not fire an update.
			logger.info("-- [TimedUpdater.timeTick]> Jobs in list because allowed: {}", joblist);
			for (GlobalDataManager.EDataUpdateJobs jobName : joblist) {
				try {
					final String reference = ServiceJob.constructReference(jobName, cred.getAccountId());
					// Search for the TS and check the expiration time.
					final TimeStamp ts = new GlobalDataManager().getNeocomDBHelper().getTimeStampDao().queryForId(reference);
					if (null == ts) {
						logger.info("-- [TimedUpdater.timeTick]> Generating job request for {}.", reference);
						final TimeStamp newts = new TimeStamp(reference, Instant.now())
								.setCredentialId(cred.getAccountId());
						doProcessJob(newts, cred);
					} else {
						// Check if time point has already happened.
						if (ts.getTimeStamp() < Instant.now().getMillis()) {
							logger.info("-- [TimedUpdater.timeTick]> Time point past. Generating job request for {}.",
									reference);
							doProcessJob(ts, cred);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("<< [TimedUpdater.timeTick]");
	}

	/**
	 * With the TS and the Credential check id the data is still valid. If the timestamp has elapsed then we should
	 * request the launch of the job. It is possible that this has been detected also on previous iterations but the
	 * Manager filter should remove the duplication of jobs.
	 * @param dataIdentifier the time stamp and reference identifier for the job to launch.
	 * @param credential     the credential to use during the update.
	 */

	private void doProcessJob( final TimeStamp dataIdentifier, final Credential credential ) {
		logger.info(">> [TimedUpdater.doProcessJob]> Job Type: {} - Job credential: {}"
				, dataIdentifier.getReference()
				, credential.getAccountName());
		// Calculate job reference to match the job request.
		String currentrequestReference = "";

		// Search for CHARACTER_CORE job request.
		//		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE
		//				, credential.getAccountId());
		//		// Check that the request is a COLONY_DATA update request.
		//		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
		//			// Submit the job to the manager
		//			final String transferredCurrentrequestReference = currentrequestReference;
		//			final ServiceJob newJob = new ServiceJob(dataIdentifier)
		//					.setCredentialIdentifier(credential.getAccountId())
		//					.setJobClass(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE)
		//					.setTask(() -> {
		//						logger.info("-- [ServiceJob.CHARACTER_CORE]> Downloading Pilot v1 information for: [{}]", credential.getAccountName());
		//						GlobalDataManager.udpatePilotV1(credential.getAccountId());
		//
		//						// Update the timer for this download at the database.
		//						final Instant validUntil = Instant.now()
		//								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.CHARACTER_PUBLIC));
		//						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
		//								.setCredentialId(credential.getAccountId())
		//								.store();
		//					});
		//			UpdateJobManager.submit(newJob);
		//			return;
		//		}

		// Search for ASSETDATA job request.
		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.ASSETDATA
				, credential.getAccountId());
		// Check that the request is a ASSETDATA update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final ServiceJob newJob = new ServiceJob(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.ASSETDATA)
					.setTask(() -> {
						boolean allWentOk = false;
						try {
							logger.info("-- [ServiceJob.ASSETDATA]> Downloading asset list for: [{}]", credential.getAccountName());
							final DownloadManager downloader = new DownloadManager(credential);
							allWentOk = downloader.downloadPilotAssetsESI();
						} catch (SQLException sqle) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> SQLExceptions writing data to repository: {}"
									, sqle.getMessage());
						} catch (RuntimeException ntex) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Runtime exception {}"
									, ntex.getMessage());
						}

						if (allWentOk) {
							// Update the timer for this download at the database.
							final Instant validUntil = Instant.now()
									.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.ASSETS_ASSETS));
							final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
									.setCredentialId(credential.getAccountId())
									.store();
						}
					});
			UpdateJobManager.submit(newJob);
			return;
		}

		// Search for BLUEPRINTDATA job request.
		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.BLUEPRINTDATA
				, credential.getAccountId());
		// Check that the request is a BLUEPRINTDATA update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final ServiceJob newJob = new ServiceJob(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.BLUEPRINTDATA)
					.setTask(() -> {
						boolean allWentOk = false;
						try {
							logger.info("-- [ServiceJob.BLUEPRINTDATA]> Downloading blueprint list for: [{}]", credential.getAccountName());
							final DownloadManager downloader = new DownloadManager(credential);
							allWentOk = downloader.downloadPilotBlueprintsESI();
						} catch (SQLException sqle) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> SQLExceptions writing data to repository: {}"
									, sqle.getMessage());
						} catch (RuntimeException ntex) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Runtime exception {}"
									, ntex.getMessage());
						}

						if (allWentOk) {
							// Update the timer for this download at the database.
							final Instant validUntil = Instant.now()
									.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.CHARACTER_BLUEPRINTS));
							final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
									.setCredentialId(credential.getAccountId())
									.store();
						}
					});
			UpdateJobManager.submit(newJob);
			return;
		}

		// Search for INDUSTRYJOBS job request.
		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.INDUSTRYJOBS
				, credential.getAccountId());
		// Check that the request is a INDUSTRYJOBS update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final ServiceJob newJob = new ServiceJob(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.INDUSTRYJOBS)
					.setTask(() -> {
						logger.info("-- [ServiceJob.INDUSTRYJOBS]> Downloading Industry Jobs for: [{}]", credential.getAccountName());
						final DownloadManager downloader = new DownloadManager(credential);
						downloader.downloadPilotJobsESI();

						// Update the timer for this download at the database.
						final Instant validUntil = Instant.now()
								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.INDUSTRY_JOBS));
						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
								.setCredentialId(credential.getAccountId())
								.store();
					});
			UpdateJobManager.submit(newJob);
			return;
		}

		// Search for MARKETORDERS job request.
		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.MARKETORDERS
				, credential.getAccountId());
		// Check that the request is a MARKETORDERS update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final ServiceJob newJob = new ServiceJob(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.MARKETORDERS)
					.setTask(() -> {
						logger.info("-- [ServiceJob.MARKETORDERS]> Downloading Market Orders for: [{}]", credential.getAccountName());
						final DownloadManager downloader = new DownloadManager(credential);
						downloader.downloadPilotMarketOrdersESI();

						// Update the timer for this download at the database.
						final Instant validUntil = Instant.now()
								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.INDUSTRY_MARKET_ORDERS));
						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
								.setCredentialId(credential.getAccountId())
								.store();
					});
			UpdateJobManager.submit(newJob);
			return;
		}
		// Search for MININGEXTRACTIONS job request.
		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.MININGEXTRACTIONS
				, credential.getAccountId());
		// Check that the request is a MININGEXTRACTIONS update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final ServiceJob newJob = new ServiceJob(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.MININGEXTRACTIONS)
					.setTask(() -> {
						try {
							logger.info("-- [ServiceJob.MININGEXTRACTIONS]> Downloading Mining Extractions for: [{}]", credential.getAccountName());
							final DownloadManager downloader = new DownloadManager(credential);
							downloader.downloadPilotMiningActionsESI();
							// If we reach this point we can fire the fragment update.
							// TODO - There is no connection to the Activity/Fragment.
						} catch (SQLException sqle) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> SQLExceptions writing data to repository: {}"
									, sqle.getMessage());
							//						} catch (NeoComRuntimeException nrex) {
							//							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Credential not found in the list. Exception: {}"
							//									, nrex.getMessage());
						} catch (RuntimeException ntex) {
							logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Runtime exception {}"
									, ntex.getMessage());
						}
						// Update the timer for this download at the database.
						final Instant validUntil = Instant.now()
								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.INDUSTRY_MINING));
						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
								.setCredentialId(credential.getAccountId())
								.store();
					});
			UpdateJobManager.submit(newJob);
			return;
		}
		//		// Search for COLONYDATA job request.
		//		currentrequestReference = ServiceJob.constructReference(GlobalDataManager.EDataUpdateJobs.COLONYDATA
		//				, credential.getAccountId());
		//		// Check that the request is a COLONYDATA update request.
		//		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
		//			// Submit the job to the manager
		//			final String transferredCurrentrequestReference = currentrequestReference;
		//			final ServiceJob newJob = new ServiceJob(dataIdentifier)
		//					.setCredentialIdentifier(credential.getAccountId())
		//					.setJobClass(GlobalDataManager.EDataUpdateJobs.COLONYDATA)
		//					.setTask(() -> {
		//						try {
		//							logger.info("-- [ServiceJob.COLONYDATA]> Downloading Colony list for: [{}]", credential.getAccountName());
		//							// Get the list of planet colonies for this credential and update each one on the database.
		//							GlobalDataManager.downloadColonies4Credential(credential);
		//
		//							// Update the timer for this download at the database.
		//							final Instant validUntil = Instant.now()
		//							                                  .plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.CHARACTER_COLONIES));
		//							final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
		//									.setCredentialId(credential.getAccountId())
		//									.store();
		//						} catch (RuntimeException rtex) {
		//							rtex.printStackTrace();
		//						}
		//					});
		//			UpdateJobManager.submit(newJob);
		//			return;
		//		}

	}
}
// - UNUSED CODE ............................................................................................
//[01]
