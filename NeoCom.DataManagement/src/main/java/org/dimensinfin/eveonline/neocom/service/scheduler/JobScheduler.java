package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.HashMap;
import java.util.Map;
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
 * The JobScheduler is a singleton instance. It should have a single instance on the whole system where the jobs are registered
 * and checked for the right time to be executed. The scheduler depends on an external time base so each platform can
 * implement its own timing base.
 *
 * It is expected that each minute the scheduler <code>runSchedule()</code> method is called to check from the list of
 * registered jobs which of them should be run on this minute.
 *
 * Being a singleton I should expect a default instance and the use of static calls to initiate the connection with the singleton.
 *
 * Registered jobs should avoid to record duplicates. Because of this I use Sets on the unique job identifier so jobs of the same class will use
 * their contents to identify possible different instances so a job with the same identifier is detected as already registered and the new
 * registration should replace the old registration. This allows to change job schedule parameters without removing old job first.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
@Singleton
public class JobScheduler {
	private static final ExecutorService schedulerExecutor = Executors.newSingleThreadExecutor();
	private static JobScheduler singleton = new JobScheduler();

	public static JobScheduler getJobScheduler() {
		if (null == singleton) singleton = new JobScheduler();
		return singleton;
	}

	private Map<Integer, Job> jobsRegistered = new HashMap<>();
	private CronScheduleGenerator cronScheduleGenerator = new HourlyCronScheduleGenerator.Builder().build();

	private JobScheduler() {}

	public int getJobCount() {
		return this.jobsRegistered.size();
	}

	public void setCronScheduleGenerator( final CronScheduleGenerator cronScheduleGenerator ) {
		this.cronScheduleGenerator = cronScheduleGenerator;
	}

	public void clear() {
		this.jobsRegistered.clear();
	}

	public boolean needsNetwork() {
		return true;
	}

	public boolean needsStorage() {
		return true;
	}

	public int registerJob( final Job job2Register ) {
		this.jobsRegistered.put( job2Register.getUniqueIdentifier(), job2Register );
		return this.jobsRegistered.size();
	}

	public void removeJob( final Job jobInstance ) {
		this.jobsRegistered.remove( jobInstance.getUniqueIdentifier() );
	}

	/**
	 * Check each of the registered jobs to see if they should be launched on this HOUR:MINUTE.
	 */
	public void runSchedule() {
		for (final Job job : this.jobsRegistered.values()) {
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
