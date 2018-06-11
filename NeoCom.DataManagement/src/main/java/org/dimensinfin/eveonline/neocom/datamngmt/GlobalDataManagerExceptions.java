//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerExceptions extends GlobalDataManagerDataAccess {
	public enum EExceptionSeverity {
		UNEXPECTED, SEVERE, WARNING, INTERCEPTED
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerExceptions");

	// --- E X C E P T I O N   L O G G I N G   S E C T I O N
	private static final List<ExceptionRecord> exceptionsIntercepted = new ArrayList();

	public static void registerException( final String location
			, final Exception exceptionIntercepted
			, final EExceptionSeverity severity ) {
		logger.info(">< [GlobalDataManagerExceptions.registerException]> Exception loc/desc: {}/{}"
				, location, exceptionIntercepted.getMessage());
		exceptionsIntercepted.add(new ExceptionRecord(location, exceptionIntercepted, severity));
	}

	public static void registerRuntimeException( final String location
			, final RuntimeException exceptionIntercepted
			, final EExceptionSeverity severity ) {
		logger.info(">< [GlobalDataManagerExceptions.registerRuntimeException]> Exception loc/desc: {}/{}"
				, location, exceptionIntercepted.getMessage());
		exceptionsIntercepted.add(new RuntimeExceptionRecord(location, exceptionIntercepted, severity));
	}

	public static Exception getLastException() {
		return exceptionsIntercepted.get(exceptionsIntercepted.size() - 1).getException();
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ExceptionRecord {
		// - F I E L D - S E C T I O N ............................................................................
		private long timeStamp = 0;
		private Exception exceptionRegistered = null;
		private String interceptionMethod = "<>";
		private EExceptionSeverity severity = EExceptionSeverity.UNEXPECTED;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ExceptionRecord( final String location
				, final Exception newexception
				, final EExceptionSeverity severity ) {
			this.exceptionRegistered = newexception;
			this.timeStamp = Instant.now().getMillis();
			this.interceptionMethod = location;
			this.severity = severity;
			// Log the message.
			if ( severity == EExceptionSeverity.SEVERE )
				logger.error("EX [" + interceptionMethod + "]({})> Interception Exception: {}."
						, severity.name()
						, exceptionRegistered.getMessage());
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public Exception getException() {
			return this.exceptionRegistered;
		}

		public void setTimeStamp( final long timeStamp ) {
			this.timeStamp = timeStamp;
		}

		public void setTimeStamp( final Instant timeStamp ) {
			this.timeStamp = timeStamp.getMillis();
		}
	}

	// ........................................................................................................
	// - CLASS IMPLEMENTATION ...................................................................................
	public static class RuntimeExceptionRecord extends ExceptionRecord {
		// - F I E L D - S E C T I O N ............................................................................

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public RuntimeExceptionRecord( final String location
				, final Exception newexception
				, final EExceptionSeverity severity
		) {
			super(location, newexception, severity);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
	}
	// ........................................................................................................
}

// - UNUSED CODE ............................................................................................
//[01]
