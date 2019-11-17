package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.annotation.Singleton;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.JobStatus;

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
	}

	public int getJobCount() {
		return this.jobsRegistered.size();
	}

	public boolean needsNetwork() {
		return true;
	}

	public boolean needsStorage() {
		return true;
	}

	public int registerJob( final Job job2Register ) {
		this.jobsRegistered.add( job2Register );
		return this.jobsRegistered.size();
	}

	public void removeJob( final Job jobInstance ) {
		this.jobsRegistered.remove( jobInstance );
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
		job.setStatus( JobStatus.SCHEDULED );
		try {
			schedulerExecutor.submit( job );
		} catch (final NeoComRuntimeException neoe) {
			NeoComLogger.info( "RT [JobScheduler.submit]> Runtime exception: {}", neoe.getMessage() );
			NeoComLogger.info( "RT [JobScheduler.submit]> Stack Trace: {}", neoe.toString() );
		}
	}
}
