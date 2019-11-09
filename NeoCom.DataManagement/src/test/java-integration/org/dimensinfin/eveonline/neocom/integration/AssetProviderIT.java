package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.provider.AssetProvider;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationEnvironmentDefinition;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.integration.support.SupportIntegrationCredential;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;

public class AssetProviderIT extends IntegrationEnvironmentDefinition {
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
	@org.junit.Test
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

	@org.junit.Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( null )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@org.junit.Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( null )
				.withLocationCatalogService( locationService )
				.build();
		Assert.assertNotNull( provider );
	}

	@org.junit.Test(expected = NullPointerException.class)
	public void buildFailureC() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( null )
				.build();
		Assert.assertNotNull( provider );
	}
}
