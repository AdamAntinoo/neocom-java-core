package org.dimensinfin.eveonline.neocom;

import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class AnnotationTest {
	private AnnotationTest() {}

	@LogEnterExit
	public void methodToLog() {
		NeoComLogger.info( "The is the content." );
	}

	// - B U I L D E R
	public static class Builder {
		private AnnotationTest onConstruction;

		public Builder() {
			this.onConstruction = new AnnotationTest();
		}

		public AnnotationTest build() {
			return this.onConstruction;
		}
	}
}
