package org.dimensinfin.eveonline.neocom;

import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class AnnotationTest {
	// - C O N S T R U C T O R S
	private AnnotationTest() {}

	//	@LogEnterExit
	public void methodToLog() {
		NeoComLogger.info( "The is the content." );
	}

	// - B U I L D E R
	public static class Builder {
		private AnnotationTest onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new AnnotationTest();
		}

		public AnnotationTest build() {
			return this.onConstruction;
		}
	}
}
