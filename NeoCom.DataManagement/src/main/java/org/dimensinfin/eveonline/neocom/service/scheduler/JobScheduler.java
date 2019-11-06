package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;

public class JobScheduler {
	private static final ExecutorService schedulerExecutor = Executors.newSingleThreadExecutor();
	private Set<String> jobsKeys = new LinkedHashSet<>();
	private Set<Job> jobsRegistered = new LinkedHashSet<>();
	// - C O M P O N E N T S
	private CronScheduleGenerator cronScheduleGenerator;

	private JobScheduler() {}

	public int registerJob( final Job job2Register ) {
		if (!this.checkDuplicatedJob( job2Register )) {
			this.jobsKeys.add( job2Register.getIdentifier().toString() );
			this.jobsRegistered.add( job2Register );
		} else NeoComLogger.info( "The job {} is already on the scheduler.", job2Register.getIdentifier().toString() );
		return this.jobsRegistered.size();
	}

	/**
	 * Check each of the registered jobs to see if they should be launched on this HOUR:MINUTE.
	 */
	public void runSchedule() {
		final Iterator<Job> it = this.jobsRegistered.iterator();
		while (it.hasNext()) {
			final Job job = it.next();
			this.cronScheduleGenerator.match( job.getSchedule() );
			this.scheduleJob( job );
		}

	}

	private void scheduleJob( final Job job ) {
		NeoComLogger.info( "-- [JobScheduler.submit]> Scheduling job {}", job.getIdentifier().toString() );
		job.setStatus( JobStatus.SCHEDULED );
		try {
//			final UpdaterJobManager.JobRecord record = new JobRecord( updater );
			final Future<Boolean> future = schedulerExecutor.submit( job );
//			record.setFuture( future );
//			runningJobs.put( updater.getIdentifier(), record );
		} catch (NeoComRuntimeException neoe) {
			NeoComLogger.info( "RT [JobScheduler.submit]> Runtime exception: {}", neoe.getMessage() );
			NeoComLogger.info( "RT [JobScheduler.submit]> Stack Trace: {}", neoe.toString() );
		}
	}

	private boolean checkDuplicatedJob( final Job job2Register ) {
		return this.jobsKeys.contains( job2Register.getIdentifier().toString() );
	}

	// - B U I L D E R
	public static class Builder {
		private JobScheduler onConstruction;

		public Builder() {
			this.onConstruction = new JobScheduler();
		}

		public JobScheduler.Builder withCronScheduleGenerator( final CronScheduleGenerator cronScheduleGenerator ) {
			Objects.requireNonNull( cronScheduleGenerator );
			this.onConstruction.cronScheduleGenerator = cronScheduleGenerator;
			return this;
		}

		public JobScheduler build() {
			return this.onConstruction;
		}
	}
}