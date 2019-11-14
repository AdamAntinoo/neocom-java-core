package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;
import org.dimensinfin.neocom.annotation.Singleton;

/**
 * The JobScheduler is a singleton instance. It should have a single instance on the while system where the jobs are registered
 * and checked for the right time to be executed. The scheduler depends on an external time basde so each platform can
 * implement its own timing base.
 *
 * It is expected that each minute the scheduler <code>runSchedule()</code> method is called to check from the list of
 * registered jobs which of them should be run on this minute.
 *
 * Being a singleton I should expect a default instance and the use of static calls to initiate the connection with the singleton.
 *
 * Registered jobs should avoid to record duplicates. Because of this I use Sets so a job with the same identifier is detected
 * as already registered and the new registration should replace the old registration.
 */
@Singleton
public class JobScheduler {
	private static final ExecutorService schedulerExecutor = Executors.newSingleThreadExecutor();
	private static JobScheduler singleton = new JobScheduler();

	public static JobScheduler getJobScheduler() {
		if (null == singleton) singleton = new JobScheduler();
		return singleton;
	}

//	private Set<String> jobsKeys = new LinkedHashSet<>();
	private Set<Job> jobsRegistered = new LinkedHashSet<>();
	// - C O M P O N E N T S
	private CronScheduleGenerator cronScheduleGenerator = new HourlyCronScheduleGenerator.Builder().build();

	private JobScheduler() {}

	public JobScheduler setCronScheduleGenerator( final CronScheduleGenerator cronScheduleGenerator ) {
		this.cronScheduleGenerator = cronScheduleGenerator;
		return this;
	}

	public void clear() {
		this.jobsRegistered.clear();
//		this.jobsKeys.clear();
	}

	public int getJobCount() {
		return this.jobsRegistered.size();
	}

	public int registerJob( final Job job2Register ) {
//		if (!this.alreadyRegistered( job2Register )) {
//			this.jobsKeys.add( job2Register.getIdentifier() );
			this.jobsRegistered.add( job2Register );
//		} else { // Replace the old job registration.
//			this.removeJob( job2Register.getIdentifier() );
//			return this.registerJob( job2Register );
////			NeoComLogger.info( "The job {} is already registered on the scheduler.",
////					job2Register.getIdentifier() );
//		}
		return this.jobsRegistered.size();
	}

	public void removeJob( final Job jobInstance ) {
		this.jobsRegistered.remove( jobInstance );
//		final Iterator<Job> it = this.jobsRegistered.iterator();
//		while (it.hasNext()) {
//			final Job target = it.next();
//			if (target.getIdentifier().equalsIgnoreCase( jobReference )) {
//				this.jobsRegistered.remove( target );
//				return target;
//			}
//		}
//		return null;
	}

	/**
	 * Check each of the registered jobs to see if they should be launched on this HOUR:MINUTE.
	 */
	public void runSchedule() {
		final Iterator<Job> it = this.jobsRegistered.iterator();
		while (it.hasNext()) {
			final Job job = it.next();
			if (this.cronScheduleGenerator.match( job.getSchedule() ))
				this.scheduleJob( job );
		}
	}

	public void wait4Completion() {
		NeoComLogger.enter();
		schedulerExecutor.shutdown();
		try {
			schedulerExecutor.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
			NeoComLogger.exit();
		} catch (InterruptedException ie) {
			NeoComLogger.info( "Scheduler terminated by external event." );
		}
	}

	protected void scheduleJob( final Job job ) {
//		NeoComLogger.info( "-- [JobScheduler.submit]> Scheduling job {}", job.getIdentifier() );
		job.setStatus( JobStatus.SCHEDULED );
		try {
			schedulerExecutor.submit( job );
		} catch (final NeoComRuntimeException neoe) {
			NeoComLogger.info( "RT [JobScheduler.submit]> Runtime exception: {}", neoe.getMessage() );
			NeoComLogger.info( "RT [JobScheduler.submit]> Stack Trace: {}", neoe.toString() );
		}
	}

//	private boolean alreadyRegistered( final Job job2Register ) {
//		return this.jobsKeys.contains( job2Register.getIdentifier() );
//	}

//	// - B U I L D E R
//	public static class Builder {
//		private JobScheduler onConstruction;
//
//		public Builder() {
//			if (null != singleton) this.onConstruction = singleton;
//			else this.onConstruction = new JobScheduler();
//		}
//
//		public JobScheduler.Builder withCronScheduleGenerator( final CronScheduleGenerator cronScheduleGenerator ) {
//			Objects.requireNonNull( cronScheduleGenerator );
//			this.onConstruction.cronScheduleGenerator = cronScheduleGenerator;
//			return this;
//		}
//
//		public JobScheduler build() {
//			Objects.requireNonNull( this.onConstruction.cronScheduleGenerator );
//			singleton = this.onConstruction;
//			return this.onConstruction;
//		}
//	}
}
