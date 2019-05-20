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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.PreferenceKeys;
import org.dimensinfin.eveonline.neocom.manager.DownloadManager;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class TimedUpdater {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("TimedUpdater");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public TimedUpdater() {
//	}
	// - M E T H O D - S E C T I O N ..........................................................................
	public void timeTick() {
		logger.info(">> [TimedUpdater.timeTick]");
//			// STEP 01. Launch pending Data Requests
//			// Get requests pending from the queue service.
//			Vector<PendingRequestEntry> requests = NeoComApp.getSingletonApp().getCacheConnector().getAndroidPendingRequests();
//			if ( requests.size() > 0 ) logger.info("-- [TimeTickReceiver.onReceive]> Pending requests: {}",
//					requests.size());
//			synchronized (requests) {
//				// Process request by priority. Additions to queue are limited.
//				limit = 0;
//				for (PendingRequestEntry entry : requests)
//					if ( entry.state == ERequestState.PENDING ) {
//						// Filter only MARKETDATA requests.
//						if ( entry.reqClass == ERequestClass.MARKETDATA ) {
//							if ( this.blockedMarket() ) continue;
//							if ( limit <= TimeTickReceiver.LAUNCH_LIMIT ) {
//								this.launchMarketUpdate(entry);
//							}
//						}
//						// Filter the rest of the character data to be updated
//						if ( entry.reqClass == ERequestClass.CHARACTERUPDATE ) {
//							this.launchCharacterDataUpdate(entry);
//						}
//						if ( entry.reqClass == ERequestClass.CITADELUPDATE ) {
//							// Launch the update and remove the event from the queue.
//							new UpdateCitadelsTask().execute();
//							NeoComApp.getSingletonApp().getCacheConnector().clearPendingRequest(entry.getIdentifier());
//						}
//						if ( entry.reqClass == ERequestClass.OUTPOSTUPDATE ) {
//							// Launch the update and remove the event from the queue.
//							new UpdateOutpostsTask().execute();
//							NeoComApp.getSingletonApp().getCacheConnector().clearPendingRequest(entry.getIdentifier());
//						}
//					}
//			}

		// STEP 02. Check Credentials for pending data to update.
		final List<Credential> credentialList = DataManagementModelStore.accessCredentialList();
		logger.info("-- [TimedUpdater.timeTick]> Accessing credentials. Credentials found: {}"
				, credentialList.size());
		for (Credential cred : credentialList) {
			logger.info("-- [TimedUpdater.timeTick]> Processing Credential: {}-{}"
					,cred.getAccountId(),cred.getAccountName());
			// Set up the complete list depending on the Preferences selected.
			boolean blockDownloads = GlobalDataManager.getDefaultSharedPreferences()
					.getBoolean(PreferenceKeys.prefkey_BlockDownloads.name(), false);
			final ArrayList<GlobalDataManager.EDataUpdateJobs> joblist = new ArrayList<>();
			if (!blockDownloads) {
				// Check Character Update
				boolean blockCharacter = GlobalDataManager.getDefaultSharedPreferences()
						.getBoolean(PreferenceKeys.prefkey_BlockCharacterUpdate.name(), false);
				if (!blockCharacter) joblist.add(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE);

				// Check Assets
				boolean blockAssets = GlobalDataManager.getDefaultSharedPreferences()
						.getBoolean(PreferenceKeys.prefkey_BlockAssetsUpdate.name(), false);
				if (!blockAssets) joblist.add(GlobalDataManager.EDataUpdateJobs.ASSETDATA);

				// Check Colony data
				boolean blockColony = GlobalDataManager.getDefaultSharedPreferences()
						.getBoolean(PreferenceKeys.prefkey_BlockColonyUpdate.name(), false);
				if (!blockColony) joblist.add(GlobalDataManager.EDataUpdateJobs.COLONYDATA);

				// Check Skills
				boolean blockSkills = GlobalDataManager.getDefaultSharedPreferences()
						.getBoolean(PreferenceKeys.prefkey_BlockSkillsUpdate.name(), false);
				if (!blockSkills) joblist.add(GlobalDataManager.EDataUpdateJobs.SKILL_DATA);

				joblist.add(GlobalDataManager.EDataUpdateJobs.BLUEPRINTDATA);
				joblist.add(GlobalDataManager.EDataUpdateJobs.INDUSTRYJOBS);
				joblist.add(GlobalDataManager.EDataUpdateJobs.MARKETORDERS);
			}

			// Now process all job classes contained on the list . If the TS is found check it. If not fire an update.
			logger.info("-- [TimedUpdater.timeTick]> Jobs to process: {}",joblist);
			for (GlobalDataManager.EDataUpdateJobs jobName : joblist) {
				try {
					final String reference = Job.constructReference(jobName, cred.getAccountId());
					// Search for the TS and check the expiration time.
					final TimeStamp ts = GlobalDataManager.getNeocomDBHelper().getTimeStampDao().queryForId(reference);
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
			logger.info("<< [TimedUpdater.timeTick]");
		}
	}

	/**
	 * With the TS and the Credential check id the data is still valid. If the timestamp has elapsed then we should
	 * request the launch of the job. It is possible that this has been detected also on previous iterations but the
	 * Manager filter should remove the duplication of jobs.
	 *
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
		currentrequestReference = Job.constructReference(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE
				, credential.getAccountId());
		// Check that the request is a COLONY_DATA update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final Job newJob = new Job(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE)
					.setTask(() -> {
						logger.info("-- [Job.CHARACTER_CORE]> Downloading Pilot v1 information for: [{}]", credential.getAccountName());
						GlobalDataManager.udpatePilotV1(credential.getAccountId());

						// Update the timer for this download at the database.
						final Instant validUntil = Instant.now()
								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.CHARACTER_PUBLIC));
						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
								.setCredentialId(credential.getAccountId())
								.store();
					});
			UpdateJobManager.submit(newJob);
			return;
		}

		// Search for COLONYDATA job request.
		currentrequestReference = Job.constructReference(GlobalDataManager.EDataUpdateJobs.COLONYDATA
				, credential.getAccountId());
		// Check that the request is a COLONYDATA update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final Job newJob = new Job(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.COLONYDATA)
					.setTask(() -> {
						try {
							logger.info("-- [Job.COLONYDATA]> Downloading Colony list for: [{}]", credential.getAccountName());
							// Get the list of planet colonies for this credential and update each one on the database.
							GlobalDataManager.downloadColonies4Credential(credential);

							// Update the timer for this download at the database.
							final Instant validUntil = Instant.now()
									.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.PLANETARY_INTERACTION_PLANETS));
							final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
									.setCredentialId(credential.getAccountId())
									.store();
						} catch (RuntimeException rtex) {
							rtex.printStackTrace();
						}
					});
			UpdateJobManager.submit(newJob);
			return;
		}

		// Search for ASSETDATA job request.
		currentrequestReference = Job.constructReference(GlobalDataManager.EDataUpdateJobs.ASSETDATA
				, credential.getAccountId());
		// Check that the request is a ASSETDATA update request.
		if (dataIdentifier.getReference().equalsIgnoreCase(currentrequestReference)) {
			// Submit the job to the manager
			final String transferredCurrentrequestReference = currentrequestReference;
			final Job newJob = new Job(dataIdentifier)
					.setCredentialIdentifier(credential.getAccountId())
					.setJobClass(GlobalDataManager.EDataUpdateJobs.ASSETDATA)
					.setTask(() -> {
						logger.info("-- [Job.ASSETDATA]> Downloading asset list for: [{}]", credential.getAccountName());
						final DownloadManager downloader = new DownloadManager(credential);
						downloader.downloadPilotAssetsESI();

						// Update the timer for this download at the database.
						final Instant validUntil = Instant.now()
								.plus(GlobalDataManager.getCacheTime4Type(GlobalDataManager.ECacheTimes.ASSETS_ASSETS));
						final TimeStamp ts = new TimeStamp(transferredCurrentrequestReference, validUntil)
								.setCredentialId(credential.getAccountId())
								.store();
					});
			UpdateJobManager.submit(newJob);
			return;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer("TimedUpdater[")
				.append("field:").append(0).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	public static class Job {
		private static final String DEFAULT_REF_VALUE = "-REF-";

		public static String constructReference( final GlobalDataManager.EDataUpdateJobs type, final long identifier ) {
			return new StringBuffer(type.name()).append("/").append(identifier).toString();
		}

		// - F I E L D - S E C T I O N ............................................................................
		private String reference = DEFAULT_REF_VALUE;
		private long credentialId = -1;
		private GlobalDataManager.EDataUpdateJobs jobClass = GlobalDataManager.EDataUpdateJobs.READY;
		private Runnable task = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public Job( final TimeStamp job ) {
			super();
			// Set som of the fields from the parameter.
			reference = job.getReference();
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public String getReference() {
			return reference;
		}

		public Job setCredentialIdentifier( final long identifier ) {
			credentialId = identifier;
			return this;
		}

		public Job setJobClass( final GlobalDataManager.EDataUpdateJobs jobClass ) {
			this.jobClass = jobClass;
			return this;
		}

		public Job setTask( final Runnable task ) {
			this.task = task;
			return this;
		}

		public Future<?> submit() throws NeoComException {
			// Check that all the parameters are valid and are filled with data.
			if (null == task) throw new NeoComException("[Job]> Jobs task is not defined. Nothing to run.");
			if (DEFAULT_REF_VALUE.equalsIgnoreCase(reference))
				throw new NeoComException("[Job]> Reference not set. Unexpected initialization error.");
			if (-1 == credentialId)
				throw new NeoComException("[Job]> Credential not identified.. Cannot run job on unidentified target.");

			// Launch job and read back the future to control the execution
			return UpdateJobManager.downloadExecutor.submit(task);
		}
	}

	/**
	 * The main responsibility of this class is to have a unique list of update jobs. If every minute we check for
	 * data to update and that data is already scheduled but not completed we can found a second and third requests
	 * that will also have to be executes.
	 * So we need something between the launcher of updated and the executor that removed already registered
	 * updates and do not request them again.
	 * Using an specific executor for this task will isolate the run effect from other tasks but anyway it
	 * requires some way for the job to notify its state so it can clear the request after completed or remove it
	 * if the process fails or gets interrupted.
	 * With the use of utures we can track pending jobs and be sure the update mechanics are followed as
	 * requested.
	 */
	//- CLASS IMPLEMENTATION ...................................................................................
	public static class UpdateJobManager {
		// - S T A T I C - S E C T I O N ..........................................................................
		public static int updateJobCounter = 0;
		private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
		private static final Hashtable<String, Future<?>> runningJobs = new Hashtable();

		/**
		 * Submits the job to out private executor. Store the Future to control job duplicated and to check when the
		 * job completes. The job reference can be used a primary key to detect job duplicates and collision.
		 *
		 * @param newJob the job to update some information.
		 */
		public synchronized static void submit( final Job newJob ) {
			try {
				// Search for the job to detect duplications
				final String identifier = newJob.getReference();
				final Future<?> hit = runningJobs.get(identifier);
				if (null == hit) {
					// New job. Launch it and store the reference.
					logger.info("-- [UpdateJobManager.submit]> Launching job {}", newJob.getReference());
					final Future<?> future = newJob.submit();
					runningJobs.put(identifier, future);
				} else {
					// Check for job completed.
					if (hit.isDone()) {
						// The job with this same reference has completed. We can launch a new one.
						final Future<?> future = newJob.submit();
						runningJobs.put(identifier, future);
					}
				}
			} catch (NeoComException neoe) {
				neoe.printStackTrace();
			}
			// Count the running or pending jobs to update the ActionBar counter.
			int counter = 0;
			for (Future<?> future : runningJobs.values()) {
				if (!future.isDone()) counter++;
			}
			updateJobCounter = counter;
		}

		// - F I E L D - S E C T I O N ............................................................................

		// - C O N S T R U C T O R - S E C T I O N ................................................................

		// - M E T H O D - S E C T I O N ..........................................................................
	}
}

// - UNUSED CODE ............................................................................................
//[01]
