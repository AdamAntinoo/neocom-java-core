package org.dimensinfin.eveonline.neocom.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessor;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class AssetDownloadProcessorIT {
	@Test
	void buildComplete() {
		final Credential credential = Mockito.mock(Credential.class);
		final ESIDataProvider esiDataProvider = Mockito.mock(ESIDataProvider.class);
		final AssetRepository assetRepository = Mockito.mock(AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock(LocationCatalogService.class );
		final GetCharactersCharacterIdAsset2NeoAssetConverter assetConverter =
				Mockito.mock(GetCharactersCharacterIdAsset2NeoAssetConverter.class );
		final AssetDownloadProcessor processor = new AssetDownloadProcessor.Builder()
				.withCredential( credential )
				.withEsiDataProvider( esiDataProvider )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.withNeoAssetConverter( assetConverter )
				.addCronSchedule( "* - *" )
				.build();
		Assertions.assertNotNull( processor );
	}

	@Test
	void assetDownloadProcessorITRun() {
		final AssetDownloadProcessor processor = new AssetDownloadProcessor.Builder()
				.withCredential( credential )
				.withEsiDataProvider( esiDataProvider )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.withNeoAssetConverter( assetConverter )
				.addCronSchedule( "* - *" )
				.build();
		Assertions.assertNotNull( processor );

	}
}