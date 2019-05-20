package org.dimensinfin.eveonline.neocom.exception;

public class NeoComError {
	private Exception ex;
	private EErrorType type = EErrorType.EXCEPTION;
	private String code;
	private String origin;

	public NeoComError( final Exception ex ) {
		this.ex = ex;
	}

	// -  B U I L D E R
	public static class Builder {
		private NeoComError onConstruction;

		public Builder( final Exception rtex ) {
			this.onConstruction = new NeoComError(rtex);
		}

		public Builder withType( final EErrorType type ) {
			this.onConstruction.type = type;
			return this;
		}

		public Builder withCode( final String code ) {
			this.onConstruction.code = code;
			return this;
		}

		public Builder withOrigin( final String origin ) {
			this.onConstruction.origin = origin;
			return this;
		}

		public NeoComError build() {
			return this.onConstruction;
		}
	}
}
