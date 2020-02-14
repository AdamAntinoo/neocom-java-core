package org.dimensinfin.eveonline.neocom.service.logger;

import com.google.gson.Gson;

public class ExceptionLogger {
//	private static final Logger logger = LoggerFactory.getLogger( ESIDataProvider.class );
//	private static ExceptionLogger singleton;

	// - S T A T I C   A P I
	public static String defaultExceptionLogAction( final Exception exception ) {
		// Render the exception to the json form.
		final Gson gson = new Gson();
		final StackTraceElement elements[] = exception.getStackTrace();
		return gson.toJson( elements  );
	}

	// - C O N S T R U C T O R S
	private ExceptionLogger() {}

	// - B U I L D E R
//	public static class Builder {
//		private ExceptionLogger onConstruction;
//
//		public Builder() {
//			this.onConstruction = new ExceptionLogger();
//		}
//
//		public ExceptionLogger build() {
//			return this.onConstruction;
//		}
//	}
}
