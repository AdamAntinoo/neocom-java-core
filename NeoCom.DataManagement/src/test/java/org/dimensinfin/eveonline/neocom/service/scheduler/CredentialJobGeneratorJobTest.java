package org.dimensinfin.eveonline.neocom.service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

public class CredentialJobGeneratorJobTest {
	private AssetRepository assetRepository;
	private CredentialRepository credentialRepository;

	@BeforeEach
	public void beforeEach() {
		this.assetRepository = Mockito.mock(AssetRepository.class);
		this.credentialRepository = Mockito.mock(CredentialRepository.class);
	}

	@Test
	public void buildComplete() {
		final IConfigurationProvider
		final CredentialJobGeneratorJob credentialJobGeneratorJob=new CredentialJobGeneratorJob.Builder()
				.withConfigurationService(  )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository(  this.credentialRepository)
	}
}
