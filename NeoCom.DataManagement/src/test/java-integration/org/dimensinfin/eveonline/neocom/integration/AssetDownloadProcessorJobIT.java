package org.dimensinfin.eveonline.neocom.integration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessorJob;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.integration.support.GetCharactersCharacterIdAssets200OkDeserializer;
import org.dimensinfin.eveonline.neocom.integration.support.GroupCount;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationEnvironmentDefinition;
import org.dimensinfin.eveonline.neocom.integration.support.SupportIntegrationCredential;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;

public class AssetDownloadProcessorJobIT extends IntegrationEnvironmentDefinition {
	public static void main( String[] args ) {
		NeoComLogger.enter();
		final AssetDownloadProcessorJobIT application = new AssetDownloadProcessorJobIT();
		try {
			application.setupEnvironment();
			application.registerJobOnScheduler();
			JobScheduler.getJobScheduler().runSchedule();
			application.waitSchedulerCompletion();
			application.checkAssertions();
		} catch (IOException ioe) {
			NeoComLogger.info( "Application interrupted: ", ioe );
		} catch (SQLException sqle) {
			NeoComLogger.info( "Application interrupted: ", sqle );
		}
		NeoComLogger.exit();
	}
	private final ObjectMapper mapper = new ObjectMapper();
	private AssetDownloadProcessorJob assetProcessorJob;
	private List<GroupCount> groupCounts;

	private AssetDownloadProcessorJobIT() {}

	private void checkAssertions() throws IOException, SQLException {
		Assertions.assertNotNull( JobScheduler.getJobScheduler() );
		Assertions.assertNotNull( this.assetProcessorJob );

		final List<NeoAsset> assets = this.itAssetRepository
				.findAllByOwnerId( SupportIntegrationCredential.itCredential.getAccountId() );
		Assertions.assertEquals( 36, assets.size() );

		this.readGroupCounts();
		int propulsionCount = 0;
		int miningLasertCount = 0;
		for (NeoAsset asset : assets) {
			if (asset.getGroupName().equalsIgnoreCase( "Mining Laser" )) miningLasertCount++;
			if (asset.getGroupName().equalsIgnoreCase( "Propulsion Module" )) propulsionCount++;
		}
		for (GroupCount count : this.groupCounts) {
			if (count.getGroup().equalsIgnoreCase( "Mining Laser" ))
				Assertions.assertEquals( miningLasertCount, count.getCount() );
			if (count.getGroup().equalsIgnoreCase( "Propulsion Module" ))
				Assertions.assertEquals( propulsionCount, count.getCount() );
		}
		Assertions.assertTrue(
				this.itCredentialRepository.findCredentialById( SupportIntegrationCredential.itCredential.getUniqueId() )
						.getMiningResourcesEstimatedValue() > 0.0
		);
	}

	private List<GetCharactersCharacterIdAssets200Ok> loadAssetTestData() throws IOException {
		SimpleModule testModule = new SimpleModule( "NoeComIntegrationModule",
				Version.unknownVersion() );
		testModule.addDeserializer( GetCharactersCharacterIdAssets200Ok.class,
				new GetCharactersCharacterIdAssets200OkDeserializer( GetCharactersCharacterIdAssets200Ok.class ) );
		mapper.registerModule( testModule );

		final GetCharactersCharacterIdAssets200Ok[] data = this.mapper.readValue( FileUtils.readFileToString(
				new File( this.itFileSystemAdapter.accessResource4Path( "TestData/assetTestList.json" ) ),
				"utf-8" ), GetCharactersCharacterIdAssets200Ok[].class );
		return new ArrayList<>( Arrays.asList( data ) );
	}

	private void readGroupCounts() throws IOException {
		final File groupCountsFile = new File( this.itFileSystemAdapter.accessResource4Path( "/TestData/groupsCounts.json" ) );
		this.groupCounts = mapper.readValue( groupCountsFile,
				mapper.getTypeFactory().constructCollectionType( List.class, GroupCount.class ) );
	}

	private void registerJobOnScheduler() {
		this.assetProcessorJob = new AssetDownloadProcessorJob.Builder()
				.withCredential( SupportIntegrationCredential.itCredential )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationService )
				.withAssetRepository( this.itAssetRepository )
				.withCredentialRepository( this.itCredentialRepository )
				.addCronSchedule( "* - *" )
				.build();
		JobScheduler.getJobScheduler().registerJob( this.assetProcessorJob );
	}

	private void waitSchedulerCompletion() {
		JobScheduler.getJobScheduler().wait4Completion();
	}

	@Test
	void runAssetProcessorIT() {
		AssetDownloadProcessorJobIT.main( null );
	}
}
