package org.dimensinfin.eveonline.neocom.core.updaters;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.core.IEventEmitter;
import org.dimensinfin.eveonline.neocom.core.IEventReceiver;
import org.dimensinfin.eveonline.neocom.core.NeoComEvent;
import org.dimensinfin.eveonline.neocom.services.UpdaterJobManager;

import org.joda.time.DateTime;

public abstract class NeoComUpdater<M> implements IEventEmitter {
	public enum JobStatus {READY, SCHEDULED, RUNNING, EXCEPTION, COMPLETED}

	// - C O M P O N E N T S
	protected static ESIDataAdapter esiDataAdapter;

	public static void injectsEsiDataAdapter( final ESIDataAdapter newesiDataAdapter ) {
		esiDataAdapter = newesiDataAdapter;
	}

	private M model;
	private DateTime startTime;
	private Exception lastException;
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

	// - N E O C O M U P D A T E R
	public abstract boolean needsRefresh();

	public abstract String getIdentifier();

	public abstract void onRun();

	public void onPrepare() {
		Objects.requireNonNull(esiDataAdapter);
		this.status = JobStatus.RUNNING;
		this.startTime = DateTime.now();
	}

	public void onException( final Exception exception ) {
		this.lastException = exception;
	}

	public void onComplete() {
		this.status = JobStatus.COMPLETED;
		this.eventEmitter.sendChangeEvent(EEvents.EVENT_REFRESHDATA.name());
	}

	// - D E L E G A T E D
	@Override
	public void addEventListener( final IEventReceiver listener ) {
		this.eventEmitter.addEventListener(listener);
	}

	@Override
	public void removeEventListener( final IEventReceiver listener ) {
		this.eventEmitter.removeEventListener(listener);
	}

	@Override
	public boolean sendChangeEvent( final String eventName ) {
		return this.eventEmitter.sendChangeEvent(eventName);
	}

	@Override
	public boolean sendChangeEvent( final NeoComEvent event ) {
		return this.eventEmitter.sendChangeEvent(event);
	}
}
