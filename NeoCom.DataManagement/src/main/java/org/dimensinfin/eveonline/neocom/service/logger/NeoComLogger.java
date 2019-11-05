package org.dimensinfin.eveonline.neocom.service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoComLogger {
	private static final Logger logger = LoggerFactory.getLogger( NeoComLogger.class );

	private NeoComLogger() {}

	public static void info( final String message ) {
		logger.info( wrapper( generateCaller() ) + "> " + message );
	}

	public static void info( final String message, String... arguments ) {
		logger.info( wrapper( generateCaller() ) + "> " + message, arguments );
	}

	private static String wrapper( final String data ) {
		return "[" + data + "]";
	}

	private static String generateCaller() {
		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		final StackTraceElement element = traceElements[2];
		return element.getClassName() + "." +
				element.getMethodName();
	}

//	// - B U I L D E R
//	public static class Builder {
//		private NeoComLogger onConstruction;
//
//		public Builder() {
//			this.onConstruction = new NeoComLogger();
//		}
//
//		public NeoComLogger build() {
//			return this.onConstruction;
//		}
//	}
}