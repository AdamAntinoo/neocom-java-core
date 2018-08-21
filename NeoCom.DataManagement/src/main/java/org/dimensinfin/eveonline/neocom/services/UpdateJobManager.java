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

import org.dimensinfin.eveonline.neocom.exception.NeoComException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The main responsibility of this class is to have a unique list of update jobs. If every minute we check for
 * data to update and that data is already scheduled but not completed we can found a second and third requests
 * that will also have to be executes.
 * So we need something between the launcher of updated and the executor that removed already registered
 * updates and do not request them again.
 * Using an specific executor for this task will isolate the run effect from other tasks but anyway it
 * requires some way for the job to notify its state so it can clear the request after completed or remove it
 * if the process fails or gets interrupted.
 * With the use of futures we can track pending jobs and be sure the update mechanics are followed as
 * requested.
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class UpdateJobManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("UpdateJobManager");
	public static int updateJobCounter = 0;
	public static final ExecutorService updaterExecutor = Executors.newSingleThreadExecutor();
	private static final Hashtable<String, Future<?>> runningJobs = new Hashtable();

	/**
	 * Submits the job to out private executor. Store the Future to control job duplicated and to check when the
	 * job completes. The job reference can be used a primary key to detect job duplicates and collision.
	 * @param newJob the job to update some information.
	 */
	public synchronized static void submit( final ServiceJob newJob ) {
		try {
			// Search for the job to detect duplications
			final String identifier = newJob.getReference();
			final Future<?> hit = runningJobs.get(identifier);
			if ( null == hit ) {
				// New job. Launch it and store the reference.
				logger.info("-- [UpdateJobManager.submit]> Launching job {}", newJob.getReference());
				final Future<?> future = newJob.submit();
				runningJobs.put(identifier, future);
			} else {
				// Check for job completed.
				if ( hit.isDone() ) {
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
			if ( !future.isDone() ) counter++;
		}
		updateJobCounter = counter;
	}

	public synchronized static int getPendingJobsCount() {
		int pending = 0;
		for (Future<?> job : runningJobs.values()) {
			if ( job.isDone() ) continue;
			pending++;
		}
		return pending;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("UpdateJobManager [ ")
				.append("jobs: ").append(updateJobCounter).append(" ")
				.append("]")
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
