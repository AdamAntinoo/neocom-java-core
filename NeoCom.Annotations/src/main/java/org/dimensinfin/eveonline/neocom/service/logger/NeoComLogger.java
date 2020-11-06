package org.dimensinfin.eveonline.neocom.service.logger;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.logging.LogWrapper;

public class NeoComLogger extends LogWrapper {
		private static final Logger logger = LoggerFactory.getLogger( NeoComLogger.class );
	//
	//	public static void info( final String message ) {
	//		logger.info( "-- " + header() + message );
	//	}
	//
	//	public static void info( final String message, final Exception exception ) {
	//		logger.info( "-- " + header() + "> " + message + "-" + exception.getMessage() );
	//	}
	//
	@Deprecated
	public static void enter( final String message, final String subMessage ) {
		enter( MessageFormat.format( "{0} {1}", message, subMessage ) );
	}

	@Deprecated
	public static void info( final String message, final String subMessage ) {
		info( MessageFormat.format( "{0} {1}", message, subMessage ) );
	}

	//
	//	public static void enter() {
	//		logger.info( ">> " + header() );
	//	}
	//
	@Deprecated
	public static void info( final String message, String... arguments ) {
		logger.info( ">> " + header() + "> " + message, arguments );
	}
	@Deprecated
	public static void enter( final String message, String... arguments ) {
		logger.info( ">> " + header() + "> " + message, arguments );
	}
	//
	//	public static void exit() {
	//		logger.info( "<< " + header() );
	//	}
	//
	//	public static void exit( final String message, String... arguments ) {
	//		logger.info( "<< " + header() + message, arguments );
	//	}
	//
	//	public static void error( final Exception exception ) {
	//		logger.error( ">E " + header() + exception.getMessage() );
	//		logger.debug( ExceptionLogger.defaultExceptionLogAction( exception ) );
	//	}
	//
	//	public static void error( final String message, final Exception exception ) {
	//		logger.error( ">E " + header() + message + "-" + exception.getMessage() );
	//		logger.debug( ExceptionLogger.defaultExceptionLogAction( exception ) );
	//	}
	//
	//	public static String toJSON( final Object target ) {
	//		return new Gson().toJson( target );
	//	}
	//
		private static String header() {
			return wrapper( generateCaller() );
		}

		private static String wrapper( final String data ) {
			return "[" + data + "]";
		}

		private static String generateCaller() {
			StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
			int androidDisplacement = 0;
			if (traceElements[0].getMethodName().equalsIgnoreCase( "getThreadStackTrace" ))
				androidDisplacement = 1;
			final StackTraceElement element = traceElements[4 + androidDisplacement];
			return element.getClassName().substring( element.getClassName().lastIndexOf( '.' ) + 1 ) + "." +
					element.getMethodName();
		}

	//	protected NeoComLogger() {}
}
