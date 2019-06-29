package org.dimensinfin.eveonline.neocom.services;

import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.domain.ServiceJob;
import org.dimensinfin.eveonline.neocom.domain.UpdaterJob;
import org.dimensinfin.eveonline.neocom.exception.NeoComException;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final ExecutorService updaterExecutor = Executors.newSingleThreadExecutor();
	private static final Hashtable<String, JobRecord> runningJobs = new Hashtable();
	//	public static int updateJobCounter = 0;
	private static Logger logger = LoggerFactory.getLogger(UpdaterJobManager.class);

	/**
	 * Submits the job to out private executor. Store the Future to control job duplicates and to check when the
	 * job completes. The job reference can be used a primary key to detect job duplicates and collision.
	 * Start the process to launch a new updater process. Check if there already the same request to remove duplicates but if the
	 * updater pointed by the key is not found or has completed post a new job on the job queue.
	 *
	 * @param updater the updater to be used when running the job.
	 */
	public synchronized static void submit( final NeoComUpdater updater ) {
		final String identifier = updater.getIdentifier();
		if (alreadyScheduled(identifier)) return;
		logger.info("-- [UpdaterJobManager.submit]> Scheduling job {}", updater.getIdentifier());
		updater.setStatus(UpdaterJob.JobStatus.SCHEDULED);
		try {
			final JobRecord record = new JobRecord(updater);
			final Future<NeoComUpdater> future = updaterExecutor.submit(record);
			record.setFuture(future);
			runningJobs.put(updater.getIdentifier(), record);
		} catch (NeoComRuntimeException neoe) {
			logger.info("RT [UpdaterJobManager.submit]> Runtime exception: {}", neoe.getMessage());
			neoe.printStackTrace();
		}
		//		updateJobCounterField(); // Count the running or pending jobs to update the ActionBar counter.
	}

	@Deprecated
	public synchronized static void submit( final ServiceJob newJob ) {
		try {
			// Search for the job to detect duplications
			final String identifier = newJob.getReference();
			//			final Future<?> hit = runningJobs.get(identifier);
			final CompletableFuture hit = null;
			if (null == hit) {
				// New job. Launch it and store the reference.
				logger.info("-- [MARKETORDERS]> Launching job {}", newJob.getReference());
				logger.info("-- [UpdaterJobManager.submit]> Launching job {}", newJob.getReference());
				final Future<?> future = newJob.submit();
//				runningJobs.put(identifier, future);
			} else {
				// Check for job completed.
				if (hit.isDone()) {
					// The job with this same reference has completed. We can launch a new one.
					final Future<?> future = newJob.submit();
					//					runningJobs.put(identifier, future);
				}
			}
		} catch (NeoComException neoe) {
			neoe.printStackTrace();
		}
		// Count the running or pending jobs to update the ActionBar counter.
		//		int counter = 0;
		//		for (Future<?> future : runningJobs.values()) {
		//			if (!future.isDone()) counter++;
		//		}
		//		updateJobCounter = counter;
	}

	public synchronized static int getPendingJobsCount() {
		int pending = 0;
		for (JobRecord job : runningJobs.values()) {
			if (job.isDone()) continue;
			else pending++;
		}
		return pending;
	}

	protected static boolean alreadyScheduled( final String jobIdentifier ) {
		final JobRecord target = runningJobs.get(jobIdentifier);
		if (null == target) return false;
		if (target.getJob().getStatus() == UpdaterJob.JobStatus.COMPLETED) return false;
		if (target.getJob().getStatus() == UpdaterJob.JobStatus.EXCEPTION) return false;
		return true;
	}

	//	public JobReport getJobreport() {
	//		return new JobReport(runningJobs);
	//	}

	//	protected void updateJobCounterField() {
	//
	//	}

//	@Override
//	public String toString() {
//		return new StringBuffer("UpdaterJobManager [ ")
//				       .append("jobs: ").append(updateJobCounter).append(" ")
//				       .append("]")
//				       .toString();
//	}

	// - J O B R E P O R T
	//	public static class JobReport {
	////		private int jobCount;
	//		private int jobsPending;
	//		public JobReport( final Hashtable<String, JobRecord> runningJobs ) {
	//
	//		}
	//
	//		public static
	//	}

	// - J O B R E C O R D
	public static class JobRecord implements Callable<NeoComUpdater> {
		private Future<NeoComUpdater> future;
		private NeoComUpdater job;

		public JobRecord( final NeoComUpdater job ) {
			this.job = job;
		}

//		public JobRecord( final Future<NeoComUpdater> future, final NeoComUpdater job ) {
//			this.future = future;
//			this.job = job;
//		}

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
		public NeoComUpdater call() throws Exception {
			return this.job.run();
		}

		public boolean isDone() {
			if (this.job.getStatus() == UpdaterJob.JobStatus.COMPLETED) return true;
			if (this.job.getStatus() == UpdaterJob.JobStatus.EXCEPTION) return true;
			return false;
		}
	}
}
