package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.CronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;

class JobSchedulerTest {
	private JobScheduler scheduler4Test;

	@BeforeEach
	void setUp() {
		this.scheduler4Test = new JobScheduler.Builder()
				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() )
				.build();
	}

	@Test
	void buildComplete() {
		final CronScheduleGenerator scheduleGenerator = Mockito.mock( CronScheduleGenerator.class );
		final JobScheduler scheduler = new JobScheduler.Builder()
				.withCronScheduleGenerator( scheduleGenerator )
				.build();
		Assertions.assertNotNull( scheduler );
	}

	@Test
	void buildFailure() {
		final CronScheduleGenerator scheduleGenerator = Mockito.mock( CronScheduleGenerator.class );
		NullPointerException thrown =
				Assertions.assertThrows( NullPointerException.class,
						() -> new JobScheduler.Builder()
								.withCronScheduleGenerator( null )
								.build(),
						"Expected JobScheduler.Builder() to throw null verification, but it didn't." );
		Assertions.assertNull( thrown.getMessage() );
		thrown =
				Assertions.assertThrows( NullPointerException.class,
						() -> new JobScheduler.Builder()
								.build(),
						"Expected JobScheduler.Builder() to throw null verification, but it didn't." );
		Assertions.assertNull( thrown.getMessage() );
	}

	@Test
	void registerJob() {
		final UUID identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" );
		final Job job = Mockito.mock( Job.class );
		Mockito.when( job.getIdentifier() ).thenReturn( identifier );

		this.scheduler4Test.clear();
		Assertions.assertEquals( 1, this.scheduler4Test.registerJob( job ) );
		Assertions.assertEquals( 1, this.scheduler4Test.registerJob( job ) );
	}

	@Test
	void runSchedule() {
		final UUID identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" );
		final Job job = Mockito.mock( Job.class );
		Mockito.when( job.getIdentifier() ).thenReturn( identifier );
		Mockito.when( job.getSchedule() ).thenReturn( "* - *" );

		this.scheduler4Test.clear();
		this.scheduler4Test.registerJob( job );
		final JobScheduler schedulerSpy = Mockito.spy( this.scheduler4Test );
		schedulerSpy.runSchedule();
		Mockito.verify( schedulerSpy, Mockito.times( 1 ) ).scheduleJob( job );
	}

	@Test
	void runScheduleWithException() {
		final UUID identifier = UUID.fromString( "10596477-3376-4d11-9b68-6213b1cf9bf4" );
		final Job job = Mockito.mock( Job.class );
		Mockito.when( job.getIdentifier() ).thenReturn( identifier );
		Mockito.when( job.getSchedule() ).thenReturn( "* - *" );

		this.scheduler4Test.clear();
		this.scheduler4Test.registerJob( new Job4TestException.Builder()
				.addCronSchedule( "* - *" ).build() );
		final JobScheduler schedulerSpy = Mockito.spy( this.scheduler4Test );
		schedulerSpy.runSchedule();
		Mockito.verify( schedulerSpy, Mockito.times( 1 ) ).scheduleJob( Mockito.any( Job.class ) );
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