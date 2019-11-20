package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.provider.AssetProvider;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.container.FacetedExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationEnvironmentDefinition;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.integration.support.SupportIntegrationCredential;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetProviderIT extends IntegrationEnvironmentDefinition {
	@Rule
	public PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:9.6.8" )
			.withDatabaseName( "postgres" )
			.withUsername( "neocom" )
			.withPassword( "01.Alpha" );
	private String connectionUrl;
	private JdbcConnectionSource connectionSource;
	private Dao<NeoAsset, UUID> assetDao;

	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@Test
	public void buildFailureA() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( null )
						.withAssetRepository( assetRepository )
						.withLocationCatalogService( locationService )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void buildFailureB() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( credential )
						.withAssetRepository( null )
						.withLocationCatalogService( locationService )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void buildFailureC() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( credential )
						.withAssetRepository( assetRepository )
						.withLocationCatalogService( null )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
	}

	public void verifyTimeStamp() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000L );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		final List<NeoAsset> assetList = new ArrayList<>();
		assetList.add( asset );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( assetList );
		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
		regionData.setRegionId( 1100000 );
		regionData.setName( "-TEST-REGION-NAME-" );
		final SpaceLocationImplementation spaceLocation = Mockito.mock( SpaceLocationImplementation.class );
		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();

		provider.classifyAssetsByLocation(); // The first time the timestamp is not set.
		provider.classifyAssetsByLocation(); // The second time I run the rest of the code
	}

	private void onCreate() throws SQLException {
		TableUtils.dropTable( this.connectionSource, NeoAsset.class, true );
		TableUtils.createTableIfNotExists( this.connectionSource, NeoAsset.class );
	}

	@Test
	void runAssetProviderIT() throws SQLException, IOException {
		this.setupEnvironment();
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( SupportIntegrationCredential.itCredential )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationService )
				.build();
		Assertions.assertNotNull( provider );

		provider.classifyAssetsByLocation();
	}

	@AfterEach
	void tearDown() {
		this.postgres.stop();
	}

	@Test
	void getRegionList() throws IOException, SQLException {
		this.setupEnvironment();
		// Configure the database to use the docker test container.
		this.postgres.start();
		this.connectionUrl = "jdbc:postgresql://"
				+ postgres.getContainerIpAddress()
				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
				+ "/" + "postgres" +
				"?user=" + "neocom" +
				"&password=" + "01.Alpha";
		NeoComLogger.info( "Postgres SQL URL: {}", connectionUrl );
		this.connectionSource = new JdbcConnectionSource( this.connectionUrl, new PostgresDatabaseType() );
		this.onCreate();
		this.assetDao = DaoManager.createDao( connectionSource, NeoAsset.class );
		this.itNeoComIntegrationDBAdapter = Mockito.mock( IntegrationNeoComDBAdapter.class );
		Mockito.when( this.itNeoComIntegrationDBAdapter.getAssetDao() ).thenReturn( this.assetDao );
		// Configure the authenticated esi access to use the apisimulator mock service.
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
		this.itRetrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();

		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( SupportIntegrationCredential.itCredential )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationService )
				.build();
		Assertions.assertNotNull( provider );
		provider.classifyAssetsByLocation();

		final List<FacetedExpandableContainer> regions = provider.getRegionList();
		Assertions.assertNotNull( regions );
		Assertions.assertEquals( 1, regions.size() );
		Assertions.assertEquals( 2, regions.get( 0 ).getContents().size() );
	}
}
