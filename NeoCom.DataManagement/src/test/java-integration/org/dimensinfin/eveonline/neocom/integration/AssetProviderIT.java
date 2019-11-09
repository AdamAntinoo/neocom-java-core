package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.provider.AssetProvider;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.integration.support.SupportIntegrationCredential;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetProviderIT {
//	private Credential itCredential;
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	private AssetRepository itAssetRepository;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private StoreCacheManager itStoreCacheManager;
	private NeoComRetrofitFactory itNeoComRetrofitFactory;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private LocationCatalogService itLocationService;
	private RetrofitFactory itRetrofitFactory;

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

	private void setupEnvironment() throws SQLException, IOException {
//		final String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0" +
//				".eyJzY3AiOlsicHVibGljRGF0YSIsImVzaS1sb2NhdGlvbi5yZWFkX2xvY2F0aW9uLnYxIiwiZXNpLWxvY2F0aW9uLnJlYWRfc2hpcF90eXBlLnYxIiwiZXNpLW1haWwucmVhZF9tYWlsLnYxIiwiZXNpLXNraWxscy5yZWFkX3NraWxscy52MSIsImVzaS1za2lsbHMucmVhZF9za2lsbHF1ZXVlLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NoYXJhY3Rlcl93YWxsZXQudjEiLCJlc2ktd2FsbGV0LnJlYWRfY29ycG9yYXRpb25fd2FsbGV0LnYxIiwiZXNpLXNlYXJjaC5zZWFyY2hfc3RydWN0dXJlcy52MSIsImVzaS1jbG9uZXMucmVhZF9jbG9uZXMudjEiLCJlc2ktdW5pdmVyc2UucmVhZF9zdHJ1Y3R1cmVzLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2Fzc2V0cy52MSIsImVzaS1wbGFuZXRzLm1hbmFnZV9wbGFuZXRzLnYxIiwiZXNpLWZpdHRpbmdzLnJlYWRfZml0dGluZ3MudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jaGFyYWN0ZXJfam9icy52MSIsImVzaS1tYXJrZXRzLnJlYWRfY2hhcmFjdGVyX29yZGVycy52MSIsImVzaS1jaGFyYWN0ZXJzLnJlYWRfYmx1ZXByaW50cy52MSIsImVzaS1jb250cmFjdHMucmVhZF9jaGFyYWN0ZXJfY29udHJhY3RzLnYxIiwiZXNpLWNsb25lcy5yZWFkX2ltcGxhbnRzLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NvcnBvcmF0aW9uX3dhbGxldHMudjEiLCJlc2ktY2hhcmFjdGVycy5yZWFkX25vdGlmaWNhdGlvbnMudjEiLCJlc2ktY29ycG9yYXRpb25zLnJlYWRfZGl2aXNpb25zLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2NvcnBvcmF0aW9uX2Fzc2V0cy52MSIsImVzaS1jb3Jwb3JhdGlvbnMucmVhZF9ibHVlcHJpbnRzLnYxIiwiZXNpLWNvbnRyYWN0cy5yZWFkX2NvcnBvcmF0aW9uX2NvbnRyYWN0cy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NvcnBvcmF0aW9uX2pvYnMudjEiLCJlc2ktbWFya2V0cy5yZWFkX2NvcnBvcmF0aW9uX29yZGVycy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NoYXJhY3Rlcl9taW5pbmcudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jb3Jwb3JhdGlvbl9taW5pbmcudjEiXSwianRpIjoiYjllYjhhNzYtZTgxNS00YmMwLWJkNzUtNmFjMWYxNjZjNDJjIiwia2lkIjoiSldULVNpZ25hdHVyZS1LZXkiLCJzdWIiOiJDSEFSQUNURVI6RVZFOjIxMTMxOTc0NzAiLCJhenAiOiJlYWNhYTljZDM2NTk0MTg5ODc3NTQ0ZDg1MTc1MzczNCIsIm5hbWUiOiJUaXAgVG9waGFuZSIsIm93bmVyIjoiWCtSZFNGTGtlVyt3YURrclhzVnRGV1F2UlpZPSIsImV4cCI6MTU3MzI1OTA1NywiaXNzIjoibG9naW4uZXZlb25saW5lLmNvbSJ9.dt7veK38pqSLCKzjhzqlS1Mz9vRzcgTBKZmZT6ORTY00mYnSVtGa2WjoAXPlFL7K54BAVmbZjcJPXaeTQSZ8OOXKRi6nUAn5e-2WfoWv9t_00NLReStD34Wn4K7z1xE3wb5y-LSG44KQ4nIy6vtATX9oTcLTvm9tK8APUdydgy7C1YtqKoG4ojKW4A1jo8LPr_EVxWZVadqLo5t1T2zuPuPOX3aBBjyIq_eeo50blbkqOzyOPgOIInYMFx8kLxlCu7boVtVYE3av0bzy9wNFMqm7qUriWh0Yc9tI1btCdGPRfX7huII7hHw-YJLostVHIUOWRbIHmn9xzlzdZ-abYA";
//		this.itCredential = new Credential.Builder( 2113197470 )
//				.withAccountId( 2113197470 )
//				.withAccountName( "Tip Tophane" )
//				.withAccessToken( token )
//				.withRefreshToken( "rXb0u0Wv6kKgaTpWGRcZZA==" )
//				.withDataSource( "Tranquility" )
//				.withScope(
//						"publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1" )
//				.build();
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
		final String neocomDatabaseURL = databaseHostName +
				"/" + databasePath +
				"?user=" + databaseUser +
				"&password=" + databasePassword;
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( neocomDatabaseURL )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.itFileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.itRetrofitUniverseConnector = new RetrofitUniverseConnector.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itStoreCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		this.itEsiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withStoreCacheManager( this.itStoreCacheManager )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itNeoComRetrofitFactory = new NeoComRetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itRetrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.build();
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationRepository( locationRepository )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
	}
}
