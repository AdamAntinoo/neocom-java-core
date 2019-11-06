package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class Job implements Callable<Boolean> {
	private UUID identifier;
	private String schedule;
	private JobStatus status = JobStatus.READY;

	protected Job() {}

	public UUID getIdentifier() {
		return this.identifier;
	}

	public String getSchedule() {
		return this.schedule;
	}

	public JobStatus getStatus() {
		return this.status;
	}

	public Job setStatus( final JobStatus status ) {
		this.status = status;
		return this;
	}

	protected Job setIdentifier( final UUID identifier ) {
		this.identifier = identifier;
		return this;
	}

	protected Job setSchedule( final String schedule ) {
		this.schedule = schedule;
		return this;
	}
//	//	@Override
//	public abstract Boolean call( final Credential credential ) throws NeoComRuntimeException;
//
//	@Override
//	public Boolean call() throws Exception {
//		return null;
//	}

	// - B U I L D E R
	public abstract static class Builder<T extends Job, B extends Job.Builder> {
		protected B actualClassBuilder;

		public Builder() {
			this.actualClassBuilder = getActualBuilder();
		}

		protected abstract T getActual();

		protected abstract B getActualBuilder();

		public B addCronSchedule( final String cronPattern ) {
			Objects.requireNonNull( cronPattern );
			this.getActual().setSchedule( cronPattern );
			return this.actualClassBuilder;
		}

		public T build() {
			this.getActual().setIdentifier( UUID.randomUUID());
			Objects.requireNonNull( this.getActual().getSchedule() );
			Objects.requireNonNull( this.getActual().getIdentifier() );
			return this.getActual();
		}
	}
}