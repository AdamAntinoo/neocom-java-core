package org.dimensinfin.eveonline.neocom.adapters;

public class NeoComUpdater<M> {
	private M model;

	public void refresh() {
		if (this.needsRefresh()) {
			final NeoComFetcher fetcher = new NeoComFetcher.Builder().build();
		}
	}

	public boolean needsRefresh() {
		modelTS = this.getModel().getTimestampReference();
	}

	protected M getModel() {
		return this.model;
	}

	public static class NeoComFetcher {
		public static class Builder {
			private NeoComFetcher onConstruction;

			public Builder() {
				this.onConstruction = new NeoComFetcher();
			}

			public NeoComFetcher build() {
				return this.onConstruction;
			}
		}
	}
}
