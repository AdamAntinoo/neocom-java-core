package org.dimensinfin.eveonline.neocom.core.updaters;

import java.beans.PropertyChangeListener;

import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.core.IEventEmitter;
import org.dimensinfin.eveonline.neocom.core.IEventReceiver;
import org.dimensinfin.eveonline.neocom.services.UpdaterJobManager;

public abstract class NeoComUpdater<M> implements IEventEmitter {
	private M model;
	private JobStatus status = JobStatus.READY;
	private EventEmitter eventEmitter = new EventEmitter();

	public NeoComUpdater( final M model ) {
		this.model = model;
	}

	protected M getModel() {
		return this.model;
	}

	public JobStatus getStatus() {
		return this.status;
	}

	public void setStatus( final JobStatus status ) {
		this.status = status;
	}

	public void refresh() {
		if (this.needsRefresh()) {
			UpdaterJobManager.submit(this);
		}
	}
	// - D E L E G A T E D
	public void addEventListener( final IEventReceiver listener ) {
		this.eventEmitter.addEventListener(listener);
	}

	@Override
	public void removeEventListener( final IEventReceiver listener ) {
		this.eventEmitter.removeEventListener(listener);
	}

	@Override
	public void addPropertyChangeListener( final PropertyChangeListener listener ) {
		this.eventEmitter.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener( final PropertyChangeListener listener ) {
		this.eventEmitter.removePropertyChangeListener(listener);
	}

	@Override
	public boolean sendChangeEvent( final String eventName ) {
		return this.eventEmitter.sendChangeEvent(eventName);
	}
//
//	public boolean sendChangeEvent( final PropertyChangeEvent event ) {
//		return eventEmitter.sendChangeEvent(event);
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
