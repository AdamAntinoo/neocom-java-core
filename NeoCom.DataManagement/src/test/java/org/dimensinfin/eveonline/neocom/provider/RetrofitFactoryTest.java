package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.integration.support.NeoComUnitTestComponentFactory;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;

import retrofit2.Retrofit;

public class RetrofitFactoryTest {
	private SBConfigurationProvider configurationProvider = (SBConfigurationProvider) NeoComUnitTestComponentFactory.getSingleton()
			.getConfigurationProvider();
	private IFileSystem fileSystemAdapter = NeoComUnitTestComponentFactory.getSingleton().getFileSystemAdapter();

	@Test
	public void accessAuthenticatedConnector() throws IOException {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getUniqueId() ).thenReturn( "tranquility/98876543" );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );

		final Retrofit connector = retrofitFactory.accessAuthenticatedConnector( credential );
		Assertions.assertNotNull( connector );
	}

	@Test
	public void accessUniverseConnector() {
		final RetrofitFactory retrofitFactory = new RetrofitFactory.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.build();

		final Retrofit connector = retrofitFactory.accessUniverseConnector();
		Assertions.assertNotNull( connector );
	}
}
