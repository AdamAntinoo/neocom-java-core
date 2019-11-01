package org.dimensinfin.eveonline.neocom.service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.core.LogMessagesExternalisedType;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.updater.NeoComUpdater;

/**
 * The main responsibility of this class is to have a unique list of update jobs. If every minute we check for
 * data to update and that data is already scheduled but not completed we can found a second and third requests
 * that will also have to be executed.
 * So we need something between the launcher of updated and the executor that removes already registered
 * updates and do not launch them again.
 * Using an specific executor for this task will isolate the run effect from other tasks but anyway it
 * requires some way for the job to notify its state so it can clear the request after completed or remove it
 * if the process fails or gets interrupted.
 * With the use of futures we can track pending jobs and be sure the update mechanics are followed as
 * requested.
 *
 * @author Adam Antinoo
 */
public class UpdaterJobManager {
	public static Logger logger = LoggerFactory.getLogger(UpdaterJobManager.class);
	public static final ExecutorService updaterExecutor = Executors.newSingleThreadExecutor();
	private static final Map<String, JobRecord> runningJobs = new ConcurrentHashMap<>();

	/**
	 * Submits the job to out private executor. Store the Future to control job duplicates and to check when the
	 * job completes. The job reference can be used a primary key to detect job duplicates and collision.
	 * Start the process to launch a new updater process. Check if there already the same request to remove duplicates but if the
	 * updater pointed by the key is not found or has completed post a new job on the job queue.
	 *
	 * @param updater the updater to be used when running the job.
	 */
	public static synchronized void submit( final NeoComUpdater updater ) {
		final String identifier = updater.getIdentifier();
		if (alreadyScheduled(identifier)) {
			final JobRecord target = runningJobs.get(identifier);
			final NeoComUpdater.JobStatus jobStatus = target.getJob().getStatus();
			logger.info("-- [UpdaterJobManager.submit]> Job {} already on state: {}",
					updater.getIdentifier(), jobStatus.name());
			return;
		}
		logger.info("-- [UpdaterJobManager.submit]> Scheduling job {}", updater.getIdentifier());
		updater.setStatus(NeoComUpdater.JobStatus.SCHEDULED);
		try {
			final JobRecord record = new JobRecord(updater);
			final Future<NeoComUpdater> future = updaterExecutor.submit(record);
			record.setFuture(future);
			runningJobs.put(updater.getIdentifier(), record);
		} catch (NeoComRuntimeException neoe) {
			logger.info("RT [UpdaterJobManager.submit]> Runtime exception: {}", neoe.getMessage());
			logger.error("RT [UpdaterJobManager.submit]> Stack Trace: {}", neoe);
		}
	}

//	@Deprecated
//	public static synchronized void submit( final ServiceJob newJob ) {
//		try {
//			// Search for the job to detect duplications
//			final String identifier = newJob.getReference();
//			//			final Future<?> hit = runningJobs.get(identifier);
//			final CompletableFuture hit = null;
//			if (null == hit) {
//				// New job. Launch it and store the reference.
//				logger.info("-- [MARKETORDERS]> Launching job {}", newJob.getReference());
//				logger.info("-- [UpdaterJobManager.submit]> Launching job {}", newJob.getReference());
//				final Future<?> future = newJob.submit();
//				//				runningJobs.put(identifier, future);
//			} else {
//				// Check for job completed.
//				if (hit.isDone()) {
//					// The job with this same reference has completed. We can launch a new one.
//					final Future<?> future = newJob.submit();
//					//					runningJobs.put(identifier, future);
//				}
//			}
//		} catch (NeoComRuntimeException neoe) {
//			neoe.printStackTrace();
//		}
//		// Count the running or pending jobs to update the ActionBar counter.
//		//		int counter = 0;
//		//		for (Future<?> future : runningJobs.values()) {
//		//			if (!future.isDone()) counter++;
//		//		}
//		//		updateJobCounter = counter;
//	}

	/**
	 * Clean up all the jobs that are already completed.
	 *
	 * @return the number of jobs still pending execution or running.
	 */
	public static int clearJobs() {
		synchronized (runningJobs) {
			for (Map.Entry<String, JobRecord> entry : runningJobs.entrySet()) {
				if (entry.getValue().getFuture().isDone()) runningJobs.remove(entry.getKey());
				if (entry.getValue().getJob().getStatus() == NeoComUpdater.JobStatus.COMPLETED)
					runningJobs.remove(entry.getKey());
				if (entry.getValue().getJob().getStatus() == NeoComUpdater.JobStatus.EXCEPTION)
					runningJobs.remove(entry.getKey());
			}
			return runningJobs.size();
		}
	}

	public static int getPendingJobsCount() {
		synchronized (runningJobs) {
			int pending = 0;
			for (JobRecord job : runningJobs.values())
				if (!job.isDone()) pending++;
			return pending;
		}
	}

	protected static boolean alreadyScheduled( final String jobIdentifier ) {
		final JobRecord target = runningJobs.get(jobIdentifier);
		if (null == target) return false;
		if (target.getJob().getStatus() == NeoComUpdater.JobStatus.COMPLETED) return false;
		return !(target.getJob().getStatus() == NeoComUpdater.JobStatus.EXCEPTION);
	}

	private UpdaterJobManager() {
	}

	// - J O B R E C O R D
	public static class JobRecord implements Callable<NeoComUpdater> {
		private Future<NeoComUpdater> future;
		private NeoComUpdater job;

		public JobRecord( final NeoComUpdater job ) {
			this.job = job;
		}

		public Future<NeoComUpdater> getFuture() {
			return future;
		}

		public void setFuture( final Future<NeoComUpdater> future ) {
			this.future = future;
		}

		public NeoComUpdater getJob() {
			return job;
		}

		@Override
		public NeoComUpdater call() {
			try {
				logger.info(LogMessagesExternalisedType.UPDATEMANAGER_JOB_ENTERING_STATE.getMessage()
						, this.getJob().getIdentifier(), "OnPrepare");
				this.job.onPrepare();
				logger.info(LogMessagesExternalisedType.UPDATEMANAGER_JOB_ENTERING_STATE.getMessage()
						, this.getJob().getIdentifier(), "OnRun");
				this.job.onRun();
				logger.info(LogMessagesExternalisedType.UPDATEMANAGER_JOB_ENTERING_STATE.getMessage()
						, this.getJob().getIdentifier(), "OnComplete");
				this.job.onComplete();
			} catch (RuntimeException rte) {
				logger.info(LogMessagesExternalisedType.UPDATEMANAGER_JOB_ENTERING_STATE.getMessage()
						, this.getJob().getIdentifier(), "OnException");
				this.job.onException(rte);
			}
			return this.job;
		}

		public boolean isDone() {
			return (this.job.getStatus() == NeoComUpdater.JobStatus.COMPLETED) ||
					(this.job.getStatus() == NeoComUpdater.JobStatus.EXCEPTION);
		}
	}
}
