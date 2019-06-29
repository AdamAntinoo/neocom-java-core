package org.dimensinfin.eveonline.neocom.core.updaters;

import org.dimensinfin.eveonline.neocom.domain.UpdaterJob;
import org.dimensinfin.eveonline.neocom.services.UpdaterJobManager;

public abstract class NeoComUpdater<M> {
	private M model;
	//	private static UpdaterJobManager scheduler;
	private UpdaterJob.JobStatus status = UpdaterJob.JobStatus.READY;

	protected M getModel() {
		return this.model;
	}

	public UpdaterJob.JobStatus getStatus() {
		return this.status;
	}

	public void setStatus( final UpdaterJob.JobStatus status ) {
		this.status = status;
	}

	public void refresh() {
		if (this.needsRefresh()) {
			//			final NeoComFetcher fetcher = new NeoComFetcher.Builder().build();
			UpdaterJobManager.submit(this);
		}
	}

	//	public static void injectUpdaterScheduler( final UpdaterJobManager newscheduler ) {
	//		scheduler = newscheduler;
	//	}

	public abstract boolean needsRefresh();

	public abstract String getIdentifier();
	public abstract NeoComUpdater run();


	public enum JobStatus {READY, SCHEDULED, RUNNING, EXCEPTION, COMPLETED}

	//	public static class NeoComFetcher {
	//		public static class Builder {
	//			private NeoComFetcher onConstruction;
	//
	//			public Builder() {
	//				this.onConstruction = new NeoComFetcher();
	//			}
	//
	//			public NeoComFetcher build() {
	//				return this.onConstruction;
	//			}
	//		}
	//	}
}
