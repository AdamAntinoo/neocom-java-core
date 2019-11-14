package org.dimensinfin.eveonline.neocom.service.scheduler.domain;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Job implements Callable<Boolean> {
//	private String identifier;
	private String schedule;
	private JobStatus status = JobStatus.READY;

	protected Job() {}

//	public String getIdentifier() {
//		return this.identifier;
//	}

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

//	protected Job setIdentifier( final String identifier ) {
//		this.identifier = identifier;
//		return this;
//	}

	protected Job setSchedule( final String schedule ) {
		this.schedule = schedule;
		return this;
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Job job = (Job) o;
		return new EqualsBuilder()
//				.append( this.identifier, job.identifier )
				.append( this.schedule, job.schedule )
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
//				.append( this.identifier )
				.append( this.schedule )
				.toHashCode();
	}

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
//			this.getActual().setIdentifier( this.getClass().getSimpleName() +
//					"." + UUID.randomUUID() );
			Objects.requireNonNull( this.getActual().getSchedule() );
//			Objects.requireNonNull( this.getActual().getIdentifier() );
			return this.getActual();
		}
	}
}
