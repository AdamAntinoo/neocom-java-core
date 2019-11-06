package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessor;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.scheduler.HourlyCronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetProcessorIT {
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private JobScheduler itJobScheduler;
	private AssetRepository itAssetRepository;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;

	@Rule
	public GenericContainer redis = new GenericContainer<>( "postgres:11.2" )
			.withExposedPorts( 5432 )
			.withEnv( "POSTGRES_DB", "postgres" )
			.withEnv( "POSTGRES_USER", "neocom" )
			.withEnv( "POSTGRES_PASSWORD", "01.Alpha" );

	@BeforeEach
	void setUpEnvironment() throws IOException, SQLException {
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		this.itFileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/eoCom.IntegrationTest/" )
				.build();
		this.itJobScheduler = new JobScheduler.Builder()
				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() ).build();
		// Database setup
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
		final String neocomDatabaseURL = databaseHostName + "/" + databasePath +
				"?user=" + databaseUser +
				"&password=" + databasePassword;
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( neocomDatabaseURL )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
	}

	public void registerJobOnScheduler() {
		final Credential credential = Mockito.mock( Credential.class );
		final Job assetProcessorJob = new AssetDownloadProcessor.Builder()
				.withCredential( credential )
				.withAssetRepository( this.itAssetRepository )
				.withNeoAssetConverter( new GetCharactersCharacterIdAsset2NeoAssetConverter() )
				.build();
	}

	@Test
	void downloadAssets() {
	}
}