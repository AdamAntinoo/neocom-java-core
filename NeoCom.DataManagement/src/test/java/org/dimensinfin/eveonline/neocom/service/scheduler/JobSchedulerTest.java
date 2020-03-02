package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;

class JobSchedulerTest {
//	private JobScheduler scheduler4Test;

//	@BeforeEach
//	void setUp() {
//		this.scheduler4Test = new JobScheduler.Builder()
//				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() )
//				.build();
//	}

//	@Test
//	void buildComplete() {
//		final CronScheduleGenerator scheduleGenerator = Mockito.mock( CronScheduleGenerator.class );
//		final JobScheduler scheduler = new JobScheduler.Builder()
//				.withCronScheduleGenerator( scheduleGenerator )
//				.build();
//		Assertions.assertNotNull( scheduler );
//	}

	/**
	 * JobScheduler now is a global singleton so different calls to the same instance really modify the global singleton. This
	 * is why the second test will not fire an exception because it does test an already set field. Once set a schedule
	 * generator it is not possible with the api to clear that field.
	 */
//	@Test
//	void buildFailure() {
//		final CronScheduleGenerator scheduleGenerator = Mockito.mock( CronScheduleGenerator.class );
//		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
//				() -> new JobScheduler.Builder()
//						.withCronScheduleGenerator( null )
//						.build(),
//				"Expected JobScheduler.Builder() to throw null verification, but it didn't." );
//		Assertions.assertNull( thrown.getMessage() );
//		final JobScheduler alreadyExistingScheduler = new JobScheduler.Builder().build();
//		Assertions.assertNotNull( alreadyExistingScheduler );
//	}
	@Test
	void runSchedule() {
		final String identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" ) + "-TEST-";
		final Job job = Mockito.mock( Job.class );
		Mockito.when( job.getSchedule() ).thenReturn( "* - *" );

		JobScheduler.getJobScheduler().clear();
		JobScheduler.getJobScheduler().registerJob( job );
		final JobScheduler schedulerSpy = Mockito.spy( JobScheduler.getJobScheduler() );
		schedulerSpy.runSchedule();
		Mockito.verify( schedulerSpy, Mockito.times( 1 ) ).scheduleJob( job );
	}

	@Test
	void runScheduleWithException() {
		final String identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" ) + "-TEST-";
		final Job job = Mockito.mock( Job.class );
//		Mockito.when( job.getIdentifier() ).thenReturn( identifier );
		Mockito.when( job.getSchedule() ).thenReturn( "* - *" );

		JobScheduler.getJobScheduler().clear();
		JobScheduler.getJobScheduler().registerJob( new Job4TestException.Builder()
				.addCronSchedule( "* - *" ).build() );
		final JobScheduler schedulerSpy = Mockito.spy( JobScheduler.getJobScheduler() );
		schedulerSpy.runSchedule();
		Mockito.verify( schedulerSpy, Mockito.times( 1 ) ).scheduleJob( Mockito.any( Job.class ) );
	}

	@Test
	void getJobScheduler() {
		Assert.assertNotNull( JobScheduler.getJobScheduler() );
	}

	@Test
	void getJobCount() {
		final Job job = Mockito.mock( Job.class );
//		Mockito.when( job.getIdentifier() ).thenReturn( "-TEST-JOB-IDENTIFIER-" );
		JobScheduler.getJobScheduler().clear();
		Assertions.assertEquals( 0, JobScheduler.getJobScheduler().getJobCount() );
		JobScheduler.getJobScheduler().registerJob( job );
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().getJobCount() );
	}

	@Test
	void registerJob() {
		final String identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" ) + "-TEST-";
		final Job job = Mockito.mock( Job.class );
//		Mockito.when( job.getIdentifier() ).thenReturn( identifier );

		JobScheduler.getJobScheduler().clear();
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().registerJob( job ) );
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().registerJob( job ) );
	}

	@Test
	void registerJobReplacingPrevious() {
		final Job jobA = new Job4TestRegistration.Builder()
				.addCronSchedule( "* - *" )
				.withRegistrationTest( "-TEST-JOB-A-" ).build();
		final Job jobB = new Job4TestRegistration.Builder()
				.addCronSchedule( "* - *" )
				.withRegistrationTest( "-TEST-JOB-B-" ).build();
		final Job jobC = new Job4TestRegistration.Builder()
				.addCronSchedule( "* - *" )
				.withRegistrationTest( "-TEST-JOB-A-" ).build();
		JobScheduler.getJobScheduler().clear();

		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().registerJob( jobA ) );
		Assertions.assertEquals( 2, JobScheduler.getJobScheduler().registerJob( jobB ) );
		Assertions.assertEquals( 2, JobScheduler.getJobScheduler().registerJob( jobC ) );
		JobScheduler.getJobScheduler().removeJob( jobB );
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().getJobCount());
	}

	@Test
	void setCronScheduleGenerator() {
		JobScheduler.getJobScheduler().setCronScheduleGenerator( new CronScheduleGenerator() {
			@Override
			public boolean match( final String schedule ) {
				return true;
			}
		} );
	}

	private static class Job4TestRegistration extends Job {
		private String registration;

		@Override
		public Boolean call() throws Exception {
			return true;
		}

		@Override
		public String toString() {
			return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
					.append( "registration", registration )
					.toString();
		}

		@Override
		public boolean equals( final Object o ) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			final Job4TestRegistration that = (Job4TestRegistration) o;
			return new EqualsBuilder()
					.appendSuper( super.equals( o ) )
					.append( this.registration, that.registration )
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder( 17, 37 )
					.appendSuper( super.hashCode() )
					.append( this.registration )
					.toHashCode();
		}

		// - B U I L D E R
		public static class Builder extends Job.Builder<Job4TestRegistration, Job4TestRegistration.Builder> {
			private Job4TestRegistration onConstruction;

			@Override
			protected Job4TestRegistration getActual() {
				if (null == this.onConstruction) this.onConstruction = new Job4TestRegistration();
				return this.onConstruction;
			}

			@Override
			protected Job4TestRegistration.Builder getActualBuilder() {
				return this;
			}

			public Job4TestRegistration.Builder withRegistrationTest( final String registration ) {
				this.onConstruction.registration = registration;
				return this;
			}
		}
	}

	private static class Job4TestException extends Job {
		@Override
		public Boolean call() throws Exception {
			throw new NeoComRuntimeException( "This is the test exception expected." );
		}

		// - B U I L D E R
		public static class Builder extends Job.Builder<Job4TestException, Job4TestException.Builder> {
			private Job4TestException onConstruction;

			@Override
			protected Job4TestException getActual() {
				if (null == this.onConstruction) this.onConstruction = new Job4TestException();
				return this.onConstruction;
			}

			@Override
			protected Job4TestException.Builder getActualBuilder() {
				return this;
			}
		}
	}
}