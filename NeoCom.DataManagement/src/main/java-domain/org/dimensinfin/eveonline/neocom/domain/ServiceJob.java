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
package org.dimensinfin.eveonline.neocom.domain;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.exception.NeoComException;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.services.UpdateJobManager;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ServiceJob {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("ServiceJob");
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
	public ServiceJob( final TimeStamp job ) {
		super();
		// Set som of the fields from the parameter.
		reference = job.getReference();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getReference() {
		return reference;
	}

	public ServiceJob setCredentialIdentifier( final long identifier ) {
		credentialId = identifier;
		return this;
	}

	public ServiceJob setJobClass( final GlobalDataManager.EDataUpdateJobs jobClass ) {
		this.jobClass = jobClass;
		return this;
	}

	public ServiceJob setTask( final Runnable task ) {
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
		return UpdateJobManager.updaterExecutor.submit(task);
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("ServiceJob [ ")
				.append("reference:").append(reference).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
