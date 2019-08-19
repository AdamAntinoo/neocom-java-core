package org.dimensinfin.eveonline.neocom.core.updaters;

import org.dimensinfin.core.domain.EEvents;
import org.dimensinfin.core.domain.EventEmitter;
import org.dimensinfin.core.domain.IntercommunicationEvent;
import org.dimensinfin.core.interfaces.IEventEmitter;
import org.dimensinfin.core.interfaces.IEventReceiver;
import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.services.UpdaterJobManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class NeoComUpdater<M> implements IEventEmitter {
	public enum JobStatus {READY, SCHEDULED, RUNNING, EXCEPTION, COMPLETED}
	protected static Logger logger = LoggerFactory.getLogger(NeoComUpdater.class);
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

	public M getModel() {
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
		logger.info("EX [NeoComUpdater.onException]> Message: {}", exception.getMessage());
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
	public boolean sendChangeEvent( final IntercommunicationEvent event ) {
		return this.eventEmitter.sendChangeEvent(event.getPropertyName());
	}

	@Override
	public boolean sendChangeEvent( final String eventName, final Object origin ) {
		return this.eventEmitter.sendChangeEvent(eventName, origin);
	}

	@Override
	public boolean sendChangeEvent( final String eventName, final Object origin, final Object oldValue, final Object newValue ) {
		return this.eventEmitter.sendChangeEvent(eventName, origin, oldValue, newValue);
	}
}
